package net.halitesoft.lote.world.entity;

import net.halitesoft.lote.msgsys.Message;
import net.halitesoft.lote.msgsys.MessageReceiver;
import net.halitesoft.lote.msgsys.MessageSystem;
import net.halitesoft.lote.player.Camera;
import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.world.Region;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

public class EntityTalk extends Entity {
	Image spr;
	private DialogueHandler dh;
	@Override
	public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver)
			throws SlickException {
		spr = new Image("data/ent/"+extd.split(",",2)[0]+".png",false,0);
	}

	@Override
	public void initSERV() {
		dh=new DialogueHandler("test");
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
			Camera cam, GameClient receiver) throws SlickException {
		if (spr==null)
			init(gc,sbg,receiver);
		spr.draw(xs+cam.getXOff(),ys+cam.getYOff());
	}
	@Override
	public void interact(Region region, EntityPlayer entityPlayer, MessageReceiver receiver, Connection connection) {
		MessageSystem.sendClient(this,connection,new Message("CLIENT.echointwl",""),false);
	}
	
	@Override
	public void receiveMessageExt(Message msg, MessageReceiver receiver) {
		if (msg.getName().equals("tresponse")) {
			dh.response(this,msg.getData().split("\\|",2)[0],msg.getData().split("\\|",2)[1],msg.getConnection());
		} else {
			Log.warn("EntityPlayer ingored message "+msg);
		}
	}

	private void response(String lang, String call, Connection connection) {
		if (call.equals("int")) {
			if (lang.equals("JP"))
				MessageSystem.sendClient(this,connection,new Message("CLIENT.talk","ask:お元気ですか?|good:お元気です|bad:お元気じゃない|meh:無関心"),false);
			else
				MessageSystem.sendClient(this,connection,new Message("CLIENT.talk","ask:How are you?|good:Good.|bad:Bad.|meh:Meh."),false);
		} else if (call.equals("good"))  {
			MessageSystem.sendClient(this,connection,new Message("CLIENT.talk","talk:That's great!"),false);
		} else if (call.equals("bad"))  {
			MessageSystem.sendClient(this,connection,new Message("CLIENT.talk","talkwf:again:Aww :("),false);
		} else if (call.equals("meh"))  {
			MessageSystem.sendClient(this,connection,new Message("CLIENT.talk","talk:Oh, okay."),false);
		} else if (call.equals("again")) {
			MessageSystem.sendClient(this,connection,new Message("CLIENT.talk","ask:Are you still bad?|bad:Yes.|good:No."),false);
		} else {
			Log.warn("Unrecognised tresponse call '"+call+"'");
		}
	}
}
