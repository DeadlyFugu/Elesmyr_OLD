/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
