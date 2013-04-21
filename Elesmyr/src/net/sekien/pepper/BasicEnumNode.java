package net.sekien.pepper;

/**
 * Created with IntelliJ IDEA. User: matt Date: 21/04/13 Time: 4:17 PM To change this template use File | Settings |
 * File Templates.
 */
public class BasicEnumNode extends EnumNode {
@Override
protected void onSelect(int sel) {
	//Does nothing
}

public int getOrdinal() {
	return sel;
}

public Object getValue() {
	return options.getEnumConstants()[sel];
}

protected <E extends Enum<E>> BasicEnumNode(String name, String message, boolean ordinal, Class<E> options) {
	super(name, message, ordinal, options);
}
}
