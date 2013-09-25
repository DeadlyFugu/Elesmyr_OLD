/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.util;

import net.sekien.elesmyr.Element;
import net.sekien.hbt.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 8/09/13 Time: 6:11 PM To change this template use File | Settings | File
 * Templates.
 */
public abstract class EntityHBT {
public abstract boolean hasTag(String name);

public abstract HBTTag getTag(String name) throws TagNotFoundException;

public byte getByte(String name) {
	HBTTag tag = getTag(name);
	if (tag instanceof HBTByte) return ((HBTByte) tag).getData();
	else throw new TagNotFoundException(name);
}

public byte getByte(String name, byte def) {
	HBTTag tag;
	try {tag = getTag(name);} catch (TagNotFoundException e) {return def;}
	if (tag instanceof HBTByte) return ((HBTByte) tag).getData();
	else throw new TagNotFoundException(name);
}

public short getShort(String name) {
	HBTTag tag = getTag(name);
	if (tag instanceof HBTShort) return ((HBTShort) tag).getData();
	else throw new TagNotFoundException(name);
}

public short getShort(String name, short def) {
	HBTTag tag;
	try {tag = getTag(name);} catch (TagNotFoundException e) {return def;}
	if (tag instanceof HBTShort) return ((HBTShort) tag).getData();
	else throw new TagNotFoundException(name);
}

public int getInt(String name) {
	HBTTag tag = getTag(name);
	if (tag instanceof HBTInt) return ((HBTInt) tag).getData();
	else throw new TagNotFoundException(name);
}

public int getInt(String name, int def) {
	HBTTag tag;
	try {tag = getTag(name);} catch (TagNotFoundException e) {return def;}
	if (tag instanceof HBTInt) return ((HBTInt) tag).getData();
	else throw new TagNotFoundException(name);
}

public long getLong(String name) {
	HBTTag tag = getTag(name);
	if (tag instanceof HBTLong) return ((HBTLong) tag).getData();
	else throw new TagNotFoundException(name);
}

public long getLong(String name, long def) {
	HBTTag tag;
	try {tag = getTag(name);} catch (TagNotFoundException e) {return def;}
	if (tag instanceof HBTLong) return ((HBTLong) tag).getData();
	else throw new TagNotFoundException(name);
}

public float getFloat(String name) {
	HBTTag tag = getTag(name);
	if (tag instanceof HBTFloat) return ((HBTFloat) tag).getData();
	else throw new TagNotFoundException(name);
}

public float getFloat(String name, float def) {
	HBTTag tag;
	try {tag = getTag(name);} catch (TagNotFoundException e) {return def;}
	if (tag instanceof HBTFloat) return ((HBTFloat) tag).getData();
	else throw new TagNotFoundException(name);
}

public double getDouble(String name) {
	HBTTag tag = getTag(name);
	if (tag instanceof HBTDouble) return ((HBTDouble) tag).getData();
	else throw new TagNotFoundException(name);
}

public double getDouble(String name, double def) {
	HBTTag tag;
	try {tag = getTag(name);} catch (TagNotFoundException e) {return def;}
	if (tag instanceof HBTDouble) return ((HBTDouble) tag).getData();
	else throw new TagNotFoundException(name);
}

public byte[] getData(String name) {
	HBTTag tag = getTag(name);
	if (tag instanceof HBTByteArray) return ((HBTByteArray) tag).getData();
	else throw new TagNotFoundException(name);
}

public byte[] getData(String name, byte[] def) {
	HBTTag tag;
	try {tag = getTag(name);} catch (TagNotFoundException e) {return def;}
	if (tag instanceof HBTByteArray) return ((HBTByteArray) tag).getData();
	else throw new TagNotFoundException(name);
}

public HBTFlag getFlag(String name) {
	HBTTag tag = getTag(name);
	if (tag instanceof HBTFlag) return ((HBTFlag) tag);
	else throw new TagNotFoundException(name);
}

public HBTFlag getFlag(String name, HBTFlag def) {
	HBTTag tag;
	try {tag = getTag(name);} catch (TagNotFoundException e) {return def;}
	if (tag instanceof HBTFlag) return ((HBTFlag) tag);
	else throw new TagNotFoundException(name);
}

public String getString(String name) {
	HBTTag tag = getTag(name);
	if (tag instanceof HBTString) return ((HBTString) tag).getData();
	else throw new TagNotFoundException(name);
}

public String getString(String name, String def) {
	HBTTag tag;
	try {tag = getTag(name);} catch (TagNotFoundException e) {return def;}
	if (tag instanceof HBTString) return ((HBTString) tag).getData();
	else throw new TagNotFoundException(name);
}

public abstract void setTag(String fullname, HBTTag tag);

public void setByte(String name, byte val) {
	setTag(name, new HBTByte("", val));
}

public void setShort(String name, short val) {
	setTag(name, new HBTShort("", val));
}

public void setInt(String name, int val) {
	setTag(name, new HBTInt("", val));
}

public void setLong(String name, long val) {
	setTag(name, new HBTLong("", val));
}

public void setFloat(String name, float val) {
	setTag(name, new HBTFloat("", val));
}

public void setDouble(String name, double val) {
	setTag(name, new HBTDouble("", val));
}

public void setData(String name, byte[] val) {
	setTag(name, new HBTByteArray("", val));
}

public void setFlag(String name, HBTFlag val) {
	setTag(name, val);
}

public void setBoolean(String name, boolean val) {
	setTag(name, new HBTFlag(name, (byte) (val?0:1)));
}

public void setElement(String name, Element val) {
	setTag(name, new HBTFlag(name, val.toString()));
}

public void setString(String name, String val) {
	setTag(name, new HBTString("", val));
}
}
