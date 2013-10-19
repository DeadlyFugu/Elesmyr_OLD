/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.util;

import net.sekien.tiled.MapObject;
import net.sekien.tiled.TiledMapPlus;
import org.newdawn.slick.geom.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 9/06/13 Time: 10:39 AM To change this template use File | Settings |
 * File Templates.
 */
public class PointSensor {

	public static boolean update(TiledMapPlus map, int x, int y) {
		System.out.println("map.getObjectGroups() = "+map.getObjectGroups());
		try {
			map.getObjectGroup("col");
		} catch (NullPointerException npe) {
			System.err.println("col group missing");
			return false;
		}
		for (MapObject obj : map.getObjectGroup("col").getObjects()) {
			if (obj.objectType == MapObject.ObjectType.RECTANGLE || obj.objectType == MapObject.ObjectType.POLYGON) {
				Shape shape;
				if (obj.objectType == MapObject.ObjectType.POLYGON) {
					shape = obj.points.transform(Transform.createTranslateTransform(obj.x, obj.y));
				} else {
					shape = new Rectangle(obj.x, obj.y, obj.width, obj.height);
				}
				if (shape.contains(x, y)) {
					return true;
				}
			}
		}
		return false;
	}
}
