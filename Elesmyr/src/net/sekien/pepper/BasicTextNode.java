package net.sekien.pepper;

/**
 * Created with IntelliJ IDEA. User: matt Date: 21/04/13 Time: 4:30 PM To change this template use File | Settings |
 * File Templates.
 */
public class BasicTextNode extends TextNode {
@Override
protected void onSelect() {
	//Do nothing
}

public String getValue() {
	return text;
}

protected BasicTextNode(String name, String message) {
	super(name, message);
}
}
