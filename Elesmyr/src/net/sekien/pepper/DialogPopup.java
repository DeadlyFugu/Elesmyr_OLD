/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.pepper;

import com.esotericsoftware.minlog.Log;

import java.awt.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 5:12 PM To change this template use File | Settings |
 * File Templates.
 */
public abstract class DialogPopup extends PopupNode {
private final String message;

public DialogPopup(String name, String message, int def) {
	super(name);
	this.message = message;
	sel = def;
	this.def = def;
}

@Override
public boolean receiveActions() {
	return true;
}

private int sel = 0;
private int def = 0;

@Override
public void render(Renderer renderer, int w, int h, boolean sel) {
	int cx = w/2;
	int cy = h/2;
	int width = Math.max(200, renderer.textWidth(message)+38);
	renderer.rect(cx-width/2, cy-48, width, 96, Renderer.BoxStyle.FULL);
	renderer.textCentered(cx, cy-44, message);
	renderer.rect(cx-width/2, cy+16, width, 32, true, false, false, false, Renderer.BoxStyle.OUTLINE);
	renderer.sel(cx-width/2+(this.sel*width/2), cy+16, width/2, 32);
	renderer.textCentered(cx-width/4, cy+24, "Yes");
	renderer.textCentered(cx+width/4, cy+24, "No");
}

@Override
public void onAction(Action action) {
	if (action==Action.SELECT) {
		onSelect(sel);
		close();
	} else if (action==Action.BACK) {
		onSelect(def);
		close();
	} else if (action==Action.LEFT && sel==1) {
		sel = 0;
	} else if (action==Action.RIGHT && sel==0) {
		sel = 1;
	} else {
		Log.warn("Action ignored "+action);
	}
}

protected abstract void onSelect(int sel);

@Override
public Dimension getDimensions(boolean sel) {
	return null;  //To change body of implemented methods use File | Settings | File Templates.
}
}