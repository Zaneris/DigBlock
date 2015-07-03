package ca.dev9.tranquil;

import ca.dev9.tranquil.blocks.Block;

/**
 * Created by Zaneris on 02/07/2015.
 */
public class World {
	public static final byte WORLD_HEIGHT = 4; // Height of world in chunks
	public static final byte WH = WORLD_HEIGHT * Chunk.CHUNK_SIZE;
	public static final boolean TEXTURES_ON = false;
	public static short chunkX, chunkZ;
	public static Chunk[][][] chunks;

	public static void createWorld(short x, short z) {
		chunkX = x;
		chunkZ = z;
		double seed = Math.random()*1000d;
		chunks = new Chunk[x][WORLD_HEIGHT][z];
		short[][] maxHeight = new short[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];
		double temp;
		for(short x2 = 0; x2<x; x2++)
			for(short z2 = 0; z2<z; z2++) {
				for(int x3 = 0; x3<Chunk.CHUNK_SIZE; x3++)
					for(int z3 = 0; z3<Chunk.CHUNK_SIZE; z3++) {
						temp = (1d + SimplexNoise.noise(((x3+seed+x2*Chunk.CHUNK_SIZE))/200,
								((z3+seed+z2*Chunk.CHUNK_SIZE))/200))*WH/2;
						maxHeight[x3][z3] = (short)temp;
						if(maxHeight[x3][z3]<1)
							maxHeight[x3][z3]=1;
						else if (maxHeight[x3][z3]>=WH)
							maxHeight[x3][z3] = WH-1;
					}
				for(byte y = 0; y<WORLD_HEIGHT; y++) {
					chunks[x2][y][z2] = new Chunk(x2,y,z2,maxHeight);
				}
			}
		setVisibleFaces();
		createMeshes(chunks);
	}

	public static void createMeshes(Chunk[][][] chunks) {
		for(Chunk[][] chunks1:chunks)
			for(Chunk[] chunks2:chunks1)
				for(Chunk chunk:chunks2)
					ChunkMeshGenerator.createMesh(chunk);
	}

	public static void setVisibleFaces() {
		Block block;
		Block block2;
		for(int x = 0; x<chunkX*Chunk.CHUNK_SIZE; x++)
			for(int z = 0; z<chunkZ*Chunk.CHUNK_SIZE; z++)
				for(short y = WORLD_HEIGHT*Chunk.CHUNK_SIZE-1; y>=0; y--) {
					block = getBlock(x,y,z);
					if(block!=null) {
						if(block.blockType!=Block.AIR) {
							block.setFlag(Block.FACE_TOP);
							break;
						} else {
							block2 = getBlock(x+1,y,z);
							if(block2!=null && block2.blockType!=Block.AIR)
								block2.setFlag(Block.FACE_EAST);
							block2 = getBlock(x-1,y,z);
							if(block2!=null && block2.blockType!=Block.AIR)
								block2.setFlag(Block.FACE_WEST);
							block2 = getBlock(x,y,z+1);
							if(block2!=null && block2.blockType!=Block.AIR)
								block2.setFlag(Block.FACE_SOUTH);
							block2 = getBlock(x,y,z-1);
							if(block2!=null && block2.blockType!=Block.AIR)
								block2.setFlag(Block.FACE_NORTH);
						}
					}
				}
	}

	public static Block getBlock(int x, short y, int z) {
		byte bX = (byte)(x%16);
		byte bY = (byte)(y%16);
		byte bZ = (byte)(z%16);
		short cX = (short)((x-bX)/16);
		short cY = (short)((y-bY)/16);
		short cZ = (short)((z-bZ)/16);
		if(cX<0 || cY < 0 || cZ < 0 || cX >= chunkX || cY >= WORLD_HEIGHT || cZ >= chunkZ)
			return null;
		if(bX<0 || bY < 0 || bZ < 0 || bX > 15 || bY > 15 || bZ > 15)
			return null;
		return chunks[cX][cY][cZ].blocks[bX][bY][bZ];
	}
}
