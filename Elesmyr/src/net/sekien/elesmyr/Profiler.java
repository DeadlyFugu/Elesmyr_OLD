package net.sekien.elesmyr;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA. User: matt Date: 19/04/13 Time: 9:57 PM To change this template use File | Settings |
 * File Templates.
 */
public class Profiler {
private static HashMap<String, Long> currentTimes = new HashMap<String, Long>();
private static HashMap<String, Long> lastTimes;
private static String name;
private static long start;

public static void startSection(String name) {
	Profiler.name = name;
	start = System.nanoTime();
}

public static void endSection() {
	currentTimes.put(name, System.nanoTime()-start);
}

public static void flush() {
	lastTimes = (HashMap<String, Long>) currentTimes.clone();
	currentTimes.clear();
}

public static HashMap<String, Long> getTimes() {
	return (HashMap<String, Long>) lastTimes.clone();
}
}
