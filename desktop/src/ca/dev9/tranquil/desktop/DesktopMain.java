package ca.dev9.tranquil.desktop;

import ca.dev9.tranquil.GameMain;
import ca.dev9.tranquil.InputHandler;
import ca.dev9.tranquil.World;
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
		switch(keycode) {
			case Input.Keys.ESCAPE:
				Gdx.app.exit();
				break;
			case Input.Keys.W:
			case Input.Keys.UP:
				InputHandler.setXAxis(1f);
				break;
			case Input.Keys.S:
			case Input.Keys.DOWN:
				InputHandler.setXAxis(-1f);
				break;
			case Input.Keys.SHIFT_LEFT:
				InputHandler.setXAxis(10f);
				break;
			case Input.Keys.X:
				curWireframe = !curWireframe;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch(keycode) {
			case Input.Keys.W:
			case Input.Keys.UP:
			case Input.Keys.S:
			case Input.Keys.DOWN:
			case Input.Keys.SHIFT_LEFT:
				InputHandler.setXAxis(0f);
				break;
		}
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
