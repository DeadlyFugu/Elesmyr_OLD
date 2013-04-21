package net.sekien.pepper;

import java.awt.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 21/04/13 Time: 10:33 AM To change this template use File | Settings |
 * File Templates.
 */
public abstract class TextNode extends ActionNode {
private String message;

protected TextNode(String name, String message) {
	super(name);
	this.message = message;
}

private int htarget = 32;
private int hcurrent = 32;
private boolean hasTextLock = false;

protected String text = "";

@Override
public void render(Renderer renderer, int w, int h, boolean sel) {
	htarget = (sel?64:32);
	if (hcurrent < htarget) {
		hcurrent += 8;
	} else if (hcurrent > htarget) {
		hcurrent -= 8;
	}

	if (sel) {
		if (!hasTextLock)
			if (StateManager.getTextLock(this)) {
				hasTextLock = true;
				StateManager.setTextBoxText(this, text);
			}
	} else {
		if (hasTextLock) {
			text = StateManager.getTextBoxText(this);
			StateManager.freeTextLock(this);
			hasTextLock = false;
		}
	}

	if (hasTextLock) {
		StateManager.setTextBoxCentered(this, w/2, 40);
	}

	h = hcurrent;
	renderer.rect(0, 0, w, h, true, true, false, false, Renderer.BoxStyle.HFADE);
	renderer.text(w/2-renderer.textWidth(message)/2, 11, message);
	if (sel) {
		renderer.rect(0, 32, w, 32, true, false, false, false, Renderer.BoxStyle.OUTLINE);
		renderer.sel(0, 32, w, 32);
		if (hasTextLock) StateManager.renderTextBox(this, renderer);
		//renderer.textCentered(w/2, 42, );
	}
}

@Override
public void onAction(Action action) {
	System.out.println(action);
	if (action==Action.SELECT) {
		onSelect();
	}
}

protected abstract void onSelect();

@Override
public Dimension getDimensions(boolean sel) {
	return new Dimension(440, hcurrent);
}
}