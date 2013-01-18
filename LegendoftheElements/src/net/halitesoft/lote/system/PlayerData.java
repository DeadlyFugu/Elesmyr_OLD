package net.halitesoft.lote.system;


import java.util.ArrayList;

import net.halitesoft.lote.Message;
import net.halitesoft.lote.MessageSystem;
import net.halitesoft.lote.world.item.Item;
import net.halitesoft.lote.world.item.ItemFactory;

import com.esotericsoftware.kryonet.Connection;

public class PlayerData {
	public class InventoryEntry {
		Item item;
		String extd;
		int count;
		
		InventoryEntry(Item item, String extd, int count) {
			this.item = item;
			this.extd = extd;
			this.count = count;
		}
		
		public void upCount() {
			count++;
		}
		
		public boolean downCount() {
			count--;
			return (count==0);
		}
		
		public String toString() {
			return count+"x "+item.name+" ("+extd+")";
		}
		
		public Item getItem() {return item;}
		public int getCount() {return count;}
		public String getExtd() {return extd;}
		
		@Override
		public boolean equals(Object other) {
			return this.item.name.equals(((InventoryEntry) other).item.name) && this.extd.equals(((InventoryEntry) other).extd);
		}
	}

	private String name;
	private Connection connection;
	public ArrayList<InventoryEntry> inventory;
	public int health;
	public PlayerData(String name, Connection connection) {
		this.name=name;
		this.connection=connection;
		inventory = new ArrayList<InventoryEntry>();
	}
	
	public void addConnection(Connection connection) {
		this.connection = connection;
	}
	
	public void updated() {
		if (connection != null)
			MessageSystem.sendClient(null,connection,new Message("PLAYER.setPDAT",this.toString()),false);
	}
	
	@Override
	public String toString() {
		String inv = "";
		for (InventoryEntry ie : inventory)
			inv=inv+"\\"+ie.count+","+ie.item.name+","+ie.extd;
		if (inv.length()>1)
			return name+","+health+","+inv.substring(1);
		else
			return name+","+health+",";
	}
	
	public void fromString(String str) {
		String[] parts = str.split(",",3);
		name=parts[0];
		health=Integer.parseInt(parts[1]);
		inventory.clear();
		if (!parts[2].equals(""))
			for (String is : parts[2].split("\\\\")) {
				inventory.add(new InventoryEntry(ItemFactory.getItem(is.split(",",3)[1]),is.split(",",3)[2],Integer.parseInt(is.split(",",3)[0])));
			}
	}
	
	public boolean put(Item item, String extd) {
		try {
			InventoryEntry ieo = new InventoryEntry(item,extd,1);
			if (inventory.contains(ieo))
				inventory.get(inventory.indexOf(ieo)).upCount();
			else
				inventory.add(new InventoryEntry(item,extd,1));
			updated();
			return true; //Return false if inv is full so item wasn't put
		} catch (Exception e) {
			return false;
		}
	}

	public String invToString() {
		String ret = "";
		for (InventoryEntry ie : inventory) {
			ret=ret.concat(ie.toString()+"\n");
		}
		return ret;
	}
	
	public String getName() {
		return name;
	}
}
