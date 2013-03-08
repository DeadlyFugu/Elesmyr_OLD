package net.halitesoft.lote.util;

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
public static ArrayList<String> parseBook(String extd) {
	ArrayList<String> pages = new ArrayList<String>();
	String page = "";
	File file = new File("data/book/"+extd);
	if (!file.exists()) {
		pages.add("INTERNAL SERVER ERROR:\nRequested book file '"+file+" was not found");
	} else {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String l;
			while((l = br.readLine()) != null) {
				if (!l.equals("PAGE"))
					page=page+l+" [E ";
				else
					page=page+"PAGE ";
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			pages.add("INTERNAL SERVER ERROR:\nReading requested book file '"+file+" threw an exception:\n"+e.getLocalizedMessage());
		}
	}
	String pageout = "";
	String ln = "";
	for (String w : page.split(" ")) {
		if (w.startsWith("[")) {
			if (w.equals("[E")) {
				pageout+=ln+"\n";
				ln="";
				if (pageout.split("\n").length==23) {
					pages.add(pageout);
					pageout="";
				}
			} else {
				ln+=w+" ";
			}
		} else if (w.equals("PAGE")) {
			pageout+=ln+"\n";
			pages.add(pageout);
			ln="";
			pageout="";
		} else {
			String ln2 = ln+w+" ";
			if (ln2.replaceAll("\\[[A-Za-z0-9]+","").length()>30) {
				pageout+=ln+"\n";
				ln=w+" ";
				if (pageout.split("\n").length==23) {
					pages.add(pageout);
					pageout="";
				}
			} else {
				ln=ln2;
			}
		}
	}
	pageout+=ln+"\n";
	pages.add(pageout);
	return pages;
}
}
