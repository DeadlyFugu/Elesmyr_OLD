package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.Element;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTString;
import net.sekien.hbt.HBTTag;
import org.newdawn.slick.*;

import java.util.ArrayList;

public class EnemyMushroom extends EntityEnemy {
SpriteSheet spr;
float anim = 0;

public EnemyMushroom() {
	cx1 = -32;
	cx2 = 32;
	cy1 = -32;
	cy2 = 16;
	maxHealth = 10;
}

@Override
public void init(GameContainer gc, MessageEndPoint receiver)
		throws SlickException {
	spr = new SpriteSheet(FileHandler.getImage("ent.mushroom_enemy"), 32, 32);
}

@Override
public void render(GameContainer gc, Graphics g,
                   Camera cam, GameClient receiver) throws SlickException {
	if (spr==null)
		init(gc, receiver);
	int danim = (int) anim;
	if (danim==3) danim = 1;
	spr.getSprite(danim+6, 0).draw(xs+cam.getXOff()-16, ys+cam.getYOff()-32, 32, 32);
	anim += 0.1;
	if (anim >= 4) {
		anim = 0;
	}
	drawHealthBar(xs+cam.getXOff(), ys+cam.getYOff()-34, g);
}

@Override
protected ArrayList<HBTCompound> getDrops() {
	ArrayList<HBTCompound> ret = new ArrayList<HBTCompound>();
	for (int i = 0; i < airand.nextInt(5)+1; i++)
		if (airand.nextBoolean())
			ret.add(new HBTCompound("ie", new HBTTag[]{
					                                          new HBTString("n", "Fish")
			}));
		else
			ret.add(new HBTCompound("ie", new HBTTag[]{
					                                          new HBTString("n", "Potato")
			}));
	return ret;
}

@Override
public Element getElement() {
	return Element.EARTH;
}
}
