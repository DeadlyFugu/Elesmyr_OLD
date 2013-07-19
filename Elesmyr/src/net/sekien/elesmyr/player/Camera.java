/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.player;

import net.sekien.elesmyr.system.Main;
import net.sekien.elesmyr.world.Region;
import net.sekien.tiled.TiledMapPlus;

public class Camera {
float x, tx;
float y, ty;
int xoff, yoff;

public Camera(int x, int y) {
	this.x = tx = x;
	this.y = ty = y;
}

public void update(PlayerClient player) {
	tx = (player.x);
	ty = (player.y);
	if (tx < Main.INTERNAL_RESX/2)
		tx = Main.INTERNAL_RESX/2;
	if (ty < Main.INTERNAL_RESY/2)
		ty = Main.INTERNAL_RESY/2;
	Region pr = player.getRegion();
	if (pr!=null) {
		TiledMapPlus map = pr.map;
		if (tx > map.getWidth()*32-Main.INTERNAL_RESX/2)
			tx = map.getWidth()*32-Main.INTERNAL_RESX/2;
		if (ty > map.getHeight()*32-Main.INTERNAL_RESY/2)
			ty = map.getHeight()*32-Main.INTERNAL_RESY/2;
	}

	x += (tx-x)/10;
	y += (ty-y)/10;
	xoff = (int) -(x-(Main.INTERNAL_RESX/2));
	yoff = (int) -(y-(Main.INTERNAL_RESY/2));
}

public int getXOff() {
	return xoff;
}

public int getYOff() {
	return yoff;
}

public void setPosition(int x, int y, PlayerClient player) {
	this.x = tx = x;
	this.y = tx = y;
		/*if (this.x<Main.INTERNAL_RESX/2) //Uncommenting this will removes the camera sliding-y
			this.x=Main.INTERNAL_RESX/2;   //thing when the camera moves between regions
		if (this.y<Main.INTERNAL_RESY/2)
			this.y=Main.INTERNAL_RESY/2;
		Region pr = player.getRegion();
		if (pr != null) {
			TiledMap map = pr.map;
			if (this.x>map.getWidth()*32-Main.INTERNAL_RESX/2)
				this.x=map.getWidth()*32-Main.INTERNAL_RESX/2;
			if (this.y>map.getHeight()*32-Main.INTERNAL_RESY/2)
				this.y=map.getHeight()*32-Main.INTERNAL_RESY/2;
		}*/
}
}