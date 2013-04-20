package net.sekien.pepper;

import net.sekien.elesmyr.system.Globals;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 9:17 PM To change this template use File | Settings |
 * File Templates.
 */
public class GlobalsEnumNode extends EnumNode {

private String setting;

public <E extends Enum<E>> GlobalsEnumNode(String name, String message, String setting, String def, boolean ordinal, Class<E> options) {
	super(name, message, ordinal, options);
	this.setting = setting;
}

@Override
protected void onSelect(int sel) {
	if (ordinal) {
		Globals.set(setting, ""+sel);
	} else {
		Globals.set(setting, ""+options.getEnumConstants()[sel]);
	}
}
}
