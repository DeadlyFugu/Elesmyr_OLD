package net.halite.lote.system;

import com.esotericsoftware.minlog.Log;
import net.halite.hbt.HBTCompound;
import net.halite.hbt.HBTInt;
import net.halite.hbt.HBTString;
import net.halite.lote.Save;
import net.halite.lote.ScriptObject;
import net.halite.lote.lighting.LightMap;
import net.halite.lote.msgsys.Message;
import net.halite.lote.msgsys.MessageReceiver;
import net.halite.lote.msgsys.MessageSystem;
import net.halite.lote.player.Camera;
import net.halite.lote.player.PlayerClient;
import net.halite.lote.ui.*;
import net.halite.lote.util.FileHandler;
import net.halite.lote.world.Region;
import net.halite.lote.world.World;
import net.halite.lote.world.entity.Entity;
import net.halite.lote.world.item.ItemFactory;
import org.newdawn.slick.*;
import org.newdawn.slick.Input;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.LinkedList;

public class GameClient extends BasicGameState implements MessageReceiver {

public class ChatMessage {
	private String msg;
	private int time;

	public ChatMessage(String msg) {
		this.msg=msg;
		this.time=500;
	}

	public void update() {
		if (time>0)
			time--;
	}

	public String getMessage() { return msg; }

	public float getAlpha() {
		if (time>100)
			return 1;
		if (time==0)
			return 0;
		return time/100f;
	}
}

int stateID=-1;
private GameServer server;
//private ConcurrentLinkedQueue<Message> msgList;
public boolean regionLoaded=false;

public PlayerClient player; //Client player controller.

/** Client-side world */
private World world;
public Camera cam;
public LightMap lm;

public float time=-20; //in-game time in minutes
private float servtime=-20; //time according to server
public int date; //in-game date

private LinkedList<ChatMessage> chat;
private TextField textField;
private boolean showTextField;

private String error=null;

private ScriptObject chatso;

public LinkedList<UserInterface> ui;

private Image vignette;
private Image alphabg;
private Object ChatUI;

GameClient(int stateID) {
	this.stateID=stateID;
}

@Override
public int getID() {return stateID;}

public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
	ItemFactory.init();
	regionLoaded=false;
	error=null;
	chatso=new ScriptObject("Console", this);
	time=-20;
	servtime=-20;
	//msgList = new ConcurrentLinkedQueue<Message>();
	chat=new LinkedList<ChatMessage>();
	ui=new LinkedList<UserInterface>();
	HUDUI hud=new HUDUI();
	hud.init(gc, sbg, this);
	ui.addFirst(hud);
	world=new World();
	player=new PlayerClient(this, world);
	player.init(gc, sbg, this);
	cam=new Camera(10, 10);
	lm=new LightMap(true, (int) (MainMenuState.lres*Main.INTERNAL_ASPECT), MainMenuState.lres);
	vignette=FileHandler.getImage("ui.vignette");
	alphabg=FileHandler.getImageBlurry("ui.alphabg");
	System.gc();

	textField=new TextField(gc, FontRenderer.getFont(), 10, Main.INTERNAL_RESY-84, 530, 16);
	textField.setBorderColor(null);
	textField.setBackgroundColor(new Color(0, 0, 0, 0.2f));
	textField.setTextColor(Color.white);
	textField.setAcceptingInput(false);
	textField.setMaxLength(57);
}

