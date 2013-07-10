package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.world.Region;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTInt;
import net.sekien.hbt.HBTString;
import net.sekien.hbt.HBTTag;

import java.util.Random;

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
		if (e.getClass().getSimpleName().equals(inst_dat.getString("extd", "").split(",", 2)[0])) //todo n extd
			se++;
	if (rand.nextInt((int) Math.floor(Math.pow(se, 3)/2)+1)==0) {
		region.addEntityServer(new HBTCompound("spawn_dat", new HBTTag[]{
				                                                                new HBTString("class", inst_dat.getString("extd", "").split(",", 2)[0]),
				                                                                new HBTInt("x", x),
				                                                                new HBTInt("y", y),
				                                                                new HBTString("extd", inst_dat.getString("extd", "").split(",", 2)[1])
		}));
	}
}
}
