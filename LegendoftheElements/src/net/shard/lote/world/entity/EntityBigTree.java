package net.shard.lote.world.entity;


import net.shard.lote.MessageReceiver;
import net.shard.lote.system.Camera;
import net.shard.lote.system.GameClient;
import net.shard.lote.world.Region;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class EntityBigTree extends Entity {

	Image spr;

	@Override
	public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver)
			throws SlickException {
		spr = new Image("data/ent/bigtree.png",false,0);
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
			Camera cam, GameClient receiver) throws SlickException {
		if (spr==null)
			init(gc,sbg,receiver);
		//spr.draw(x+cam.getXOff(),y+cam.getYOff());
		spr.draw(xs+cam.getXOff()-96,ys+cam.getYOff()-190);
	}
}
