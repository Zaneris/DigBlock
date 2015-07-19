package ca.dev9.tranquil.chunk;

import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.screens.World;
import ca.dev9.tranquil.utils.Int3;
import ca.dev9.tranquil.utils.ChunkBlock;

/**
 * Storage container object for blocks and renderable meshes.
 * @author Zaneris
 */
public class Chunk {
	/**
	 * Value used for length, width, and height in blocks for a given chunk.
	 * Do not change this value.
	 */
	public static final byte CHUNK_SIZE = 16;
	private static final Int3 i = new Int3();
	private static final ChunkBlock cb = new ChunkBlock();
	public final Int3 id = new Int3();
	public final Int3 position = new Int3();
	public final Block[][][] blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
	// TODO - Switch to non 3 dimensional array.
	public ChunkMesh solidMesh;
	public ChunkMesh transMesh;
	public boolean wait; // Awaiting new Mesh
	public boolean built;

	public void set(Int3 int3) {
		set(int3.x, int3.y, int3.z);
	}

	public void set(int x, int y, int z) {
		id.set(x,y,z);
		position.set(x,y,z);
		position.mult(CHUNK_SIZE);
		wait = false;
		built = false;
		for(x=0;x<16;x++)
			for(y=0;y<16;y++)
				for(z=0;z<16;z++) {
					if(blocks[x][y][z] == null)
						blocks[x][y][z] = new Block();
					else blocks[x][y][z].reset();
				}
	}

	public void createBlock(byte type, Int3 location) {
		blocks[location.x][location.y][location.z].reset();
		blocks[location.x][location.y][location.z].setBlockType(type);
	}

	public boolean hasSolidMesh() {
		return solidMesh!=null && solidMesh.vertices>0;
	}

	public boolean hasTransMesh() {
		return transMesh!=null && transMesh.vertices>0;
	}
	
	public void addToMeshQueue() {
		if(!wait) {
			World.world.meshQueue.add(this);
			wait = true;
		}
	}

	public void reset() {
		if(solidMesh!=null) solidMesh.reset();
		if(transMesh!=null) transMesh.reset();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null || this.getClass() != obj.getClass())
			return false;
		return id.equals(((Chunk) obj).id);
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
	
	public ChunkBlock getBlock(int x, int y, int z) {
		if(x<0 || y<0 || z<0 || x>15 || y>15 || z>15)
			return World.world.getBlock(x + position.x, y + position.y, z + position.z);
		cb.chunk = this;
		cb.block = blocks[x][y][z];
		return cb;
	}

	public ChunkBlock getBlock(Int3 int3) {
		return getBlock(int3.x,int3.y,int3.z);
	}

	public void updateFaces() {
		ChunkBlock cb1;
		ChunkBlock cb2;
		for (i.newLoop(0, 15); i.doneLoop(); i.loop()) {
			cb1 = getBlock(i);
			cb2 = getBlock(i.x + 1, i.y, i.z);
			setFlags(Block.FACE_EAST, Block.FACE_WEST, cb1, cb2);
			cb2 = getBlock(i.x - 1, i.y, i.z);
			setFlags(Block.FACE_WEST, Block.FACE_EAST, cb1, cb2);
			cb2 = getBlock(i.x, i.y, i.z + 1);
			setFlags(Block.FACE_SOUTH, Block.FACE_NORTH, cb1, cb2);
			cb2 = getBlock(i.x, i.y, i.z - 1);
			setFlags(Block.FACE_NORTH, Block.FACE_SOUTH, cb1, cb2);
			cb2 = getBlock(i.x, i.y - 1, i.z);
			setFlags(Block.FACE_TOP, Block.FACE_BOTTOM, cb1, cb2);
			cb2 = getBlock(i.x, i.y + 1, i.z);
			setFlags(Block.FACE_BOTTOM, Block.FACE_TOP, cb1, cb2);
		}
	}
	
	private static void setFlags(byte face1, byte face2, ChunkBlock cb1, ChunkBlock cb2) {
		if(cb2!=null) {
			if (cb1.block.blockType != Block.WATER || cb2.block.blockType != Block.WATER) {
				boolean solid1 = cb1.block.hasFlag(Block.SOLID) ||
					(cb1.block.blockType == Block.WATER &&
					cb2.block.blockType == Block.AIR);
				boolean solid2 = cb2.block.hasFlag(Block.SOLID) ||
					(cb1.block.blockType == Block.AIR &&
					cb2.block.blockType == Block.WATER);
				if (solid2) cb2.setFlag(solid1, face1);
				if (solid1) cb1.setFlag(solid2, face2);
			}
		}
	}
}
