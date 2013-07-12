package net.sekien.elesmyr.player;

import net.sekien.elesmyr.world.item.Item;
import net.sekien.elesmyr.world.item.ItemFactory;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTInt;
import net.sekien.hbt.HBTString;
import net.sekien.hbt.HBTTag;

/**
 * Created with IntelliJ IDEA. User: matt Date: 12/07/13 Time: 11:03 AM To change this template use File | Settings |
 * File Templates.
 */
public class InventoryEntry {
Item item;
HBTCompound extd;
int count;

InventoryEntry(Item item, HBTCompound extd, int count) {
	this.item = item;
	this.extd = extd;
	this.count = count;
}

public InventoryEntry(HBTCompound ietag) {
	this(ItemFactory.getItem(ietag.getString("n", "Null")), ietag, ietag.getInt("c", 1));
}

void upCount() {
	count++;
}

boolean downCount() {
	count--;
	return (count==0);
}

public String toString() {
	return count+"x "+item.name+" ("+extd+")";
}

public Item getItem() {return item;}

public int getCount() {return count;}

public HBTCompound getExtd() {return extd;}

public void setExtd(HBTCompound extd) {this.extd = extd;}

public void setExtdTag(String fullname, HBTTag tag) {this.extd.setTag(fullname, tag);}

@Override
public boolean equals(Object other) {
	if (other==null)
		return false;
	return this.item.name.equals(((InventoryEntry) other).item.name) /*&& this.extd.equals(((InventoryEntry) other).extd)*/;
}

public HBTCompound toHBT() {
	HBTCompound ret = (HBTCompound) extd.deepClone();
	ret.setTag(new HBTString("n", item.name));
	ret.setTag(new HBTInt("c", count));
	return ret;
}
}
