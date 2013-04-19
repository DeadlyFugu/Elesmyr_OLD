package net.sekien.elesmyr.ui;

import com.esotericsoftware.minlog.Log;
import net.sekien.elesmyr.msgsys.MessageReceiver;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.FontRenderer;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.Main;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.elesmyr.world.entity.Entity;
import net.sekien.hbt.HBTComment;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTTag;
import org.newdawn.slick.*;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: matt Date: 17/04/13 Time: 4:51 PM To change this template use File | Settings |
 * File Templates.
 */
public class DevMode implements UserInterface {

private String target = "NULL";
private HBTCompound list;
private int panelWidth = 260;
private boolean enabled = false;
private HBTTag activeElement;
private TextField textField;
private boolean showTextField;

private HBTCompound listNew; //NEW list
private HBTCompound listDM; //DevMode list

private final HBTTag targetAETag = new HBTComment("_DTARGET");

private boolean inited = false;

@Override
public boolean inited() {return inited; }

@Override
public void ctor(String extd) {
}

@Override
public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver) throws SlickException {
	inited = true;

	textField = new TextField(gc, FontRenderer.getFont(), 0, 16, 530, 16);
	textField.setBorderColor(null);
	textField.setBackgroundColor(new Color(0, 0, 0, 0.2f));
	textField.setTextColor(Color.white);
	textField.setAcceptingInput(false);
	textField.setMaxLength(57);

	listNew = new HBTCompound("NEW");
	listDM = new HBTCompound("DEVMODE");
}

@Override
public void render(GameContainer gc, StateBasedGame sbg, Graphics g, Camera cam, GameClient receiver) throws SlickException {
	if (enabled) {
		g.setColor(new Color(0, 0, 0, 0.75f));
		g.fillRect(Main.INTERNAL_RESX-panelWidth, 16, panelWidth, Main.INTERNAL_RESY-80);
		g.setColor(Color.white);

		if (showTextField) {
			//textField.render(gc, g);
			textField.setFocus(true);
		}

		if (activeElement==targetAETag) {
			renderTextField((Main.INTERNAL_RESX-panelWidth)+6, 16, g);
		} else {
			FontRenderer.drawString((Main.INTERNAL_RESX-panelWidth)+6, 16, target, g);
		}

		if (list!=null) {
			renderList((Main.INTERNAL_RESX-panelWidth)+6+9, 32, list, g);
		}
	}
}

private void renderList(int x, int y, HBTCompound list, Graphics g) {
	int ry = y;
	for (HBTTag tag : list) {
		if (tag==activeElement) {
			renderTextField(x, ry, g);
		} else if (tag instanceof HBTCompound) {
			FontRenderer.drawString(x, ry, tag.getName()+" [+]", g);
		} else if (tag instanceof HBTComment) {
			FontRenderer.drawString(x, ry, "["+tag.getName()+"]", g);
		} else {
			FontRenderer.drawString(x, ry, tag.getName()+" ="+tag.toString().split("=", 2)[1], g);
		}
		ry += 16;
	}
}

private boolean mouseDragging = false;

