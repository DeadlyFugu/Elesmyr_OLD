package net.halite.lote;

import net.halite.lote.util.HashmapLoader;
import net.halite.lote.world.Region;
import net.halite.lote.world.World;
import net.halite.lote.world.entity.Entity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class Save {

String name;
HashMap<String, String> data;

public Save(String name) {
	this.name=name;

	ArrayList<File> temp=new ArrayList<File>();
	File[] fileList=new File("save/"+name).listFiles();

	for (int i=0; i<fileList.length; i++) {
		File choose=fileList[i];
		if (choose.isFile()&&!temp.contains(choose)) {
			temp.add(choose);
		}
	}

	data=new HashMap<String, String>();

	for (File f : temp) {
		data.putAll(HashmapLoader.readHashmapWHeader(f.getName()+".", f.getPath()));
	}
}

public String get(String key) {
	return data.get(key);
}

public void put(String key, String value) {
	data.put(key, value);
}

public void write() {
	HashMap<String, HashMap<String, String>> dataHSep=new HashMap<String, HashMap<String, String>>(); //data with the headers separated.
	for (Entry<String, String> e : data.entrySet()) {
		String head=e.getKey().split("\\.", 2)[0];
		String key=e.getKey().split("\\.", 2)[1];
		if (!dataHSep.containsKey(head))
			dataHSep.put(head, new HashMap<String, String>());
		dataHSep.get(head).put(key, e.getValue());
	}
	for (Entry<String, HashMap<String, String>> e : dataHSep.entrySet()) {
		HashmapLoader.writeHashmap("save/"+name+"/"+e.getKey(), e.getValue());
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

public Set<Entry<String, String>> getEntries() {
	return data.entrySet();
}
}
