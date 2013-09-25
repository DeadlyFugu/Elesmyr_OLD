/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.player.PlayerClient;
import net.sekien.elesmyr.util.FileHandler;
import org.newdawn.slick.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 13/09/13 Time: 3:42 PM To change this template use File | Settings |
 * File Templates.
 */
public class WalkingAnimation {

private SpriteSheet spr;
private int x, y;
private float xs, ys;
private int px, py;
private float spx, spy;
private int animState, dir; //animState: 0=still,1=moving,2=attacking; dir: 0=left, 1=up, 2=down
private float animFrame;
private boolean flip;
private boolean manualDir = false;

public WalkingAnimation(String file) {
	try {
		spr = new SpriteSheet(FileHandler.getImage("player.player_reg"), 32, 48);
	} catch (SlickException e) {
		e.printStackTrace();
	}
}

public void setPos(int x, int y, float xs, float ys) {
	this.x = x;
	this.y = y;
	this.xs = xs;
	this.ys = ys;
}

public void render(Camera cam) {
	draw(xs, ys, x, y, px, py, spx, spy, cam);
	px = (int) x;
	py = (int) y;
	spx = xs;
	spy = ys;
}

private void draw(float xd, float yd, int x, int y, int px, int py, float spx, float spy, Camera cam) { //todo: remove unneeded args
	int tx = 0;
	int ty = 0;
	if (animState!=2) {
		int wrth = 1;
		if (Math.round(x*wrth)==Math.round(spx*wrth) && Math.round(y*wrth)==Math.round(spy*wrth))
			animState = 0;
		else {
			animState = 1;
			int ddir = (int) (Math.atan2(xd-spx, yd-spy)*(180f/Math.PI)+90);
			int bdir = (int) (ddir/90f);
			if (manualDir) {
			} else if (bdir==2) {
				dir = 0;
				flip = true;
			} else if (bdir==1) {
				dir = 1;
				flip = false;
			} else if (bdir==0) {
				dir = 0;
				flip = false;
			} else if (bdir==3) {
				dir = 2;
				flip = false;
			}
		}
	}
	if (animState==0) { //still
		tx = dir;
		ty = 3;
	} else if (animState==1) { //walking
		tx = (int) animFrame;
		ty = dir;
	} else if (animState==2) { //attacking
		if (dir==0) {
			ty = 3;
			tx = (int) (3+animFrame);
		} else if (dir==1) {
			ty = 4;
			tx = (int) animFrame;
		} else if (dir==2) {
			ty = 4;
			tx = (int) (3+animFrame);
		}
	}
	if (PlayerClient.BIGSIZE) {
		spr.getSprite(tx, ty).draw(xd+(flip?32:-32), yd-78, (flip?-64:64), 96);
	} else {
		spr.getSprite(tx, ty).draw(xd+(flip?16:-16), yd-39, (flip?-32:32), 48);
	}
	if (animState!=0) //if not still
		animFrame += 0.2f; //update animFrame
	if (animFrame >= 6 && animState==1)
		animFrame = 0;
	if (animFrame >= 3 && animState==2)
		animFrame = animState = 0;
}

public void setDirection(int direction) {
	if (direction==3) {
		flip = true;
		dir = 0;
	} else {
		dir = direction;
		flip = false;
	}
	this.manualDir = true;
}
}
