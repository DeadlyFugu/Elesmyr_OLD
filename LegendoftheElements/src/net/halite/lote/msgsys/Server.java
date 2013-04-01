package net.halite.lote.msgsys;

import com.esotericsoftware.minlog.Log;
import net.halite.hbt.HBTCompound;
import net.halite.lote.system.Main;

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
private boolean running = true;

public List<Connection> getConnections() {
	return connections;
}

public void sendUDP(int connection, Message msg) {
	try {
		connections.get(connection).sendUDP(msg);
	} catch (Exception e) {
		if (running) Main.handleError(e);
	}
}

public void sendTCP(int connection, Message msg) {
	try {
	connections.get(connection).sendTCP(msg);
	} catch (Exception e) {
		if (running) Main.handleError(e);
	}
}

public void start() {
	connections = new ArrayList<Connection>();
}

public void bind(int port, int port2) throws BindException {
	try {
		serverSocket = new ServerSocket(port);
		Log.info("msgsys", "Server bound to port "+port);
	}
	catch (IOException e) {
		Log.error("msgsys","Could not listen on port: "+port);
		throw new BindException();
	}

	new Thread() {
		public void run() {
			int i = 0;
			while (running) {
				Socket clientSocket = null;
				try {
					clientSocket = serverSocket.accept();
					Connection connection = new Connection(i,clientSocket);
					if (connections.size()==0 && MessageSystem.CLIENT) {
						connection.setFastlinked();
						MessageSystem.setFastlink(connection);
					}
					connections.add(connection);
					createListenerThread(connection);
					Log.info("msgsys", "Server connected to "+clientSocket.toString());
					i++;
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

private void createListenerThread(final Connection connection) {
	new Thread() {
		public void run() {
			while (running) {
				try {
					received(connection,connection.readMsg());
				} catch (HBTCompound.TagNotFoundException e) {
					Log.error("msgsys","Badly formed message received.");
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

public void received(Connection connection, Message msg) {
	msg.addConnection(connection);
	MessageSystem.receiveServer(msg);
}

public void stop() {
	running=false;
}

public void close() throws IOException {
	for (Connection c : connections) {
		c.close();
	}
	serverSocket.close();
}
}
