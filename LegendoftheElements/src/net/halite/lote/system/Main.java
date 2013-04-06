package net.halite.lote.system;

import com.esotericsoftware.minlog.Log;
import net.halite.hbt.HBTCompound;
import net.halite.lote.ScriptRunner;
import net.halite.lote.util.FileHandler;
import net.halite.lote.util.HashmapLoader;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.*;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.LogSystem;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Godai no Densetsu/Legend of the Elements/LotE Main class
 *
 * @author DeadlyFugu
 */
public class Main extends StateBasedGame {

public static final int INTROSTATE=0;
public static final int MENUSTATE=1;
public static final int GAMEPLAYSTATE=2;
public static final int ERRORSTATE=3;
public static final int LOGINSTATE=4;

public static float INTERNAL_ASPECT=(4/3f);
public static int INTERNAL_RESY=480; //Internal resolution y
public static int INTERNAL_RESX=(int) (INTERNAL_RESY*INTERNAL_ASPECT); //Internal resolution x

public static final String verNum="0.2.3";
public static final String verRelease="PRE-ALPHA";
public static final String version="$version.prealpha| "+verNum; //0.0.1 = DEC 16

private static GameContainer gc;
private static StateBasedGame sbg;

public Main() {
	super("LotE");

	this.addState(new IntroState(INTROSTATE));
	this.addState(new MainMenuState(MENUSTATE));
	this.addState(new GameClient(GAMEPLAYSTATE));
	this.addState(new ErrorState(ERRORSTATE));
	this.addState(new LoginState(LOGINSTATE));

	if (Globals.get("showIntro", true))
		this.enterState(INTROSTATE);
	else
		this.enterState(MENUSTATE);
}

public static void main(String[] args) throws SlickException {
	Log.info("LotE version "+verRelease+" "+verNum);

	Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
		public void uncaughtException(Thread t, Throwable e) {
			Main.handleCrash(e);
			System.exit(1);
		}
	});

	if (!new File("pack").exists()) {
		Log.error("Please run this from the correct directory");
		System.exit(0);
	}

	System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir")+"/lib/native");

	org.newdawn.slick.util.Log.setLogSystem(new SlickToMinLogSystem());

	if (new File("conf").exists())
		Globals.setMap(HashmapLoader.readHashmap("conf"));
	else
		Globals.setMap(new HashMap<String,String>());

	try {
		//HBTOutputStream os = new HBTOutputStream(new FileOutputStream("save/TestOut2.hbtc"),true);
		FileHandler.readData();
	} catch (HBTCompound.TagNotFoundException e) {
		e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	} catch (IOException e) {
		e.printStackTrace();
	}

	ScriptRunner.init();

	AppGameContainer app=new CustomAppGameContainer(new Main());

	MainMenuState.disx[3]=app.getScreenWidth();
	MainMenuState.disy[3]=app.getScreenHeight();

	app.setDisplayMode(MainMenuState.disx[0], MainMenuState.disy[0], false);
	//app.setIcons(new String[]{"data/icon32.tga", "data/icon16.tga"}); //TODO: Make this work

	app.start();
}

public static void handleCrash(Throwable e) {
	Log.info("LotE crashed");
	StringWriter writer = new StringWriter(256);
	e.printStackTrace(new PrintWriter(writer));
	try {
		BufferedWriter bw=new BufferedWriter(new FileWriter("LOTE_CRASH_LOG"));
		bw.write("LOTE CRASH LOG\n");
		bw.write(writer.toString());
		bw.write("at "+(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())));
		bw.flush();
		bw.close();
	} catch (IOException e2) {
		e2.printStackTrace();
	}
	try {
		String[] parts = writer.toString().trim().split("\n");
		String out = parts[0];
		boolean ellipsisYet=false;
		boolean ignoreRest=false;
		for (int i=1; i<parts.length; i++) {
			String s=parts[i];
			if (s.trim().startsWith("at ")&&!(ignoreRest|s.trim().startsWith("at java.")||s.trim().startsWith("at sun."))) {
				out=out+"\n    "+s.trim();
				ellipsisYet=false;
				if (s.trim().matches("at net\\.halite\\.lote\\.system\\.(GameClient|GameServer|.*State).*"))
					ignoreRest=true;
			} else if (!ellipsisYet) {
				out=out+"\n    ...";
				ellipsisYet=true;
			}
		}
		JOptionPane.showMessageDialog(null, "Info for geeks:\n"+out+"\nA full log can be found at ./LOTE_CRASH_LOG", "LotE just kinda stopped working. :(", JOptionPane.ERROR_MESSAGE);
	} catch (Exception e2) {
		e2.printStackTrace();
	}
	e.printStackTrace();
}

