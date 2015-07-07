package ca.dev9.tranquil;

/**
 * Created by Zaneris on 06/07/2015.
 */
public final class InputHandler {
	private static float x;
	private static float y;

	public static void setXYAxis(float x, float y) {
		InputHandler.x = x;
		InputHandler.y = y;
	}

	public static void setXAxis(float x) {
		InputHandler.x = x;
	}

	public static void setYAxis(float y) {
		InputHandler.y = y;
	}

	public static void processInput() {
		World.player.axisInput(x,y);
	}
}
