/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr;

import groovy.lang.GroovyObject;
import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;

public class ScriptObject {
private String name;
private Object master;
private GroovyObject go;

public ScriptObject(String name, GameElement master) {
	this.name = name;
	this.master = master;
}

public ScriptObject(String name, MessageEndPoint master) {
	this.name = name;
	this.master = master;
	this.go = ScriptRunner.get(name);
}

public void call(String func, Object[] args) {
	go.invokeMethod(func, args);
}

public void receiveMessage(Message msg, MessageEndPoint receiver) {
	if (msg.getName().equals("placeholder")) {
		//placeholdin'
	} else {
		call("receive", new Object[]{msg, receiver});
	}
}
}
