package ca.dev9.tranquil.blocks;

import ca.dev9.tranquil.Chunk;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by Zaneris on 01/07/2015.
 */
public class Dirt extends Block {
	private static final float dirt = Color.valueOf("573B0CFF").toFloatBits();

	public Dirt(Chunk chunk) {
		super(chunk);
		blockType = Block.DIRT;
		setFlag(SOLID);
	}

	@Override
	public float getSideColor() {
		return dirt;
	}
}
