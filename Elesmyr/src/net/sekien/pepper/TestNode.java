/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.pepper;

import java.awt.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 11:07 AM To change this template use File | Settings |
 * File Templates.
 */
public class TestNode extends Node {
public TestNode(String name) {
	super(name);
}

@Override
public void render(Renderer renderer, int w, int h, boolean sel) {
	//renderer.renderThing();
	//renderer.rectPos(50,50,w-50, h-50, Renderer.BoxStyle.OUTLINE);
	renderer.rectPos(100, 0, w-100, 100, false, false, true, true, Renderer.BoxStyle.UPFADE);
	renderer.rectPos(100, 100, w-100, h-100, false, false, true, true, Renderer.BoxStyle.FULL);
	renderer.rectPos(100, h-100, w-100, h, false, false, true, true, Renderer.BoxStyle.DOWNFADE);

}

@Override
public Node nodeAt(int x, int y) {
	return this;
}

@Override
public void setSel(int x, int y) {
	System.out.println("sel at "+x+","+y);
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
