package net.shard.lote.system;


import net.shard.lote.world.Region;

import org.newdawn.slick.tiled.TiledMap;

public class Camera {
	float x,tx;
	float y,ty;
	int xoff,yoff;
	
	Camera(int x,int y) {
		this.x = tx = x;
		this.y = ty = y;
	}
	
	public void update(PlayerClient player) {
		tx = (player.x);
		ty = (player.y);
		if (tx<Main.INTERNAL_RESX/2)
			tx=Main.INTERNAL_RESX/2;
		if (ty<Main.INTERNAL_RESY/2)
			ty=Main.INTERNAL_RESY/2;
		Region pr = player.getRegion();
		if (pr != null) {
			TiledMap map = pr.map;
			if (tx>map.getWidth()*32-Main.INTERNAL_RESX/2)
				tx=map.getWidth()*32-Main.INTERNAL_RESX/2;
			if (ty>map.getHeight()*32-Main.INTERNAL_RESY/2)
				ty=map.getHeight()*32-Main.INTERNAL_RESY/2;
		}
		
		x += (tx-x)/10;
		y += (ty-y)/10;
		xoff=(int) -(x-(Main.INTERNAL_RESX/2));
		yoff=(int) -(y-(Main.INTERNAL_RESY/2));
	}

	public int getXOff() {
		return xoff;
	}
	
	public int getYOff() {
		return yoff;
	}
}