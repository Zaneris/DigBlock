package ca.dev9.tranquil;

import com.badlogic.gdx.Input;

/**
 * Config management.
 * @author Zaneris
 */
public final class Config {
	public static final boolean DEBUG = true;
	public static boolean WIREFRAME = false;
	public static boolean MOBILE;
	public static byte DRAW_DIST;
	
	public static void load() {
		if(MOBILE)
			DRAW_DIST = 10;
		else
			DRAW_DIST = 20;
	}

	public static final class Keys {
		public static int[] UP		= new int[] {Input.Keys.W, Input.Keys.UP};
		public static int[] DOWN	= new int[] {Input.Keys.S, Input.Keys.DOWN};
		public static int[] LEFT	= new int[] {Input.Keys.A, Input.Keys.LEFT};
		public static int[] RIGHT	= new int[] {Input.Keys.D, Input.Keys.RIGHT};
		public static int[] RUN		= new int[] {Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT};
		public static int[] XRAY	= new int[] {Input.Keys.X, -1};
		public static int[] QUIT	= new int[] {Input.Keys.ESCAPE, Input.Keys.BACK};
	}
}
