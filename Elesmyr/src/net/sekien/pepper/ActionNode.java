package net.sekien.pepper;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 1:05 PM To change this template use File | Settings |
 * File Templates.
 */
public abstract class ActionNode extends Node {

public ActionNode(String name) {
	super(name);
}

@Override
public Iterator iterator() {
	throw new AssertionError("ActionNodes don't have children.");
}

@Override
public void addChild(Node child) {
	throw new AssertionError("ActionNodes don't have children.");
}

public void clear() {
	throw new AssertionError("ActionNodes don't have children.");
}

public Node nodeAt(int x, int y) {return this;}

public void setSel(int x, int y) {}
}