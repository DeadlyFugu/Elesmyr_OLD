package net.sekien.pepper;

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
	for (int i = 0; i < temp.size(); i++) {
		File file = temp.get(i);
		nodes.add(new ButtonNode("save_"+i, file.getName(), "SAVE "+file.getName()));
	}
	return nodes;
}

public SaveSelectState(String name) {
	super(name);
}
}
