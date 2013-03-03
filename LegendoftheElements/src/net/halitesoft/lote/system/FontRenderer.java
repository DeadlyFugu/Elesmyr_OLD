package net.halitesoft.lote.system;

import net.halitesoft.lote.util.HashmapLoader;
import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.ColorEffect;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA. User: matt Date: 2/03/13 Time: 10:48 AM To change this template use File | Settings |
 * File Templates.
 */
public class FontRenderer {

private static SpriteSheetFont bpfont;
private static UnicodeFont jpfont = null;
private static Language lang = Language.EN_US;
private static Language newlang = lang;

private static HashMap<String,String> i18n_lang;
private static HashMap<String,String> i18n_backup;

public enum Language { EN_US, JP };

//public static void drawString(int x, int y, String text) {
//
//}

public static void setLang(Language lang) {
	FontRenderer.newlang=lang;
}

public static void reset(GameContainer gc) throws SlickException {
	lang=newlang;
	i18n_lang = HashmapLoader.readHashmap("data/lang/"+lang.name());
	if (jpfont==null && lang==Language.JP) {
		String fontPath = "data/jp.ttf";
		UnicodeFont uFont = new UnicodeFont(fontPath , 32, false, false);
		uFont.addAsciiGlyphs();
		uFont.addGlyphs('ぁ','ヿ'); //Hiragana + Katakana
		//uFont.addGlyphs('一','龥'); //Kanji
		uFont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
		uFont.loadGlyphs();
		jpfont=uFont;
	}
	((AppGameContainer) gc).setTitle(resolveI18n("bar.title"));
}

public static Language getLang() {
	return newlang;
}

public static void initialise(GameContainer gc) throws SlickException {
	bpfont = new SpriteSheetFont(new SpriteSheet(new org.newdawn.slick.Image("data/font.png",false,0),9,16),' ');
	i18n_backup = HashmapLoader.readHashmap("data/lang/EN_US");
	reset(gc);
}

public static Font getFont() {
	switch (lang) {
		//case JP:
		//	return jpfont;
		default:
			return bpfont;
	}
}

public static int getWidth(String s) {
	if (s.startsWith("#"))
		s=resolveI18n(s.substring(1));
	switch (lang) {
		case JP:
			return jpfont.getWidth(s)/2;
		default:
			return bpfont.getWidth(s);
	}
}

public static void drawString(float x, float y, String str, Graphics g) {
	if (str.startsWith("#"))
		str=resolveI18n(str.substring(1));
	switch (lang) {
		case JP:
			jpfont.addGlyphs(str);
			try {
				jpfont.loadGlyphs();
			} catch (SlickException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
			g.pushTransform();
			g.scale(0.5f,0.5f);
			jpfont.drawString(x*2,y*2,str);
			g.popTransform(); break;
		default:
			bpfont.drawString(x,y,str); break;
	}
}

public static void drawString(int x, int y, String str, Color col, Graphics g) {
	switch (lang) {
		/*case JP:
			g.pushTransform();
			g.scale(0.5f,0.5f);
			jpfont.drawString(x*2,y*2,str);
			g.popTransform(); break;*/
		default:
			bpfont.drawString(x,y,str,col); break;
	}
}

public static String resolveI18n(String key) {
	if (key.contains("|")) {
		String ret="";
		String[] split = key.split("\\|");
		for (String s : split) {
			if (s.startsWith("$")) {
				ret=ret+resolveI18n(s.substring(1));
			} else {
				ret+=s;
			}
		}
		return ret;
	} else if (i18n_lang.containsKey(key)) {
		return i18n_lang.get(key);
	} else if (i18n_backup.containsKey(key)) {
		return i18n_backup.get(key);
	}
	return key;
}
}
