package net.halite.lote.world;

import net.halite.lote.world.WeatherSystem.WeatherType;

public class WeatherCondition {
private WeatherType type;
private float strength;
private float dist; //Used by storms to determine brightness of lighting and volume/delay of thunder.

public WeatherCondition() {
	this.type=WeatherType.CLEAR;
	this.strength=0;
	this.dist=0;
}

public void set(WeatherType type, float strength, float dist) {
	this.type=type;
	this.strength=strength;
	this.dist=dist;
}

public float[] getAmbCol(float[] timeBasedAmb) {
	switch (type) {
		case FOG: return new float[]{0.37f, 0.38f, 0.4f, 0.9f};
		default: return timeBasedAmb;
	}
}

public String toString() {
	return type+" str="+strength+" dist="+dist;
}
}
