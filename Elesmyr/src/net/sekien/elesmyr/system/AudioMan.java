package net.sekien.elesmyr.system;

import com.esotericsoftware.minlog.Log;
import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.msgsys.MessageReceiver;
import net.sekien.elesmyr.msgsys.MessageSystem;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.elesmyr.util.ResourceType;
import net.sekien.hbt.*;
import org.newdawn.slick.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 28/09/13 Time: 1:48 PM To change this template use File | Settings |
 * File Templates.
 */
public class AudioMan implements MusicListener, MessageReceiver {
	private static Music music;
	private static String file;

	private AudioMan() {
	}

	public static void init() {
		MessageSystem.registerReceiverClient(new AudioMan());
	}

	public static void setMusic(String title, float pos) {
		if (!Globals.get("disableMusic", false)) try {
			file = title;
			Music newm = new Music(FileHandler.parse("mus."+title, ResourceType.MUSIC), false);
			if (music != null) music.stop();
			music = newm;
			music.setPosition(pos);
			music.addListener(new AudioMan());
			music.play();
		} catch (Throwable e) {
			e.printStackTrace();
			Log.warn("Missing sfx: "+title);
		}
	}

	public static void playSound(String title) {
		try {
			new Sound(FileHandler.parse("au."+title, ResourceType.SFX)).play();
		} catch (Exception e) {
			e.printStackTrace();
			Log.warn("Missing sfx: "+title);
		}
	}

	@Override public void musicEnded(Music music) {
		if (file.startsWith("ui_"))
			music.play();
		else {
			try {
				MessageSystem.sendServer(this, new Message("_serveraudio.sngend", new HBTCompound("p")), true);
			} catch (Exception e) {
				//failing case if server unavailable
				music.play();
			}
		}
	}

	@Override public void musicSwapped(Music music, Music music2) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public void receiveMessage(Message msg, MessageEndPoint receiver) {
		System.out.println("msg = "+msg);
		if (msg.getName().equals("setm")) {
			HBTCompound data = msg.getData();
			setMusic(data.getString("file", "(hbt msg is missing 'file' attribute)"), data.getFloat("pos", 0));
		} else if (msg.getName().equals("getm")) {
			msg.reply("getm_r", new HBTCompound("p", new HBTTag[]{new HBTString("file", file), new HBTFloat("pos", music.getPosition()), msg.getData().getTag("qhash")}), this);
		} else {
			System.err.println("AudioMan: unrecognised message "+msg);
		}
	}

	@Override public String getReceiverName() {
		return "_audioman";
	}

	@Override public void fromHBT(HBTCompound tag) {
	}

	@Override public HBTCompound toHBT(boolean msg) {
		return HBTTools.msgString("class", "AudioMan");
	}
}
