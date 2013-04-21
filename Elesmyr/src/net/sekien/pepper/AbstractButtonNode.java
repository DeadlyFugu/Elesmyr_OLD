package net.sekien.pepper;

import java.awt.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 21/04/13 Time: 4:32 PM To change this template use File | Settings |
 * File Templates.
 */
public abstract class AbstractButtonNode extends ActionNode {

private final String text;

public AbstractButtonNode(String name, String text) {
	super(name);
	this.text = text;
}

@Override
public void render(Renderer renderer, int w, int h, boolean sel) {
	if (h==-1)
		h = (sel?32:32);
	renderer.rect(0, 0, w, h, true, true, false, false, Renderer.BoxStyle.HFADE);
	if (sel)
		renderer.rect(0, 0, w, h, true, true, false, false, Renderer.BoxStyle.SEL);
	renderer.text(w/2-renderer.textWidth(text)/2, 11, text);
}

@Override
public Dimension getDimensions(boolean sel) {
	return new Dimension(440, (sel?32:32));
}

public String getMessage() {
	return text;
}
}
