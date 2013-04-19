package net.sekien.hbt;

import net.sekien.elesmyr.util.FileHandler;

import java.io.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 15/04/13 Time: 10:34 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTEditor {
private static final PrintStream outs = System.out;
private static final PrintStream errs = System.err;
private static final BufferedReader ins = new BufferedReader(new InputStreamReader(System.in));
private static final HBTCompound root = new HBTCompound("root");
private static String path = "";

public static void main(String[] args) {
	puts("HBT Commandline editor 0.2\n");
	while (true) {
		puts(path+"$ ");
		String instr = gets();
		parse(instr);
	}
}

private static void parse(String instr) {
	String func = instr.split(" ", 2)[0];
	String arg = "";
	try {
		arg = instr.split(" ", 2)[1];
	} catch (ArrayIndexOutOfBoundsException ignored) {}
	if (func.equals("cd")) {
		try {
			HBTTag tag = null;
			String newPath = null;
			if (arg.equals("")) {
				tag = getCurrentTag();
				newPath = path;
			} else if (arg.equals(".")) {
				tag = root;
				newPath = "";
			} else if (arg.startsWith(".")) {
				tag = root.getTag(arg.substring(1));
				newPath = arg.substring(1);
			} else {
				tag = getCurrentTag().getTag(arg);
				newPath = (path.equals("")?"":path+".")+arg;
			}
			if (tag instanceof HBTCompound) {
				path = newPath;
			} else {
				error("Not a compound.");
			}
		} catch (HBTCompound.TagNotFoundException e) {
			error("Can not find tag '"+e.getLocalizedMessage()+"'.");
		}
	} else if (func.equals("ls")) {
		if (resolveArgPath(arg)!=null)
			for (HBTTag tag : resolveArgPath(arg)) {
				puts(tag.getClass().getName().substring("net.sekien.hbt.HBT".length()).toLowerCase()+" "+
						     tag.getName()+(tag instanceof HBTCompound?" {...}":
								                    (tag instanceof HBTComment?"":" ="+tag.toString().split("=")[1]))+"\n");
			}
	} else if (func.equals("print")) {
		if (resolveArgPath(arg)!=null)
			puts(resolveArgPath(arg).toString()+"\n");
	} else if (func.equals("read")) {
		if (new File(arg).exists()) {
			try {
				for (HBTTag tag : FileHandler.readHBTFile(arg)) {
					getCurrentTag().addTag(tag);
				}
			} catch (IOException e) {
				error(e.getLocalizedMessage());
			}
		} else {
			error("File not found.");
		}
	} else if (func.equals("write")) {
		try {
			HBTOutputStream out = new HBTOutputStream(new FileOutputStream(arg), false);
			for (HBTTag tag : getCurrentTag()) {
				out.write(tag);
			}
			out.close();
		} catch (IOException e) {
			error(e.getLocalizedMessage());
		}
	} else if (func.equals("set")) {
		try {
			for (HBTTag tag : FileHandler.parseTextHBT(arg))
				getCurrentTag().setTag(tag);
		} catch (IOException e) {
			error(e.getLocalizedMessage());
		}
	} else if (func.equals("mkdir")) {
		if (arg.matches("[0-9A-Za-z_]+"))
			if (!getCurrentTag().hasTag(arg))
				getCurrentTag().addTag(new HBTCompound(arg));
			else
				error("Tag already exists.");
		else
			error("Invalid name.");
	} else if (func.equals("get")) {
		puts(resolveArgPathTag(arg).toString().split("= ")[1]+"\n");
	} else if (func.equals("rm")) {
		try {
			String str;
			if (arg.equals("")) {str = path;} else if (arg.equals(".")) {str = "";} else if (arg.startsWith(".")) {
				str = arg.substring(1);
			} else {str = (path.equals("")?"":path+".")+arg;}
			HBTCompound parent = root;
			String child;
			if (str.contains(".")) {
				int len = str.lastIndexOf('.');
				parent = (HBTCompound) root.getTag(str.substring(0, len));
				child = str.substring(len+1);
			} else {
				child = str;
			}
			parent.deleteTag(child);
		} catch (HBTCompound.TagNotFoundException e) {
			error("Can not find tag '"+e.getLocalizedMessage()+"'.");
		} catch (ClassCastException e) {
			error("Can not find tag '"+e.getLocalizedMessage()+"'.");
		}
	} else if (func.equals("help")) {
		puts(
				    "Help: (Note that, for these explaination, the words \"Directory\" and \"Compound\" are used interchangeably)\n"+
						    "    cd [dir]        Changes current directory.\n"+
						    "    ls [dir]        Lists all tags in a directory.\n"+
						    "    print [tag]     Prints the tag in HBTX syntax.\n"+
						    "    read <file>     Reads a HBT/X file on the harddrive.\n"+
						    "    write <file>    Write to a HBT file on the harddrive.\n"+
						    "    get <tag>       Prints the value of a tag.\n"+
						    "    set <HBTX>      Adds a tag to the current directory. Tag is formatted with HBTX syntax.\n"+
						    "    mkdir <name>    Adds a compound to the current directory.\n"+
						    "    rm [tag]        Deletes a tag from the file system.\n"+
						    "    help            Displays this help information.\n"+
						    "    exit            Closes HBTEditor.\n"
		);
	} else if (func.equals("exit")) {
		System.exit(1);
	} else {
		error("Unknown command.");
	}
}

private static HBTTag resolveArgPathTag(String arg) {
	try {
		if (arg.equals("")) return getCurrentTag();
		else if (arg.equals(".")) return root;
		else if (arg.startsWith(".")) return root.getTag(arg.substring(1));
		else return getCurrentTag().getTag(arg);
	} catch (HBTCompound.TagNotFoundException e) {
		error("Can not find tag '"+e.getLocalizedMessage()+"'.");
		return null;
	}
}

private static HBTCompound resolveArgPath(String arg) {
	try {
		if (arg.equals("")) return getCurrentTag();
		else if (arg.equals(".")) return root;
		else if (arg.startsWith(".")) return (HBTCompound) root.getTag(arg.substring(1));
		else return (HBTCompound) getCurrentTag().getTag(arg);
	} catch (HBTCompound.TagNotFoundException e) {
		error("Can not find tag '"+e.getLocalizedMessage()+"'.");
		return null;
	} catch (ClassCastException e) {
		error("Not a compound.");
		return null;
	}
}

private static HBTCompound getCurrentTag() {
	return (HBTCompound) (path.equals("")?root:root.getTag(path));
}

public static void puts(String out) {
	outs.print(out);
}

public static String gets() {
	try {
		return ins.readLine();
	} catch (IOException e) {
		e.printStackTrace();
	}
	return "";
}

public static void error(String error) {
	outs.print("ERROR: "+error+"\n");
}
}