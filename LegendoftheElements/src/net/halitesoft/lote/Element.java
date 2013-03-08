package net.halitesoft.lote;

public enum Element {
	//		Neu	Ear	Wat	Fir	Air	Void
	NEUTRAL(1, 1, 1, 1, 1, 0.25f),
	EARTH(1, 1, .5f, 2, 1, 0.25f),
	WATER(1, 2, 1, 1, .5f, 0.25f),
	FIRE(1, .5f, 1, 1, 2, 0.25f),
	AIR(1, 1, 2, .5f, 1, 0.25f),
	VOID(4, 4, 4, 4, 4, 1);            //...-AIR-FIRE-EARTH-WATER-AIR-...

private float[] multTable;

private Element(float n, float e, float w, float f, float a, float v) {
	this.multTable=new float[]{n, e, w, f, a, v};
}

public float multAgainst(Element other) {
	switch (other) {
		case NEUTRAL: return this.multTable[0];
		case EARTH: return this.multTable[1];
		case WATER: return this.multTable[2];
		case FIRE: return this.multTable[3];
		case AIR: return this.multTable[4];
		case VOID: return this.multTable[5];
		default: return 1;
	}
}
}
