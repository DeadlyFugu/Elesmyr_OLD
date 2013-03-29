package net.halite.lote.msgsys;

public interface MessageReceiver {
public boolean receiveMessage(Message msg);

public boolean isServer();
}
