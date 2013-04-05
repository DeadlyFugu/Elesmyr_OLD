package net.halite.hbt;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 9:03 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTShort extends HBTTag {
private short data;

public HBTShort(String name, short data) {
	super(name);
	this.data=data;
}

@Override
public String toString() {
	return "short "+getName()+" = "+data;
}

public short getData() {
	return data;
}
}
