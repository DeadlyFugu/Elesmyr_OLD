package net.halitesoft.lote.ui;


import java.util.ArrayList;

import net.halitesoft.lote.MessageReceiver;
import net.halitesoft.lote.MessageSystem;
import net.halitesoft.lote.system.Camera;
import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.system.Main;
import net.halitesoft.lote.system.PlayerData;
import net.halitesoft.lote.world.entity.Entity;
import net.halitesoft.lote.world.entity.EntityPlayer;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class HUDUI implements UserInterface {
	private Image bart;
	private Image barb;
	private Image bars;
	private Image equip;
	private int updtimer = 10;
	private ArrayList<EntityPlayer> nearplayers;
	
	public HUDUI() {
		nearplayers = new ArrayList<EntityPlayer>();
	}
	@Override
	public void init(GameContainer gc, StateBasedGame sbg,
			MessageReceiver receiver) throws SlickException {
		bart = new Image("data/ui/bar_t.png",false,0);
		barb = new Image("data/ui/bar_b.png",false,0);
		bars = new Image("data/ui/bars.png",false,0);
		equip = new Image("data/ui/equip.png",false,0);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
			Camera cam, GameClient receiver) throws SlickException {
		bart.draw(0,0,Main.INTERNAL_RESX,16);
		Main.font.drawString(0, 0, HUDUI.getTopString());
		barb.draw(0,Main.INTERNAL_RESY-64,Main.INTERNAL_RESX,64);
		
		int i=0;
		if (nearplayers.size()>1)
			for (EntityPlayer ep : nearplayers) {
				int dx = i*160;
				int dy = Main.INTERNAL_RESY-64;
				equip.draw(dx,dy);
				if (ep.pdat.getEquipped()!=null)
					ep.pdat.getEquipped().getItem().spr.draw(dx+16,dy+16);
				Main.font.drawString(dx+13,dy+6,ep.getName());
				bars.startUse();
				bars.drawEmbedded(dx+57, dy+25, dx+57+104, dy+34,0,75,104,84);
				if (ep.pdat!=null)
					bars.drawEmbedded(dx+57, dy+25, dx+57+(ep.pdat.health/60f)*104, dy+34,0,48,(ep.pdat.health/60f)*104,57);
				bars.endUse();
				i++;
			}
		else if (nearplayers.size()==1) {
			int dx = 0;
			int dy = Main.INTERNAL_RESY-64;
			EntityPlayer ep = nearplayers.get(0);
			equip.draw(dx,dy);
			if (ep.pdat.getEquipped()!=null)
				ep.pdat.getEquipped().getItem().spr.draw(dx+16,dy+16);
			Main.font.drawString(dx+62,dy+25,ep.getName());
			int bdx = Main.INTERNAL_RESX-112;
			bars.startUse();
			bars.drawEmbedded(bdx, dy+13, bdx+104, dy+25,0,75,104,84);
			bars.drawEmbedded(bdx, dy+13, bdx+(ep.pdat.health/60f)*104, dy+25,0,0,(ep.pdat.health/60f)*104,12);
			bars.endUse();
		}
		//playerInfo.draw(Main.INTERNAL_RESX*0.5f-80,Main.INTERNAL_RESY-64);
	}

	private static String getTopString() {
		return "LotE "+Main.version+":"+
				(GameClient.CLIENT?" CLIENT":"")+
				(GameClient.SERVER?" SERVER":"")+
				(MessageSystem.fastLink?" FASTLINK":"")+
				(Boolean.parseBoolean(Main.globals.get("debug"))?" DEBUG":"");
	}
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, GameClient receiver) {
		if (updtimer==0) { //Calculate nearest players
			updtimer=60;
			if (receiver.player.region==null)
				return; //Do nothing if the PlayerClient.region is null.
			EntityPlayer player = ((EntityPlayer) receiver.player.region.entities.get(receiver.player.entid));
			if (player==null)
				return; //Do nothing if the player is null. The player temp. goes null when switching regions.
			nearplayers.clear();
			ArrayList<EntityPlayer> temp = new ArrayList<EntityPlayer>();
			for (Entity e : receiver.player.region.entities.values()) {
				if (e instanceof EntityPlayer)
					temp.add((EntityPlayer) e);
			}
			int tsize = Math.min(temp.size(), 4);
			for (int i=0;i<tsize;i++) {
				EntityPlayer nearest = null;
				float ndist = 256; //Max distance. Players past here will be ignored
				for (Entity e : temp) {
					float tnd;
					if ((tnd=(float) Math.hypot(e.x-player.x,e.y-player.y))<ndist) {
						nearest=(EntityPlayer) e;
						ndist=tnd;
					}
				}
				if (nearest != null) {
					nearplayers.add(nearest);
					temp.remove(nearest);
				}
			}
		} else {
			updtimer--;
		}
	}

	@Override
	public boolean blockUpdates() {return false;}

}
