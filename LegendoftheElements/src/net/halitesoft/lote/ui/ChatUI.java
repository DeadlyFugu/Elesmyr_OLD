package net.halitesoft.lote.ui;

import net.halitesoft.lote.msgsys.Message;
import net.halitesoft.lote.msgsys.MessageReceiver;
import net.halitesoft.lote.player.Camera;
import net.halitesoft.lote.system.FontRenderer;
import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.system.Input;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class ChatUI implements UserInterface {

	private String msg;
	private String type;
	private int sel = 0;
	private Message recMsg;
	private int killTimer=-1;

	@Override
	public void ctor(String extd) {
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg,
			MessageReceiver receiver) throws SlickException {
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
			Camera cam, GameClient receiver) throws SlickException { //TODO: Make this look good.
		if (type.equals("talk")) {
			FontRenderer.drawString(77,17,msg, g);
		} else if (type.equals("talkwf")) {
			FontRenderer.drawString(77, 17, msg.split(":", 2)[1], g);
		} else if (type.equals("ask")) {
			FontRenderer.drawString(77,17,msg.split("\\|")[0], g);
			for (int i=0; i< msg.split("\\|").length-1; i++) {
				if (i==sel) {
					FontRenderer.drawString(346, 17+i*16, ">", g);
				}
				FontRenderer.drawString(356,17+i*16,msg.split("\\|")[i+1].split(":",2)[1], g);
			}
		} else {
			FontRenderer.drawString(77,17,"ERROR: ChatUI type = "+type, g);
		}
		if (killTimer>0) {
			killTimer--;
			if (killTimer==0) {
				receiver.ui.remove(this);
			}
		}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, GameClient receiver) {
		org.newdawn.slick.Input in = gc.getInput();
		if (Input.isKeyPressed(gc, "int")) {
			if (type.equals("ask"))
				recMsg.reply(recMsg.getSender()+".tresponse", FontRenderer.getLang().name()+"|"+msg.split("\\|")[sel+1].split(":")[0], null);
			if (type.equals("talkwf"))
				recMsg.reply(recMsg.getSender()+".tresponse", FontRenderer.getLang().name()+"|"+msg.split(":")[0], null);
			killTimer=10;
		}
		if (in.isKeyPressed(org.newdawn.slick.Input.KEY_UP) && type.equals("ask"))
			if (sel>0)
				sel--;
		if (in.isKeyPressed(org.newdawn.slick.Input.KEY_DOWN) && type.equals("ask"))
			if (sel<msg.split("\\|").length-2)
				sel++;
	}

	@Override
	public boolean blockUpdates() {
		return true;
	}

	public void setMsg(String type, String data, Message recMsg) {
		this.type=type;
		this.msg=data;
		this.recMsg=recMsg;
		sel=0;
		killTimer=-1;
	}

}
