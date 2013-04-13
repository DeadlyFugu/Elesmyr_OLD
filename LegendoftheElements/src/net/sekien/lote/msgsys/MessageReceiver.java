package net.sekien.lote.msgsys;

public interface MessageReceiver {
public boolean receiveMessage(Message msg);

public boolean isServer();
}
