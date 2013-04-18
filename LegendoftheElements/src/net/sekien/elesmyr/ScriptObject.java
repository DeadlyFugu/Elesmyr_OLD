package net.sekien.elesmyr;

import groovy.lang.GroovyObject;
import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageReceiver;

public class ScriptObject {
private String name;
private Object master;
private GroovyObject go;

public ScriptObject(String name, GameElement master) {
	this.name = name;
	this.master = master;
}

public ScriptObject(String name, MessageReceiver master) {
	this.name = name;
	this.master = master;
	this.go = ScriptRunner.get(name);
}

public void call(String func, Object[] args) {
	go.invokeMethod(func, args);
}

public void receiveMessage(Message msg, MessageReceiver receiver) {
	if (msg.getName().equals("placeholder")) {
		//placeholdin'
	} else {
		call("receive", new Object[]{msg, receiver});
	}
}
}
