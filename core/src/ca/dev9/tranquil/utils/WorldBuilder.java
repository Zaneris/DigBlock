package ca.dev9.tranquil.utils;

import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.chunk.Chunk;

/**
 * For construction of chunks.
 * @author Zaneris
 */
public final class WorldBuilder {
	private static final float 	TERRAIN_INTENSITY = 0.005f;
	private static final float 	TERRAIN_INTENSITY2 = 0.015f;
	private static final byte 	WATER_HEIGHT = 15;
	private static final byte 	CHUNK_SIZE = Chunk.CHUNK_SIZE;
	public static final byte 	WORLD_VCHUNK = 2;
	public static final byte 	WORLD_VBLOCK = CHUNK_SIZE*WORLD_VCHUNK;
	private static final Int3 i = new Int3();

	public static void buildChunk(Chunk chunk, double seed) {
		Int3 p = chunk.position;
		short h; // Terrain height
		for (i.x = 0; i.x < CHUNK_SIZE; i.x++)
			for (i.z = 0; i.z < CHUNK_SIZE; i.z++) {
				h = terrainHeight(i.x + p.x, i.z + p.z, seed);
				for (i.y = 0; i.y < CHUNK_SIZE; i.y++) {
					if (i.y + p.y < h || (i.y + p.y == h && i.y + p.y < WATER_HEIGHT)) {
						chunk.createBlock(Block.DIRT, i);
					} else if (i.y + p.y == h) {
						chunk.createBlock(Block.GRASS, i);
					} else if(i.y + p.y <= WATER_HEIGHT) {
						chunk.createBlock(Block.WATER, i);
					}
				}
			}
	}

	private static short terrainHeight(int x, int z, double seed) {
		double noise = (1d + SimplexNoise.noise(
				seed + (x * TERRAIN_INTENSITY),
				seed + (z * TERRAIN_INTENSITY)))
				* WORLD_VCHUNK * CHUNK_SIZE / 4;
		noise += (1d + SimplexNoise.noise(
				seed + (x * TERRAIN_INTENSITY2),
				seed + (z * TERRAIN_INTENSITY2)))
				* WORLD_VCHUNK * CHUNK_SIZE / 4;
		if (noise < 1d)
			return 1;
		if (noise >= WORLD_VBLOCK)
			return WORLD_VBLOCK;
		return (short) Math.floor(noise);
	}
}
