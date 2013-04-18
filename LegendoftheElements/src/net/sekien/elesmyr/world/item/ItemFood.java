package net.sekien.elesmyr.world.item;

import net.sekien.elesmyr.player.PlayerData;
import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.world.entity.EntityPlayer;

public class ItemFood extends Item {
@Override
public String getType() { return "Food"; }

@Override
public boolean onUse(GameServer receiver, EntityPlayer player, PlayerData.InventoryEntry entry) {
	if (player.pdat.health==player.pdat.healthMax)
		return false;
	player.pdat.health += extd.getInt("heal", 0);
	return true;
}

;
}
