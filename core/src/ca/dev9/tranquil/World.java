package ca.dev9.tranquil;

import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import java.util.ArrayList;

/**
 * Created by Zaneris on 02/07/2015.
 */
public class World {
	public static final byte 	WORLD_VCHUNK = 2; // Height of world in chunks
	public static final byte 	CHUNK_SIZE = 16;
	public static final byte 	WORLD_VBLOCK = WORLD_VCHUNK*CHUNK_SIZE-1;
	public static final float 	TERRAIN_INTENSITY = 0.005f;
	public static final float 	TERRAIN_INTENSITY2 = 0.015f;
	public static final byte 	WATER_HEIGHT = 15;
	public static final boolean TEXTURES_ON = true;
	public static boolean 		WIREFRAME = false;
	public static final ChunkMap <Chunk> chunkMap 	= new <Chunk>ChunkMap<Chunk>();
	public static final ArrayList<Chunk> buildQueue = new <Chunk>ArrayList<Chunk>();
	public static final ArrayList<Chunk> faceQueue 	= new <Chunk>ArrayList<Chunk>();
	public static final ArrayList<Chunk> meshQueue 	= new <Chunk>ArrayList<Chunk>();
	public static Player player;
	public static double seed;

	public static void createNewWorld(PerspectiveCamera camera) {
		seed = Math.random()*10000d;
		player = new Player(camera);
	}

	private static final Int3 i = new Int3();
	public static void buildChunks() {
		if(!buildQueue.isEmpty()) {
			Chunk chunk = buildQueue.get(0);
			Int3 p = chunk.position;
			short h;
			for (i.x = 0; i.x < Chunk.CHUNK_SIZE; i.x++)
				for (i.z = 0; i.z < Chunk.CHUNK_SIZE; i.z++) {
					h = terrainHeight(i.x + p.x, i.z + p.z);
					for (i.y = 0; i.y < Chunk.CHUNK_SIZE; i.y++) {
						if (i.y + p.y < h) {
							// TODO - Add line below once lighting is enabled
							// || (i.y + p.y == j && i.y + p.y < WATER_HEIGHT))
							chunk.createBlock(Block.DIRT, i);
						} else if (i.y + p.y == h) {
							chunk.createBlock(Block.GRASS, i);
						} else if(i.y + p.y <= WATER_HEIGHT) {
							chunk.createBlock(Block.WATER, i);
						}
					}
				}
			faceQueue.add(chunk);
			chunk.built = true;
			buildQueue.remove(0);
		}
	}

	private static double noise;
	public static short terrainHeight(int x, int z) {
		noise = (1d + SimplexNoise.noise(
				seed+(x*TERRAIN_INTENSITY),
				seed+(z*TERRAIN_INTENSITY)))
				* WORLD_VCHUNK *CHUNK_SIZE/4;
		noise += (1d + SimplexNoise.noise(
				seed+(x*TERRAIN_INTENSITY2),
				seed+(z*TERRAIN_INTENSITY2)))
				* WORLD_VCHUNK *CHUNK_SIZE/4;
		if(noise <1d)
			return 1;
		if(noise >= WORLD_VBLOCK)
			return WORLD_VBLOCK;
		return (short)Math.floor(noise);
	}

	public static void createMeshes() {
		if(!meshQueue.isEmpty()) {
			Chunk chunk = meshQueue.get(0);
			ChunkMeshGenerator.createMesh(chunk);
			chunk.wait = false;
			meshQueue.remove(0);
		}
	}

	public static void updateFaces() {
		if(!faceQueue.isEmpty()) {
			Block block1;
			Block block2;
			Chunk chunk = faceQueue.get(0);
			for (i.newLoop(0, 15); i.doneLoop(); i.loop()) {
				block1 = chunk.getBlock(i);
				block2 = chunk.getBlock(i.x + 1, i.y, i.z);
				setFlags(Block.FACE_EAST, Block.FACE_WEST, block1, block2);
				block2 = chunk.getBlock(i.x - 1, i.y, i.z);
				setFlags(Block.FACE_WEST, Block.FACE_EAST, block1, block2);
				block2 = chunk.getBlock(i.x, i.y, i.z + 1);
				setFlags(Block.FACE_SOUTH, Block.FACE_NORTH, block1, block2);
				block2 = chunk.getBlock(i.x, i.y, i.z - 1);
				setFlags(Block.FACE_NORTH, Block.FACE_SOUTH, block1, block2);
				block2 = chunk.getBlock(i.x, i.y - 1, i.z);
				setFlags(Block.FACE_TOP, Block.FACE_BOTTOM, block1, block2);
				block2 = chunk.getBlock(i.x, i.y + 1, i.z);
				setFlags(Block.FACE_BOTTOM, Block.FACE_TOP, block1, block2);
			}
			faceQueue.remove(0);
		}
	}

	private static void setFlags(byte face1, byte face2, Block block1, Block block2) {
		if(block2!=null) {
			if(block2.chunk.built) {
				if (block1.blockType != Block.WATER || block2.blockType != Block.WATER) {
					boolean solid1 = block1.hasFlag(Block.SOLID) ||
							(block1.blockType == Block.WATER &&
									block2.blockType == Block.AIR);
					boolean solid2 = block2.hasFlag(Block.SOLID) ||
							(block1.blockType == Block.AIR &&
									block2.blockType == Block.WATER);
					if (solid2) block2.setFlag(solid1, face1);
					if (solid1) block1.setFlag(solid2, face2);
				}
			}
		}
	}

	private static final Int3 temp = new Int3();
	public static Block getBlock(int x, int y, int z) {
		temp.set(x, y, z);
		return getBlock(temp);
	}

	private static final Int3 inner = new Int3();
	private static final Int3 cC = new Int3();
	public static Block getBlock(Int3 int3) {
		if(int3.y>WORLD_VBLOCK) return null;
		inner.copyFrom(int3);
		cC.copyFrom(int3);
		inner.mod(CHUNK_SIZE);
		cC.div(CHUNK_SIZE);
		Chunk temp = chunkMap.get(cC);
		if(temp==null)
			return null;
		return temp.blocks[inner.x][inner.y][inner.z];
	}
}