package net.sekien.elesmyr.ui;

import net.sekien.elesmyr.msgsys.MessageReceiver;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.GameClient;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public interface UserInterface {
public void ctor(String extd);

public boolean inited();

public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver) throws SlickException;

public void render(GameContainer gc, StateBasedGame sbg, Graphics g, Camera cam, GameClient receiver) throws SlickException;

public void update(GameContainer gc, StateBasedGame sbg, GameClient receiver);

public boolean blockUpdates();
}
