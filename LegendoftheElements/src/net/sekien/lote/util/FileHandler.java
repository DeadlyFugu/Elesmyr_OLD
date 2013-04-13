package net.sekien.lote.util;

import net.sekien.hbt.HBTComment;
import net.sekien.hbt.HBTFlag;
import net.sekien.lote.system.Main;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

import java.io.*;
import java.util.*;

/** Class to handle all file IO. Also handles mods */
public class FileHandler {

private static net.sekien.hbt.HBTCompound data;

private static List<String> packs;

public static String parseExt(String name, ResourceType type) throws FileNotFoundException {
	try {
		return parseFileName(name, type.getExtensions(), true).get(0);
	} catch (IndexOutOfBoundsException e) {
		throw new FileNotFoundException(name);
	}
}

public static String parse(String name, ResourceType type) {
	try {
		return parseFileName(name, type.getExtensions(), true).get(0);
	} catch (IndexOutOfBoundsException e) {
		Main.handleCrash(new FileNotFoundException(name));
		System.exit(0);
	}
	return null;
}

/**
 * Returns paths to all files matching 'name' Examples: parseFileName("item_def",new String[] {""}) return
 * {"data/item_def","mod/MoreWeapons/item_def"} parseFileName("ui.book",new String[] {"png"}) might return
 * {"data/ui/book.png"} parseFileName("pass",new String[] {""}) might return {"pass"} parseFileName("SAVE",new String[]
 * {"hbtx","hbt","hbtc"}) might return {"save/nameOfSave.hbt"} //Maybe don't implement?
 *
 * @param name
 * 		The name of the file
 * @param extension
 * 		The acceptable extensions
 * @return The file's path
 */
public static List<String> parseFileName(String name, String[] extension, boolean dataFile) {
	List<String> found=new ArrayList<String>();
	File f;
	if (dataFile) {
		for (String pack : packs) {
			for (String s : extension) {
				if (!s.equals("")) {
					s="."+s; //TODO: Remove this and files without extensions (To .hm for hashmap files maybe?)
				}
				f=new File(pack+"/"+name.replaceAll("\\.", "/")+s);
				if (f.exists())
					found.add(f.getPath());
			}
		}
	} else {
		for (String s : extension) {
			f=new File(name.replaceAll("\\.", "/")+"."+s);
			if (f.exists())
				found.add(f.getPath());
		}
	}
	return found;
}

public static void readData() throws IOException {
	packs=new ArrayList<String>();
	for (net.sekien.hbt.HBTTag tag : readHBTFile("pack/packs.hbtx")) {
		if (tag.getName().equals("packs")) {
			for (net.sekien.hbt.HBTTag packentry : ((net.sekien.hbt.HBTCompound) tag)) {
				if (packentry instanceof HBTFlag) {
					if (((HBTFlag) packentry).isTrue()) {
						packs.add("pack/"+packentry.getName());
					}
				}
			}
		}
	}

	data=new net.sekien.hbt.HBTCompound("data");
	for (String pack : packs) {
		for (File f : getAllFiles(new File(pack+"/"))) {
			if (f.getName().matches(".*\\.hbt(|x|c)"))
				try {
					for (net.sekien.hbt.HBTTag tag : readHBTFile(f.getPath())) {
						data.addTag(tag);
					}
				} catch (IOException e) {
					Log.error(e.getLocalizedMessage()+" in file "+f.getPath()+"");
				}
		}
	}
}

private static List<File> getAllFiles(File parent) {
	ArrayList<File> ret=new ArrayList<File>();
	for (File f : parent.listFiles()) {
		if (f.isFile()) {
			ret.add(f);
		} else {
			ret.addAll(getAllFiles(f));
		}
	}
	return ret;
}

/**
 * Reads a HBT matching name
 *
 * @return array of HBTCompounds.
 */
public static List<net.sekien.hbt.HBTTag> readHBT(String name, boolean dataFile) throws IOException {
	List<String> paths=parseFileName(name, ResourceType.HBT.getExtensions(), dataFile);
	List<net.sekien.hbt.HBTTag> out=new ArrayList<net.sekien.hbt.HBTTag>();
	if (!dataFile) {
		for (String path : paths) {
			out.addAll(readHBTFile(path));
		}
	} else {
		out.addAll(readHBTFile(paths.get(0)));
	}
	return out;
}

/** Returns the HBTTag at name */
public static net.sekien.hbt.HBTTag getTag(String name) throws net.sekien.hbt.HBTCompound.TagNotFoundException {
	return data.getTag(name);
}

public static net.sekien.hbt.HBTCompound getCompound(String name) {
	try {return (net.sekien.hbt.HBTCompound) getTag(name);} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return new net.sekien.hbt.HBTCompound(name);
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return new net.sekien.hbt.HBTCompound(name);}
}

