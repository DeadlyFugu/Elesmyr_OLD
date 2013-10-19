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
	}

	public String getName() {
		return name;
	}

	public InventoryEntry getEquipped() {
		if (!inventory.contains(equipped))
			equipped = null;
		return equipped;
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
}
