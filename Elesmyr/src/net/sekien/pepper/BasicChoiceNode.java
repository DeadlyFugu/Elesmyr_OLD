package net.sekien.pepper;

/**
 * Created with IntelliJ IDEA. User: matt Date: 21/04/13 Time: 4:20 PM To change this template use File | Settings |
 * File Templates.
 */
public class BasicChoiceNode extends MultiChoiceButtonNode {
@Override
protected void onSelect(int sel) {
	//Does nothing.
}

public String getValue() {
	return options[sel];
}

protected BasicChoiceNode(String name, String message, String[] options) {
	super(name, message, options);
}
}
