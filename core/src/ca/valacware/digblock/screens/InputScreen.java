package ca.valacware.digblock.screens;

import ca.valacware.digblock.DigBlock;
import ca.valacware.digblock.input.Input;
import ca.valacware.digblock.input.InputHandler;
import com.badlogic.gdx.Gdx;

/**
 * Created by Zaneris on 14/07/2015.
 */
public abstract class InputScreen extends Input implements ScreenInterface {
	/**
	 * Whether or not mouse is captured by screen.
	 */
	public boolean mouseCaught;
	/**
	 * Constructor for an input object.
	 *
	 * @param full Determines if the occupied screen space is 100% (true), or 0% (false).
	 */
	public InputScreen(boolean full, boolean mouseCaught) {
		this(full ? 0f : -1f, full ? 0f : -1f, full ? 100f : 0f, full ? 100f : 0f, mouseCaught);
	}

	/**
	 * Constructor for an input object.
	 *
	 * @param x      Location in percent.
	 * @param y      Location in percent.
	 * @param width  Width in percent.
	 * @param height Height in percent.
	 */
	public InputScreen(float x, float y, float width, float height, boolean mouseCaught) {
		super(x, y, width, height);
		this.mouseCaught = mouseCaught;
		Gdx.input.setCursorCatched(mouseCaught);
		InputHandler.centerMouse();
		DigBlock.screens.add(this);
	}
}
