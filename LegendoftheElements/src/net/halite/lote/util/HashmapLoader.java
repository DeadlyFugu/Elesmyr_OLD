package net.halite.lote.util;

import java.io.*;
import java.util.HashMap;

/*
 * Hashmap code obtained from Technius at http://forums.bukkit.org/threads/saving-loading-hashmap.56447/
 * All credit for original code goes to him.
 */

public class HashmapLoader {

/**
 * Writes a hashmap to a file.
 * @deprecated Use {@link net.halite.lote.util.FileHandler#writeMap(java.io.File, java.util.Map)} instead.
 * @param filename
 * @param hashMap
 */
@Deprecated public static void writeHashmap(String filename, HashMap<String, String> hashMap) {
	File file=new File(filename);
	try {
		BufferedWriter bw=new BufferedWriter(new FileWriter(file));
		for (String k : hashMap.keySet()) {
			bw.write(k+","+hashMap.get(k));
			bw.newLine();
		}
		bw.flush();
		bw.close();
	} catch (Exception e) {
		e.printStackTrace();
		System.exit(1);
	}
}

/**
 * Read a hashmap from a file.
 * @deprecated Use {@link net.halite.lote.util.FileHandler#readMap(String)} instead.
 * @param filename the name of the file to read from
 * @return the HashMap read
 */
@Deprecated public static HashMap<String, String> readHashmap(String filename) {
	HashMap<String, String> hashMap=new HashMap<String, String>();
	File file=new File(filename);
	try {
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String l;
		while ((l=br.readLine())!=null) {
			String[] args=l.split("[,]", 2);
			if (args.length!=2) continue;
			String p=args[0];
			String b=args[1];
			hashMap.put(p, b);
		}
		br.close();
	} catch (Exception e) {
		e.printStackTrace();
		System.exit(1);
	}
	return hashMap;
}

public static HashMap<String, String> readHashmapWHeader(String keyHeader, String filename) {
	HashMap<String, String> hashMap=new HashMap<String, String>();
	File file=new File(filename);
	try {
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String l;
		while ((l=br.readLine())!=null) {
			String[] args=l.split("[,]", 2);
			if (args.length!=2) continue;
			String p=args[0];
			String b=args[1];
			hashMap.put(keyHeader+p, b);
		}
		br.close();
	} catch (Exception e) {
		e.printStackTrace();
		System.exit(1);
	}
	return hashMap;
}
}
