package ca.dev9.tranquil;

import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.blocks.Dirt;
import ca.dev9.tranquil.blocks.Grass;
import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.graphics.Mesh;

/**
 * Created by Zaneris on 29/06/2015.
 */
public class Chunk {
	public static final byte CHUNK_SIZE = World.CHUNK_SIZE; // 32 max
	private static final Int3 chunkCenter = new Int3();
	public final Int3 id = new Int3();
	private static final Int3 position = new Int3();
	public int xOff, yOff, zOff;
	public Block[][][] blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
	public int visibleFaces = 0;
	public Mesh mesh;
	public boolean hasMesh = false;
	public boolean wait = false; // Awaiting new mesh

	public Chunk(Int3 int3) {
		this(int3.x, int3.y, int3.z);
	}

	public Chunk(int x, int y, int z) {
		id.set(x, y, z);
		position.set(id);
		position.mult(CHUNK_SIZE);
		xOff = position.x;
		yOff = position.y;
		zOff = position.z;
	}

	public void createBlock(byte type, Int3 location) {
		blocks[location.x][location.y][location.z] = createBlock(type);
	}

	public void addToMeshQueue() {
		if(!wait) {
			World.meshQueue.add(this);
			wait = true;
		}
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

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null || this.getClass() != obj.getClass())
			return false;
		return id.equals(((Chunk)obj).id);
	}

	@Override
	public int hashCode() {
		return generateHash(id.x, id.y, id.z);
	}

	public static int generateHash(int x, int y, int z) {
		return generateHash((short)x, (short)y, (short)z);
	}

	public static int generateHash(short x, short y, short z) {
		return (y*521 + x)*31963 + z;
	}

	public void addToMap() {
		World.chunkMap.put(hashCode(), this);
	}

	public static Int3 getCenterOfChunk(int x, int y, int z) {
		chunkCenter.set(x,y,z);
		chunkCenter.mult(CHUNK_SIZE);
		chunkCenter.add(8);
		return chunkCenter;
	}

	public Int3 getChunkPosition() {
		position.set(id);
		position.mult(CHUNK_SIZE);
		return position;
	}

	public Block getBlock(Int3 int3) {
		return blocks[int3.x][int3.y][int3.z];
	}
}
