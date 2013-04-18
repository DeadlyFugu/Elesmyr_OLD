package net.sekien.elesmyr;

import com.esotericsoftware.minlog.Log;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import net.sekien.elesmyr.util.FileHandler;
import org.codehaus.groovy.control.CompilationFailedException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ScriptRunner {
private static HashMap<String, Class> classes;

public static void init() {
	classes = new HashMap<String, Class>();
	//Setup GroovyClassLoader
	GroovyClassLoader loader = new GroovyClassLoader();

	//Loop through files
	for (File f : FileHandler.getDataFolderContents("scr")) {
		Class tclass = null;
		try {
			tclass = loader.parseClass(f);
		} catch (CompilationFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (tclass!=null)
			classes.put(tclass.getSimpleName(), tclass);
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
