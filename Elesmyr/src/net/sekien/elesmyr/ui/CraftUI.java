/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.ui;

import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.msgsys.MessageSystem;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.player.InventoryEntry;
import net.sekien.elesmyr.player.PlayerData;
import net.sekien.elesmyr.system.FontRenderer;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.Main;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.elesmyr.util.HashmapLoader;
import net.sekien.elesmyr.util.ResourceType;
import net.sekien.elesmyr.world.Region;
import net.sekien.elesmyr.world.entity.EntityPlayer;
import net.sekien.elesmyr.world.item.ItemFactory;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTTools;
import net.sekien.pepper.ListNode;
import net.sekien.pepper.Renderer;
import org.newdawn.slick.*;

import java.util.ArrayList;
import java.util.Map.Entry;

public class CraftUI implements UserInterface {

	public static class Craftable {
		private String result;
		private String[] ingredients;

		public Craftable(String result, String recipe) {
			this.result = result;
			this.ingredients = recipe.split("\\|");
		}

		public boolean isCraftable(ArrayList<InventoryEntry> inv) {
			for (String s : ingredients) {
				boolean matched = false;
				String[] parts = parseIng(s);
				for (InventoryEntry ie : inv) {
					if (ie.getItem().name.equals(parts[1]) /*&& ie.getExtd().equals(parts[2])*/ && ie.getCount() >= Integer.parseInt(parts[0])) {
						matched = true;
						break;
					}
				}
				if (!matched)
					return false;
			}
			return true;
		}

		private String[] parseIng(String s) {
			String[] ret = new String[]{"1", "", ""};
			if (s.contains("*")) {
				ret[0] = s.split("\\*")[0];
				s = s.split("\\*")[1];
			}
			if (s.contains(":")) {
				ret[1] = s.split(":")[0];
				ret[2] = s.split(":")[1];
			} else {
				ret[1] = s;
			}
			return ret;
		}

		public String toString() {
			String out = result;
			boolean metBefore = false;
			for (String s : ingredients) {
				String[] parts = parseIng(s);
				if (metBefore)
					out = out+",";
				out = out+"| "+parts[0]+"x|$item."+parts[1]+(parts[2].equals("")?"|":"|: "+parts[2]);
				metBefore = true;
			}
			return out;
		}

		public void addToPDAT(PlayerData pdat, Region r, String ent, EntityPlayer ep) {
			for (String s : ingredients) {
				boolean matched = false;
				String[] parts = parseIng(s);
				for (InventoryEntry ie : pdat.inventory) {
					if (ie.getItem().name.equals(parts[1]) && ie.getExtd().equals(parts[2]) && ie.getCount() >= Integer.parseInt(parts[0])) {
						matched = true;
						for (int i = 0; i < Integer.parseInt(parts[0]); i++)
							pdat.removeItem(pdat.inventory.indexOf(ie), r, ent);
						break;
					}
				}
				if (!matched)
					return;
			}
			pdat.put(ItemFactory.getItem(result), new HBTCompound("iextd"), r, ent, ep);
		}
	}

	private static ArrayList<Craftable> recipes;

	static {
		recipes = new ArrayList<Craftable>();
		for (Entry<String, String> e : HashmapLoader.readHashmap(FileHandler.parse("craft_def", ResourceType.PLAIN)).entrySet()) //TODO: use HBT here
			recipes.add(new Craftable(e.getKey(), e.getValue()));
	}

	private int isel = 0;
	private int smax = 1;
	private static final int width = ListNode.width;

	private boolean inited = false;

	@Override
	public boolean inited() {
		return inited;
	}

	@Override
	public void ctor(String extd) {

	}

	@Override
	public void init(GameContainer gc,
	                 MessageEndPoint receiver) throws SlickException {
		inited = true;
	}

	@Override
	public void render(Renderer renderer, Camera cam, GameClient receiver) throws SlickException {
		Graphics g = renderer.g;
		int w = Main.INTERNAL_RESX;
		int h = Main.INTERNAL_RESY;
		int awidth = width;
		int border = (w-width)/2;
		if (border < 0) {
			border = 0;
			awidth = w;
		}
		renderer.rectPos(border, 16, w-border, 16+82, false, true, true, true, Renderer.BoxStyle.FULL);
		renderer.rectPos(border, 16+82, w-border, h-64, false, false, true, true, Renderer.BoxStyle.FULL);

		FontRenderer.drawString(border+77, 17, "Crafting", g);

		int i = 0;
		ArrayList<InventoryEntry> inv = ((EntityPlayer) receiver.player.region.entities.get(receiver.player.entid)).pdat.inventory;
		for (Craftable c : recipes) {
			if (c.isCraftable(inv)) {
				g.setColor(Color.lightGray);
				if (i == isel)
					renderer.rect(border, 116+i*38, awidth, 36, Renderer.BoxStyle.SEL);
				g.setColor(Color.white);
				//iei.spr.draw(xoff+78,120+i*38);
				FontRenderer.drawString(border+117, 128+i*38, "#$item."+c.toString(), g);
				//Main.font.drawString(xoff+450,128+i*38, ""+ie.getCount());
				//Main.font.drawString(526,128+i*40,"$"+ie.getValue()); //TODO: Value thingies
				//Main.font.drawString(xoff+526,128+i*38, ie.getExtd());

				i++;
			}
		}
		smax = i;
	}

	@Override
	public void update(GameContainer gc, GameClient receiver) {
		Input in = gc.getInput();
		if (in.isKeyPressed(Input.KEY_UP))
			if (isel > 0)
				isel--;
		if (in.isKeyPressed(Input.KEY_DOWN))
			if (isel < smax-1)
				isel++;
		if (in.isKeyPressed(Input.KEY_X)) {
			ArrayList<InventoryEntry> inv = ((EntityPlayer) receiver.player.region.entities.get(receiver.player.entid)).pdat.inventory;
			int i = 0;
			int iv = -1;
			for (Craftable c : recipes) {
				if (c.isCraftable(inv))
					iv++;
				if (iv == isel) {
					MessageSystem.sendServer(null, new Message(receiver.player.region.name+"."+receiver.player.entid+".craftItem", HBTTools.msgInt("i", i)), false);
				}
				i++;
			}
		}
		if (isel > smax-1)
			isel = smax-1;
	}

	@Override
	public boolean blockUpdates() {
		return true;
	}

	public static Craftable getRecipe(int index) {
		return recipes.get(index);
	}
}
