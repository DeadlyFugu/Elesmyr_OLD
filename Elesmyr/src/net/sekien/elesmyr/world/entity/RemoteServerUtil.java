/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageReceiver;
import net.sekien.elesmyr.msgsys.MessageSystem;
import net.sekien.elesmyr.world.Region;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTInt;
import net.sekien.hbt.HBTString;

/**
 * Created with IntelliJ IDEA. User: matt Date: 7/09/13 Time: 12:31 PM To change this template use File | Settings |
 * File Templates.
 */
public class RemoteServerUtil {
private final String regionName;
private final String regionNameDot;
private MessageReceiver parent;

public RemoteServerUtil(Region region, MessageReceiver parent) {
	this.parent = parent;
	regionName = region.getReceiverName();
	regionNameDot = regionName.concat(".");
}

public void sendMessage(String name, HBTCompound msg) {
	MessageSystem.sendServer(parent, new Message(regionName.concat(name), msg), false);
}

public void addEntity(HBTCompound tag) {
	sendMessage("addEntSERV", tag);
}

public void addEntity(int x, int y, HBTCompound tag) {
	tag.setTag(new HBTInt("x", x));
	tag.setTag(new HBTInt("y", y));
	addEntity(tag);
}

public void addEntity(String type, int x, int y, HBTCompound tag) {
	tag.setTag(new HBTString("class", type));
	tag.setTag(new HBTInt("y", y));
	addEntity(tag);
}
}
