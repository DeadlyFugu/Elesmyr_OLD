/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.item;

import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.player.InventoryEntry;
import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.world.entity.EntityPlayer;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTInt;
import net.sekien.hbt.HBTString;
import net.sekien.hbt.HBTTag;

/**
 * Created with IntelliJ IDEA. User: matt Date: 9/03/13 Time: 1:59 PM To change this template use File | Settings | File
 * Templates.
 */
public class ItemTorch extends Item {
	@Override
	public boolean stickyDrops() {
		return true;
	}

	@Override
	public boolean onUse(GameServer receiver, EntityPlayer player, InventoryEntry entry) {
		int dx = player.x-16;
		int dy = player.y-16;
		HBTCompound ietag = entry.getExtd();
		ietag.setName("ie");
		receiver.receiveMessage(new Message(player.getReceiverName().split("\\.", 2)[0]+".addEntSERV", new HBTCompound("p", new HBTTag[]{
		                                                                                                                                new HBTString("class", "EntityPlacedItem"),
		                                                                                                                                new HBTInt("x", dx),
		                                                                                                                                new HBTInt("y", dy),
		                                                                                                                                ietag
		})));
		return true;
	}
}
