/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.entity;

import java.util.Random;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/07/13 Time: 6:28 PM To change this template use File | Settings |
 * File Templates.
 */
public abstract class EntityWalkAI extends EntityBasic {

protected int xmove, ymove;
protected boolean moveFree = true;
protected int xtarget, ytarget;
protected int mdist, cmdist; //mdist = distance to move in tiles, cmdist = distance moved so far in pixels
protected Random airand = new Random();

@Override public void onInitServer() {
	constantUpdate = true;
}

@Override public void onUpdateServer() {
	if (moveFree) {
		if (region.aiPlaceFreeRect(x+xmove-10, y-4, x+xmove+10, y+4))
			x += xmove;
		else
			cmdist = mdist*32;
		if (region.aiPlaceFreeRect(x-10, y+ymove-4, x+10, y+ymove+4))
			y += ymove;
		else
			cmdist = mdist*32;

		cmdist++;
		if (cmdist > mdist*32) {
			//use airand to change direction and mdist
			int dir = airand.nextInt(4);
			xmove = ymove = 0;
			if (dir==0)
				xmove = 1;
			else if (dir==1)
				xmove = -1;
			else if (dir==2)
				ymove = 1;
			else if (dir==3)
				ymove = -1;
			mdist = airand.nextInt(3)+3;
			cmdist = 0;
		}
	}
}
}
