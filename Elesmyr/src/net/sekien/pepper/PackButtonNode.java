/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.pepper;

import net.sekien.hbt.HBTCompound;
import org.newdawn.slick.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 22/04/13 Time: 12:44 PM To change this template use File | Settings |
 * File Templates.
 */
public class PackButtonNode extends MultiChoiceButtonNode {
private final String pname;
private final HBTCompound pdata;
private final boolean pstate;

public PackButtonNode(String iname, String pname, HBTCompound pdata, boolean pstate) {
	super(iname, pname, new String[]{pstate?"Disable":"Enable", "Up", "Down", "Info"});
	this.pname = pname;
	this.pdata = pdata;
	this.pstate = pstate;
}

@Override
protected void onSelect(int sel) {
	System.out.println(sel);
}

@Override
public void render(Renderer renderer, int w, int h, boolean sel) {
	htarget = (sel?96:32);
	if (hcurrent < htarget) {
		hcurrent += 16;
	} else if (hcurrent > htarget) {
		hcurrent -= 16;
	}
	h = hcurrent;
	renderer.rect(0, 0, w, h, true, true, false, false, Renderer.BoxStyle.HFADE);
	String dispname = pdata.getString("name", pname);
	if (pdata.hasTag("author")) dispname += " by "+pdata.getString("author", "LOL ERROR");
	renderer.text(10, 11, dispname, new Color(1, 1, 1, sel?1f:0.25f));
	String statestr = (pstate?"Enabled":"Disabled");
	renderer.text(w-renderer.textWidth(statestr)-10, 11, statestr, (pstate?new Color(0, 1, 0, sel?1f:0.25f):new Color(1, 0, 0, sel?1f:0.25f)));
	if (sel) {
		renderer.text(10, 43, pdata.getString("desc", "No description provided."));
		renderer.rect(0, 64, w, 32, true, false, false, false, Renderer.BoxStyle.OUTLINE);
		renderer.sel(this.sel*(w/options.length), 64, w/options.length, 32);
		for (int i = 0; i < options.length; i++) {
			renderer.textCentered(((i+1)*(w/options.length))-(w/options.length/2), 74, options[i]);
		}
	}
}
}
