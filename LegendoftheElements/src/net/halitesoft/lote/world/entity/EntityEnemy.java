package net.halitesoft.lote.world.entity;

import java.util.ArrayList;
import java.util.Random;


import net.halitesoft.lote.msgsys.Message;
import net.halitesoft.lote.msgsys.MessageReceiver;
import net.halitesoft.lote.msgsys.MessageSystem;
import net.halitesoft.lote.player.Camera;
import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.system.GameServer;
import net.halitesoft.lote.system.Main;
import net.halitesoft.lote.world.Region;
import net.halitesoft.lote.world.World;
import net.halitesoft.lote.world.item.Item;
import net.halitesoft.lote.world.item.ItemWeapon;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.minlog.Log;

public class EntityEnemy extends Entity {

	Image spr;
	protected int xmove,ymove;
	protected boolean moveFree = true;
	protected int xtarget,ytarget;
	protected int mdist,cmdist; //mdist = distance to move in tiles, cmdist = distance moved so far in pixels
	protected int health = 0;
	protected int maxHealth = 0;
	protected Random airand = new Random();
	
	public EntityEnemy() {
		constantUpdate = true;
	}
	
	@Override
	public void initSERV() {
		health = Integer.parseInt(extd.split(",",2)[0]);
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
		drawHealthBar(xs+cam.getXOff(),ys+cam.getYOff(),g);
	}

	protected void drawHealthBar(float x, float y, Graphics g) {
		if (health!=maxHealth) {
			g.setColor(Color.lightGray);
			g.fillRect(x-16, y-4, 32, 4);
			g.setColor(Color.red);
			g.fillRect(x-16, y-4, ((float) health/maxHealth)*32, 4);
			g.setColor(Color.white);
		}
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
	public void hurt(Region region, Entity entity, MessageReceiver receiver) {
		float dmg = 1;
		if (entity.getEquipped()!=null) {
			Item i = entity.getEquipped().getItem();
			if (i instanceof ItemWeapon)
				dmg=((ItemWeapon) i).getMult(entity.getEquipped().getExtd());
			dmg*=i.getElement().multAgainst(this.getElement());
		}
		health-=dmg;
		if (health<=0) {
			this.drop(region);
			region.receiveMessage(new Message(region.name+".killSERV",this.name), receiver );
		} else {
			MessageSystem.sendClient(this, region.connections, new Message(this.getReceiverName()+".setHealth",""+health), false);
		}
		if (extd.contains(","))
			extd = health+","+extd.split(",",2)[1];
		else
			extd = ""+health;
	}
	
	@Override public void receiveMessageExt(Message msg, MessageReceiver receiver) {
		if (msg.getName().equals("setHealth")) {
			this.health=Integer.parseInt(msg.getData());
		} else {
			Log.warn("ENTITY: Ignored message "+msg.toString());
		}
	}
}
