/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.pepper;

import net.sekien.elesmyr.system.Globals;
import net.sekien.elesmyr.system.Main;
import net.sekien.hbt.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: matt Date: 21/04/13 Time: 9:30 AM To change this template use File | Settings |
 * File Templates.
 */
public class NewSaveNode extends ListNode {
private static enum Races {
	HUMAN,
	DWARF,
	ELF,
	JAALKIN,
	BAALHAAD,
	GHAST
}

private static enum Spawns {
	START("start, Debugheim", "start", 800, 532),
	CLEARING("clearing_thing, Debugheim", "clearing_thing", 400, 400),
	RINAN("Rinan, Ausaheim", "au_rnb00", 0, 0),
	HEAGRUND("Heagrund, Lopheim", "lp_hea00", 0, 0),
	HALIGRUND("Haligrund, Baalheim", "bl_hal00", 0, 0),
	RASKENBURG("Raskenburg, Lundheim", "lu_rsk00", 0, 0),
	JERLIC("Jerlic, Lundheim", "lu_jrl00", 0, 0);
	private String desc;
	private final String region;
	private final int x;
	private final int y;

	private Spawns(String desc, String region, int x, int y) {
		this.desc = desc;
		this.region = region;
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return desc;
	}

	public String getRegion() {
		return region;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}

public NewSaveNode(String name) {
	super(name);
	addChild(new BasicTextNode("name", "Save name"));
	addChild(new BasicEnumNode("race", "Player race", false, Races.class));
	addChild(new BasicEnumNode("spawn", "Start point", false, Spawns.class));
	addChild(new BasicChoiceNode("diffc", "Difficulty", new String[]{"Easy", "Medium", "Hard"}));
	addChild(new AbstractButtonNode("make", "Create") {
		@Override
		public void onAction(Action action) {
			if (action==Action.SELECT) {
				createWorld();
			}
		}
	});
}

public void createWorld() {
	System.out.println("Create world:"+
			                   "\n    "+((BasicTextNode) getChild("name")).getValue()+
			                   "\n    "+((BasicEnumNode) getChild("race")).getValue()+
			                   "\n    "+((BasicEnumNode) getChild("spawn")).getValue()+
			                   "\n    "+((BasicChoiceNode) getChild("diffc")).getValue());
	HBTCompound save = new HBTCompound("save_root");
	//Add tags
	HBTCompound meta = new HBTCompound("meta");
	HBTCompound init = new HBTCompound("init");
	init.addTag(new HBTLong("savedate", new Date().getTime()));
	init.addTag(new HBTString("gver", Main.verRelease+" "+Main.verNum));
	init.addTag(new HBTInt("sver", 20));
	meta.addTag(init);
	save.addTag(meta);

	HBTCompound player = new HBTCompound("players");
	HBTCompound user = new HBTCompound(Globals.get("name", "Player"));
	user.addTag(new HBTString("region", ((Spawns) ((BasicEnumNode) getChild("spawn")).getValue()).getRegion()));
	user.addTag(new HBTInt("x", ((Spawns) ((BasicEnumNode) getChild("spawn")).getValue()).getX()));
	user.addTag(new HBTInt("y", ((Spawns) ((BasicEnumNode) getChild("spawn")).getValue()).getY()));
	player.addTag(user);
	save.addTag(player);

	try {
		HBTOutputStream os = new HBTOutputStream(new FileOutputStream("save/"+((BasicTextNode) getChild("name")).getValue()+".hbt"), false);
		for (HBTTag tag : save)
			os.write(tag);
		os.close();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}

	StateManager.back();
}
}
