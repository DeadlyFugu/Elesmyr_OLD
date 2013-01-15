package net.halitesoft.lote;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.system.GameServer;

import com.esotericsoftware.kryonet.Connection;

public class MessageSystem {
	private static HashMap<String,GameElement> clientReceivers;
	private static HashMap<String,GameElement> serverReceivers;
	public static GameClient client;
	public static GameServer server;
	public static boolean fastLink=false;
	private static ConcurrentLinkedQueue<Message> serverMsgQueue;
	private static ConcurrentLinkedQueue<Message> clientMsgQueue;
	public static void initialise(GameClient client, GameServer server) {
		MessageSystem.client=client;
		MessageSystem.server=server;
		if (client!=null && server!=null) {
			fastLink=true;
			System.out.println("nonNetLink");
		} else {
			fastLink=false;
		}
		clientReceivers=new HashMap<String,GameElement>();
		serverReceivers=new HashMap<String,GameElement>();
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
			server.sendToUDP(connection,msg);
		else
			server.sendToTCP(connection,msg);
	}
	
	public static void sendClient(GameElement sender, Connection connection, Message msg, boolean udp) {
		sendClient(sender, connection.getID(),msg,udp);
	}
	
	public static void sendClient(GameElement sender, ArrayList<Connection> connections, Message msg, boolean udp) {
		for (Connection c : connections) {
			sendClient(sender, c,msg,udp);
		}
	}
	
	public static void registerReceiverClient(GameElement receiver) {
		clientReceivers.put(receiver.getReceiverName(),receiver);
	}
	
	public static void registerReceiverServer(GameElement receiver) {
		serverReceivers.put(receiver.getReceiverName(),receiver);
	}
	
	public static void receiveServer(Message msg) {
		//System.out.println("Server received "+msg);
		msg.setServerBound(true);
		serverMsgQueue.add(msg);
	}
	
	public static void receiveClient(Message msg) {
		//System.out.println("Client received "+msg);
		msg.setServerBound(false);
		clientMsgQueue.add(msg);
	}
	
	public static void receiveMessageServer() {
		for (Message msg : serverMsgQueue) {
			if (serverReceivers.containsKey(msg.getTarget())) {
				serverReceivers.get(msg.getTarget()).receiveMessage(msg, server);
			} else {
				server.receiveMessage(msg);
			}
		}
		serverMsgQueue.clear();
	}
	
	public static void receiveMessageClient() {
		for (Message msg : clientMsgQueue) {
			if (clientReceivers.containsKey(msg.getTarget())) {
				clientReceivers.get(msg.getTarget()).receiveMessage(msg, client);
			} else {
				client.receiveMessage(msg);
			}
		}
		clientMsgQueue.clear();
	}
}
