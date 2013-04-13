package net.sekien.lote.world;

import com.esotericsoftware.minlog.Log;
import net.sekien.hbt.HBTTag;
import net.sekien.lote.GameElement;
import net.sekien.lote.Save;
import net.sekien.lote.lighting.Light;
import net.sekien.lote.msgsys.Connection;
import net.sekien.lote.msgsys.Message;
import net.sekien.lote.msgsys.MessageReceiver;
import net.sekien.lote.msgsys.MessageSystem;
import net.sekien.lote.player.Camera;
import net.sekien.lote.player.PlayerData;
import net.sekien.lote.system.GameClient;
import net.sekien.lote.system.GameServer;
import net.sekien.lote.system.Globals;
import net.sekien.lote.system.Main;
import net.sekien.lote.util.FileHandler;
import net.sekien.lote.util.ResourceType;
import net.sekien.lote.world.entity.*;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Region implements GameElement {
public String name;
public ConcurrentHashMap<Integer, Entity> entities;
public ArrayList<Connection> connections;
public TiledMap map;
public int mapColLayer=0; //Layer containing collision tiles
public int mapColTOff=0; //Offset of first collision tile
private int sendEntities=20;
public WeatherCondition weather=new WeatherCondition();

public Region(String name) {
	this.name=name;
	entities=new ConcurrentHashMap<Integer, Entity>();
	connections=new ArrayList<Connection>();
}

public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver) throws SlickException {
	try {
		map=new TiledMap(FileHandler.parse("region."+name, ResourceType.MAP));
		mapColLayer=map.getLayerIndex("col");
		int tsid=Integer.parseInt(map.getLayerProperty(mapColLayer, "tileset", "0"));
		mapColTOff=map.getTileSet(tsid).firstGID;
	} catch (SlickException e) {
		e.printStackTrace();
	}
}

public void render(GameContainer gc, StateBasedGame sbg, Graphics g, Camera cam, GameClient receiver) throws SlickException {
	//Map is rendered in GameplayState
	List<Entity> list=new ArrayList<Entity>(entities.values());
	try {
		Collections.sort(list); //TODO: Occasionally throws IllegalArgumentException: Comparison methood violates its general contract! May be fixed? 99% sure it's fixed.
	} catch (Exception e) {
		e.printStackTrace();
	}
	for (Entity e : list) {
		e.render(gc, sbg, g, cam, receiver);
	}
}

@Override
public void load(Save save) {
	//if (save.get("region."+name)!=null)
	//	this.parseEntityStringGenIDs(save.get("region."+name), false);
	fromHBT(save.getCompound("world."+name));
}

@Override
public void save(Save save) {
	save.clearTag("world."+name);
	save.putTag("world."+name, this.toHBTSave());
	String ret="";
	int id=0;
	for (Entity e : entities.values()) {
		if (e instanceof EntityPlayer) {
			EntityPlayer p=(EntityPlayer) e;
			save.putPlayer(p.getName(), name+"."+p.name, this);
		} else {
			e.save(save); //TODO: remove
			String[] parts=e.toString().split(",", 3);
			ret=ret+"\\"+parts[0]+","+parts[2];
			id++;
		}
	}
	/*if (!ret.equals(""))
		save.put("region."+name, ret.substring(1));
	else
		save.put("region."+name, ret);*/
}

@Override
public void fromHBT(net.sekien.hbt.HBTCompound tag) {
	for (HBTTag sub : tag) {
		if (sub instanceof net.sekien.hbt.HBTCompound) {
			addEntityServer((net.sekien.hbt.HBTCompound) sub);
		}
	}
}

public net.sekien.hbt.HBTCompound toHBTSave() {
	net.sekien.hbt.HBTCompound ret=new net.sekien.hbt.HBTCompound(name);
	for (Entity e : entities.values()) {
		if (!(e instanceof EntityPlayer)) //Don't save EntityPlayers. They break things.
			ret.addTag(e.toHBT());
	}
	return ret;
}

@Override
public net.sekien.hbt.HBTCompound toHBT() {
	net.sekien.hbt.HBTCompound ret=new net.sekien.hbt.HBTCompound(name);
	for (Entity e : entities.values()) {
		ret.addTag(e.toHBT());
	}
	return ret;
}

