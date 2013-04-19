package net.sekien.hbt;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 9:05 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTFloat extends HBTTag {
private float data;

public HBTFloat(String name, float data) {
	super(name);
	this.data = data;
}

@Override
public String toString() {
	return "float "+getName()+" = "+data;
}

public float getData() {
	return data;
}
}
