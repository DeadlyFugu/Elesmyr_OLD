package net.sekien.elesmyr.system;

import com.esotericsoftware.minlog.Log;
import net.sekien.elesmyr.Profiler;
import net.sekien.elesmyr.Save;
import net.sekien.elesmyr.ScriptObject;
import net.sekien.elesmyr.lighting.LightMap;
import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.msgsys.MessageSystem;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.player.PlayerClient;
import net.sekien.elesmyr.ui.*;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.elesmyr.world.Region;
import net.sekien.elesmyr.world.World;
import net.sekien.elesmyr.world.entity.Entity;
import net.sekien.elesmyr.world.item.ItemFactory;
import net.sekien.hbt.HBTCompound;
import net.sekien.hbt.HBTInt;
import net.sekien.hbt.HBTString;
import net.sekien.hbt.HBTTools;
import net.sekien.pepper.StateManager;
import org.newdawn.slick.*;
import org.newdawn.slick.Input;
import org.newdawn.slick.gui.*;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GameClient implements MessageEndPoint {

public class ChatMessage {
	private String msg;
	private int time;

	public ChatMessage(String msg) {
		this.msg = msg;
		this.time = 500;
	}

	public void update() {
		if (time > 0)
			time--;
	}

	public String getMessage() { return msg; }

	public float getAlpha() {
		if (time > 100)
			return 1;
		if (time==0)
			return 0;
		return time/100f;
	}
}

int stateID = -1;
private GameServer server;
//private ConcurrentLinkedQueue<Message> msgList;
public boolean regionLoaded = false;

public PlayerClient player; //Client player controller.

/** Client-side world */
private World world;
public Camera cam;
public LightMap lm;

public float time = -20; //in-game time in minutes
private float servtime = -20; //time according to server
public int date; //in-game date

private LinkedList<ChatMessage> chat;
private TextField textField;
private boolean showTextField;

private String error = null;

private ScriptObject chatso;

public LinkedList<UserInterface> ui;

private Image vignette;
private Image alphabg;
private Object ChatUI;

public GameClient(int stateID) {
	this.stateID = stateID;
}

public int getID() {return stateID;}

public void init(GameContainer gc) throws SlickException {
	ItemFactory.init();
	regionLoaded = false;
	error = null;
	chatso = new ScriptObject("Console", this);
	time = -20;
	servtime = -20;
	//msgList = new ConcurrentLinkedQueue<Message>();
	chat = new LinkedList<ChatMessage>();
	ui = new LinkedList<UserInterface>();
	HUDUI hud = new HUDUI();
	hud.init(gc, this);
	ui.addFirst(hud);
	world = new World();
	player = new PlayerClient(this, world);
	player.init(gc, this);
	cam = new Camera(10, 10);
	lm = new LightMap(true, (int) (MainMenuState.lres*Main.INTERNAL_ASPECT), MainMenuState.lres);
	vignette = FileHandler.getImage("ui.vignette");
	alphabg = FileHandler.getImageBlurry("ui.alphabg");
	System.gc();

	textField = new TextField(gc, FontRenderer.getFont(), 10, Main.INTERNAL_RESY-84, 530, 16);
	textField.setBorderColor(null);
	textField.setBackgroundColor(new Color(0, 0, 0, 0.2f));
	textField.setTextColor(Color.white);
	textField.setAcceptingInput(false);
	textField.setMaxLength(57);
}

public void render(net.sekien.pepper.Renderer renderer) throws SlickException {
	GameContainer gc = renderer.gc;
	Graphics g = renderer.g;
	//g.scale(gc.getWidth()/((float) (Main.INTERNAL_RESY)*((float) gc.getWidth()/(float) gc.getHeight())),gc.getHeight()/(float) (Main.INTERNAL_RESY));
	//g.scale(gc.getWidth()/(float) (Main.INTERNAL_RESX), gc.getHeight()/(float) (Main.INTERNAL_RESY));
	if (!regionLoaded) {
		//g.setColor(Color.black);
		//g.fillRect(0,0,Main.INTERNAL_RESX,Main.INTERNAL_RESY);
		//g.setColor(Color.white);
	} else if (MessageSystem.CLIENT) {
		g.pushTransform();
		renderMap(player.region, false);
		world.render(gc, g, cam, this);
		player.render(gc, g, cam, this);
		renderMap(player.region, true);
		if (Globals.get("debug", false) && Globals.get("showEnt", true)) { //Show ent IDs.
			for (Entity e : player.region.entities.values()) //TODO: Somehow this line throws an NPE occasionally O.o
				FontRenderer.drawString(e.x+cam.getXOff(), e.y+cam.getYOff(), ""+e.id, g);
		}
		g.popTransform();
		g.pushTransform();
		g.scale((float) Main.INTERNAL_RESX/(lm.resx-2), (float) Main.INTERNAL_RESY/(lm.resy-2));
		g.fillRect(0, 0, 0, 0);
		g.translate(((cam.getXOff()/(float) lm.gw)%1), ((cam.getYOff()/(float) lm.gh)%1));
		lm.render();
		g.setDrawMode(Graphics.MODE_NORMAL);
		g.popTransform();
		int i = 1;
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
		Iterator<UserInterface> itui = ui.descendingIterator();
		while (itui.hasNext()) {
			UserInterface uii = itui.next();
			if (!uii.inited()) {
				uii.init(gc, this);
				Log.info(uii+" was init-ed");
			}
			if (uii.blockUpdates()) {
				alphabg.draw(0, 0, Main.INTERNAL_RESX, Main.INTERNAL_RESY);
				vignette.draw(0, 0, Main.INTERNAL_RESX, Main.INTERNAL_RESY);
			}
			uii.render(renderer, cam, this);
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
	if (MessageSystem.SERVER && (!MessageSystem.CLIENT || (Globals.get("debug", false) && Globals.get("showConnections", true)))) {
		if (!MessageSystem.CLIENT) {
			g.setColor(Color.black);
			g.fillRect(0, 0, Main.INTERNAL_RESX, Main.INTERNAL_RESY);
			g.setColor(Color.white);
		}
		server.render(gc, g, MessageSystem.CLIENT);
	}
	if (MessageSystem.CLIENT && Globals.get("debugInfo", true)) {
		//Client debug mode
		String debugText = "ERROR.";
		try {
			debugText =
					"Pos: X="+player.x+" Y="+player.y+"\n"+
							"Time raw: "+time+"\n"+
							"Time norm: "+(int) ((time/60)%12)+":"+(int) (time%60)+"\n"+
							"FPS: "+gc.getFPS()+"\n"+
							"Ents: "+player.region.entities.size();
			long total = 0;
			for (Map.Entry entry : Profiler.getTimes().entrySet()) {
				debugText += "\n"+entry.getKey()+": "+TimeUnit.MILLISECONDS.convert((Long) entry.getValue(), TimeUnit.NANOSECONDS)+"ms";
				total += (Long) entry.getValue();
			}
			long ms = TimeUnit.MILLISECONDS.convert(total, TimeUnit.NANOSECONDS);
			debugText += "\nTotal: "+ms+"ms";
			debugText += "\nFPS: "+(1000/ms);
			if (ms <= 16) {
				debugText += "\n"+(16-ms)+"ms under";
			} else {
				debugText += "\n"+(ms-16)+"ms over";
			}
		} catch (Exception e) {
		}
			/*try {
				debugText = debugText+"Inventory:\n"+((EntityPlayer) player.region.entities.get(player.entid)).pdat.invToString();
			} catch (Exception e) {
			}*/
		int i = 1;
		for (String s : debugText.split("\n")) {
			FontRenderer.drawString(Main.INTERNAL_RESX-FontRenderer.getWidth(s)-10, i*18, s, g);
			i++;
		}
	}
}

private void renderMap(Region region, boolean fg) {
	if (region==null || region.map==null)
		return;
	for (int i = 0; i < region.map.getLayerCount(); i++) {
		if (region.map.getLayerIndex("col")!=i) {
			//if (fg==Boolean.parseBoolean(region.map.getLayerProperty(i, "fg", "false")));
			if (fg==(region.map.getLayerIndex("fg")==i))
				region.map.render(cam.getXOff(), cam.getYOff(), i);
			//region.map.render(cam.getXOff()%32,cam.getYOff()%32,-cam.getXOff()/32, -cam.getYOff()/32,Main.INTERNAL_RESX+32/32,Main.INTERNAL_RESY+32/32, i,false);
		}
	}
}

private boolean devModeInited = false;

public void update(GameContainer gc, int delta) throws SlickException {
	if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
		if (showTextField) {
			showTextField = false;
			textField.setText("");
			textField.setAcceptingInput(false);
			textField.setFocus(false);
		} else if (ui.peekFirst().blockUpdates()) {
			ui.removeFirst();
		} else {
			MessageSystem.close();
			gc.getInput().clearKeyPressedRecord();
			StateManager.back();
			return;
		}
	}
	if (Globals.get("debug", false) && regionLoaded) {
		if (!devModeInited) {
			DevMode dm = new DevMode();
			dm.init(gc, this);
			ui.addFirst(dm);
			devModeInited = true;
		}
	}
	if (gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
		if (regionLoaded) {
			if (!showTextField) {
				showTextField = true;
				textField.setText("");
				textField.setAcceptingInput(true);
				textField.setFocus(true);
			} else {
				if (textField.getText().startsWith("<") //TODO: Remove - Deprecated by DevMode and useless due to msgsys using HBT.
						    && textField.getText().contains(":")
						    && textField.getText().split(":", 2)[0].contains(".")) {
					MessageSystem.sendServer(null, new Message(textField.getText().split(":", 2)[0].substring(1), textField.getText().split(":", 2)[1]), false);
				} else if (textField.getText().startsWith("/")) {
					//chatInitVar=ScriptRunner.run(textField.getText().substring(1), "ccmd", this,chatInitVar+" CLIENT=true SERVER=false").substring(1);
					chatso.call("call", new Object[]{textField.getText().substring(1), this});
				} else
					MessageSystem.sendServer(null, new Message("SERVER.chat", HBTTools.msgString("msg", textField.getText())), true);
				showTextField = false;
				textField.setText("");
				textField.setAcceptingInput(false);
				textField.setFocus(false);
			}
		}
	}
	if (gc.getInput().isKeyPressed(Input.KEY_F3)) {
		Globals.set("debug", ""+!Globals.get("debug", false));
	}
	if (gc.getInput().isKeyPressed(Input.KEY_F4)) {
		Globals.set("debugInfo", ""+!Globals.get("debugInfo", false));
	}
	if (net.sekien.elesmyr.system.Input.isKeyPressed(gc, "inv") && showTextField==false) {
		if (ui.peekFirst() instanceof InventoryUI) {
			ui.removeFirst();
		} else if (!ui.peekFirst().blockUpdates()) {
			InventoryUI inv = new InventoryUI();
			inv.init(gc, this);
			ui.addFirst(inv);
		}
	}
	if (MessageSystem.CLIENT) {
		MessageSystem.receiveMessageClient();

		if (!MessageSystem.clientConnected())
			error = "Lost connection to server.";

		if (error!=null) {
			gc.getInput().clearKeyPressedRecord();
			MessageSystem.close();
			StateManager.error(error, true);
		}
		boolean blocked = false;
		if (ui.peekFirst().blockUpdates()) {
			ui.peekFirst().update(gc, this);
			blocked = true;
			gc.getInput().clearKeyPressedRecord();
		}
		for (UserInterface uii : ui) {
			if (!blocked || !uii.blockUpdates())
				uii.update(gc, this);
		}
		for (ChatMessage cm : chat)
			cm.update();
		world.clientUpdate(gc, this);
		if (!blocked)
			player.clientUpdate(gc, this);
		cam.update(player);
		lm.update(player.region, cam, time);
		time += (servtime-time)/120;
	}
	if (MessageSystem.SERVER) {
		server.gameUpdate();
	}
}

public boolean receiveMessage(Message msg) {
	if (msg==null) {
		return true;
	} else if (msg.getTarget().equals("CLIENT")) {
		String name = msg.getName();
		if (name.equals("setRegion")) {
			lm.clearLight();
			String rname = msg.getData().getString("region", "error");
			world.touchRegionClient(rname);
			//world.getRegion(rname).parseEntityString(msg.getDataStr().split(":", 2)[1], true);
			lm.addLight(world.getRegion(rname).getLights());
			player.setRegion(rname);
			regionLoaded = true;
			StateManager.stopLoading();
		} else if (name.equals("chat")) {
			chat.addFirst(new ChatMessage(msg.getData().getString("msg", "ERROR: Badly formatted chat message")));
			//if (chat.size()>5) {
			//	chat.removeLast();
			//}
		} else if (name.equals("talk")) {
			if (ui.peekFirst() instanceof net.sekien.elesmyr.ui.ChatUI) {
				((ChatUI) ui.peekFirst()).setMsg(msg.getDataStr().split(":", 2)[0], msg.getDataStr().split(":", 2)[1], msg);
			} else {
				ChatUI nui = new ChatUI();
				nui.setMsg(msg.getDataStr().split(":", 2)[0], msg.getDataStr().split(":", 2)[1], msg);
				ui.addFirst(nui);
			}
		} else if (name.equals("echointwl")) {
			msg.reply(msg.getSender()+".tresponse", FontRenderer.getLang()+"|int", null);
		} else if (name.equals("error")) {
			error = msg.getData().getString("msg", "Error had badly formatted msg\n"+msg.getData());
		} else if (name.equals("time")) {
			float oltime = servtime;
			servtime = msg.getData().getFloat("time", 0);
			date = msg.getData().getInt("date", 0);
			if (servtime > oltime+10 || servtime < oltime-10) {
				Log.info("Time skip");
				lm.skipFade(servtime/60f, player.region);
				time = servtime;
			}
		} else if (name.equals("openUI")) {
			UserInterface nui = UIFactory.getUI(msg.getData().getString("ui", "error"));
			if (nui!=null)
				ui.addFirst(nui);
			else
				Log.warn("CLIENT: Could not open UI "+msg.getData().getString("ui", "error"));
		} else if (name.equals("book")) {
			if (ui.peekFirst() instanceof BookUI) {
				((BookUI) ui.peekFirst()).addPage(msg.getData().getString("page", "ERROR: Badly formed message."));
			} else {
				BookUI nui = new BookUI();
				nui.ctor(null);
				nui.addPage(msg.getData().getString("page", "ERROR: Badly formed message."));
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

public void loadSave(GameContainer gc, String saveName, boolean serverOnly) throws Exception {
	Save save = new Save(saveName);
	if (serverOnly) {
		MessageSystem.SERVER = true;
		MessageSystem.CLIENT = false;
	} else {
		MessageSystem.SERVER = true;
		MessageSystem.CLIENT = true;
	}
	MessageSystem.initialise(MessageSystem.CLIENT?this:null, MessageSystem.SERVER, InetAddress.getLocalHost(), save);
	this.server = MessageSystem.server;
	if (MessageSystem.SERVER)
		server.load();
	if (MessageSystem.CLIENT) {
		HBTCompound tag = new HBTCompound("p");
		tag.addTag(new HBTString("name", Globals.get("name", "Player")));
		tag.addTag(new HBTInt("pass", "".hashCode()));
		MessageSystem.sendServer(null, new Message("SERVER.wantPlayer", tag), false);
	}
}

public PlayerClient getPlayer() {
	return player;
}

public void join(InetAddress hostaddr) throws Exception {
	MessageSystem.SERVER = false;
	MessageSystem.CLIENT = true;
	MessageSystem.initialise(this, false, hostaddr, null);
}

public void login(String name, int pass) {
	HBTCompound tag = new HBTCompound("p");
	tag.addTag(new HBTString("name", name));
	tag.addTag(new HBTInt("pass", pass));
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