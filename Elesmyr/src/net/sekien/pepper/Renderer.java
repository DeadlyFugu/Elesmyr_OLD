package net.sekien.pepper;

import net.sekien.elesmyr.system.FontRenderer;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.elesmyr.util.ResourceType;
import org.newdawn.slick.*;
import org.newdawn.slick.util.Log;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 10:39 AM To change this template use File | Settings |
 * File Templates.
 */
public class Renderer {

private Image error;
private Image[][][] box;
public Graphics g;
public GameContainer gc;

Renderer() {
	try {
		error = new Image(FileHandler.parse("menu.error", ResourceType.IMAGE), false, 0);
		box = new Image[7][3][3];
		Image boxfull = new Image(FileHandler.parse("ui.box", ResourceType.IMAGE), false, 0);
		loadBoxStyle(0, boxfull);
		Image boxoutline = new Image(FileHandler.parse("ui.boxoutline", ResourceType.IMAGE), false, 0);
		loadBoxStyle(1, boxoutline);
		Image boxvfade = new Image(FileHandler.parse("ui.boxvfade", ResourceType.IMAGE), false, 0);
		loadBoxStyle(2, boxvfade);
		Image boxhfade = new Image(FileHandler.parse("ui.boxhfade", ResourceType.IMAGE), false, 0);
		loadBoxStyle(3, boxhfade);
		Image boxupfade = new Image(FileHandler.parse("ui.boxupfade", ResourceType.IMAGE), false, 0);
		loadBoxStyle(4, boxupfade);
		Image boxdownfade = new Image(FileHandler.parse("ui.boxdownfade", ResourceType.IMAGE), false, 0);
		loadBoxStyle(5, boxdownfade);
		Image boxsel = new Image(FileHandler.parse("ui.boxsel", ResourceType.IMAGE), false, 0);
		loadBoxStyle(6, boxsel);
	} catch (SlickException e) {
		Log.error(e);
	}
}

private void loadBoxStyle(int i, Image boxfull) {
	box[i][0][0] = boxfull.getSubImage(0, 0, 16, 16);
	box[i][0][1] = boxfull.getSubImage(0, 16, 16, 32);
	box[i][0][2] = boxfull.getSubImage(0, 48, 16, 16);
	box[i][1][0] = boxfull.getSubImage(16, 0, 32, 16);
	box[i][1][1] = boxfull.getSubImage(16, 16, 32, 32);
	box[i][1][2] = boxfull.getSubImage(16, 48, 32, 16);
	box[i][2][0] = boxfull.getSubImage(48, 0, 16, 16);
	box[i][2][1] = boxfull.getSubImage(48, 16, 16, 32);
	box[i][2][2] = boxfull.getSubImage(48, 48, 16, 16);
}

void setGraphicsAndGC(GameContainer gc, Graphics g) {
	this.g = g;
	this.gc = gc;
}

public void renderThing() {
	error.draw();
}

public void rect(int x, int y, int w, int h, BoxStyle style) {
	rectPos(x, y, x+w, y+h, true, true, true, true, style);
}

public void rectPos(int x, int y, int x2, int y2, BoxStyle style) {
	rectPos(x, y, x2, y2, true, true, true, true, style);
}

public void rect(int x, int y, int w, int h, boolean top, boolean bottom, boolean left, boolean right, BoxStyle style) {
	rectPos(x, y, x+w, y+h, top, bottom, left, right, style);
}

public void rectPos(int x, int y, int x2, int y2, boolean top, boolean bottom, boolean left, boolean right, BoxStyle style) {
	int inLeft = (left?x+16:x);
	int inRight = (right?x2-16:x2);
	int inTop = (top?y+16:y);
	int inBottom = (bottom?y2-16:y2);
	int inWidth = inRight-inLeft;
	int inHeight = inBottom-inTop;
	int s = style.ordinal();
	if (top && left)
		box[s][0][0].draw(x, y);
	if (left)
		box[s][0][1].draw(x, inTop, 16, inHeight);
	if (bottom && left)
		box[s][0][2].draw(x, inBottom);

	if (top)
		box[s][1][0].draw(inLeft, y, inWidth, 16);

	box[s][1][1].draw(inLeft, inTop, inWidth, inHeight);

	if (bottom)
		box[s][1][2].draw(inLeft, inBottom, inWidth, 16);

	if (top && right)
		box[s][2][0].draw(inRight, y);
	if (right)
		box[s][2][1].draw(inRight, inTop, 16, inHeight);
	if (bottom && right)
		box[s][2][2].draw(inRight, inBottom);
}

public void pushPos(int x, int y) {
	g.pushTransform();
	g.translate(x, y);
}

public void popPos() {
	g.popTransform();
}

public void text(int x, int y, String text) {
	text(x, y, text, Color.white);
}

public void text(int x, int y, String text, Color color) {
	String[] split = text.split("\n");
	for (int i = 0, splitLength = split.length; i < splitLength; i++) {
		String s = split[i];
		FontRenderer.drawString(x, y+(i*12), s, color, g);
	}
}

public int textWidth(String text) {
	if (text.contains("\n")) {
		int width = 0;
		for (String s : text.split("\n")) {
			width = Math.max(width, FontRenderer.getWidth(s));
		}
		return width;
	}
	return FontRenderer.getWidth(text);
}

public int textHeight(String text) {
	return text.split("\n").length*12;
}

public void textCentered(int cx, int y, String text) {
	String[] split = text.split("\n");
	for (int i = 0, splitLength = split.length; i < splitLength; i++) {
		String s = split[i];
		FontRenderer.drawString(cx-textWidth(s)/2, y+(i*12), s, g);
	}
}

public void sel(int x, int y, int width, int height) {
	rect(x, y, width, height, BoxStyle.SEL);
}

public void text(int x, int y, String text, boolean sel) {
	text(x, y, text, new Color(1, 1, 1, sel?1f:0.25f));
}

public void fillRect(Color color, float x, float y, float w, float h) {
	g.setColor(color);
	g.fillRect(x, y, w, h);
}

public enum BoxStyle {FULL, OUTLINE, VFADE, HFADE, UPFADE, DOWNFADE, SEL}
}
