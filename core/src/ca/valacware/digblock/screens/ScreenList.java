package ca.valacware.digblock.screens;

import ca.valacware.digblock.input.Input;

import java.util.ArrayList;

/**
 * Ensure screens that handle input are removed from input processing prior to nullifying.
 * @author Zaneris
 */
public class ScreenList<E> extends ArrayList<E> {
	/**
	 * Remove object from list and InputHandler.
	 * @param o Object to be removed.
	 * @return Whether or not object was removed.
	 */
	@Override
	public boolean remove(Object o) {
		if(Input.class.isAssignableFrom(o.getClass()))
			((Input)o).destroy();
		((ScreenInterface)o).dispose();
		return super.remove(o);
	}
}
