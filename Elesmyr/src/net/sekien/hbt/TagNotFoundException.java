/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.hbt;

/**
 * Created with IntelliJ IDEA. User: matt Date: 8/09/13 Time: 6:35 PM To change this template use File | Settings | File
 * Templates.
 */
public class TagNotFoundException extends RuntimeException {
public TagNotFoundException(String name) {
	super(name);
}
}
