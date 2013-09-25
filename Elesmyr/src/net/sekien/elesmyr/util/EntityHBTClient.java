/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.util;

import net.sekien.elesmyr.msgsys.Message;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTTag;
import net.sekien.hbt.TagNotFoundException;
import org.newdawn.slick.util.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 8/09/13 Time: 7:06 PM To change this template use File | Settings | File
 * Templates.
 */
public class EntityHBTClient extends EntityHBT {
private HBTCompound cache;

@Override public boolean hasTag(String name) {
	if (cache==null) throw new TagNotFoundException(name);
	return cache.hasTag(name);
}

@Override public HBTTag getTag(String name) throws TagNotFoundException {
	if (cache==null) throw new TagNotFoundException(name);
	return cache.getTag(name);
}

@Override public void setTag(String fullname, HBTTag tag) {
	//TODO: consider make this update server-side
	//cache.setTag(fullname, tag);
	Log.error("Error: can't set tag client side (not implemented)");
}

public boolean onReceive(Message msg) {
	if (msg.getName().equals("_uc")) {
		cache = msg.getData();
		return true;
	} else return false;
}
}
