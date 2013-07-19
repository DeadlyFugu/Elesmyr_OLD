/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.system;

import com.esotericsoftware.minlog.Log;
import net.sekien.pepper.StateManager;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
import org.newdawn.slick.opengl.shader.*;

/**
 * Created with IntelliJ IDEA. User: matt Date: 25/05/13 Time: 4:53 PM To change this template use File | Settings |
 * File Templates.
 */
public class Renderer {

private static boolean useshaders = false;
private static ShaderProgram screen;
private static Image postImage;
private static Graphics postGraphics;
private static int frame = 0;
private static final String shadername = "base";

public static void init(GameContainer gameContainer) {
	if (!ShaderProgram.isSupported()) {
		Log.warn("Your GPU doesn't support GLSL shaders.");
	} else {
		try {
			postImage = Image.createOffscreenImage(Main.INTERNAL_RESX, Main.INTERNAL_RESY, Image.FILTER_NEAREST);
			postGraphics = postImage.getGraphics();

			// load our vertex and fragment shaders
			ShaderProgram.setStrictMode(false);
			final String VERT = "pack/core/shaders/"+shadername+".vert"; //TODO: Make it not use hard coded shader locations
			final String FRAG = "pack/core/shaders/"+shadername+".frag";
			screen = ShaderProgram.loadProgram(VERT, FRAG);
			useshaders = true;
			screen.bind();
			screen.setUniform1i("tex0", 0); //texture 0
			screen.setUniform2f("size", new Vector2f(Main.INTERNAL_RESX, Main.INTERNAL_RESY)); //size of tex
			screen.setUniform1i("frame", 0); //current frame
			ShaderProgram.unbindAll();
		} catch (SlickException e) {
			// there was a problem compiling our source! show the log
			e.printStackTrace();
		}
	}
}

public static void render(GameContainer gameContainer, Graphics g) {
	if (useshaders) {
		Graphics.setCurrent(postGraphics);
		postGraphics.clear();
		StateManager.render(gameContainer, postGraphics);
		postGraphics.flush();
		g.clear();
		screen.bind();
		screen.setUniform1i("frame", frame);
		g.drawImage(postImage, 0, 0, gameContainer.getWidth(), gameContainer.getHeight(), 0, 0, Main.INTERNAL_RESX, Main.INTERNAL_RESY);
		screen.unbind();
		frame++;
	} else {
		StateManager.render(gameContainer, postGraphics);
	}
}

public static void onResize() {
	try {
		postImage.destroy();
		postImage = Image.createOffscreenImage(Main.INTERNAL_RESX, Main.INTERNAL_RESY, Image.FILTER_NEAREST);
		postGraphics = postImage.getGraphics();
		screen.setUniform2f("size", new Vector2f(Main.INTERNAL_RESX, Main.INTERNAL_RESY)); //size of tex
	} catch (SlickException e) {
		e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	}
}

public static Image getScreen() {
	return postImage;
}
}
