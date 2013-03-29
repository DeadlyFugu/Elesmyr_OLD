package net.halite.lote.world.item

import net.halite.lote.player.PlayerData
import net.halite.lote.system.GameServer
import net.halite.lote.world.entity.EntityPlayer

/**
 * This class is an example of how to write a mod
 */
public class MagicWand extends ItemWeapon {
public boolean onUse(GameServer reciever, EntityPlayer player, PlayerData.InventoryEntry entry) {
	player.region.addEntityServer("EnemyMushroom,"+player.x+","+player.y+",10")
	return false;
}
}
