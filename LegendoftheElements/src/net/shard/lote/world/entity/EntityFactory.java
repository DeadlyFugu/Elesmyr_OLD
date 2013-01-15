package net.shard.lote.world.entity;

public class EntityFactory {
	public static Entity getEntity(String str, String rname) {
		//Note: str /should/ be formatted as 
		String[] parts = str.split(",",5);
		if (parts.length == 5)
			try {
				Entity e = (Entity) Class.forName("net.shard.lote.world.entity."+parts[0]).newInstance();
				e.ctor(parts[1],Integer.parseInt(parts[2]),Integer.parseInt(parts[3]),parts[4],rname+"."+parts[1]);
				return e;
			} catch (Exception e) {
				e.printStackTrace();
			}
			//return new EntityEnemy(parts[1],Integer.parseInt(parts[2]),Integer.parseInt(parts[3]));
		return new Entity().ctor("null",0,0,"null",rname+".null");
	}
}
