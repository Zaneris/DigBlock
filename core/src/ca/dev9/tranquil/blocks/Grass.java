package ca.dev9.tranquil.blocks;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Zaneris on 01/07/2015.
 */
public class Grass extends Dirt {
	private static final Color grass = Color.valueOf("007B0CFF");

	public Grass() {
		this.blockType = Block.GRASS;
	}

	@Override
	public Color getTopColor() {
		return grass;
	}
}
