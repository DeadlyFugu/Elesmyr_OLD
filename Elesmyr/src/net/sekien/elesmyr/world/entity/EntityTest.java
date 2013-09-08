/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.util.FileHandler;
import net.sekien.hbt.HBTAX;
import org.newdawn.slick.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/07/13 Time: 5:47 PM To change this template use File | Settings |
 * File Templates.
 */
public class EntityTest extends EntityWalkAI {

/*public void onInitServer() {Log.info("test.onInitServer");}
public void onInitClient() {Log.info("test.onInitClient");}
public void onUpdateServer() {Log.info("test.onUpdSv");}
public void onUpdateClient() {Log.info("test.onUpdCl");}
public void onRender() {Log.info("test.onRender");}
public void onDestroyServer() {Log.info("test.onDestrSrv");}
public void onDestroyClient() {Log.info("test.onDestrCl");}
public void onHit() {Log.info("test.onht");}
public void onInteract() {Log.info("test.oninter");}*/

public void onInitClient() throws SlickException {
	System.out.println("init_CLIENT");
	sprite = FileHandler.getImage("ent.worktable");
}

public void onInteract() {
	server.addEntity("EnemyMushroom", x, y, HBTAX.getTags("health:5", "ent"));
	moveFree = !moveFree;
}
}
