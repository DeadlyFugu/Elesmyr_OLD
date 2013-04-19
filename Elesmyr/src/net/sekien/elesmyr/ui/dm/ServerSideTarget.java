package net.sekien.elesmyr.ui.dm;

import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.msgsys.MessageReceiver;
import net.sekien.elesmyr.msgsys.MessageSystem;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTFlag;
import net.sekien.hbt.HBTString;
import net.sekien.hbt.HBTTag;

/**
 * Created with IntelliJ IDEA. User: matt Date: 19/04/13 Time: 4:13 PM To change this template use File | Settings |
 * File Templates.
 */
public class ServerSideTarget implements DevModeTarget, MessageReceiver {

private HBTCompound cached = new HBTCompound("DMRcache");

private String target, get, set;

private boolean registered = false;

public ServerSideTarget(String target) {
	this(target, "_hbt", "_hbtSET");
}

public ServerSideTarget(String target, String get, String set) {
	this.target = target;
	this.get = get;
	this.set = set;
}

public void setTarget(String target) {
	this.target = target;
}

@Override
public void set(HBTCompound list, String subTarget, GameClient client) {
	MessageSystem.sendServer(this, new Message(target+"."+set, list), false);
	cached = (HBTCompound) list.deepClone();
}

@Override
public HBTCompound getList(GameClient client) {
	if (!registered)
		MessageSystem.registerReceiverClient(this);
	MessageSystem.sendServer(this, new Message(target+"."+get, new HBTCompound("p", new HBTTag[]{new HBTString("receiver", this.getReceiverName()), new HBTFlag("full", "TRUE")})), false);
	return cached;
}

@Override
public void receiveMessage(Message msg, MessageEndPoint receiver) {
	System.out.println(msg);
	if (msg.getName().equals("hbtResponse")) {
		//Swap cached's contents with the responses.
		cached.getData().clear();
		for (HBTTag tag : msg.getData()) {
			cached.addTag(tag);
		}
	}
}

@Override
public String getReceiverName() {
	return "_DMR"+Integer.toHexString(this.hashCode());
}

@Override
public void fromHBT(HBTCompound tag) {
}

@Override
public HBTCompound toHBT(boolean msg) {
	return null;
}
}
