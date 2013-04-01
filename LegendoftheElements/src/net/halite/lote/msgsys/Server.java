package net.halite.lote.msgsys;

import net.halite.hbt.HBTCompound;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: matt Date: 1/04/13 Time: 9:25 AM To change this template use File | Settings | File
 * Templates.
 */
public class Server {
private ArrayList<Connection> connections;
private ServerSocket serverSocket;

public List<Connection> getConnections() {
	return connections;
}

public void sendUDP(int connection, Message msg) {
	connections.get(connection).sendUDP(msg);
}

public void sendTCP(int connection, Message msg) {
	connections.get(connection).sendTCP(msg);}

public void start() {
	connections = new ArrayList<Connection>();
}

public void bind(int port, int port2) throws BindException {
	try {
		serverSocket = new ServerSocket(port);
	}
	catch (IOException e) {
		System.out.println("Could not listen on port: "+port);
		System.exit(-1);
	}

	new Thread() {
		public void run() {
			int i = 0;
			while (true) {
				Socket clientSocket = null;
				try {
					System.out.println("Server waiting for connection");
					clientSocket = serverSocket.accept();
					Connection connection = new Connection(i,clientSocket);
					connections.add(connection);
					createListenerThread(connection);
					i++;
				} catch (IOException e) {
					System.out.println("Accept failed.");
					System.exit(-1);
				}
			}
		}
	}.start();
}

private void createListenerThread(final Connection connection) {
	new Thread() {
		public void run() {
			while (connection.isConnected()) {
				try {
					System.out.println("Server waiting for message");
					received(connection,connection.readMsg());
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

public void received(Connection connection, Message msg) {
	msg.addConnection(connection);
	MessageSystem.receiveServer(msg);
}

public void close() {
	//To change body of created methods use File | Settings | File Templates.
}
}
