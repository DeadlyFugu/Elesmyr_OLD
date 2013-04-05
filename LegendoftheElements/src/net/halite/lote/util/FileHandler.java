package net.halite.lote.util;

import net.halite.hbt.*;
import net.halite.lote.system.Main;

import java.io.*;
import java.util.*;

/**
 * Class to handle all file IO.
 * Also handles mods
 */
public class FileHandler {

private static final String[] HBT_EXTENSIONS={"hbtx","hbt","hbtc"};

private static HBTCompound data;
/**
 * Returns paths to all files matching 'name'
 * Examples:
 *   parseFileName("item_def",new String[] {""}) return {"data/item_def","mod/MoreWeapons/item_def"}
 *   parseFileName("ui.book",new String[] {"png"}) might return {"data/ui/book.png"}
 *   parseFileName("pass",new String[] {""}) might return {"pass"}
 *   parseFileName("SAVE",new String[] {"hbtx","hbt","hbtc"}) might return {"save/nameOfSave.hbt"} //Maybe don't implement?
 * @param name The name of the file
 * @param extension The acceptable extensions
 * @return The file's path
 */
private static List<String> parseFileName(String name, String[] extension) {
	List<String> found = new ArrayList<String>();
	File f;
	for (String s : extension) {
		f = new File("data/"+name.replaceAll("\\.","/")+"."+s);
		if (f.exists())
			found.add(f.getPath());
		f = new File(name.replaceAll("\\.","/")+"."+s);
		if (f.exists())
			found.add(f.getPath());
	}
	return found;
}

public static void readData() throws IOException {
	data = new HBTCompound("data");
	for (File f : getAllFiles(new File("data/"))) {
		if (f.getName().matches(".*\\.hbt(|x|c)"))
			try {
				for (HBTTag tag : readHBTFile(f.getPath())) {
					data.addTag(tag);
				}
			} catch (IOException e) {
				throw new IOException("in file "+f.getPath()+":",e);
			}
	}
}

private static List<File> getAllFiles(File parent) {
	ArrayList<File> ret = new ArrayList<File>();
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
 * @param name
 * @return array of HBTCompounds, Usually one per file matched.
 */
private static List<HBTTag> readHBT(String name) throws IOException {
	List<String> paths = parseFileName(name,HBT_EXTENSIONS);
	List<HBTTag> out = new ArrayList<HBTTag>();
	for (String path : paths) {
		out.addAll(readHBTFile(path));
	}
	return out;
}

/**
 * Returns the HBTTag at name
 */
public static HBTTag getTag(String name) throws HBTCompound.TagNotFoundException {
	return data.getTag(name);
}

/**
 * Returns all root-level HBTCompounds within a HBT,HBTC or HBTX file.
 * @param path Path to the file
 * @return ArrayList of root-level HBTTags found in the file.
 * @throws IOException
 */
private static List<HBTTag> readHBTFile(String path) throws IOException {
	List<HBTTag> ret = new ArrayList<HBTTag>();
	String extension = path.substring(path.lastIndexOf('.')+1);
	if (extension.equals("hbt") || extension.equals("hbtc")) {
		HBTInputStream inputStream = new HBTInputStream(new FileInputStream(path),extension.equals("hbtc"));
		while (true) {
			HBTTag tag = inputStream.read();
			if (tag!=null)
				ret.add(tag);
			else
				break;
		}
		inputStream.close();
	} else if (extension.equals("hbtx")) {
		StringBuilder out = new StringBuilder();
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

public static List<HBTTag> parseTextHBT(String out) throws IOException {
	Stack<HBTCompound> compoundStack = new Stack<HBTCompound>();
	compoundStack.push(new HBTCompound("master"));
	for(String s : out.split("\n")) {
		s=s.trim();
		String name = null;
		try {
		name = s.split("=",2)[0].split("\\s+",3)[1];
		} catch (Exception e) {
		}
		if (s.matches("[a-zA-Z0-9_]+ ?\\{")) {
			HBTCompound compound = new HBTCompound(s.split("\\{",2)[0].trim());
			compoundStack.push(compound);
		} else if (s.matches("byte [a-zA-Z0-9_]+\\s*=\\s*-?[0-9]+")) { //byte name = 64
			compoundStack.peek().addTag(new HBTByte(name,(byte) Short.parseShort(s.split("=",2)[1].trim())));
		} else if (s.matches("byte [a-zA-Z0-9_]+\\s*=\\s*0x[0-9A-Fa-f][0-9A-Fa-f]")) {
			compoundStack.peek().addTag(new HBTByte(name,(byte) Short.parseShort(s.split("=",2)[1].trim().substring(2),16)));
		} else if (s.matches("flag [a-zA-Z0-9_]+\\s*=\\s*(TRUE|FALSE|NEUTRAL|EARTH|WATER|FIRE|AIR|VOID)")) { //byte name = 64
			compoundStack.peek().addTag(new HBTFlag(name,s.split("=",2)[1].trim()));
		} else if (s.matches("short [a-zA-Z0-9_]+\\s*=\\s*-?[0-9]+")) {
			compoundStack.peek().addTag(new HBTShort(name,Short.parseShort(s.split("=", 2)[1].trim())));
		} else if (s.matches("int [a-zA-Z0-9_]+\\s*=\\s*-?[0-9]+")) {
			compoundStack.peek().addTag(new HBTInt(name,Integer.parseInt(s.split("=", 2)[1].trim())));
		} else if (s.matches("long [a-zA-Z0-9_]+\\s*=\\s*-?[0-9]+")) {
			compoundStack.peek().addTag(new HBTLong(name,Long.parseLong(s.split("=", 2)[1].trim())));
		} else if (s.matches("float [a-zA-Z0-9_]+\\s*=\\s*-?[0-9]+(.[0-9]+)?([Ee]?-?[0-9]+)?")) {
			compoundStack.peek().addTag(new HBTFloat(name,Float.parseFloat(s.split("=", 2)[1].trim())));
		} else if (s.matches("double [a-zA-Z0-9_]+\\s*=\\s*-?[0-9]+(.[0-9]+)?([Ee]?-?[0-9]+)?")) {
			compoundStack.peek().addTag(new HBTDouble(name,Double.parseDouble(s.split("=", 2)[1].trim())));
		} else if (s.matches("data [a-zA-Z0-9_]+\\s*=\\s*([0-9A-Fa-f][0-9A-Fa-f])+")) {
			compoundStack.peek().addTag(new HBTByteArray(name,parseByteArray(s.split("=", 2)[1].trim())));
		} else if (s.matches("string [a-zA-Z0-9_]+\\s*=\\s*\"[^\"]*\"")) {
			String str = s.split("=", 2)[1].trim();
			compoundStack.peek().addTag(new HBTString(name,str.substring(1,str.length()-1)));
		} else if (s.matches("//.*")) {
			compoundStack.peek().addTag(new HBTComment(s.substring(2)));
		} else if (s.equals("}")) {
			if (compoundStack.size()>1) {
			HBTCompound compound = compoundStack.pop();
			compoundStack.peek().addTag(compound);
			} else {
				throw new IOException("Unmatched '}'");
			}
		} else {
			throw new IOException("Unrecognised tag type '"+s+"'.");
		}
	}
	if (compoundStack.size() != 1) {
		throw new IOException("Unmatched '{'");
	}
	return compoundStack.peek().getData();
}

private static byte[] parseByteArray(String str) {
	ArrayList<String> parts = new ArrayList<String>();
	Character p = null;
	for (char c : str.toCharArray()) {
		if (p!=null) {
			parts.add(""+p+""+c);
			p=null;
		} else {
			p=c;
		}
	}
	byte[] ret = new byte[parts.size()];
	for (int i=0;i<parts.size();i++) {
		ret[i] = (byte) Short.parseShort(parts.get(i),16);
	}
	return ret;
}

/**
 * Reads a text formatted map from a file
 *
 * Reads a map from a file.
 * The map must be formated as such:
 *     KEY,VALUE
 *     KEY,VALUE
 *     ...
 *
 * @param name
 * @return A map
 */
public static Map<String,String> readMap(String name) throws IOException {
	List<String> paths = parseFileName(name,new String[] {});
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
 *
 * Writes a map to a file.
 * See {@link #readMap(String)} for how the file is formatted.
 *
 * @param file The file to write to.
 * @param map The map to write.
 * @return A map
 */
public static void writeMap(File file,Map<String,String> map) throws IOException {
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
}
