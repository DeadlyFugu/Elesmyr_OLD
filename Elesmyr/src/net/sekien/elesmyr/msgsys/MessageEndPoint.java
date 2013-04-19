package net.sekien.elesmyr.msgsys;

public interface MessageEndPoint {
public boolean receiveMessage(Message msg);

public boolean isServer();
}
