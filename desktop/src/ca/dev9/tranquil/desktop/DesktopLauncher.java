package ca.dev9.tranquil.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title="Tranquil-Dev";
		config.width=854;
		config.height=480;
		config.samples=4;
		new LwjglApplication(new DesktopMain(), config);
	}
}
