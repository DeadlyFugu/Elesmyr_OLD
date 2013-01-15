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
		so = new ScriptObject(extd.split(",",2)[0],extd.split(",",2)[1],this);
		so.call("init",false," ENTID="+name+" X="+x+" Y="+y,null);
		constantUpdate=true;
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver)
			throws SlickException {
		if (so.getVar("spr")!=null)
			spr = new Image("data/ent/"+so.getVar("spr")+".png",false,0);
		inited = true;
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
			Camera cam, GameClient receiver) throws SlickException {
		if (!inited)
			init(gc,sbg,receiver);
		if (spr!=null)
			spr.draw(xs+cam.getXOff()-96,ys+cam.getYOff()-190);
	}

	@Override
	public void clientUpdate(GameContainer gc, StateBasedGame sbg, GameClient receiver) {
		super.clientUpdate(gc, sbg, receiver);
		//so.call("update",true,"",receiver);
	}
	
	@Override
	public void update(Region region, GameServer receiver) {
		so.call("update",false,"",receiver);
	}
	
	@Override
	protected void receiveMessageExt(Message msg, MessageReceiver receiver) {
		if (msg.getName().equals("setInitVar")) {
			initVar=msg.getData();
			extd=extd.split(",",2)[0]+","+initVar;
			readVarFromInitVar();
		} else {
			so.receiveMessage(msg,receiver);
		}
	}
	
	protected void readVarFromInitVar() {
		x=(int) Float.parseFloat(so.getVar("X"));
		y=(int) Float.parseFloat(so.getVar("Y"));
		if (so.getVar("constantUpdate")!=null)
			constantUpdate=Boolean.parseBoolean(so.getVar("constantUpdate"));
	}

	@Override
	public void hurt(Region region, int damage, MessageReceiver receiver) {
		so.call("hurt",!receiver.isServer()," DAMAGE="+damage,receiver);
	}
	
	@Override
	public void interact(Region region, EntityPlayer entityPlayer, MessageReceiver receiver) {
		so.call("interact",!receiver.isServer()," PLAYER="+region.name+"."+entityPlayer.name,receiver);
	}

	/*private void runScript(String func, boolean client, String initVarExt, MessageReceiver receiver) {
		String ret = ScriptRunner.run(func, extd.split(",",2)[0], receiver, initVar+" CLIENT="+client+" SERVER="+!client+initVarExt);
		if (!ret.startsWith("F")) {
			initVar = ret.substring(1);
			extd=extd.split(",",2)[0]+","+initVar;
		}
	}*/
	
	@Override
	public void save(Save save) {
		extd=extd.split(",",2)[0]+","+initVar;
	}
}