@Override
public void update(Region region, GameServer receiver) {
	if (sendEntities==0) {
		//sendEntities=Math.max(1,entities.size()/50);
		sendEntities=5;
	} else {
		sendEntities--;
	}
	for (Entity e : entities.values()) {
		e.update(this, receiver);
		if ((e instanceof EntityPlayer)||(sendEntities==1&&e.constantUpdate)) {
			MessageSystem.sendClient(this, (ArrayList<Connection>) connections.clone(), new Message(name+"."+e.name+".move", e.x+","+e.y), true);
		}
	}
}

@Override
public void clientUpdate(GameContainer gc, StateBasedGame sbg, GameClient receiver) {
	for (Entity e : entities.values()) {
		e.clientUpdate(gc, sbg, receiver);
	}
}

@Override
public void receiveMessage(Message msg, MessageReceiver receiver) {
	if (msg.getTarget().equals(name)) {
		if (msg.getName().equals("addEnt")) {
			addEntity(msg.getDataStr(), true);
		} else if (msg.getName().equals("addEntSERV")) {
			if (addEntityServer(msg.getDataStr())==-1) {
				msg.reply("CLIENT.chat", "ERROR: addEntityServer failed for input string:", this);
				msg.reply("CLIENT.chat", "       "+msg.getDataStr(), this);
			}
		} else if (msg.getName().equals("killSERV")) {
			entities.remove(Integer.parseInt(msg.getDataStr()));
			MessageSystem.sendClient(this, connections, new Message(name+".kill", msg.getDataStr()), false);
		} else if (msg.getName().equals("kill")) {
			try {
				entities.get(Integer.parseInt(msg.getDataStr())).kill((GameClient) receiver);
				entities.remove(Integer.parseInt(msg.getDataStr()));
			} catch (Exception e) {
				Log.info("Client could not kill "+msg.getDataStr());
			}
		} else if (msg.getName().equals("hitAt")) {
			EntityPlayer ep=((GameServer) receiver).getPlayerEnt(msg.getConnection());
			PlayerData.InventoryEntry ie=ep.pdat.getEquipped();
			if (ie!=null)
				if (ie.getItem().onUse((GameServer) receiver, ep, ie))
					ep.pdat.removeItem(ep.pdat.inventory.indexOf(ie), ep.region, ep.getReceiverName());
			for (Entity e : getEntitiesAt(Integer.parseInt(msg.getDataStr().split(",", 2)[0]), Integer.parseInt(msg.getDataStr().split(",", 2)[1]))) {
				if (e!=ep||(Globals.get("debug", false)&&Globals.get("selfHit", true)))
					e.hurt(this, ep, receiver);
			}
		} else if (msg.getName().equals("intAt")) {
			for (Entity e : getEntitiesAt(Integer.parseInt(msg.getDataStr().split(",", 2)[0]), Integer.parseInt(msg.getDataStr().split(",", 2)[1]))) {
				e.interact(this, ((GameServer) receiver).getPlayerEnt(msg.getConnection()), receiver, msg);
			}
		} else if (msg.getName().equals("pickupAt")) {
			for (Entity e : getEntitiesAt(Integer.parseInt(msg.getDataStr().split(",", 2)[0]), Integer.parseInt(msg.getDataStr().split(",", 2)[1]))) {
				if (e instanceof EntityItem)
					e.interact(this, ((GameServer) receiver).getPlayerEnt(msg.getConnection()), receiver, msg);
			}
		}
	} else if (entities.containsKey(Integer.valueOf(msg.getTarget().split("\\.", 2)[1]))) {
		entities.get(Integer.valueOf(msg.getTarget().split("\\.", 2)[1])).receiveMessage(msg, receiver);
	} else {
		Log.warn("World."+name+": Ignored message - unrecognised target: "+msg.toString());
	}
}

private ArrayList<Entity> getEntitiesAt(int x, int y) {
	ArrayList<Entity> ret=new ArrayList<Entity>();
	for (Entity e : entities.values())
		if (e.collidesWith(x, y))
			ret.add(e);
	return ret;
}

public String getEntityString() {
	String ret="";
	for (Entity e : entities.values()) {
		ret=ret+"\\"+e.toString();
	}
	if (!ret.equals(""))
		return ret.substring(1);
	else
		return ret;
}

