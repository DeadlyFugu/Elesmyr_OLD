/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.hbt;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 7:57 AM To change this template use File | Settings |
 * File Templates.
 */
public abstract class HBTTag {
private String name;

public HBTTag(String name) {
	this.name = name;
}

public String getName() {
	return name;
}

public abstract HBTTag deepClone();

public void setName(String name) {
	this.name = name;
}
}
