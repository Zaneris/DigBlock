package ca.valacware.digblock.input;

/**
 * For use by objects that process input.
 * @author Zaneris
 */
public abstract class Input implements InputInterface {
	/**
	 * Location on screen in percent.
	 */
	public float x,y;
	/**
	 * Size on screen in percent.
	 */
	public float width,height;
	/**
	 * Whether or not the object is currently processing input.
	 */
	public boolean active;

	/**
	 * Constructor for an input object.
	 * @param full Determines if the occupied screen space is 100% (true), or 0% (false).
	 */
	public Input(boolean full) {
		this(full?0f:-1f,full?0f:-1f,full?100f:0f,full?100f:0f);
	}

	/**
	 * Constructor for an input object.
	 * @param x Location in percent.
	 * @param y Location in percent.
	 * @param width Width in percent.
	 * @param height Height in percent.
	 */
	public Input(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		active = true;
		InputHandler.inputObjects.add(this);
	}

	/**
	 * Must be called whenever an object is nullified.
	 */
	public void destroy() {
		InputHandler.inputObjects.remove(this);
	}
}
