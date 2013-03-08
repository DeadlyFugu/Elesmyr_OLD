package net.halitesoft.lote.world.item;

import net.halitesoft.lote.msgsys.Message;
import net.halitesoft.lote.msgsys.MessageSystem;
import net.halitesoft.lote.system.GameServer;
import net.halitesoft.lote.util.BookParser;
import net.halitesoft.lote.world.entity.EntityPlayer;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA. User: matt Date: 7/03/13 Time: 4:41 PM To change this template use File | Settings | File
 * Templates.
 */
public class ItemBook extends Item {

@Override public String getType() { return "Books"; }
@Override public boolean onUse(GameServer receiver, EntityPlayer player) {
	ArrayList<String> pages = BookParser.parseBook(extd);
	for (String s : pages)
		MessageSystem.sendClient(null, receiver.getPlayerConnection(player), new Message("CLIENT.book", s), false);
	return false;
}
}
