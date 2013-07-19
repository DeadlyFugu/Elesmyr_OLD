/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.msgsys;

import java.net.InetAddress;

/**
 * Created with IntelliJ IDEA. User: matt Date: 27/04/13 Time: 3:29 PM To change this template use File | Settings |
 * File Templates.
 */
public class PotentialHost {
public final InetAddress address;
public final String name;
public int time;

PotentialHost(InetAddress address, String name, int time) {
	this.address = address;
	this.name = name;
	this.time = time;
}

@Override
public String toString() {
	return this.name+" @"+address+" time="+time+"ms";
}

@Override
public boolean equals(Object other) {
	if (other instanceof PotentialHost)
		return ((PotentialHost) other).address.equals(this.address);
	return false;
}
}
