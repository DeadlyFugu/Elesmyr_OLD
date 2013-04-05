package net.halite.hbt;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 9:04 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTLong extends HBTTag {
private long data;

public HBTLong(String name, long data) {
	super(name);
	this.data=data;
}

@Override
public String toString() {
	return "long "+getName()+" = "+data;
}

public long getData() {
	return data;
}
}
