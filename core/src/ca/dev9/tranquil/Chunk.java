package ca.dev9.tranquil;

import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.blocks.Dirt;
import ca.dev9.tranquil.blocks.Grass;
import com.badlogic.gdx.graphics.Mesh;

/**
 * Created by Zaneris on 29/06/2015.
 */
public class Chunk {
	public static final byte CHUNK_SIZE = 16; // 32 max
	public int xOff, yOff, zOff;
	public final Block[][][] blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
	public int visibleFaces = 0;
	public Mesh mesh;
	public boolean hasMesh = false;
	public final short[][] heightMap = new short[CHUNK_SIZE][CHUNK_SIZE];
	private static short max;
	private static byte tX, tY, tZ;

	public Chunk(short locX, byte locY, short locZ, short[][] heightMap) {
		xOff = locX * CHUNK_SIZE;
		yOff = locY * CHUNK_SIZE;
		zOff = locZ * CHUNK_SIZE;
		for(tX = 0; tX <CHUNK_SIZE; tX++)
			for(tZ = 0; tZ <CHUNK_SIZE; tZ++) {
				this.heightMap[tX][tZ] = heightMap[tX][tZ];
				if(this.heightMap[tX][tZ] < 1)
					this.heightMap[tX][tZ] = 1;
			}
		for(tX = 0; tX <CHUNK_SIZE; tX++)
			for(tZ = 0; tZ <CHUNK_SIZE; tZ++) {
				max = heightMap[tX][tZ];
				for(tY = 0; tY <CHUNK_SIZE; tY++)
					if(yOff+ tY <=max) {
						if(yOff+ tY ==max) {
							blocks[tX][tY][tZ] = createBlock(Block.GRASS);
							//blocks[tX][tY][tZ].setFlag(Block.FACE_TOP);
						} else blocks[tX][tY][tZ] = createBlock(Block.DIRT);
					} else {
						blocks[tX][tY][tZ] = createBlock(Block.AIR);
					}
				}
	}

	public Block getBlock(int x, int y, int z) {
		if(x-xOff<CHUNK_SIZE && x-xOff>=0)
			return blocks[x-xOff][y-yOff][z-zOff];
		else
			return null;
	}

	private Block createBlock(byte type) {
		switch (type) {
			case Block.DIRT:
				return new Dirt(this);
			case Block.GRASS:
				return new Grass(this);
			default:
				return new Block(this);
		}
	}
}
