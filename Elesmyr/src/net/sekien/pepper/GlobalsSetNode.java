package net.sekien.pepper;

import net.sekien.elesmyr.system.Globals;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 7:01 PM To change this template use File | Settings |
 * File Templates.
 */
public class GlobalsSetNode extends MultiChoiceButtonNode {
private String setting;
private String[] inOptions;

@Override
protected void onSelect(int sel) {
	Globals.set(setting, inOptions[sel]);
}

public GlobalsSetNode(String name, String message, String setting, String[] dispOptions, String[] inOptions) {
	super(name, message, dispOptions);
	this.setting = setting;
	this.inOptions = inOptions;
}
}
