package net.shard.lote.ui;


import net.shard.lote.MessageReceiver;
import net.shard.lote.system.Camera;
import net.shard.lote.system.GameClient;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public interface UserInterface {
	public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver) throws SlickException;
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g, Camera cam, GameClient receiver) throws SlickException;
	public void update(GameContainer gc, StateBasedGame sbg, GameClient receiver);
	public boolean blockUpdates();
}
