package ca.dev9.tranquil.blocks;

import ca.dev9.tranquil.Chunk;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by Zaneris on 01/07/2015.
 */
public class Grass extends Dirt {
	private static final float grass = Color.valueOf("007B0CFF").toFloatBits();

	public Grass(Chunk chunk) {
		super(chunk);
		blockType = Block.GRASS;
		setFlag(SOLID);
	}

	@Override
	public float getTopColor() {
		return grass;
	}
}