public static byte getByte(String name, byte def) {
	try {return ((net.sekien.hbt.HBTByte) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public static short getShort(String name, short def) {
	try {return ((net.sekien.hbt.HBTShort) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public static int getInt(String name, int def) {
	try {return ((net.sekien.hbt.HBTInt) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public static long getLong(String name, long def) {
	try {return ((net.sekien.hbt.HBTLong) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public static float getFloat(String name, float def) {
	try {return ((net.sekien.hbt.HBTFloat) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public static double getDouble(String name, double def) {
	try {return ((net.sekien.hbt.HBTDouble) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public static String getString(String name, String def) {
	try {return ((net.sekien.hbt.HBTString) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public static byte[] getByteArray(String name, byte[] def) {
	try {return ((net.sekien.hbt.HBTByteArray) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public static HBTFlag getFlag(String name, String def) {
	try {return (HBTFlag) getTag(name);} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return new HBTFlag(name, def);
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return new HBTFlag(name, def);}
}

/**
 * Returns all root-level HBTCompounds within a HBT,HBTC or HBTX file.
 *
 * @param path
 * 		Path to the file
 * @return ArrayList of root-level HBTTags found in the file.
 * @throws IOException
 */
public static List<net.sekien.hbt.HBTTag> readHBTFile(String path) throws IOException {
	List<net.sekien.hbt.HBTTag> ret=new ArrayList<net.sekien.hbt.HBTTag>();
	String extension=path.substring(path.lastIndexOf('.')+1);
	if (extension.equals("hbt")||extension.equals("hbtc")) {
		net.sekien.hbt.HBTInputStream inputStream=new net.sekien.hbt.HBTInputStream(new FileInputStream(path), extension.equals("hbtc"));
		while (true) {
			net.sekien.hbt.HBTTag tag=inputStream.read();
			if (tag!=null)
				ret.add(tag);
			else
				break;
		}
		inputStream.close();
	} else if (extension.equals("hbtx")) {
		StringBuilder out=new StringBuilder();
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		String l;
		while ((l=br.readLine())!=null) {
			out.append(l+"\n");
		}
		br.close();
		ret.addAll(parseTextHBT(out.toString()));
	} else {
		throw new IOException("File format "+extension+" unrecognised.");
	}
	return ret;
}

public static List<net.sekien.hbt.HBTTag> parseTextHBT(String out) throws IOException {
	Stack<net.sekien.hbt.HBTCompound> compoundStack=new Stack<net.sekien.hbt.HBTCompound>();
	compoundStack.push(new net.sekien.hbt.HBTCompound("master"));
	for (String s : out.split("\n")) {
		s=s.trim();
		String name=null;
		try {
			name=s.split("=", 2)[0].split("\\s+", 3)[1];
		} catch (Exception e) {
		}
		if (s.matches("[a-zA-Z0-9_]+ ?\\{")) {
			net.sekien.hbt.HBTCompound compound=new net.sekien.hbt.HBTCompound(s.split("\\{", 2)[0].trim());
			compoundStack.push(compound);
		} else if (s.matches("byte [a-zA-Z0-9_]+\\s*=\\s*-?[0-9]+")) { //byte name = 64
			compoundStack.peek().addTag(new net.sekien.hbt.HBTByte(name, (byte) Short.parseShort(s.split("=", 2)[1].trim())));
		} else if (s.matches("byte [a-zA-Z0-9_]+\\s*=\\s*0x[0-9A-Fa-f][0-9A-Fa-f]")) {
			compoundStack.peek().addTag(new net.sekien.hbt.HBTByte(name, (byte) Short.parseShort(s.split("=", 2)[1].trim().substring(2), 16)));
		} else if (s.matches("flag [a-zA-Z0-9_]+\\s*=\\s*(TRUE|FALSE|NEUTRAL|EARTH|WATER|FIRE|AIR|VOID)")) { //byte name = 64
			compoundStack.peek().addTag(new HBTFlag(name, s.split("=", 2)[1].trim()));
		} else if (s.matches("short [a-zA-Z0-9_]+\\s*=\\s*-?[0-9]+")) {
			compoundStack.peek().addTag(new net.sekien.hbt.HBTShort(name, Short.parseShort(s.split("=", 2)[1].trim())));
		} else if (s.matches("int [a-zA-Z0-9_]+\\s*=\\s*-?[0-9]+")) {
			compoundStack.peek().addTag(new net.sekien.hbt.HBTInt(name, Integer.parseInt(s.split("=", 2)[1].trim())));
		} else if (s.matches("long [a-zA-Z0-9_]+\\s*=\\s*-?[0-9]+")) {
			compoundStack.peek().addTag(new net.sekien.hbt.HBTLong(name, Long.parseLong(s.split("=", 2)[1].trim())));
		} else if (s.matches("float [a-zA-Z0-9_]+\\s*=\\s*-?[0-9]+(.[0-9]+)?([Ee]?-?[0-9]+)?")) {
			compoundStack.peek().addTag(new net.sekien.hbt.HBTFloat(name, Float.parseFloat(s.split("=", 2)[1].trim())));
		} else if (s.matches("double [a-zA-Z0-9_]+\\s*=\\s*-?[0-9]+(.[0-9]+)?([Ee]?-?[0-9]+)?")) {
			compoundStack.peek().addTag(new net.sekien.hbt.HBTDouble(name, Double.parseDouble(s.split("=", 2)[1].trim())));
		} else if (s.matches("data [a-zA-Z0-9_]+\\s*=\\s*([0-9A-Fa-f][0-9A-Fa-f])+")) {
			compoundStack.peek().addTag(new net.sekien.hbt.HBTByteArray(name, parseByteArray(s.split("=", 2)[1].trim())));
		} else if (s.matches("string [a-zA-Z0-9_]+\\s*=\\s*\"[^\"]*\"")) {
			String str=s.split("=", 2)[1].trim();
			compoundStack.peek().addTag(new net.sekien.hbt.HBTString(name, str.substring(1, str.length()-1)));
		} else if (s.matches("//.*")) {
			compoundStack.peek().addTag(new HBTComment(s.substring(2)));
		} else if (s.equals("}")) {
			if (compoundStack.size()>1) {
				net.sekien.hbt.HBTCompound compound=compoundStack.pop();
				compoundStack.peek().addTag(compound);
			} else {
				throw new IOException("Unmatched '}'");
			}
		} else {
			throw new IOException("Unrecognised tag type '"+s+"'.");
		}
	}
	if (compoundStack.size()!=1) {
		throw new IOException("Unmatched '{'");
	}
	return compoundStack.peek().getData();
}

private static byte[] parseByteArray(String str) {
	ArrayList<String> parts=new ArrayList<String>();
	Character p=null;
	for (char c : str.toCharArray()) {
		if (p!=null) {
			parts.add(""+p+""+c);
			p=null;
		} else {
			p=c;
		}
	}
	byte[] ret=new byte[parts.size()];
	for (int i=0; i<parts.size(); i++) {
		ret[i]=(byte) Short.parseShort(parts.get(i), 16);
	}
	return ret;
}

/**
 * Reads a text formatted map from a file
 * <p/>
 * Reads a map from a file. The map must be formated as such: KEY,VALUE KEY,VALUE ...
 *
 * @return A map
 */
public static Map<String, String> readMap(String name, boolean dataFile) throws IOException {
	List<String> paths=parseFileName(name, new String[]{}, dataFile);
	Map<String, String> map=new HashMap<String, String>();
	for (String path : paths) {
		File file=new File(path);
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String l;
		while ((l=br.readLine())!=null) {
			String[] args=l.split("[,]", 2);
			if (args.length!=2) continue;
			String p=args[0];
			String b=args[1];
			map.put(p, b);
		}
		br.close();
	}
	return map;
}

/**
 * Writes a text formatted map to a file
 * <p/>
 * Writes a map to a file. See {@link #readMap(String, boolean)} for how the file is formatted.
 *
 * @param file
 * 		The file to write to.
 * @param map
 * 		The map to write.
 * @return A map
 */
public static void writeMap(File file, Map<String, String> map) throws IOException {
	try {
		BufferedWriter bw=new BufferedWriter(new FileWriter(file));
		for (String k : map.keySet()) {
			bw.write(k+","+map.get(k));
			bw.newLine();
		}
		bw.flush();
		bw.close();
	} catch (Exception e) {
		Main.handleCrash(e);
		System.exit(1);
	}
}

public static Image getImage(String s) throws SlickException {
	return new Image(FileHandler.parse(s, ResourceType.IMAGE), false, 0);
}

public static Image getImageBlurry(String s) throws SlickException {
	return new Image(FileHandler.parse(s, ResourceType.IMAGE), false, 1);
}

public static List<File> getDataFolderContents(String name) {
	ArrayList<File> ret=new ArrayList<File>();
	for (String folder : parseFileName(name, new String[]{""}, true)) {
		ret.addAll(getAllFiles(new File(folder)));
	}
	return ret;
}
}
