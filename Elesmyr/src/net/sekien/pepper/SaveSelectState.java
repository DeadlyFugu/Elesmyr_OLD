package net.sekien.pepper;

import org.newdawn.slick.GameContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 3:42 PM To change this template use File | Settings |
 * File Templates.
 */
public class SaveSelectState extends DynamicListNode {
@Override
public List<Node> getList() {
	ArrayList<File> temp = new ArrayList<File>();
	File[] fileList = new File("save").listFiles();
	for (int i = 0; i < fileList.length; i++) {
		File choose = fileList[i];
		if (choose.isFile() && !temp.contains(choose) && choose.getName().endsWith(".hbt")) {
			temp.add(choose);
		}
	}
	ArrayList<Node> nodes = new ArrayList<Node>();
	nodes.add(new CommandButtonNode("new", "New Game", "STATE NewSave"));
	for (int i = 0; i < temp.size(); i++) {
		File file = temp.get(i);
		nodes.add(new CommandButtonNode("save_"+i, file.getName().split("\\.", 2)[0], "SAVE "+file.getName()));
	}
	return nodes;
}

private int prevSel = -1;

@Override
public void update(GameContainer gc) {
	updTimer--;
	if (updTimer < 1) {
		updTimer = updateInterval();
		children.clear();
		children.addAll(getList());
	}
	if (sel!=prevSel) {
		prevSel = sel;
		StateManager.setBackground(((CommandButtonNode) children.get(sel)).getMessage().split("\\.", 2)[0]);
	}
}

public SaveSelectState(String name) {
	super(name);
}
}
