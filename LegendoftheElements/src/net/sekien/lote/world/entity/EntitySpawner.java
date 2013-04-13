package net.sekien.lote.world.entity;

import net.sekien.lote.system.GameServer;
import net.sekien.lote.world.Region;

import java.util.Random;

public class EntitySpawner extends Entity {

private Random rand;

public EntitySpawner() {
	tellClient=false;
	rand=new Random();
}

@Override
public void update(Region region, GameServer receiver) {
	int se=0;
	for (Entity e : region.entities.values())
		if (e.getClass().getSimpleName().equals(extd.split(",", 2)[0]))
			se++;
	if (rand.nextInt((int) Math.floor(Math.pow(se, 3)/2)+1)==0) {
		region.addEntityServer(extd.split(",", 2)[0]+","+x+","+y+","+extd.split(",", 2)[1]);
	}
}
}
