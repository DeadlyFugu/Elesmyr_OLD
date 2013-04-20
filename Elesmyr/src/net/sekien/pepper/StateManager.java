package net.sekien.pepper;

import com.jhlabs.image.AbstractBufferedImageOp;
import com.jhlabs.image.GaussianFilter;
import net.sekien.elesmyr.system.ErrorState;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.Globals;
import net.sekien.elesmyr.system.Main;
import net.sekien.elesmyr.util.FileHandler;
import org.newdawn.slick.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.BufferedImageUtil;
import org.newdawn.slick.util.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 10:40 AM To change this template use File | Settings |
 * File Templates.
 */
public class StateManager {
private static HashMap<String, Node> states;
private static Renderer renderer;
private static Image background;
private static Stack<Node> stateTrace;
private static List<PopupNode> popup;
private static boolean enterGame = false;
private static String gameSettings;

private static int animtimer = 0;
private static String newState = null;

public static void init() {
	states = new HashMap<String, Node>();
	stateTrace = new Stack<Node>();
	popup = new LinkedList<PopupNode>();
	renderer = new Renderer();
	setBackground(Globals.get("lastSave", ""));
}

public static void render(GameContainer gc, Graphics g) {
	renderer.setGraphics(g);
	background.draw(0, 0, gc.getWidth(), gc.getHeight());
	g.scale(gc.getWidth()/(float) (Main.INTERNAL_RESX), gc.getHeight()/(float) (Main.INTERNAL_RESY));
	if (!stateTrace.empty()) {
		if (newState!=null) {
			System.out.println(animtimer);
			animtimer++;
			if (animtimer < 20) {
				stateTrace.peek().transitionLeave(renderer, Main.INTERNAL_RESX, Main.INTERNAL_RESY, true, animtimer/20f);
			} else if (animtimer==20) {
				stateTrace.peek().transitionLeave(renderer, Main.INTERNAL_RESX, Main.INTERNAL_RESY, true, animtimer/20f);
				if (newState.equals("_POP")) {
					stateTrace.pop();
				} else {
					stateTrace.push(states.get(newState));
					stateTrace.peek().update(gc); //This is to update DynamicListNodes, otherwise they won't animate properly.
				}
			} else if (animtimer < 40) {
				stateTrace.peek().transitionEnter(renderer, Main.INTERNAL_RESX, Main.INTERNAL_RESY, true, (animtimer-20)/20f);
			} else {
				stateTrace.peek().transitionEnter(renderer, Main.INTERNAL_RESX, Main.INTERNAL_RESY, true, (animtimer-20)/20f);
				newState = null;
				animtimer = 0;
			}
		} else {
			stateTrace.peek().render(renderer, Main.INTERNAL_RESX, Main.INTERNAL_RESY, true);
		}
	}
	for (PopupNode node : popup) {
		node.render(renderer, Main.INTERNAL_RESX, Main.INTERNAL_RESY, false);
	}
}

public static void update(GameContainer gc, StateBasedGame sbg) {
	if (enterGame) {
		enterGame = false;
		String mode = gameSettings;
		String arg = "";
		if (gameSettings.contains(" ")) {
			mode = gameSettings.split(" ", 2)[0];
			arg = gameSettings.split(" ", 2)[1];
		}
		if (mode.equals("SAVE")) {
			Globals.set("save", arg);
			try {
				((GameClient) sbg.getState(Main.GAMEPLAYSTATE)).loadSave(gc, arg, false, sbg);
				((GameClient) sbg.getState(Main.GAMEPLAYSTATE)).init(gc, sbg);
				gc.getInput().clearKeyPressedRecord();
				sbg.enterState(Main.GAMEPLAYSTATE);
			} catch (Exception e) {
				if (e.getLocalizedMessage()!=null && e.getLocalizedMessage().equals("__BIND_EXCEPTION")) {
					com.esotericsoftware.minlog.Log.error(e.getLocalizedMessage());
					gc.getInput().clearKeyPressedRecord();
					((ErrorState) sbg.getState(Main.ERRORSTATE)).errorText =
							"#error.bindport";
					sbg.enterState(Main.ERRORSTATE);
					return;
				} else {
					Main.handleCrash(e);
					gc.exit();
				}
			}
		} else if (mode.equals("MAINMENU")) {
			sbg.enterState(Main.MENUSTATE);
		}
	}
	if (!stateTrace.empty()) {
		if (newState==null) {
			Input input = gc.getInput();
			Action action = getAction(input);
			boolean popupHasFocus = false;
			List<PopupNode> removePopups = new ArrayList<PopupNode>(5);
			for (PopupNode node : popup) {
				if (node.receiveActions() && !popupHasFocus) {
					if (action!=null)
						node.onAction(action);
					node.update(gc);
					popupHasFocus = true;
				}
				if (node.isClosed())
					removePopups.add(node);
			}
			for (PopupNode node : removePopups) {
				popup.remove(node);
			}
			if (!popupHasFocus) {
				if (action==Action.BACK)
					back();
				else if (action!=null)
					stateTrace.peek().onAction(action);
				if (!stateTrace.empty())
					stateTrace.peek().update(gc);
			}
		}
	} else {
		gc.exit();
	}
}

private static Action getAction(Input input) {
	if (input.isKeyPressed(Input.KEY_ENTER)) {
		return Action.SELECT;
	} else if (input.isKeyPressed(Input.KEY_BACK)) {
		return Action.BACK;
	} else if (input.isKeyPressed(Input.KEY_UP)) {
		return Action.UP;
	} else if (input.isKeyPressed(Input.KEY_DOWN)) {
		return Action.DOWN;
	} else if (input.isKeyPressed(Input.KEY_LEFT)) {
		return Action.LEFT;
	} else if (input.isKeyPressed(Input.KEY_RIGHT)) {
		return Action.RIGHT;
	}
	return null;
}

public static void setState(String name) {
	if (states.containsKey(name)) {
		newState = name;
	} else {
		error("State not found: "+name, false);
		//throw new StateNotFoundException(name);
	}
}

public static void setStateInitial(String name) {
	stateTrace.push(states.get(name));
	newState = name;
	animtimer = 21; //Skip closing of previous state;
}

public static void registerState(Node node) {
	if (!states.containsKey(node.getName())) {
		states.put(node.getName(), node);
	} else {
		throw new StateAlreadyExistsException(node.getName());
	}
}

public static void setBackground(String name) {
	try {
		File file = new File("save/thumb/"+name+".png");
		if (file.exists()) {
			try {
				BufferedImage src = ImageIO.read(file);
				AbstractBufferedImageOp filter = new GaussianFilter(8);
				BufferedImage filtered = filter.filter(src, null);
				Texture texture = BufferedImageUtil.getTexture("", filtered);
				background = new Image(texture.getImageWidth(), texture.getImageHeight());
				background.setTexture(texture);
			} catch (IOException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		} else {
			background = FileHandler.getImage("menu.bg");
		}
	} catch (SlickException e) {
		Log.error(e);
	}
}

public static void back() {
	if (stateTrace.size() > 1)
		newState = "_POP";
	else
		popup.add(new DialogPopup("_quitys", "Close the game and return to the desktop?", 1) {
			@Override
			protected void onSelect(int sel) {
				if (sel==0)
					newState = "_POP";
			}
		});
}

public static void error(String string, boolean goBack) {
	popup.add(new ErrorPopup("Error", string, goBack));
}

public static void updFunc(String func) {
	enterGame = true;
	gameSettings = func;
}

private static class StateNotFoundException extends RuntimeException {
	public StateNotFoundException(String name) {super(name);}
}

private static class StateAlreadyExistsException extends RuntimeException {
	public StateAlreadyExistsException(String name) {super(name);}
}
}
