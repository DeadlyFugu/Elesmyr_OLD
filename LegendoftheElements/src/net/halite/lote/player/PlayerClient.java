package net.halite.lote.player;

import com.esotericsoftware.minlog.Log;
import net.halite.lote.GameElement;
import net.halite.lote.Save;
import net.halite.lote.msgsys.Message;
import net.halite.lote.msgsys.MessageReceiver;
import net.halite.lote.msgsys.MessageSystem;
import net.halite.lote.system.GameClient;
import net.halite.lote.system.GameServer;
import net.halite.lote.system.Input;
import net.halite.lote.world.Region;
import net.halite.lote.world.World;
import net.halite.lote.world.entity.EntityPlayer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class PlayerClient implements GameElement {
private World world;
private GameClient gs;
public int x, y;
private String regionName;
public Region region;
public int entid=-1;
private int caxis_x=0;
private int caxis_y=1;
private boolean controller;
private String pdat=null;
private boolean warpWalkControl=false;
private char da, db;
private int twx, twy;
private String twd;
private boolean pastHalf=false;

public PlayerClient(GameClient gs, World world) {
	this.gs=gs;
	this.world=world;
	//this.region = "start";
}

@Override
public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver)
		throws SlickException {
	controller=(gc.getInput().getControllerCount()>0);
	if (controller)
		for (int i=0; i<gc.getInput().getAxisCount(0); i++) {
			String aname=gc.getInput().getAxisName(0, i);
			if (aname.equals("x"))
				caxis_x=i;
			if (aname.equals("y"))
				caxis_y=i;
		}
}

@Override
public void load(Save save) {
}

@Override
public void render(GameContainer gc, StateBasedGame sbg, Graphics g, Camera cam, GameClient receiver)
		throws SlickException {
}

@Override
public void update(Region region, GameServer receiver) {
}

@Override
public void clientUpdate(GameContainer gc, StateBasedGame sbg, GameClient receiver) {
	//Log.info((region==null)+" "+((region==null)?"":region.name)+" "+regionName);
	if (region==null||!region.name.equals(regionName)) {
		region=this.getRegion();
	} else if (warpWalkControl) {
		int mvspd=2;
		if (!pastHalf) {
			if (da=='U')
				y-=mvspd;
			else if (da=='D')
				y+=mvspd;
			else if (da=='L')
				x-=mvspd;
			else if (da=='R')
				x+=mvspd;
			if (x<0||y<0||x>region.map.getWidth()*32||y>region.map.getHeight()*32||da=='C') {
				pastHalf=true;
				int smx=twx, smy=twy;
				if (db=='U')
					while (smy<region.map.getHeight()*32)
						smy+=mvspd;
				else if (db=='D')
					while (smy>0)
						smy-=mvspd;
				else if (db=='L')
					while (smx<region.map.getWidth()*32)
						smx+=mvspd;
				else if (db=='R')
					while (smx>0)
						smx-=mvspd;
				gs.cam.setPosition(this.x=smx, this.y=smy, this);
				gs.regionLoaded=false;
				this.region=null;
				this.entid=-1;
				gs.sendMessage("SERVER.changeRegion", twd+","+x+","+y);
			}
		} else {
			if (db=='U')
				y-=mvspd;
			else if (db=='D')
				y+=mvspd;
			else if (db=='L')
				x-=mvspd;
			else if (db=='R')
				x+=mvspd;
			if (x<twx+10&&y<twy+10&&x>twx-10&&y>twy-10) {
				pastHalf=false;
				warpWalkControl=false;
			}
		}
		posUpdate();
	} else {
		org.newdawn.slick.Input in=gc.getInput();
		int xp=x;
		int yp=y;
		int mvspd=2;
		int snkspd=1;
		int noMove=0;
		int xm=0;
		int ym=0;
		if (in.isKeyDown(org.newdawn.slick.Input.KEY_LEFT)) {
			x-=mvspd;
			xm=-1;
		} else if (in.isKeyDown(org.newdawn.slick.Input.KEY_RIGHT)) {
			x+=mvspd;
			xm=1;
		} else {
			noMove++;
			xm=0;
		}
		if (in.isKeyDown(org.newdawn.slick.Input.KEY_UP)) {
			y-=mvspd;
			ym=-1;
		} else if (in.isKeyDown(org.newdawn.slick.Input.KEY_DOWN)) {
			y+=mvspd;
			ym=1;
		} else {
			noMove++;
			ym=0;
		}
		if (noMove==2&&controller) {
			if (in.getAxisValue(0, caxis_x)<-0.3f)
				x-=mvspd;
			else if (in.getAxisValue(0, caxis_x)<-0.2f)
				x-=snkspd;
			if (in.getAxisValue(0, caxis_x)>0.3f)
				x+=mvspd;
			else if (in.getAxisValue(0, caxis_x)>0.2f)
				x+=snkspd;

			if (in.getAxisValue(0, caxis_y)<-0.3f)
				y-=mvspd;
			else if (in.getAxisValue(0, caxis_y)<-0.2f)
				y-=snkspd;
			if (in.getAxisValue(0, caxis_y)>0.3f)
				y+=mvspd;
			else if (in.getAxisValue(0, caxis_y)>0.2f)
				y+=snkspd;
		}

		if (Input.isKeyPressed(gc, "atk"))
			MessageSystem.sendServer(this, new Message(regionName+".hitAt", (x+xm*16)+","+(y+ym*16)), true);

		if (Input.isKeyPressed(gc, "int")) {
			int colInfront=region.map.getTileId((int) (x+xm*16)/32, (int) (y+ym*16)/32, region.mapColLayer);
			if (!intWith(colInfront))
				MessageSystem.sendServer(this, new Message(regionName+".intAt", (x+xm*16)+","+(y+ym*16)), true);
		}

		if (region!=null) {
			if (!in.isKeyDown(org.newdawn.slick.Input.KEY_SPACE)) {
				if (placeFree(x, yp))
					x=xp;
				if (placeFree(x, y))
					y=yp;
			}
			try {
				int colHere=region.map.getTileId((int) (x)/32, (int) (y)/32, region.mapColLayer);
				if (colHere==region.mapColTOff+12)
					useWarp(region.map.getMapProperty("warp1", "error,12,12,C"));
				else if (colHere==region.mapColTOff+13)
					useWarp(region.map.getMapProperty("warp2", "error,12,12,C"));
			} catch (ArrayIndexOutOfBoundsException e) {
			}
		}
		posUpdate();
	}
}

