package net.halite.lote.world.item;

import net.halite.lote.msgsys.Message;
import net.halite.lote.msgsys.MessageSystem;
import net.halite.lote.player.PlayerData;
import net.halite.lote.system.GameServer;
import net.halite.lote.util.BookParser;
import net.halite.lote.world.entity.EntityPlayer;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA. User: matt Date: 7/03/13 Time: 4:41 PM To change this template use File | Settings | File
 * Templates.
 */
public class ItemBook extends Item {

@Override
public String getType() { return "Books"; }

@Override
public boolean onUse(GameServer receiver, EntityPlayer player, PlayerData.InventoryEntry entry) {
	ArrayList<String> pages=BookParser.parseBook(extd);
	for (String s : pages)
		MessageSystem.sendClient(null, receiver.getPlayerConnection(player), new Message("CLIENT.book", s), false);
	return false;
}

@Override
public String getName(PlayerData.InventoryEntry entry) { return "book."+extd; }
}
