package net.sekien.lote.world.item;

import com.esotericsoftware.minlog.Log;
import groovy.lang.GroovyObject;
import net.sekien.hbt.HBTTag;
import net.sekien.lote.ScriptRunner;
import net.sekien.lote.util.FileHandler;

import java.util.HashMap;

public class ItemFactory {
private static HashMap<String, Item> items=null;

public static void init() {
	if (items!=null)
		return;
	items=new HashMap<String, Item>();
	//HashMap<String, String> item_str=HashmapLoader.readHashmap(FileHandler.parse("item_def", ResourceType.PLAIN));
	for (HBTTag tag : FileHandler.getCompound("items")) {
		if (tag instanceof net.sekien.hbt.HBTCompound)
			items.put(tag.getName(), parseItem(tag.getName(), (net.sekien.hbt.HBTCompound) tag));
	}
}

private static Item parseItem(String name, net.sekien.hbt.HBTCompound tag) {
	try {
		Item i=(Item) Class.forName("net.sekien.lote.world.item."+tag.getString("class", "Item")).newInstance();
		i.ctor(name, tag.getString("image", "null"), tag);
		return i;
	} catch (ClassNotFoundException e) {
		GroovyObject go=ScriptRunner.get(tag.getString("class", "Item"));
		if (go!=null) {
			go.invokeMethod("ctor", new Object[]{name, tag.getString("image", "null"), tag});
			return (Item) go.invokeMethod("toItem", new Object[0]);
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	Log.warn("Invalid item entry "+name+":\n"+tag);

	return new Item().ctor("Null", "null", new net.sekien.hbt.HBTCompound("Null"));
}

public static Item getItem(String str) {
	if (!items.containsKey(str))
		return items.get("Null");
	return items.get(str);
}
}
