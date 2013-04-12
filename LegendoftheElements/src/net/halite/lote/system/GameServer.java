package net.halite.lote.system;

import com.esotericsoftware.minlog.Log;
import net.halite.hbt.HBTString;
import net.halite.hbt.HBTTag;
import net.halite.lote.Save;
import net.halite.lote.msgsys.Connection;
import net.halite.lote.msgsys.Message;
import net.halite.lote.msgsys.MessageReceiver;
import net.halite.lote.msgsys.MessageSystem;
import net.halite.lote.player.PlayerData;
import net.halite.lote.util.HashmapLoader;
import net.halite.lote.world.Region;
import net.halite.lote.world.World;
import net.halite.lote.world.entity.Entity;
import net.halite.lote.world.entity.EntityPlayer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer implements MessageReceiver {
private Save save;
/** Server-side world; */
private World world;

private ConcurrentHashMap<Connection, String> players;
private HashMap<String, String> pass;

private int timeCheck=20;

private int date;
private float time; //In-game minutes
private long rlStartTime;
private int igStartTime;
private static final int timescale=50; //Default = 50

private String hostUName="";

public static boolean running=true;

/** Ent is stored as "REGION.ENTID" */
private ConcurrentHashMap<String, String> playerEnt;

private ConcurrentHashMap<String, PlayerData> playerDat;

public GameServer(Save save, String hostUName) {
	this.save=save;
	players=new ConcurrentHashMap<Connection, String>();
	playerEnt=new ConcurrentHashMap<String, String>();
	playerDat=new ConcurrentHashMap<String, PlayerData>();
	if (new File("pass").exists())
		pass=HashmapLoader.readHashmap("pass");
	else
		pass=new HashMap<String, String>();
	this.hostUName=hostUName;
	rlStartTime=System.currentTimeMillis();
}

public void load() {
	world=new World();
	world.load(save);
	igStartTime=save.getInt("gen.time",0);
	date=save.getInt("gen.date",1234);
	/*for (Entry<String, String> e : save.getEntries())
		if (e.getKey().startsWith("pdat.")) {
			PlayerData pdat=new PlayerData(e.getKey().substring(5), null);
			pdat.fromString(e.getValue());
			playerDat.put(e.getKey().substring(5), pdat);
		}*/
	System.out.println(save.getCompound("pdat"));
	for (HBTTag tag : save.getCompound("pdat")) {
		PlayerData pdat=new PlayerData(tag.getName(), null);
		pdat.fromHBT(tag);
		playerDat.put(tag.getName(), pdat);
	}

}

public void save() {
	HashmapLoader.writeHashmap("pass", pass);
	world.save(save);
	save.putInt("gen.time", (int) time);
	save.putInt("gen.date", date);
	for (Entry<String, PlayerData> e : playerDat.entrySet()) {
		//save.put("pdat."+e.getKey(), e.getValue().toString()); //rewrite to use HBT
		save.putTag("pdat."+e.getKey(), e.getValue().toHBT());
	}
	save.write(); //Write to disk
}

public void gameUpdate() {
	//Time
	float elapsedTime=(System.currentTimeMillis()-rlStartTime);
	time=(((elapsedTime/1000)/60f)*timescale)+igStartTime;
	if (time>1440) {
		rlStartTime=System.currentTimeMillis();
		time=time-1440;
		igStartTime=0;
		date++;
	}

	MessageSystem.receiveMessageServer();

	//World
	world.update(null, this);

	for (Entry<String,PlayerData> entry : playerDat.entrySet()) {
		if (entry.getValue().reqUpdate()) {
			String pEnt = playerEnt.get(entry.getKey());
			entry.getValue().updated(world.getRegion(pEnt.split("\\.")[0]),pEnt);
		}
	}

	//Check stuff
	if (timeCheck==0) {
		timeCheck=100;
		if (Globals.get("autosave", true))
			this.save();
		for (Connection c : players.keySet()) {
			//Check if connection lost
			if (!c.isConnected()) {
				if (players.containsKey(c)) {
					sendChat("SERVER: "+players.get(c)+" timed out.");
					world.receiveMessage(new Message(playerEnt.get(players.get(c)).split("\\.", 2)[0]+".killSERV", playerEnt.get(players.get(c)).split("\\.", 2)[1]), this);
					for (Region r : world.regions.values()) {
						r.connections.remove(c);
					}
					playerEnt.remove(players.get(c));
					players.remove(c);
				}
			}

			//Send time packet
			MessageSystem.sendClient(null, c, new Message("CLIENT.time", String.valueOf(time)+":"+String.valueOf(date)), true);
		}
	} else {
		timeCheck--;
	}
}

public boolean receiveMessage(Message msg) {
	Connection connection=msg.getConnection();
	if (msg.getTarget().equals("SERVER")) {
		String name=msg.getName();
		String data=msg.getData();
		if (name.equals("wantPlayer")) {
			String uname=data.split(",", 2)[0];
			String upass=data.split(",", 2)[1];
			if (pass.containsKey(uname)) {
				if (!pass.get(uname).equals(upass)&&!uname.equals(hostUName)) {
					MessageSystem.sendClient(null, connection, new Message("CLIENT.error", "Password incorrect!"), false);
					Log.info("server",connection.toString()+" failed password for "+uname);
					try {
					connection.close();
					} catch (IOException e) {
					}
					return false;
				}
			} else if (hostUName=="") {
				//Register user
				pass.put(uname, upass);
			}
			//String uname = data;
			if (playerEnt.containsKey(uname)) {
				MessageSystem.sendClient(null, connection, new Message("CLIENT.error", "There is already a player called "+uname+" on that server!"), false);
			} else {
				sendChat("SERVER: "+uname+" joined the game.");
				Log.info("server",connection.toString()+" logged in as "+uname);
				players.put(connection, uname);
				String pInfo=((HBTString) save.getTag("players."+uname+".str")).getData(); //TODO: Use proper HBTCompound stuffs
				if (pInfo!=null) {
					MessageSystem.sendClient(null, connection, new Message("PLAYER.playerInfo", pInfo), false);
				} else {
					MessageSystem.sendClient(null, connection, new Message("PLAYER.playerInfo", ((HBTString) save.getTag("players.new.str")).getData()), false);
				}
			}
		} else if (name.equals("getRegion")) {
			int x=Integer.parseInt(data.split(",")[1]);
			int y=Integer.parseInt(data.split(",")[2]);
			data=data.split(",")[0];
			changePlayerRegion(data, x, y, connection, false);
		} else if (name.equals("changeRegion")) {
			int x=Integer.parseInt(data.split(",")[1]);
			int y=Integer.parseInt(data.split(",")[2]);
			data=data.split(",")[0];
			changePlayerRegion(data, x, y, connection, true);
		} else if (name.equals("close")) {
			sendChat("SERVER: "+players.get(connection)+" left the game.");
			if (players.get(connection)==null||playerEnt.get(players.get(connection))==null) {
				Log.info("Close message from non-player");
				return false;
			}
			save.putPlayer(players.get(connection), playerEnt.get(players.get(connection)), world);
			world.receiveMessage(new Message(playerEnt.get(players.get(connection)).split("\\.", 2)[0]+".killSERV",
					                                playerEnt.get(players.get(connection)).split("\\.", 2)[1]), this);
			for (Region r : world.regions.values()) {
				r.connections.remove(connection);
			}
			playerEnt.remove(players.get(connection));
			players.remove(connection);
		} else if (name.equals("chat")) {
			sendChat(players.get(connection)+": "+msg.getData());
		} else if (name.equals("setTime")) {
			rlStartTime=System.currentTimeMillis();
			igStartTime=Integer.valueOf(msg.getData());
		} else {
			Log.warn("SERVER: Ignored message - unrecognised name: "+msg.toString());
		}
	} else {
		world.receiveMessage(msg, this);
	}
	return false;
}

public void changePlayerRegion(String data, int x, int y, Connection connection, boolean killOld) {
	if (killOld) {
		world.receiveMessage(new Message(playerEnt.get(players.get(connection)).split("\\.", 2)[0]+".killSERV",
				                                playerEnt.get(players.get(connection)).split("\\.", 2)[1]), this);
		for (Region r : world.regions.values()) {
			r.connections.remove(connection);
		}
	}
	world.touchRegion(data); //Load region if unloaded
	world.getRegion(data).connections.add(connection); //Add the connection to the region
	String rEntD=world.getRegion(data).getEntityString(); //Get the region's entity string
	if (rEntD.length()<2800) //If the string is smaller than 2800 chars, send it
		MessageSystem.sendClient(null, connection, new Message("CLIENT.setRegion", data+":"+rEntD), false);
	else {
		MessageSystem.sendClient(null, connection, new Message("CLIENT.setRegion", data+":"), false);
		for (Entity e : world.getRegion(data).entities.values()) {
			MessageSystem.sendClient(null, connection, new Message(data+".addEnt", e.toString()), false);
		}
	}
	int entid=world.getRegion(data).addEntityServer("EntityPlayer,"+x+","+y+","+players.get(connection)); //Add a player entity
	if (entid==-1) { //Shouldn't happen
		MessageSystem.sendClient(null, connection, new Message("CLIENT.error", "addEntityServer returned -1"), false);
		return;
	}
	PlayerData pdat=playerDat.get(players.get(connection)); //Get the PlayerData object for this player
	if (pdat==null) //If there is no PlayerData object for this player, make one.
		playerDat.put(players.get(connection), pdat=new PlayerData(players.get(connection), connection));
	else
		pdat.addConnection(connection); //Set the connection
	((EntityPlayer) world.getRegion(data).entities.get(entid)).setSERVDAT(connection, pdat); //Tell the player entity about the PlayerData object
	playerEnt.put(players.get(connection), data+"."+entid); //Put the entities name into playerEnt
	MessageSystem.sendClient(null, connection, new Message("PLAYER.setID", ""+entid), false); //Send a message to the client notifying it of its entity's ID.
	MessageSystem.sendClient(null, connection, new Message("PLAYER.setPDAT", pdat.toString()), false); //Send client PDAT
	MessageSystem.sendClient(null, connection, new Message("CLIENT.time", String.valueOf(time)+":"+String.valueOf(date)), false); //Send the client the time
	for (Entity e : world.getRegion(data).entities.values()) {
		if (e instanceof EntityPlayer) {
			((EntityPlayer) e).pdat.updated(world.getRegion(data), e.getReceiverName());
		}
	}
}

public void sendChat(String msg) {
	MessageSystem.sendClient(null, getConnections(), new Message("CLIENT.chat", msg), false);
}

private List<Connection> getConnections() {
	return MessageSystem.getConnections();
}

public void render(GameContainer gc, StateBasedGame sbg, Graphics g, boolean overlay) {
	if (overlay) {
		FontRenderer.drawString(10, 18, "Connections:", g);
		int i=0;
		for (Entry<String, String> e : playerEnt.entrySet()) {
			FontRenderer.drawString(10, 32+i*10, e.getKey()+":"+e.getValue(), g);
			i++;
		}
	} else {
		FontRenderer.drawString(10, 18, "Connections:", g);
		FontRenderer.drawString(128, 18, "Regions:", g);
		int i=0;
		for (Entry<String, String> e : playerEnt.entrySet()) {
			FontRenderer.drawString(10, 32+i*10, e.getKey()+":"+e.getValue(), g);
			i++;
		}
		i=0;
		for (Region r : this.world.regions.values()) {
			FontRenderer.drawString(128, 32+i*10, r.name, g);
			i++;
			for (Entity e : r.entities.values()) {
				FontRenderer.drawString(128, 32+i*10, "    "+e.toString(), g);
				i++;
			}
		}
	}
}

public EntityPlayer getPlayerEnt(Connection connection) {
	return (EntityPlayer) getEntity(playerEnt.get(players.get(connection)));
}

public Connection getPlayerConnection(EntityPlayer player) {
	String name=player.getName();
	for (Entry<Connection, String> e : players.entrySet()) {
		if (e.getValue().equals(name)) {
			return e.getKey();
		}
	}
	Log.error("SERVER: Could not find corresponding connection for "+name);
	return null;
}

public Entity getEntity(String string) {
	String[] args=string.split("\\.", 2);
	return world.getRegion(args[0]).entities.get(Integer.parseInt(args[1]));
}

@Override
public boolean isServer() {
	return true;
}

public void broadcastKill() {
	MessageSystem.sendClient(null, getConnections(), new Message("CLIENT.error", "Server has been closed."), false);
}
}
