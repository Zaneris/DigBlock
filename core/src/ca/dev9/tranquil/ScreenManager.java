package ca.dev9.tranquil;

import ca.dev9.tranquil.input.InputHandler;
import ca.dev9.tranquil.screens.ScreenList;
import ca.dev9.tranquil.screens.ScreenInterface;
import ca.dev9.tranquil.screens.World;
import com.badlogic.gdx.ApplicationAdapter;

public class ScreenManager extends ApplicationAdapter {
	public static ScreenList<ScreenInterface> screens;
	public static InputHandler input;

	public ScreenManager(boolean mobile) {
		Config.MOBILE = mobile;
	}

	@Override
	public void create () {
		Graphics.loadShaders();
		Graphics.loadAssets();
		screens = new ScreenList<>();
		input = new InputHandler();
		screens.add(new World());
	}

	@Override
	public void render () {
		if(Graphics.checkAssets())
			for(ScreenInterface screen:screens)
				screen.run();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		// TODO - Handle window resize.
	}
}
