/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.player;

import com.esotericsoftware.minlog.Log;
import net.sekien.elesmyr.GameElement;
import net.sekien.elesmyr.Save;
import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.msgsys.MessageSystem;
import net.sekien.elesmyr.system.*;
import net.sekien.elesmyr.util.PointSensor;
import net.sekien.elesmyr.world.Region;
import net.sekien.elesmyr.world.World;
import net.sekien.elesmyr.world.entity.Entity;
import net.sekien.elesmyr.world.entity.EntityPlayer;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTInt;
import net.sekien.hbt.HBTString;
import net.sekien.hbt.HBTTools;
import net.sekien.pepper.StateManager;
import org.newdawn.slick.*;

public class PlayerClient implements GameElement {
	public static final int INTERACT_DISTANCE = 24;
	public static boolean BIGSIZE = true;
	private World world;
	private GameClient gs;
	public int x, y;
	private String regionName;
	public Region region;
	public int entid = -1;
	private int caxis_x = 0;
	private int caxis_y = 1;
	private boolean controller;
	private HBTCompound pdat = null;
	private boolean warpWalkControl = false;
	private char da, db;
	private int twx, twy;
	private String twd;
	private boolean pastHalf = false;

	public PlayerClient(GameClient gs, World world) {
		this.gs = gs;
		this.world = world;
		PlayerClient.BIGSIZE = Boolean.parseBoolean(Globals.get("big", "false"));
		//this.region = "start";
	}

	@Override
	public void init(GameContainer gc, MessageEndPoint receiver)
	throws SlickException {
		controller = (gc.getInput().getControllerCount() > 0);
		if (controller)
			for (int i = 0; i < gc.getInput().getAxisCount(0); i++) {
				String aname = gc.getInput().getAxisName(0, i);
				if (aname.equals("x"))
					caxis_x = i;
				if (aname.equals("y"))
					caxis_y = i;
			}
	}

	@Override
	public void load(Save save) {
	}

	@Override
	public void render(GameContainer gc, Graphics g, Camera cam, GameClient receiver)
	throws SlickException {
	}

	@Override
	public void update(Region region, GameServer receiver) {
	}