public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
	//g.scale(gc.getWidth()/((float) (Main.INTERNAL_RESY)*((float) gc.getWidth()/(float) gc.getHeight())),gc.getHeight()/(float) (Main.INTERNAL_RESY));
	g.scale(gc.getWidth()/(float) (Main.INTERNAL_RESX), gc.getHeight()/(float) (Main.INTERNAL_RESY));
	if (!regionLoaded) {
		//g.setColor(Color.black);
		//g.fillRect(0,0,Main.INTERNAL_RESX,Main.INTERNAL_RESY);
		//g.setColor(Color.white);
	} else if (MessageSystem.CLIENT) {
		g.pushTransform();
		renderMap(player.region, false);
		world.render(gc, sbg, g, cam, this);
		player.render(gc, sbg, g, cam, this);
		renderMap(player.region, true);
		if (Globals.get("debug", false)&&Globals.get("showEnt", true)) { //Show ent IDs.
			for (Entity e : player.region.entities.values()) //TODO: Somehow this line throws an NPE occasionally O.o
				FontRenderer.drawString(e.x+cam.getXOff(), e.y+cam.getYOff(), e.name, g);
		}
		g.popTransform();
		g.pushTransform();
		g.scale((float) Main.INTERNAL_RESX/(lm.resx-2), (float) Main.INTERNAL_RESY/(lm.resy-2));
		g.fillRect(0, 0, 0, 0);
		g.translate(((cam.getXOff()/(float) lm.gw)%1), ((cam.getYOff()/(float) lm.gh)%1));
		lm.render();
		g.setDrawMode(Graphics.MODE_NORMAL);
		g.popTransform();
		int i=1;
		for (ChatMessage cm : chat) {
			if (i==22)
				break;
			g.setColor(new Color(0, 0, 0, (showTextField?0.25f:cm.getAlpha()*0.25f)));
			g.fillRect(8, Main.INTERNAL_RESY-89-i*18, FontRenderer.getWidth(cm.getMessage())+4, 18);
			g.setColor(Color.white);
			if (showTextField)
				FontRenderer.drawString(10, Main.INTERNAL_RESY-89-i*18, cm.getMessage(), g);
			else if (cm.getAlpha()!=0)
				FontRenderer.drawString(10, Main.INTERNAL_RESY-89-i*18, cm.getMessage(), new Color(1, 1, 1, cm.getAlpha()), g);
			i++;
		}
		Iterator<UserInterface> itui=ui.descendingIterator();
		while (itui.hasNext()) {
			UserInterface uii=itui.next();
			if (!uii.inited()) {
				uii.init(gc, sbg, this);
				Log.info(uii+" was init-ed");
			}
			if (uii.blockUpdates()) {
				alphabg.draw(0, 0, Main.INTERNAL_RESX, Main.INTERNAL_RESY);
				vignette.draw(0, 0, Main.INTERNAL_RESX, Main.INTERNAL_RESY);
			}
			uii.render(gc, sbg, g, cam, this);
		}

		if (showTextField) {
			textField.render(gc, g);
			textField.setFocus(true);
		}
	} else {
		g.setColor(Color.black);
		g.fillRect(0, 0, Main.INTERNAL_RESX, Main.INTERNAL_RESY);
		g.setColor(Color.white);
	}
	if (!MessageSystem.CLIENT)
		FontRenderer.drawString(0, 0, "#$bar.title| |"+Main.version+": "+(MessageSystem.CLIENT?"|$bar.client| ":"")+(MessageSystem.SERVER?"|$bar.server| ":"")+(MessageSystem.fastLink?"|$bar.fastlink| ":""), g);
	if (MessageSystem.SERVER&&(!MessageSystem.CLIENT||(Globals.get("debug", false)&&Globals.get("showConnections", true)))) {
		if (!MessageSystem.CLIENT) {
			g.setColor(Color.black);
			g.fillRect(0, 0, Main.INTERNAL_RESX, Main.INTERNAL_RESY);
			g.setColor(Color.white);
		}
		server.render(gc, sbg, g, MessageSystem.CLIENT);
	}
	if (MessageSystem.CLIENT&&Globals.get("debug", false)&&Globals.get("debugInfo", true)) {
		//Client debug mode
		String debugText="ERROR.";
		try {
			debugText=
					"Pos: X="+player.x+" Y="+player.y+"\n"+
							"Time raw: "+time+"\n"+
							"Time norm: "+(int) ((time/60)%12)+":"+(int) (time%60)+"\n"+
							"FPS: "+gc.getFPS()+"\n"+
							"Ents: "+player.region.entities.size();
		} catch (Exception e) {
		}
			/*try {
				debugText = debugText+"Inventory:\n"+((EntityPlayer) player.region.entities.get(player.entid)).pdat.invToString();
			} catch (Exception e) {
			}*/
		int i=1;
		for (String s : debugText.split("\n")) {
			FontRenderer.drawString(Main.INTERNAL_RESX-FontRenderer.getWidth(s)-10, i*18, s, g);
			i++;
		}
	}
}

