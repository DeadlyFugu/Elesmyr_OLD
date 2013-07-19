/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.pepper;

import java.awt.*;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA. User: matt Date: 22/04/13 Time: 2:43 PM To change this template use File | Settings |
 * File Templates.
 */
public class TitledListNode extends ListNode {
public TitledListNode(String name, String title) {
	super(name);
	sel = 1;
	addChild(new TitleNode("title", title));
}

@Override
public void onAction(Action action) {
	if (action==Action.UP) {
		if (sel > 1)
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

private class TitleNode extends Node {

	private final String text;

	public TitleNode(String name, String text) {
		super(name);
		this.text = text;
	}

	@Override
	public void render(Renderer renderer, int w, int h, boolean sel) {
		renderer.g.pushTransform();
		renderer.g.scale(5, 5);
		renderer.text((w-renderer.textWidth(text)*5)/5, 1, text);
		renderer.g.popTransform();
	}

	@Override
	public Node nodeAt(int x, int y) {return this;}

	@Override
	public void setSel(int x, int y) {}

	@Override
	public void onAction(Action action) {}

	@Override
	public Dimension getDimensions(boolean sel) {return new Dimension(440, 96);}

	@Override
	public Iterator iterator() {throw new AssertionError("ImageNodes don't have children.");}

	@Override
	public void addChild(Node child) {throw new AssertionError("ImageNodes don't have children.");}

	@Override
	public void clear() {throw new AssertionError("ImageNodes don't have children.");}
}
}
