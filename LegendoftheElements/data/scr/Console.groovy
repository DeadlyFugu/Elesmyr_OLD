import net.halite.lote.DateTime
import net.halite.lote.msgsys.Message
import net.halite.lote.msgsys.MessageSystem
import net.halite.lote.system.GameClient
import net.halite.lote.system.Globals
import net.halite.lote.system.Main
import net.halite.lote.world.WeatherSystem.WeatherType

public class Console {
	private static final helpString = "Commands:\n"+
			"echo: Prints the arguments to the chat.\n"+
			"time [time]: Sets the time (Prints it if passed no args).\n"+
			"item <name> [amount]: Give the player an item.\n"+
            "spawn <name> [count] [extd]: Spawns an entity where the player is.\n"+
			"set <key>=<value>: Change a setting.\n"+
            "get <key>: Retrieve a setting.\n"+
			"debug [on/off]: Enable/disable debug (Toggles if passed no args).\n"+
			"listconf: List all settings and current value.\n"+
			"health <value>: Sets the health of the player.\n"+
            "affinity <element>: Sets the elemental affinity of the player.\n"+
			"weather [type] [strength] [dist]: Changes the weather.\n"+
			"help: Prints this text."
	public void call(String cmd, GameClient gc) {
		String func = cmd.split(" ")[0];
		String args = "";
		if (cmd.contains(" "))
			args = cmd.split(" ",2)[1];
		switch (func) {
		case "echo": echo(args); break;
		case "time": setTime(args,gc); break;
        case "td": echo(new DateTime(gc.date,(int) gc.time).asDetailedString()); break;
		case "item": giveItem(args,gc); break;
        case "spawn": spawnEnt(args,gc); break;
		case "set": Globals.set(args.split("=",2)[0],args.split("=",2)[1]); Globals.save(); break;
        case "get": echo(Globals.retrieve(args)); break;
		case "debug": Globals.set("debug",parseDebugBool(args)); break;
		case "listconf": echo(Globals.getString()); break;
		case "health": sendMsg(getPlayerRName(gc)+".setHealth",args); break;
        case "affinity": sendMsg(getPlayerRName(gc)+".setAffinity",args); break;
		case "setires": Main.INTERNAL_RESY = Integer.parseInt(args); Main.INTERNAL_RESX = (int) (Main.INTERNAL_RESY*Main.INTERNAL_ASPECT); break;
		case "weather": setWeather(args,gc); break;
		case "help": echo(helpString); break;
		default: echo("Unknown command "+func); break;
		}
	}

    private void spawnEnt(String args, GameClient gc) {
        String[] parts = args.split(" ");
        String name = parts[0];
        int count = 1;
        String extd = "";
        if (parts.length>1)
            count=Integer.valueOf(parts[1])
        if (parts.length>2)
            extd=Integer.valueOf(parts[2])
        while (count>0) {
            sendMsg(gc.player.regionName+".addEntSERV",name+","+gc.player.x+","+gc.player.y+","+extd);
            count--;
        }
    }

    private String getPlayerRName(GameClient gc) { return gc.player.regionName+"."+gc.player.entid; }

	private String parseDebugBool(String args) {
		if (args=="")
			return ""+!Boolean.parseBoolean(Globals.get("debug",false));
		if (args.equalsIgnoreCase("yes") ||
				args.equalsIgnoreCase("y") ||
				args.equalsIgnoreCase("on") ||
				args.equalsIgnoreCase("true"))
			return "true";
		return "false";
	}
	
	private void echo(String str) {
        if (str==null) {
            echo("_NULL");
        return
        }
		println(str);
		for (String s : str.split("\n"))
			MessageSystem.receiveClient(new Message("CLIENT.chat",s));
	}
	
	private void sendMsg(String name, String data) {
		MessageSystem.sendServer(null,new Message(name,data),false);
	}
	
	private void setTime(String args, GameClient gc) {
		switch (args) {
		case "": echo(((int) ((gc.time/60)%12))+":"+(int) (gc.time%60)+(gc.time<720?"AM":"PM")); break;
		case "midnight": setTime("0",gc); break;
		case "sunrise": setTime("300",gc); break;
		case "day": setTime("360",gc); break;
		case "midday": setTime("720",gc); break;
		case "sunset": setTime("1080",gc); break;
		case "night": setTime("1200",gc); break;
		default:
			try {
				sendMsg("SERVER.setTime",""+Integer.parseInt(args));
			} catch (Exception e) {
				echo("Invalid time "+args);
				echo("Valid times: midnight,sunrise,day,midday,sunset,night");
			}
		}
	}
	
	private void setWeather(String args, GameClient gc) {
		if (args == "") {
			echo(gc.player.region.weather.toString());
			return;
		}
		String[] parts = args.split(" ");
		int strength = (parts.size()>=2?Integer.parseInt(parts[1]):1);
		int dist = (parts.size()>=3?Integer.parseInt(parts[2]):0);
		try {
			gc.player.region.weather.set(WeatherType.valueOf(parts[0]),strength,dist);
		} catch (Exception e) {
			echo("Invalid type "+args);
			echo("Valid times: midnight,sunrise,day,midday,sunset,night");
		}
	}
	
	private void giveItem(String args, GameClient gc) {
		String[] parts = args.split(" ");
		if (parts.size()==1) {
			sendMsg(gc.player.regionName+"."+gc.player.entid+".putItem",parts[0]+",");
		} else if (parts.size()==2) {
			Integer.parseInt(parts[1]).times{sendMsg(gc.player.regionName+"."+gc.player.entid+".putItem",parts[0]+",")};
		} else if (parts.size()==3) {
			echo("Giving to other users not implemented yet");
			//Integer.parseInt(parts[1]).times{sendMsg(gc.player.regionName+"."+gc.player.entid+".putItem",parts[0]+",")};
		}
	}
}
