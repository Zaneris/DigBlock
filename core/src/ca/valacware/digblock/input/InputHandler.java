package ca.valacware.digblock.input;

import ca.valacware.digblock.Config;
import ca.valacware.digblock.screens.InputScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

import java.util.ArrayList;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.*;
import ca.valacware.digblock.utils.*;

/**
 * Handle and process all input to the system/device.
 * @author Zaneris
 */
public class InputHandler implements InputProcessor {
	public static ArrayList<Input> inputObjects;
	public static final short vHeight = 1000;
	public static short vWidth;
	private static short width, height;
	private float aspectRatio;
	private final IntSet keysDown;
	private final IntMap<Int2> touchList;
	private final ArrayList<Int2> garbage;
	private static final Int2 int2 = new Int2();

	public InputHandler() {
		Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
		updateScreenDimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		inputObjects = new ArrayList<>();
		keysDown = new IntSet();
		touchList = new IntMap<>();
		garbage = new ArrayList<>();
	}

	public void updateScreenDimensions(int screenX, int screenY) {
		width = (short)screenX;
		height = (short)screenY;
		aspectRatio = width/height;
		vWidth = (short)(vHeight*aspectRatio);
	}

	public void processInput() {
		for(Input input:inputObjects) {
			if (touchList.size > 0)
				input.processTouch(touchList);
			if (keysDown.size > 0)
				input.processKeysDown(keysDown);
			if(!Config.MOBILE && InputScreen.class.isAssignableFrom(input.getClass()))
				if(((InputScreen)input).mouseCaught)
					input.processMouseMove(virtualX(Gdx.input.getX()), virtualY(Gdx.input.getY()));
		}
	}

	public static void centerMouse() {
		Gdx.input.setCursorPosition(realX(vWidth/2),realY(vHeight/2));
	}
	
	public static Int2 getXY(int pointer) {
		int2.x = virtualX(Gdx.input.getX(pointer));
		int2.y = virtualY(Gdx.input.getY(pointer));
		return int2;
	}
	
	private static int virtualX(float x) {
		return (int)Math.ceil(x/width*vWidth);
	}
	
	private static int virtualY(float y) {
		return (int)Math.ceil(y/height*vHeight);
	}

	private static int realX(float x) {
		return (int)(x/vWidth*width);
	}

	private static int realY(float y) {
		return (int)(y/vHeight*height);
	}

	@Override
	public boolean keyDown(int keycode) {
		keysDown.add(keycode);
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
		screenX = virtualX(screenX);
		screenY = virtualY(screenY);
		if(!touchList.containsKey(pointer)) {
			Int2 xy;
			if(!garbage.isEmpty()) {
				xy = garbage.remove(0);
				xy.set(screenX,screenY);
			} else xy = new Int2(screenX,screenY);
			touchList.put(pointer,xy);
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		garbage.add(touchList.remove(pointer));
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
