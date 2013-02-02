package net.halitesoft.lote.world;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.halitesoft.lote.GameElement;
import net.halitesoft.lote.Message;
import net.halitesoft.lote.MessageReceiver;
import net.halitesoft.lote.MessageSystem;
import net.halitesoft.lote.Save;
import net.halitesoft.lote.system.Camera;
import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.system.GameServer;
import net.halitesoft.lote.system.Globals;
import net.halitesoft.lote.system.Light;
import net.halitesoft.lote.system.Main;
import net.halitesoft.lote.system.PlayerData;
import net.halitesoft.lote.world.entity.Entity;
import net.halitesoft.lote.world.entity.EntityFactory;
import net.halitesoft.lote.world.entity.EntityItem;
import net.halitesoft.lote.world.entity.EntityPlayer;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

public class Region implements GameElement {
	public String name;
	public ConcurrentHashMap<Integer,Entity> entities;
	public ArrayList<Connection> connections;
	public TiledMap map;
	public int mapColLayer = 0; //Layer containing collision tiles
	public int mapColTOff = 0; //Offset of first collision tile
	private int sendEntities = 20;
	public WeatherCondition weather = new WeatherCondition();
	
	public Region(String name) {
		this.name = name;
		entities = new ConcurrentHashMap<Integer,Entity>();
		connections = new ArrayList<Connection>();
	}
	