	@Override
	public void clientUpdate(GameContainer gc, GameClient receiver) {
		//Log.info((region==null)+" "+((region==null)?"":region.name)+" "+regionName);
		if (region == null || !region.name.equals(regionName)) {
			region = this.getRegion();
		} else if (warpWalkControl) {
			int mvspd = BIGSIZE?4:2;
			if (!pastHalf) {
				if (da == 'U')
					y -= mvspd;
				else if (da == 'D')
					y += mvspd;
				else if (da == 'L')
					x -= mvspd;
				else if (da == 'R')
					x += mvspd;
				if (x < 0 || y < 0 || x > region.map.getWidth()*32 || y > region.map.getHeight()*32 || da == 'C') {
					pastHalf = true;
					int smx = twx, smy = twy;
					if (db == 'U')
						while (smy < region.map.getHeight()*32)
							smy += mvspd;
					else if (db == 'D')
						while (smy > 0)
							smy -= mvspd;
					else if (db == 'L')
						while (smx < region.map.getWidth()*32)
							smx += mvspd;
					else if (db == 'R')
						while (smx > 0)
							smx -= mvspd;
					gs.cam.setPosition(this.x = smx, this.y = smy, this);
					gs.regionLoaded = false;
					StateManager.startLoading();
					this.region = null;
					this.entid = -1;
					HBTCompound pTag = new HBTCompound("p");
					pTag.addTag(new HBTString("region", twd)); //TODO: Proper player to hbt
					pTag.addTag(new HBTInt("x", x));
					pTag.addTag(new HBTInt("y", y));
					MessageSystem.sendServer(null, new Message("SERVER.changeRegion", pTag), false);
				}
			} else {
				if (db == 'U')
					y -= mvspd;
				else if (db == 'D')
					y += mvspd;
				else if (db == 'L')
					x -= mvspd;
				else if (db == 'R')
					x += mvspd;
				if (x < twx+10 && y < twy+10 && x > twx-10 && y > twy-10) {
					pastHalf = false;
					warpWalkControl = false;
				}
			}
			posUpdate();
		} else {
			org.newdawn.slick.Input in = gc.getInput();
			int xp = x;
			int yp = y;
			int mvspd = 2;
			int snkspd = 1;
			int noMove = 0;
			int xm = 0;
			int ym = 0;
			if (in.isKeyDown(org.newdawn.slick.Input.KEY_LEFT)) {
				x -= mvspd;
				xm = -1;
			} else if (in.isKeyDown(org.newdawn.slick.Input.KEY_RIGHT)) {
				x += mvspd;
				xm = 1;
			} else {
				noMove++;
				xm = 0;
			}
			if (in.isKeyDown(org.newdawn.slick.Input.KEY_UP)) {
				y -= mvspd;
				ym = -1;
			} else if (in.isKeyDown(org.newdawn.slick.Input.KEY_DOWN)) {
				y += mvspd;
				ym = 1;
			} else {
				noMove++;
				ym = 0;
			}
			if (noMove == 2 && controller) {
				if (in.getAxisValue(0, caxis_x) < -0.3f)
					x -= mvspd;
				else if (in.getAxisValue(0, caxis_x) < -0.2f)
					x -= snkspd;
				if (in.getAxisValue(0, caxis_x) > 0.3f)
					x += mvspd;
				else if (in.getAxisValue(0, caxis_x) > 0.2f)
					x += snkspd;

				if (in.getAxisValue(0, caxis_y) < -0.3f)
					y -= mvspd;
				else if (in.getAxisValue(0, caxis_y) < -0.2f)
					y -= snkspd;
				if (in.getAxisValue(0, caxis_y) > 0.3f)
					y += mvspd;
				else if (in.getAxisValue(0, caxis_y) > 0.2f)
					y += snkspd;
			}

			if (GameInput.isKeyPressed(gc, "atk")) {
				AudioMan.playSound("atk_sword"+(int) (Math.random()*3.999));
				MessageSystem.sendServer(this, new Message(regionName+".hitAt", HBTTools.position(x+xm*16, y+ym*16)), true);
			}

			if (GameInput.isKeyPressed(gc, "int")) {
				int colInfront = region.map.getTileId((int) (x+xm*16)/32, (int) (y+ym*16)/32, region.mapColLayer);
				if (!intWith(colInfront))
					MessageSystem.sendServer(this, new Message(regionName+".intAt", HBTTools.position(x+xm*16, y+ym*16)), true);
			}

			int chkdist = 8;

			if (region != null) {
				if (!in.isKeyDown(org.newdawn.slick.Input.KEY_SPACE)) {
					if (!placeFree(x, yp)) {
						if (placeFree(x, yp+chkdist)) {
							y += mvspd;
						} else if (placeFree(x, yp-chkdist)) {
							y -= mvspd;
						} else {
							x = xp;
						}
					}
					if (!placeFree(x, y)) {
						if (placeFree(x+chkdist, y)) {
							x += mvspd;
						} else if (placeFree(x-chkdist, y)) {
							x -= mvspd;
						} else {
							y = yp;
						}
					}
				}
				try {
					int colHere = region.map.getTileId((int) (x)/32, (int) (y)/32, region.mapColLayer);
					if (colHere == region.mapColTOff+12)
						useWarp(region.map.getMapProperty("warp1", "error,12,12,C"));
					else if (colHere == region.mapColTOff+13)
						useWarp(region.map.getMapProperty("warp2", "error,12,12,C"));
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
			posUpdate();
		}
	}

	private void posUpdate() {
		//Send player position to server
		if (entid != -1) {
			MessageSystem.sendServer(this, new Message(regionName+"."+entid+".move", HBTTools.position(x, y)), true);
			MessageSystem.sendServer(this, new Message(regionName+".pickupAt", HBTTools.position(x, y)), true);
		}
		world.receiveMessage(new Message(regionName+"."+entid+".moveClient", HBTTools.position(x, y)), gs);
		if (pdat != null && ((EntityPlayer) region.entities.get(entid)) != null) {
			((EntityPlayer) region.entities.get(entid)).setPDat(pdat);
			pdat = null;
		}
	}

	private boolean intWith(int colInfront) {
		if (colInfront == region.mapColTOff+4) {
			useWarp(region.map.getMapProperty("door1", "error,12,12")+",C");
		} else if (colInfront == region.mapColTOff+5) {
			useWarp(region.map.getMapProperty("door2", "error,12,12")+",C");
		} else if (colInfront == region.mapColTOff+6) {
			useWarp(region.map.getMapProperty("door3", "error,12,12")+",C");
		} else if (colInfront == region.mapColTOff+7) {
			useWarp(region.map.getMapProperty("door4", "error,12,12")+",C");
		} else if (colInfront == region.mapColTOff+8) {
			useWarp(region.map.getMapProperty("door5", "error,12,12")+",C");
		} else if (colInfront == region.mapColTOff+9) {
			useWarp(region.map.getMapProperty("door6", "error,12,12")+",C");
		} else if (colInfront == region.mapColTOff+10) {
			useWarp(region.map.getMapProperty("door7", "error,12,12")+",C");
		} else if (colInfront == region.mapColTOff+11) {
			useWarp(region.map.getMapProperty("door8", "error,12,12")+",C");
		}
		return false;
	}

	private void useWarp(String dest) {
		//gs.sendMessage("SERVER.changeRegion",dest.split(",")[0]);
		//gs.cam.x=this.x=(int) (Float.parseFloat(dest.split(",")[1])*32);
		//gs.cam.y=this.y=(int) (Float.parseFloat(dest.split(",")[2])*32);
		String dir = dest.split(",")[3];
		da = dir.charAt(0);
		db = da;
		try {db = dir.charAt(1);} catch (Exception e) {}
		;
		twx = (int) (Float.parseFloat(dest.split(",")[1])*32);
		twy = (int) (Float.parseFloat(dest.split(",")[2])*32);
		twd = dest.split(",")[0];
		warpWalkControl = true;

		//gs.regionLoaded=false;
	}

	private boolean placeFree(float x, float y) {
	/*try {
		return (isSolid(region.map.getTileId((int) (x-8)/32, (int) (y+3)/32, region.mapColLayer)-region.mapColTOff) ||
				        isSolid(region.map.getTileId((int) (x+8)/32, (int) (y+3)/32, region.mapColLayer)-region.mapColTOff) ||
				        isSolid(region.map.getTileId((int) (x-8)/32, (int) (y-3)/32, region.mapColLayer)-region.mapColTOff) ||
				        isSolid(region.map.getTileId((int) (x+8)/32, (int) (y-3)/32, region.mapColLayer)-region.mapColTOff));
	} catch (ArrayIndexOutOfBoundsException e) {
		return true;
	}*/
		return !(PointSensor.update(region.map, (int) x-8, (int) y+3) ||
		         PointSensor.update(region.map, (int) x+8, (int) y+3) ||
		         PointSensor.update(region.map, (int) x-8, (int) y-3) ||
		         PointSensor.update(region.map, (int) x+8, (int) y-3));
	}

/*private boolean isSolid(int id) {
	return (id==0 || (id >= 4 && id < 12));
}*/

	@Override
	public void receiveMessage(Message msg, MessageEndPoint receiver) {
		String name = msg.getName();
		if (name.equals("playerInfo")) {
			regionName = msg.getData().getString("region", "error");
			gs.cam.x = x = msg.getData().getInt("x", 0);
			gs.cam.y = y = msg.getData().getInt("y", 0);
			//msg.reply("SERVER.getRegion", msg.getData(), this);
			MessageSystem.sendServer(this, new Message("SERVER.getRegion", msg.getData()), false);
		} else if (name.equals("setID")) {
			this.entid = msg.getData().getInt("id", -1);
		} else if (name.equals("setPDAT")) {
			if (entid != -1 && region != null && region.entities.get(entid) != null && (region.entities.get(entid) instanceof EntityPlayer))
				((EntityPlayer) region.entities.get(entid)).setPDat(msg.getData());
			pdat = msg.getData();
		} else {
			Log.warn("PlayerClient Ignored message - unrecognised name: "+msg.toString());
		}
	}

	@Override
	public void save(Save save) {
	}

	@Override
	public void fromHBT(HBTCompound tag) {
	}

	@Override
	public HBTCompound toHBT(boolean msg) {
		return null;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegion(String rname) {
		this.regionName = rname;
	}

	public Region getRegion() {
		if (regionName != null)
			return world.regions.get(regionName);
		return null;
	}

	public String getID() {
		return String.valueOf(entid);
	}

	@Override
	public String getReceiverName() {
		return "PLAYER";
	}

	public int distanceFrom(Entity entity, int xoff, int yoff) {
		return (int) Math.hypot(x-(entity.x+xoff), y-(entity.y+yoff));
	}
}
