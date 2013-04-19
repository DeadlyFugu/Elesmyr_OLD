package net.sekien.hbt;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 9:08 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTString extends HBTTag {
private String data;

public HBTString(String name, String data) {
	super(name);
	this.data = data;
}

@Override
public String toString() {
	return "string "+getName()+" = \""+data+"\"";
}

public String getData() {
	return data;
}

@Override
public HBTTag deepClone() {
	return new HBTString(getName(), data);
}
}
