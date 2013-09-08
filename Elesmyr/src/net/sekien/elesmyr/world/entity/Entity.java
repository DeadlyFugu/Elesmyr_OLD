/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.entity;

import com.esotericsoftware.minlog.Log;
import net.sekien.elesmyr.Element;
import net.sekien.elesmyr.GameElement;
import net.sekien.elesmyr.Save;
import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.player.InventoryEntry;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.world.Region;
import net.sekien.hbt.*;
import org.newdawn.slick.*;

import java.util.ArrayList;
import java.util.Random;

public class Entity implements GameElement, Comparable<Entity> {
public int id;
public String receiverName; //TODO: Make name into an int
public int x, y; //Actual location according to server
protected int cx1 = 0, cy1 = 0, cx2 = 32, cy2 = 32;
public float xs, ys; //Smoothed
public boolean constantUpdate = false;
public boolean tellClient = true;
public Region region;
protected HBTCompound inst_dat;
private MessageEndPoint msgreceiver;

/**
 * 'Constructor' for entity. Used because I suck at reflection and can't figure out how to pass arguments to a real
 * constructor.
 */
public Entity ctor(int id, int x, int y, HBTCompound tag, String receiverName, Region region, MessageEndPoint receiver) {
	this.id = id;
	this.receiverName = receiverName;
	xs = this.x = x;
	ys = this.y = y;
	this.region = region;
	this.inst_dat = tag;
	if (receiver instanceof GameServer) {
		initSERV((GameServer) receiver, region);
	}
	this.msgreceiver = receiver;
	return this;
}

protected void initSERV(GameServer server, Region region) {
}

@Override
public void init(GameContainer gc, MessageEndPoint receiver)
		throws SlickException {
}

@Override
public void load(Save save) {
}

@Override
public void render(GameContainer gc, Graphics g,
                   Camera cam, GameClient receiver) throws SlickException {
}

@Override
public void update(Region region, GameServer receiver) {
}

@Override
public void clientUpdate(GameContainer gc, GameClient receiver) {
	xs += (x-xs)/10;
	ys += (y-ys)/10;
}

@Override
public void receiveMessage(Message msg, MessageEndPoint receiver) {
	String name = msg.getName();
	HBTCompound data = msg.getData();
	if (name.equals("move")) {
		this.x = data.getInt("x", 0);
		this.y = data.getInt("y", 0);
	} else if (name.equals("toString")) {
		msg.reply("CLIENT.chat", HBTTools.msgString("msg", "ENT."+name+": "+this.toString()), this);
	} else {
		receiveMessageExt(msg, receiver);
	}
}

void receiveMessageExt(Message msg, MessageEndPoint receiver) {
	Log.warn("ENTITY: Ignored message "+msg.toString());
}

@Override
public void save(Save save) {
}

@Override
public void fromHBT(HBTCompound tag) {
	x = tag.getInt("x", 0);
	y = tag.getInt("y", 0);
	inst_dat = tag;
	//if (!tag.hasTag("name")) {} else {this.name = tag.getInt("name", "ERROR");} //MAY ERROR (ERROR BEFORE BECAUSE tag.getString)
	//extd = tag.getString("extd", "");
	//loadExtd(tag);
}

@Override
public HBTCompound toHBT(boolean msg) {
	HBTCompound ret = (HBTCompound) inst_dat.deepClone();
	ret.setTag(new HBTString("class", this.getClass().getName().substring("net.sekien.elesmyr.world.entity.".length())));
	ret.setTag(new HBTInt("x", x));
	ret.setTag(new HBTInt("y", y));
	ret.setName(String.valueOf(id));
	return ret;
}

@Deprecated protected String gEXTD() {
	return inst_dat.getString("extd", "ERROR NO EXTD TAG");
}

@Override
public String toString() {
	return this.getClass().getName().substring("net.sekien.elesmyr.world.entity.".length())+","+id+","+x+","+y+","+inst_dat.toString();
}

@Override
public int compareTo(Entity other) {
	int thisy = (int) ys;
	if (this instanceof EntityPlayer && ((EntityPlayer) this).isUser==true)
		thisy = ((EntityPlayer) this).cy;
	int othery = (int) other.ys;
	if (other instanceof EntityPlayer && ((EntityPlayer) other).isUser==true)
		othery = ((EntityPlayer) other).cy;
	if (thisy < othery)
		return -1;
	else if (thisy > othery)
		return 1;
	else if (this.id > other.id)
		return 1;
	return -1;
}

/** Client-side kill code. Use to remove lights etc. */
public void kill(GameClient gs) {
}

public boolean collidesWith(int x, int y) {
	return (this.x+cx1 < x && this.x+cx2 > x && this.y+cy1 < y && this.y+cy2 > y);
}

/**
 * Make an entity take damage
 *
 * @param region
 * 		Region the entity is in
 * @param entity
 * 		Entity that attacked
 * @return True if entity needs to be destroyed.
 */
public void hurt(Region region, Entity entity, MessageEndPoint receiver) {
}

/**
 * Called when a player interacts with this entity
 *
 * @param region
 * 		Region the entity is in
 */
public void interact(Region region, EntityPlayer entityPlayer, MessageEndPoint receiver, Message msg) {
}

protected ArrayList<HBTCompound> getDrops() {
	return new ArrayList<HBTCompound>();
}

protected void drop(Region region) {
	Random rand = new Random();
	for (HBTCompound ie : this.getDrops()) {
		ie.setName("ie");
		//region.addEntityServer("EntityItem,"+(x-rand.nextInt(32))+","+(y-rand.nextInt(32))+","+i);
		region.addEntityServer(new HBTCompound("item_ent_dat", new HBTTag[]{
				                                                                   new HBTString("class", "EntityItem"),
				                                                                   new HBTInt("x", x-rand.nextInt(32)),
				                                                                   new HBTInt("y", y-rand.nextInt(32)),
				                                                                   ie
		}), msgreceiver);
	}
}

@Override
public String getReceiverName() {
	return receiverName;
}

/** Returns this. Used for scripting purposes */
public Entity toEntity() {
	return this;
}

public Element getElement() { return Element.NEUTRAL; }

public InventoryEntry getEquipped() {
	return null;
}

public void killserv() {}
}
