/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.item;

import net.sekien.elesmyr.player.InventoryEntry;
import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.world.entity.EntityPlayer;

public class ItemFood extends Item {
@Override
public String getType() { return "Food"; }

@Override
public boolean onUse(GameServer receiver, EntityPlayer player, InventoryEntry entry) {
	if (player.pdat.health==player.pdat.healthMax)
		return false;
	player.pdat.health += extd.getInt("heal", 0);
	return true;
}

;
}