@Override
public void update(GameContainer gc, StateBasedGame sbg, GameClient receiver) {
	Input input = gc.getInput();
	if (input.isKeyPressed(Input.KEY_TAB)) {
		enabled = !enabled;
		panelWidth = 260;
	}

	if (enabled) {
		int mx = (int) (((float) input.getMouseX()/gc.getWidth())*Main.INTERNAL_RESX);
		int my = (int) (((float) input.getMouseY()/gc.getHeight())*Main.INTERNAL_RESY);

		if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			if (mx > (Main.INTERNAL_RESX-panelWidth)-16 && mx < (Main.INTERNAL_RESX-panelWidth)+16) {
				mouseDragging = true;
			}
		} else {
			mouseDragging = false;
		}

		if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			System.out.print("ML PRESS");
			if (activeElement!=null)
				writeActiveElement(receiver);
			if (mx > (Main.INTERNAL_RESX-panelWidth)+16 && my > 16 && my < Main.INTERNAL_RESY-64) {
				my = my-16;
				if (my < 16) { //In the 'target' area.
					activeElement = targetAETag;
					setTextFieldActive(true);
					setText(target);
				} else {
					HBTTag element = getElementAt((my-16)/16, list);
					if (element==null) {
						activeElement = null;
						setTextFieldActive(false);
					} else {
						activeElement = element;
						setTextFieldActive(true);
						setText(element.toString());
					}
				}
			} else {
				activeElement = null;
				setTextFieldActive(false);
			}
		}

		if (mouseDragging) {
			panelWidth = Math.min(Math.max(-(mx-Main.INTERNAL_RESX), 20), Main.INTERNAL_RESX-20);
		}
	}
}

private HBTTag getElementAt(int i, HBTCompound search) {
	if (search==null) //In case target=="NULL"
		return null;
	int si = 0;
	for (HBTTag tag : search) {
		if (si==i) {
			return tag;
		} else if (tag instanceof HBTCompound) {
			HBTTag found = getElementAt(i-si, (HBTCompound) tag);
			if (found!=null) {
				return tag;
			}
		}
		si++;
	}
	return null;
}

private void writeActiveElement(GameClient client) {
	String str = textField.getText();
	if (activeElement==targetAETag) { //Target
		String olTarget = target;
		target = str;
		if (!updateList(client)) {
			target = olTarget;
		}
	} else {
		HBTCompound test = getActiveParent(list);
		if (test==null) {
			Log.error("DevMode activeElement unrecognised: "+activeElement);
		} else {
			if (activeElement instanceof HBTComment) {
				//TODO: Button handling code
			} else {
				int index = test.getData().indexOf(activeElement);
				HBTTag old = activeElement;
				test.getData().remove(activeElement);
				try {
					for (HBTTag tag : FileHandler.parseTextHBT(textField.getText()))
						test.getData().add(index, tag);
				} catch (IOException e) {
					test.getData().add(index, old); //Reset incase adding new tag fails.
					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
				targetUpdate(client);
			}
		}
	}
}

private void targetUpdate(GameClient client) {
	if (target.equals("NULL")) {
	} else if (target.equals("NEW")) {
		listNew = list; //Unneeded?
	} else if (target.startsWith("ENT")) {
		String sub = target.split("\\.", 2)[1];
		Entity ent = client.getPlayer().getRegion().entities.get(Integer.parseInt(sub));
		ent.fromHBT(list);
	}
}

private HBTCompound getActiveParent(HBTCompound search) {
	for (HBTTag tag : search) {
		if (tag==activeElement) {
			return search;
		} else if (tag instanceof HBTCompound) {
			if (getActiveParent((HBTCompound) tag)!=null) {
				return (HBTCompound) tag;
			}
		}
	}
	return null;
}

private boolean updateList(GameClient client) {
	if (target.equals("NULL")) {
		list = null;
	} else if (target.equals("NEW")) {
		list = listNew;
	} else if (target.startsWith("ENT.")) {
		String sub = target.split("\\.", 2)[1];
		Entity ent = client.getPlayer().getRegion().entities.get(Integer.parseInt(sub));
		list = ent.toHBT(false);
	} else {
		return false;
	}
	return true;
}

@Override
public boolean blockUpdates() {
	return false;
}

public void setTextFieldActive(boolean active) {
	showTextField = active;
	if (!active)
		setText("");
	textField.setAcceptingInput(active);
	textField.setFocus(active);
}

private void setText(String text) {
	textField.setText(text);
	textField.setCursorPos(text.length());
}

public void renderTextField(int x, int y, Graphics g) {
	FontRenderer.drawString(x, y, textField.getText(), g);
	FontRenderer.drawString(x-4+textField.getText().length()*9, y, "_", g);
}
}
