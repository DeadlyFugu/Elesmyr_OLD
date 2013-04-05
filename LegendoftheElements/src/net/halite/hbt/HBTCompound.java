package net.halite.hbt;

import org.newdawn.slick.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 7:56 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTCompound extends HBTTag implements Iterable<HBTTag> {
private final ArrayList<HBTTag> data=new ArrayList<HBTTag>();

public HBTCompound(String name) {
	super(name);
}

public void addTag(HBTTag tag) {
	boolean hasAlready=false;
	for (HBTTag tag1 : data) {
		if (tag1.getName().equals(tag.getName())) hasAlready=true;
	}
	if (hasAlready) try {
		HBTTag current=getTag(tag.getName());
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

public HBTTag getTag(String name) throws TagNotFoundException {
	if (name.contains(".")) {
		String[] parts=name.split("\\.");
		HBTTag parent=this;
		for (String s : parts) {
			parent=((HBTCompound) parent).getTag(s);
		}
		return parent;
	} else {
		for (HBTTag tag : this) {
			if (tag.getName().equals(name)) {
				return tag;
			}
		}
		throw new TagNotFoundException(name);
	}
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
	StringBuilder builder=new StringBuilder();
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

public class TagNotFoundException extends Exception {
	public TagNotFoundException(String name) {
		super(name);
	}
}
}
