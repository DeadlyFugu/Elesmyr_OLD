package net.sekien.pepper;

import net.sekien.elesmyr.system.Globals;

/**
 * Created with IntelliJ IDEA. User: matt Date: 21/04/13 Time: 10:32 AM To change this template use File | Settings |
 * File Templates.
 */
public class GlobalsTextNode extends TextNode {
private String setting;

public GlobalsTextNode(String name, String message, String setting) {
	super(name, message);
	this.setting = setting;
}

@Override
protected void onSelect() {
	Globals.set(setting, StateManager.getTextBoxText(this));
}
}
