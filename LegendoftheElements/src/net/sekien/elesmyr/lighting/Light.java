package net.sekien.elesmyr.lighting;

import java.util.Random;

public class Light {
public int x;
public int y;
public int dist;
public int bdist;
public float r;
public float g;
public float b;
public float a;

public Light(int x, int y, int dist, float r, float g, float b, float a) {
	this.x = x;
	this.y = y;
	this.dist = bdist = dist;
	this.r = r;
	this.g = g;
	this.b = b;
	this.a = a;
}

public Light(String l) {
	String[] param = l.split(",", 7);
	if (param.length==7) {
		this.x = Integer.parseInt(param[0]);
		this.y = Integer.parseInt(param[1]);
		this.dist = Integer.parseInt(param[2]);

		this.r = Float.parseFloat(param[3]);
		this.g = Float.parseFloat(param[4]);
		this.b = Float.parseFloat(param[5]);
		this.a = Float.parseFloat(param[6]);
	}
}

public void move(int x, int y) {
	this.x = x;
	this.y = y;
}

public void randomize() {
	Random rand = new Random();
	dist = bdist+16-rand.nextInt(8);
	x += 1-rand.nextInt(3);
	y += 1-rand.nextInt(3);
}
}
