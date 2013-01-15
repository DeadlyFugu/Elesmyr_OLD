package net.halitesoft.lote.system;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import net.halitesoft.lote.util.HashmapLoader;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.SpriteSheetFont;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.minlog.Log;

public class MainMenuState extends BasicGameState {

	int stateID = -1;

	Image background = null;
	Image bg2 = null;
	Image bg3 = null;

	private String[] levels = null;
	private String[] showList;

	private boolean debug;

	static int subMenu = 0; //0=main 1=play 2=options 3=join 4=controls
	static int[] entryCount = {5,2,8,2,8};
	static String[][] entryString = {{"Single Player","Join","Server","Settings","Exit"},
		{"Back","New Game"},
		{"Video: 480p","Name: Player","VSync: false","Volume: 10","Debug: false","Lightmap Res: 24p","Controls...","Done"},
		{"Back","Enter IP"},
		{"Input method: Keyboard/Mouse","Walk: Arrow keys","Interact: Z","Attack: X","Inventory: E","Select: Enter","Back/Pause: Esc","Back"}};
	static String[][] entryDesc = {{"Play single player.","Join a multiplayer game.","Run server-only.","Configure the game.","Close the window."},
		{"Return to the menu."},
		{"Change the resolution.","Change your name","Toggle VSync.","Change the volume.","Toggle debug mode.",
			"Change lightmap resolution.","Configure the controls","Apply changes and return to the menu."},
		{"Return to the menu."}};
	static int selection = 0;
	private static int dm = 0;
	public static int[] disx = {(int) (480*Main.INTERNAL_ASPECT),(int) (720*Main.INTERNAL_ASPECT),(int) (960*Main.INTERNAL_ASPECT),0,(int) (480*Main.INTERNAL_ASPECT)};
	public static int[] disy = {480,720,960,0,480};
	private static boolean fullscreen;
	public static int lres = 24;
	private static Image[] button = new Image[2];
	private TextField textField;
	private boolean showTextField;
	private ArrayList<InetAddress> lanServers;

	private boolean serverOnly = false;

	private int dx = -256;
	private int tdx = 64;



	MainMenuState( int stateID ) 
	{
		this.stateID = stateID;
	}

	@Override
	public int getID() {
		return stateID;
	}

	/**
	 * This method will retrieve all the -sub folders- (Changed to .txt files) from the current directory
	 * that was passed in. Taken from http://www.dreamincode.net/code/snippet4167.htm
	 * @param cur - Current directory that the user is in
	 * @return A list of folders
	 */
	private ArrayList<File> getSubs(File cur) {

		//Get a list of all the files in folder
		ArrayList<File> temp = new ArrayList<File>();
		File[] fileList = cur.listFiles();

		//for each file in the folder
		for (int i = 0; i < fileList.length; i++) {

			//If the file is a Directory(folder) add it to return, if not done so already
			File choose = fileList[i];
			if ( choose.isDirectory() && !temp.contains(choose) && (!choose.getName().equals("new") || debug)) {
				temp.add(choose);
			}
		}
		return temp;
	}

	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		background = new Image("data/menu/bg.png");
		bg2 = new Image("data/menu/bg2.png",false,0);
		bg3 = new Image("data/menu/bg3.png",false,0);

		ArrayList<File> files = getSubs(new File("save"));
		levels = new String[files.size()+2];
		entryCount[1]=files.size()+2;
		levels[0] = "Back";
		levels[1] = "New Game";
		int i=2;
		for (File f:files) {
			levels[i] = f.getName();
			i++;
		}

		if (Main.globals.containsKey("resdm"))
			dm = Integer.parseInt(Main.globals.get("resdm"));
		if (Main.globals.containsKey("lres"))
			lres = Integer.parseInt(Main.globals.get("lres"));
		debug = Boolean.parseBoolean(Main.globals.get("debug"));

		Main.font = new SpriteSheetFont(new SpriteSheet(new Image("data/font.png",false,0),9,16),' ');

		Image temp = new Image("data/menu/button.png",false,0);
		button[0] = temp.getSubImage(0, 1, 256, 32);
		button[1] = temp.getSubImage(0, 33, 256, 32);
		//temp.destroy();
		//disx[3]=gc.getScreenWidth();
		//disy[3]=gc.getScreenHeight();

