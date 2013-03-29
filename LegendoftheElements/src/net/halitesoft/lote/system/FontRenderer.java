package net.halitesoft.lote.system;

import net.halitesoft.lote.util.HashmapLoader;
import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.ColorEffect;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA. User: matt Date: 2/03/13 Time: 10:48 AM To change this template use File | Settings |
 * File Templates.
 */
public class FontRenderer {

private static SpriteSheetFont bpfont;
private static UnicodeFont jpfont=null;
private static UnicodeFont bookfont=null;
private static Language lang=Language.EN_US;
private static Language newlang=lang;

private static HashMap<String, String> i18n_lang;
private static HashMap<String, String> i18n_backup;

public enum Language {EN_US, JP}

;

//public static void drawString(int x, int y, String text) {
//
//}

public static void setLang(Language lang) {
	FontRenderer.newlang=lang;
}

public static void reset(GameContainer gc) throws SlickException {
	lang=newlang;
	i18n_lang=HashmapLoader.readHashmap("data/lang/"+lang.name());
	if (jpfont==null&&lang==Language.JP) {
		String fontPath="data/jp.ttf";
		UnicodeFont uFont=new UnicodeFont(fontPath, 32, false, false);
		uFont.addAsciiGlyphs();
		uFont.addGlyphs('ぁ', 'ヿ'); //Hiragana + Katakana
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
	bpfont=new SpriteSheetFont(new SpriteSheet(new org.newdawn.slick.Image("data/font.png", false, 0), 9, 16), ' ');
	i18n_backup=HashmapLoader.readHashmap("data/lang/EN_US");
	bookfont=new UnicodeFont("data/ui/book.ttf", 32, false, false);
	bookfont.addAsciiGlyphs();
	bookfont.getEffects().add(new ColorEffect(java.awt.Color.BLACK));
	bookfont.loadGlyphs();
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
			g.scale(0.5f, 0.5f);
			jpfont.drawString(x*2, y*2, str);
			g.popTransform(); break;
		default:
			bpfont.drawString(x, y, str); break;
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
			bpfont.drawString(x, y, str, col); break;
	}
}

public static void drawStringBook(int x, int y, String str, Graphics g) {
	String parts[] = str.split(" ");
	ArrayList<String> toRender = new ArrayList<String>();
	boolean bold = false;
	byte alignment = 0; //0=left, 1=center, 2=right
	boolean justify=false;
	for (String s : parts) {
		if (s.startsWith("[") && s.length()==2) {
			if (s.equals("[B"))
				bold=true;
			else if (s.equals("[C"))
				alignment=1;
			else if (s.equals("[R"))
				alignment=2;
			else if (s.equals("[J"))
				justify=true;
			else
				toRender.add(s);
		} else {
			toRender.add(s);
		}
	}
	g.pushTransform();
	g.scale(0.5f, 0.5f);
	int spacing=6;
	if (justify) {
		int addSpace=0;
		String out = "";
		for (String s : toRender) {
			if (s.equals("[tab"))
				addSpace+=24;
			else
				out+=s;
		}
		spacing=(472-(bookfont.getWidth(out)+addSpace))/(Math.max(1,toRender.size()-1))/2;
	}
	int xdsofar = 0;
	if (alignment!=0) {
		String out = "";
		for (String s : toRender) {
			out+=s;
		}
		xdsofar=(472-bookfont.getWidth(out))/2;
		if (alignment==1) {
			xdsofar/=2;
		}
	}
	for (String s : toRender) {
		if (s.equals("[tab"))
			xdsofar+=24;
		else {
		bookfont.drawString((x+xdsofar)*2, y*2, s);
		xdsofar+=bookfont.getWidth(s)/2+spacing;
		}
	}
	g.popTransform();
}

public static String resolveI18n(String key) {
	if (key.contains("|")) {
		String ret="";
		String[] split=key.split("\\|");
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
