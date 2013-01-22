package net.halitesoft.lote.world.entity;

import java.util.ArrayList;
import java.util.Random;

import net.halitesoft.lote.Element;
import net.halitesoft.lote.GameElement;
import net.halitesoft.lote.Message;
import net.halitesoft.lote.MessageReceiver;
import net.halitesoft.lote.Save;
import net.halitesoft.lote.system.Camera;
import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.system.GameServer;
import net.halitesoft.lote.system.PlayerData.InventoryEntry;
import net.halitesoft.lote.world.Region;
import net.halitesoft.lote.world.item.Item;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.minlog.Log;


public class Entity implements GameElement, Comparable<Entity> {
	public String name,extd,receiverName;
	public int x,y; //Actual location according to server
	protected int cx1=0,cy1=0,cx2=32,cy2=32;
	public float xs,ys; //Smoothed
	public boolean constantUpdate = false;
	public boolean tellClient = true;
	public Region region;
	/**
	 * 'Constructor' for entity. Used because I suck at reflection and can't figure out how to pass arguments to a real constructor.
	 * @param name
	 * @param x
	 * @param y
	 */
	public Entity ctor(String name, int x, int y, String extd, String receiverName, Region region) {
		this.name=name;
		this.receiverName=receiverName;
		xs=this.x=x;
		ys=this.y=y;
		this.region=region;
		this.extd=extd;
		this.initSERV();
		return this;
	}
	
	protected void initSERV() {
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver)
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
	public void receiveMessage(Message msg, MessageReceiver receiver) {
		String name = msg.getName();
		String data = msg.getData();
		if (name.equals("move")) {
			this.x=Integer.parseInt(data.split(",")[0]);
			this.y=Integer.parseInt(data.split(",")[1]);
		} else if (name.equals("toString")) {
			msg.reply("CLIENT.chat", name+": "+this.toString(),this);
		} else {
			receiveMessageExt(msg, receiver);
		}
	}
	
	void receiveMessageExt(Message msg, MessageReceiver receiver) {
		Log.warn("ENTITY: Ignored message "+msg.toString());
	}
	
	@Override
	public void save(Save save) {
	}
	
	@Override
	public String toString() {
		return this.getClass().getName().substring("net.halitesoft.lote.world.entity.".length())+","+name+","+x+","+y+","+extd;
	}

	@Override
	public int compareTo(Entity other) {
		if (other instanceof EntityPlayer && ((EntityPlayer) other).isUser == true)
			if (this.ys<((EntityPlayer) other).cy)
				return -1;
			else if (this.ys>((EntityPlayer) other).cy)
				return 1;
		if (this.ys<other.ys)
			return -1;
		else if (this.ys>other.ys)
			return 1;
		else
			if (Integer.parseInt(this.name)>Integer.parseInt(other.name))
				return 1;
		return -1;
	}
	
	/**
	 * Client-side kill code. Use to remove lights etc.
	 */
	public void kill(GameClient gs) {
	}

	public boolean collidesWith(int x, int y) {
		return (this.x+cx1<x&&this.x+cx2>x&&this.y+cy1<y&&this.y+cy2>y);
	}
	
	/**
	 * Make an entity take damage
	 * @param region Region the entity is in
	 * @param entity Entity that attacked
	 * @return True if entity needs to be destroyed.
	 */
	public void hurt(Region region, Entity entity, MessageReceiver receiver) {
	}
	
	/**
	 * Called when a player interacts with this entity
	 * @param region Region the entity is in
	 * @param entityPlayer Player entity that interacted
	 */
	public void interact(Region region, EntityPlayer entityPlayer, MessageReceiver receiver) {
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
	
	public InventoryEntry getEquipped() {
		return null;
	}
}
