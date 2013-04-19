package net.sekien.elesmyr.msgsys;

public interface MessageReceiver {
public boolean receiveMessage(Message msg);

public boolean isServer();
}
