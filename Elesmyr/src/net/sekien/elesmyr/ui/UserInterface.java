/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.ui;

import net.sekien.elesmyr.msgsys.MessageEndPoint;
import net.sekien.elesmyr.player.Camera;
import net.sekien.elesmyr.system.GameClient;
import net.sekien.pepper.Renderer;
import org.newdawn.slick.*;

public interface UserInterface {
public void ctor(String extd);

public boolean inited();

public void init(GameContainer gc, MessageEndPoint receiver) throws SlickException;

public void render(Renderer renderer, Camera cam, GameClient receiver) throws SlickException;

public void update(GameContainer gc, GameClient receiver);

public boolean blockUpdates();
}
