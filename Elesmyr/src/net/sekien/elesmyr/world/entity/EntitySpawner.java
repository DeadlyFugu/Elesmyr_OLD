package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.world.Region;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTInt;

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
		if (e.getClass().getSimpleName().equals(inst_dat.getString("spawn.class", "")))
			se++;
	if (rand.nextInt((int) Math.floor(Math.pow(se, 3)/2)+1)==0) {
		HBTCompound ent = (HBTCompound) inst_dat.getCompound("spawn").deepClone();
		ent.setTag(new HBTInt("x", x));
		ent.setTag(new HBTInt("y", y));
		region.addEntityServer(ent);
	}
}
}
