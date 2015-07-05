package ca.dev9.tranquil;

import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.utils.Int3;

import java.util.ArrayList;

/**
 * Created by Zaneris on 02/07/2015.
 */
public class World {
	public static final byte WORLD_HEIGHT = 2; // Height of world in chunks
	public static final byte CHUNK_SIZE = 16;
	public static final float TERRAIN_INTENSITY = 0.01f;
	public static final short DRAW_DISTANCE = 100;
	public static final boolean TEXTURES_ON = false;
	public static final ChunkMap<Integer,Chunk> chunkMap = new ChunkMap<Integer,Chunk>();
	public static final ArrayList<Chunk> buildQueue = new ArrayList<Chunk>();
	public static final ArrayList<Chunk> faceQueue = new ArrayList<Chunk>();
	public static final ArrayList<Chunk> meshQueue = new ArrayList<Chunk>();
	public static double seed;

	public static void createNewWorld() {
		seed = Math.random()*10000d;
	}

	private static Chunk chunk;
	private static final Int3 i = new Int3();
	private static Int3 p;
	private static int j;
	public static void buildChunks() {
		if(!buildQueue.isEmpty()) {
			chunk = buildQueue.get(0);
			p = chunk.getChunkPosition();
			for(i.x = 0; i.x<Chunk.CHUNK_SIZE; i.x++)
				for(i.z = 0; i.z<Chunk.CHUNK_SIZE; i.z++) {
					j = terrainHeight(i.x + p.x, i.z + p.z);
					for (i.y = 0; i.y < Chunk.CHUNK_SIZE; i.y++) {
						if (i.y + p.y < j)
							chunk.createBlock(Block.DIRT, i);
						else if (i.y + p.y == j)
							chunk.createBlock(Block.GRASS, i);
						else {
							chunk.createBlock(Block.AIR, i);
						}
					}
				}
			faceQueue.add(chunk);
			buildQueue.remove(0);
		}
	}

	private static double noise;
	public static short terrainHeight(int x, int z) {
		noise = (1d + SimplexNoise.noise(
				seed+(x*TERRAIN_INTENSITY),
				seed+(z*TERRAIN_INTENSITY)))
				* WORLD_HEIGHT*CHUNK_SIZE/2;
		if(noise<1d)
			return 1;
		if(noise>=WORLD_HEIGHT*CHUNK_SIZE)
			return WORLD_HEIGHT*CHUNK_SIZE-1;
		return (short)Math.floor(noise);
	}

	public static void createMeshes() {
		if(!meshQueue.isEmpty()) {
			for(Chunk chunk:meshQueue) {
				ChunkMeshGenerator.createMesh(chunk);
				chunk.wait = false;
			}
			meshQueue.clear();
		}
	}

	private static Block block;
	private static Block block2;
	private static boolean isVoid;
	private static boolean solid;
	public static void updateFaces() {
		if(!faceQueue.isEmpty()) {
			for(Chunk chunk:faceQueue) {
				p = chunk.getChunkPosition();
				for(i.x = 0 + p.x; i.x<p.x+CHUNK_SIZE; i.x++)
					for(i.z = 0 + p.z; i.z<p.z+CHUNK_SIZE; i.z++)
						for(i.y = 0 + p.y; i.y<p.y+CHUNK_SIZE; i.y++) {
							block = getBlock(i.x,i.y,i.z);
							isVoid = (block == null || !block.hasFlag(Block.SOLID));

							block2 = getBlock(i.x+1,i.y,i.z);
							solid = block2!=null && block2.hasFlag(Block.SOLID);
							if(solid) block2.setFlag(isVoid, Block.FACE_EAST);
							if(!isVoid) block.setFlag(!solid, Block.FACE_WEST);

							block2 = getBlock(i.x-1,i.y,i.z);
							solid = block2!=null && block2.hasFlag(Block.SOLID);
							if(solid) block2.setFlag(isVoid, Block.FACE_WEST);
							if(!isVoid) block.setFlag(!solid, Block.FACE_EAST);

							block2 = getBlock(i.x,i.y,i.z+1);
							solid = block2!=null && block2.hasFlag(Block.SOLID);
							if(solid) block2.setFlag(isVoid, Block.FACE_SOUTH);
							if(!isVoid) block.setFlag(!solid, Block.FACE_NORTH);

							block2 = getBlock(i.x,i.y,i.z-1);
							solid = block2!=null && block2.hasFlag(Block.SOLID);
							if(solid) block2.setFlag(isVoid, Block.FACE_NORTH);
							if(!isVoid) block.setFlag(!solid, Block.FACE_SOUTH);

							block2 = getBlock(i.x,i.y+1,i.z);
							solid = block2!=null && block2.hasFlag(Block.SOLID);
							if(solid) block2.setFlag(isVoid, Block.FACE_BOTTOM);
							if(!isVoid) block.setFlag(!solid, Block.FACE_TOP);

							block2 = getBlock(i.x,i.y-1,i.z);
							solid = block2!=null && block2.hasFlag(Block.SOLID);
							if(solid) block2.setFlag(isVoid, Block.FACE_TOP);
							if(!isVoid) block.setFlag(!solid, Block.FACE_BOTTOM);
						}
			}
			faceQueue.clear();
		}
	}


	private static final Int3 temp = new Int3();
	public static Block getBlock(int x, int y, int z) {
		temp.set(x, y, z);
		return getBlock(temp);
	}
	private static Chunk tB;
	private static final Int3 inner = new Int3();
	public static Block getBlock(Int3 int3) {
		inner.copyFrom(int3);
		inner.mod(CHUNK_SIZE);
		int3.div(CHUNK_SIZE);
		tB = chunkMap.get(int3);
		if(tB==null)
			return null;
		return tB.getBlock(inner);
	}
}