		if (dm!=3&&dm!=4)
			entryString[2][0] = "Video: "+disy[dm]+"p windowed";
		else
			entryString[2][0] = "Video: "+disy[dm]+"p fullscreen";
		entryString[2][1] = "Name: "+Main.globals.get("name");
		entryString[2][2] = "VSync: "+Main.globals.get("vsync");
		if (Integer.parseInt(Main.globals.get("volume")) == 0)
			entryString[2][3] = "Volume: Mute";
		else
			entryString[2][3] = "Volume: "+Main.globals.get("volume");
		entryString[2][4] = "Debug: "+debug;
		entryString[2][5] = "Lightmap Res: "+lres+"p";

		textField = new TextField(gc, Main.font, tdx,128+19,256,16);
		textField.setBorderColor(null);
		textField.setBackgroundColor(null);
		textField.setTextColor(Color.white);
		textField.setAcceptingInput(false);
		textField.setMaxLength(16);
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.setColor(Color.black);
		float vw = gc.getWidth();
		float vh = gc.getHeight();
		float ox,oy,w,h;
		/*if (vw/vh < 1.6) { //Old aspect-ratio correction
			w = vw;
			h = vw*0.625f;
			ox = 0;
			oy = (vh-h)/2;
			g.fillRect(0, 0, w, oy);
			g.fillRect(0, oy+h, w, oy);
		} else {*/
		w = vh*(16/9f);
		h = vh;
		ox = (vw-w)/2;
		oy = 0;
		g.fillRect(0, 0, ox, h);
		g.fillRect(ox+w, 0, ox, h);
		//}*/

