package net.sekien.elesmyr.ui;

import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.FontRenderer;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.Globals;
import net.sekien.elesmyr.system.Main;
import net.sekien.elesmyr.util.FileHandler;
import org.newdawn.slick.*;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA. User: matt Date: 8/03/13 Time: 5:00 PM To change this template use File | Settings | File
 * Templates.
 */
public class BookUI implements UserInterface {

private ArrayList<String> pages;
private int page;
private Image bg;

private boolean inited = false;

@Override
public boolean inited() {
	return inited;
}

@Override
public void ctor(String extd) {
	pages = new ArrayList<String>();
}

public void addPage(String page) {
	pages.add(page);
}

@Override
public void init(GameContainer gc, MessageEndPoint receiver) throws SlickException {
	inited = true;
	bg = FileHandler.getImage("ui.book");
}

@Override
public void render(GameContainer gc, Graphics g, Camera cam, GameClient receiver) throws SlickException {
	int xoff = (Main.INTERNAL_RESX/2);
	bg.draw(xoff-320, 0);
	if (Globals.get("debug", false))
		FontRenderer.drawString(xoff-260, 64, (page*2+1)+"/"+(pages.size()), g);
	drawPage(xoff-248, pages.get(page*2), g);
	if (pages.size()!=page*2+1)
		drawPage(xoff+12, pages.get(page*2+1), g);
}

private void drawPage(int x, String s, Graphics g) {
	int i = 0;
	for (String ln : s.split("\n")) {
		FontRenderer.drawStringBook(x, 74+i*14, ln, g);
		i++;
	}
}

@Override
public void update(GameContainer gc, GameClient receiver) {
	Input in = gc.getInput();
	if (in.isKeyPressed(Input.KEY_LEFT))
		if (page > 0)
			page--;
	if (in.isKeyPressed(Input.KEY_RIGHT))
		if (page*2 < pages.size()-2)
			page++;
}

@Override
public boolean blockUpdates() {
	return true;
}
}
