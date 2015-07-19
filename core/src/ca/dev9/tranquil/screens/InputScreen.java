package ca.dev9.tranquil.screens;

import ca.dev9.tranquil.ScreenManager;
import ca.dev9.tranquil.input.Input;

/**
 * Created by Zaneris on 14/07/2015.
 */
public abstract class InputScreen extends Input implements ScreenInterface {

	/**
	 * Constructor for an input object.
	 *
	 * @param full Determines if the occupied screen space is 100% (true), or 0% (false).
	 */
	public InputScreen(boolean full) {
		super(full);
		ScreenManager.screens.add(this);
	}

	/**
	 * Constructor for an input object.
	 *
	 * @param x      Location in percent.
	 * @param y      Location in percent.
	 * @param width  Width in percent.
	 * @param height Height in percent.
	 */
	public InputScreen(float x, float y, float width, float height) {
		super(x, y, width, height);
		ScreenManager.screens.add(this);
	}
}
