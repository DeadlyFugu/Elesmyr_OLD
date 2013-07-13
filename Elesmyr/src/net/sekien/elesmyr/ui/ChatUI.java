package net.sekien.elesmyr.ui;

import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.FontRenderer;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.Input;
import net.sekien.pepper.Renderer;
import org.newdawn.slick.*;

public class ChatUI implements UserInterface {

private String msg;
private String type;
private int sel = 0;
private Message recMsg;
private int killTimer = -1;

private boolean inited = false;

@Override
public void ctor(String extd) {
}

@Override
public boolean inited() {
	return inited;
}

@Override
public void init(GameContainer gc,
                 MessageEndPoint receiver) throws SlickException {
	inited = true;
}

@Override
public void render(Renderer renderer, Camera cam, GameClient receiver) throws SlickException { //TODO: Make this look good.
	Graphics g = renderer.g;
	if (type.equals("talk")) {
		FontRenderer.drawString(77, 17, msg, g);
	} else if (type.equals("talkwf")) {
		FontRenderer.drawString(77, 17, msg.split(":", 2)[1], g);
	} else if (type.equals("ask")) {
		FontRenderer.drawString(77, 17, msg.split("\\|")[0], g);
		for (int i = 0; i < msg.split("\\|").length-1; i++) {
			if (i==sel) {
				FontRenderer.drawString(346, 17+i*16, ">", g);
			}
			FontRenderer.drawString(356, 17+i*16, msg.split("\\|")[i+1].split(":", 2)[1], g);
		}
	} else {
		FontRenderer.drawString(77, 17, "ERROR: ChatUI type = "+type, g);
	}
	if (killTimer > 0) {
		killTimer--;
		if (killTimer==0) {
			receiver.ui.remove(this);
		}
	}
}

@Override
public void update(GameContainer gc, GameClient receiver) {
	org.newdawn.slick.Input in = gc.getInput();
	if (Input.isKeyPressed(gc, "int")) {
		if (type.equals("ask"))
			recMsg.reply(recMsg.getSender()+".tresponse", FontRenderer.getLang().name()+"|"+msg.split("\\|")[sel+1].split(":")[0], null);
		if (type.equals("talkwf"))
			recMsg.reply(recMsg.getSender()+".tresponse", FontRenderer.getLang().name()+"|"+msg.split(":")[0], null);
		killTimer = 10;
	}
	if (in.isKeyPressed(org.newdawn.slick.Input.KEY_UP) && type.equals("ask"))
		if (sel > 0)
			sel--;
	if (in.isKeyPressed(org.newdawn.slick.Input.KEY_DOWN) && type.equals("ask"))
		if (sel < msg.split("\\|").length-2)
			sel++;
}

@Override
public boolean blockUpdates() {
	return true;
}

public void setMsg(String type, String data, Message recMsg) {
	this.type = type;
	this.msg = data;
	this.recMsg = recMsg;
	sel = 0;
	killTimer = -1;
}

}
