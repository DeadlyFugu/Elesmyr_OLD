package net.shard.lote.system;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class IntroState extends BasicGameState {

	int stateID = -1;
	
	float timer = 0;

	Image intro1;
	Image intro2;
	Image intro3;
	Image intro4;
	Image menubg;
	Image bg2;
	Image bg3;

	IntroState( int stateID )
	{
		this.stateID = stateID;
	}

	@Override
	public int getID() {
		return stateID;
	}
	
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		intro1 = new Image("data/menu/intro1.png"); //Godai no Densetsu (LotE)
		intro2 = new Image("data/menu/intro2.png"); //Shard CSE
		intro3 = new Image("data/menu/intro3.png"); //AML
		intro4 = new Image("data/menu/intro4.png"); //Powered by Slick
		menubg = new Image("data/menu/bg.png"); //Menu image (BG)
		bg2 = new Image("data/menu/bg2.png",false,0); //Menu image (Shard CSE)
		bg3 = new Image("data/menu/bg3.png",false,0); //Menu image (LotE/AML)
		gc.setVSync(true); //True in release
		gc.setVerbose(false); //False in release
		gc.setClearEachFrame(false); //Set to false in release!
		gc.setShowFPS(false); //Set to false in release
		gc.setTargetFrameRate(60);
		gc.getInput().initControllers();
		
		if (Main.globals.containsKey("resdm")) {
			int dm = Integer.parseInt(Main.globals.get("resdm"));
			((AppGameContainer) gc).setDisplayMode(MainMenuState.disx[dm], MainMenuState.disy[dm], dm==3||dm==4);
			((AppGameContainer) gc).setMouseGrabbed(dm==3||dm==4);
			Main.INTERNAL_ASPECT=((float) MainMenuState.disx[dm]/(float) MainMenuState.disy[dm]);
			Main.INTERNAL_RESX = (int) (Main.INTERNAL_RESY*Main.INTERNAL_ASPECT); //Internal resolution x
		}
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		float vw = gc.getWidth();
		float vh = gc.getHeight();
		float ox,oy,w,h;
//		if (vw/vh < 1.6) {
//			w = vw;
//			h = vw*0.625f;
//			ox = 0;
//			oy = (vh-h)/2;
//		} else {
			w = vh*1.6f;
			h = vh;
			ox = (vw-w)/2;
			oy = 0;
		//}
		if (timer < 500) {
			intro1.setAlpha((timer)/500f);
			intro1.draw(ox,oy,w, h);
		} else if (timer < 1000) {
			intro2.setAlpha((timer-500)/500f);
			intro2.draw(ox,oy,w, h);
		} else if (timer < 1500) {
			intro3.setAlpha((timer-1000)/500f);
			intro3.draw(ox,oy,w, h);
		} else if (timer < 2000) {
			intro4.setAlpha((timer-1500)/500f);
			intro4.draw(ox,oy,w, h);
		} else if (timer < 2250) {
			w = vh*(16/9f);
			h = vh;
			ox = (vw-w)/2;
			oy = 0;
			menubg.setAlpha((timer-2000)/500f);
			menubg.draw(ox,oy,w, h);
		} else if (timer < 2500) {
			w = vh*(16/9f);
			h = vh;
			ox = (vw-w)/2;
			oy = 0;
			menubg.setAlpha((timer-2000)/500f);
			menubg.draw(ox,oy,w, h);
			bg2.setAlpha((timer-2250)/200f);
			bg3.setAlpha((timer-2250)/200f);
			bg2.draw(0,0,vh*(4/3f),h);
			bg3.draw(vw-vh*(4/3f),0,vh*(4/3f),h);
		} else {
			gc.getInput().clearKeyPressedRecord();
			sbg.enterState(Main.MENUSTATE);
		}
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		timer += delta/4f;
	}

}
