package net.halite.lote.world.item;

public class ItemWeapon extends Item {

@Override
public String getType() { return "Weapons"; }

@Override
public boolean canEquip() { return true; }

public float getMult(String iextd) {
	try {
		return Float.parseFloat(extd);
	} catch (Exception e) {
		return 1;
	}
}
}
