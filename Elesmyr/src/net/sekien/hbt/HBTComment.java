package net.sekien.hbt;

/**
 * Created with IntelliJ IDEA. User: matt Date: 30/03/13 Time: 1:38 PM To change this template use File | Settings |
 * File Templates.
 */
public class HBTComment extends HBTTag {
public HBTComment(String name) {
	super(name);
}

@Override
public String toString() {
	return "//"+getName();
}
}
