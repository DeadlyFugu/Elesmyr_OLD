/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world;

import net.sekien.elesmyr.world.WeatherSystem.WeatherType;

public class WeatherCondition {
private WeatherType type;
private float strength;
private float dist; //Used by storms to determine brightness of lighting and volume/delay of thunder.

public WeatherCondition() {
	this.type = WeatherType.CLEAR;
	this.strength = 0;
	this.dist = 0;
}

public void set(WeatherType type, float strength, float dist) {
	this.type = type;
	this.strength = strength;
	this.dist = dist;
}

public float[] getAmbCol(float[] timeBasedAmb) {
	switch (type) {
		case FOG:
			return mult4f(timeBasedAmb, new float[]{0.37f, 0.38f, 0.4f, 0.9f});
		default:
			return timeBasedAmb;
	}
}

private float[] mult4f(float[] a, float[] b) {
	float[] c = new float[4];
	for (int i = 0; i < 3; i++) {
		c[i] = b[i]+(a[i]-b[i])*(a[3]/1.25f);
	}
	c[3] = Math.max(a[3], b[3]);
	return c;
}

public String toString() {
	return type+" str="+strength+" dist="+dist;
}
}
