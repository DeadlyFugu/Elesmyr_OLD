/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.ui;

import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.FontRenderer;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.Main;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.elesmyr.world.entity.Entity;
import net.sekien.elesmyr.world.entity.EntityPlayer;
import net.sekien.pepper.Renderer;
import org.newdawn.slick.*;

import java.util.ArrayList;

public class HUDUI implements UserInterface {
	private int updtimer = 10;
	private ArrayList<EntityPlayer> nearplayers;

	private boolean inited = false;

	private Image bg;
	private Image itembg;
	private Image bgstart;
	private Image bgend;
	private Image barbg;
	private Image redglow;
	private Image greenglow;
	private Image blueglow;
	private Image bars;
	private Image slotSel;

	@Override
	public boolean inited() { return inited; }

	public HUDUI() { nearplayers = new ArrayList<EntityPlayer>(); }

	@Override
	public void init(GameContainer gc, MessageEndPoint receiver) throws SlickException {
		inited = true;
		bg = FileHandler.getImage("ui.hud.bg");
		itembg = FileHandler.getImage("ui.hud.itembg");
		bgstart = FileHandler.getImage("ui.hud.bgstart");
		bgend = FileHandler.getImage("ui.hud.bgend");
		barbg = FileHandler.getImage("ui.hud.barbg");
		redglow = FileHandler.getImage("ui.hud.redglow");
		greenglow = FileHandler.getImage("ui.hud.greenglow");
		blueglow = FileHandler.getImage("ui.hud.blueglow");
		bars = FileHandler.getImage("ui.hud.bars");
		slotSel = FileHandler.getImage("ui.hud.slot_sel");
	}

