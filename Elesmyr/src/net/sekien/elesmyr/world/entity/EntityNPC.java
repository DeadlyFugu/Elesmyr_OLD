package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.system.FontRenderer;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.hbt.HBTFlag;
import org.newdawn.slick.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 10/09/13 Time: 4:00 PM To change this template use File | Settings |
 * File Templates.
 */
public class EntityNPC extends EntityWalkAI {
private NPCData npc;
private WalkingAnimation anim;

public void onInitClient() throws SlickException {
	sprite = FileHandler.getImage("ent.worktable");
	anim = new WalkingAnimation("player.player_reg");
}

public void onInitServer() {
	super.onInitServer();
	npc = new NPCData(hbt);
	setCollision(-16, -16, 32, 32);
}

public void onInteract() {
	super.onInteract();
	hbt.setString("msg", npc.getGreet(hbt.getInt("fl", (byte) 4)));
	hbt.setInt("timer", 200);
}

public void onUpdateServer() {
	super.onUpdateServer();
	int timer = hbt.getInt("timer", 0);
	if (timer > 0) {
		hbt.setInt("timer", --timer);
	}
	hbt.setByte("_dir", (byte) this.getDirection());
	moveFree = hbt.getFlag("move", HBTFlag.TRUE).isTrue();
}

public void onUpdateClient() {
	super.onUpdateClient();
	if (anim!=null) {
		anim.setPos(x, y, xs, ys);
		anim.setDirection(hbt.getByte("_dir", (byte) 0));
	}
}

public void onHit() {
	server.removeEntity(this);
}

public void onRender() {
	/*g.pushTransform();
	g.translate(-16,-16);
	super.onRender();
	g.popTransform();*/
	anim.render(cam);
	if (hbt.getInt("timer", 0) > 0) {
		String msg = hbt.getString("msg", "ERROR: timer!=0 but no msg");
		msg = chatSplit(msg);

		String[] split = msg.split("\n");
		for (int i = 0, splitLength = split.length; i < splitLength; i++) {
			String s = split[i];
			FontRenderer.drawString(xs-FontRenderer.getWidth(s)/2, ys-48-(splitLength-i)*12, s, g);
		}
	}
}

private String chatSplit(String msg) {
	StringBuilder sb = new StringBuilder();
	int len = 0;
	for (String s : msg.split(" ")) {
		len += FontRenderer.getWidth(s);
		if (len < 200) {
			sb.append(s);
			sb.append(' ');
		} else {
			sb.append("\n");
			sb.append(s);
			sb.append(' ');
			len = 0;
		}
	}
	return sb.toString();
}
}
