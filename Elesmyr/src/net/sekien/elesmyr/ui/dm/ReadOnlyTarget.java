package net.sekien.elesmyr.ui.dm;

import net.sekien.elesmyr.system.GameClient;
import net.sekien.hbt.HBTCompound;

/**
 * Created with IntelliJ IDEA. User: matt Date: 19/04/13 Time: 1:01 PM To change this template use File | Settings |
 * File Templates.
 */
public class ReadOnlyTarget implements DevModeTarget {

private HBTCompound list = new HBTCompound("StoredListTarget");

public ReadOnlyTarget(HBTCompound list) {
	this.list = list;
}

@Override
public void set(HBTCompound list, String subTarget, GameClient client) {
	//Read-only, can't modify
}

@Override
public HBTCompound getList(GameClient client, String subTarget) {
	return list;
}
}
