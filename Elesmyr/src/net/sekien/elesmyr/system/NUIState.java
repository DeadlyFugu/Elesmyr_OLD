package net.sekien.elesmyr.system;

import net.sekien.pepper.*;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/** Class for messing around with the new UI. */
public class NUIState extends BasicGameState {

int stateID = -1;

NUIState(int stateID) {
	this.stateID = stateID;
}

@Override
public int getID() {
	return stateID;
}

@Override
public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
	StateManager.init();
	Node main = new ListNode("Main");
	main.addChild(new ButtonNode("singleplayer", "Single Player", "STATE Saves"));
	main.addChild(new ButtonNode("multiplayer", "Join", "STATE Join"));
	main.addChild(new ButtonNode("options", "Options", "STATE Options"));
	main.addChild(new ButtonNode("old", "Old Menu", "MAINMENU"));
	main.addChild(new ButtonNode("exit", "Quit", "BACK"));
	StateManager.registerState(main);

	StateManager.registerState(new SaveSelectState("Saves"));

	Node options = new ListNode("Options");
	options.addChild(new GlobalsSetNode("debug", "Debug mode", "debug", new String[]{"Enabled", "Disabled"}, new String[]{"true", "false"}));
	options.addChild(new GlobalsSetNode("vsync", "Vertical Sync", "vsync", new String[]{"Enabled", "Disabled"}, new String[]{"true", "false"}));
	options.addChild(new GlobalsSetNode("lres", "Lightmap resolution", "lres", new String[]{"6", "12", "18", "24", "36", "48"}, new String[]{"6", "12", "18", "24", "36", "48"}));
	options.addChild(new GlobalsEnumNode("lang", "Language", "lang", "EN_US", false, FontRenderer.Language.class));
	options.addChild(new ButtonNode("done", "Done", "BACK"));
	StateManager.registerState(options);

	StateManager.setStateInitial("Main");
}

@Override
public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
	StateManager.render(gameContainer, graphics);
}

@Override
public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
	StateManager.update(gameContainer, stateBasedGame);
}
}
