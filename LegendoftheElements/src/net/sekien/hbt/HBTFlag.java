package net.sekien.hbt;

import net.sekien.elesmyr.Element;
import org.newdawn.slick.util.Log;

/**
 * Created with IntelliJ IDEA. User: matt Date: 30/03/13 Time: 2:12 PM To change this template use File | Settings |
 * File Templates.
 */
public class HBTFlag extends HBTTag {
private static final String[] VALUES = {
		                                       "TRUE",
		                                       "FALSE",
		                                       "NEUTRAL",
		                                       "EARTH",
		                                       "WATER",
		                                       "FIRE",
		                                       "AIR",
		                                       "VOID"
};
private byte data;

public HBTFlag(String name, byte data) {
	super(name);
	this.data = data;
}

public HBTFlag(String name, String data) {
	super(name);
	this.data = stringToByte(data);
}

private byte stringToByte(String data) {
	for (byte i = 0; i < VALUES.length; i++) {
		if (VALUES[i].equals(data)) {
			return i;
		}
	}
	Log.error("Unrecognised HBTFlag value '"+data+"'");
	return 0;
}

@Override
public String toString() {
	return "flag "+getName()+" = "+VALUES[data];
}

public byte getData() {
	return data;
}

public boolean isTrue() {
	return VALUES[data].equals("TRUE");
}

public Element asElement() {
	if (data==2) return Element.NEUTRAL;
	else if (data==3) return Element.EARTH;
	else if (data==4) return Element.WATER;
	else if (data==5) return Element.FIRE;
	else if (data==6) return Element.AIR;
	else if (data==7) return Element.VOID;
	else {
		Log.error("HBTFlag.asElement called on "+VALUES[data]);
		return Element.NEUTRAL;
	}
}
}
