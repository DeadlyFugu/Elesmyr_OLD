package net.halite.lote.msgsys;

import net.halite.hbt.HBTCompound;
import net.halite.hbt.HBTString;
import net.halite.lote.GameElement;

public class Message {
private String target, name;
private HBTCompound data;
private Object connection;
private String sender;
private boolean serverBound=true;

@Deprecated public Message(String name, String data) {
	int splitIndex=name.lastIndexOf(".");
	if (splitIndex==-1) {
		new Exception("Wrongly formatted Message name. It needs atleast one period.").printStackTrace();
		target=name="none";
		data=data;
	} else {
		this.target=name.substring(0, splitIndex);
		this.name=name.substring(splitIndex+1);
		HBTCompound p = new HBTCompound("p");
		p.addTag(new HBTString("s",data));
		this.data=p;
	}
}

public Message(String name, HBTCompound data) {
	int splitIndex=name.lastIndexOf(".");
	if (splitIndex==-1) {
		new Exception("Wrongly formatted Message name. It needs atleast one period.").printStackTrace();
		target=name="none";
		data=data;
	} else {
		this.target=name.substring(0, splitIndex);
		this.name=name.substring(splitIndex+1);
		this.data=data;
	}
}

public Message() {
}

public String getTarget() {
	return target;
}

public String getName() {
	return name;
}

public String getDataStr() {
	return data.getString("s","ERROR");
}

public HBTCompound getData() {
	return data;
}

public String toString() {
	return sender+" to "+target+" "+name+"("+data+")";
}

public void addConnection(Connection connection) {
	this.connection=connection;
}

public Connection getConnection() {
	return (Connection) connection;
}

public void reply(String name, String data, GameElement sender) {
	if (sender!=null) {
		if (!serverBound)
			MessageSystem.sendServer(sender, new Message(name, data), false);
	} else if (connection!=null) {
		if (serverBound)
			MessageSystem.sendClient(sender, ((Connection) connection).getID(), new Message(name, data), false);
		else
			MessageSystem.sendServer(sender, new Message(name, data), false);
	} else if (!serverBound) {
		MessageSystem.sendServer(sender, new Message(name, data), false);
	}
}

public void setServerBound(boolean bound) {
	serverBound=bound;
}

public void setSender(String sender) {
	this.sender=sender;
}

public String getSender() {
	return sender;
}
}
