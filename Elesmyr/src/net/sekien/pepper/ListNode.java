package net.sekien.pepper;

import java.awt.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 12:56 PM To change this template use File | Settings |
 * File Templates.
 */
public class ListNode extends Node {

private int sel = 0;

public ListNode(String name) {
	super(name);
}

@Override
public void render(Renderer renderer, int w, int h, boolean sel) {
	renderer.rectPos(100, 0, w-100, 100, false, false, true, true, Renderer.BoxStyle.UPFADE);
	renderer.rectPos(100, 100, w-100, h-100, false, false, true, true, Renderer.BoxStyle.FULL);
	renderer.rectPos(100, h-100, w-100, h, false, false, true, true, Renderer.BoxStyle.DOWNFADE);

	renderer.rectPos(0, 0, renderer.textWidth(name)+20, 32, false, true, false, true, Renderer.BoxStyle.FULL);
	renderer.text(10, 11, name);

	int y = 0;
	int i = 0;
	for (Node child : children) {
		renderer.pushPos(100, y);
		child.render(renderer, 440, -1, i==this.sel);
		y += child.getDimensions(i==this.sel).height;
		i++;
		renderer.popPos();
	}
}

@Override
public void transitionEnter(Renderer renderer, int w, int h, boolean sel, float time) {
	renderer.rectPos(100, 0, w-100, 100, false, false, true, true, Renderer.BoxStyle.UPFADE);
	renderer.rectPos(100, 100, w-100, h-100, false, false, true, true, Renderer.BoxStyle.FULL);
	renderer.rectPos(100, h-100, w-100, h, false, false, true, true, Renderer.BoxStyle.DOWNFADE);

	renderer.rect((int) (-(renderer.textWidth(name)+20)*(1-time)), 0, renderer.textWidth(name)+20, 32, false, true, false, true, Renderer.BoxStyle.FULL);
	renderer.text((int) (-(renderer.textWidth(name)+20)*(1-time)+10), 11, name);

	int y = (int) ((-32-children.size()*32)*(1-time));
	int i = 0;
	for (Node child : children) {
		renderer.pushPos(100, y);
		child.render(renderer, 440, -1, i==this.sel);
		y += child.getDimensions(i==this.sel).height;
		i++;
		renderer.popPos();
	}
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
	if (action==Action.UP) {
		if (sel > 0)
			sel--;
	} else if (action==Action.DOWN) {
		if (sel < children.size()-1) {
			sel++;
		}
	} else {
		if (children.size() > 0)
			children.get(sel).onAction(action);
	}
}

@Override
public Dimension getDimensions(boolean sel) {
	return new Dimension(440, 480);
}
}
