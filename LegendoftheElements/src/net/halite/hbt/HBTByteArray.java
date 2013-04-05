package net.halite.hbt;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 9:12 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTByteArray extends HBTTag {
private byte[] data;

public HBTByteArray(String name, byte[] data) {
	super(name);
	this.data=data;
}

@Override
public String toString() {
	StringBuilder builder=new StringBuilder();
	builder.append("data "+getName()+" = ");
	for (int i=0; i<data.length; i++) {
		builder.append(String.format("%02X", data[i]));
	}
	return builder.toString();
}

public byte[] getData() {
	return data;
}
}
