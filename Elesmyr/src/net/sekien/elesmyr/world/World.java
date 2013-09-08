/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world;

import com.esotericsoftware.minlog.Log;
import net.sekien.elesmyr.GameElement;
import net.sekien.elesmyr.Save;
import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.GameServer;
import net.sekien.hbt.HBTCompound;
import org.newdawn.slick.*;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class World implements GameElement {
public ConcurrentHashMap<String, Region> regions;
public Save save;
public ArrayList<Region> needsInit;

public World() {
	regions = new ConcurrentHashMap<String, Region>();
	//removeRegions = new ArrayList<Region>();
	needsInit = new ArrayList<Region>(); //Needed because
}

@Override
public void render(GameContainer gc, Graphics g, Camera cam, GameClient receiver) throws SlickException {
	Region r = null;
	String name = receiver.getPlayer().getRegionName();
	if (name!=null)
		r = regions.get(name);
	if (r!=null)
		r.render(gc, g, cam, receiver);
}

@Override
public void init(GameContainer gc, MessageEndPoint receiver) throws SlickException {
	for (Region r : regions.values()) {
		r.init(gc, receiver);
	}
}

@Override
public void load(Save save) {
	this.save = save;
}

@Override
public void update(Region region, GameServer receiver) {
	for (Region r : needsInit) {
		try {
			r.init(null, receiver);
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
public void clientUpdate(GameContainer gc, GameClient receiver) {
	for (Region r : needsInit) {
		try {
			r.init(null, receiver);
			regions.clear();
			regions.put(r.name, r);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	needsInit.clear();
	for (Region r : regions.values()) {
		r.clientUpdate(gc, receiver);
	}
}

/**
 * Load a region if it is not already loaded.
 *
 * @param name
 * 		Name of the region
 */
public void touchRegion(String name, MessageEndPoint receiver) {
	if (getRegion(name)==null) {
		loadRegion(name, receiver);
	}
}

/**
 * Load a region if it is not already loaded. Does not read from save file
 *
 * @param name
 * 		Name of the region
 */
public void touchRegionClient(String name, MessageEndPoint receiver) {
	if (getRegion(name)==null) {
		Region r = new Region(name, receiver);
		needsInit.add(r);
	}
}

@Override
public void receiveMessage(Message msg, MessageEndPoint receiver) {
	if (msg.getTarget().equals("WORLD")) {
		String name = msg.getName();
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

public void loadRegion(String name, MessageEndPoint receiver) {
	Region r = new Region(name, receiver);
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
public void fromHBT(HBTCompound tag) {
	//What I do here?
}

@Override
public HBTCompound toHBT(boolean msg) {
	HBTCompound ret = new HBTCompound("world");
	for (Region r : regions.values()) {
		ret.addTag(r.toHBTSave());
	}
	return ret;
}

public Region getRegion(String name) {
	Region ret = regions.get(name);
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
