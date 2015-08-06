package ca.dev9.tranquil.desktop;

import ca.dev9.tranquil.ScreenManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title="Tranquil-Dev";
		config.width=854;
		config.height=480;
		config.samples=4;
		config.fullscreen=false;
		new LwjglApplication(new ScreenManager(false), config);
	}
}
