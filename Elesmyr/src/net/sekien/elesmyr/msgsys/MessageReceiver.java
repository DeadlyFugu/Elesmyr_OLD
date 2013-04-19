package net.sekien.elesmyr.msgsys;

import net.sekien.hbt.HBTCompound;

/**
 * Created with IntelliJ IDEA. User: matt Date: 19/04/13 Time: 4:14 PM To change this template use File | Settings |
 * File Templates.
 */
public interface MessageReceiver {
/**
 * Receive a message from the server (or client)
 *
 * @param msg
 * 		String containing the message
 */
public void receiveMessage(Message msg, MessageEndPoint receiver);

public String getReceiverName();

public void fromHBT(HBTCompound tag);

public HBTCompound toHBT(boolean msg);
}
