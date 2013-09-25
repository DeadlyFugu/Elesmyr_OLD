/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.hbt;

import org.newdawn.slick.util.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 7:56 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTCompound extends HBTTag implements Iterable<HBTTag> {
private final ArrayList<HBTTag> data = new ArrayList<HBTTag>();

public HBTCompound(String name) {
	super(name);
}

public HBTCompound(String name, HBTTag[] tags) {
	super(name);
	for (HBTTag tag : tags) {
		data.add(tag);
	}
}

public void addTag(HBTTag tag) {
	boolean hasAlready = false;
	for (HBTTag tag1 : data) {
		if (tag1.getName().equals(tag.getName())) hasAlready = true;
	}
	if (hasAlready) try {
		HBTTag current = getTag(tag.getName());
		if (current instanceof HBTCompound && tag instanceof HBTCompound) {
			((HBTCompound) current).merge((HBTCompound) tag);
		} else {
			Log.warn("conflict between:\n"+current+"\nand:\n"+tag+"\nFirst was chosen.");
		}
	} catch (TagNotFoundException e) {
		e.printStackTrace();
	}
	else {
		data.add(tag);
	}
}

public void setTag(HBTTag tag) {
	int hasAlready = -1;
	for (HBTTag tag1 : data) {
		if (tag1.getName().equals(tag.getName())) hasAlready = data.indexOf(tag1);
	}
	if (hasAlready!=-1) {
		data.set(hasAlready, tag);
	} else {
		data.add(tag);
	}
}

public void setTag(String fullname, HBTTag tag) {
	if (fullname.contains(".")) {
		String[] parts = fullname.split("\\.", 2);
		getCompoundTouch(parts[0]).setTag(parts[1], tag);
	} else {
		setTag(tag);
	}
}

private HBTCompound getCompoundTouch(String name) {
	try {return (HBTCompound) getTag(name);} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return (HBTCompound) addTagPass(new HBTCompound(name));
	} catch (TagNotFoundException e) {return (HBTCompound) addTagPass(new HBTCompound(name));}
}

public HBTTag getTag(String name) {
	if (name.contains(".")) {
		String[] parts = name.split("\\.", 2);
		if (hasTag(parts[0]))
			return getCompoundTouch(parts[0]).getTag(parts[1]);
		throw new TagNotFoundException(name);
	} else {
		for (HBTTag tag : this) {
			if (tag.getName().equals(name)) {
				return tag;
			}
		}
		throw new TagNotFoundException(name);
	}
}

private HBTTag addTagPass(HBTTag tag) {
	addTag(tag);
	return tag;
}

public HBTCompound getCompound(String name) {
	try {return (HBTCompound) getTag(name);} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return new HBTCompound(name);
	} catch (TagNotFoundException e) {return new HBTCompound(name);}
}

public byte getByte(String name, byte def) {
	try {return ((HBTByte) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public short getShort(String name, short def) {
	try {return ((HBTShort) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public int getInt(String name, int def) {
	try {return ((HBTInt) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public long getLong(String name, long def) {
	try {return ((HBTLong) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public float getFloat(String name, float def) {
	try {return ((HBTFloat) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public double getDouble(String name, double def) {
	try {return ((HBTDouble) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public String getString(String name, String def) {
	try {return ((HBTString) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public byte[] getByteArray(String name, byte[] def) {
	try {return ((HBTByteArray) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public HBTFlag getFlag(String name, String def) {
	try {return (HBTFlag) getTag(name);} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return new HBTFlag(name, def);
	} catch (TagNotFoundException e) {return new HBTFlag(name, def);}
}

public List<HBTTag> getData() {
	return data;
}

@Override
public Iterator<HBTTag> iterator() {
	return data.iterator();
}

@Override
public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append(getName()+" {");
	for (HBTTag tag : this) {
		builder.append("\n    "+tag.toString().replaceAll("\n", "\n    "));
	}
	builder.append("\n}");
	return builder.toString();
}

public void merge(HBTCompound other) {
	for (HBTTag tag : other) {
		this.addTag(tag);
	}
}

public boolean hasTag(String name) {
	for (HBTTag tag : data) {
		if (tag.getName().equals(name)) return true;
	}
	return false;
}

public void deleteTag(String child) {
	data.remove(getTag(child));
}

@Override
public HBTTag deepClone() {
	HBTCompound clone = new HBTCompound(getName());
	for (HBTTag tag : data) {
		clone.addTag(tag.deepClone());
	}
	return clone;
}

}
