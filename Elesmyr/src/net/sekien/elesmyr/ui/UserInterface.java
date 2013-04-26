package net.sekien.elesmyr.ui;

import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.GameClient;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public interface UserInterface {
public void ctor(String extd);

public boolean inited();

public void init(GameContainer gc, MessageEndPoint receiver) throws SlickException;

public void render(GameContainer gc, Graphics g, Camera cam, GameClient receiver) throws SlickException;

public void update(GameContainer gc, GameClient receiver);

public boolean blockUpdates();
}
