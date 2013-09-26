/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.util.FileHandler;
import org.newdawn.slick.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/07/13 Time: 5:47 PM To change this template use File | Settings |
 * File Templates.
 */
public class EntityAnimTest extends EntityBasic {

Animatable anim;

public void onInitClient() throws SlickException {
	sprite = FileHandler.getImage("ent.worktable");
	anim = new Animatable("ent.animtest");
	anim.setFrameTime(10);
}

public void onInteract() {
	hbt.setByte("dir", hbt.getByte("dir", (byte) 0)==0?(byte) 1:0);
}

public void onRender() {
	anim.setAnimation(hbt.getByte("dir", (byte) 0));
	anim.draw(x, y);
}
}
