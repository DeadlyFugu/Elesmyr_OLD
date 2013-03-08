package net.halitesoft.lote.system;

import com.esotericsoftware.minlog.Log;
import net.halitesoft.lote.ScriptRunner;
import net.halitesoft.lote.util.HashmapLoader;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.LogSystem;

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

public static final String verNum="0.1.11";
public static final String verRelease="PRE-ALPHA";
public static final String version="$version.prealpha| "+verNum; //0.0.1 = DEC 16

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

	System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir")+"/lib/native");

	org.newdawn.slick.util.Log.setLogSystem(new SlickToMinLogSystem());

	Globals.setMap(HashmapLoader.readHashmap("conf"));

	ScriptRunner.init();

	AppGameContainer app=new AppGameContainer(new Main());

	MainMenuState.disx[3]=app.getScreenWidth();
	MainMenuState.disy[3]=app.getScreenHeight();

	app.setDisplayMode(MainMenuState.disx[0], MainMenuState.disy[0], false);
	app.setIcons(new String[]{"data/icon32.tga", "data/icon16.tga"});

	app.start();
}

@Override
public void initStatesList(GameContainer gameContainer) throws SlickException {
	FontRenderer.setLang(FontRenderer.Language.valueOf(Globals.get("lang", "EN_US")));
	FontRenderer.initialise(gameContainer);
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
}