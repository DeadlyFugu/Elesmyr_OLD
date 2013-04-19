package net.sekien.hbt;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 9:03 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTInt extends HBTTag {
private int data;

public HBTInt(String name, int data) {
	super(name);
	this.data = data;
}

@Override
public String toString() {
	return "int "+getName()+" = "+data;
}

public int getData() {
	return data;
}

@Override
public HBTTag deepClone() {
	return new HBTInt(getName(), data);
}
}