private void posUpdate() {
	//Send player position to server
	if (entid!=-1) {
		MessageSystem.sendServer(this, new Message(regionName+"."+entid+".move", x+","+y), true);
		MessageSystem.sendServer(this, new Message(regionName+".pickupAt", x+","+y), true);
	}
	world.receiveMessage(new Message(regionName+"."+entid+".moveClient", x+","+y), gs);
	if (pdat!=null&&((EntityPlayer) region.entities.get(entid))!=null) {
		((EntityPlayer) region.entities.get(entid)).setPDat(pdat);
		pdat=null;
	}
}

private boolean intWith(int colInfront) {
	if (colInfront==region.mapColTOff+4) {
		useWarp(region.map.getMapProperty("door1", "error,12,12")+",C");
	} else if (colInfront==region.mapColTOff+5) {
		useWarp(region.map.getMapProperty("door2", "error,12,12")+",C");
	} else if (colInfront==region.mapColTOff+6) {
		useWarp(region.map.getMapProperty("door3", "error,12,12")+",C");
	} else if (colInfront==region.mapColTOff+7) {
		useWarp(region.map.getMapProperty("door4", "error,12,12")+",C");
	} else if (colInfront==region.mapColTOff+8) {
		useWarp(region.map.getMapProperty("door5", "error,12,12")+",C");
	} else if (colInfront==region.mapColTOff+9) {
		useWarp(region.map.getMapProperty("door6", "error,12,12")+",C");
	} else if (colInfront==region.mapColTOff+10) {
		useWarp(region.map.getMapProperty("door7", "error,12,12")+",C");
	} else if (colInfront==region.mapColTOff+11) {
		useWarp(region.map.getMapProperty("door8", "error,12,12")+",C");
	}
	return false;
}

private void useWarp(String dest) {
	//gs.sendMessage("SERVER.changeRegion",dest.split(",")[0]);
	//gs.cam.x=this.x=(int) (Float.parseFloat(dest.split(",")[1])*32);
	//gs.cam.y=this.y=(int) (Float.parseFloat(dest.split(",")[2])*32);
	String dir=dest.split(",")[3];
	da=dir.charAt(0);
	db=da;
	try {db=dir.charAt(1);} catch (Exception e) {} ;
	twx=(int) (Float.parseFloat(dest.split(",")[1])*32);
	twy=(int) (Float.parseFloat(dest.split(",")[2])*32);
	twd=dest.split(",")[0];
	warpWalkControl=true;

	//gs.regionLoaded=false;
}

private boolean placeFree(float x, float y) {
	try {
		return (isSolid(region.map.getTileId((int) (x-8)/32, (int) (y+3)/32, region.mapColLayer)-region.mapColTOff)||
				        isSolid(region.map.getTileId((int) (x+8)/32, (int) (y+3)/32, region.mapColLayer)-region.mapColTOff)||
				        isSolid(region.map.getTileId((int) (x-8)/32, (int) (y-3)/32, region.mapColLayer)-region.mapColTOff)||
				        isSolid(region.map.getTileId((int) (x+8)/32, (int) (y-3)/32, region.mapColLayer)-region.mapColTOff));
	} catch (ArrayIndexOutOfBoundsException e) {
		return true;
	}
}

private boolean isSolid(int id) {
	return (id==0||(id>=4&&id<12));
}

@Override
public void receiveMessage(Message msg, MessageReceiver receiver) {
	String name=msg.getName();
	if (name.equals("playerInfo")) {
		String[] parts=msg.getData().split(",");
		regionName=parts[0];
		gs.cam.x=x=Integer.parseInt(parts[1]);
		gs.cam.y=y=Integer.parseInt(parts[2]);
		msg.reply("SERVER.getRegion", parts[0]+","+parts[1]+","+parts[2], this);
	} else if (name.equals("setID")) {
		this.entid=Integer.parseInt(msg.getData());
	} else if (name.equals("setPDAT")) {
		if (entid!=-1&&region!=null&&region.entities.get(entid)!=null&&(region.entities.get(entid) instanceof EntityPlayer))
			((EntityPlayer) region.entities.get(entid)).setPDat(msg.getData());
		pdat=msg.getData();
	} else {
		Log.warn("PlayerClient Ignored message - unrecognised name: "+msg.toString());
	}
}

@Override
public void save(Save save) {
}

public String getRegionName() {
	return regionName;
}

public void setRegion(String rname) {
	this.regionName=rname;
}

public Region getRegion() {
	if (regionName!=null)
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
}
