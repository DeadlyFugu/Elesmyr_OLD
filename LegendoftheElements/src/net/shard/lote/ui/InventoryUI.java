package net.shard.lote.ui;


import net.shard.lote.MessageReceiver;
import net.shard.lote.system.Camera;
import net.shard.lote.system.GameClient;
import net.shard.lote.system.Main;
import net.shard.lote.system.PlayerData;
import net.shard.lote.world.entity.EntityPlayer;
import net.shard.lote.world.item.Item;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class InventoryUI implements UserInterface {
	Image bg;
	int sel = 0;
	String[] types = {"Weapon","Ingredient","Misc"};
	@Override public void init(GameContainer gc, StateBasedGame sbg,
			MessageReceiver receiver) throws SlickException {
		bg = new Image("data/ui/inv.png",false,0);
	}

	@Override public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
			Camera cam, GameClient receiver) throws SlickException {
		bg.draw();
		try {
		PlayerData pdat = ((EntityPlayer) receiver.player.region.entities.get(receiver.player.entid)).pdat;
		int i=0;
		for (PlayerData.InventoryEntry ie : pdat.inventory) {
			Item iei = ie.getItem();
			if (iei.getType().equalsIgnoreCase(types[sel])) {
				Main.font.drawString(55,111+i*40, ie.getCount()+"x");
				iei.spr.draw(109,105+i*40);
				Main.font.drawString(149,111+i*40, iei.name);
				Main.font.drawString(374,111+i*40, ie.getExtd());
				i++;
			}
		}
		} catch (Exception e) {};
		if (sel>0)
			Main.font.drawString(100-Main.font.getWidth(types[sel-1])/2,40,types[sel-1]);
		Main.font.drawString(310-Main.font.getWidth(types[sel])/2,40,types[sel]);
		if (sel<types.length-1)
			Main.font.drawString(525-Main.font.getWidth(types[sel+1])/2,40,types[sel+1]);
	}

	@Override public void update(GameContainer gc, StateBasedGame sbg, GameClient receiver) {
		Input in = gc.getInput();
		if (in.isKeyPressed(Input.KEY_LEFT))
			if (sel>0)
				sel--;
		if (in.isKeyPressed(Input.KEY_RIGHT))
			if (sel<types.length-1)
				sel++;
	}
	
	@Override public boolean blockUpdates() {return true;}
}
