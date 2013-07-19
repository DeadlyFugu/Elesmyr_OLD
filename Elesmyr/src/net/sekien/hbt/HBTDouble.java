/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.hbt;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 9:06 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTDouble extends HBTTag {
private double data;

public HBTDouble(String name, double data) {
	super(name);
	this.data = data;
}

@Override
public String toString() {
	return "double "+getName()+" = "+data;
}

public double getData() {
	return data;
}

@Override
public HBTTag deepClone() {
	return new HBTDouble(getName(), data);
}
}
