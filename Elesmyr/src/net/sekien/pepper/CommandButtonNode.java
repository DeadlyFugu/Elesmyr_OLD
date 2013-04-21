package net.sekien.pepper;

/**
 * Created with IntelliJ IDEA. User: matt Date: 20/04/13 Time: 1:15 PM To change this template use File | Settings |
 * File Templates.
 */
public class CommandButtonNode extends AbstractButtonNode {

private final String action;

public CommandButtonNode(String name, String text, String action) {
	super(name, text);
	this.action = action;
}

@Override
public void onAction(Action enumAction) {
	if (enumAction==Action.SELECT) {
		String func = action;
		String arg = null;
		if (action.contains(" ")) {
			func = action.split(" ", 2)[0];
			arg = action.split(" ", 2)[1];
		}
		if (func.equals("STATE")) {
			if (arg!=null) {
				StateManager.setState(arg);
			} else {
				StateManager.error("Invalid action "+action, false);
			}
		} else if (func.equals("BACK")) {
			StateManager.back();
		} else if (func.equals("SAVE")) {
			if (arg!=null)
				StateManager.updFunc("SAVE "+arg);
			else
				StateManager.error("Invalid action "+action, false);
		} else if (func.equals("MAINMENU")) {
			StateManager.updFunc("MAINMENU");
		}
	}
}
}
