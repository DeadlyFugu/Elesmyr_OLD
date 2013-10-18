/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.system;

import net.sekien.elesmyr.msgsys.*;
import net.sekien.elesmyr.world.Region;
import net.sekien.hbt.*;

import java.util.HashMap;

public class ServerAudio implements MessageReceiver {
	private static HashMap<Integer, Connection> queue;
	private static ServerAudio instance;

	private ServerAudio() {
	}

	public static void init() {
		queue = new HashMap<Integer, Connection>();
		instance = new ServerAudio();
		MessageSystem.registerReceiverServer(instance);
	}

	public static void update(GameServer server) {

	}

	public static void sendMusicUpdate(Connection target, Region region) {
		int hash = target.hashCode();
		if (region.connections.size() > 1) {
			queue.put(hash, target);
			Connection clientToAsk = null;
			for (Connection c : region.connections) {
				if (c != target) {
					clientToAsk = c;
					break;
				}
			}
			if (clientToAsk != null)
				MessageSystem.sendClient(instance, clientToAsk, new Message("_audioman.getm", new HBTCompound("p", new HBTTag[]{new HBTInt("qhash", hash)})), false);
			else
				System.err.println("ServerAudio: no valid client to supply audio info found");
		} else {
			MessageSystem.sendClient(instance, target, new Message("_audioman.setm", new HBTCompound("p", new HBTTag[]{new HBTString("file", genSong(region))})), false);
		}
	}

	private static String genSong(Region region) {
		switch ((int) (Math.random()*3.99f)) {
			case 0: return "aubrm_day_00";
			case 1: return "aubrm_night_00";
			case 2: return "aubrm_night_01";
			case 3: return "aubrm_town_00";
			default: return "aubrm_day_00";
		}
	}

	@Override public void receiveMessage(Message msg, MessageEndPoint receiver) {
		System.out.println("msg = "+msg);
		if (msg.getName().equals("getm_r")) {
			HBTCompound data = msg.getData();
			if (queue.containsKey(data.getInt("qhash", 0))) {
				Connection sendmsg = queue.get(data.getInt("qhash", 0));
				MessageSystem.sendClient(this, sendmsg, new Message("_audioman.setm", data), false);
			}
		} else if (msg.getName().equals("sngend")) {
			Region region = ((GameServer) receiver).getPlayerRegion(msg.getConnection());
			MessageSystem.sendClient(this, region.connections, new Message("_audioman.setm", new HBTCompound("p", new HBTTag[]{new HBTString("file", genSong(region))})), false);
		} else {
			System.err.println("ServerAudio: unrecognised message "+msg);
		}
	}

	@Override public String getReceiverName() {
		return "_serveraudio";
	}

	@Override public void fromHBT(HBTCompound tag) {
	}

	@Override public HBTCompound toHBT(boolean msg) {
		return HBTTools.msgString("class", "ServerAudio");
	}
}
