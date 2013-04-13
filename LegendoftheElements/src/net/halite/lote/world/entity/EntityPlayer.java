package net.halite.lote.world.entity;

import com.esotericsoftware.minlog.Log;
import net.halite.lote.Element;
import net.halite.lote.lighting.Light;
import net.halite.lote.msgsys.Connection;
import net.halite.lote.msgsys.Message;
import net.halite.lote.msgsys.MessageReceiver;
import net.halite.lote.msgsys.MessageSystem;
import net.halite.lote.player.Camera;
import net.halite.lote.player.PlayerClient;
import net.halite.lote.player.PlayerData;
import net.halite.lote.system.FontRenderer;
import net.halite.lote.system.GameClient;
import net.halite.lote.system.GameServer;
import net.halite.lote.system.Globals;
import net.halite.lote.ui.CraftUI;
import net.halite.lote.util.FileHandler;
import net.halite.lote.world.Region;
import net.halite.lote.world.item.Item;
import net.halite.lote.world.item.ItemFactory;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

public class EntityPlayer extends Entity {

SpriteSheet spr;
public int cx, cy;
private int px, py;
private float spx, spy;
private int animState, dir; //animState: 0=still,1=moving,2=attacking; dir: 0=left, 1=up, 2=down
private float animFrame;
private boolean flip;
private Light torchLight;
public boolean isUser=false;
private Connection connection;
public PlayerData pdat;

public EntityPlayer() {
	constantUpdate=true;
	cx1=cy1=-16;
	cx2=cy2=16;
}

public void setSERVDAT(Connection connection, PlayerData pdat) {
	this.connection=connection;
	this.pdat=pdat;
}

@Override
public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver)
		throws SlickException {
	spr=new SpriteSheet(FileHandler.getImage("player.player_reg"), 32, 48);
	torchLight=new Light(600, 550, 256, 0.8f, 0.5f, 0.2f, 0.4f); //TORCH LIGHT
	//((GameClient) sbg.getState(Main.GAMEPLAYSTATE)).lm.addLight(torchLight);
	constantUpdate=true;
}

@Override
public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
                   Camera cam, GameClient receiver) throws SlickException {
	//if (!((GameplayState) sbg.getState(Main.GAMEPLAYSTATE)).getPlayer().getID().equals(name)) {
	if (!isUser) {
		if (spr==null)
			init(gc, sbg, receiver);
		draw(xs, ys, x, y, px, py, spx, spy, cam);
		FontRenderer.drawString((xs+cam.getXOff())-FontRenderer.getWidth(getName())/2, (ys+cam.getYOff()-52), getName(), g);
		px=(int) x;
		py=(int) y;
		spx=xs;
		spy=ys;
		torchLight.move((int) xs, (int) ys);
		torchLight.randomize();

	} else {
		if (spr==null)
			init(gc, sbg, receiver);
		draw(cx, cy, cx, cy, px, py, px, py, cam);
		px=cx;
		py=cy;
		torchLight.move(cx, cy);
		torchLight.randomize();
	}
}

private void draw(float xd, float yd, int x, int y, int px, int py, float spx, float spy, Camera cam) {
	int tx=0;
	int ty=0;
	if (animState!=2) {
		int wrth=1;
		if (Math.round(x*wrth)==Math.round(spx*wrth)&&Math.round(y*wrth)==Math.round(spy*wrth))
			animState=0;
		else {
			animState=1;
			int ddir=(int) (Math.atan2(xd-spx, yd-spy)*(180f/Math.PI)+90);
			int bdir=(int) (ddir/90f);
			if (bdir==2) {
				dir=0;
				flip=true;
			} else if (bdir==1) {
				dir=1;
				flip=false;
			} else if (bdir==0) {
				dir=0;
				flip=false;
			} else if (bdir==3) {
				dir=2;
				flip=false;
			}
		}
	}
	if (animState==0) { //still
		tx=dir;
		ty=3;
	} else if (animState==1) { //walking
		tx=(int) animFrame;
		ty=dir;
	} else if (animState==2) { //attacking
		if (dir==0) {
			ty=3;
			tx=(int) (3+animFrame);
		} else if (dir==1) {
			ty=4;
			tx=(int) animFrame;
		} else if (dir==2) {
			ty=4;
			tx=(int) (3+animFrame);
		}
	}
	if (PlayerClient.BIGSIZE) {
		spr.getSprite(tx, ty).draw(((int) xd+cam.getXOff())+(flip?32:-32), ((int) yd+cam.getYOff())-78, (flip?-64:64), 96);
	} else {
		spr.getSprite(tx, ty).draw(((int) xd+cam.getXOff())+(flip?16:-16), ((int) yd+cam.getYOff())-39, (flip?-32:32), 48);
	}
	if (animState!=0) //if not still
		animFrame+=0.2f; //update animFrame
	if (animFrame>=6&&animState==1)
		animFrame=0;
	if (animFrame>=3&&animState==2)
		animFrame=animState=0;
}

