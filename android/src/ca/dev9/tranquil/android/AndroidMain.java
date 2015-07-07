package ca.dev9.tranquil.android;

import ca.dev9.tranquil.GameMain;
import ca.dev9.tranquil.InputHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by Zaneris on 05/07/2015.
 */
public class AndroidMain extends GameMain implements InputProcessor {
	@Override
	public void create() {
		WORLD_SIZE=8;
		framesPerCycle=10;
		Gdx.input.setInputProcessor(this);
		mobile = true;
		super.create();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		InputHandler.setXAxis(1f);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		InputHandler.setXAxis(0f);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
