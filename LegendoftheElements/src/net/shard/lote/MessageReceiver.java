package net.shard.lote;

public interface MessageReceiver {
	public boolean receiveMessage(Message msg);
	public boolean isServer();
}
