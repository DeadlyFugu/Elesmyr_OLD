/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.entity;

import com.esotericsoftware.minlog.Log;
import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.util.*;
import net.sekien.elesmyr.world.Region;
import org.newdawn.slick.*;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/07/13 Time: 5:21 PM To change this template use File | Settings |
 * File Templates.
 */
public abstract class EntityBasic extends Entity {

protected Image sprite;
protected ClientUtil client;
protected ServerUtil server;
protected RemoteServerUtil remote;
protected EntityHBT hbt;
protected Graphics g;
protected Camera cam;

public EntityBasic() {
	super();
}

private boolean inited = false;

public void onInitServer() {}

public void onInitClient() throws SlickException {}

public void onUpdateServer() {}

public void onUpdateClient() {}

public void onRender() {if (sprite!=null) sprite.draw(xs, ys, decode(hbt.getString("color", "white")));}

private static HashMap<String, Color> colTable;

static {
	colTable = new HashMap<String, Color>();
	colTable.put("white", Color.white);
	colTable.put("black", Color.black);
	colTable.put("red", Color.red);
	colTable.put("green", Color.green);
	colTable.put("blue", Color.blue);
	colTable.put("purple", new Color(0xFF, 0x00, 0xFF));
	colTable.put("yellow", Color.yellow);
	colTable.put("aqua", Color.cyan);
	colTable.put("cyan", Color.cyan);
}

private Color decode(String string) {
	if (string.charAt(0)=='#')
		return Color.decode(string);
	else {
		Color col = colTable.get(string.toLowerCase());
		return col==null?Color.white:col;
	}
}

public void onDestroyServer() {}

public void onDestroyClient() {}

public void onHit() {}

public void onInteract() {}

public void onMessage(Message msg, MessageEndPoint receiver) {Log.warn("EntityBasic missed message "+msg);}

protected void initSERV(GameServer server, Region region) {
	this.server = new ServerUtil(server, region, this);
	hbt = new EntityHBTServer(inst_dat, region, this);
	onInitServer();
}

@Override
public void init(GameContainer gc, MessageEndPoint receiver)
		throws SlickException {
	client = new ClientUtil((GameClient) receiver);
	remote = new RemoteServerUtil(region, this);
	if (!inited) {
		inited = true;
		hbt = new EntityHBTClient();
		onInitClient();
	}
}

@Override
public void render(GameContainer gc, Graphics g, Camera cam, GameClient receiver) throws SlickException {
	if (!inited) {
		init(gc, receiver);
	}
	g.pushTransform();
	g.translate(cam.getXOff(), cam.getYOff());
	this.g = g;
	this.cam = cam;
	onRender();
	this.g = null;
	this.cam = null;
	g.popTransform();
}

@Override
public void update(Region region, GameServer receiver) {
	((EntityHBTServer) hbt).update(getReceiverName());
	onUpdateServer();
}

@Override
public void clientUpdate(GameContainer gc, GameClient receiver) {
	xs += (x-xs)/10;
	ys += (y-ys)/10;
	onUpdateClient();
}

@Override public void kill(GameClient gc) {onDestroyClient();}

@Override public void killserv() {onDestroyServer();}

@Override public void hurt(Region region, Entity entity, MessageEndPoint receiver) {onHit();}

@Override public void interact(Region region, EntityPlayer entityPlayer, MessageEndPoint receiver, Message msg) {
	onInteract();
}

@Override public void receiveMessageExt(Message msg, MessageEndPoint receiver) {
	if (((EntityHBTClient) hbt).onReceive(msg)) {} else onMessage(msg, receiver);
}

protected void setCollision(int x, int y, int w, int h) {
	cx1 = x; cy1 = y;
	cx2 = x+w; cy2 = y+h;
}
}
