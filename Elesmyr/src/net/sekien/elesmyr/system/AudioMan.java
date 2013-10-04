package net.sekien.elesmyr.system;

import com.esotericsoftware.minlog.Log;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.elesmyr.util.ResourceType;
import org.newdawn.slick.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 28/09/13 Time: 1:48 PM To change this template use File | Settings |
 * File Templates.
 */
public class AudioMan implements MusicListener {

private static Music music;

private AudioMan() {
}

public static void setMusic(String title) {
	try {
		//		Music newm = new Music(FileHandler.parse("mus."+title, ResourceType.MUSIC));
		//		if (music!=null) music.stop();
		//		music = newm;
		//		music.addListener(new AudioMan());
		//		music.play();
	} catch (Exception e) {
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
	music.play();
}

@Override public void musicSwapped(Music music, Music music2) {
	//To change body of implemented methods use File | Settings | File Templates.
}
}
