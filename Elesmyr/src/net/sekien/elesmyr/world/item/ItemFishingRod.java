package net.sekien.elesmyr.world.item;

import net.sekien.elesmyr.player.InventoryEntry;
import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.world.entity.EntityPlayer;
import net.sekien.hbt.HBTByte;
import net.sekien.hbt.HBTCompound;

/**
 * Created with IntelliJ IDEA. User: matt Date: 10/03/13 Time: 1:18 PM To change this template use File | Settings |
 * File Templates.
 */
public class ItemFishingRod extends Item {
@Override
public boolean canEquip() { return true; }

@Override
public boolean onUse(GameServer receiver, EntityPlayer player, InventoryEntry entry) {
	HBTCompound iextd = entry.getExtd();
	if (iextd.getByte("cast", (byte) 0)==1) {
		System.out.println("Fishing rod was reeled!");
		iextd.setTag(new HBTByte("cast", (byte) 0));
		entry.setExtd(iextd);
		player.pdat.markUpdate();
	} else {
		System.out.println("Fishing rod was cast!");
		iextd.setTag(new HBTByte("cast", (byte) 1));
		entry.setExtd(iextd);
		player.pdat.markUpdate();
	}
	return false;
}

@Override
public String getName(InventoryEntry entry) {
	if (entry.getExtd().getByte("cast", (byte) 0)==1) {
		return name+"| (|$item.FishingRod.cast|)";
	} else {
		return name;
	}
}
}
