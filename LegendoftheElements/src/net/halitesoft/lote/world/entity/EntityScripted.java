package net.halitesoft.lote.world.entity;


import net.halitesoft.lote.Message;
import net.halitesoft.lote.MessageReceiver;
import net.halitesoft.lote.Save;
import net.halitesoft.lote.ScriptObject;
import net.halitesoft.lote.ScriptRunner;
import net.halitesoft.lote.system.Camera;
import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.system.GameServer;
import net.halitesoft.lote.system.Main;
import net.halitesoft.lote.world.Region;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class EntityScripted extends Entity {

	Image spr = null;
	boolean inited = false;
	String initVar = "";
	ScriptObject so;
	
	public EntityScripted() {
		constantUpdate=true;
	}
	
	@Override
	protected void initSERV() {
		so = new ScriptObject(extd,this);
		so.call("init",new Object[] {this});
		constantUpdate=true;
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver)
			throws SlickException {
		so.call("initG",new Object[] {this,gc,receiver});
		inited = true;
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
			Camera cam, GameClient receiver) throws SlickException {
		if (!inited)
			init(gc,sbg,receiver);
		if (spr!=null)
			spr.draw(xs+cam.getXOff()-96,ys+cam.getYOff()-190);
		so.call("render",new Object[] {this,gc,receiver});
	}

	@Override
	public void clientUpdate(GameContainer gc, StateBasedGame sbg, GameClient receiver) {
		super.clientUpdate(gc, sbg, receiver);
		//so.call("update",true,"",receiver);
	}
	
	@Override
	public void update(Region region, GameServer receiver) {
		so.call("update",new Object[] {this,region,receiver});
	}
	
	@Override
	protected void receiveMessageExt(Message msg, MessageReceiver receiver) {
		if (msg.getName().equals("setInitVar")) {
			initVar=msg.getData();
			extd=extd.split(",",2)[0]+","+initVar;
		} else {
			so.receiveMessage(msg,receiver);
		}
	}

	@Override
	public void hurt(Region region, Entity entity, MessageReceiver receiver) {
		so.call("hurt",new Object[] {this,region,entity,receiver});
	}
	
	@Override
	public void interact(Region region, EntityPlayer entityPlayer, MessageReceiver receiver) {
		so.call("interact",new Object[] {this,region,entityPlayer,receiver});
	}
	
	@Override
	public void save(Save save) {
		extd=extd.split(",",2)[0]+","+initVar;
	}
}
