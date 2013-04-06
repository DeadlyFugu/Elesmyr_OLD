package net.halite.lote.world.item;

import net.halite.lote.player.PlayerData;
import net.halite.lote.system.GameServer;
import net.halite.lote.world.entity.EntityPlayer;

public class ItemFood extends Item {
@Override
public String getType() { return "Food"; }

@Override
public boolean onUse(GameServer receiver, EntityPlayer player, PlayerData.InventoryEntry entry) {
	if (player.pdat.health==player.pdat.healthMax)
		return false;
	player.pdat.health+=extd.getInt("heal",0);
	return true;
}

;
}
