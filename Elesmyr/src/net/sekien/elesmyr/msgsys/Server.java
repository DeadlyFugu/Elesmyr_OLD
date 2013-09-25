/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.msgsys;

import com.esotericsoftware.minlog.Log;
import net.sekien.elesmyr.system.Main;
import net.sekien.hbt.TagNotFoundException;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: matt Date: 1/04/13 Time: 9:25 AM To change this template use File | Settings | File
 * Templates.
 */
public class Server {
private final ArrayList<Connection> connections = new ArrayList<Connection>();
private ServerSocket serverSocket;
private DatagramSocket udpSocket;
private boolean running = true;

public List<Connection> getConnections() {
	return connections;
}

public void sendUDP(int connection, Message msg) {
	try {
		synchronized (connections) {
			connections.get(connection).sendUDP(msg);
		}
	} catch (IndexOutOfBoundsException be) {
		Log.warn("Tried to send a message to disconnected connection "+connection);
	} catch (Exception e) {
		if (running) Main.handleError(e);
	}
}

public void sendTCP(int connection, Message msg) {
	try {
		synchronized (connections) {
			connections.get(connection).sendTCP(msg);
		}
	} catch (IndexOutOfBoundsException be) {
		Log.warn("Tried to send a message to disconnected connection "+connection);
	} catch (Exception e) {
		if (running) Main.handleError(e);
	}
}

public void start() {
}

public void bind(int port, int port2) throws BindException {
	try {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(480);
		udpSocket = new DatagramSocket(port2);
		udpSocket.setSoTimeout(20);
		Log.info("server", "bound to ports TCP="+port+" UDP="+port2);
	} catch (IOException e) {
		Log.error("msgsys", "Could not listen on ports: TCP="+port+" UDP="+port2);
		throw new BindException();
	}

	new Thread() {
		public void run() {
			this.setName("[server] Request listener");
			while (running) {
				try {
					Socket clientSocket = null;
					clientSocket = serverSocket.accept();
					Connection connection = new Connection(connections.size(), clientSocket);
					if (connections.size()==0 && MessageSystem.CLIENT) {
						connection.setFastlinked();
						MessageSystem.setFastlink(connection);
					}
					synchronized (connections) {
						connections.add(connection);
					}
					createListenerThread(connection);
					Log.info("server", "Server connected to "+clientSocket.toString());
				} catch (SocketTimeoutException ignored) {
				} catch (Exception e) {
					if (running) {
						Main.handleCrash(e);
						System.exit(1);
					}
				}
				try {
					DatagramPacket packet = new DatagramPacket(new byte[0], 0);
					udpSocket.receive(packet);
					packet.setData(new byte[]{7, 42, 98, 43, 64, 98});
					udpSocket.send(packet); //TODO: Send actual server info.
				} catch (SocketTimeoutException ignored) {
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
			this.setName("[server] "+connection.toString()+" listener");
			while (running) {
				try {
					received(connection, connection.readMsg());
				} catch (TagNotFoundException e) {
					Log.error("server", "Badly formed message received.");
					e.printStackTrace();
				} catch (NullPointerException npe) {
					break;
				} catch (SocketException e) {
					break;
				} catch (Exception e) {
					if (!connection.isConnected()) {
						break;
					} else if (running) {
						Main.handleCrash(e);
						System.exit(1);
					}
				}
			}
			synchronized (connections) {
				connections.remove(connection);
			}
			try {
				connection.close();
			} catch (IOException e) {
				Main.handleCrash(e);
				System.exit(1);
			}
		}
	}.start();
}

public void received(Connection connection, Message msg) {
	msg.addConnection(connection);
	MessageSystem.receiveServer(msg);
}

public void stop() {
	Log.info("server", "Server stopped");
	running = false;
}

public void close() throws IOException {
	for (Connection c : connections) {
		c.close();
	}
	serverSocket.close();
	udpSocket.close();
}
}
