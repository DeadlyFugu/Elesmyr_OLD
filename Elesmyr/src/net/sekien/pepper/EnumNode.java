package net.sekien.pepper;

import java.awt.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 8:48 PM To change this template use File | Settings |
 * File Templates.
 */
public abstract class EnumNode extends ActionNode {
private String message;
private boolean slide;
protected boolean ordinal;
protected Class options;
protected int sel = 0;

protected <E extends Enum<E>> EnumNode(String name, String message, boolean ordinal, Class<E> options) {
	super(name);
	this.message = message;
	this.slide = true;
	this.ordinal = ordinal;
	this.options = options;
}

private int htarget;
private int hcurrent;

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
		if (slide) {
			Object[] optVal = options.getEnumConstants();
			renderer.rect(0, 32, w, 32, true, false, false, false, Renderer.BoxStyle.OUTLINE);
			renderer.sel(0, 32, w, 32);
			renderer.textCentered(w/2, 42, optVal[this.sel].toString());
		} else {
			Object[] optVal = options.getEnumConstants();
			renderer.rect(0, 32, w, 32, true, false, false, false, Renderer.BoxStyle.OUTLINE);
			renderer.sel(this.sel*(w/optVal.length), 32, w/optVal.length, 32);
			for (int i = 0; i < optVal.length; i++) {
				renderer.textCentered(((i+1)*(w/optVal.length))-(w/optVal.length/2), 42, optVal[i].toString());
			}
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
	} else if (action==Action.RIGHT && sel < options.getEnumConstants().length-1) {
		sel++;
	} else {
		System.out.println("Action ignored "+action);
	}
}

protected abstract void onSelect(int sel);

@Override
public Dimension getDimensions(boolean sel) {
	return new Dimension(440, (sel?64:32));
}
}

