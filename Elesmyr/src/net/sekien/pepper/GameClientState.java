package net.sekien.pepper;

import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.Main;
import org.newdawn.slick.Color;
import org.newdawn.slick.*;

import java.awt.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 22/04/13 Time: 5:02 PM To change this template use File | Settings |
 * File Templates.
 */
public class GameClientState extends Node {
private GameClient client;

public GameClientState(String name) {
	super(name);
}

public void setClient(GameClient client) {
	this.client = client;
}

@Override
public void render(Renderer renderer, int w, int h, boolean sel) {
	try {
		client.render(renderer.gc, renderer.g);
	} catch (SlickException e) {
		StateManager.back();
		Main.handleError(e);
	}
	//renderer.rect(0, 0, 156, 48, false, true, false, true, Renderer.BoxStyle.FULL);
	//renderer.text(10, 11, "GameClientState");
}

@Override
public void transitionEnter(Renderer renderer, int w, int h, boolean sel, float time) {
	StateManager.getBG().draw(0, 0, Main.INTERNAL_RESX, Main.INTERNAL_RESY, new Color(1, 1, 1, time));
	render(renderer, w, h, sel);
}

@Override
public void transitionLeave(Renderer renderer, int w, int h, boolean sel, float time) {
	StateManager.getBG().draw(0, 0, Main.INTERNAL_RESX, Main.INTERNAL_RESY, new Color(1, 1, 1, time));
	render(renderer, w, h, sel);
}

@Override
public void update(GameContainer gc) {
	try {
		client.update(gc, 1);
	} catch (SlickException e) {
		StateManager.back();
		Main.handleError(e);
	}
}

@Override
public boolean rawKey() {
	return true;
}

@Override
public Node nodeAt(int x, int y) {
	return this;
}

@Override
public void setSel(int x, int y) {
	System.out.println("setSel "+x+","+y);
}

@Override
public void onAction(Action action) {
	System.out.println(action);
}

@Override
public Dimension getDimensions(boolean sel) {
	return new Dimension(640, 480);
}
}
