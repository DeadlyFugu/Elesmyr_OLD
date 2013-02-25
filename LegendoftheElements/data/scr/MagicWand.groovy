package net.halitesoft.lote.world.item

import net.halitesoft.lote.system.GameServer
import net.halitesoft.lote.world.entity.EntityPlayer

public class MagicWand extends ItemWeapon {
public boolean onUse(GameServer reciever, EntityPlayer player) {
	player.region.addEntityServer("EnemyMushroom,"+player.x+","+player.y+",")
	return false;
}
}
