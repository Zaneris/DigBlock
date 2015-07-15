package ca.dev9.tranquil.blocks;

import ca.dev9.tranquil.chunk.Chunk;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by Zaneris on 29/06/2015.
 */
public class Block {
	private byte visibleFaces;
	public static final byte SOLID       = 0b0100_0000;
	public static final byte FACE_TOP    = 0b0010_0000;
	public static final byte FACE_BOTTOM = 0b0001_0000;
	public static final byte FACE_NORTH  = 0b0000_1000;
	public static final byte FACE_SOUTH  = 0b0000_0100;
	public static final byte FACE_EAST   = 0b0000_0010;
	public static final byte FACE_WEST   = 0b0000_0001;
	public static final byte ALL_FACES	 = 0b0011_1111;

	public byte blockType = -1;
	public static final byte AIR    = -1;
	public static final byte WATER  = 0;
	public static final byte DIRT   = 1;
	public static final byte GRASS  = 2;

	private static final float water = Color.valueOf("3094CF88").toFloatBits();
	private static final float dirt = Color.valueOf("573B0CFF").toFloatBits();
	private static final float grass = Color.valueOf("007B0CFF").toFloatBits();

	public Chunk chunk;

	public Block(Chunk chunk) {
		this.chunk = chunk;
	}

	public void reset() {
		blockType = -1;
		visibleFaces = 0;
	}

	public void setFlag(byte flag) {
		if(!hasFlag(flag)) {
			visibleFaces = (byte) (visibleFaces | flag);
			if(flag!=SOLID) {
				if (hasFlag(SOLID))
					chunk.visSolidFaces++;
				else
					chunk.visTransFaces++;
				chunk.addToMeshQueue();
			}
		}
	}

	public void setBlockType(byte type) {
		blockType = type;
		if(type!=AIR && type!=WATER)
			setFlag(SOLID);
	}

	public boolean hasFaces() {
		return (visibleFaces&ALL_FACES)>0;
	}

	public void setFlag(boolean value, byte flag) {
		if(value)
			removeFlag(flag);
		else
			setFlag(flag);
	}

	public byte copyFaces() {
		if(hasFlag(SOLID))
			return (byte)(visibleFaces^SOLID);
		else return visibleFaces;
	}

	public void removeFlag(byte flag) {
		if(hasFlag(flag)) {
			visibleFaces = (byte) (visibleFaces ^ flag);
			if(flag!=SOLID) {
				if (hasFlag(SOLID))
					chunk.visSolidFaces--;
				else
					chunk.visTransFaces--;
				chunk.addToMeshQueue();
			}
		}
	}

	public boolean hasFlag(byte flag) {
		return (flag&visibleFaces)==flag;
	}

	public float getTopColor() {
		return getColorFromBlockType(blockType, true);
	}

	public float getSideColor() {
		return getColorFromBlockType(blockType, false);
	}

	public static float getColorFromBlockType(byte block, boolean top) {
		switch(block) {
			case WATER:
				return water;
			case DIRT:
				return dirt;
			case GRASS:
				if(top) return grass;
				else return dirt;
		}
		return 0;
	}

	public byte getTopTexture() {
		return getTextureFromBlockType(blockType, true);
	}

	public byte getSideTexture() {
		return getTextureFromBlockType(blockType, false);
	}

	public static byte getTextureFromBlockType(byte blockType, boolean top) {
		switch(blockType) {
			case WATER:
				return 0;
			case DIRT:
				return 1;
			case GRASS:
				if(top) return 3;
				else return 2;
		}
		return 0;
	}

	public static void setFlags(byte face1, byte face2, Block block1, Block block2) {
		if(block2!=null) {
			if(block2.chunk.built) {
				if (block1.blockType != WATER || block2.blockType != WATER) {
					boolean solid1 = block1.hasFlag(Block.SOLID) ||
							(block1.blockType == WATER &&
									block2.blockType == AIR);
					boolean solid2 = block2.hasFlag(Block.SOLID) ||
							(block1.blockType == AIR &&
									block2.blockType == WATER);
					if (solid2) block2.setFlag(solid1, face1);
					if (solid1) block1.setFlag(solid2, face2);
				}
			}
		}
	}
}
