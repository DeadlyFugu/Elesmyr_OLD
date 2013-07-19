/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.pepper;

import java.awt.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 2:54 PM To change this template use File | Settings |
 * File Templates.
 */
public class ErrorPopup extends PopupNode {

private final String message;
private final boolean goBack;

public ErrorPopup(String name, String message, boolean goBack) {
	super(name);
	this.message = message;
	this.goBack = goBack;
}

@Override
public boolean receiveActions() {
	return true;
}

@Override
public void render(Renderer renderer, int w, int h, boolean sel) {
	int cx = w/2;
	int cy = h/2;
	int height = Math.max(27, renderer.textHeight(message))+32+8;
	int width = Math.max(200, renderer.textWidth(message)+38);
	renderer.rect(cx-width/2, cy-height/2, width, height, Renderer.BoxStyle.FULL);
	renderer.textCentered(cx, cy-height/2+4, message);
	renderer.rect(cx-width/2, cy+height/2-32, width, 32, true, false, false, false, Renderer.BoxStyle.OUTLINE);
	renderer.sel(cx-width/2, cy+height/2-32, width, 32);
	renderer.textCentered(cx, cy+height/2-24, "OK");
}

@Override
public void onAction(Action action) {
	if (action==Action.SELECT || action==Action.BACK) {
		if (goBack)
			StateManager.back();
		close();
	}
}

@Override
public Dimension getDimensions(boolean sel) {
	return null;  //To change body of implemented methods use File | Settings | File Templates.
}
}
