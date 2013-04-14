package net.sekien.lote.world.entity;

import net.sekien.hbt.HBTTools;
import net.sekien.lote.msgsys.Message;
import net.sekien.lote.msgsys.MessageReceiver;
import net.sekien.lote.msgsys.MessageSystem;
import net.sekien.lote.player.Camera;
import net.sekien.lote.system.GameClient;
import net.sekien.lote.util.FileHandler;
import net.sekien.lote.world.Region;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class EntityUI extends Entity {
Image spr;

@Override
public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver)
		throws SlickException {
	spr = FileHandler.getImage("ent."+extd.split(",", 2)[0]);
}

@Override
public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
                   Camera cam, GameClient receiver) throws SlickException {
	if (spr==null)
		init(gc, sbg, receiver);
	spr.draw(xs+cam.getXOff(), ys+cam.getYOff());
}

@Override
public void interact(Region region, EntityPlayer entityPlayer, MessageReceiver receiver, Message msg) {
	MessageSystem.sendClient(this, msg.getConnection(), new Message("CLIENT.openUI", HBTTools.msgString("ui", extd.split(",", 2)[1])), false);
}
}
