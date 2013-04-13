package net.sekien.lote.world.entity;

import net.sekien.lote.lighting.Light;
import net.sekien.lote.msgsys.Message;
import net.sekien.lote.msgsys.MessageReceiver;
import net.sekien.lote.player.Camera;
import net.sekien.lote.system.GameClient;
import net.sekien.lote.system.GameServer;
import net.sekien.lote.system.Main;
import net.sekien.lote.world.Region;
import net.sekien.lote.world.item.Item;
import net.sekien.lote.world.item.ItemFactory;
import net.sekien.lote.world.item.ItemTorch;
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
private Light light=null;

@Override
public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver)
		throws SlickException {
	this.item=ItemFactory.getItem(extd.split(",", 2)[0]);
	spr=item.spr;
	if (item instanceof ItemTorch) {
		light=new Light(600, 550, 256, 0.8f, 0.5f, 0.2f, 0.4f); //TORCH LIGHT
		((GameClient) sbg.getState(Main.GAMEPLAYSTATE)).lm.addLight(light);
	}
}

@Override
public void kill(GameClient gs) {
	if (light!=null)
		gs.lm.removeLight(light);
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
	if (light!=null)
		light.move((int) xs+16, (int) ys+16);
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
public void interact(Region region, EntityPlayer entityPlayer, MessageReceiver receiver, Message msg) {
	if (destTimer==-1)
		if (entityPlayer.putItem(item, extd.split(",", 2)[1])) {
			destTimer=40;
			this.targetEP=entityPlayer;
			constantUpdate=true;
		}
}
}
