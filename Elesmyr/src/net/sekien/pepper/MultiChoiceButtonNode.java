package net.sekien.pepper;

import java.awt.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 6:44 PM To change this template use File | Settings |
 * File Templates.
 */
public abstract class MultiChoiceButtonNode extends ActionNode {

protected String message;
protected String[] options;
protected int sel = 0;

protected MultiChoiceButtonNode(String name, String message, String[] options) {
	super(name);
	this.message = message;
	this.options = options;
}

protected int htarget = 32;
protected int hcurrent = 32;

@Override
public void render(Renderer renderer, int w, int h, boolean sel) {
	htarget = (sel?64:32);
	if (hcurrent < htarget) {
		hcurrent += 8;
	} else if (hcurrent > htarget) {
		hcurrent -= 8;
	}
	h = hcurrent;
	renderer.rect(0, 0, w, h, true, true, false, false, Renderer.BoxStyle.HFADE);
	renderer.text(w/2-renderer.textWidth(message)/2, 11, message, sel);
	if (sel) {
		renderer.rect(0, 32, w, 32, true, false, false, false, Renderer.BoxStyle.OUTLINE);
		renderer.sel(this.sel*(w/options.length), 32, w/options.length, 32);
		for (int i = 0; i < options.length; i++) {
			renderer.textCentered(((i+1)*(w/options.length))-(w/options.length/2), 42, options[i]);
		}
	}
}

@Override
public void onAction(Action action) {
	System.out.println(action+","+sel);
	if (action==Action.SELECT) {
		onSelect(sel);
	} else if (action==Action.LEFT && sel > 0) {
		sel--;
	} else if (action==Action.RIGHT && sel < options.length-1) {
		sel++;
	} else {
		System.out.println("Action ignored "+action);
	}
}

protected abstract void onSelect(int sel);

@Override
public Dimension getDimensions(boolean sel) {
	return new Dimension(440, hcurrent);
}
}
