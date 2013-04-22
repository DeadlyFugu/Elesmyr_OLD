package net.sekien.pepper;

import net.sekien.elesmyr.util.FileHandler;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.awt.*;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA. User: matt Date: 22/04/13 Time: 2:28 PM To change this template use File | Settings |
 * File Templates.
 */
public class ImageNode extends Node {

protected Image image = null;

public ImageNode(String name, String image) {
	super(name);
	try {
		this.image = FileHandler.getImage(image);
	} catch (SlickException e) {
		e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	}
}

@Override
public void render(Renderer renderer, int w, int h, boolean sel) {
	image.draw(0, 0, w==-1?image.getWidth():Math.min(image.getWidth(), w), h==-1?image.getHeight():Math.min(image.getHeight(), h));
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
}

@Override
public Dimension getDimensions(boolean sel) {
	return new Dimension(image.getWidth(), image.getHeight());
}

@Override
public Iterator iterator() {
	throw new AssertionError("ImageNodes don't have children.");
}

@Override
public void addChild(Node child) {
	throw new AssertionError("ImageNodes don't have children.");
}

public void clear() {
	throw new AssertionError("ImageNodes don't have children.");
}
}
