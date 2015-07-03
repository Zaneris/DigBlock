package ca.dev9.tranquil.blocks;

import ca.dev9.tranquil.Chunk;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by Zaneris on 29/06/2015.
 */
public class Block {
	public byte visibleFaces = 0b0000_0000;;
	//public static final byte SOLID       = 0b0100_0000;
	public static final byte FACE_TOP    = 0b0010_0000;
	public static final byte FACE_BOTTOM = 0b0001_0000;
	public static final byte FACE_NORTH  = 0b0000_1000;
	public static final byte FACE_SOUTH  = 0b0000_0100;
	public static final byte FACE_EAST   = 0b0000_0010;
	public static final byte FACE_WEST   = 0b0000_0001;

	public byte blockType = 0;
	public static final byte AIR    = 0;
	public static final byte DIRT   = 1;
	public static final byte GRASS  = 2;

	public Chunk chunk;

	public Block(Chunk chunk) {
		this.chunk = chunk;
	}

	public void setFlag(byte flag) {
		if(hasFlag(flag)) {
			System.out.println("Block flag problem " + Byte.toString(visibleFaces) + " " + Byte.toString(flag));
			return;
		}
		if(blockType==AIR)
			return;
		visibleFaces = (byte)(visibleFaces|flag);
		//if(flag!=SOLID)
			chunk.visibleFaces++;
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
