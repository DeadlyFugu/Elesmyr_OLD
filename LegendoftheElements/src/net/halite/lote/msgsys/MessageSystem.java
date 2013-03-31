package net.halite.lote.msgsys;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import net.halite.lote.GameElement;
import net.halite.lote.Save;
import net.halite.lote.system.GameClient;
import net.halite.lote.system.GameServer;
import net.halite.lote.system.Globals;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
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
	if (fastLink&&netServer.getConnections().length>0) {
		msg.addConnection(netServer.getConnections()[netServer.getConnections().length-1]);
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
		netServer.sendToUDP(connection, msg);
	else
		netServer.sendToTCP(connection, msg);
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

public static void initialise(GameClient client, boolean server, InetAddress connect, Save save) throws Exception {
	if (SERVER)
		startServer(save);
	if (CLIENT)
		startClient(connect);
	MessageSystem.client=client;
	//MessageSystem.server=server; //Set in startServer(Save);
	if (CLIENT&&SERVER) {
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

public static void startClient(InetAddress address) throws IOException {
	netClient=new Client(8192, 4096);
	netClient.start();
	netClient.connect(5000, "localhost", 37020, 37021);
	netClient.getKryo().register(Message.class);
	netClient.addListener(new Listener() {
		public void received(Connection connection, Object object) {
			//if (! (object instanceof Message) || !((Message) object).getName().equals("move"))
			//	Log.info("Client received: "+object.toString());
			if (object instanceof Message) {
				((Message) object).addConnection(connection);
				MessageSystem.receiveClient((Message) object);
			} else if (!(object instanceof com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive)) {
				Log.warn("CLIENT: Ignored message, unrecognised type "+object.getClass().getName()+", toString: "+object.toString());
			}
		}
	});
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
	netServer.getKryo().register(Message.class);
	netServer.addListener(new Listener() {
		public void received(Connection connection, Object object) {
			try {
				if (object instanceof Message) {
					((Message) object).addConnection(connection);
					MessageSystem.receiveServer((Message) object);
				} else if (!(object instanceof com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive)) {
					Log.warn("SERVER: Ignored unrecognised message type: "+object.getClass().getName()+", from: "+connection.getID()+" toString: "+object.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				MessageSystem.sendClient(null, connection, new Message("CLIENT.chat", "Internal error occured processing "+object.toString()), false);
				MessageSystem.sendClient(null, connection, new Message("CLIENT.chat", "Error: "+e.toString()), false);
			}
		}
	});
}

public static void close() {
	if (CLIENT) {
		if (server==null)
			MessageSystem.sendServer(null, new Message("SERVER.close", ""), false);
		netClient.close();
	}
	if (SERVER) {
		server.save();
		server.broadcastKill();
		netServer.close();
	}
}

public static boolean clientConnected() {return netClient.isConnected();}

public static Connection[] getConnections() {
	return netServer.getConnections();
}
}
