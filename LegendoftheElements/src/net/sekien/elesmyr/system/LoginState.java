package net.sekien.elesmyr.system;

import net.sekien.elesmyr.util.FileHandler;
import org.newdawn.slick.*;
import org.newdawn.slick.Input;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class LoginState extends BasicGameState {

int stateID = -1;

private Image overlay;
private float time = 0;

private TextField tfUser;
private TextField tfPass;

LoginState(int stateID) {
	this.stateID = stateID;
}

@Override
public int getID() {
	return stateID;
}

public void init(GameContainer gc, StateBasedGame sbg)
		throws SlickException {
	overlay = FileHandler.getImage("menu.error");
	tfUser = new TextField(gc, FontRenderer.getFont(), (Main.INTERNAL_RESX/4)-64, 90, 256, 16);
	tfUser.setBorderColor(null);
	tfUser.setBackgroundColor(null);
	tfUser.setTextColor(Color.white);
	tfUser.setAcceptingInput(true);
	tfUser.setFocus(true);
	tfUser.setText(Globals.get("name", "Player"));
	tfUser.setMaxLength(15);
	tfPass = new TextField(gc, FontRenderer.getFont(), (Main.INTERNAL_RESX/4)-64, 120, 256, 16);
	tfPass.setBorderColor(null);
	tfPass.setBackgroundColor(null);
	tfPass.setTextColor(Color.white);
	tfPass.setAcceptingInput(false);
	tfPass.setFocus(false);
	tfPass.setMaxLength(64);
	time = 0;
}

public void reset() {
	tfUser.setText(Globals.get("name", "Player"));
	tfPass.setText("");
	time = 0;
}

public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
		throws SlickException {
	float vw = gc.getWidth();
	float vh = gc.getHeight();
	float ox, oy, w, h;
	w = vh*(16/9f);
	h = vh;
	ox = (vw-w)/2;
	oy = 0;
	if (time < 20) {
		overlay.setAlpha(0.1f);
		overlay.draw(ox, oy, w, h);
		time++;
	} else {
		overlay.setAlpha(1f);
		overlay.draw(ox, oy, w, h); //TODO: replace with no-bg version;
		g.scale(vw/(Main.INTERNAL_RESX/2), vh/(Main.INTERNAL_RESY/2));
		//tfUser.render(gc,g);
		FontRenderer.drawString((Main.INTERNAL_RESX/4)-64, 80, "#login", g);
		FontRenderer.drawString((Main.INTERNAL_RESX/4)-64, 110,
				                       tfUser.getText().concat(tfUser.isAcceptingInput()?"_":""), g);
		FontRenderer.drawString((Main.INTERNAL_RESX/4)-64, 130,
				                       tfPass.getText().replaceAll(".", "*").concat(tfPass.isAcceptingInput()?"_":""), g);
	}
}

public void update(GameContainer gc, StateBasedGame sbg, int delta)
		throws SlickException {
	tfUser.setCursorPos(tfUser.getText().length());
	tfPass.setCursorPos(tfPass.getText().length());
	if (gc.getInput().isKeyPressed(Input.KEY_UP)) {
		tfUser.setAcceptingInput(true);
		tfUser.setFocus(true);
		tfPass.setAcceptingInput(false);
		tfPass.setFocus(false);
	}
	if (gc.getInput().isKeyPressed(Input.KEY_DOWN)) {
		tfUser.setAcceptingInput(false);
		tfUser.setFocus(false);
		tfPass.setAcceptingInput(true);
		tfPass.setFocus(true);
	}
	if (gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
		if (tfUser.getText().matches("[0-9a-zA-Z_]+")) {
			tfUser.setAcceptingInput(false);
			tfUser.setFocus(false);
			tfPass.setAcceptingInput(false);
			tfPass.setFocus(false);
			((GameClient) sbg.getState(Main.GAMEPLAYSTATE)).login(tfUser.getText(), tfPass.getText().hashCode());
			gc.getInput().clearKeyPressedRecord();
			sbg.enterState(Main.GAMEPLAYSTATE);
		} else {
			gc.getInput().clearKeyPressedRecord();
			((ErrorState) sbg.getState(Main.ERRORSTATE)).errorText = "Invalid username "+tfUser.getText()+
					                                                         "\nUsernames may only contain Lowercase letters,\n"+
					                                                         "uppercase letters, numbers, and an underscore.";
			sbg.enterState(Main.ERRORSTATE);
		}
	}
	if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
		gc.getInput().clearKeyPressedRecord();
		sbg.enterState(Main.MENUSTATE);
	}
}

}
