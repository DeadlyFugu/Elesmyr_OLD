/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.util;

import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.world.Region;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTInt;
import net.sekien.hbt.HBTString;

/**
 * Created with IntelliJ IDEA. User: matt Date: 7/09/13 Time: 7:48 AM To change this template use File | Settings | File
 * Templates.
 */
public class ServerUtil {
private final GameServer server;
private final Region region;

public ServerUtil(GameServer server, Region region) {
	this.server = server;
	this.region = region;
}

public void addEntity(HBTCompound tag) {
	region.addEntityServer(tag, server);
}

public void addEntity(int x, int y, HBTCompound tag) {
	tag.setTag(new HBTInt("x", x));
	tag.setTag(new HBTInt("y", y));
	addEntity(tag);
}

public void addEntity(String type, int x, int y, HBTCompound tag) {
	tag.setTag(new HBTString("class", type));
	tag.setTag(new HBTInt("x", x));
	tag.setTag(new HBTInt("y", y));
	addEntity(tag);
}
}
