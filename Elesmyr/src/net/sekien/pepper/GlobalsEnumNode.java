/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
		selectOnChange = true;
	}

	@Override
	protected void onSelect(int sel) {
		if (ordinal) {
			Globals.set(setting, ""+sel);
		} else {
			Globals.set(setting, ""+options.getEnumConstants()[sel]);
		}
		Globals.save();
	}
}
