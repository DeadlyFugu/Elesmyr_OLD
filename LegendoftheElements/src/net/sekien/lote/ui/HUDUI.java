package net.sekien.lote.ui;

import net.sekien.lote.Element;
import net.sekien.lote.msgsys.MessageReceiver;
import net.sekien.lote.msgsys.MessageSystem;
import net.sekien.lote.player.Camera;
import net.sekien.lote.system.FontRenderer;
import net.sekien.lote.system.GameClient;
import net.sekien.lote.system.Globals;
import net.sekien.lote.system.Main;
import net.sekien.lote.util.FileHandler;
import net.sekien.lote.world.entity.Entity;
import net.sekien.lote.world.entity.EntityPlayer;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;

public class HUDUI implements UserInterface {
private Image bart;
private Image barb;
private Image bars;
private Image equip;
private SpriteSheet elements;
private int updtimer=10;
private ArrayList<EntityPlayer> nearplayers;

private boolean inited=false;

@Override
public boolean inited() {
	return inited;
}

public HUDUI() {
	nearplayers=new ArrayList<EntityPlayer>();
}

@Override
public void init(GameContainer gc, StateBasedGame sbg,
                 MessageReceiver receiver) throws SlickException {
	inited=true;
	bart=FileHandler.getImage("ui.bar_t");
	barb=FileHandler.getImage("ui.bar_b");
	bars=FileHandler.getImage("ui.bars");
	equip=FileHandler.getImage("ui.equip");
	elements=new SpriteSheet(FileHandler.getImage("ui.elements"), 16, 16);
}

@Override
public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
                   Camera cam, GameClient receiver) throws SlickException {
	bart.draw(0, 0, Main.INTERNAL_RESX, 16);
	FontRenderer.drawString(0, 0, HUDUI.getTopString(), g);
	barb.draw(0, Main.INTERNAL_RESY-64, Main.INTERNAL_RESX, 64);

	int i=0;
	if (nearplayers.size()>1)
		for (EntityPlayer ep : nearplayers) {
			int dx=i*160;
			int dy=Main.INTERNAL_RESY-64;
			equip.draw(dx, dy);
			if (ep.pdat.getEquipped()!=null)
				ep.pdat.getEquipped().getItem().spr.draw(dx+16, dy+16);
			FontRenderer.drawString(dx+60, dy+6, ep.getName(), g);
			bars.startUse();
			bars.drawEmbedded(dx+57, dy+25, dx+57+104, dy+34, 0, 75, 104, 84);
			bars.drawEmbedded(dx+57, dy+35, dx+57+104, dy+44, 0, 75, 104, 84);
			bars.drawEmbedded(dx+57, dy+45, dx+57+104, dy+54, 0, 75, 104, 84);
			if (ep.pdat!=null) {
				bars.drawEmbedded(dx+57, dy+25, dx+57+((float) ep.pdat.health/ep.pdat.healthMax)*104, dy+34, 0, 48, ((float) ep.pdat.health/ep.pdat.healthMax)*104, 57);
				bars.drawEmbedded(dx+57, dy+35, dx+57+((float) ep.pdat.magicka/ep.pdat.magickaMax)*104, dy+44, 0, 57, ((float) ep.pdat.magicka/ep.pdat.magickaMax)*104, 65);
				bars.drawEmbedded(dx+57, dy+45, dx+57+((float) ep.pdat.stamina/ep.pdat.staminaMax)*104, dy+54, 0, 65, ((float) ep.pdat.stamina/ep.pdat.staminaMax)*104, 74);
			}
			bars.endUse();
			i++;
		}
	else if (nearplayers.size()==1) {
		int dx=0;
		int dy=Main.INTERNAL_RESY-64;
		EntityPlayer ep=nearplayers.get(0);
		equip.draw(dx, dy);
		if (ep.pdat.getEquipped()!=null)
			ep.pdat.getEquipped().getItem().spr.draw(dx+16, dy+16);
		FontRenderer.drawString(dx+62, dy+25, ep.getName(), g);
		drawElement(dx+64, dy+45, ep.getElement());
		//FontRenderer.drawString(dx+62, dy+45, "("+ep.getElement().toString()+")", g);
		int bdx=Main.INTERNAL_RESX-112;
		bars.startUse();
		//bars.drawEmbedded(bdx, dy+13, bdx+104, dy+25, 0, 75, 104, 84);
		bars.drawEmbedded(bdx, dy+13, bdx+104, dy+25, 0, 36, 104, 48);
		bars.drawEmbedded(bdx, dy+26, bdx+104, dy+38, 0, 36, 104, 48);
		bars.drawEmbedded(bdx, dy+39, bdx+104, dy+51, 0, 36, 104, 48);
		bars.drawEmbedded(bdx, dy+13, bdx+((float) ep.pdat.health/ep.pdat.healthMax)*104, dy+25, 0, 0, ((float) ep.pdat.health/ep.pdat.healthMax)*104, 12);
		bars.drawEmbedded(bdx, dy+26, bdx+((float) ep.pdat.magicka/ep.pdat.magickaMax)*104, dy+38, 0, 12, ((float) ep.pdat.magicka/ep.pdat.magickaMax)*104, 24);
		bars.drawEmbedded(bdx, dy+39, bdx+((float) ep.pdat.stamina/ep.pdat.staminaMax)*104, dy+51, 0, 24, ((float) ep.pdat.stamina/ep.pdat.staminaMax)*104, 36);
		//bars.drawEmbedded(bdx, dy+13, bdx+(ep.pdat.health/60f)*104, dy+25, 0, 0, (ep.pdat.health/60f)*104, 12);
		bars.endUse();
	}
	//playerInfo.draw(Main.INTERNAL_RESX*0.5f-80,Main.INTERNAL_RESY-64);
}

