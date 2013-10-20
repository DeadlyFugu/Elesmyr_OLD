/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.entity;

import com.esotericsoftware.minlog.Log;
import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.msgsys.MessageSystem;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.elesmyr.util.HintHelper;
import net.sekien.elesmyr.world.Region;
import net.sekien.elesmyr.world.item.Item;
import net.sekien.elesmyr.world.item.ItemWeapon;
import net.sekien.hbt.HBTInt;
import net.sekien.hbt.HBTTools;
import org.newdawn.slick.*;

import java.util.Random;

public class EntityEnemy extends Entity {

	Image spr;
	protected int xmove, ymove;
	protected boolean moveFree = true;
	protected int xtarget, ytarget;
	protected int mdist, cmdist; //mdist = distance to move in tiles, cmdist = distance moved so far in pixels
	protected int health = 0;
	protected int maxHealth = 0;
	protected Random airand = new Random();
	private int nextHit = 10;

	public EntityEnemy() {
		constantUpdate = true;
	}

	@Override
	public void initSERV(GameServer server, Region region) {
		health = inst_dat.getInt("health", -1);
		if (health == -1) {
			if (inst_dat.hasTag("extd")) {
				health = Integer.parseInt(inst_dat.getString("extd", "0"));
				inst_dat.deleteTag("extd");
				inst_dat.addTag(new HBTInt("health", health));
			} else {
				health = maxHealth;
			}
		}
	}

	@Override
	public void init(GameContainer gc, MessageEndPoint receiver)
	throws SlickException {
		spr = FileHandler.getImage("ent.enemy");
	}

	@Override
	public void render(GameContainer gc, Graphics g,
	                   Camera cam, GameClient receiver) throws SlickException {
		HintHelper.attack(this, g, receiver, cam, 8, -16, 0, 0);
		if (spr == null)
			init(gc, receiver);
		//spr.draw(x+cam.getXOff(),y+cam.getYOff());
		spr.draw(xs+cam.getXOff(), ys+cam.getYOff());
		drawHealthBar(xs+cam.getXOff(), ys+cam.getYOff(), g);
	}

	protected void drawHealthBar(float x, float y, Graphics g) {
		if (health != maxHealth) {
			g.setColor(Color.lightGray);
			g.fillRect(x-16, y-4, 32, 4);
			g.setColor(Color.red);
			g.fillRect(x-16, y-4, ((float) health/maxHealth)*32, 4);
			g.setColor(Color.white);
		}
	}

	@Override
	public void update(Region region, GameServer receiver) {
		if (region.aiPlaceFreeRect(x+xmove-10, y-4, x+xmove+10, y+4))
			x += xmove;
		else
			cmdist = mdist*32;
		if (region.aiPlaceFreeRect(x-10, y+ymove-4, x+10, y+ymove+4))
			y += ymove;
		else
			cmdist = mdist*32;

		if (moveFree) {
			cmdist++;
			if (cmdist > mdist*32) {
				//use airand to change direction and mdist
				int dir = airand.nextInt(4);
				xmove = ymove = 0;
				if (dir == 0)
					xmove = 1;
				else if (dir == 1)
					xmove = -1;
				else if (dir == 2)
					ymove = 1;
				else if (dir == 3)
					ymove = -1;
				mdist = airand.nextInt(3)+3;
				cmdist = 0;
			}
		}
		if (nextHit == 0) {
			attack(region, receiver);
			nextHit = airand.nextInt(6)+6;
		} else {
			nextHit--;
		}
		//if (x>800)
		//	move=-1;
		//if (x<100)
		//	move=1;
	}

	private void attack(Region region, GameServer receiver) {
		region.entHitAt(this, xmove*32, ymove*32, receiver);
	}

	@Override
	public void hurt(Region region, Entity entity, MessageEndPoint receiver) {
		float dmg = 1;
		if (entity.getEquipped() != null) {
			Item i = entity.getEquipped().getItem();
			if (i instanceof ItemWeapon)
				dmg = ((ItemWeapon) i).getMult(entity.getEquipped());
			dmg *= i.getElement().multAgainst(this.getElement())*entity.getElement().multAgainst(this.getElement());
		}
		health -= dmg;
		if (health <= 0) {
			this.drop(region);
			region.receiveMessage(new Message(region.name+".killSERV", HBTTools.msgString("ent", ""+this.id)), receiver); //TODO: Merge all killSEV:<this> into one method in Entity
			if (entity instanceof EntityPlayer) {
				((EntityPlayer) entity).gainExp(getExp());
			}
		} else {
			MessageSystem.sendClient(this, region.connections, new Message(this.getReceiverName()+".setHealth", HBTTools.msgInt("health", health)), false);
		}
		inst_dat.setTag(new HBTInt("health", health));
	}

	@Override
	public void receiveMessageExt(Message msg, MessageEndPoint receiver) {
		if (msg.getName().equals("setHealth")) {
			this.health = msg.getData().getInt("health", 0);
		} else {
			Log.warn("ENTITY: Ignored message "+msg.toString());
		}
	}

	public int getExp() {
		return 5;
	}
}
