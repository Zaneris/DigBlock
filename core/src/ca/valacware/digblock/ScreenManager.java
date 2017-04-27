package ca.dev9.tranquil;

import ca.dev9.tranquil.input.InputHandler;
import ca.dev9.tranquil.screens.ScreenList;
import ca.dev9.tranquil.screens.ScreenInterface;
import ca.dev9.tranquil.screens.World;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

/**
 * Entry class into engine. Manages and executes all active screens.
 * @author Zaneris
 */
public class ScreenManager extends ApplicationAdapter {
	public static ScreenList<ScreenInterface> screens;
	public static InputHandler input;

	public ScreenManager(boolean mobile) {
		Config.MOBILE = mobile;
	}

	@Override
	public void create () {
		Config.load();
		Graphics.loadShaders();
		Graphics.loadAssets();
		screens = new ScreenList<>();
		input = new InputHandler();
		screens.add(new World());
	}

	@Override
	public void render () {
		input.processInput();
		if(Graphics.checkAssets())
			for(ScreenInterface screen:screens)
				screen.run(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		for(ScreenInterface screen:screens)
			screen.resize(width, height);
		input.updateScreenDimensions(width, height);
	}
}
