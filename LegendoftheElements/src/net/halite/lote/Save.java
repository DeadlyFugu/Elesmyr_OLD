package net.halite.lote;

import net.halite.hbt.*;
import net.halite.lote.util.FileHandler;
import net.halite.lote.util.HashmapLoader;
import net.halite.lote.world.Region;
import net.halite.lote.world.World;
import net.halite.lote.world.entity.Entity;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class Save {

String name;
@Deprecated HashMap<String, String> hmData;
HBTCompound data;

public Save(String name) {
	this.name=name;

	ArrayList<File> temp=new ArrayList<File>();
	File[] fileList=new File("save/"+name).listFiles();

	for (int i=0; i<fileList.length; i++) {
		File choose=fileList[i];
		if (choose.isFile()&&!temp.contains(choose)&&!choose.getName().matches("(data\\.hbt(|x|c)|)")) {
			temp.add(choose);
		}
	}

	hmData=new HashMap<String, String>();

	for (File f : temp) {
		hmData.putAll(HashmapLoader.readHashmapWHeader(f.getName()+".", f.getPath()));
	}

	data= new HBTCompound("saveroot");
	try {
		for (HBTTag tag : FileHandler.readHBT("save/"+name+"/dataunc",false)) {
			data.addTag(tag);
		}
	} catch (IOException e) {
		e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	}
}

@Deprecated public String get(String key) {
	return hmData.get(key);
}

@Deprecated public void put(String key, String value) {
	hmData.put(key, value);
}

public HBTTag getTag(String name) throws HBTCompound.TagNotFoundException {
	return data.getTag(name);
}

public HBTCompound getCompound(String name) {
	try {return (HBTCompound) getTag(name);}
	catch (ClassCastException e) {Log.warn("tag:"+name, e); return new HBTCompound(name);}
	catch (HBTCompound.TagNotFoundException e) {return new HBTCompound(name);}
}

public byte getByte(String name, byte def) {
	try {return ((HBTByte) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public short getShort(String name, short def) {
	try {return ((HBTShort) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public int getInt(String name, int def) {
	try {return ((HBTInt) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public long getLong(String name, long def) {
	try {return ((HBTLong) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public float getFloat(String name, float def) {
	try {return ((HBTFloat) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public double getDouble(String name, double def) {
	try {return ((HBTDouble) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public String getString(String name, String def) {
	try {return ((HBTString) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public byte[] getByteArray(String name, byte[] def) {
	try {return ((HBTByteArray) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public HBTFlag getFlag(String name, String def) {
	try {return (HBTFlag) getTag(name);}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return new HBTFlag(name, def);}
	catch (HBTCompound.TagNotFoundException e) {return new HBTFlag(name, def);}
}

public void putTag(String name, HBTTag tag) throws HBTCompound.TagNotFoundException {
	data.setTag(name, tag);
}

public void putByte(String name, byte data) {
	putTag(name, new HBTByte(name.substring(name.lastIndexOf('.')+1), data));
}

public void putShort(String name, short data) {
	putTag(name,new HBTShort(name.substring(name.lastIndexOf('.')+1),data));
}

public void putInt(String name, int data) {
	putTag(name,new HBTInt(name.substring(name.lastIndexOf('.')+1),data));
}

public void putLong(String name, long data) {
	putTag(name,new HBTLong(name.substring(name.lastIndexOf('.')+1),data));
}

public void putFloat(String name, float data) {
	putTag(name,new HBTFloat(name.substring(name.lastIndexOf('.')+1),data));
}

public void putDouble(String name, double data) {
	putTag(name,new HBTDouble(name.substring(name.lastIndexOf('.')+1),data));
}

public void putByteArray(String name, byte[] data) {
	putTag(name,new HBTByteArray(name.substring(name.lastIndexOf('.')+1),data));
}

public void putString(String name, String data) {
	putTag(name,new HBTString(name.substring(name.lastIndexOf('.')+1),data));
}


public void putCompound(String name, HBTCompound data) {
	putTag(name,data);
}

public void write() {
	HashMap<String, HashMap<String, String>> dataHSep=new HashMap<String, HashMap<String, String>>(); //hmData with the headers separated.
	for (Entry<String, String> e : hmData.entrySet()) {
		String head=e.getKey().split("\\.", 2)[0];
		String key=e.getKey().split("\\.", 2)[1];
		if (!dataHSep.containsKey(head))
			dataHSep.put(head, new HashMap<String, String>());
		dataHSep.get(head).put(key, e.getValue());
	}
	for (Entry<String, HashMap<String, String>> e : dataHSep.entrySet()) {
		HashmapLoader.writeHashmap("save/"+name+"/"+e.getKey(), e.getValue());
	}
	putLong("meta.savedate", new Date().getTime());
	//System.out.println(data);
	try {
		HBTOutputStream os = new HBTOutputStream(new FileOutputStream("save/"+name+"/dataunc.hbt"),false);
		for (HBTTag tag : data)
			os.write(tag);
		os.close();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
}

public void putPlayer(String name, String data, World world) {
	if (data==null)
		return;
	Entity player=world.getRegion(data.split("\\.")[0]).entities.get(Integer.parseInt(data.split("\\.", 2)[1])); //Can sometimes cause null pointer.
	put("players."+name, data.split("\\.")[0]+","+player.x+","+player.y);
}

public void putPlayer(String name, String data, Region region) {
	Entity player=region.entities.get(Integer.parseInt(data.split("\\.", 2)[1]));
	put("players."+name, data.split("\\.")[0]+","+player.x+","+player.y);
}

@Deprecated public Set<Entry<String, String>> getEntries() {
	return hmData.entrySet();
}

public void clearTag(String world) {
	HBTCompound tag =data;
	while (world.contains(".")) {
		tag=tag.getCompound(world.split("\\.",2)[0]);
		world = world.split("\\.",2)[1];
	}
	HBTTag toDel=null;
	for (HBTTag tag1 : tag) {
		if (tag1.getName().equals(world)) toDel=tag;
	}
	tag.getData().remove(toDel);
	if (toDel==null)
		Log.warn("Finding tag "+world+" failed.");
}
}
