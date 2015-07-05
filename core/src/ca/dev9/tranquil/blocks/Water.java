package ca.dev9.tranquil.blocks;

import ca.dev9.tranquil.Chunk;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by Zaneris on 05/07/2015.
 */
public class Water extends Block {
	private static final float water = Color.valueOf("3094CF88").toFloatBits();

	public Water(Chunk chunk) {
		super(chunk);
		blockType = Block.WATER;
	}

	@Override
	public float getSideColor() {
		return water;
	}
}
