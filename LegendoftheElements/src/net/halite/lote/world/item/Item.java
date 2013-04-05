package net.halite.lote.world.item;

import net.halite.lote.Element;
import net.halite.lote.player.PlayerData;
import net.halite.lote.system.GameServer;
import net.halite.lote.util.FileHandler;
import net.halite.lote.world.entity.EntityPlayer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Item {
public Image spr;
public String name;
protected String extd;

public Item ctor(String name, String img, String extd) {
	this.name=name;
	this.extd=extd;
	try {
		spr=FileHandler.getImage("item."+img);
	} catch (SlickException e) {}
	return this;
}

@Override
public String toString() {
	return name+","+this.getClass().getSimpleName()+","+extd;
}

public String getType() {
	return "Misc";
}

public boolean canEquip() {
	return false;
}

public boolean onUse(GameServer reciever, EntityPlayer player, PlayerData.InventoryEntry entry) {
	return false;
}

/** Returns this. Used for scripting purposes */
public Item toItem() {
	return this;
}

public Element getElement() { return Element.NEUTRAL; }

public String getName(PlayerData.InventoryEntry entry) {
	return name;
}

public boolean stickyDrops() {
	return false;
}

public boolean stackable() {
	return true;
}
}
