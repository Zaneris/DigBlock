package ca.dev9.tranquil.android;

import ca.dev9.tranquil.GameMain;

/**
 * Created by Zaneris on 05/07/2015.
 */
public class AndroidMain extends GameMain {
	private static final byte WORLD_SIZE = 10;

	@Override
	protected byte getWorldSize() {
		return WORLD_SIZE;
	}
}
