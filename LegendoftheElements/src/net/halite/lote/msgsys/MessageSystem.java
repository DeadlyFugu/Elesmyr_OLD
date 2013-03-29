package net.halite.lote.msgsys;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import net.halite.lote.GameElement;
import net.halite.lote.system.GameClient;
import net.halite.lote.system.GameServer;
import net.halite.lote.system.Globals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageSystem {
private static HashMap<String, GameElement> clientReceivers;
private static HashMap<String, GameElement> serverReceivers;
public static GameClient client;
public static GameServer server;
public static boolean fastLink=false;
private static ConcurrentLinkedQueue<Message> serverMsgQueue;
private static ConcurrentLinkedQueue<Message> clientMsgQueue;

public static void initialise(GameClient client, GameServer server) {
	MessageSystem.client=client;
	MessageSystem.server=server;
	if (client!=null&&server!=null) {
		fastLink=true;
		Log.info("Fastlink established");
	} else {
		fastLink=false;
	}
	clientReceivers=new HashMap<String, GameElement>();
	serverReceivers=new HashMap<String, GameElement>();
	clientMsgQueue=new ConcurrentLinkedQueue<Message>();
	serverMsgQueue=new ConcurrentLinkedQueue<Message>();
}

public static void sendServer(GameElement sender, Message msg, boolean udp) {
	if (sender!=null)
		msg.setSender(sender.getReceiverName());
	if (fastLink&&server.getConnections().length>0) {
		msg.addConnection(server.getConnections()[server.getConnections().length-1]);
		receiveServer(msg);
	} else if (udp)
		client.client.sendUDP(msg);
	else
		client.client.sendTCP(msg);
}

public static void sendClient(GameElement sender, int connection, Message msg, boolean udp) {
	if (sender!=null)
		msg.setSender(sender.getReceiverName());
	if (fastLink&&connection==1)
		receiveClient(msg);
	else if (udp)
		server.sendToUDP(connection, msg);
	else
		server.sendToTCP(connection, msg);
}

public static void sendClient(GameElement sender, Connection connection, Message msg, boolean udp) {
	sendClient(sender, connection.getID(), msg, udp);
}

public static void sendClient(GameElement sender, ArrayList<Connection> connections, Message msg, boolean udp) {
	for (Connection c : connections) {
		sendClient(sender, c, msg, udp);
	}
}

public static void registerReceiverClient(GameElement receiver) {
	clientReceivers.put(receiver.getReceiverName(), receiver);
}

public static void registerReceiverServer(GameElement receiver) {
	serverReceivers.put(receiver.getReceiverName(), receiver);
}

public static void receiveServer(Message msg) {
	msg.setServerBound(true);
	serverMsgQueue.add(msg);
}

public static void receiveClient(Message msg) {
	msg.setServerBound(false);
	clientMsgQueue.add(msg);
}

public static void receiveMessageServer() {
	for (Message msg : serverMsgQueue) {
		if (Globals.get("printAllMsg", false)||Globals.get("printMsg", false)&&!msg.getName().equals("move")&&!msg.getName().equals("pickupAt"))
			Log.info("Server received "+msg);
		if (serverReceivers.containsKey(msg.getTarget())) {
			if (msg.getName().equals("_info"))
				MessageSystem.sendClient(null, msg.getConnection(), new Message("CLIENT.chat", serverReceivers.get(msg.getTarget()).toString()), false);
			serverReceivers.get(msg.getTarget()).receiveMessage(msg, server);
		} else {
			server.receiveMessage(msg);
		}
	}
	serverMsgQueue.clear();
}

public static void receiveMessageClient() {
	while (!clientMsgQueue.isEmpty()) {
		Message msg=clientMsgQueue.poll();
		if (Globals.get("printAllMsg", false)||Globals.get("printMsg", false)&&!msg.getName().equals("move")&&!msg.getName().equals("pickupAt"))
			Log.info("Client received "+msg);
		try {
			if (clientReceivers.containsKey(msg.getTarget())) {
				clientReceivers.get(msg.getTarget()).receiveMessage(msg, client);
			} else {
				client.receiveMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
}
