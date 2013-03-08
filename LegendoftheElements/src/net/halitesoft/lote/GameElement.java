package net.halitesoft.lote;

import net.halitesoft.lote.msgsys.Message;
import net.halitesoft.lote.msgsys.MessageReceiver;
import net.halitesoft.lote.player.Camera;
import net.halitesoft.lote.system.GameClient;
import net.halitesoft.lote.system.GameServer;
import net.halitesoft.lote.world.Region;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public interface GameElement {

/**
 * Initialization. Use for loading resources or setting variables
 *
 * @throws SlickException
 */
public void init(GameContainer gc, StateBasedGame sbg, MessageReceiver receiver) throws SlickException;

/**
 * Load this object from a save file
 *
 * @param save
 * 		The Save object to load from
 */
public void load(Save save);

/**
 * Client-side rendering.
 *
 * @throws SlickException
 */
public void render(GameContainer gc, StateBasedGame sbg, Graphics g, Camera cam, GameClient receiver) throws SlickException;

/** Server-side updating */
public void update(Region region, GameServer receiver);

/** Client-side updating */
public void clientUpdate(GameContainer gc, StateBasedGame sbg, GameClient receiver);

/**
 * Receive a message from the server (or client)
 *
 * @param msg
 * 		String containing the message
 */
public void receiveMessage(Message msg, MessageReceiver receiver);

/** Save data Server-side */
public void save(Save save);

public String getReceiverName();
}
