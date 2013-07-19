/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.hbt;

/**
 * Created with IntelliJ IDEA. User: matt Date: 14/04/13 Time: 8:20 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTTools {
/** Takes an X and Y position and stores them as such: pos { int x = x int y = y } */
public static HBTCompound position(int x, int y) {
	return new HBTCompound("pos", new HBTTag[]{new HBTInt("x", x), new HBTInt("y", y)});
}

public static HBTCompound location(String region, int x, int y) {
	return new HBTCompound("pos", new HBTTag[]{new HBTString("region", region), new HBTInt("x", x), new HBTInt("y", y)});
}

public static HBTCompound msgString(String name, String data) {
	return new HBTCompound("p", new HBTTag[]{new HBTString(name, data)});
}

public static HBTCompound msgInt(String name, int data) {
	return new HBTCompound("p", new HBTTag[]{new HBTInt(name, data)});
}

public static HBTCompound msgWrap(HBTTag tag) {
	return new HBTCompound("p", new HBTTag[]{tag});
}
}