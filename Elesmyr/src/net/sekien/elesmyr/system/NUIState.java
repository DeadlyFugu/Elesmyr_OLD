/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.system;

import net.sekien.pepper.*;
import net.sekien.pepper.IntroState;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

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
		StateManager.init(gameContainer);
		Node main = new ListNode("Main");
		//main.addChild(new ImageNode("logo","ui.elesmyr"));
		main.addChild(new CommandButtonNode("singleplayer", "#menu.singleplayer", "STATE Saves"));
		main.addChild(new CommandButtonNode("multiplayer", "#menu.join", "STATE Join"));
		main.addChild(new CommandButtonNode("packs", "#menu.packs", "STATE Packs"));
		main.addChild(new CommandButtonNode("options", "#menu.settings", "STATE Options"));
		main.addChild(new CommandButtonNode("exit", "#menu.exit", "BACK"));
		StateManager.registerState(main);

		StateManager.registerState(new SaveSelectState("Saves"));
		StateManager.registerState(new NewSaveNode("NewSave"));

		StateManager.registerState(new PackSelectState("Packs"));

		Node options = new ListNode("Options");
		options.addChild(new GlobalsSetNode("debug", "#menu.debug", "debug", new String[]{"#true", "#false"}, new String[]{"true", "false"}));
		options.addChild(new GlobalsSetNode("vsync", "#menu.vsync", "vsync", new String[]{"#true", "#false"}, new String[]{"true", "false"}));
		options.addChild(new GlobalsSetNode("lres", "#menu.lres", "lres", new String[]{"6", "12", "18", "24", "36", "48"}, new String[]{"6", "12", "18", "24", "36", "48"}));
		options.addChild(new GlobalsEnumNode("lang", "#menu.lang", "lang", "EN_US", false, FontRenderer.Language.class));
		options.addChild(new CommandButtonNode("old", "Old Menu", "MAINMENU"));
		StateManager.registerState(options);

		StateManager.registerState(new GameClientState("GameClient"));

		StateManager.registerState(new JoinState("Join"));

		StateManager.registerState(new IntroState("Intro"));

		StateManager.setBackground(Globals.get("lastSave", ""));

		StateManager.setStateInitial(Globals.get("showIntro", true)?"Intro":"Main");
	}

	@Override
	public void enter(GameContainer gameContainer, StateBasedGame stateBasedGame) {
		StateManager.setBackground(Globals.get("lastSave", ""));
	}

	@Override
	public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
		StateManager.render(gameContainer, graphics);
	}

	@Override
	public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
		StateManager.update(gameContainer);
	}
}
