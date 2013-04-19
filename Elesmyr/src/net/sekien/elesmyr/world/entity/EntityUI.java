package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.msgsys.MessageSystem;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.elesmyr.world.Region;
import net.sekien.hbt.HBTTools;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class EntityUI extends Entity {
Image spr;

@Override
public void init(GameContainer gc, StateBasedGame sbg, MessageEndPoint receiver)
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
public void interact(Region region, EntityPlayer entityPlayer, MessageEndPoint receiver, Message msg) {
	MessageSystem.sendClient(this, msg.getConnection(), new Message("CLIENT.openUI", HBTTools.msgString("ui", extd.split(",", 2)[1])), false);
}
}
