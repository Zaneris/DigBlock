package ca.dev9.tranquil.blocks;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Zaneris on 01/07/2015.
 */
public class Dirt extends Block {
    private static final Color dirt = Color.valueOf("573B0CFF");

    public Dirt() {
        this.blockType = Block.DIRT;
    }

    @Override
    public Color getSideColor() {
        return dirt;
    }
}
