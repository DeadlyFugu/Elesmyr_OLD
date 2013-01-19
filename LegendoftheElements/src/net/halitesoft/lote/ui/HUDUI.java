package net.halitesoft.lote.ui;


import java.util.ArrayList;

import net.halitesoft.lote.MessageReceiver;
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
	private Image playerInfo;
	private int updtimer = 10;
	private ArrayList<EntityPlayer> nearplayers;
	
	public HUDUI() {
		nearplayers = new ArrayList<EntityPlayer>();
	}
	@Override
	public void init(GameContainer gc, StateBasedGame sbg,
			MessageReceiver receiver) throws SlickException {
		playerInfo = new Image("data/ui/hud.png",false,0);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
			Camera cam, GameClient receiver) throws SlickException {
		int i=0;
		for (EntityPlayer ep : nearplayers) {
			int dx = i*160;
			int dy = Main.INTERNAL_RESY-64;
			playerInfo.draw(dx,dy);
			Main.font.drawString(dx+13,dy+6,ep.getName());
			//System.out.println(ep);
			//System.out.println(ep.pdat);
			if (ep.pdat!=null)
				Main.font.drawString(dx+30,dy+18,"HP:"+ep.pdat.health);
			i++;
		}
		//playerInfo.draw(Main.INTERNAL_RESX*0.5f-80,Main.INTERNAL_RESY-64);
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