		g.setColor(Color.white);
		background.draw(ox,oy,w,h);
		bg2.draw(0,0,vh*(4/3f),h);
		bg3.draw(vw-vh*(4/3f),0,vh*(4/3f),h);
		//g.translate(ox, oy);
		g.scale(vw/Main.INTERNAL_RESX,vh/Main.INTERNAL_RESY);
		dx += (tdx-dx)/20;
		int menuSpace = 19;
		if (subMenu == 3) { //Join
			for (int i=0; i<showList.length;i++) {
				int buttonState = 0;
				if (i==selection) buttonState = 1;
				int size = (int) (Main.font.getWidth(showList[i]))+16;
				button[buttonState].draw(dx+64-size/2,(float) (128+(i-Math.max(selection-12,0))*menuSpace),size,16);
				Main.font.drawString(dx+64-(Main.font.getWidth(showList[i])/2), (float) (128+(i-Math.max(selection-12,0))*menuSpace), showList[i]);
			}
		} else if (subMenu != 1) { //normal
			for (int i=0; i<entryCount[subMenu];i++) {
				int buttonState = 0;
				if (i==selection) buttonState = 1;
				int size = (int) (Main.font.getWidth(entryString[subMenu][i]))+16;
				button[buttonState].draw(dx+64-size/2,(float) (128+(i-Math.max(selection-12,0))*menuSpace),size,16);
				Main.font.drawString(dx+64-(Main.font.getWidth(entryString[subMenu][i])/2), (float) (128+(i-Math.max(selection-12,0))*menuSpace), entryString[subMenu][i]);
			}
		} else { //Levels
			for (int i=0; i<levels.length;i++) {
				int buttonState = 0;
				if (i==selection) buttonState = 1;
				int size = (int) (Main.font.getWidth(levels[i]))+16;
				button[buttonState].draw(dx+64-size/2,(float) (128+(i-Math.max(selection-12,0))*menuSpace),size,16);
				Main.font.drawString(dx+64-(Main.font.getWidth(levels[i])/2), (float) (128+(i-Math.max(selection-12,0))*menuSpace), levels[i]);
			}
		}
		if (showTextField) {
			textField.setLocation(dx+64-(Main.font.getWidth(textField.getText())/2), textField.getY());
			textField.render(gc, g);
			textField.setFocus(true);
		}
		Main.font.drawString(Main.INTERNAL_RESX-(Main.font.getWidth(getDescription(subMenu,selection))+128),380,getDescription(subMenu,selection));
		Main.font.drawString(0, 0, "LotE "+Main.version);
		g.scale(2, 2);
		Main.font.drawString((dx+64-(Main.font.getWidth(menuName(subMenu))))/2,56/2,menuName(subMenu));
	}

	private String menuName(int menu) {
		switch (menu) {
		case 0: return "MENU";
		case 1: if (serverOnly) { return "SERVER"; } else { return "PLAY"; }
		case 2: return "SETTINGS";
		case 3: return "JOIN";
		case 4: return "CONTROLS";
		default: return "NULL";
		}
	}

	private String getDescription(int sub, int sel) {
		try {
			return entryDesc[sub][sel];
		} catch (Exception e) {
			if (sub == 1) { //levels
				if (serverOnly) {
					if (sel==1)
						if (showTextField) {
							return "Enter name for the new save.";
						} else {
							return "Host game with a new save.";
						}
					return "Host game with save "+levels[sel]+".";
				} else {
					if (sel==1)
						if (showTextField) {
							return "Enter name for the new save.";
						} else {
							return "Play game with a new save.";
						}
					return "Play game with save "+levels[sel]+".";
				}
			} else if (sub == 3) { //join
				if (sel==1)
					if (showTextField) {
						return "Enter remote server's IP.";
					} else {
						return "Connect to a remote server.";
					}
				return "Join "+showList[sel]+".";
			}
		}
		return "NO DESC";
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {

		Input input = gc.getInput();
		if (input.isKeyPressed(Input.KEY_DOWN) && !showTextField) {
			if (selection<entryCount[subMenu]-1) {
				selection++;
			}
		}
		if (input.isKeyPressed(Input.KEY_UP) && !showTextField) {
			if (selection>0) {
				selection--;
			}
		}
		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			if (showTextField) {
				showTextField=false;
				textField.setText("");
				textField.setAcceptingInput(false);
				textField.setFocus(false);
				if (subMenu==1)
					levels[1] = "New Game";
				else if (subMenu==3)
					showList[1] = "Enter IP";
			} else if (subMenu != 0){
				selection = 0;
				subMenu = 0;
				dx = -256;
			} else {
				gc.exit();
			}
		}
		if (input.isKeyPressed(Input.KEY_ENTER)){
			switch ((subMenu*100)+selection) {
			case 000: { //Singleplayer
				selection = 0;
				subMenu = 1;
				dx=-256;
				serverOnly=false;
			} break;
			case 001: { //Join
				selection = 0;
				subMenu = 3;
				dx=-256;
				try {
					Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
					while (nis.hasMoreElements()) {
						Log.info(((NetworkInterface) nis.nextElement()).toString());
					}
				} catch (SocketException e) {
					e.printStackTrace();
				}
				lanServers = (ArrayList<InetAddress>) discoverHosts(37021, 1000);
				showList = new String[lanServers.size()+2];
				showList[0]=entryString[3][0];
				showList[1]=entryString[3][1];
				for (int i = 2; i< showList.length; i++) {
					showList[i]=lanServers.get(i-2).toString();
				}
				entryCount[3] = showList.length;
			} break;
			case 002: { //Server
				selection = 0;
				subMenu = 1;
				dx=-256;
				serverOnly = true;
			} break;
			case 003: {
				selection = 0;
				subMenu = 2;
				dx=-256;
			} break;
			case 004: {
				gc.exit();
			} break;
			case 200: {
				dm++;
				if (dm==5) dm=0;
				fullscreen = (dm==3 || dm==4);
				//entryString[2][0] = "Resolution: "+disx[dm]+"x"+disy[dm];
				if (!fullscreen)
					entryString[2][0] = "Video: "+disy[dm]+"p windowed";
				else
					entryString[2][0] = "Video: "+disy[dm]+"p fullscreen";
				Main.globals.put("resdm", String.valueOf(dm));
			} break;
			case 201: {
				//Enter name
				if (!showTextField) {
					entryString[2][1] = "";
					showTextField=true;
					textField.setText("");
					textField.setAcceptingInput(true);
					textField.setFocus(true);
				} else {
					entryString[2][1] = "Name: "+textField.getText();
					Main.globals.put("name",textField.getText());
					showTextField=false;
					textField.setText("");
					textField.setAcceptingInput(false);
					textField.setFocus(false);
				}
			} break;
			case 202: {
				boolean vsync = Boolean.parseBoolean(Main.globals.get("vsync"));
				//entryString[2][0] = "Resolution: "+disx[dm]+"x"+disy[dm];
				entryString[2][2] = "VSync: "+!vsync;
				Main.globals.put("vsync", String.valueOf(!vsync));
			} break;
			case 203: {
				int vol = Integer.parseInt(Main.globals.get("volume"));
				vol++;
				if (vol>10) {
					vol = 0;
				}
				//entryString[2][0] = "Resolution: "+disx[dm]+"x"+disy[dm];
				if (vol == 0)
					entryString[2][3] = "Volume: Mute";
				else
					entryString[2][3] = "Volume: "+vol;
				Main.globals.put("volume", String.valueOf(vol));
			} break;
			case 204: {
				debug = Boolean.parseBoolean(Main.globals.get("debug"));
				//entryString[2][0] = "Resolution: "+disx[dm]+"x"+disy[dm];
				debug = !debug;
				entryString[2][4] = "Debug: "+debug;
				Main.globals.put("debug", String.valueOf(debug));
				ArrayList<File> files = getSubs(new File("save"));
				levels = new String[files.size()+2];
				entryCount[1]=files.size()+2;
				levels[0] = "Back";
				levels[1] = "New Game";
				int i=2;
				for (File f:files) {
					levels[i] = f.getName();
					i++;
				}
			} break;
			case 205: {
				if (lres>=24)
					lres+=12;
				else
					lres+=6;
				if (lres>48) lres=6;
				entryString[2][5] = "Lightmap Res: "+lres+"p";
				Main.globals.put("lres", String.valueOf(lres));
			} break;
			case 206: {
				selection = 0;
				subMenu = 4;
				dx=-256;
			} break;
			case 207: {
				if (((AppGameContainer) gc).getHeight()!=disy[dm]) {
					((AppGameContainer) gc).setDisplayMode(disx[dm], disy[dm], fullscreen);
					Main.INTERNAL_ASPECT=((float) MainMenuState.disx[dm]/(float) MainMenuState.disy[dm]);
					Main.INTERNAL_RESX = (int) (Main.INTERNAL_RESY*Main.INTERNAL_ASPECT); //Internal resolution x
				}
				((AppGameContainer) gc).setMouseGrabbed(dm==3 || dm==3);
				HashmapLoader.writeHashmap("conf", Main.globals);
				selection = 0;
				subMenu = 0;
				dx=-256;
			} break;
			case 402: {
				//TODO: make this
			} break;
			case 407: {
				selection = 0;
				subMenu = 2;
				dx=-256;
			} break;
			case 100: {
				selection = 0;
				subMenu = 0;
				dx=-256;
			} break;
			case 101: {
				//New game
				if (!showTextField) {
					levels[1] = "";
					showTextField=true;
					textField.setText("");
					textField.setAcceptingInput(true);
					textField.setFocus(true);
				} else {
					if (textField.getText().equals("")) {
					} else if (new File("save/"+textField.getText()).exists()) {
						gc.getInput().clearKeyPressedRecord();
						((ErrorState) sbg.getState(Main.ERRORSTATE)).errorText = "Save "+textField.getText()+" already exists!";
						sbg.enterState(Main.ERRORSTATE);
					} else {
						try {
							copyDirectory(new File("save/new"),new File("save/"+textField.getText()));
							ArrayList<File> files = getSubs(new File("save"));
							levels = new String[files.size()+2];
							entryCount[1]=files.size()+2;
							levels[0] = "Back";
							levels[1] = "New Game";
							int i=2;
							for (File f:files) {
								levels[i] = f.getName();
								i++;
							}
						} catch (IOException e) {
							gc.getInput().clearKeyPressedRecord();
							((ErrorState) sbg.getState(Main.ERRORSTATE)).errorText = "IO Error making save.\n" +
									"Try another name.";
							sbg.enterState(Main.ERRORSTATE);
						}
					}
					levels[1] = "New Game";
					showTextField=false;
					textField.setText("");
					textField.setAcceptingInput(false);
					textField.setFocus(false);
				}
			} break;
			case 300: {
				selection = 0;
				subMenu = 0;
				dx=-256;
			} break;
			case 301: {
				//Enter IP
				if (!showTextField) {
					showList[1] = "";
					showTextField=true;
					textField.setText("");
					textField.setAcceptingInput(true);
					textField.setFocus(true);
				} else {
					try {
						((GameClient) sbg.getState(Main.GAMEPLAYSTATE)).join(InetAddress.getByName(textField.getText()));
						gc.getInput().clearKeyPressedRecord();
						sbg.enterState(Main.LOGINSTATE);
						//TODO: FIX
					} catch (UnknownHostException e) {
						gc.getInput().clearKeyPressedRecord();
						((ErrorState) sbg.getState(Main.ERRORSTATE)).errorText = "Unknown host "+textField.getText();
						sbg.enterState(Main.ERRORSTATE);
					} catch (IOException e) {
						gc.getInput().clearKeyPressedRecord();
						((ErrorState) sbg.getState(Main.ERRORSTATE)).errorText = "Couldn't connect to "+textField.getText();
						sbg.enterState(Main.ERRORSTATE);
					}
					showList[1] = entryString[3][1];
					showTextField=false;
					textField.setText("");
					textField.setAcceptingInput(false);
					textField.setFocus(false);
				}
			} break;
			default: {
				if (subMenu == 1) {
					Main.globals.put("save",levels[selection]);
					((GameClient) sbg.getState(Main.GAMEPLAYSTATE)).loadSave(gc,levels[selection],serverOnly);
					((GameClient) sbg.getState(Main.GAMEPLAYSTATE)).init(gc,sbg);
					gc.getInput().clearKeyPressedRecord();
					sbg.enterState(Main.GAMEPLAYSTATE);
				} else if (subMenu == 3) {
					try {
						((GameClient) sbg.getState(Main.GAMEPLAYSTATE)).join(lanServers.get(selection-2));
						((GameClient) sbg.getState(Main.GAMEPLAYSTATE)).init(gc,sbg);
						gc.getInput().clearKeyPressedRecord();
						sbg.enterState(Main.LOGINSTATE);
					} catch (IOException e) {
						//TODO: warn
					}
				}
			}
			}
		}
	}

	public List<InetAddress> discoverHosts (int udpPort, int timeoutMillis) {
		List<InetAddress> hosts = new ArrayList<InetAddress>();
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			ByteBuffer dataBuffer = ByteBuffer.allocate(64);
			new KryoSerialization().write(null, dataBuffer, new FrameworkMessage.DiscoverHost());
			dataBuffer.flip();
			byte[] data = new byte[dataBuffer.limit()];
			dataBuffer.get(data);
			for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
				if (networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue; // Don't want to broadcast to the loopback interface
				}

				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null) {
						continue;
					}

					// Send the broadcast package!
					try {
						DatagramPacket sendPacket = new DatagramPacket(data, data.length, broadcast, udpPort);
						socket.send(sendPacket);
					} catch (Exception e) {
					}

					Log.info("Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
				}
				//try {
				/*for (InetAddress address : Collections.list(iface.getInetAddresses())) {
					//Log.info(iface.toString()+": "+address);
					// Java 1.5 doesn't support getting the subnet mask, so try the two most common.
					//byte[] ip = address.getAddress();
					//ip[3] = -1;

					socket.send(new DatagramPacket(data, data.length, InetAddress.getByAddress(ip), udpPort));
				}*/
				//} catch (Exception e) { e.printStackTrace();}
			}
			if (Log.DEBUG) Log.debug("kryonet", "Broadcasted host discovery on port: " + udpPort);
			socket.setSoTimeout(timeoutMillis);
			while (true) {
				DatagramPacket packet = new DatagramPacket(new byte[0], 0);
				try {
					socket.receive(packet);
				} catch (SocketTimeoutException ex) {
					if (Log.INFO) Log.info("kryonet", "Host discovery timed out.");
					return hosts;
				}
				if (Log.INFO) Log.info("kryonet", "Discovered server: " + packet.getAddress());
				hosts.add(packet.getAddress());
			}
		} catch (IOException ex) {
			if (Log.ERROR) Log.error("kryonet", "Host discovery failed.", ex);
			return hosts;
		} finally {
			if (socket != null) socket.close();
		}
	}


	// If targetLocation does not exist, it will be created.
	public static void copyDirectory(File sourceLocation , File targetLocation)
			throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i=0; i<children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]),
						new File(targetLocation, children[i]));
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}
}