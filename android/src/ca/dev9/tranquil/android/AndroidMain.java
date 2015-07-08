package ca.dev9.tranquil.android;

import ca.dev9.tranquil.GameMain;
import ca.dev9.tranquil.InputHandler;
import ca.dev9.tranquil.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by Zaneris on 05/07/2015.
 */
public class AndroidMain extends GameMain implements InputProcessor {
	int originY = 0;

	@Override
	public void create() {
		WORLD_SIZE=12;
		//framesPerCycle=10;
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
		if(screenX < 200 && screenY < 200)
			curWireframe = !curWireframe;
		else originY = screenY;
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(screenX>=200 && screenY>=200) {
			float drag = (originY-screenY)/10f;
			if(drag>10f)
				drag = 10f;
			else if(drag<-10f)
				drag = -10f;
			InputHandler.setXAxis(drag);
		}
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
