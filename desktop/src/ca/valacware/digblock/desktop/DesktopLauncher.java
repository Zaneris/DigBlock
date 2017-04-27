package ca.valacware.digblock.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ca.valacware.digblock.DigBlock;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title="DigBlock";
		config.width=1900;
		config.height=800;
		config.samples=4;
		config.fullscreen=false;
		new LwjglApplication(new DigBlock(false), config);
	}
}
