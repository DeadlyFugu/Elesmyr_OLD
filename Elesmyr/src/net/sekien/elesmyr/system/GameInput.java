/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.system;

import com.esotericsoftware.minlog.Log;
import org.newdawn.slick.*;

public class GameInput {
	public static boolean isKeyPressed(GameContainer gc, String name) {
		String val = Globals.get("IN_"+name, "null");
		if (val.equals("null"))
			return false;
		String type = val.split("_")[0];
		int keyid = Integer.parseInt(val.split("_")[1]);
		if (type.equals("KEY"))
			return gc.getInput().isKeyPressed(keyid);
		if (type.equals("MOUSE"))
			return gc.getInput().isMousePressed(keyid);
		if (type.equals("GC"))
			return gc.getInput().isButtonPressed(0, keyid);
		Log.warn("Input", "Type not found '"+type+"' for key "+name);
		return false;
	}

	public static boolean isUpPressed(GameContainer gc) {
		return gc.getInput().isKeyPressed(org.newdawn.slick.Input.KEY_UP);
	}

	public static boolean isDownPressed(GameContainer gc) {
		return gc.getInput().isKeyPressed(org.newdawn.slick.Input.KEY_DOWN);
	}

	public static boolean isLeftPressed(GameContainer gc) {
		return gc.getInput().isKeyPressed(org.newdawn.slick.Input.KEY_LEFT);
	}

	public static boolean isRightPressed(GameContainer gc) {
		return gc.getInput().isKeyPressed(org.newdawn.slick.Input.KEY_RIGHT);
	}

	public static void setKey(String name, String value) {
		Globals.set("IN_"+name, value);
	}

}
