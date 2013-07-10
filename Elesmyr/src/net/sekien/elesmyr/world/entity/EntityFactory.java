package net.sekien.elesmyr.world.entity;

import groovy.lang.GroovyObject;
import net.sekien.elesmyr.ScriptRunner;
import net.sekien.elesmyr.world.Region;
import net.sekien.hbt.HBTCompound;

public class EntityFactory {
public static Entity getEntity(HBTCompound tag, Region r) {
	String eclass = tag.getString("class", "Entity");
	int id = tag.getInt("name", 0); // why is ID called name? :S
	int x = tag.getInt("x", 0);
	int y = tag.getInt("y", 0);
	try {
		Entity e = (Entity) Class.forName("net.sekien.elesmyr.world.entity."+eclass).newInstance();
		e.ctor(""+id, x, y, tag, r.name+"."+id, r);
		return e;
	} catch (ClassNotFoundException e) {
		GroovyObject go = ScriptRunner.get(eclass);
		if (go!=null) {
			go.invokeMethod("ctor", new Object[]{""+id, x, y, tag, r.name+"."+id, r});
			return (Entity) go.invokeMethod("toEntity", new Object[0]);
		}
	} catch (InstantiationException e1) {
		e1.printStackTrace();
	} catch (IllegalAccessException e1) {
		e1.printStackTrace();
	}
	//return new EntityEnemy(parts[1],Integer.parseInt(parts[2]),Integer.parseInt(parts[3]));
	System.out.println("ENTITYFACTORY FAILED MAKING tag="+tag+" r="+r);
	return null;
}
}
