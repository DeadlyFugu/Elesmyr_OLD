package net.halite.lote.world.item;

import net.halite.lote.player.PlayerData;
import net.halite.lote.system.GameServer;
import net.halite.lote.world.entity.EntityPlayer;

/**
 * Created with IntelliJ IDEA. User: matt Date: 10/03/13 Time: 1:18 PM To change this template use File | Settings |
 * File Templates.
 */
public class ItemFishingRod extends Item {
@Override
public boolean canEquip() { return true; }

@Override
public boolean onUse(GameServer receiver, EntityPlayer player, PlayerData.InventoryEntry entry) {
	if (entry.getExtd().equals("C")) {
		System.out.println("Fishing rod was reeled!");
		entry.setExtd("R");
		player.pdat.markUpdate();
	} else {
		System.out.println("Fishing rod was cast!");
		entry.setExtd("C");
		player.pdat.markUpdate();
	}
	return false;
}

@Override
public String getName(PlayerData.InventoryEntry entry) {
	if (entry.getExtd().equals("C")) {
		return name+"| (|$item.FishingRod.cast|)";
	} else {
		return name;
	}
}
}