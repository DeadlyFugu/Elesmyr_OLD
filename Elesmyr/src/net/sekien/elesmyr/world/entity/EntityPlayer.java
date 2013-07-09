package net.sekien.elesmyr.world.entity;

import com.esotericsoftware.minlog.Log;
import net.sekien.elesmyr.Element;
import net.sekien.elesmyr.lighting.Light;
import net.sekien.elesmyr.msgsys.Connection;
import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.msgsys.MessageSystem;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.player.PlayerClient;
import net.sekien.elesmyr.player.PlayerData;
import net.sekien.elesmyr.system.FontRenderer;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.elesmyr.system.GameServer;
import net.sekien.elesmyr.system.Globals;
import net.sekien.elesmyr.ui.CraftUI;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.elesmyr.world.Region;
import net.sekien.elesmyr.world.item.Item;
import net.sekien.elesmyr.world.item.ItemFactory;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTTools;
import org.newdawn.slick.*;

public class EntityPlayer extends Entity {

SpriteSheet spr;
public int cx, cy;
private int px, py;
private float spx, spy;
private int animState, dir; //animState: 0=still,1=moving,2=attacking; dir: 0=left, 1=up, 2=down
private float animFrame;
private boolean flip;
private Light torchLight;
public boolean isUser = false;
private Connection connection;
public PlayerData pdat;

public EntityPlayer() {
	constantUpdate = true;
	cx1 = cy1 = -16;
	cx2 = cy2 = 16;
}

public void setSERVDAT(Connection connection, PlayerData pdat) {
	this.connection = connection;
	this.pdat = pdat;
}

@Override
public void init(GameContainer gc, MessageEndPoint receiver)
		throws SlickException {
	spr = new SpriteSheet(FileHandler.getImage("player.player_reg"), 32, 48);
	torchLight = new Light(600, 550, 256, 0.8f, 0.5f, 0.2f, 0.4f); //TORCH LIGHT
	//((GameClient) sbg.getState(Main.GAMEPLAYSTATE)).lm.addLight(torchLight);
	constantUpdate = true;
}

@Override
public void render(GameContainer gc, Graphics g,
                   Camera cam, GameClient receiver) throws SlickException {
	//if (!((GameplayState) sbg.getState(Main.GAMEPLAYSTATE)).getPlayer().getID().equals(name)) {
	if (!isUser || Globals.get("serverpos", false)) {
		if (spr==null)
			init(gc, receiver);
		draw(xs, ys, x, y, px, py, spx, spy, cam);
		FontRenderer.drawString((xs+cam.getXOff())-FontRenderer.getWidth(getName())/2, (ys+cam.getYOff()-52), getName(), g);
		px = (int) x;
		py = (int) y;
		spx = xs;
		spy = ys;
		torchLight.move((int) xs, (int) ys);
		torchLight.randomize();

	} else {
		if (spr==null)
			init(gc, receiver);
		draw(cx, cy, cx, cy, px, py, px, py, cam);
		px = cx;
		py = cy;
		torchLight.move(cx, cy);
		torchLight.randomize();
	}
}

private void draw(float xd, float yd, int x, int y, int px, int py, float spx, float spy, Camera cam) {
	int tx = 0;
	int ty = 0;
	if (animState!=2) {
		int wrth = 1;
		if (Math.round(x*wrth)==Math.round(spx*wrth) && Math.round(y*wrth)==Math.round(spy*wrth))
			animState = 0;
		else {
			animState = 1;
			int ddir = (int) (Math.atan2(xd-spx, yd-spy)*(180f/Math.PI)+90);
			int bdir = (int) (ddir/90f);
			if (bdir==2) {
				dir = 0;
				flip = true;
			} else if (bdir==1) {
				dir = 1;
				flip = false;
			} else if (bdir==0) {
				dir = 0;
				flip = false;
			} else if (bdir==3) {
				dir = 2;
				flip = false;
			}
		}
	}
	if (animState==0) { //still
		tx = dir;
		ty = 3;
	} else if (animState==1) { //walking
		tx = (int) animFrame;
		ty = dir;
	} else if (animState==2) { //attacking
		if (dir==0) {
			ty = 3;
			tx = (int) (3+animFrame);
		} else if (dir==1) {
			ty = 4;
			tx = (int) animFrame;
		} else if (dir==2) {
			ty = 4;
			tx = (int) (3+animFrame);
		}
	}
	if (PlayerClient.BIGSIZE) {
		spr.getSprite(tx, ty).draw(((int) xd+cam.getXOff())+(flip?32:-32), ((int) yd+cam.getYOff())-78, (flip?-64:64), 96);
	} else {
		spr.getSprite(tx, ty).draw(((int) xd+cam.getXOff())+(flip?16:-16), ((int) yd+cam.getYOff())-39, (flip?-32:32), 48);
	}
	if (animState!=0) //if not still
		animFrame += 0.2f; //update animFrame
	if (animFrame >= 6 && animState==1)
		animFrame = 0;
	if (animFrame >= 3 && animState==2)
		animFrame = animState = 0;
}

@Override
public void receiveMessageExt(Message msg, MessageEndPoint server) {
	if (msg.getName().equals("moveClient")) {
		this.cx = msg.getData().getInt("x", 0);
		this.cy = msg.getData().getInt("y", 0);
		this.isUser = true;
	} else if (msg.getName().equals("putItem")) {
		pdat.put(ItemFactory.getItem(msg.getData().getString("item", "Null")), msg.getData().getString("extd", ""), region, receiverName); //TODO: proper extd
	} else if (msg.getName().equals("equip")) {
		pdat.setEquipped(pdat.inventory.get(msg.getData().getInt("i", 0)), region, receiverName);
	} else if (msg.getName().equals("use")) {
		PlayerData.InventoryEntry ie = pdat.inventory.get(msg.getData().getInt("i", 0));
		if (ie!=null)
			if (ie.getItem().onUse((GameServer) server, this, ie))
				pdat.removeItem(msg.getData().getInt("i", 0), region, receiverName);
	} else if (msg.getName().equals("drop")) {
		PlayerData.InventoryEntry ie = pdat.inventory.get(msg.getData().getInt("i", 0));
		if (ie!=null) {
			int dx = x-16;
			int dy = y-16;
			if (!ie.getItem().stickyDrops()) {
				int attempts = 0;
				do {
					dx = x-16;
					dy = y-16;
					double rnd = Math.random()*Math.PI*2;
					dx = (int) (dx+(32f*Math.sin(rnd)));
					dy = (int) (dy+(30f*-Math.cos(rnd)));
					attempts++;
				} while (!(region.aiPlaceFree(dx, dy) &&
						           region.aiPlaceFree(dx+32, dy) &&
						           region.aiPlaceFree(dx+32, dy+32) &&
						           region.aiPlaceFree(dx, dy+32)) && attempts < 100);
				if (attempts==100) {
					return;
				}
			}
			region.addEntityServer("Entity"+(ie.getItem().stickyDrops()?"Placed":"")+"Item,"+dx+","+dy+","+ie.getItem().name+","+ie.getExtd());
			pdat.removeItem(msg.getData().getInt("i", 0), region, receiverName);
		}
	} else if (msg.getName().equals("craftItem")) {
		CraftUI.getRecipe(msg.getData().getInt("i", 0)).addToPDAT(pdat, region, receiverName);
	} else if (msg.getName().equals("setPDAT")) {
		if (pdat==null)
			pdat = new PlayerData(extd, msg.getConnection());
		pdat.fromHBT(msg.getData());
		pdat.updated(region, receiverName);
	} else if (msg.getName().equals("setHealth")) {
		pdat.health = msg.getData().getInt("health", 60);
		pdat.updated(region, receiverName);
	} else if (msg.getName().equals("setAffinity")) {
		pdat.affinity = msg.getData().getFlag("element", "NEUTRAL").asElement();
		pdat.updated(region, receiverName);
	} else if (msg.getName().equals("pdat_SET")) {
		if (pdat==null)
			pdat = new PlayerData(extd, msg.getConnection());
		pdat.fromHBT(msg.getData());
		pdat.updated(region, receiverName);
	} else if (msg.getName().equals("pdat_GET")) {
		msg.reply("hbtResponse", (HBTCompound) pdat.toHBT(), this);
	} else {
		Log.info("ENTITYPLAYER: Ignored message "+msg.toString());
	}
}

public String getName() {
	return extd.split(",")[0];
}

@Override
public void hurt(Region region, Entity entity, MessageEndPoint receiver) {
	if (Globals.get("godmode", false))
		return;
	pdat.health -= 1;
	if (pdat.health <= 0) { //TODO: Proper player kill code
		//this.drop(region);
		region.receiveMessage(new Message(region.name+".killSERV", HBTTools.msgString("ent", this.name)), receiver);
		//((GameServer) receiver).changePlayerRegion("start", 800, 532, connection, true); //TODO: Below line causes crash.
		MessageSystem.sendClient(this, connection, new Message("PLAYER.playerInfo", HBTTools.location("start", 800, 532)), false); //TODO: proper respawn place maybe
		pdat.health = pdat.healthMax;
	}
	pdat.updated(region, receiverName);
}

@Override
public void kill(GameClient gs) {
	gs.lm.removeLight(torchLight);
}

public Connection getConnection() {
	return connection;
}

public void setConnection(Connection connection) {
	this.connection = connection;
}

/**
 * Put an item in this player's inventory
 *
 * @param item
 * 		Item to put into inventory
 * @return true if successful
 */
public boolean putItem(Item item, String extd) {
	return pdat.put(item, extd, region, receiverName);
}

public void setPDat(HBTCompound data) {
	if (pdat==null)
		pdat = new PlayerData(getName(), null);
	this.pdat.fromHBT(data);
}

@Override
public Element getElement() {
	return pdat.affinity;
}

@Override
public PlayerData.InventoryEntry getEquipped() {
	return pdat.getEquipped();
}
}
