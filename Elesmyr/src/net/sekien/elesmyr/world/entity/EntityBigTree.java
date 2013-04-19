package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.util.FileHandler;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class EntityBigTree extends Entity {

Image spr;

@Override
public void init(GameContainer gc, StateBasedGame sbg, MessageEndPoint receiver)
		throws SlickException {
	spr = FileHandler.getImage("ent.bigtree");
}

@Override
public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
                   Camera cam, GameClient receiver) throws SlickException {
	if (spr==null)
		init(gc, sbg, receiver);
	//spr.draw(x+cam.getXOff(),y+cam.getYOff());
	spr.draw(xs+cam.getXOff()-96, ys+cam.getYOff()-190);
}
}
