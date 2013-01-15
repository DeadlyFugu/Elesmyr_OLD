package net.shard.lote.world.entity;

import java.util.ArrayList;
import java.util.Random;


import net.shard.lote.Message;
import net.shard.lote.MessageReceiver;
import net.shard.lote.system.Camera;
import net.shard.lote.system.GameClient;
import net.shard.lote.system.GameServer;
import net.shard.lote.world.Region;
import net.shard.lote.world.World;
import net.shard.lote.world.item.Item;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class EntityEnemy extends Entity {

	Image spr;
	protected int xmove,ymove;
	protected boolean moveFree = true;
	protected int xtarget,ytarget;
	protected int mdist,cmdist; //mdist = distance to move in tiles, cmdist = distance moved so far in pixels
	protected int health = 0;
	protected Random airand = new Random();
	
	public EntityEnemy() {
		constantUpdate = true;
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver)
			throws SlickException {
		spr = new Image("data/ent/enemy.png",false,0);
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
			Camera cam, GameClient receiver) throws SlickException {
		if (spr==null)
			init(gc,sbg,receiver);
		//spr.draw(x+cam.getXOff(),y+cam.getYOff());
		spr.draw(xs+cam.getXOff(),ys+cam.getYOff());
	}

	@Override
	public void update(Region region, GameServer receiver) {
		if (region.aiPlaceFreeRect(x+xmove-10,y-4,x+xmove+10,y+4))
			x+=xmove;
		else
			cmdist=mdist*32;
		if (region.aiPlaceFreeRect(x-10,y+ymove-4,x+10,y+ymove+4)) 
			y+=ymove;
		else
			cmdist=mdist*32;
		
		if (moveFree) {
			cmdist++;
			if (cmdist>mdist*32) {
				 //use airand to change direction and mdist
				int dir = airand.nextInt(4);
				xmove=ymove=0;
				if (dir==0)
					xmove=1;
				else if (dir==1)
					xmove=-1;
				else if (dir==2)
					ymove=1;
				else if (dir==3)
					ymove=-1;
				mdist=airand.nextInt(3)+3;
				cmdist = 0;
			}
		}
		//if (x>800)
		//	move=-1;
		//if (x<100)
		//	move=1;
	}
	
	@Override
	public void hurt(Region region, int damage, MessageReceiver receiver) {
		this.health-=damage;
		if (health<=0) {
			this.drop(region);
			region.receiveMessage(new Message(region.name+".killSERV",this.name), receiver );
		}
	}
}
