package ca.dev9.tranquil.input;

import ca.dev9.tranquil.utils.Int2;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;

/**
 * Interface used by all Input objects.
 * @author Zaneris
 */
public interface InputInterface {
	/**
	 * For handling keys currently held down.
	 * @param keysDown Array of keys held down.
	 */
	public void processKeysDown(IntSet keysDown);

	/**
	 * For handling keys that were pressed and then released.
	 * @param keysTyped Array of keys pressed and released.
	 */
	public void processKeysTyped(IntSet keysTyped);

	/**
	 * For handling touch input.
	 * @param touch X,Y location on screen in percent.
	 */
	public void processTouch(IntMap<Int2> touch);

	/**
	 * For handling mouse movement with no button pressed.
	 * @param x X location on screen.
	 * @param y Y location on screen.
	 */
	public void processMouseMove(int x, int y);
}
