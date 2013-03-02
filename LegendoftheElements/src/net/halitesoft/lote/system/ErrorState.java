package net.halitesoft.lote.system;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class ErrorState extends BasicGameState {

	int stateID = -1;

	Image overlay;
	int time = 0;

	public String errorText = "Error: no error";

	ErrorState( int stateID )
	{
		this.stateID = stateID;
	}

	@Override
	public int getID() {
		return stateID;
	}
	
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		overlay = new Image("data/menu/error.png",false,0);
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		float vw = gc.getWidth();
		float vh = gc.getHeight();
		float ox,oy,w,h;
		w = vh*(16/9f);
		h = vh;
		ox = (vw-w)/2;
		oy = 0;
		if (time<30) {
			overlay.setAlpha(0.1f);
			overlay.draw(ox,oy,w, h);
			time++;
			
			g.scale(vw/Main.INTERNAL_RESX,vh/Main.INTERNAL_RESY);
			int i=0;
			for (String s : errorText .split("\n")) {
				FontRenderer.drawString(Main.INTERNAL_RESX/2-(FontRenderer.getWidth(s)/2),160+i*18,s, g);
				i++;
			}
			FontRenderer.drawString(Main.INTERNAL_RESX / 2 - (FontRenderer.getWidth("Press <ENTER> to close.") / 2), 300, "Press <ENTER> to close.", g);
		}
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		if (gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
			time=0;
			errorText = "Error: no error";
			gc.getInput().clearKeyPressedRecord();
			sbg.enterState(Main.MENUSTATE);
		}
	}

}
