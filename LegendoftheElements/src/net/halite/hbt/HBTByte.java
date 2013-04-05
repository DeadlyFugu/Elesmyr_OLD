package net.halite.hbt;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 9:00 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTByte extends HBTTag {
private byte data;

public HBTByte(String name, byte data) {
	super(name);
	this.data=data;
}

@Override
public String toString() {
	return "byte "+getName()+" = "+String.format("0x%02X", data);
}

public byte getData() {
	return data;
}
}
