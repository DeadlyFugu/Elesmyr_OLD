package net.halitesoft.lote.world.entity;

import java.util.Random;


import net.halitesoft.lote.player.Camera;
import net.halitesoft.lote.system.GameServer;
import net.halitesoft.lote.world.Region;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.minlog.Log;

public class EntitySpawner extends Entity {
	
	private Random rand;
	public EntitySpawner() {
		tellClient = false;
		rand = new Random();
	}

	@Override
	public void update(Region region, GameServer receiver) {
		int se = 0;
		for (Entity e : region.entities.values())
			if (e.getClass().getSimpleName().equals(extd.split(",",2)[0]))
				se++;
		if (rand.nextInt((int) Math.ceil(600*Math.log10((se+1)*2)))==1) {
				region.addEntityServer(extd.split(",",2)[0]+","+x+","+y+","+extd.split(",",2)[1]);
				//Log.info("Spawned chance = 1/"+600*Math.log10((se+1)*2));
		}
	}
}
