package net.sekien.lote.world.item;

public class ItemWeapon extends Item {

@Override
public String getType() { return "Weapons"; }

@Override
public boolean canEquip() { return true; }

public float getMult(String iextd) {
	try {
		return extd.getFloat("dmg", 1);
	} catch (Exception e) {
		return 1;
	}
}
}
