/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.util;

import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.player.PlayerClient;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.world.entity.Entity;
import org.newdawn.slick.*;

public class HintHelper {
	private static boolean loaded = false;
	private static Image keyInteract;
	private static Image keyAttack;
	private static Image keyInventory;
	private static Image keyCharaMenu;

	public static void interact(Entity e, Graphics g, GameClient receiver, Camera camera, int xoff, int yoff, int cx, int cy) {
		lazyLoad();
		showHint(e, g, receiver, keyInteract, camera, xoff, yoff, cx, cy);
	}

	public static void attack(Entity e, Graphics g, GameClient receiver, Camera camera, int xoff, int yoff, int cx, int cy) {
		lazyLoad();
		showHint(e, g, receiver, keyAttack, camera, xoff, yoff, cx, cy);
	}

	public static void inventory(Entity e, Graphics g, GameClient receiver, Camera camera, int xoff, int yoff, int cx, int cy) {
		lazyLoad();
		showHint(e, g, receiver, keyInventory, camera, xoff, yoff, cx, cy);
	}

	public static void charaMenu(Entity e, Graphics g, GameClient receiver, Camera camera, int xoff, int yoff, int cx, int cy) {
		lazyLoad();
		showHint(e, g, receiver, keyCharaMenu, camera, xoff, yoff, cx, cy);
	}

	private static void showHint(Entity e, Graphics g, GameClient receiver, Image hintKey, Camera camera, int xoff, int yoff, int cx, int cy) {
		if (receiver.getPlayer().distanceFrom(e, cx, cy) < PlayerClient.INTERACT_DISTANCE)
			hintKey.draw(camera.getXOff()+e.xs+xoff, camera.getYOff()+e.ys+yoff);
	}

	private static void lazyLoad() {
		if (!loaded) {
			loaded = true;
			try {
				Image keys = FileHandler.getImage("ui.hint");
				keyInteract = keys.getSubImage(0, 0, 16, 16);
				keyAttack = keys.getSubImage(16, 0, 16, 16);
				keyInventory = keys.getSubImage(32, 0, 16, 16);
				keyCharaMenu = keys.getSubImage(48, 0, 16, 16);
			} catch (SlickException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
