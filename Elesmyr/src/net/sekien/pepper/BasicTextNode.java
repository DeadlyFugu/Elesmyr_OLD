/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.pepper;

/**
 * Created with IntelliJ IDEA. User: matt Date: 21/04/13 Time: 4:30 PM To change this template use File | Settings |
 * File Templates.
 */
public class BasicTextNode extends TextNode {
@Override
protected void onSelect() {
	//Do nothing
}

public String getValue() {
	return text;
}

protected BasicTextNode(String name, String message) {
	super(name, message);
}
}
