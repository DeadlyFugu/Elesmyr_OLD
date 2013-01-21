package net.halitesoft.lote.ui;


import java.util.ArrayList;

import net.halitesoft.lote.Message;
import net.halitesoft.lote.MessageReceiver;
import net.halitesoft.lote.MessageSystem;
import net.halitesoft.lote.system.Camera;
import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.system.Main;
import net.halitesoft.lote.system.PlayerData;
import net.halitesoft.lote.system.PlayerData.InventoryEntry;
import net.halitesoft.lote.world.entity.EntityPlayer;
import net.halitesoft.lote.world.item.Item;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class InventoryUI implements UserInterface {
	Image bg;
	Image invsel;
	int sel = 0;
	int isel = 0;
	int smax = 0;
	String[] types = {"All","Weapons","Armor","Potions","Food","Books","Misc"};
	@Override public void init(GameContainer gc, StateBasedGame sbg,
			MessageReceiver receiver) throws SlickException {
		bg = new Image("data/ui/inv.png",false,0);
		invsel = new Image("data/ui/invsel.png",false,0);
	}

	@Override public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
			Camera cam, GameClient receiver) throws SlickException {
		bg.draw();
		invsel.draw(84+sel*35,66);
		Main.font.drawString(77,17,types[sel]);
		try {
		PlayerData pdat = ((EntityPlayer) receiver.player.region.entities.get(receiver.player.entid)).pdat;
		int i=0;
		for (PlayerData.InventoryEntry ie : pdat.inventory) {
			Item iei = ie.getItem();
			if (iei.getType().equalsIgnoreCase(types[sel]) || sel==0) {
				if (i==isel)
					g.fillRect(74, 116+i*38, 406, 36);
				iei.spr.draw(78,120+i*38);
				Main.font.drawString(117,128+i*38, iei.name);
				Main.font.drawString(450,128+i*38, ""+ie.getCount());
				//Main.font.drawString(526,128+i*40,"$"+ie.getValue()); //TODO: Value thingies
				Main.font.drawString(526,128+i*38, ie.getExtd());
				i++;
			}
		}
		smax=i;
		} catch (Exception e) {};
	}

	@Override public void update(GameContainer gc, StateBasedGame sbg, GameClient receiver) {
		Input in = gc.getInput();
		if (in.isKeyPressed(Input.KEY_LEFT))
			if (sel>0) {
				sel--;
				isel=0;
			}
		if (in.isKeyPressed(Input.KEY_RIGHT))
			if (sel<types.length-1) {
				sel++;
				isel=0;
			}
		if (in.isKeyPressed(Input.KEY_UP))
			if (isel>0)
				isel--;
		if (in.isKeyPressed(Input.KEY_DOWN))
			if (isel<smax-1)
				isel++;
		if (in.isKeyPressed(Input.KEY_X)) {
			PlayerData.InventoryEntry i = getItem(isel, receiver);
			System.out.println(i);
			if (i!=null) {
				EntityPlayer ep = ((EntityPlayer) receiver.player.region.entities.get(receiver.player.entid));
				ArrayList<PlayerData.InventoryEntry> inv = ((EntityPlayer) receiver.player.region.entities.get(receiver.player.entid)).pdat.inventory;
				if (i.getItem().canEquip()) {
					MessageSystem.sendServer(null,new Message(ep.getReceiverName()+".equip",""+inv.indexOf(i)),false);
					//ep.pdat.setEquipped(i,receiver.player.region,ep.getReceiverName());
				} else {
					MessageSystem.sendServer(null,new Message(ep.getReceiverName()+".use",""+inv.indexOf(i)),false);
					//i.getItem().onUse(receiver);
				}
			}
		}
	}
	
	private InventoryEntry getItem(int isel, GameClient receiver) {
		int i=0;
		for (PlayerData.InventoryEntry ie : ((EntityPlayer) receiver.player.region.entities.get(receiver.player.entid)).pdat.inventory) {
			if (ie.getItem().getType().equalsIgnoreCase(types[sel]) || sel==0) {
				if (i==isel)
					return ie;
				i++;
			}
		}
		return null;
	}

	@Override public boolean blockUpdates() {return true;}
}
