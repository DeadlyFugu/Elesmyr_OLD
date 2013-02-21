package net.halitesoft.lote.world.entity;

import java.util.ArrayList;
import java.util.Random;


import net.halitesoft.lote.msgsys.MessageReceiver;
import net.halitesoft.lote.player.Camera;
import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.system.Main;
import net.halitesoft.lote.world.item.Item;
import net.halitesoft.lote.world.item.ItemFactory;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

public class EnemyMushroom extends EntityEnemy {
	SpriteSheet spr;
	float anim = 0;
	
	public EnemyMushroom() {
		cx1=-32;
		cx2=32;
		cy1=-32;
		cy2=16;
		maxHealth = 10;
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver)
			throws SlickException {
		spr = new SpriteSheet(new Image("data/ent/mushroom_enemy.png",false,0),32,32);
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
			Camera cam, GameClient receiver) throws SlickException {
		if (spr==null)
			init(gc,sbg,receiver);
		int danim = (int) anim;
		if (danim == 3) danim=1;
		spr.getSprite(danim+6,0).draw(xs+cam.getXOff()-16,ys+cam.getYOff()-32,32,32);
		anim+=0.1;
		if (anim>=4) {
			anim=0;
		}
		drawHealthBar(xs+cam.getXOff(),ys+cam.getYOff()-34,g);
	}
	
	@Override
	protected ArrayList<String> getDrops() {
		ArrayList<String> ret = new ArrayList<String>();
		for (int i=0;i<airand.nextInt(5)+1;i++)
			if (airand.nextBoolean())
				ret.add("Fish,");
			else
				ret.add("Potato,");
		return ret;
	}
}
