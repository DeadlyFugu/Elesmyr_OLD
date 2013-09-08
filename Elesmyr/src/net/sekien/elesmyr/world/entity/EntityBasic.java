/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.util.ClientUtil;
import net.sekien.elesmyr.util.ServerUtil;
import net.sekien.elesmyr.world.Region;
import org.newdawn.slick.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/07/13 Time: 5:21 PM To change this template use File | Settings |
 * File Templates.
 */
public abstract class EntityBasic extends Entity {

protected Image sprite;
protected ClientUtil client;
protected ServerUtil server;
protected RemoteServerUtil remote;

public EntityBasic() {
	super();
}

private boolean inited = false;

public void onInitServer() {}

public void onInitClient() throws SlickException {}

public void onUpdateServer() {}

public void onUpdateClient() {}

public void onRender() {if (sprite!=null) sprite.draw(xs, ys);}

public void onDestroyServer() {}

public void onDestroyClient() {}

public void onHit() {}

public void onInteract() {}

public void onMessage(Message msg, MessageEndPoint receiver) {}

protected void initSERV(GameServer server, Region region) {
	this.server = new ServerUtil(server, region);
	onInitServer();
}

@Override
public void init(GameContainer gc, MessageEndPoint receiver)
		throws SlickException {
	client = new ClientUtil((GameClient) receiver);
	remote = new RemoteServerUtil(region, this);
	if (!inited) {
		inited = true;
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
	onRender();
	g.popTransform();
}

@Override
public void update(Region region, GameServer receiver) {
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
	onMessage(msg, receiver);
}
}
