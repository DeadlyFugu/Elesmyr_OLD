package net.halite.lote.world.item;

import com.esotericsoftware.minlog.Log;
import groovy.lang.GroovyObject;
import net.halite.lote.ScriptRunner;
import net.halite.lote.util.FileHandler;
import net.halite.lote.util.HashmapLoader;
import net.halite.lote.util.ResourceType;

import java.util.HashMap;
import java.util.Map.Entry;

public class ItemFactory {
private static HashMap<String, Item> items=null;

public static void init() {
	if (items!=null)
		return;
	items=new HashMap<String, Item>();
	HashMap<String, String> item_str=HashmapLoader.readHashmap(FileHandler.parse("item_def", ResourceType.PLAIN));
	for (Entry<String, String> e : item_str.entrySet()) {
		items.put(e.getKey(), parseItem(e.getKey(), e.getValue()));
	}
}

private static Item parseItem(String name, String str) {
	String[] parts=str.split(",", 3);
	if (parts.length==3)
		try {
			Item i=(Item) Class.forName("net.halite.lote.world.item."+parts[0]).newInstance();
			i.ctor(name, parts[1], parts[2]);
			return i;
		} catch (ClassNotFoundException e) {
			GroovyObject go=ScriptRunner.get(parts[0]);
			if (go!=null) {
				go.invokeMethod("ctor", new Object[]{name, parts[1], parts[2]});
				return (Item) go.invokeMethod("toItem", new Object[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	Log.warn("Invalid item entry "+name+": "+str);

	return new Item().ctor("Null", "null", "");
}

public static Item getItem(String str) {
	return items.get(str);
}
}
