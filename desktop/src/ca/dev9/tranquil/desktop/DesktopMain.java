package ca.dev9.tranquil.desktop;

import ca.dev9.tranquil.GameMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by Zaneris on 05/07/2015.
 */
public class DesktopMain extends GameMain implements InputProcessor {
	@Override
	public void create() {
		super.create();
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.ESCAPE)
			Gdx.app.exit();
		return true;
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
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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