private void renderMap(Region region, boolean fg) {
	if (region==null||region.map==null)
		return;
	for (int i=0; i<region.map.getLayerCount(); i++) {
		if (region.map.getLayerIndex("col")!=i) {
			//if (fg==Boolean.parseBoolean(region.map.getLayerProperty(i, "fg", "false")));
			if (fg==(region.map.getLayerIndex("fg")==i))
				region.map.render(cam.getXOff(), cam.getYOff(), i);
			//region.map.render(cam.getXOff()%32,cam.getYOff()%32,-cam.getXOff()/32, -cam.getYOff()/32,Main.INTERNAL_RESX+32/32,Main.INTERNAL_RESY+32/32, i,false);
		}
	}
}

public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
	if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
		if (showTextField) {
			showTextField=false;
			textField.setText("");
			textField.setAcceptingInput(false);
			textField.setFocus(false);
		} else if (ui.peekFirst().blockUpdates()) {
			ui.removeFirst();
		} else {
			MessageSystem.close();
			gc.getInput().clearKeyPressedRecord();
			sbg.enterState(Main.MENUSTATE);
			return;
		}
	}
	if (gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
		if (regionLoaded) {
			if (!showTextField) {
				showTextField=true;
				textField.setText("");
				textField.setAcceptingInput(true);
				textField.setFocus(true);
			} else {
				if (textField.getText().startsWith("<")
						    &&textField.getText().contains(":")
						    &&textField.getText().split(":", 2)[0].contains(".")) {
					MessageSystem.sendServer(null, new Message(textField.getText().split(":", 2)[0].substring(1), textField.getText().split(":", 2)[1]), false);
				} else if (textField.getText().startsWith("/")) {
					//chatInitVar=ScriptRunner.run(textField.getText().substring(1), "ccmd", this,chatInitVar+" CLIENT=true SERVER=false").substring(1);
					chatso.call("call", new Object[]{textField.getText().substring(1), this});
				} else
					MessageSystem.sendServer(null, new Message("SERVER.chat", textField.getText()), true);
				showTextField=false;
				textField.setText("");
				textField.setAcceptingInput(false);
				textField.setFocus(false);
			}
		}
	}
	if (gc.getInput().isKeyPressed(Input.KEY_F3)) {
		Globals.set("debug", ""+!Globals.get("debug", false));
	}
	if (net.halite.lote.system.Input.isKeyPressed(gc, "inv")&&showTextField==false) {
		if (ui.peekFirst() instanceof InventoryUI) {
			ui.removeFirst();
		} else if (!ui.peekFirst().blockUpdates()) {
			InventoryUI inv=new InventoryUI();
			inv.init(gc, sbg, this);
			ui.addFirst(inv);
		}
	}
	if (MessageSystem.SERVER) {
		server.gameUpdate();
	}
	if (MessageSystem.CLIENT) {
		MessageSystem.receiveMessageClient();

		if (!MessageSystem.clientConnected())
			error="Lost connection to server.";

		if (error!=null) {
			gc.getInput().clearKeyPressedRecord();
			MessageSystem.close();
			((ErrorState) sbg.getState(Main.ERRORSTATE)).errorText=error;
			sbg.enterState(Main.ERRORSTATE);
		}
		boolean blocked=false;
		if (ui.peekFirst().blockUpdates()) {
			ui.peekFirst().update(gc, sbg, this);
			blocked=true;
			gc.getInput().clearKeyPressedRecord();
		}
		for (UserInterface uii : ui) {
			if (!blocked||!uii.blockUpdates())
				uii.update(gc, sbg, this);
		}
		for (ChatMessage cm : chat)
			cm.update();
		world.clientUpdate(gc, sbg, this);
		if (!blocked)
			player.clientUpdate(gc, sbg, this);
		cam.update(player);
		lm.update(player.region, cam, time);
		time+=(servtime-time)/120;
	}
}

