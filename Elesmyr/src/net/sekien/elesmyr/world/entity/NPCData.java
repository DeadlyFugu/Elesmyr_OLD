/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.util.EntityHBT;
import net.sekien.elesmyr.util.FileHandler;
import org.newdawn.slick.util.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: matt Date: 11/09/13 Time: 5:57 PM To change this template use File | Settings |
 * File Templates.
 */
public class NPCData {
private HashMap<String, List<String>> msgs;
private HashMap<String, String> props;

public NPCData(EntityHBT hbt) {
	msgs = new HashMap<String, List<String>>();
	props = new HashMap<String, String>();
	List<String> npc = FileHandler.parseFileName("npc."+hbt.getString("name", "error"), new String[]{"npc"}, true);
	if (npc.size()==0) {
		Log.error("NPC NOT FOUND - npc."+hbt.getString("name", "error"));
		npc = FileHandler.parseFileName("npc.error", new String[]{"npc"}, true);
	} else {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(npc.get(0))));
			String line;
			String listaddin = null;
			List<String> list = new ArrayList<String>();
			while ((line = in.readLine())!=null) {
				line = line.trim();
				if (line.contains("//")) line = line.split("//")[0];
				if (listaddin!=null) {
					if (line.equals("}")) {
						msgs.put(listaddin, list);
						list = new ArrayList<String>();
						listaddin = null;
					} else {
						list.add(line);
					}
				} else {
					if (line.endsWith("{")) {
						listaddin = line.substring(0, line.length()-1);
					} else if (line.contains(":")) {
						String[] parts = line.split(":");
						props.put(parts[0], parts[1]);
					} else if (line.length()!=0) {
						Log.error("NPC Parse error - invalid line '"+line+"' in npc."+hbt.getString("name", "error"));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}
}

public String getGreet(int flvl) {
	List<String> options = msgs.get("hello");
	List<String> refined = new ArrayList<String>();
	int mid = Math.max(2, Math.min(9, flvl));
	for (String str : options) {
		int val = Integer.parseInt(str.split("\\s", 2)[0]);
		if (val <= mid+1 && val >= mid-1) refined.add(str.split("\\s", 2)[1]);
	}
	if (refined.size()==0) return "Error: field hello empty in npc";
	return refined.get((int) (Math.random()*refined.size()));
}
}
