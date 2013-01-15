package net.halitesoft.lote;

public class ScriptObject {
	private String name;
	private String initVar;
	private Object master;
	
	public ScriptObject(String name, String initVar, GameElement master) {
		this.name = name;
		this.initVar = initVar;
		this.master=master;
	}
	
	public ScriptObject(String name, String initVar, MessageReceiver master) {
		this.name = name;
		this.initVar = initVar;
		this.master=master;
	}
	
	public void call(String func, boolean client, String initVarExt, MessageReceiver receiver) {
		String ret = ScriptRunner.run(func, name, receiver, initVar+" CLIENT="+client+" SERVER="+!client+initVarExt,(master instanceof GameElement)?(GameElement)master:null);
		if (!ret.startsWith("F")) {
			initVar = ret.substring(1);
		}
		if (master instanceof GameElement)
			((GameElement) this.master).receiveMessage(new Message("master.setInitVar",initVar), receiver);
		else
			((MessageReceiver) this.master).receiveMessage(new Message("master.setInitVar",initVar));
	}
	
	public void receiveMessage(Message msg, MessageReceiver receiver) {
		if (msg.getName().equals("placeholder")) {
			//placeholdin'
		} else {
			call("receiveMsg",!receiver.isServer()," MSGNAME="+msg.getName()+" MSGDATA="+msg.getData(),receiver);
		}
	}

	public void putVariable(String var) {
		initVar=initVar+" "+var;
	}

	public String getVar(String var) {
		for (String s : initVar.split(" ")) {
			if (s.startsWith(var+"="))
				return s.split("=",2)[1];
		}
		return null;
	}
}
