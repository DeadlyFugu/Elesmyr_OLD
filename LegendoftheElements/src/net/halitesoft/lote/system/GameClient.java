package net.halitesoft.lote.system;


import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;

import net.halitesoft.lote.Message;
import net.halitesoft.lote.MessageReceiver;
import net.halitesoft.lote.MessageSystem;
import net.halitesoft.lote.Save;
import net.halitesoft.lote.ScriptObject;
import net.halitesoft.lote.ui.HUDUI;
import net.halitesoft.lote.ui.InventoryUI;
import net.halitesoft.lote.ui.UserInterface;
import net.halitesoft.lote.world.Region;
import net.halitesoft.lote.world.World;
import net.halitesoft.lote.world.item.ItemFactory;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
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
	
	private float time=-1; //in-game time in minutes
	private float servtime=-1; //time according to server
	
	private LinkedList<String> chat;
	private TextField textField;
	private boolean showTextField;
	
	private String error = null;
	
	private ScriptObject chatso;
	
	private LinkedList<UserInterface> ui;

	GameClient(int stateID) {
		this.stateID = stateID;
	}

	@Override
	public int getID() {return stateID;}

	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		ItemFactory.init();
		regionLoaded = false;
		error = null;
		chatso = new ScriptObject("ccmd","NAME="+Main.globals.get("name"),this);
		time=-1;
		servtime=-1;
		//msgList = new ConcurrentLinkedQueue<Message>();
		chat = new LinkedList<String>();
		ui = new LinkedList<UserInterface>();
		HUDUI hud = new HUDUI();
		hud.init(gc,sbg,this);
		ui.addFirst(hud);
		world = new World();
		player = new PlayerClient(this,world);
		player.init(gc, sbg,this);
		cam = new Camera(10,10);
		lm = new LightMap(true,(int) (MainMenuState.lres*Main.INTERNAL_ASPECT),MainMenuState.lres);
		System.gc();

		textField = new TextField(gc, Main.font, 10,Main.INTERNAL_RESY-20,530,16);
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
			g.popTransform();
			g.pushTransform();
			g.scale((float) Main.INTERNAL_RESX/(lm.resx-2), (float) Main.INTERNAL_RESY/(lm.resy-2));
			g.fillRect(0,0,0,0);
			g.translate(((cam.getXOff()/(float) lm.gw)%1),((cam.getYOff()/(float) lm.gh)%1));
			lm.render();
			g.setDrawMode(Graphics.MODE_NORMAL);
			g.popTransform();
			int i = 1;
			for (String s : ((LinkedList<String>) chat.clone())) {
				Main.font.drawString(10,Main.INTERNAL_RESY-25-i*18, s);
				i++;
			}
			for (UserInterface uii : ui) {
				uii.render(gc, sbg, g, cam, this);
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
		Main.font.drawString(0, 0, "LotE "+Main.version+":"+(CLIENT?" CLIENT":"")+(SERVER?" SERVER":"")+(MessageSystem.fastLink?" FASTLINK":""));
		if (SERVER && (!CLIENT || Boolean.parseBoolean(Main.globals.get("debug")))) {
			if (!CLIENT) {
				g.setColor(Color.black);
				g.fillRect(0,0,Main.INTERNAL_RESX,Main.INTERNAL_RESY);
				g.setColor(Color.white);
			}
			server.render(gc, sbg, g, CLIENT);
		}
		if (CLIENT && Boolean.parseBoolean(Main.globals.get("debug"))) {
			//Client debug mode
			String debugText = "DEBUG: \n" +
					"Pos: X="+player.x+" Y="+player.y+"\n" +
					"Time raw: "+time+"\n" +
					"Time norm: "+(int) ((time/60)%12)+":"+(int) (time%60)+"\n" +
					"FPS: "+gc.getFPS();
			/*try {
				debugText = debugText+"Inventory:\n"+((EntityPlayer) player.region.entities.get(player.entid)).pdat.invToString();
			} catch (Exception e) {
			}*/
			int i = 0;
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
					region.map.render(cam.getXOff(), cam.getYOff(), i);
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
						client.sendTCP(new Message("SERVER.close",""));
					client.close();
				}
				if (SERVER) {
					server.save();
					server.close();
				}
				//gc.exit();
				gc.getInput().clearKeyPressedRecord();
				sbg.enterState(Main.MENUSTATE);
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
						client.sendTCP(new Message(textField.getText().split(":",2)[0].substring(1),textField.getText().split(":",2)[1]));
					} else if (textField.getText().startsWith("/def ")) {
						if (textField.getText().contains("="))
							chatso.putVariable(textField.getText().substring(5).trim());
					} else if (textField.getText().startsWith("/")) {
						//chatInitVar=ScriptRunner.run(textField.getText().substring(1), "ccmd", this,chatInitVar+" CLIENT=true SERVER=false").substring(1);
						chatso.putVariable("PLAYERX="+player.x);
						chatso.putVariable("PLAYERY="+player.y);
						chatso.putVariable("PLAYERREGION="+player.region.name);
						chatso.putVariable("PLAYERENT="+player.region.name+"."+player.entid);
						chatso.call(textField.getText().substring(1), true, "", this);
					} else
						MessageSystem.sendServer(null,new Message("SERVER.chat",textField.getText()),true);
					showTextField=false;
					textField.setText("");
					textField.setAcceptingInput(false);
					textField.setFocus(false);
				}
			}
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
			}
			for (UserInterface uii : ui) {
				if (!blocked||!uii.blockUpdates())
					uii.update(gc, sbg, this);
			}
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
				chat.addFirst(msg.getData());
				if (chat.size()>5) {
					chat.removeLast();
				}
			} else if (name.equals("error")) {
				error = msg.getData();
			} else if (name.equals("time")) {
				float oltime = servtime;
				servtime = Float.parseFloat(msg.getData());
				if (servtime>oltime+5 || servtime<oltime-5) {
					lm.skipFade(servtime/60f);
					time=servtime;
					System.out.println("time skip");
				}
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

	public void loadSave(GameContainer gc, String saveName, boolean serverOnly) {
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
				server = new GameServer(save,CLIENT?Main.globals.get("name"):"");
				server.start();
				server.bind(37020,37021);
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
			client.sendTCP(new Message("SERVER.wantPlayer",Main.globals.get("name")+","+Integer.toHexString("".hashCode())));
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
		client.sendTCP(new Message(name,data));
	}

	public void login(String name, String pass) {
		client.sendTCP(new Message("SERVER.wantPlayer",name+","+pass));
	}

	public GameServer getServer() {
		return this.server;
	}

	@Override
	public boolean isServer() {
		return false;
	}
}