package net.sekien.pepper;

import net.sekien.elesmyr.system.GameClient;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 22/04/13 Time: 5:02 PM To change this template use File | Settings |
 * File Templates.
 */
public class GameClientState extends Node {
private GameClient client;
private StateBasedGame sbg;

public GameClientState(String name) {
	super(name);
}

public void setClient(GameClient client, StateBasedGame sbg) {
	this.client = client;
	this.sbg = sbg;
}

@Override
public void render(Renderer renderer, int w, int h, boolean sel) {
	try {
		client.render(renderer.gc, null, renderer.g);
	} catch (SlickException e) {
		StateManager.error(e.toString(), true);
	}
	renderer.rect(0, 0, 156, 48, false, true, false, true, Renderer.BoxStyle.FULL);
	renderer.text(10, 11, "GameClientState");
}

@Override
public void update(GameContainer gc) {
	try {
		client.update(gc, null, 1);
	} catch (SlickException e) {
		StateManager.error(e.toString(), true);
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
