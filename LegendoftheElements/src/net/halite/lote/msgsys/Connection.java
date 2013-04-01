package net.halite.lote.msgsys;

import net.halite.hbt.HBTCompound;
import net.halite.hbt.HBTInputStream;
import net.halite.hbt.HBTOutputStream;
import net.halite.hbt.HBTString;

import java.io.IOException;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA. User: matt Date: 1/04/13 Time: 9:23 AM To change this template use File | Settings | File
 * Templates.
 */
public class Connection {
private int id;
private Socket socket = null;
private HBTOutputStream out;
private HBTInputStream in;

public Connection(int id, Socket clientSocket) throws IOException {
	this.socket = clientSocket;
	out = new HBTOutputStream(socket.getOutputStream(), false);
	in = new HBTInputStream(socket.getInputStream(), false);
	this.id=id;
}

public int getID() {
	return id;
}

public boolean isConnected() {
	return socket.isConnected();
}

public void close() throws IOException {
	in.close();
	out.close();
	socket.close();
}

void sendTCP(Message msg) {
	HBTCompound msgc = new HBTCompound("");
	msgc.addTag(new HBTString("t",msg.getTarget()));
	msgc.addTag(new HBTString("n",msg.getName()));
	msgc.addTag(new HBTString("d",msg.getData()));
	try {
	out.write(msgc);
	} catch (IOException e) {
		e.printStackTrace();
	}
}

void sendUDP(Message msg) {
	sendTCP(msg);
}

Message readMsg() throws IOException, HBTCompound.TagNotFoundException {
	HBTCompound msgc = (HBTCompound) in.read();
	return new Message(((HBTString) msgc.getTag("t")).getData()+"."+
			                   ((HBTString) msgc.getTag("n")).getData(),
			                  ((HBTString) msgc.getTag("d")).getData());
}
}
