package ca.dev9.tranquil.blocks;

import ca.dev9.tranquil.Chunk;

/**
 * Created by Zaneris on 29/06/2015.
 */
public class Block {
	private byte visibleFaces = 0b0000_0000;
	public static final byte SOLID       = 0b0100_0000;
	public static final byte FACE_TOP    = 0b0010_0000;
	public static final byte FACE_BOTTOM = 0b0001_0000;
	public static final byte FACE_NORTH  = 0b0000_1000;
	public static final byte FACE_SOUTH  = 0b0000_0100;
	public static final byte FACE_EAST   = 0b0000_0010;
	public static final byte FACE_WEST   = 0b0000_0001;
	public static final byte ALL_FACES	 = 0b0011_1111;

	public byte blockType = 0;
	public static final byte WATER  = -1;
	public static final byte AIR    = 0;
	public static final byte DIRT   = 1;
	public static final byte GRASS  = 2;

	public Chunk chunk;

	public Block(Chunk chunk) {
		this.chunk = chunk;
	}

	public void setFlag(byte flag) {
		if(hasFlag(flag)) return;
		if(blockType==AIR)
			return;
		visibleFaces = (byte)(visibleFaces|flag);
			chunk.visibleFaces++;
		chunk.addToMeshQueue();
	}

	public boolean hasFaces() {
		return (visibleFaces&ALL_FACES)>0;
	}

	public void setFlag(boolean yes, byte flag) {
		if(yes)
			setFlag(flag);
		else
			removeFlag(flag);
	}

	public byte copyFaces() {
		if(hasFlag(SOLID))
			return (byte)(visibleFaces^SOLID);
		else return visibleFaces;
	}

	public void removeFlag(byte flag) {
		if(!hasFlag(flag))
			return;
		visibleFaces = (byte)(visibleFaces^flag);
			chunk.visibleFaces--;
		chunk.addToMeshQueue();
	}

	public boolean hasFlag(byte flag) {
		return (flag&visibleFaces)==flag;
	}

	public float getTopColor() {
		return getSideColor();
	}

	public float getSideColor() {
		return 0f;
	}
}
