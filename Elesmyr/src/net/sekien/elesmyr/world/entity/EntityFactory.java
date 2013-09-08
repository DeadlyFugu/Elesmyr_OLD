/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.entity;

import groovy.lang.GroovyObject;
import net.sekien.elesmyr.ScriptRunner;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.world.Region;
import net.sekien.hbt.HBTCompound;

public class EntityFactory {
public static Entity getEntity(HBTCompound tag, Region r, MessageEndPoint receiver) {
	String eclass = tag.getString("class", "Entity");
	int id = tag.getInt("id", 0);
	int x = tag.getInt("x", 0);
	int y = tag.getInt("y", 0);
	try {
		Entity e = (Entity) Class.forName("net.sekien.elesmyr.world.entity."+eclass).newInstance();
		e.ctor(id, x, y, tag, r.name+"."+id, r, receiver);
		return e;
	} catch (ClassNotFoundException e) {
		GroovyObject go = ScriptRunner.get(eclass);
		if (go!=null) {
			go.invokeMethod("ctor", new Object[]{id, x, y, tag, r.name+"."+id, r});
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
