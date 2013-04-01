package net.halite.lote.msgsys;

import net.halite.hbt.HBTCompound;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA. User: matt Date: 1/04/13 Time: 9:25 AM To change this template use File | Settings | File
 * Templates.
 */
public class Client {
private Connection connection;

public void sendUDP(Message msg) {
	connection.sendUDP(msg);
}

public void sendTCP(Message msg) {
	connection.sendTCP(msg);
}

public void received(Message msg) {
	msg.addConnection(connection);
	MessageSystem.receiveClient(msg);
}

public void start() {
}

public void connect(int i, InetAddress address, int port, int port2) throws IOException {
	try {
		Thread.sleep(2000,0);
	} catch (InterruptedException e) {
		e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	}
	System.out.println(address);
	Socket socket = new Socket(address, port);
	System.out.println("socket created");
	connection = new Connection(-1,socket);
	new Thread() {
		public void run() {
			while (true) {
				try {
					System.out.println("Client waiting for message");
					received(connection.readMsg());
				} catch (HBTCompound.TagNotFoundException e) {
					System.err.println("Badly formed message received.");
					e.printStackTrace();
				} catch (IOException e) {
					System.err.println("IOException occured");
					e.printStackTrace();
					break;
				}
			}
		}
	}.start();
}

public void close() throws IOException {
}

public boolean isConnected() {
	return connection.isConnected();
}
}
