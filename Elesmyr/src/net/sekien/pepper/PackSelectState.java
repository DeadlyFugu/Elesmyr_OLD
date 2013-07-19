/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.pepper;

import net.sekien.elesmyr.util.FileHandler;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTTag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: matt Date: 22/04/13 Time: 10:25 AM To change this template use File | Settings |
 * File Templates.
 */
public class PackSelectState extends DynamicListNode {
public PackSelectState(String name) {
	super(name);
}

@Override
public List<Node> getList() {
	ArrayList<File> temp = new ArrayList<File>();
	File[] fileList = new File("pack").listFiles();
	for (int i = 0; i < fileList.length; i++) {
		File choose = fileList[i];
		if (choose.isDirectory() && !temp.contains(choose)) {
			temp.add(choose);
		}
	}
	ArrayList<Object[]> packs = new ArrayList<Object[]>();
	HBTCompound packHBT = new HBTCompound("emptyPacks");
	try {
		packHBT = (HBTCompound) FileHandler.readHBT("pack/packs", false).get(0);
	} catch (IOException e) {
		e.printStackTrace();
	}
	for (int i = 0; i < temp.size(); i++) {
		File file = temp.get(i);
		int pindex = 9001;
		if (packHBT.hasTag(file.getName())) { //Get index in HBT
			List<HBTTag> data = packHBT.getData();
			for (int i1 = 0, dataSize = data.size(); i1 < dataSize; i1++) {
				HBTTag tag = data.get(i1);
				if (tag.getName().equals(file.getName())) {pindex = i1;}
			}
		}
		packs.add(new Object[]{file, packHBT.getFlag(file.getName(), "FALSE").isTrue(), pindex});
	}
	ArrayList<Node> sorted = new ArrayList<Node>();
	while (packs.size() > 0) { //Really bad sorting algorithm
		int minid = 9002;
		Object[] maxob = null;
		for (Object[] object : packs) {
			if ((Integer) object[2] < minid) {
				minid = (Integer) object[2];
				maxob = object;
			}
		}
		File file = (File) maxob[0];
		boolean enabled = (Boolean) maxob[1];
		int index = (Integer) maxob[2];
		HBTCompound pdata = new HBTCompound("empty");
		try {
			List<HBTTag> pdatamaybe = FileHandler.readHBT("pack/"+file.getName()+"/pack", false);
			for (HBTTag tag : pdatamaybe) {
				if (tag.getName().equals("packinfo") && tag instanceof HBTCompound) {
					pdata = (HBTCompound) tag;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		sorted.add(new PackButtonNode("pack_"+file.getName(), file.getName(), pdata, enabled));
		packs.remove(maxob);
	}
	return sorted;
}
}
