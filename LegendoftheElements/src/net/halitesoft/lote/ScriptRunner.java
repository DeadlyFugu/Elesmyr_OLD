package net.halitesoft.lote;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.groovy.control.CompilationFailedException;

import com.esotericsoftware.minlog.Log;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

public class ScriptRunner {
	private static HashMap<String,Class> classes;
	public static void init() {
		classes = new HashMap<String,Class>();
		//Setup GroovyClassLoader
		GroovyClassLoader loader = new GroovyClassLoader();
		
		//Read each file in
		ArrayList<File> temp = new ArrayList<File>();
		File[] fileList = new File("data/scr").listFiles();

		for (int i = 0; i < fileList.length; i++) {
			File choose = fileList[i];
			if (choose.isFile() && !temp.contains(choose)) {
				temp.add(choose);
			}
		}
		
		//Loop through files
		for (File f : temp) {
			Class tclass = null;
			try {
				tclass = loader.parseClass(f);
			} catch (CompilationFailedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (tclass != null)
				classes.put(tclass.getSimpleName(),tclass);
		}
		
		Log.info("Loaded classes: "+classes);

		get("Init").invokeMethod("run", new Object[0]);
	}
	
	public static GroovyObject get(String name) {
		try {
			return (GroovyObject) classes.get(name).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
		}
		return null;
	}
}
