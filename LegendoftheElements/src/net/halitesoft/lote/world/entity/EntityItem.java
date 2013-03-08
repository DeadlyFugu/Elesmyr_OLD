package net.halitesoft.lote.world.entity;

import com.esotericsoftware.kryonet.Connection;
import net.halitesoft.lote.msgsys.Message;
import net.halitesoft.lote.msgsys.MessageReceiver;
import net.halitesoft.lote.player.Camera;
import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.system.GameServer;
import net.halitesoft.lote.world.Region;
import net.halitesoft.lote.world.item.Item;
import net.halitesoft.lote.world.item.ItemFactory;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class EntityItem extends Entity {
Image spr;
Item item;
int destTimer=-1;
private EntityPlayer targetEP;

@Override
public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver)
		throws SlickException {
	this.item=ItemFactory.getItem(extd.split(",", 2)[0]);
	spr=item.spr;
}

@Override
public void initSERV() {
	this.item=ItemFactory.getItem(extd.split(",", 2)[0]);
}

@Override
public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
                   Camera cam, GameClient receiver) throws SlickException {
	if (spr==null)
		init(gc, sbg, receiver);
	spr.draw(xs+cam.getXOff(), ys+cam.getYOff());
}

@Override
public void update(Region region, GameServer receiver) {
	if (destTimer>0) {
		destTimer--;
		this.x=targetEP.x-16;
		this.y=targetEP.y-16;
	} else if (destTimer==0)
		region.receiveMessage(new Message(region.name+".killSERV", this.name), receiver);
}

@Override
public void interact(Region region, EntityPlayer entityPlayer, MessageReceiver receiver, Connection connection) {
	if (destTimer==-1)
		if (entityPlayer.putItem(item, extd.split(",", 2)[1])) {
			destTimer=40;
			this.targetEP=entityPlayer;
			constantUpdate=true;
		}
}
}
