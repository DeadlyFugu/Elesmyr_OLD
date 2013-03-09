package net.halitesoft.lote.player;

import com.esotericsoftware.kryonet.Connection;
import net.halitesoft.lote.Element;
import net.halitesoft.lote.msgsys.Message;
import net.halitesoft.lote.msgsys.MessageSystem;
import net.halitesoft.lote.world.Region;
import net.halitesoft.lote.world.item.Item;
import net.halitesoft.lote.world.item.ItemFactory;

import java.util.ArrayList;

public class PlayerData {

public class InventoryEntry {
	Item item;
	String extd;
	int count;

	InventoryEntry(Item item, String extd, int count) {
		this.item=item;
		this.extd=extd;
		this.count=count;
	}

	private void upCount() {
		count++;
	}

	private boolean downCount() {
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
		if (other==null)
			return false;
		return this.item.name.equals(((InventoryEntry) other).item.name)&&this.extd.equals(((InventoryEntry) other).extd);
	}
}

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

public PlayerData(String name, Connection connection) {
	this.name=name;
	this.connection=connection;
	inventory=new ArrayList<InventoryEntry>();
}

public void addConnection(Connection connection) {
	this.connection=connection;
}

public void updated(Region r, String entRName) {
	if (health>healthMax)
		health=healthMax;
	if (stamina>staminaMax)
		stamina=staminaMax;
	if (magicka>magickaMax)
		magicka=magickaMax;
	if (connection!=null)
		MessageSystem.sendClient(null, connection, new Message("PLAYER.setPDAT", this.toString()), false);
	for (Connection c : r.connections) {
		if (c!=connection)
			MessageSystem.sendClient(null, c, new Message(entRName+".setPDAT", this.toString()), false);
	}
}

@Override
public String toString() {
	String inv="";
	for (InventoryEntry ie : inventory)
		inv=inv+"\\"+ie.count+","+ie.item.name+","+ie.extd;
	if (inv.length()>1)
		return name+","+health+"/"+healthMax+","+magicka+"/"+magickaMax+","+stamina+"/"+staminaMax+","+affinity.toString()+","+inventory.indexOf(equipped)+","+inv.substring(1);
	else
		return name+","+health+"/"+healthMax+","+magicka+"/"+magickaMax+","+stamina+"/"+staminaMax+","+affinity.toString()+","+inventory.indexOf(equipped)+",";
}

public void fromString(String str) {
	String[] parts=str.split(",", 7);
	name=parts[0];
	health=Integer.parseInt(parts[1].split("/")[0]);
	healthMax=Integer.parseInt(parts[1].split("/")[1]);
	magicka=Integer.parseInt(parts[2].split("/")[0]);
	magickaMax=Integer.parseInt(parts[2].split("/")[1]);
	stamina=Integer.parseInt(parts[3].split("/")[0]);
	staminaMax=Integer.parseInt(parts[3].split("/")[1]);
	affinity=Element.valueOf(parts[4]);
	inventory.clear();
	if (!parts[6].equals(""))
		for (String is : parts[6].split("\\\\")) {
			inventory.add(new InventoryEntry(ItemFactory.getItem(is.split(",", 3)[1]), is.split(",", 3)[2], Integer.parseInt(is.split(",", 3)[0])));
		}
	if (!parts[5].equals("-1"))
		equipped=inventory.get(Integer.valueOf(parts[5]));
}

public boolean put(Item item, String extd, Region r, String ent) {
	try {
		InventoryEntry ieo=new InventoryEntry(item, extd, 1);
		if (inventory.contains(ieo))
			inventory.get(inventory.indexOf(ieo)).upCount();
		else
			inventory.add(new InventoryEntry(item, extd, 1));
		updated(r, ent);
		return true; //Return false if inv is full so item wasn't put
	} catch (Exception e) {
		return false;
	}
}

public String invToString() {
	String ret="";
	for (InventoryEntry ie : inventory) {
		ret=ret.concat(ie.toString()+"\n");
	}
	return ret;
}

public String getName() {
	return name;
}

public InventoryEntry getEquipped() {
	if (!inventory.contains(equipped))
		equipped=null;
	return equipped;
}

public void setEquipped(InventoryEntry equipped, Region r, String ent) {
	if (inventory.contains(equipped))
		this.equipped=equipped;
	updated(r, ent);
}

public void removeItem(int pos, Region r, String ent) {
	if (inventory.get(pos).downCount()) {
		inventory.remove(pos);
	}
	updated(r, ent);
}
}