public void parseEntityString(String str, boolean client) {
	String[] ents=str.split("\\\\");
	for (String s : ents) {
		addEntity(s, client);
	}
}

public void parseEntityStringGenIDs(String str, boolean client) {
	String[] ents=str.split("\\\\");
	int id=0;
	for (String s : ents) {
		if (s.contains(","))
			addEntity(s.split(",", 2)[0]+","+id+","+s.split(",", 2)[1], client);
		id++;
	}
}

/**
 * Adds an entity to this region, with a given ID
 *
 * @param data
 * 		String containing entity info, formatted the same as Entity.toString();
 */
public void addEntity(String data, boolean client) {
	if (data.split(",", 5).length==5) {
		Entity ent=EntityFactory.getEntity(data, this);
		if (ent!=null) {
			entities.put(Integer.valueOf(data.split(",")[1]), ent);
			if (client)
				MessageSystem.registerReceiverClient(ent);
			else
				MessageSystem.registerReceiverServer(ent);
		}
	} else
		Log.info("Ignored invalid entity '"+data+"'");
}

/**
 * Adds an entity to this region, dynamically creating a new ID.
 *
 * @param data
 * 		String containing entity info, without the ID.
 * @return ID of the new entity. -1 if creation failed.
 */
public int addEntityServer(String data) {
	int idmax=0;
	for (Integer name : entities.keySet())
		if (name>idmax) idmax=name;
	//for (Connection c : connections)
	//	c.sendTCP(new Message(name+".addEnt",data.split(",",2)[0]+","+(idmax+1)+","+data.split(",",2)[1]));
	MessageSystem.sendClient(this, connections, new Message(name+".addEnt", data.split(",", 2)[0]+","+(idmax+1)+","+data.split(",", 2)[1]), false);
	Entity ent=EntityFactory.getEntity(data.split(",", 2)[0]+","+(idmax+1)+","+data.split(",", 2)[1], this);
	if (ent!=null) {
		entities.put(idmax+1, ent);
		MessageSystem.registerReceiverServer(ent);
		return idmax+1;
	} else
		return -1;
}

public int addEntityServer(net.sekien.hbt.HBTCompound data) {
	int idmax=0;
	for (Integer name : entities.keySet())
		if (name>idmax) idmax=name;
	//for (Connection c : connections)
	//	c.sendTCP(new Message(name+".addEnt",data.split(",",2)[0]+","+(idmax+1)+","+data.split(",",2)[1]));
	MessageSystem.sendClient(this, connections, new Message(name+".addEnt", data.getString("class", "Entity")+","+(idmax+1)+","+data.getInt("x", 0)+","+data.getInt("y", 0)+","+data.getString("extd", "")), false);
	Entity ent=EntityFactory.getEntity(data.getString("class", "Entity")+","+(idmax+1)+","+data.getInt("x", 0)+","+data.getInt("y", 0)+","+data.getString("extd", ""), this);
	if (ent!=null) {
		entities.put(idmax+1, ent);
		MessageSystem.registerReceiverServer(ent);
		return idmax+1;
	} else
		return -1;
}

public boolean aiPlaceFree(int x, int y) {
	try {
		return (map.getTileId(x/32, y/32, mapColLayer)==0);
	} catch (ArrayIndexOutOfBoundsException e) {
	}
	return true;
}

public boolean aiPlaceFreeRect(int x, int y, int x2, int y2) {
	return (aiPlaceFree(x, y)&&aiPlaceFree(x2, y)&&aiPlaceFree(x2, y2)&&aiPlaceFree(x, y2));
}

public ArrayList<Light> getLights() {
	ArrayList<Light> ret=new ArrayList<Light>();
	File file=new File(FileHandler.parse("region."+name, ResourceType.LIGHTMAP));
	try {
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String l;
		while ((l=br.readLine())!=null) {
			ret.add(new Light(l));
		}
		br.close();
	} catch (Exception e) {
		Main.handleCrash(e);
		System.exit(1);
	}
	return ret;
}

@Override
public String getReceiverName() {
	return name;
}

public void entHitAt(EntityEnemy ent, int xdist, int ydist, GameServer receiver) {
	for (Entity e : getEntitiesAt(ent.x, ent.y)) {
		if (e instanceof EntityPlayer)
			e.hurt(this, ent, receiver);
	}
}
}
