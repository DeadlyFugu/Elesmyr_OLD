/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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

@Override
public HBTTag deepClone() {
	return new HBTComment(getName());
}
}
