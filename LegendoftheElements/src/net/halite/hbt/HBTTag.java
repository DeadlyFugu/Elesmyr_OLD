package net.halite.hbt;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 7:57 AM To change this template use File | Settings |
 * File Templates.
 */
public abstract class HBTTag {
private String name;

public HBTTag(String name) {
	this.name=name;
}

public String getName() {
	return name;
}
}
