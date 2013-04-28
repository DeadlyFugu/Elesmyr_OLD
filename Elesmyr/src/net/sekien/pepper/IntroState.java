package net.sekien.pepper;

import net.sekien.elesmyr.system.Globals;
import net.sekien.elesmyr.util.FileHandler;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.awt.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 27/04/13 Time: 6:07 PM To change this template use File | Settings |
 * File Templates.
 */
public class IntroState extends Node {
private Image sekien;
private Image game;
int time = 0;

public IntroState(String name) {
	super(name);
	try {
		sekien = FileHandler.getImage("menu.intro1"); //Sekien
		game = FileHandler.getImage("menu.intro3"); //Elesmyr
	} catch (SlickException e) {
		e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	}
}

private static final float stepTime = 100;

@Override
public void render(Renderer renderer, int w, int h, boolean sel) {
	float vw = renderer.gc.getWidth();
	float vh = renderer.gc.getHeight();
	float bx, by, bw, bh;
	bw = vh*1.778125f;
	bh = vh;
	bx = (vw-bw)/2;
	by = 0;
	renderer.fillRect(Color.black, bx, by, bw, bh);
	if (time < stepTime*0.5) {
		sekien.setAlpha(time/(stepTime*0.5f));
		sekien.draw(bx, by, bw, bh);
	} else if (time < stepTime*2.5f) {
		sekien.setAlpha(1);
		sekien.draw(bx, by, bw, bh);
	} else if (time < stepTime*3f) {
		sekien.draw(bx, by, bw, bh);
		game.setAlpha((time-stepTime*2.5f)/(stepTime*0.5f));
		game.draw(bx, by, bw, bh);
	} else if (time < stepTime*5f) {
		game.setAlpha(1);
		game.draw(bx, by, bw, bh);
	} else if (time < stepTime*5.5f) {
		game.setAlpha(1-((time-stepTime*5f)/(stepTime*0.5f)));
		game.draw(bx, by, bw, bh);
	} else {
		StateManager.forcePop();
		StateManager.setStateInitial("Main");
	}
	if (Globals.get("debug", false)) renderer.text(0, 16, Float.toString((float) Math.floor((time/stepTime)*10)/10));
}

@Override
public void update(GameContainer gc) {
	time++;
}

@Override
public Node nodeAt(int x, int y) {
	return this;
}

@Override
public void setSel(int x, int y) {
}

@Override
public void onAction(Action action) {
	if (action.equals(Action.SELECT))
		time = 5*(int) stepTime;
}

@Override
public Dimension getDimensions(boolean sel) {
	return new Dimension(640, 480);
}
}
