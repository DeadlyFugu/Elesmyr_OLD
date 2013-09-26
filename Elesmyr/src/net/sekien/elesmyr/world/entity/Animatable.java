/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.util.FileHandler;
import org.newdawn.slick.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 26/09/13 Time: 10:15 AM To change this template use File | Settings |
 * File Templates.
 */
public class Animatable {
SpriteSheet ss;
private int animation;
private int frame;
private int frameMax;
private int frameTime = 1;

public Animatable(String s) throws SlickException {
	this(s, 32, 32);
}

public Animatable(String s, int w, int h) throws SlickException {
	ss = new SpriteSheet(FileHandler.getImage(s), w, h);
	frameMax = ss.getHorizontalCount();
}

public void setAnimation(int animation) {
	this.animation = animation;
	frameMax = ss.getHorizontalCount();
}

public void setAnimation(int animation, int frameMax) {
	this.animation = animation;
	this.frameMax = frameMax;
}

public int getAnimation() {
	return animation;
}

public int getFrame() {
	return frame;
}

public void setFrame(int frame) {
	this.frame = frame;
}

public void draw(int x, int y) {
	ss.getSubImage((frame++)/frameTime%frameMax, animation).draw(x, y);
}

public void draw(int x, int y, Color col) {
	ss.getSubImage((frame++)/frameTime%frameMax, animation).draw(x, y, col);
}

public void draw(int x, int y, int w, int h, Color col) {
	ss.getSubImage((frame++)/frameTime%frameMax, animation).draw(x, y, w, h, col);
}

public Image getImage() {
	return ss.getSubImage((frame++)/frameTime%frameMax, animation);
}

public int getFrameTime() {
	return frameTime;
}

public void setFrameTime(int frameTime) {
	if (frameTime==0) frameTime = 1;
	this.frameTime = frameTime;
}
}
