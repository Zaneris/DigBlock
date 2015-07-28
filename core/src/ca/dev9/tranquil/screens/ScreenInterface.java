package ca.dev9.tranquil.screens;

/**
 * Interface used by all renderable classes within the engine.
 * @author Zaneris
 */
public interface ScreenInterface {
	/**
	 * Render the screen.
	 */
	public void run(float deltaTime);
	
	/**
	 * Should be used to nullify any references set on statics so the garbage collector can reclaim the memory.
	 */
	public void dispose();

	/**
	 * What to do when the resolution changes.
	 */
	public void resize(int width, int height);
}
