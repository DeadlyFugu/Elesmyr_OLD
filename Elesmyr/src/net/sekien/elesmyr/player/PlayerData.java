/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.player;

import com.esotericsoftware.minlog.Log;
import net.sekien.elesmyr.Element;
import net.sekien.elesmyr.msgsys.Connection;
import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageSystem;
import net.sekien.elesmyr.world.Region;
import net.sekien.elesmyr.world.entity.EntityPlayer;
import net.sekien.elesmyr.world.item.Item;
import net.sekien.hbt.*;

import java.util.ArrayList;

public class PlayerData {

	private String name;
	private Connection connection;
	public ArrayList<InventoryEntry> inventory;
	public int health = 60;
	public int magicka = 60;
	public int stamina = 60;
	public int healthMax = 60;
	public int magickaMax = 60;
	public int staminaMax = 60;
	public Element affinity = Element.NEUTRAL;
	private InventoryEntry equipped;
	private boolean updated = false;
	private InventoryEntry slot1, slot2, slot3, slot4, slot5;
	private int exp = 0;
	private int level = 0;

	public void markUpdate() {
		updated = true;
	}

	public boolean reqUpdate() {
		return updated;
	}

	public PlayerData(String name, Connection connection) {
		this.name = name;
		this.connection = connection;
		inventory = new ArrayList<InventoryEntry>();
	}

	public void addConnection(Connection connection) {
		this.connection = connection;
	}

	public void updated(Region r, String entRName) {
		if (health > healthMax)
			health = healthMax;
		if (stamina > staminaMax)
			stamina = staminaMax;
		if (magicka > magickaMax)
			magicka = magickaMax;
		if (connection != null)
			MessageSystem.sendClient(null, connection, new Message("PLAYER.setPDAT", (HBTCompound) this.toHBT()), false);
		for (Connection c : r.connections) {
			if (c != connection)
				MessageSystem.sendClient(null, c, new Message(entRName+".setPDAT", (HBTCompound) this.toHBT()), false);
		}
	}

	@Override
	public String toString() {
		String inv = "";
		for (InventoryEntry ie : inventory)
			inv = inv+"\\"+ie.count+","+ie.item.name+","+ie.extd;
		if (inv.length() > 1)
			return name+","+health+"/"+healthMax+","+magicka+"/"+magickaMax+","+stamina+"/"+staminaMax+","+affinity.toString()+","+inventory.indexOf(equipped)+","+inv.substring(1);
		else
			return name+","+health+"/"+healthMax+","+magicka+"/"+magickaMax+","+stamina+"/"+staminaMax+","+affinity.toString()+","+inventory.indexOf(equipped)+",";
	}

	public boolean put(Item item, HBTCompound extd, Region r, String ent, EntityPlayer ep) {
		try {
			InventoryEntry ieo = new InventoryEntry(item, extd, 1);
			InventoryEntry ie;
			if (item.stackable() && inventory.contains(ieo)) {
				ie = inventory.get(inventory.indexOf(ieo));
				ie.upCount();
			} else {
				ie = new InventoryEntry(item, extd, 1);
				inventory.add(ie);
			}
			updated(r, ent);
			MessageSystem.sendClient(ep, ep.connection, new Message("CLIENT.dmsg", new HBTCompound("p", new HBTTag[]{
			                                                                                                        new HBTString("msg", "#Obtained |$item."+item.getName(ie)),
			                                                                                                        new HBTString("img", item.getImageString()),
			})), true);
			return true; //Return false if inv is full so item wasn't put
		} catch (Exception e) {
			return false;
		}
	}

	public String invToString() {
		String ret = "";
		for (InventoryEntry ie : inventory) {
			ret = ret.concat(ie.toString()+"\n");
		}
		return ret;
	}

	public HBTTag toHBT() {
		HBTCompound ret = new HBTCompound(name);
		ret.addTag(new HBTInt("health", health));
		ret.addTag(new HBTInt("healthMax", healthMax));
		ret.addTag(new HBTInt("stamina", stamina));
		ret.addTag(new HBTInt("staminaMax", staminaMax));
		ret.addTag(new HBTInt("magicka", magicka));
		ret.addTag(new HBTInt("magickaMax", magickaMax));
		ret.addTag(new HBTFlag("affinity", affinity.toString()));
		ret.addTag(new HBTInt("equip", inventory.indexOf(equipped)));
		ret.addTag(new HBTInt("slot1", inventory.indexOf(slot1)));
		ret.addTag(new HBTInt("slot2", inventory.indexOf(slot2)));
		ret.addTag(new HBTInt("slot3", inventory.indexOf(slot3)));
		ret.addTag(new HBTInt("slot4", inventory.indexOf(slot4)));
		ret.addTag(new HBTInt("slot5", inventory.indexOf(slot5)));
		ret.addTag(new HBTInt("exp", exp));
		ret.addTag(new HBTInt("level", level));
		HBTCompound inv = new HBTCompound("inv");
		for (InventoryEntry ie : inventory) {
			HBTTag itag = ie.toHBT();
			itag.setName(Integer.toHexString(inventory.indexOf(ie)));
			inv.addTag(itag);
		}
		ret.addTag(inv);
		return ret;
		//return name+","+health+"/"+healthMax+","+magicka+"/"+magickaMax+","+stamina+"/"+staminaMax+","+affinity.toString()+","+inventory.indexOf(equipped)+","+inv.substring(1);
	}

