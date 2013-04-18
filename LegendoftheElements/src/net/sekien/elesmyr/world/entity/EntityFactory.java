package net.sekien.elesmyr.world.entity;

import groovy.lang.GroovyObject;
import net.sekien.elesmyr.ScriptRunner;
import net.sekien.elesmyr.world.Region;

public class EntityFactory {
public static Entity getEntity(String str, Region r) {
	//Note: str /should/ be formatted as
	String[] parts = str.split(",", 5);
	if (parts.length==5)
		try {
			Entity e = (Entity) Class.forName("net.sekien.elesmyr.world.entity."+parts[0]).newInstance();
			e.ctor(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), parts[4], r.name+"."+parts[1], r);
			return e;
		} catch (ClassNotFoundException e) {
			GroovyObject go = ScriptRunner.get(parts[0]);
			if (go!=null) {
				go.invokeMethod("ctor", new Object[]{parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), parts[4], r.name+"."+parts[1], r});
				return (Entity) go.invokeMethod("toEntity", new Object[0]);
			}
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
	//return new EntityEnemy(parts[1],Integer.parseInt(parts[2]),Integer.parseInt(parts[3]));
	System.out.println("ENTITYFACTORY FAILED MAKING str="+str+" r="+r);
	return null;
}
}
