package net.sekien.hbt;

import org.newdawn.slick.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 7:56 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTCompound extends net.sekien.hbt.HBTTag implements Iterable<net.sekien.hbt.HBTTag> {
private final ArrayList<net.sekien.hbt.HBTTag> data=new ArrayList<net.sekien.hbt.HBTTag>();

public HBTCompound(String name) {
	super(name);
}

public void addTag(net.sekien.hbt.HBTTag tag) {
	boolean hasAlready=false;
	for (net.sekien.hbt.HBTTag tag1 : data) {
		if (tag1.getName().equals(tag.getName())) hasAlready=true;
	}
	if (hasAlready) try {
		net.sekien.hbt.HBTTag current=getTag(tag.getName());
		if (current instanceof HBTCompound&&tag instanceof HBTCompound) {
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

public void setTag(net.sekien.hbt.HBTTag tag) {
	int hasAlready=-1;
	for (net.sekien.hbt.HBTTag tag1 : data) {
		if (tag1.getName().equals(tag.getName())) hasAlready=data.indexOf(tag1);
	}
	if (hasAlready!=-1) {
		data.set(hasAlready, tag);
	} else {
		data.add(tag);
	}
}

public void setTag(String fullname, net.sekien.hbt.HBTTag tag) {
	if (fullname.contains(".")) {
		String[] parts=fullname.split("\\.", 2);
		getCompoundTouch(parts[0]).setTag(parts[1], tag);
	} else {
		setTag(tag);
	}
}

private HBTCompound getCompoundTouch(String name) {
	try {return (HBTCompound) getTag(name);} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return (HBTCompound) addTagPass(new HBTCompound(name));
	} catch (HBTCompound.TagNotFoundException e) {return (HBTCompound) addTagPass(new HBTCompound(name));}
}

public net.sekien.hbt.HBTTag getTag(String name) {
	if (name.contains(".")) {
		String[] parts=name.split("\\.", 2);
		return getCompoundTouch(parts[0]).getTag(parts[1]);
	} else {
		for (net.sekien.hbt.HBTTag tag : this) {
			if (tag.getName().equals(name)) {
				return tag;
			}
		}
		throw new TagNotFoundException(name);
	}
}

private net.sekien.hbt.HBTTag addTagPass(net.sekien.hbt.HBTTag tag) {
	addTag(tag);
	return tag;
}

public HBTCompound getCompound(String name) {
	try {return (HBTCompound) getTag(name);} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return new HBTCompound(name);
	} catch (HBTCompound.TagNotFoundException e) {return new HBTCompound(name);}
}

public byte getByte(String name, byte def) {
	try {return ((HBTByte) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (HBTCompound.TagNotFoundException e) {return def;}
}

public short getShort(String name, short def) {
	try {return ((net.sekien.hbt.HBTShort) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (HBTCompound.TagNotFoundException e) {return def;}
}

public int getInt(String name, int def) {
	try {return ((net.sekien.hbt.HBTInt) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (HBTCompound.TagNotFoundException e) {return def;}
}

public long getLong(String name, long def) {
	try {return ((net.sekien.hbt.HBTLong) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (HBTCompound.TagNotFoundException e) {return def;}
}

public float getFloat(String name, float def) {
	try {return ((net.sekien.hbt.HBTFloat) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (HBTCompound.TagNotFoundException e) {return def;}
}

public double getDouble(String name, double def) {
	try {return ((net.sekien.hbt.HBTDouble) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (HBTCompound.TagNotFoundException e) {return def;}
}

public String getString(String name, String def) {
	try {return ((net.sekien.hbt.HBTString) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (HBTCompound.TagNotFoundException e) {return def;}
}

public byte[] getByteArray(String name, byte[] def) {
	try {return ((net.sekien.hbt.HBTByteArray) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (HBTCompound.TagNotFoundException e) {return def;}
}

public HBTFlag getFlag(String name, String def) {
	try {return (HBTFlag) getTag(name);} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return new HBTFlag(name, def);
	} catch (HBTCompound.TagNotFoundException e) {return new HBTFlag(name, def);}
}

public List<net.sekien.hbt.HBTTag> getData() {
	return data;
}

@Override
public Iterator<net.sekien.hbt.HBTTag> iterator() {
	return data.iterator();
}

@Override
public String toString() {
	StringBuilder builder=new StringBuilder();
	builder.append(getName()+" {");
	for (net.sekien.hbt.HBTTag tag : this) {
		builder.append("\n    "+tag.toString().replaceAll("\n", "\n    "));
	}
	builder.append("\n}");
	return builder.toString();
}

public void merge(HBTCompound other) {
	for (net.sekien.hbt.HBTTag tag : other) {
		this.addTag(tag);
	}
}

public class TagNotFoundException extends RuntimeException {
	public TagNotFoundException(String name) {
		super(name);
	}
}
}
