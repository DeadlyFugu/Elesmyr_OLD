/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