public boolean receiveMessage(Message msg) {
	if (msg==null) {
		return true;
	} else if (msg.getTarget().equals("CLIENT")) {
		String name=msg.getName();
		if (name.equals("setRegion")) {
			lm.clearLight();
			String rname=msg.getDataStr().split(":", 2)[0];
			world.touchRegionClient(rname);
			world.getRegion(rname).parseEntityString(msg.getDataStr().split(":", 2)[1], true);
			lm.addLight(world.getRegion(rname).getLights());
			player.setRegion(rname);
			regionLoaded=true;
		} else if (name.equals("chat")) {
			chat.addFirst(new ChatMessage(msg.getDataStr()));
			//if (chat.size()>5) {
			//	chat.removeLast();
			//}
		} else if (name.equals("talk")) {
			if (ui.peekFirst() instanceof ChatUI) {
				((ChatUI) ui.peekFirst()).setMsg(msg.getDataStr().split(":", 2)[0], msg.getDataStr().split(":", 2)[1], msg);
			} else {
				ChatUI nui=new ChatUI();
				nui.setMsg(msg.getDataStr().split(":", 2)[0], msg.getDataStr().split(":", 2)[1], msg);
				ui.addFirst(nui);
			}
		} else if (name.equals("echointwl")) {
			msg.reply(msg.getSender()+".tresponse", FontRenderer.getLang()+"|int", null);
		} else if (name.equals("error")) {
			error=msg.getDataStr();
		} else if (name.equals("time")) {
			float oltime=servtime;
			servtime=Float.parseFloat(msg.getDataStr().split(":",2)[0]);
			date=Integer.parseInt(msg.getDataStr().split(":",2)[1]);
			if (servtime>oltime+10||servtime<oltime-10) {
				Log.info("Time skip");
				lm.skipFade(servtime/60f, player.region);
				time=servtime;
			}
		} else if (name.equals("openUI")) {
			UserInterface nui=UIFactory.getUI(msg.getDataStr());
			if (nui!=null)
				ui.addFirst(nui);
			else
				Log.warn("CLIENT: Could not open UI "+msg.getDataStr());
		} else if (name.equals("book")) {
			if (ui.peekFirst() instanceof BookUI) {
				((BookUI) ui.peekFirst()).addPage(msg.getDataStr());
			} else {
				BookUI nui=new BookUI();
				nui.ctor(null);
				nui.addPage(msg.getDataStr());
				ui.addFirst(nui);
			}
		} else {
			Log.warn("CLIENT: Ignored message - unrecognised name: "+msg.toString());
		}
	} else if (msg.getTarget().equals("PLAYER")) {
		player.receiveMessage(msg, this);
	} else {
		world.receiveMessage(msg, this);
	}
	return false;
}

public void loadSave(GameContainer gc, String saveName, boolean serverOnly, StateBasedGame sbg) throws Exception {
	Save save=new Save(saveName);
	if (serverOnly) {
		MessageSystem.SERVER=true;
		MessageSystem.CLIENT=false;
	} else {
		MessageSystem.SERVER=true;
		MessageSystem.CLIENT=true;
	}
	MessageSystem.initialise(MessageSystem.CLIENT?this:null, MessageSystem.SERVER,InetAddress.getLocalHost(),save);
	this.server=MessageSystem.server;
	if (MessageSystem.SERVER)
		server.load();
	if (MessageSystem.CLIENT) {
		HBTCompound tag = new HBTCompound("p");
		tag.addTag(new HBTString("name",Globals.get("name", "Player")));
		tag.addTag(new HBTInt("pass","".hashCode()));
		MessageSystem.sendServer(null, new Message("SERVER.wantPlayer", tag), false);
	}
}

public PlayerClient getPlayer() {
	return player;
}

public void join(InetAddress hostaddr) throws Exception {
	MessageSystem.SERVER=false;
	MessageSystem.CLIENT=true;
	MessageSystem.initialise(this, false, hostaddr, null);
}

@Deprecated public void sendMessage(String name, String data) {
	MessageSystem.sendServer(null, new Message(name, data), false);
}

public void login(String name, int pass) {
	HBTCompound tag = new HBTCompound("p");
	tag.addTag(new HBTString("name",Globals.get("name", "Player")));
	tag.addTag(new HBTInt("pass",pass));
	MessageSystem.sendServer(null, new Message("SERVER.wantPlayer", tag), false);
}

public GameServer getServer() {
	return this.server;
}

@Override
public boolean isServer() {
	return false;
}
}