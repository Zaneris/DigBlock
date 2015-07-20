package ca.dev9.tranquil.blocks;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Zaneris on 29/06/2015.
 */
public class Block {
	private byte visibleFaces = 0;
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

	public void reset() {
		blockType = -1;
		visibleFaces = 0;
	}

	public boolean setFlag(byte flag) {
		if(!hasFlag(flag)) {
			visibleFaces = (byte) (visibleFaces | flag);
			if(flag!=SOLID) return true;
		}
		return false;
	}

	public void setBlockType(byte type) {
		blockType = type;
		if(type!=AIR && type!=WATER)
			setFlag(SOLID);
	}

	public boolean hasFaces() {
		return (visibleFaces&ALL_FACES)>0;
	}

	public boolean setFlag(boolean value, byte flag) {
		if(value)
			return removeFlag(flag);
		else
			return setFlag(flag);
	}

	public byte copyFaces() {
		if(hasFlag(SOLID))
			return (byte)(visibleFaces^SOLID);
		else return visibleFaces;
	}

	public boolean removeFlag(byte flag) {
		if(hasFlag(flag)) {
			visibleFaces = (byte) (visibleFaces ^ flag);
			if(flag!=SOLID) return true;
		}
		return false;
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
}
