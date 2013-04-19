package net.sekien.elesmyr.world.entity;

import com.esotericsoftware.minlog.Log;
import net.sekien.elesmyr.Element;
import net.sekien.elesmyr.GameElement;
import net.sekien.elesmyr.Save;
import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.player.PlayerData;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.world.Region;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTInt;
import net.sekien.hbt.HBTString;
import net.sekien.hbt.HBTTools;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;
import java.util.Random;

public class Entity implements GameElement, Comparable<Entity> {
public String name, extd, receiverName; //TODO: Make name into an int
public int x, y; //Actual location according to server
private int cheese = -12;
protected int cx1 = 0, cy1 = 0, cx2 = 32, cy2 = 32;
public float xs, ys; //Smoothed
public boolean constantUpdate = false;
public boolean tellClient = true;
public Region region;

/**
 * 'Constructor' for entity. Used because I suck at reflection and can't figure out how to pass arguments to a real
 * constructor.
 */
public Entity ctor(String name, int x, int y, String extd, String receiverName, Region region) {
	this.name = name;
	this.receiverName = receiverName;
	xs = this.x = x;
	ys = this.y = y;
	this.region = region;
	this.extd = extd;
	this.initSERV();
	return this;
}

protected void initSERV() {
}

@Override
public void init(GameContainer gc, StateBasedGame sbg, MessageEndPoint receiver)
		throws SlickException {
}

@Override
public void load(Save save) {
}

@Override
public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
                   Camera cam, GameClient receiver) throws SlickException {
}

@Override
public void update(Region region, GameServer receiver) {
}

@Override
public void clientUpdate(GameContainer gc, StateBasedGame sbg, GameClient receiver) {
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
		this.cheese = x;
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
	//if (!tag.hasTag("name")) {} else {this.name = tag.getInt("name", "ERROR");} //MAY ERROR (ERROR BEFORE BECAUSE tag.getString)
	extd = tag.getString("extd", "");
}

@Override
public HBTCompound toHBT(boolean msg) {
	HBTCompound ret = new HBTCompound(name);
	ret.addTag(new HBTString("class", this.getClass().getName().substring("net.sekien.elesmyr.world.entity.".length())));
	ret.addTag(new HBTInt("x", x));
	ret.addTag(new HBTInt("y", y));
	if (msg)
		ret.addTag(new HBTInt("name", Integer.parseInt(name)));
	ret.addTag(new HBTString("extd", extd));
	return ret;
}

@Override
public String toString() {
	return this.getClass().getName().substring("net.sekien.elesmyr.world.entity.".length())+","+name+","+x+","+y+","+extd;
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
	else if (Integer.parseInt(this.name) > Integer.parseInt(other.name))
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

protected ArrayList<String> getDrops() {
	return new ArrayList<String>();
}

protected void drop(Region region) {
	Random rand = new Random();
	for (String i : this.getDrops()) {
		region.addEntityServer("EntityItem,"+(x-rand.nextInt(32))+","+(y-rand.nextInt(32))+","+i);
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

public PlayerData.InventoryEntry getEquipped() {
	return null;
}
}