	public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver) throws SlickException {
		try {
			map = new TiledMap("data/region/"+name+".tmx");
			mapColLayer = map.getLayerIndex("col");
				int tsid = Integer.parseInt(map.getLayerProperty(mapColLayer, "tileset", "0"));
				mapColTOff=map.getTileSet(tsid).firstGID;
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g, Camera cam, GameClient receiver) throws SlickException {
		//Map is rendered in GameplayState
		List<Entity> list = new ArrayList<Entity>(entities.values());
		try {
		Collections.sort(list); //TODO: Occasionally throws IllegalArgumentException: Comparison methood violates its general contract!
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Entity e : list) {
			e.render(gc, sbg, g, cam, receiver);
		}
	}

	@Override
	public void load(Save save) {
		if (save.get("region."+name) != null)
			this.parseEntityString(save.get("region."+name),false);
	}

	@Override
	public void save(Save save) {
		String ret = "";
		int id = 0;
		for (Entity e : entities.values()) {
			if (e instanceof EntityPlayer) {
				EntityPlayer p = (EntityPlayer) e;
				save.putPlayer(p.getName(), name+"."+p.name, this);
			} else {
				e.save(save);
				String[] parts = e.toString().split(",",3);
				ret = ret+"\\"+parts[0]+","+id+","+parts[2];
				id++;
			}
		}
		if (!ret.equals(""))
			save.put("region."+name, ret.substring(1));
		else
			save.put("region."+name, ret);
	}

	@Override
	public void update(Region region, GameServer receiver) {
		if (sendEntities == 0) {
			//sendEntities=Math.max(1,entities.size()/50);
			sendEntities=5;
		} else {
			sendEntities--;
		}
		for (Entity e : entities.values()) {
			e.update(this,receiver);
			if ((e instanceof EntityPlayer) || (sendEntities == 1 && e.constantUpdate)) {
				MessageSystem.sendClient(this, (ArrayList<Connection>) connections.clone(), new Message(name+"."+e.name+".move",e.x+","+e.y), true);
			}
		}
	}

	@Override
	public void clientUpdate(GameContainer gc, StateBasedGame sbg, GameClient receiver) {
		for (Entity e : entities.values()) {
			e.clientUpdate(gc,sbg,receiver);
		}
	}

	@Override
	public void receiveMessage(Message msg, MessageReceiver receiver) {
		if (msg.getTarget().equals(name)) {
			if (msg.getName().equals("addEnt")) {
				addEntity(msg.getData(),true);
			} else if (msg.getName().equals("addEntSERV")) {
				if (addEntityServer(msg.getData())==-1) {
					msg.reply("CLIENT.chat","ERROR: addEntityServer failed for input string:",this);
					msg.reply("CLIENT.chat","       "+msg.getData(),this);
				}
			} else if (msg.getName().equals("killSERV")) {
				entities.remove(Integer.parseInt(msg.getData()));
				MessageSystem.sendClient(this, connections, new Message(name+".kill",msg.getData()), false);
			} else if (msg.getName().equals("kill")) {
				try {
				entities.get(Integer.parseInt(msg.getData())).kill((GameClient) receiver);
				entities.remove(Integer.parseInt(msg.getData()));
				} catch (Exception e) {
					Log.info("Client could not kill "+msg.getData());
				}
			} else if (msg.getName().equals("hitAt")) {
				EntityPlayer ep = ((GameServer) receiver).getPlayerEnt(msg.getConnection());
				PlayerData.InventoryEntry ie = ep.pdat.getEquipped();
				if (ie!=null)
					if (ie.getItem().onUse((GameServer) receiver,ep))
						ep.pdat.removeItem(ep.pdat.inventory.indexOf(ie),ep.region,ep.getReceiverName());
				for (Entity e : getEntitiesAt(Integer.parseInt(msg.getData().split(",",2)[0]),Integer.parseInt(msg.getData().split(",",2)[1]))) {
					if (e!=ep || (Globals.get("debug",false) && Globals.get("selfHit",true)))
						e.hurt(this,ep,receiver);
				}
			} else if (msg.getName().equals("intAt")) {
				for (Entity e : getEntitiesAt(Integer.parseInt(msg.getData().split(",",2)[0]),Integer.parseInt(msg.getData().split(",",2)[1]))) {
					e.interact(this,((GameServer) receiver).getPlayerEnt(msg.getConnection()),receiver,msg.getConnection());
				}
			} else if (msg.getName().equals("pickupAt")) {
				for (Entity e : getEntitiesAt(Integer.parseInt(msg.getData().split(",",2)[0]),Integer.parseInt(msg.getData().split(",",2)[1]))) {
					if (e instanceof EntityItem)
						e.interact(this,((GameServer) receiver).getPlayerEnt(msg.getConnection()),receiver,msg.getConnection());
				}
			}
		} else if (entities.containsKey(Integer.valueOf(msg.getTarget().split("\\.",2)[1]))) {
			entities.get(Integer.valueOf(msg.getTarget().split("\\.",2)[1])).receiveMessage(msg,receiver);
		} else {
			Log.warn("World."+name+": Ignored message - unrecognised target: "+msg.toString());
		}
	}

	private ArrayList<Entity> getEntitiesAt(int x, int y) {
		ArrayList<Entity> ret = new ArrayList<Entity>();
		for (Entity e : entities.values())
			if (e.collidesWith(x,y))
				ret.add(e);
		return ret;
	}

	public String getEntityString() {
		String ret = "";
		for (Entity e : entities.values()) {
			ret = ret+"\\"+e.toString();
		}
		if (!ret.equals(""))
			return ret.substring(1);
		else
			return ret;
	}
	
	public void parseEntityString(String str,boolean client) {
		String[] ents = str.split("\\\\");
		for (String s : ents) {
			addEntity(s,client);
		}
	}
	
	/**
	 * Adds an entity to this region, with a given ID
	 * @param data String containing entity info, formatted the same as Entity.toString();
	 */
	public void addEntity(String data,boolean client) {
		if (data.split(",",5).length==5) {
			Entity ent = EntityFactory.getEntity(data,this);
			if (ent!=null) {
				entities.put(Integer.valueOf(data.split(",")[1]),ent);
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
	 * @param data String containing entity info, without the ID.
	 * @return ID of the new entity. -1 if creation failed.
	 */
	public int addEntityServer(String data) {
		int idmax=0;
		for (Integer name : entities.keySet())
			if (name>idmax) idmax=name;
		//for (Connection c : connections)
		//	c.sendTCP(new Message(name+".addEnt",data.split(",",2)[0]+","+(idmax+1)+","+data.split(",",2)[1]));
		MessageSystem.sendClient(this, connections, new Message(name+".addEnt",data.split(",",2)[0]+","+(idmax+1)+","+data.split(",",2)[1]), false);
		Entity ent = EntityFactory.getEntity(data.split(",",2)[0]+","+(idmax+1)+","+data.split(",",2)[1],this);
		if (ent!=null) {
			entities.put(idmax+1,ent);
			MessageSystem.registerReceiverServer(ent);
			return idmax+1;
		} else
			return -1;
	}
	
	public boolean aiPlaceFree(int x, int y) {
		try {
			return (map.getTileId(x/32,y/32, mapColLayer) == 0);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		return true;
	}

	public boolean aiPlaceFreeRect(int x, int y, int x2, int y2) {
		return (aiPlaceFree(x,y)&&aiPlaceFree(x2,y)&&aiPlaceFree(x2,y2)&&aiPlaceFree(x,y2));
	}
	
	public ArrayList<Light> getLights() {
		ArrayList<Light> ret = new ArrayList<Light>();
		File file = new File("data/region/"+name+".lm");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String l;
			while((l = br.readLine()) != null) {
				ret.add(new Light(l));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return ret;
	}

	@Override
	public String getReceiverName() {
		return name;
	}
}