	@Override
	public void render(Renderer renderer, Camera cam, GameClient receiver) throws SlickException {
		Graphics g = renderer.g;
		int dy = Main.INTERNAL_RESY-64;
		int dx = 0;

		for (int i = 0; i < 1+Main.INTERNAL_RESX/480; i++) {
			bg.draw(i*480, dy, 480, 64);
		}

		bgstart.draw(0, dy, 12, 64);
		bgend.draw(Main.INTERNAL_RESX-12, dy, 12, 64);

		for (int i = 0; i < 5; i++) {
			itembg.draw(11+i*47, dy+16);
		}

		if (nearplayers.size() > 0) {
			EntityPlayer ep = nearplayers.get(0);
			FontRenderer.drawStringHud(dx+246, dy+27, ep.getName(), ep.getElement().color(), g);

			FontRenderer.drawString(dx, dy, "Level: "+ep.pdat.getLevel()+" Exp: "+ep.pdat.getExp()+"/"+ep.pdat.getExpToNextLevel(), g);

			for (int i = 1; i <= 5; i++) {
				if (ep.pdat.getSlot(i) != null) {
					if (ep.pdat.getEquipped() == ep.pdat.getSlot(i)) {
						slotSel.draw(dx+5+(47*(i-1)), dy+11, ep.getElement().color());
					}
					ep.pdat.getSlot(i).getItem().spr.draw(dx+14+(47*(i-1)), dy+19);
				}
			}

			//render bars
			for (int i = 0; i < 3; i++) {
				barbg.draw(Main.INTERNAL_RESX-117, dy+13+i*16);
			}

			int StartX = Main.INTERNAL_RESX-115;
			int StartY = dy+15;
			float healthDif = ((float) ep.pdat.health/ep.pdat.healthMax);
			float magickaDif = ((float) ep.pdat.magicka/ep.pdat.magickaMax);
			float staminaDif = ((float) ep.pdat.stamina/ep.pdat.staminaMax);
			int healthPercent = (int) (healthDif*100);
			int magickaPercent = (int) (magickaDif*100);
			int staminaPercent = (int) (staminaDif*100);
			int health = (int) (healthDif*102);
			int magicka = (int) (magickaDif*102);
			int stamina = (int) (staminaDif*102);

			FontRenderer.drawStringPixel(Main.INTERNAL_RESX-126-FontRenderer.getPixelWidth(String.valueOf(healthPercent)+":"), dy+14, String.valueOf(healthPercent)+":", new Color(0xdd4632), g);
			FontRenderer.drawStringPixel(Main.INTERNAL_RESX-126-FontRenderer.getPixelWidth(String.valueOf(staminaPercent)+":"), dy+30, String.valueOf(staminaPercent)+":", new Color(0x80e24a), g);
			FontRenderer.drawStringPixel(Main.INTERNAL_RESX-126-FontRenderer.getPixelWidth(String.valueOf(magickaPercent)+":"), dy+46, String.valueOf(magickaPercent)+":", new Color(0x5aa3ee), g);

			if (healthPercent == 100) {
				redglow.draw(Main.INTERNAL_RESX-118, dy+10, 110, 16);
			}
			if (staminaPercent == 100) {
				greenglow.draw(Main.INTERNAL_RESX-118, dy+26, 110, 16);
			}
			if (magickaPercent == 100) {
				blueglow.draw(Main.INTERNAL_RESX-118, dy+42, 110, 16);
			}

			int healthmid = health-2;
			int staminamid = stamina-2;
			int magickamid = magicka-2;

			bars.startUse();

			int capwidth = 2;
			int healthdis = healthmid+capwidth;
			int staminadis = staminamid+capwidth;
			int magickadis = magickamid+capwidth;

			if (health > 0) {
				bars.drawEmbedded(StartX, StartY, StartX+capwidth, StartY+6, 0, 0, capwidth, 6);
				bars.drawEmbedded(StartX+capwidth, StartY, StartX+healthdis, StartY+6, 2, 0, 2, 6);
				bars.drawEmbedded(StartX+healthdis, StartY, StartX+capwidth+healthdis, StartY+6, 5-capwidth, 0, 5, 6);
			} else {
				// renders nothing
			}
			if (stamina > 0) {
				bars.drawEmbedded(StartX, StartY+16, StartX+capwidth, StartY+22, 0, 6, capwidth, 12);
				bars.drawEmbedded(StartX+capwidth, StartY+16, StartX+staminadis, StartY+22, 2, 6, 2, 12);
				bars.drawEmbedded(StartX+staminadis, StartY+16, StartX+capwidth+staminadis, StartY+22, 5-capwidth, 6, 5, 12);
			} else {
				// renders nothing
			}
			if (magicka > 0) {
				bars.drawEmbedded(StartX, StartY+32, StartX+capwidth, StartY+38, 0, 12, capwidth, 18);
				bars.drawEmbedded(StartX+capwidth, StartY+32, StartX+magickadis, StartY+38, 2, 12, 2, 18);
				bars.drawEmbedded(StartX+magickadis, StartY+32, StartX+capwidth+magickadis, StartY+38, 5-capwidth, 12, 5, 18);
			} else {
				// renders nothing
			}
			bars.endUse();
		}
	}

	@Override
	public void update(GameContainer gc, GameClient receiver) {
		if (updtimer == 0) { //Calculate nearest players
			updtimer = 60;
			if (receiver.player.region == null)
				return; //Do nothing if the PlayerClient.region is null.
			EntityPlayer player = ((EntityPlayer) receiver.player.region.entities.get(receiver.player.entid));
			if (player == null)
				return; //Do nothing if the player is null. The player temp. goes null when switching regions.
			nearplayers.clear();
			ArrayList<EntityPlayer> temp = new ArrayList<EntityPlayer>();
			for (Entity e : receiver.player.region.entities.values()) {
				if (e instanceof EntityPlayer)
					temp.add((EntityPlayer) e);
			}
			int tsize = Math.min(temp.size(), 4);
			for (int i = 0; i < tsize; i++) {
				EntityPlayer nearest = null;
				float ndist = 256; //Max distance. Players past here will be ignored
				for (Entity e : temp) {
					float tnd;
					if ((tnd = (float) Math.hypot(e.x-player.x, e.y-player.y)) < ndist) {
						nearest = (EntityPlayer) e;
						ndist = tnd;
					}
				}
				if (nearest != null) {
					nearplayers.add(nearest);
					temp.remove(nearest);
				}
			}
		} else { updtimer--; }
	}

	@Override
	public boolean blockUpdates() {return false;}

	@Override
	public void ctor(String extd) {}
}