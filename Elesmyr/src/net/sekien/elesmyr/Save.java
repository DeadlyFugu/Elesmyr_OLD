/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr;

import net.sekien.elesmyr.system.Globals;
import net.sekien.elesmyr.system.Renderer;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.elesmyr.world.Region;
import net.sekien.elesmyr.world.World;
import net.sekien.elesmyr.world.entity.Entity;
import net.sekien.hbt.*;
import net.sekien.pepper.StateManager;
import org.newdawn.slick.*;
import org.newdawn.slick.imageout.*;
import org.newdawn.slick.util.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class Save {

String name;
HBTCompound data;

public Save(String name) {
	this.name = name;

	data = new HBTCompound("saveroot");
	try {
		for (HBTTag tag : FileHandler.readHBT("save/"+name, false)) {
			data.addTag(tag);
		}
	} catch (IOException e) {
		e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	}
}

public HBTTag getTag(String name) throws TagNotFoundException {
	return data.getTag(name);
}

public HBTCompound getCompound(String name) {
	try {return (HBTCompound) getTag(name);} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return new HBTCompound(name);
	} catch (TagNotFoundException e) {return new HBTCompound(name);}
}

public byte getByte(String name, byte def) {
	try {return ((HBTByte) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public short getShort(String name, short def) {
	try {return ((HBTShort) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public int getInt(String name, int def) {
	try {return ((HBTInt) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public long getLong(String name, long def) {
	try {return ((HBTLong) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public float getFloat(String name, float def) {
	try {return ((HBTFloat) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public double getDouble(String name, double def) {
	try {return ((HBTDouble) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public String getString(String name, String def) {
	try {return ((HBTString) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public byte[] getByteArray(String name, byte[] def) {
	try {return ((HBTByteArray) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (TagNotFoundException e) {return def;}
}

public HBTFlag getFlag(String name, String def) {
	try {return (HBTFlag) getTag(name);} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return new HBTFlag(name, def);
	} catch (TagNotFoundException e) {return new HBTFlag(name, def);}
}

public void putTag(String name, HBTTag tag) throws TagNotFoundException {
	data.setTag(name, tag);
}

public void putByte(String name, byte data) {
	putTag(name, new HBTByte(name.substring(name.lastIndexOf('.')+1), data));
}

public void putShort(String name, short data) {
	putTag(name, new HBTShort(name.substring(name.lastIndexOf('.')+1), data));
}

public void putInt(String name, int data) {
	putTag(name, new HBTInt(name.substring(name.lastIndexOf('.')+1), data));
}

public void putLong(String name, long data) {
	putTag(name, new HBTLong(name.substring(name.lastIndexOf('.')+1), data));
}

public void putFloat(String name, float data) {
	putTag(name, new HBTFloat(name.substring(name.lastIndexOf('.')+1), data));
}

public void putDouble(String name, double data) {
	putTag(name, new HBTDouble(name.substring(name.lastIndexOf('.')+1), data));
}

public void putByteArray(String name, byte[] data) {
	putTag(name, new HBTByteArray(name.substring(name.lastIndexOf('.')+1), data));
}

public void putString(String name, String data) {
	putTag(name, new HBTString(name.substring(name.lastIndexOf('.')+1), data));
}

public void putCompound(String name, HBTCompound data) {
	putTag(name, data);
}

public void write() {
	putLong("meta.savedate", new Date().getTime());
	try {
		HBTOutputStream os = new HBTOutputStream(new FileOutputStream("save/"+name+".hbt"), false);
		for (HBTTag tag : data)
			os.write(tag);
		os.close();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	saveScreen();
	Globals.set("lastSave", name);
	Globals.save();
}

public void putPlayer(String name, String data, World world) {
	if (data==null)
		return;
	Entity player = world.getRegion(data.split("\\.")[0]).entities.get(Integer.parseInt(data.split("\\.", 2)[1])); //Can sometimes cause null pointer.
	//put("players."+name, data.split("\\.")[0]+","+player.x+","+player.y);
	HBTCompound pTag = new HBTCompound(name);
	pTag.addTag(new HBTString("region", data.split("\\.")[0])); //TODO: Proper player to hbt
	pTag.addTag(new HBTInt("x", player.x));
	pTag.addTag(new HBTInt("y", player.y));
	putTag("players."+name, pTag);
}

public void putPlayer(String name, String data, Region region) {
	Entity player = region.entities.get(Integer.parseInt(data.split("\\.", 2)[1]));
	//put("players."+name, data.split("\\.")[0]+","+player.x+","+player.y);
	HBTCompound pTag = new HBTCompound(name);
	pTag.addTag(new HBTString("region", data.split("\\.")[0])); //TODO: Proper player to hbt
	pTag.addTag(new HBTInt("x", player.x));
	pTag.addTag(new HBTInt("y", player.y));
	putTag("players."+name, pTag);
}

public void clearTag(String world) {
	HBTCompound tag = data;
	while (world.contains(".")) {
		tag = tag.getCompound(world.split("\\.", 2)[0]);
		world = world.split("\\.", 2)[1];
	}
	HBTTag toDel = null;
	for (HBTTag tag1 : tag) {
		if (tag1.getName().equals(world)) toDel = tag;
	}
	tag.getData().remove(toDel);
	if (toDel==null)
		Log.warn("Finding tag "+world+" failed.");
}

public void saveScreen() {
	try {
		if (!new File("save/thumb").exists())
			new File("save/thumb").mkdir();
		ImageOut.write(Renderer.getScreen(), ImageOut.PNG, "save/thumb/"+name+".png");
		StateManager.setBackground(name);
	} catch (SlickException e) {
		e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	}
}
}