@Override
public void initStatesList(GameContainer gameContainer) throws SlickException {
	FontRenderer.setLang(FontRenderer.Language.valueOf(Globals.get("lang", "EN_US")));
	FontRenderer.initialise(gameContainer);
	Main.gc=gameContainer;
	Main.sbg=this;
}

public static void handleError(Exception e) {
	e.printStackTrace();
	handleError(e.getLocalizedMessage());
}

public static void handleError(String error) {
	gc.getInput().clearKeyPressedRecord();
	((ErrorState) sbg.getState(Main.ERRORSTATE)).errorText=error;
	sbg.enterState(Main.ERRORSTATE);
}

private static class SlickToMinLogSystem implements LogSystem {

	@Override
	public void debug(String arg0) {
		Log.debug(arg0);
	}

	@Override
	public void error(Throwable arg0) {
		Log.error(arg0.getClass().getSimpleName()+":", arg0);
	}

	@Override
	public void error(String arg0) {
		Log.error(arg0);
	}

	@Override
	public void error(String arg0, Throwable arg1) {
		Log.error(arg0, arg1);
	}

	@Override
	public void info(String arg0) {
		Log.info(arg0);
	}

	@Override
	public void warn(String arg0) {
		Log.warn(arg0);
	}

	@Override
	public void warn(String arg0, Throwable arg1) {
		Log.warn(arg0, arg1);
	}

}

private static class CustomAppGameContainer extends AppGameContainer {
	public CustomAppGameContainer(Game game) throws SlickException {super(game);}
	protected void updateAndRender(int delta) throws SlickException {
		if (smoothDeltas) {
			if (getFPS() != 0) {
				delta = 1000 / getFPS();
			}
		}

		input.poll(width, height);

		Music.poll(delta);
		if (!paused) {
			storedDelta += delta;

			if (storedDelta >= minimumLogicInterval) {
				try {
					if (maximumLogicInterval != 0) {
						long cycles = storedDelta / maximumLogicInterval;
						for (int i=0;i<cycles;i++) {
							game.update(this, (int) maximumLogicInterval);
						}

						int remainder = (int) (storedDelta % maximumLogicInterval);
						if (remainder > minimumLogicInterval) {
							game.update(this, (int) (remainder % maximumLogicInterval));
							storedDelta = 0;
						} else {
							storedDelta = remainder;
						}
					} else {
						game.update(this, (int) storedDelta);
						storedDelta = 0;
					}

				} catch (Throwable e) {
					running=false;
					Main.handleCrash(e);
				}
			}
		} else {
			game.update(this, 0);
		}

		if (hasFocus() || getAlwaysRender()) {
			if (clearEachFrame) {
				GL.glClear(SGL.GL_COLOR_BUFFER_BIT | SGL.GL_DEPTH_BUFFER_BIT);
			}

			GL.glLoadIdentity();
			Graphics graphics = getGraphics();
			graphics.resetTransform();
			graphics.resetFont();
			graphics.resetLineWidth();
			graphics.setAntiAlias(false);
			try {
				game.render(this, graphics);
			} catch (Throwable e) {
				running=false;
				Main.handleCrash(e);
			}
			graphics.resetTransform();

			//if (this.isShowingFPS()) {
			//	this.getDefaultFont().drawString(10, 10, "FPS: "+recordedFPS);
			//}

			GL.flush();
		}

		if (targetFPS != -1) {
			Display.sync(targetFPS);
		}
	}
}
}