package net.sekien.pepper;

import java.awt.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 1:15 PM To change this template use File | Settings |
 * File Templates.
 */
public class ButtonNode extends ActionNode {

private final String text;
private final String action;

public ButtonNode(String name, String text, String action) {
	super(name);
	this.text = text;
	this.action = action;
}

@Override
public void onAction(Action enumAction) {
	if (enumAction==Action.SELECT) {
		String func = action;
		String arg = null;
		if (action.contains(" ")) {
			func = action.split(" ", 2)[0];
			arg = action.split(" ", 2)[1];
		}
		if (func.equals("STATE")) {
			if (arg!=null) {
				StateManager.setState(arg);
			} else {
				StateManager.error("Invalid action "+action, false);
			}
		} else if (func.equals("BACK")) {
			StateManager.back();
		} else if (func.equals("SAVE")) {
			if (arg!=null)
				StateManager.updFunc("SAVE "+arg);
			else
				StateManager.error("Invalid action "+action, false);
		} else if (func.equals("MAINMENU")) {
			StateManager.updFunc("MAINMENU");
		}
	}
}

@Override
public void render(Renderer renderer, int w, int h, boolean sel) {
	if (h==-1)
		h = (sel?64:32);
	renderer.rect(0, 0, w, h, true, true, false, false, Renderer.BoxStyle.HFADE);
	if (sel)
		renderer.rect(0, 0, w, h, true, true, false, false, Renderer.BoxStyle.SEL);
	renderer.text(w/2-renderer.textWidth(text)/2, 11, text);
}

@Override
public Dimension getDimensions(boolean sel) {
	return new Dimension(440, (sel?64:32));
}
}
