/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.world.entity;

import net.sekien.elesmyr.GameElement;
import net.sekien.elesmyr.msgsys.Connection;
import net.sekien.elesmyr.msgsys.Message;
import net.sekien.elesmyr.msgsys.MessageSystem;
import net.sekien.elesmyr.system.FontRenderer;
import net.sekien.elesmyr.util.FileHandler;
import net.sekien.elesmyr.util.ResourceType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA. User: matt Date: 3/03/13 Time: 3:47 PM To change this template use File | Settings | File
 * Templates.
 */
public class DialogueHandler {
HashMap<String, ArrayList<String>> dialogue;
String afname;

public DialogueHandler(String fname) {
	dialogue = new HashMap<String, ArrayList<String>>();
	for (FontRenderer.Language lang : FontRenderer.Language.values()) {
		loadDiag(fname, lang.name(), lang!=FontRenderer.Language.EN_US);
	}
}

private void loadDiag(String fname, String lang, boolean ignorable) {
	File file = new File(FileHandler.parse("npc."+lang+"."+fname, ResourceType.PLAIN));
	afname = file.toString();
	ArrayList<String> diagInL = new ArrayList<String>();
	if (!file.exists()) {
		if (ignorable) return;
		diagInL.add("int:");
		diagInL.add("INTERNAL SERVER ERROR:\nRequested dialogue file '"+file+" was not found");
	} else {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String l;
			while ((l = br.readLine())!=null) {
				diagInL.add(l);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			diagInL.add("int:");
			diagInL.add("INTERNAL SERVER ERROR:\nReading requested dialogue file '"+file+" threw an exception:\n"+e.getLocalizedMessage());
		}
	}
	dialogue.put(lang, diagInL);
}

public void response(GameElement master, String lang, String call, Connection connection) { //TODO: fix tresponse getting ignored
	int ln = 1;
	boolean in = false;
	boolean askmode = false;
	String sendline = "";
	if (FontRenderer.Language.valueOf(lang)==null) {
		MessageSystem.sendClient(master, connection, new Message("CLIENT.talk", "talk:INTERNAL SERVER ERROR:\nDialogueHandler.response was called with an\nunrecognised language: '"+lang+"'"), false);
		return;
	}
	String langm = "EN_US";
	if (dialogue.containsKey(lang))
		langm = lang;
	while (ln < dialogue.get(langm).size()+1) {
		String line = dialogue.get(langm).get(ln-1).trim();
		if (in) {
			System.out.println(askmode+line);
			if (askmode) {
				if (line.equals("END")) {
					MessageSystem.sendClient(master, connection, new Message("CLIENT.talk", "ask:"+sendline), false);
					return;
				} else {
					sendline = sendline+"|"+line;
				}
			} else if (line.startsWith("ASK")) {
				askmode = true;
				sendline = line.substring(4);
			} else if (line.startsWith("GOTO")) {
				if (line.split(" ").length > 2)
					MessageSystem.sendClient(master, connection, new Message("CLIENT.talk", "talkwf:"+line.split(" ", 3)[1]+":"+line.split(" ", 3)[2]), false);
				else
					response(master, lang, line.split(" ")[1], connection);
				return;
			} else {
				MessageSystem.sendClient(master, connection, new Message("CLIENT.talk", "talk:"+line), false);
				return;
			}
		} else if (call.startsWith("#") && ln==Integer.parseInt(call.substring(1))) {
			in = true;
		} else if (line.equals(call+":")) {
			in = true;
		}
		ln++;
	}
	if (!in) {
		MessageSystem.sendClient(master, connection, new Message("CLIENT.talk", "talk:INTERNAL SERVER ERROR:\nLabel '"+call+"' was not found in file "+afname), false);
	} else {
		MessageSystem.sendClient(master, connection, new Message("CLIENT.talk", "talk:INTERNAL SERVER ERROR:\nLabel '"+call+"' in file '"+afname+"' has no end."), false);
	}
}
}
