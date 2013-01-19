package net.halitesoft.lote.world.entity;


import net.halitesoft.lote.Message;
import net.halitesoft.lote.MessageReceiver;
import net.halitesoft.lote.MessageSystem;
import net.halitesoft.lote.system.Camera;
import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.system.GameServer;
import net.halitesoft.lote.system.Light;
import net.halitesoft.lote.system.Main;
import net.halitesoft.lote.system.PlayerData;
import net.halitesoft.lote.world.Region;
import net.halitesoft.lote.world.item.Item;
import net.halitesoft.lote.world.item.ItemFactory;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

public class EntityPlayer extends Entity {

	SpriteSheet spr;
	public int cx,cy;
	private int px,py;
	private float spx,spy;
	private int animState,dir; //animState: 0=still,1=moving,2=attacking; dir: 0=left, 1=up, 2=down
	private float animFrame;
	private boolean flip;
	private Light torchLight;
	public boolean isUser = false;
	private Connection connection;
	public PlayerData pdat;

	public EntityPlayer() {
		constantUpdate = true;
		cx1=cy1=-16;
		cx2=cy2=16;
	}
	
	public void setSERVDAT(Connection connection, PlayerData pdat) {
		this.connection=connection;
		this.pdat = pdat;
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver)
			throws SlickException {
		spr = new SpriteSheet(new Image("data/player/player_reg.png",false,0),32,48);
		torchLight = new Light(600,550,256,0.8f,0.5f,0.2f,0.4f); //TORCH LIGHT
		((GameClient) sbg.getState(Main.GAMEPLAYSTATE)).lm.addLight(torchLight);
		constantUpdate = true;
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g,
			Camera cam, GameClient receiver) throws SlickException {
		//if (!((GameplayState) sbg.getState(Main.GAMEPLAYSTATE)).getPlayer().getID().equals(name)) {
		if (!isUser) {
			if (spr==null)
				init(gc,sbg,receiver);
			draw(xs,ys,x,y,px,py,spx,spy,cam);
			Main.font.drawString((xs+cam.getXOff())-Main.font.getWidth(getName())/2,(ys+cam.getYOff()-52),getName());
			px=(int) x;
			py=(int) y;
			spx=xs;
			spy=ys;
			torchLight.move((int) xs,(int) ys);
			torchLight.randomize();
			
		} else {
			if (spr==null)
				init(gc,sbg,receiver);
			draw(cx,cy,cx,cy,px,py,px,py,cam);
			px=cx;
			py=cy;
			torchLight.move(cx,cy);
			torchLight.randomize();
		}
	}
	
	private void draw(float xd, float yd, int x, int y, int px, int py, float spx, float spy, Camera cam) {
		int tx=0;
		int ty=0;
		if (animState!=2) {
		int wrth = 1;
		if (Math.round(x*wrth)==Math.round(spx*wrth) && Math.round(y*wrth)==Math.round(spy*wrth))
			animState=0;
		else {
			animState=1;
			int ddir = (int) (Math.atan2(xd-spx,yd-spy)*(180f/Math.PI)+90);
			int bdir = (int) (ddir/90f);
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
		spr.getSprite(tx,ty).draw(((int) xd+cam.getXOff())+(flip?16:-16),((int) yd+cam.getYOff())-39,(flip?-32:32),48);
		if (animState != 0) //if not still
			animFrame+=0.2f; //update animFrame
		if (animFrame>=6 && animState==1)
			animFrame=0;
		if (animFrame>=3 && animState==2)
			animFrame=animState=0;
	}

	@Override
	public void receiveMessageExt(Message msg, MessageReceiver server) {
		if (msg.getName().equals("moveClient")) {
			this.cx=Integer.parseInt(msg.getData().split(",")[0]);
			this.cy=Integer.parseInt(msg.getData().split(",")[1]);
			this.isUser = true;
		} else if (msg.getName().equals("putItem")) {
			pdat.put(ItemFactory.getItem(msg.getData().split(",",2)[0]), msg.getData().split(",",2)[1],region,receiverName);
		} else if (msg.getName().equals("setPDAT")) {
			System.out.println("player ent received setPDAT "+msg);
			if (pdat==null)
				pdat=new PlayerData(extd,msg.getConnection());
			pdat.fromString(msg.getData());
		} else {
			Log.info("ENTITYPLAYER: Ignored message "+msg.toString());
		}
	}
	
	public String getName() {
		return extd.split(",")[0];
	}
	
	@Override
	public void hurt(Region region, Entity entity, MessageReceiver receiver) {
		pdat.health -=1;
		if (pdat.health<=0) { //TODO: Proper player kill code
			//this.drop(region);
			region.receiveMessage(new Message(region.name+".killSERV",this.name), receiver );
			//((GameServer) receiver).changePlayerRegion("start", 800, 532, connection, true);
			MessageSystem.sendClient(this, connection, new Message("PLAYER.playerInfo","start,800,532"), false);
			pdat.health=60;
		}
		pdat.updated(region,receiverName);
	}
	
	@Override
	public void kill(GameClient gs) {
		gs.lm.removeLight(torchLight);
	}
	
	@Override
	public int compareTo(Entity other) {
		if (other instanceof EntityPlayer && ((EntityPlayer) other).isUser == true)
			if (this.cy<((EntityPlayer) other).cy)
				return -1;
			else if (this.cy>((EntityPlayer) other).cy)
				return 1;
			else
				return 0;
		if (this.cy<other.ys)
			return -1;
		else if (this.cy>other.ys)
			return 1;
		else
			return 0;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Put an item in this player's inventory
	 * @param item Item to put into inventory
	 * @return true if successful
	 */
	public boolean putItem(Item item, String extd) {
		return pdat.put(item,extd,region,receiverName);
	}

	public void setPDat(String data) {
		if (pdat==null)
			pdat=new PlayerData(getName(),null);
		this.pdat.fromString(data);
	}
}
