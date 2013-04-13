package net.sekien.lote.world;

import com.esotericsoftware.minlog.Log;
import net.sekien.lote.GameElement;
import net.sekien.lote.Save;
import net.sekien.lote.msgsys.Message;
import net.sekien.lote.msgsys.MessageReceiver;
import net.sekien.lote.player.Camera;
import net.sekien.lote.system.GameClient;
import net.sekien.lote.system.GameServer;
import net.sekien.lote.system.Main;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class World implements GameElement {
public ConcurrentHashMap<String, Region> regions;
public Save save;
public ArrayList<Region> needsInit;

public World() {
	regions=new ConcurrentHashMap<String, Region>();
	//removeRegions = new ArrayList<Region>();
	needsInit=new ArrayList<Region>(); //Needed because
}

@Override
public void render(GameContainer gc, StateBasedGame sbg, Graphics g, Camera cam, GameClient receiver) throws SlickException {
	Region r=null;
	String name=((GameClient) sbg.getState(Main.GAMEPLAYSTATE)).getPlayer().getRegionName();
	if (name!=null)
		r=regions.get(name);
	if (r!=null)
		r.render(gc, sbg, g, cam, receiver);
}

@Override
public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver) throws SlickException {
	for (Region r : regions.values()) {
		r.init(gc, sbg, receiver);
	}
}

@Override
public void load(Save save) {
	this.save=save;
}

@Override
public void update(Region region, GameServer receiver) {
	for (Region r : needsInit) {
		try {
			r.init(null, null, receiver);
			regions.put(r.name, r);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	needsInit.clear();
	for (Region r : regions.values()) {
		r.update(null, receiver);
		if (r.connections.size()==0) {
			r.save(save);
			regions.remove(r.name);
		}
	}
		/*for (Region r : removeRegions) {
			r.save(save);
			regions.remove(r);
		}
		removeRegions.clear();*/
}

@Override
public void clientUpdate(GameContainer gc, StateBasedGame sbg, GameClient receiver) {
	for (Region r : needsInit) {
		try {
			r.init(null, null, receiver);
			regions.clear();
			regions.put(r.name, r);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	needsInit.clear();
	for (Region r : regions.values()) {
		r.clientUpdate(gc, sbg, receiver);
	}
}

/**
 * Load a region if it is not already loaded.
 *
 * @param name
 * 		Name of the region
 */
public void touchRegion(String name) {
	if (getRegion(name)==null) {
		loadRegion(name);
	}
}

/**
 * Load a region if it is not already loaded. Does not read from save file
 *
 * @param name
 * 		Name of the region
 */
public void touchRegionClient(String name) {
	if (getRegion(name)==null) {
		Region r=new Region(name);
		needsInit.add(r);
	}
}

@Override
public void receiveMessage(Message msg, MessageReceiver receiver) {
	if (msg.getTarget().equals("WORLD")) {
		String name=msg.getName();
		if (name.equals("someName")) {
			//TODO: put actual stuff here
		} else {
			Log.warn("World: Ignored message - unrecognised name: "+msg.toString());
		}
	} else if (regions.containsKey(msg.getTarget().split("\\.", 2)[0])) {
		regions.get(msg.getTarget().split("\\.", 2)[0]).receiveMessage(msg, receiver);
	} else {
		for (Region r : needsInit) {
			if (r.name.equals(msg.getTarget().split("\\.", 2)[0])) {
				r.receiveMessage(msg, receiver);
				return;
			}
		}
		Log.warn("World: Ignored message - unrecognised target: "+msg.toString());
	}
}

public void loadRegion(String name) {
	Region r=new Region(name);
	r.load(save);
	needsInit.add(r);
}

@Override
public void save(Save save) {
	for (Region r : regions.values()) {
		r.save(save);
	}
}

@Override
public void fromHBT(net.sekien.hbt.HBTCompound tag) {
	//What I do here?
}

@Override
public net.sekien.hbt.HBTCompound toHBT() {
	net.sekien.hbt.HBTCompound ret=new net.sekien.hbt.HBTCompound("world");
	for (Region r : regions.values()) {
		ret.addTag(r.toHBTSave());
	}
	return ret;
}

public Region getRegion(String name) {
	Region ret=regions.get(name);
	if (ret!=null)
		return ret;
	for (Region r : needsInit) {
		if (r.name.equals(name)) {
			return r;
		}
	}
	return null;
}

@Override
public String getReceiverName() {
	return "WORLD";
}
}
