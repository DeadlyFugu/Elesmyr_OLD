/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.pepper;

import net.sekien.elesmyr.msgsys.DetectHosts;
import net.sekien.elesmyr.msgsys.PotentialHost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: matt Date: 27/04/13 Time: 2:47 PM To change this template use File | Settings |
 * File Templates.
 */
public class JoinState extends DynamicListNode {
private volatile List<PotentialHost> hostCache = new ArrayList<PotentialHost>();
private int cacheAge = -1;

public JoinState(String name) {
	super(name);
}

@Override
public List<Node> getList() {
	if (cacheAge==-1 || cacheAge > 10) {
		cacheAge = 0;
		updateCache();
	}
	cacheAge++;
	ArrayList<Node> nodes = new ArrayList<Node>();
	nodes.add(new TextNode("ip", "Enter IP") {
		@Override
		protected void onSelect() {
			StateManager.updFunc("JOIN "+this.text);
		}
	});
	for (PotentialHost host : hostCache) {
		nodes.add(new CommandButtonNode("j"+host.hashCode(), "Join "+host.name+" ("+host.time+"ms)", "JOIN "+host.address.getHostAddress()));
	}
	return nodes;
}

@Override
protected int updateInterval() {
	return 10;
}

private void updateCache() {
	new Thread() {
		public void run() {
			this.setName("Local host detection");
			ArrayList<PotentialHost> newCache = new ArrayList<PotentialHost>();
			DetectHosts.getHosts(37021, 500, newCache);
			for (PotentialHost address : newCache) {
				if (!hostCache.contains(address)) {
					hostCache.add(address);
				} else {
					hostCache.get(hostCache.indexOf(address)).time = address.time;
				}
			}
			for (PotentialHost address : hostCache) {
				if (!newCache.contains(address)) {
					address.time = -1; //Mark as dead
				}
			}
		}
	}.start();
}
}
