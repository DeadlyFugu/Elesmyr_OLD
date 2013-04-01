package net.halite.lote.msgsys;

import com.esotericsoftware.minlog.Log;
import net.halite.hbt.HBTCompound;
import net.halite.lote.system.Main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA. User: matt Date: 1/04/13 Time: 9:25 AM To change this template use File | Settings | File
 * Templates.
 */
public class Client {
private Connection connection;
private boolean running = true;

public void sendUDP(Message msg) {
	try {
		connection.sendUDP(msg);
	} catch (Exception e) {
		if (running) Main.handleError(e);
	}
}

public void sendTCP(Message msg) {
	try {
		connection.sendTCP(msg);
	} catch (Exception e) {
		if (running) Main.handleError(e);
	}
}

public void received(Message msg) {
	msg.addConnection(connection);
	MessageSystem.receiveClient(msg);
}

public void start() {
}

public void connect(int i, InetAddress address, int port, int port2) throws IOException {
	Socket socket = new Socket(address, port);
	Log.info("msgsys", "Client connected to "+socket.toString());
	connection = new Connection(-1,socket);
	new Thread() {
		public void run() {
			while (running) {
				try {
					received(connection.readMsg());
				} catch (HBTCompound.TagNotFoundException e) {
					Log.error("Badly formed message received.");
					e.printStackTrace();
				} catch (Exception e) {
					if (running) {
						Main.handleCrash(e);
						System.exit(1);
					}
				}
			}
		}
	}.start();
}

public void stop() {
	running = false;
}

public void close() throws IOException {
	connection.close();
}

public boolean isConnected() {
	return connection.isConnected();
}
}
