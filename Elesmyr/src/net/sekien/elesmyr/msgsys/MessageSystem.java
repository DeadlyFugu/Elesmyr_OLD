/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.msgsys;

import com.esotericsoftware.minlog.Log;
import net.sekien.elesmyr.Save;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.system.Globals;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTTools;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageSystem {
	public static boolean CLIENT;
	public static boolean SERVER;
	private static HashMap<String, MessageReceiver> clientReceivers;
	private static HashMap<String, MessageReceiver> serverReceivers;
	public static GameClient client;
	public static GameServer server;
	private static ConcurrentLinkedQueue<Message> serverMsgQueue;
	private static ConcurrentLinkedQueue<Message> clientMsgQueue;
	private static Client netClient;
	private static Server netServer;
	public static boolean fastLink = false;
	private static int fastlinkedID;

	private static int si, so, ci, co;
	private static long sec_timer;
	public static String netstat = "";

	public static void sendServer(MessageReceiver sender, Message msg, boolean udp) {
		co++;
		if (sender != null)
			msg.setSender(sender.getReceiverName());
		if (fastLink && netServer.getConnections().size() > 0) {
			msg.addConnection(netServer.getConnections().get(0));
			receiveServer(msg);
		} else if (udp)
			netClient.sendUDP(msg);
		else
			netClient.sendTCP(msg);
	}

	public static void sendClient(MessageReceiver sender, int connection, Message msg, boolean udp) {
		so++;
		if (sender != null)
			msg.setSender(sender.getReceiverName());
		if (fastLink && connection == fastlinkedID)
			receiveClient(msg);
		else if (udp)
			netServer.sendUDP(connection, msg);
		else if (SERVER)
			netServer.sendTCP(connection, msg);
		else
			Log.warn("Client tried to send a message to other clients.");
	}

	public static void sendClient(MessageReceiver sender, Connection connection, Message msg, boolean udp) {
		sendClient(sender, connection.getID(), msg, udp);
	}

	public static void sendClient(MessageReceiver sender, List<Connection> connections, Message msg, boolean udp) {
		for (Connection c : connections) {
			sendClient(sender, c, msg, udp);
		}
	}

	public static void registerReceiverClient(MessageReceiver receiver) {
		if (clientReceivers == null) lazyInit();
		clientReceivers.put(receiver.getReceiverName(), receiver);
	}

	public static void registerReceiverServer(MessageReceiver receiver) {
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
		si += serverMsgQueue.size();
		for (Message msg : serverMsgQueue) {
			try {
				if (Globals.get("printAllMsg", false) || Globals.get("printMsg", false) && !msg.getName().equals("move") && !msg.getName().equals("pickupAt") && !msg.getName().equals("time"))
					Log.info("Server received "+msg);
				if (serverReceivers.containsKey(msg.getTarget())) {
					if (msg.getName().equals("_info"))
						MessageSystem.sendClient(null, msg.getConnection(), new Message("CLIENT.chat", HBTTools.msgString("msg", serverReceivers.get(msg.getTarget()).toString())), false);
					else if (msg.getName().equals("_hbt"))
						MessageSystem.sendClient(null, msg.getConnection(), new Message(msg.getData().getString("receiver", "CLIENT")+".hbtResponse", serverReceivers.get(msg.getTarget()).toHBT(msg.getData().getFlag("full", "FALSE").isTrue())), false);
					else if (msg.getName().equals("_hbtSET"))
						serverReceivers.get(msg.getTarget()).fromHBT(msg.getData());
					else serverReceivers.get(msg.getTarget()).receiveMessage(msg, server);
				} else {
					server.receiveMessage(msg);
				}
			} catch (Exception e) {
				Log.error("Exception caught receiving a message.\n_MSG:\n"+msg+"\n");
				Log.error("_EXCEPTION:", e);
			}
		}
		serverMsgQueue.clear();
	}

	public static void receiveMessageClient() {
		ci += clientMsgQueue.size();
		while (!clientMsgQueue.isEmpty()) {
			Message msg = clientMsgQueue.poll();
			if (Globals.get("printAllMsg", false) || Globals.get("printMsg", false) && !msg.getName().equals("move") && !msg.getName().equals("pickupAt") && !msg.getName().equals("time"))
				Log.info("Client received "+msg);
			try {
				if (clientReceivers.containsKey(msg.getTarget())) {
					clientReceivers.get(msg.getTarget()).receiveMessage(msg, client);
				} else {
					client.receiveMessage(msg);
				}
			} catch (Exception e) {
				Log.error("Exception caught receiving a message.\n_MSG:\n"+msg+"\n");
				Log.error("_EXCEPTION:", e);
			}
		}
		if (System.currentTimeMillis() > sec_timer+1000) {
			sec_timer = System.currentTimeMillis();
			if (server != null) {
				netstat =
				"sin  "+si+"\n"+
				"sout "+so+"\n";
			} else {netstat = "";}
			netstat +=
			"cin  "+ci+"\n"+
			"cout "+co+"\n";
			si = so = ci = co = 0;
		}
	}

	public static void initialise(GameClient client, boolean server, InetAddress connect, Save save) throws Exception {
		lazyInit();
		if (SERVER)
			startServer(save);
		if (CLIENT)
			startClient(connect);
		MessageSystem.client = client;
		//MessageSystem.server=server; //Set in startServer(Save);
		if (CLIENT && SERVER && Globals.get("allowFL", true)) {
			fastLink = true;
		} else {
			fastLink = false;
		}
	}

	private static void lazyInit() {
		if (clientReceivers == null) {
			clientReceivers = new HashMap<String, MessageReceiver>();
			serverReceivers = new HashMap<String, MessageReceiver>();
			clientMsgQueue = new ConcurrentLinkedQueue<Message>();
			serverMsgQueue = new ConcurrentLinkedQueue<Message>();
		}
	}

	public static void startClient(InetAddress address) throws IOException {
		netClient = new Client();
		netClient.start();
		netClient.connect(5000, address, 37020, 37021);
	}

	public static void startServer(Save save) throws Exception {
		server = new GameServer(save, client == null?Globals.get("name", "Player"):"");
		netServer = new Server();
		netServer.start();
		try {
			netServer.bind(37020, 37021);
		} catch (java.net.BindException be) { //For some reason, I can't directly throw a BindException.
			throw new Exception("__BIND_EXCEPTION");
		}
	}

	public static void close() {
		if (CLIENT)
			netClient.stop();
		if (SERVER)
			netServer.stop();
		if (CLIENT) {
			if (server == null)
				MessageSystem.sendServer(null, new Message("SERVER.close", new HBTCompound("p")), false);
			try {
				netClient.close();
			} catch (IOException e) {
				Log.error("Error closing client.");
				e.printStackTrace();
			}
		}
		if (SERVER) {
			server.save();
			server.broadcastKill();
			try {
				netServer.close();
			} catch (IOException e) {
				Log.error("Error closing client.");
				e.printStackTrace();
			}
		}
	}

	public static boolean clientConnected() {return netClient.isConnected();}

	public static List<Connection> getConnections() {
		return netServer.getConnections();
	}

	public static void setFastlink(Connection connection) {
		MessageSystem.fastlinkedID = connection.getID();
	}

	public static void removeConnection(Connection connection) {
		netServer.getConnections().remove(connection);
	}

	public static void removeConnection(int connection) {
		Connection found = null;
		for (Connection c : netServer.getConnections()) {
			if (c.getID() == connection) {
				found = c;
			}
		}
		if (found != null) {
			netServer.getConnections().remove(found);
		} else {
			Log.warn("Could not remove connection "+connection+" because it doesn't exist");
		}
	}

	public static void dbgClientReceivers() {
		System.out.println("clientReceivers = "+clientReceivers);
	}
}
