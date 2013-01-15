package net.shard.lote.world.item;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Item {
	public Image spr;
	public String name;
	private String extd;
	private String img;
	
	public Item ctor(String name, String img, String extd) {
		this.name=name;
		this.img=img;
		this.extd=extd;
		try {
			spr = new Image("data/item/"+img+".png",false,0);
		} catch (SlickException e) {}
		return this;
	}
	
	@Override
	public String toString() {
		return name+","+this.getClass().getName().substring("net.shard.lote.world.item.".length())+","+extd;
	}
	
	public String getType() {
		return "Misc";
	}
}
