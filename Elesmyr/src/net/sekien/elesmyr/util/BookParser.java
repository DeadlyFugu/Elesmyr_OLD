/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.util;

import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA. User: matt Date: 8/03/13 Time: 5:39 PM To change this template use File | Settings | File
 * Templates.
 */
public class BookParser {

private static UnicodeFont bookfont;

static {
	try {
		bookfont = new UnicodeFont(FileHandler.parse("font.book", ResourceType.FONT), 32, false, false);
		bookfont.addAsciiGlyphs();
		bookfont.getEffects().add(new ColorEffect(java.awt.Color.BLACK));
		bookfont.loadGlyphs();
	} catch (SlickException e) {
		e.printStackTrace();
	}
}

public static ArrayList<String> parseBook(String extd) {
	ArrayList<String> pages = new ArrayList<String>();
	String page = "";
	File file = new File(FileHandler.parse("book."+extd, ResourceType.PLAIN));
	if (!file.exists()) {
		pages.add("INTERNAL SERVER ERROR:\nRequested book file '"+file+" was not found");
	} else {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String l;
			while ((l = br.readLine())!=null) {
				if (!l.equals("PAGE"))
					page = page+l+" [E ";
				else
					page = page+"PAGE ";
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			pages.add("INTERNAL SERVER ERROR:\nReading requested book file '"+file+" threw an exception:\n"+e.getLocalizedMessage());
		}
	}
	String pageout = "";
	String ln = "";
	boolean justified = false;
	boolean globalj = false;
	boolean firstLine = true;
	for (String w : page.split(" ")) {
		if (w.startsWith("[")) {
			if (w.equals("[E")) {
				if ((globalj && !(ln.contains("[C") || ln.contains("[R"))) && firstLine) {
					pageout += "[tab ";
				}
				pageout += ln+"\n";
				ln = "";
				justified = false;
				firstLine = true;
				if (pageout.split("\n").length==23) {
					pages.add(pageout);
					pageout = "";
				}
			} else if (w.equals("[NOVEL")) {
				globalj = true;
			} else {
				ln += w+" ";
			}
		} else if (w.equals("PAGE")) {
			pageout += ln+"\n";
			pages.add(pageout);
			ln = "";
			pageout = "";
			justified = false;
			firstLine = true;
		} else {
			String ln2 = ln+w+" ";
			if (bookfont.getWidth(ln2.replaceAll("\\[[A-Za-z0-9] ", "")) > (globalj?480-48:480)) {
				if ((globalj && !(ln.contains("[C") || ln.contains("[R"))) || justified)
					pageout += "[J ";
				if ((globalj && !(ln.contains("[C") || ln.contains("[R"))) && firstLine) {
					pageout += "[tab ";
				}
				if (ln.contains("[J "))
					justified = true;
				pageout += ln+"\n";
				ln = w+" ";
				firstLine = false;
				if (pageout.split("\n").length==23) {
					pages.add(pageout);
					pageout = "";
				}
			} else {
				ln = ln2;
			}
		}
	}
	pageout += ln+"\n";
	pages.add(pageout);
	return pages;
}
}