@Override
public void receiveMessageExt(Message msg, MessageReceiver server) {
	if (msg.getName().equals("moveClient")) {
		this.cx=Integer.parseInt(msg.getDataStr().split(",")[0]);
		this.cy=Integer.parseInt(msg.getDataStr().split(",")[1]);
		this.isUser=true;
	} else if (msg.getName().equals("putItem")) {
		pdat.put(ItemFactory.getItem(msg.getDataStr().split(",", 2)[0]), msg.getDataStr().split(",", 2)[1], region, receiverName);
	} else if (msg.getName().equals("equip")) {
		pdat.setEquipped(pdat.inventory.get(Integer.parseInt(msg.getDataStr())), region, receiverName);
	} else if (msg.getName().equals("use")) {
		PlayerData.InventoryEntry ie=pdat.inventory.get(Integer.parseInt(msg.getDataStr()));
		if (ie!=null)
			if (ie.getItem().onUse((GameServer) server, this, ie))
				pdat.removeItem(Integer.parseInt(msg.getDataStr()), region, receiverName);
	} else if (msg.getName().equals("drop")) {
		PlayerData.InventoryEntry ie=pdat.inventory.get(Integer.parseInt(msg.getDataStr()));
		if (ie!=null) {
			int dx=x-16;
			int dy=y-16;
			if (!ie.getItem().stickyDrops()) {
				int attempts = 0;
				do {
					dx=x-16;
					dy=y-16;
					double rnd = Math.random()*Math.PI*2;
					dx=(int) (dx+(32f*Math.sin(rnd)));
					dy=(int) (dy+(30f*-Math.cos(rnd)));
					attempts++;
				} while (!(region.aiPlaceFree(dx,dy) &&
						region.aiPlaceFree(dx+32,dy) &&
						region.aiPlaceFree(dx+32,dy+32) &&
                        region.aiPlaceFree(dx,dy+32)) && attempts<100);
				if (attempts==100) {
					return;
				}
			}
			System.out.println(dx+","+dy);
			region.addEntityServer("Entity"+(ie.getItem().stickyDrops()?"Placed":"")+"Item,"+dx+","+dy+","+ie.getItem().name+","+ie.getExtd());
			pdat.removeItem(Integer.parseInt(msg.getDataStr()), region, receiverName);
		}
	} else if (msg.getName().equals("craftItem")) {
		CraftUI.getRecipe(Integer.parseInt(msg.getDataStr())).addToPDAT(pdat, region, receiverName);
	} else if (msg.getName().equals("setPDAT")) {
		if (pdat==null)
			pdat=new PlayerData(extd, msg.getConnection());
		pdat.fromString(msg.getDataStr());
	} else if (msg.getName().equals("setHealth")) {
		pdat.health=Integer.parseInt(msg.getDataStr());
		pdat.updated(region, receiverName);
	} else if (msg.getName().equals("setAffinity")) {
		pdat.affinity=Element.valueOf(msg.getDataStr());
		pdat.updated(region, receiverName);
	} else {
		Log.info("ENTITYPLAYER: Ignored message "+msg.toString());
	}
}

public String getName() {
	return extd.split(",")[0];
}

@Override
public void hurt(Region region, Entity entity, MessageReceiver receiver) {
	if (Globals.get("godmode", false))
		return;
	pdat.health-=1;
	if (pdat.health<=0) { //TODO: Proper player kill code
		//this.drop(region);
		region.receiveMessage(new Message(region.name+".killSERV", this.name), receiver);
		//((GameServer) receiver).changePlayerRegion("start", 800, 532, connection, true);
		MessageSystem.sendClient(this, connection, new Message("PLAYER.playerInfo", "start,800,532"), false);
		pdat.health=pdat.healthMax;
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
	this.connection=connection;
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

public void setPDat(String data) {
	if (pdat==null)
		pdat=new PlayerData(getName(), null);
	this.pdat.fromString(data);
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
