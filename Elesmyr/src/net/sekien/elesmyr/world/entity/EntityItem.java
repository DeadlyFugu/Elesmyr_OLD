package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.lighting.Light;
import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.world.Region;
import net.sekien.elesmyr.world.item.Item;
import net.sekien.elesmyr.world.item.ItemFactory;
import net.sekien.elesmyr.world.item.ItemTorch;
import net.sekien.hbt.HBTTools;
import org.newdawn.slick.*;

public class EntityItem extends Entity {
Image spr;
Item item;
int destTimer = -1;
private EntityPlayer targetEP;
private Light light = null;

@Override
public void init(GameContainer gc, MessageEndPoint receiver)
		throws SlickException {
	this.item = ItemFactory.getItem(inst_dat.getString("ie.n", ""));
	spr = item.spr;
	if (item instanceof ItemTorch) {
		light = new Light(600, 550, 256, 0.8f, 0.5f, 0.2f, 0.4f); //TORCH LIGHT
		((GameClient) receiver).lm.addLight(light);
	}
}

@Override
public void kill(GameClient gs) {
	if (light!=null)
		gs.lm.removeLight(light);
}

@Override
public void initSERV() {
	this.item = ItemFactory.getItem(inst_dat.getString("ie.n", ""));
}

@Override
public void render(GameContainer gc, Graphics g,
                   Camera cam, GameClient receiver) throws SlickException {
	if (spr==null)
		init(gc, receiver);
	if (light!=null)
		light.move((int) xs+16, (int) ys+16);
	spr.draw(xs+cam.getXOff(), ys+cam.getYOff());
}

@Override
public void update(Region region, GameServer receiver) {
	if (destTimer > 0) {
		destTimer--;
		this.x = targetEP.x-16;
		this.y = targetEP.y-16;
	} else if (destTimer==0)
		region.receiveMessage(new Message(region.name+".killSERV", HBTTools.msgString("ent", ""+this.id)), receiver);
}

@Override
public void interact(Region region, EntityPlayer entityPlayer, MessageEndPoint receiver, Message msg) {
	if (destTimer==-1)
		if (entityPlayer.putItem(item, inst_dat.getCompound("ie"))) {
			destTimer = 40;
			this.targetEP = entityPlayer;
			constantUpdate = true;
		}
}
}
