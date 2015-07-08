package ca.dev9.tranquil.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title="Tranquil";
		config.width=1280;
		config.height=720;
		config.fullscreen=true;
		new LwjglApplication(new DesktopMain(), config);
	}
}
