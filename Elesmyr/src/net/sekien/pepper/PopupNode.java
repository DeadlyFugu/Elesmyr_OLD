/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.pepper;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 2:33 PM To change this template use File | Settings |
 * File Templates.
 */
public abstract class PopupNode extends ActionNode {

private boolean closed = false;

public PopupNode(String name) {
	super(name);
}

public boolean isClosed() {return closed;}

protected void close() {closed = true;}

public abstract boolean receiveActions();
}