private void drawElement(int x, int y, Element element) {
	switch (element) {
		case FIRE:
			elements.getSprite(0, 0).draw(x, y);
			break;
		case WATER:
			elements.getSprite(1, 0).draw(x, y);
			break;
		case EARTH:
			elements.getSprite(2, 0).draw(x, y);
			break;
		case AIR:
			elements.getSprite(3, 0).draw(x, y);
			break;
		case NEUTRAL:
			elements.getSprite(0, 1).draw(x, y);
			break;
		case VOID:
			elements.getSprite(1, 1).draw(x, y);
			break;
	}
}

private static String getTopString() {
	//FontRenderer.drawString(0, 0, "#LotE |"+Main.version+": "+(CLIENT?"|$bar.client| ":"")+(SERVER?"|$bar.server| ":"")+(MessageSystem.fastLink?"|$bar.fastlink| ":""), g);
	return "#$bar.title| |"+Main.version+":"+
			       (MessageSystem.CLIENT?" |$bar.client|":"")+
			       (MessageSystem.SERVER?" |$bar.server|":"")+
			       (MessageSystem.fastLink?" |$bar.fastlink|":"")+
			       (Globals.get("debug", false)?" |$menu.debug|":"");
}

@Override
public void update(GameContainer gc, StateBasedGame sbg, GameClient receiver) {
	if (updtimer==0) { //Calculate nearest players
		updtimer=60;
		if (receiver.player.region==null)
			return; //Do nothing if the PlayerClient.region is null.
		EntityPlayer player=((EntityPlayer) receiver.player.region.entities.get(receiver.player.entid));
		if (player==null)
			return; //Do nothing if the player is null. The player temp. goes null when switching regions.
		nearplayers.clear();
		ArrayList<EntityPlayer> temp=new ArrayList<EntityPlayer>();
		for (Entity e : receiver.player.region.entities.values()) {
			if (e instanceof EntityPlayer)
				temp.add((EntityPlayer) e);
		}
		int tsize=Math.min(temp.size(), 4);
		for (int i=0; i<tsize; i++) {
			EntityPlayer nearest=null;
			float ndist=256; //Max distance. Players past here will be ignored
			for (Entity e : temp) {
				float tnd;
				if ((tnd=(float) Math.hypot(e.x-player.x, e.y-player.y))<ndist) {
					nearest=(EntityPlayer) e;
					ndist=tnd;
				}
			}
			if (nearest!=null) {
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

@Override
public void ctor(String extd) {
}

}
