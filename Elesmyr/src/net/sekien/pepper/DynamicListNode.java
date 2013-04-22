package net.sekien.pepper;

import org.newdawn.slick.GameContainer;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 3:38 PM To change this template use File | Settings |
 * File Templates.
 */
public abstract class DynamicListNode extends ListNode {
public DynamicListNode(String name) {super(name);}

int updTimer = 0;

@Override
public void update(GameContainer gc) {
	updTimer--;
	if (updTimer < 1) {
		updTimer = updateInterval();
		List<Node> newList = getList();
		if (!listsEquals(children, newList)) {
			children.clear();
			children.addAll(getList());
		}
	}
}

private boolean listsEquals(List<Node> a, List<Node> b) {
	if (a.size()!=b.size())
		return false;
	for (int i = 0, size = a.size(); i < size; i++) {
		if (!a.get(i).getName().equals(b.get(i).getName()))
			return false;
	}
	return true;
}

protected int updateInterval() {return 100;}

public abstract List<Node> getList();
}
