package net.halitesoft.lote;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.system.Main;

import com.esotericsoftware.minlog.Log;

public class ScriptRunner {
	static HashMap<String,HashMap<String,String[]>> functions; //<Namespace<Name,Code>>
	
	public static void init() {
		functions = new HashMap<String,HashMap<String,String[]>>();
		functions.put("global", new HashMap<String,String[]>());
		
		ArrayList<File> temp = new ArrayList<File>();
		File[] fileList = new File("data/scr").listFiles();

		for (int i = 0; i < fileList.length; i++) {
			File choose = fileList[i];
			if (choose.isFile() && !temp.contains(choose)) {
				temp.add(choose);
			}
		}
		
		for (File f : temp) {
			readFile(f);
		}
		
		run("init.vm","init",null,"",null);
	}
	
	private static void readFile(File file) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String fname = "";
			String namespace = "global";
			String out = "";
			String l = "";
			while((l = br.readLine()) != null) {
				if (l.startsWith("namespace")) {
					if (!l.contains(" ")) {
						Log.error(file.getName()+": Expected namespace name after 'namespace'");
						break;
					}
					if (!fname.equals("")) {
						if (!functions.containsKey(namespace))
							functions.put(namespace,new HashMap<String,String[]>());
						functions.get(namespace).put(fname, out.split("\n"));
					}
					namespace = l.split(" ",2)[1];
					out = "";
				} else if (l.startsWith("func")){
					if (!l.contains(" ")) {
						Log.error(file.getName()+": Expected function name after 'func'");
						break;
					}
					if (!fname.equals("")) {
						if (!functions.containsKey(namespace))
							functions.put(namespace,new HashMap<String,String[]>());
						functions.get(namespace).put(fname, out.split("\n"));
					}
					fname = l.split(" ",2)[1];
					out = "";
				} else {
					out = out + l.trim() + "\n";
				}
			}
			if (!fname.equals("")) {
				if (!functions.containsKey(namespace))
					functions.put(namespace,new HashMap<String,String[]>());
				functions.get(namespace).put(fname, out.split("\n"));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static String run(String in, String callns, MessageReceiver receiver, String initVar, GameElement master) {
		try {
		String fname = "";
		String fargs = "";
		in=in.trim().replaceAll("\\s+", " "); //Replace whitespace with ' '.
		if (in.contains(" ")) {
			String[] inparts = in.split(" ",2);
			fname = inparts[0];
			fargs = inparts[1];
		} else {
			fname = in;
		}
		
		//Check if system function
		if (fname.equals("print")) {
			Log.info(fargs);
			return "T"+initVar;
		} else if (fname.equals("cmsg")) {
			Message msg = new Message(fargs.split(":",2)[0],fargs.split(":",2)[1]);
				if (fargs.contains("|")) {
					String argmsg=fargs.split("|")[1];
					MessageSystem.sendClient(master,Integer.decode(fargs.split("|")[0]), new Message(argmsg.split(":",2)[0],argmsg.split(":",2)[1]),false);
				} else if (receiver instanceof GameClient) {
					if (master != null)
						msg.setSender(master.getReceiverName());
					MessageSystem.receiveClient(msg);
				}
			return "T"+initVar;
		} else if (fname.equals("smsg")) {
			Message msg = new Message(fargs.split(":",2)[0],fargs.split(":",2)[1]);
			MessageSystem.sendServer(master,msg,false);
			return "T"+initVar;
		} else if (fname.equals("set")) {
			Main.globals.put(fargs.split("=",2)[0],fargs.split("=",2)[1]);
		}
		
		//Find function
		String namespace = "global";
		String[] fcode = null;
		if (fname.contains(".")) {
			fcode = functions.get(fname.split("\\.",2)[0]).get(fname.split("\\.",2)[1]);
			namespace = fname.split("\\.",2)[0];
		} else if (functions.get(callns).containsKey(fname)) {
			fcode = functions.get(callns).get(fname);
			namespace = callns;
		} else if (functions.get("global").containsKey(fname)) {
			fcode = functions.get("global").get(fname);
			namespace = "global";
		}
		
		if (fcode == null) {
			Log.error("Function could not be found "+fname);
			return "F"+initVar;
		}
		
		//Execute function
		int ln = 0;
		HashMap<String,String> localVar = new HashMap<String,String>();
		if (!initVar.equals(""))
			for (String s : initVar.split(" ")) {
				if (s.contains("="))
					localVar.put(s.split("=",2)[0],s.split("=",2)[1]);
				else
					Log.warn(fname+": initVar contained invalid entry '"+s+"'.");
			}
		localVar.put("ARG", parseLine(fargs,localVar, namespace,receiver,master));
		int skiplvl = 0;
		while (true) {
			String l = fcode[ln];
			if (skiplvl!=0) {
				if (l.trim().equals("end"))
					skiplvl--;
				if (l.startsWith("if "))
					skiplvl++;
				if (l.trim().equals("else")&&skiplvl==1)
					skiplvl=0;
			} else if (l.trim().equals("else")) {
				skiplvl=1;
			} else {
				l=parseLine(l,localVar, namespace,receiver,master);

				if (l.startsWith("def ")) {
					String cargs = l.split(" ",2)[1];
					if (cargs.contains("=")||cargs.contains(" ")) {
						String[] parts = cargs.split("[= ]",2);
						localVar.put(parts[0],parts[1]);
					} else {
						Log.warn("def called with invalid argument "+cargs);
					}
				} else if (l.startsWith("solve ")) {
					String[] parts = l.split(" ",3);
					localVar.put(parts[1], parseIntExpr(parts[2]));
				} else if (l.startsWith("if ")) {
					if (!Boolean.parseBoolean(parseBoolExpr(l.split(" ",2)[1])))
						skiplvl=1;
				} else if (l.equals("end") || l.equals("")) { //Ignore end commands
				} else {
					run(l,namespace,receiver,makeInitVarStr(localVar),master);
				}
			}
			ln++;
			if (ln>=fcode.length)
				break;
		}
		return "T"+makeInitVarStrRet(localVar);
		} catch (Exception e) {
			Log.error("ScriptRunner.run("+in+",...) caught exception",e);
			return "F"+initVar;
		}
	}

	private static String parseIntExpr(String in) {
		float out = 0;
		char p = '+';
		String read = "";
		for (char c : (in+"+").toCharArray()) {
			if ((""+c).matches("[\\+\\-\\*/%^]")&&!(c=='-'&&read.equals(""))) {
				if (p=='+') {
					out=out+Float.parseFloat(read);
				} else if (p=='-') {
					out=out-Float.parseFloat(read);
				} else if (p=='*') {
					out=out*Float.parseFloat(read);
				} else if (p=='/') {
					out=out/Float.parseFloat(read);
				} else if (p=='%') {
					out=out%Float.parseFloat(read);
				} else if (p=='^') {
					out=(float) Math.pow(out,Float.parseFloat(read));
				}
				read="";
				p=c;
			} else {
				read=read.concat(""+c);
			}
		}
		return ""+out;
	}
	
	private static String parseBoolExpr(String in) {
		if (!in.matches(".*(!=|<=|>=|=|<|>|&|\\|).*")) {
			if (in.matches(".*(true|false)")) {
			if (in.startsWith("!")) {
				return ""+!Boolean.parseBoolean(in.substring(1));
			}
			return ""+Boolean.parseBoolean(in);
			} else {
				return in;
			}
		}
		String[] parts = in.split("(!=|<=|>=|=|<|>|&|\\|)",2);
		parts[0]=(""+parseBoolExpr(parts[0])).trim();
		parts[1]=(""+parseBoolExpr(parts[1])).trim();
		Matcher matcher = Pattern.compile("(!=|<=|>=|=|<|>|&|\\|)").matcher(in);
		matcher.find();
		String op = in.substring(matcher.start());
		if (op.startsWith("=")) {
			return ""+parts[0].equalsIgnoreCase(parts[1]);
		} else if (op.startsWith("!=")) {
			return ""+!parts[0].equalsIgnoreCase(parts[1]);
		} else if (op.startsWith("&")) {
			return ""+(Boolean.parseBoolean(parts[0])&&Boolean.parseBoolean(parts[1]));
		} else if (op.startsWith("|")) {
			return ""+(Boolean.parseBoolean(parts[0])||Boolean.parseBoolean(parts[1]));
		} else if (op.startsWith("<=")) {
			return ""+(Float.parseFloat(parts[0])<=Float.parseFloat(parts[1]));
		} else if (op.startsWith(">=")) {
			return ""+(Float.parseFloat(parts[0])>=Float.parseFloat(parts[1]));
		} else if (op.startsWith("<")) {
			return ""+(Float.parseFloat(parts[0])<Float.parseFloat(parts[1]));
		} else if (op.startsWith(">")) {
			return ""+(Float.parseFloat(parts[0])>Float.parseFloat(parts[1]));
		}
		return "false";
	}

	private static String simplify(String out) {
		try {
			return ""+Float.valueOf(out);
		} catch (Exception e) {};
		return out;
	}

	private static String makeInitVarStr(HashMap<String, String> localVar) {
		String out = "";
		for (Entry<String,String> e : localVar.entrySet())
			out=out+" "+e.getKey()+"="+e.getValue();
		if (out.equals(""))
			return "";
		return out.substring(1);
	}
	
	private static String makeInitVarStrRet(HashMap<String, String> localVar) {
		String out = "";
		for (Entry<String,String> e : localVar.entrySet())
			if (!e.getKey().matches("(CLIENT|SERVER|ARG)")) //Put variables to ignore in here
				out=out+" "+e.getKey()+"="+e.getValue();
		if (out.equals(""))
			return "";
		return out.substring(1);
	}

	private static String parseLine(String l, HashMap<String,String> localVar, String namespace, MessageReceiver receiver, GameElement master) {
		if (l.contains("$")) {
			for (Entry<String, String> e : localVar.entrySet()) {
				if (l.matches(".*\\$"+e.getKey()+"\\[.*\\].*")) {
					Matcher matcher = Pattern.compile("\\$"+e.getKey()+"\\[([0-9]+|\\$[a-zA-Z]+)\\]").matcher(l);
					while(matcher.find()) {
						String arrayname = l.substring(matcher.start(),matcher.end());
						l=l.replace(arrayname,e.getValue().split("\\|")[Integer.parseInt(parseLine(arrayname.split("[\\[\\]]")[1],localVar,namespace,receiver,master))]);
					}
				}
				l=l.replaceAll("\\$"+e.getKey(), e.getValue().replace("$", "\\$"));
			}
		}
		if (l.contains(" ")) {
		String[] lparts = l.split(" ",2)[1].split("\\|");
		for (String s : lparts) {
			if (s.startsWith("\\<")) {
				run(s.substring(1),namespace,receiver,makeInitVarStr(localVar),master);
			}
		}
		}
		return l;
	}
}