	public void fromHBT(HBTTag tag) {
		health = ((HBTCompound) tag).getInt("health", 60);
		healthMax = ((HBTCompound) tag).getInt("healthMax", 60);
		stamina = ((HBTCompound) tag).getInt("stamina", 60);
		staminaMax = ((HBTCompound) tag).getInt("staminaMax", 60);
		magicka = ((HBTCompound) tag).getInt("magicka", 60);
		magickaMax = ((HBTCompound) tag).getInt("magickaMax", 60);
		affinity = ((HBTCompound) tag).getFlag("affinity", "NEUTRAL").asElement();
		inventory.clear();
		for (HBTTag ietag : ((HBTCompound) tag).getCompound("inv")) {
			if (ietag instanceof HBTCompound)
				inventory.add(new InventoryEntry((HBTCompound) ietag));
			else
				Log.warn("Invalid inventory entry: "+ietag);
		}
		if (((HBTCompound) tag).getInt("equip", -1) != -1)
			equipped = inventory.get(((HBTCompound) tag).getInt("equip", -1));
		if (((HBTCompound) tag).getInt("slot1", -1) != -1)
			slot1 = inventory.get(((HBTCompound) tag).getInt("slot1", -1));
		if (((HBTCompound) tag).getInt("slot2", -1) != -1)
			slot2 = inventory.get(((HBTCompound) tag).getInt("slot2", -1));
		if (((HBTCompound) tag).getInt("slot3", -1) != -1)
			slot3 = inventory.get(((HBTCompound) tag).getInt("slot3", -1));
		if (((HBTCompound) tag).getInt("slot4", -1) != -1)
			slot4 = inventory.get(((HBTCompound) tag).getInt("slot4", -1));
		if (((HBTCompound) tag).getInt("slot5", -1) != -1)
			slot5 = inventory.get(((HBTCompound) tag).getInt("slot5", -1));
		exp = ((HBTCompound) tag).getInt("exp", 0);
		level = ((HBTCompound) tag).getInt("level", 0);
	}

	public String getName() {
		return name;
	}

	public InventoryEntry getEquipped() {
		if (!inventory.contains(equipped))
			equipped = null;
		return equipped;
	}

	public InventoryEntry getSlot(int n) {
		InventoryEntry slotItem;
		switch (n) {
			case 1: slotItem = slot1; break;
			case 2: slotItem = slot2; break;
			case 3: slotItem = slot3; break;
			case 4: slotItem = slot4; break;
			case 5: slotItem = slot5; break;
			default: System.err.println("invalid slot id "+n+" passed to PlayerData.getSlot()"); return null;
		}
		if (!inventory.contains(slotItem))
			slotItem = null;
		return slotItem;
	}

	public void setEquipped(InventoryEntry equipped, Region r, String ent) {
		if (inventory.contains(equipped))
			this.equipped = equipped;
		updated(r, ent);
	}

	public void removeItem(int pos, Region r, String ent) {
		if (inventory.get(pos).downCount()) {
			inventory.remove(pos);
		}
		updated(r, ent);
	}

	public void setSlot(int n, InventoryEntry equipped, Region r, String ent) {
		System.out.println("n = "+n);
		switch (n) {
			case 1: slot1 = equipped; break;
			case 2: slot2 = equipped; break;
			case 3: slot3 = equipped; break;
			case 4: slot4 = equipped; break;
			case 5: slot5 = equipped; break;
			default: System.err.println("invalid slot id "+n+" passed to PlayerData.setSlot()");
		}
		updated(r, ent);
	}

	public int getLevel() {
		return level;
	}

	public int getExp() {
		return exp;
	}

	public void addExp(int amount, EntityPlayer ep) {
		exp += amount;
		while (exp > getExpToNextLevel()) {
			exp -= getExpToNextLevel();
			level++;
			MessageSystem.sendClient(ep, ep.connection, new Message("CLIENT.dmsg", new HBTCompound("p", new HBTTag[]{
			                                                                                                        new HBTString("msg", "Level Up!")
			})), false);
		}
	}

	public int getExpToNextLevel() {
		return (int) (Math.pow(level, 3)-Math.pow(level, 2)+(10*level));
	}

	static {
		for (int i = 0; i < 100; i++) {
			System.out.println("Level "+i+": "+(int) (((Math.pow(i, 3))-(Math.pow(i, 2))+(10*i))/4));
		}
	}
}
