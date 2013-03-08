package net.halitesoft.lote.ui;

import net.halitesoft.lote.msgsys.MessageReceiver;
import net.halitesoft.lote.player.Camera;
import net.halitesoft.lote.system.FontRenderer;
import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.system.Globals;
import net.halitesoft.lote.system.Main;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA. User: matt Date: 8/03/13 Time: 5:00 PM To change this template use File | Settings | File
 * Templates.
 */
public class BookUI implements UserInterface {

private ArrayList<String> pages;
private int page;
private Image bg;

private boolean inited=false;

@Override
public boolean inited() {
	return inited;
}

@Override
public void ctor(String extd) {
	pages=new ArrayList<String>();
}

public void addPage(String page) {
	pages.add(page);
}

@Override
public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver) throws SlickException {
	inited=true;
	bg=new Image("data/ui/book.png", false, 0);
}

@Override
public void render(GameContainer gc, StateBasedGame sbg, Graphics g, Camera cam, GameClient receiver) throws SlickException {
	int xoff=(Main.INTERNAL_RESX/2);
	bg.draw(xoff-320, 0);
	if (Globals.get("debug", false))
		FontRenderer.drawString(xoff-260, 64, (page*2+1)+"/"+(pages.size()), g);
	drawPage(xoff-248, pages.get(page*2), g);
	if (pages.size()!=page*2+1)
		drawPage(xoff+12, pages.get(page*2+1), g);
}

private void drawPage(int x, String s, Graphics g) {
	int i=0;
	for (String ln : s.split("\n")) {
		FontRenderer.drawString(x, 74+i*14, ln, g);
		i++;
	}
}

@Override
public void update(GameContainer gc, StateBasedGame sbg, GameClient receiver) {
	Input in=gc.getInput();
	if (in.isKeyPressed(Input.KEY_LEFT))
		if (page>0)
			page--;
	if (in.isKeyPressed(Input.KEY_RIGHT))
		if (page*2<pages.size()-2)
			page++;
}

@Override
public boolean blockUpdates() {
	return true;
}
}
