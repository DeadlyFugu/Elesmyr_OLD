package net.halite.lote.msgsys;

import com.esotericsoftware.minlog.Log;
import net.halite.lote.GameElement;
import net.halite.lote.Save;
import net.halite.lote.system.GameClient;
import net.halite.lote.system.GameServer;
import net.halite.lote.system.Globals;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageSystem {
public static boolean CLIENT;
public static boolean SERVER;
private static HashMap<String, GameElement> clientReceivers;
private static HashMap<String, GameElement> serverReceivers;
public static GameClient client;
public static GameServer server;
public static boolean fastLink=false;
private static ConcurrentLinkedQueue<Message> serverMsgQueue;
private static ConcurrentLinkedQueue<Message> clientMsgQueue;
private static Client netClient;
private static Server netServer;

public static void sendServer(GameElement sender, Message msg, boolean udp) {
	if (sender!=null)
		msg.setSender(sender.getReceiverName());
	if (fastLink&&netServer.getConnections().size()>0) {
		msg.addConnection(netServer.getConnections().get(netServer.getConnections().size()-1));
		receiveServer(msg);
	} else if (udp)
		netClient.sendUDP(msg);
	else
		netClient.sendTCP(msg);
}

public static void sendClient(GameElement sender, int connection, Message msg, boolean udp) {
	if (sender!=null)
		msg.setSender(sender.getReceiverName());
	if (fastLink&&connection==1)
		receiveClient(msg);
	else if (udp)
		netServer.sendUDP(connection, msg);
	else
		netServer.sendTCP(connection, msg);
}

public static void sendClient(GameElement sender, Connection connection, Message msg, boolean udp) {
	sendClient(sender, connection.getID(), msg, udp);
}

public static void sendClient(GameElement sender, List<Connection> connections, Message msg, boolean udp) {
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

public static void initialise(GameClient client, boolean server, InetAddress connect, Save save) throws Exception {
	if (SERVER)
		startServer(save);
	if (CLIENT)
		startClient(connect);
	MessageSystem.client=client;
	//MessageSystem.server=server; //Set in startServer(Save);
	if (CLIENT&&SERVER) {
		//fastLink=true;
		//Log.info("Fastlink established");
	} else {
		fastLink=false;
	}
	clientReceivers=new HashMap<String, GameElement>();
	serverReceivers=new HashMap<String, GameElement>();
	clientMsgQueue=new ConcurrentLinkedQueue<Message>();
	serverMsgQueue=new ConcurrentLinkedQueue<Message>();
}

public static void startClient(InetAddress address) throws IOException {
	netClient=new Client();
	netClient.start();
	netClient.connect(5000, address, 37020, 37021);
}

public static void startServer(Save save) throws Exception {
	server=new GameServer(save, client==null?Globals.get("name", "Player"):"");
	netServer=new Server();
	netServer.start();
	try {
		netServer.bind(37020, 37021);
	} catch (java.net.BindException be) { //For some reason, I can't directly throw a BindException.
		throw new Exception("__BIND_EXCEPTION");
	}
}

public static void close() {
	if (CLIENT) {
		if (server==null)
			MessageSystem.sendServer(null, new Message("SERVER.close", ""), false);
		try {
		netClient.close();
		} catch (IOException e) {
			//TODO: handle IOException.
		}
	}
	if (SERVER) {
		server.save();
		server.broadcastKill();
		netServer.close();
	}
}

public static boolean clientConnected() {return netClient.isConnected();}

public static List<Connection> getConnections() {
	return netServer.getConnections();
}
}
