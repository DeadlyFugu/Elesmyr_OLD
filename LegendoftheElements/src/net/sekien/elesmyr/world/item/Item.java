package net.sekien.elesmyr.world.item;

import net.sekien.elesmyr.Element;
import net.sekien.elesmyr.player.PlayerData;
import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.elesmyr.world.entity.EntityPlayer;
import net.sekien.hbt.HBTCompound;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Item {
public Image spr;
public String name;
protected HBTCompound extd;

public Item ctor(String name, String img, HBTCompound extd) {
	this.name = name;
	this.extd = extd;
	try {
		spr = FileHandler.getImage("item."+img);
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
