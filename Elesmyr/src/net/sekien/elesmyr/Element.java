/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr;

import org.newdawn.slick.*;

public enum Element {
	//		Neu	Ear	Wat	Fir	Air	Void
	NEUTRAL(1, 1, 1, 1, 1, 0.5f, Color.white),
	EARTH(1, 1, 2, .5f, 1, 0.5f, new Color(120, 213, 0)),
	WATER(1, .5f, 1, 1, 2, 0.5f, new Color(64, 169, 242)),
	FIRE(1, 2, 1, 1, .5f, 0.5f, new Color(255, 68, 31)),
	AIR(1, 1, .5f, 2, 1, 0.5f, new Color(0.87f, 0.97f, 1.0f, 0.35f)),
	VOID(2, 2, 2, 2, 2, 1, new Color(84, 62, 78));            //...-AIR-FIRE-EARTH-WATER-AIR-...

private float[] multTable;
private Color color;

private Element(float n, float e, float w, float f, float a, float v, Color color) {
	this.color = color;
	this.multTable = new float[]{n, e, w, f, a, v};
}

public float multAgainst(Element other) {
	switch (other) {
		case NEUTRAL:
			return this.multTable[0];
		case EARTH:
			return this.multTable[1];
		case WATER:
			return this.multTable[2];
		case FIRE:
			return this.multTable[3];
		case AIR:
			return this.multTable[4];
		case VOID:
			return this.multTable[5];
		default:
			return 1;
	}
}

public Color color() {
	return color;
}
}
