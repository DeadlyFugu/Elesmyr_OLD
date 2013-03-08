package net.halitesoft.lote.system;

import com.esotericsoftware.minlog.Log;
import org.newdawn.slick.GameContainer;

public class Input {
public static boolean isKeyPressed(GameContainer gc, String name) {
	String val=Globals.get("IN_"+name, "null");
	if (val.equals("null"))
		return false;
	String type=val.split("_")[0];
	int keyid=Integer.parseInt(val.split("_")[1]);
	if (type.equals("KEY"))
		return gc.getInput().isKeyPressed(keyid);
	if (type.equals("MOUSE"))
		return gc.getInput().isMousePressed(keyid);
	if (type.equals("GC"))
		return gc.getInput().isButtonPressed(0, keyid);
	Log.warn("Input", "Type not found '"+type+"' for key "+name);
	return false;
}

public static void setKey(String name, String value) {
	Globals.set("IN_"+name, value);
}

}
