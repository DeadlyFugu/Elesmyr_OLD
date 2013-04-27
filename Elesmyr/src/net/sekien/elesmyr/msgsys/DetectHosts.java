package net.sekien.elesmyr.msgsys;

import com.esotericsoftware.minlog.Log;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: matt Date: 27/04/13 Time: 2:30 PM To change this template use File | Settings |
 * File Templates.
 */
public class DetectHosts {
public static void main(String[] args) {
	Log.info("Scanning for hosts...");
	List<PotentialHost> hosts = new ArrayList<PotentialHost>();
	getHosts(37021, 1000, hosts);
	System.out.println(hosts);
}

public static void getHosts(int port, int timeout, List<PotentialHost> hosts) {
	DatagramSocket socket = null;
	try {
		socket = new DatagramSocket();
		for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
			if (networkInterface.isLoopback() || !networkInterface.isUp()) {
				continue; // Don't want to broadcast to the loopback interface
			}

			for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
				InetAddress broadcast = interfaceAddress.getBroadcast();
				if (broadcast==null) {
					continue;
				}

				// Send the broadcast package!
				try {
					DatagramPacket sendPacket = new DatagramPacket(new byte[0], 0, broadcast, port);
					socket.send(sendPacket);
				} catch (Exception e) {
				}

				Log.debug("Request packet sent to: "+broadcast.getHostAddress()+"; Interface: "+networkInterface.getDisplayName());
			}
		}
		Log.debug("Broadcasted host discovery on port: "+port);
		socket.setSoTimeout(timeout);
		long start = System.currentTimeMillis();
		while (true) {
			DatagramPacket packet = new DatagramPacket(new byte[0], 0);
			try {
				socket.receive(packet);
			} catch (SocketTimeoutException ex) {
				return;
			}
			Log.debug("Discovered server: "+packet.getAddress());
			hosts.add(new PotentialHost(packet.getAddress(), "Name here", (int) (System.currentTimeMillis()-start)));
		}
	} catch (IOException ex) {
		Log.error("Host discovery failed.");
	} finally {
		if (socket!=null) socket.close();
	}
}
}
