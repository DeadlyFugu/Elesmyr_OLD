package net.sekien.pepper;

import org.newdawn.slick.GameContainer;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 10:27 AM To change this template use File | Settings |
 * File Templates.
 */
public abstract class Node implements Iterable {
protected LinkedList<Node> children;
protected String name;

public Node(String name) {
	this.name = name;
	children = new LinkedList<Node>();
}

public String getName() {
	return name;
}

@Override
public Iterator iterator() {
	return children.iterator();
}

public void addChild(Node child) {
	children.add(child);
}

public Node getChild(String name) {
	for (Node child : children) {
		if (child.name.equals(name)) {
			return child;
		}
	}
	return null;
}

public void clear() {
	children.clear();
}

public abstract void render(Renderer renderer, int w, int h, boolean sel);

public abstract Node nodeAt(int x, int y);

public abstract void setSel(int x, int y);

public abstract void onAction(Action action);

public void update(GameContainer gc) {
	for (Node child : children)
		child.update(gc);
}

public boolean wantKeys() {return false;}

public abstract Dimension getDimensions(boolean sel);

public void transitionEnter(Renderer renderer, int w, int h, boolean sel, float time) {
	render(renderer, w, h, sel);
}

public void transitionLeave(Renderer renderer, int w, int h, boolean sel, float time) {
	transitionEnter(renderer, w, h, sel, 1-time);
}
}
