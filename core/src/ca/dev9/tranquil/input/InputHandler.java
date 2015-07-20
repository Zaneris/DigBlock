package ca.dev9.tranquil.input;

import ca.dev9.tranquil.screens.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntMap;

import java.util.ArrayList;

/**
 * Handle and process all input to the system/device.
 * @author Zaneris
 */
public class InputHandler implements InputProcessor {
	public static ArrayList<Input> inputObjects;
	private int width, height;
	private boolean touch = false;
	private IntMap<Integer> keysDown;

	public InputHandler() {
		Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
		updateScreenDimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		keysDown = new IntMap<>();
		inputObjects = new ArrayList<>();
	}

	public void updateScreenDimensions(int screenX, int screenY) {
		width = screenX;
		height = screenY;
	}

	public void processInput() {
		if(touch)
			for(Input input:inputObjects)
				input.processTouchDown(null);
		if(keysDown.size>0)
			for(Input input:inputObjects)
				input.processTouchDown(null);
	}

	@Override
	public boolean keyDown(int keycode) {
		keysDown.put(keycode, keycode);
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		keysDown.remove(keycode);
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touch = true;
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		touch = false;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		return true;
	}
}
