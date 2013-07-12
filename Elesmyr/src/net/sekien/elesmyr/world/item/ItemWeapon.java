package net.sekien.elesmyr.world.item;

import net.sekien.elesmyr.player.InventoryEntry;

public class ItemWeapon extends Item {

@Override
public String getType() { return "Weapons"; }

@Override
public boolean canEquip() { return true; }

public float getMult(InventoryEntry ie) {
	try {
		return extd.getFloat("dmg", 1);
	} catch (Exception e) {
		return 1;
	}
}
}
