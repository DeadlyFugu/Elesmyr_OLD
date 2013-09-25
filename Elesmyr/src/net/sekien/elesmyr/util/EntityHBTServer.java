/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.util;

import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageReceiver;
import net.sekien.elesmyr.msgsys.MessageSystem;
import net.sekien.elesmyr.world.Region;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTTag;
import net.sekien.hbt.TagNotFoundException;

/**
 * Created with IntelliJ IDEA. User: matt Date: 8/09/13 Time: 7:56 PM To change this template use File | Settings | File
 * Templates.
 */
public class EntityHBTServer extends EntityHBT {
private HBTCompound tag;
private Region region;
private MessageReceiver parent;

public EntityHBTServer(HBTCompound tag, Region region, MessageReceiver parent) {
	this.tag = tag;
	this.region = region;
	this.parent = parent;
}

@Override public boolean hasTag(String name) {
	return tag.hasTag(name);
}

@Override public HBTTag getTag(String name) throws TagNotFoundException {
	return tag.getTag(name);
}

@Override public void setTag(String fullname, HBTTag tag) {
	String[] tmp = fullname.split("\\.");
	tag.setName(tmp[tmp.length-1]);
	this.tag.setTag(fullname, tag);
}

private int updTimer;

public void update(String receiverName) {
	if (updTimer-- < 0) {
		updTimer = 30;
		MessageSystem.sendClient(parent, region.connections, new Message(parent.getReceiverName()+"._uc", tag), true);
	}
}
}
