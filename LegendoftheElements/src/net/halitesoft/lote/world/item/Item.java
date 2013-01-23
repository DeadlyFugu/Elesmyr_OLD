package net.halitesoft.lote.world.item;

import net.halitesoft.lote.Element;
import net.halitesoft.lote.system.GameServer;
import net.halitesoft.lote.world.entity.EntityPlayer;

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
			spr = new Image("data/item/"+img+".png",false,0);
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
	
	public boolean onUse(GameServer reciever, EntityPlayer player) {
		return false;
	}
	
	/** Returns this. Used for scripting purposes */
	public Item toItem() {
		return this;
	}

	public Element getElement() { return Element.NEUTRAL; }
}
