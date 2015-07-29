package ca.dev9.tranquil.chunk;

import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.screens.World;
import ca.dev9.tranquil.utils.Int3;
import ca.dev9.tranquil.utils.ChunkBlock;
import ca.dev9.tranquil.utils.WorldBuilder;

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
	public final Int3 id = new Int3();
	public final Int3 position = new Int3();
	public final Block[][][] blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
	private static final Block airBlock = new Block();
	public ChunkMesh solidMesh;
	public ChunkMesh transMesh;
	public boolean wait = false; // Awaiting new Mesh
	public boolean built = false;
	public boolean garbage = true;

	public void set(Int3 int3) {
		set(int3.x, int3.y, int3.z);
	}

	public void set(int x, int y, int z) {
		id.set(x,y,z);
		position.set(x, y, z);
		position.mult(CHUNK_SIZE);
		garbage = false;
	}

	public void createBlock(byte type, Int3 l) {
		if(blocks[l.x][l.y][l.z] == null)
			blocks[l.x][l.y][l.z] = new Block();
		blocks[l.x][l.y][l.z].setBlockType(type);
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
		garbage = true;
		wait = false;
		built = false;
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

	public static int generateHash(Int3 int3) {
		return generateHash(int3.x,int3.y,int3.z);
	}

	public static int generateHash(int x, int y, int z) {
		return (y*521 + x)*31963 + z;
	}
	
	public ChunkBlock getChunkBlock(int x, int y, int z) {
		return World.world.getChunkBlock(x + position.x, y + position.y, z + position.z);
	}

	public ChunkBlock getChunkBlock(Int3 int3) {
		return getChunkBlock(int3.x, int3.y, int3.z);
	}
	
	public Block getWorldBlock(Int3 int3) {
		int x = int3.x-position.x;
		int y = int3.y-position.y;
		int z = int3.z-position.z;
		if(x>=0 && y>=0 && z>=0 && x<16 && y<16 && z<16)
			return blocks[x][y][z];
		else {
			ChunkBlock tR = World.world.getChunkBlock(int3);
			if(tR==null)
				return null;
			else
				return tR.block;
		}
	}
	
	public Block getBlock(Int3 int3) {
		return getBlock(int3.x,int3.y,int3.z);
	}
	
	public Block getBlock(int x, int y, int z) {
		if(x>=0 && y>=0 && z>=0 && x<16 && y<16 && z<16)
			return blocks[x][y][z];
		else {
			ChunkBlock tR = World.world.getChunkBlock(position.x+x,position.y+y,position.z+z);
			if(tR==null)
				return null;
			else
				return tR.block;
		}
	}

	public void updateFaces() {
		Block b;
		for (i.newLoop(0, 15); i.doneLoop(); i.loop()) {
			b = blocks[i.x][i.y][i.z];
			if(i.x==0) setFlags(Block.FACE_WEST, Block.FACE_EAST, b, getChunkBlock(i.x - 1, i.y, i.z));
			if(i.y==0) setFlags(Block.FACE_TOP,Block.FACE_BOTTOM, b, getChunkBlock(i.x, i.y - 1, i.z));
			if(i.z==0) setFlags(Block.FACE_NORTH,Block.FACE_SOUTH,b, getChunkBlock(i.x, i.y, i.z - 1));
			if(i.x==15) setFlags(Block.FACE_EAST, Block.FACE_WEST, b, getChunkBlock(i.x+1, i.y, i.z));
			else setFlags(Block.FACE_EAST, Block.FACE_WEST, b, blocks[i.x+1][i.y][i.z]);
			if(i.z==15) setFlags(Block.FACE_SOUTH,Block.FACE_NORTH,b, getChunkBlock(i.x, i.y, i.z + 1));
			else setFlags(Block.FACE_SOUTH,Block.FACE_NORTH, b, blocks[i.x][i.y][i.z+1]);
			if(i.y==15 && id.y!=WorldBuilder.WORLD_VCHUNK-1)
				setFlags(Block.FACE_BOTTOM,Block.FACE_TOP, b, getChunkBlock(i.x, i.y + 1, i.z));
			else if(i.y==15 && id.y==WorldBuilder.WORLD_VCHUNK-1)
				setFlags(Block.FACE_BOTTOM,Block.FACE_TOP, b, airBlock);
			else setFlags(Block.FACE_BOTTOM,Block.FACE_TOP, b, blocks[i.x][i.y+1][i.z]);
		}
	}

	private void setFlags(byte face1, byte face2, Block b1, Block b2) {
		if (b1.blockType != Block.WATER || b2.blockType != Block.WATER) {
			boolean solid1 = b1.hasFlag(Block.SOLID) ||
					(b1.blockType == Block.WATER &&
							b2.blockType == Block.AIR);
			boolean solid2 = b2.hasFlag(Block.SOLID) ||
					(b1.blockType == Block.AIR &&
							b2.blockType == Block.WATER);
			if (solid2 && b2.setFlag(solid1, face1)) addToMeshQueue();
			if (solid1 && b1.setFlag(solid2, face2)) addToMeshQueue();
		}
	}
	
	private void setFlags(byte face1, byte face2, Block b, ChunkBlock cb) {
		if(cb!=null) {
			if (b.blockType != Block.WATER || cb.block.blockType != Block.WATER) {
				boolean solid1 = b.hasFlag(Block.SOLID) ||
					(b.blockType == Block.WATER &&
					cb.block.blockType == Block.AIR);
				boolean solid2 = cb.block.hasFlag(Block.SOLID) ||
					(b.blockType == Block.AIR &&
					cb.block.blockType == Block.WATER);
				if (solid2 && cb.block.setFlag(solid1, face1)) cb.chunk.addToMeshQueue();
				if (solid1 && b.setFlag(solid2, face2)) addToMeshQueue();
			}
		}
	}

	public static void dispose() {

	}
}
