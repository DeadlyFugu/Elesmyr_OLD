package net.halitesoft.lote.system;


import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.LinkedList;

import net.halitesoft.lote.Message;
import net.halitesoft.lote.MessageReceiver;
import net.halitesoft.lote.MessageSystem;
import net.halitesoft.lote.Save;
import net.halitesoft.lote.ScriptObject;
import net.halitesoft.lote.ui.CraftUI;
import net.halitesoft.lote.ui.HUDUI;
import net.halitesoft.lote.ui.InventoryUI;
import net.halitesoft.lote.ui.UIFactory;
import net.halitesoft.lote.ui.UserInterface;
import net.halitesoft.lote.world.Region;
import net.halitesoft.lote.world.World;
import net.halitesoft.lote.world.entity.Entity;
import net.halitesoft.lote.world.item.ItemFactory;

import org.lwjgl.opengl.EXTBlendMinmax;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

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

	int stateID = -1;
	private GameServer server;
	public Client client;
	//private ConcurrentLinkedQueue<Message> msgList;
	boolean regionLoaded = false;

	public PlayerClient player; //Client player controller.

	public static boolean CLIENT;
	public static boolean SERVER;

	/**
	 * Client-side world
	 */
	private World world;
	Camera cam;
	public LightMap lm;
	
	private float time=-20; //in-game time in minutes
	private float servtime=-20; //time according to server
	
	private LinkedList<ChatMessage> chat;
	private TextField textField;
	private boolean showTextField;
	
	private String error = null;
	
	private ScriptObject chatso;
	
	private LinkedList<UserInterface> ui;
	
	private Image vignette;
	private Image alphabg;

	GameClient(int stateID) {
		this.stateID = stateID;
	}

	@Override
	public int getID() {return stateID;}

	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		ItemFactory.init();
		regionLoaded = false;
		error = null;
		chatso = new ScriptObject("Console",this);
		time=-20;
		servtime=-20;
		//msgList = new ConcurrentLinkedQueue<Message>();
		chat = new LinkedList<ChatMessage>();
		ui = new LinkedList<UserInterface>();
		HUDUI hud = new HUDUI();
		hud.init(gc,sbg,this);
		ui.addFirst(hud);
		world = new World();
		player = new PlayerClient(this,world);
		player.init(gc, sbg,this);
		cam = new Camera(10,10);
		lm = new LightMap(true,(int) (MainMenuState.lres*Main.INTERNAL_ASPECT),MainMenuState.lres);
		vignette = new Image("data/ui/vignette.png",false,1);
		alphabg = new Image("data/ui/alphabg.png",false,0);
		System.gc();

		textField = new TextField(gc, Main.font, 10,Main.INTERNAL_RESY-84,530,16);
		textField.setBorderColor(null);
		textField.setBackgroundColor(new Color(0,0,0,0.2f));
		textField.setTextColor(Color.white);
		textField.setAcceptingInput(false);
		textField.setMaxLength(57);
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		//g.scale(gc.getWidth()/((float) (Main.INTERNAL_RESY)*((float) gc.getWidth()/(float) gc.getHeight())),gc.getHeight()/(float) (Main.INTERNAL_RESY));
		g.scale(gc.getWidth()/(float) (Main.INTERNAL_RESX),gc.getHeight()/(float) (Main.INTERNAL_RESY));
		if (!regionLoaded) {
			//g.setColor(Color.black);
			//g.fillRect(0,0,Main.INTERNAL_RESX,Main.INTERNAL_RESY);
			//g.setColor(Color.white);
		} else if (CLIENT) {
			g.pushTransform();
			renderMap(player.region,false);
			world.render(gc, sbg, g, cam,this);
			player.render(gc,sbg,g,cam,this);
			renderMap(player.region,true);
			if (Globals.get("debug",false) && Globals.get("showEnt",true)) { //Show ent IDs.
				for (Entity e : player.region.entities.values()) //TODO: Somehow this line throws an NPE occasionally O.o
					Main.font.drawString(e.x+cam.getXOff(),e.y+cam.getYOff(),e.name);
			}
			g.popTransform();
			g.pushTransform();
			g.scale((float) Main.INTERNAL_RESX/(lm.resx-2), (float) Main.INTERNAL_RESY/(lm.resy-2));
			g.fillRect(0,0,0,0);
			g.translate(((cam.getXOff()/(float) lm.gw)%1),((cam.getYOff()/(float) lm.gh)%1));
			lm.render();
			g.setDrawMode(Graphics.MODE_NORMAL);
			g.popTransform();
			int i = 1;
			for (ChatMessage cm : chat) {
				if (i==22)
					break;
				g.setColor(new Color(0,0,0,(showTextField?0.25f:cm.getAlpha()*0.25f)));
				g.fillRect(8,Main.INTERNAL_RESY-89-i*18, Main.font.getWidth(cm.getMessage())+4, 18);
				g.setColor(Color.white);
				if (showTextField)
					Main.font.drawString(10,Main.INTERNAL_RESY-89-i*18, cm.getMessage());
				else if (cm.getAlpha()!=0)
					Main.font.drawString(10,Main.INTERNAL_RESY-89-i*18, cm.getMessage(), new Color(1,1,1,cm.getAlpha()));
				i++;
			}
			if (ui.peekFirst().blockUpdates()) {
				alphabg.draw(0,0,Main.INTERNAL_RESX,Main.INTERNAL_RESY);
				vignette.draw(0,0,Main.INTERNAL_RESX,Main.INTERNAL_RESY);
			}
			//System.out.println(ui);
			Iterator<UserInterface> itui = ui.descendingIterator();
			while (itui.hasNext()) {
				itui.next().render(gc, sbg, g, cam, this);
			}

			if (showTextField) {
				textField.render(gc, g);
				textField.setFocus(true);
			}
		} else {
			g.setColor(Color.black);
			g.fillRect(0,0,Main.INTERNAL_RESX,Main.INTERNAL_RESY);
			g.setColor(Color.white);
		}
		if (!CLIENT)
			Main.font.drawString(0, 0, "LotE "+Main.version+":"+(CLIENT?" CLIENT":"")+(SERVER?" SERVER":"")+(MessageSystem.fastLink?" FASTLINK":""));
		if (SERVER && (!CLIENT || (Globals.get("debug",false) && Globals.get("showConnections",true)))) {
			if (!CLIENT) {
				g.setColor(Color.black);
				g.fillRect(0,0,Main.INTERNAL_RESX,Main.INTERNAL_RESY);
				g.setColor(Color.white);
			}
			server.render(gc, sbg, g, CLIENT);
		}
		if (CLIENT && Globals.get("debug",false) && Globals.get("debugInfo",true)) {
			//Client debug mode
			String debugText = 
					"Pos: X="+player.x+" Y="+player.y+"\n" +
					"Time raw: "+time+"\n" +
					"Time norm: "+(int) ((time/60)%12)+":"+(int) (time%60)+"\n" +
					"FPS: "+gc.getFPS();
			/*try {
				debugText = debugText+"Inventory:\n"+((EntityPlayer) player.region.entities.get(player.entid)).pdat.invToString();
			} catch (Exception e) {
			}*/
			int i = 1;
			for (String s : debugText.split("\n")) {
				Main.font.drawString(Main.INTERNAL_RESX-Main.font.getWidth(s)-10, i*18, s);
				i++;
			}
		}
	}

	private void renderMap(Region region, boolean fg) {
		if (region==null || region.map==null)
			return;
		for (int i=0;i<region.map.getLayerCount();i++) {
			if (region.map.getLayerIndex("col")!=i) {
				//if (fg==Boolean.parseBoolean(region.map.getLayerProperty(i, "fg", "false")));
				if (fg==(region.map.getLayerIndex("fg")==i))
					region.map.render(cam.getXOff(),cam.getYOff(),i);
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
				if (CLIENT) {
					if (!SERVER)
						MessageSystem.sendServer(null,new Message("SERVER.close",""),false);
					client.close();
				}
				if (SERVER) {
					server.save();
					server.broadcastKill();
					server.close();
				}
				//gc.exit();
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
							&& textField.getText().contains(":")
							&& textField.getText().split(":",2)[0].contains(".")) {
						MessageSystem.sendServer(null,new Message(textField.getText().split(":",2)[0].substring(1),textField.getText().split(":",2)[1]),false);
					} else if (textField.getText().startsWith("/")) {
						//chatInitVar=ScriptRunner.run(textField.getText().substring(1), "ccmd", this,chatInitVar+" CLIENT=true SERVER=false").substring(1);
						chatso.call("call",new Object[] {textField.getText().substring(1),this});
					} else
						MessageSystem.sendServer(null,new Message("SERVER.chat",textField.getText()),true);
					showTextField=false;
					textField.setText("");
					textField.setAcceptingInput(false);
					textField.setFocus(false);
				}
			}
		}
		if (gc.getInput().isKeyPressed(Input.KEY_F3)) {
			Globals.set("debug",""+!Globals.get("debug",false));
		}
		if (gc.getInput().isKeyPressed(Input.KEY_E) && showTextField==false) {
			if (ui.peekFirst() instanceof InventoryUI) {
				ui.removeFirst();
			} else if (!ui.peekFirst().blockUpdates()) {
				InventoryUI inv = new InventoryUI();
				inv.init(gc, sbg, this);
				ui.addFirst(inv);
			}
		}
		if (SERVER) {
			server.gameUpdate();
		}
		if (CLIENT) {
			MessageSystem.receiveMessageClient();
			
			if (!client.isConnected())
				error="Lost connection to server.";
			
			if (error!=null) {
				gc.getInput().clearKeyPressedRecord();
				client.close();
				if (SERVER) {
					server.save();
					server.close();
				}
				((ErrorState) sbg.getState(Main.ERRORSTATE)).errorText = error;
				sbg.enterState(Main.ERRORSTATE);
			}
			boolean blocked = false;
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
			world.clientUpdate(gc, sbg,this);
			if (!blocked)
				player.clientUpdate(gc, sbg,this);
			cam.update(player);
			lm.update(player.region,cam,time);
			time+=(servtime-time)/120;
		}
	}

	public boolean receiveMessage(Message msg) {
		if (msg==null) {
			return true;
		} else if (msg.getTarget().equals("CLIENT")) {
			String name = msg.getName();
			if (name.equals("setRegion")) {
				lm.clearLight();
				String rname = msg.getData().split(":",2)[0];
				world.touchRegionClient(rname);
				world.getRegion(rname).parseEntityString(msg.getData().split(":",2)[1],true);
				lm.addLight(world.getRegion(rname).getLights());
				player.setRegion(rname);
				regionLoaded = true;
			} else if (name.equals("chat")) {
				chat.addFirst(new ChatMessage(msg.getData()));
				//if (chat.size()>5) {
				//	chat.removeLast();
				//}
			} else if (name.equals("error")) {
				error = msg.getData();
			} else if (name.equals("time")) {
				float oltime = servtime;
				servtime = Float.parseFloat(msg.getData());
				if (servtime>oltime+10 || servtime<oltime-10) {
					Log.info("Time skip");
					lm.skipFade(servtime/60f);
					time=servtime;
				}
			} else if (name.equals("openUI")) {
				UserInterface nui = UIFactory.getUI(msg.getData());
				if (nui!=null)
					ui.addFirst(nui);
				else
					Log.warn("CLIENT: Could not open UI "+msg.getData());
			} else {
				Log.warn("CLIENT: Ignored message - unrecognised name: "+msg.toString());
			}
		} else if (msg.getTarget().equals("PLAYER")) {
			player.receiveMessage(msg,this);
		} else {
			world.receiveMessage(msg,this);
		}
		return false;
	}

	public void loadSave(GameContainer gc, String saveName, boolean serverOnly, StateBasedGame sbg) throws Exception {
		Save save = new Save(saveName);
		if (serverOnly) {
			SERVER = true;
			CLIENT = false;
		} else {
			SERVER = true;
			CLIENT = true;
		}
		try {
			if (SERVER) {
				server = new GameServer(save,CLIENT?Globals.get("name","Player"):"");
				server.start();
				try {
				server.bind(37020,37021);
				} catch (java.net.BindException be) { //For some reason, I can't directly throw a BindException.
					throw new Exception();
				}
				server.getKryo().register(Message.class);
			}
			if (CLIENT) {
				client = new Client(8192,4096);
				client.start();
				client.connect(5000, "localhost", 37020,37021);
				client.getKryo().register(Message.class);
				client.addListener(new ClientListener());
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			gc.exit();
		}
		MessageSystem.initialise(CLIENT?this:null, SERVER?server:null);
		if (SERVER)
			server.load();
		if (CLIENT) {
			MessageSystem.sendServer(null,new Message("SERVER.wantPlayer",Globals.get("name","Player")+","+Integer.toHexString("".hashCode())),false);
		}
	}

	private class ClientListener extends Listener {

		public void received (Connection connection, Object object) {
			//if (! (object instanceof Message) || !((Message) object).getName().equals("move"))
			//	Log.info("Client received: "+object.toString());
			if (object instanceof Message) {
				((Message) object).addConnection(connection);
				MessageSystem.receiveClient((Message) object);
			} else if (!(object instanceof com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive)) {
				Log.warn("CLIENT: Ignored message, unrecognised type "+object.getClass().getName()+", toString: "+object.toString());
			}
		}
	}

	public PlayerClient getPlayer() {
		return player;
	}
	
	public void join(InetAddress hostaddr) throws java.io.IOException {
		SERVER = false;
		CLIENT = true;
		client = new Client(8192,4096);
		client.start();
		client.connect(5000,hostaddr, 37020, 37021);
		client.getKryo().register(Message.class);
		client.addListener(new ClientListener());
		MessageSystem.initialise(this, null);
	}

	public void sendMessage(String name, String data) {
		MessageSystem.sendServer(null,new Message(name,data),false);
	}

	public void login(String name, String pass) {
		MessageSystem.sendServer(null,new Message("SERVER.wantPlayer",name+","+pass),false);
	}

	public GameServer getServer() {
		return this.server;
	}

	@Override
	public boolean isServer() {
		return false;
	}
}