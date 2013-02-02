package net.halitesoft.lote.world.entity;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.Connection;

import net.halitesoft.lote.msgsys.Message;
import net.halitesoft.lote.msgsys.MessageReceiver;
import net.halitesoft.lote.msgsys.MessageSystem;
import net.halitesoft.lote.player.Camera;
import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.world.Region;
import net.halitesoft.lote.world.item.ItemFactory;

public class EntityUI extends Entity {
	Image spr;
	@Override
	public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver)
			throws SlickException {
		spr = new Image("data/ent/"+extd.split(",",2)[0]+".png",false,0);
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
		MessageSystem.sendClient(this,connection,new Message("CLIENT.openUI",extd.split(",",2)[1]),false);
	}
}
