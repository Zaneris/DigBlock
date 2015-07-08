package ca.dev9.tranquil;

import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Zaneris on 29/06/2015.
 */
public class Chunk {
	public static final byte CHUNK_SIZE = World.CHUNK_SIZE; // 32 max
	public final Int3 id = new Int3();
	public final Int3 position = new Int3();
	public final Block[][][] blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
	public int visSolidFaces;
	public int visTransFaces;
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
		visSolidFaces = 0;
		visTransFaces = 0;
		wait = false;
		built = false;
		for(x=0;x<16;x++)
			for(y=0;y<16;y++)
				for(z=0;z<16;z++) {
					if(blocks[x][y][z] == null)
						blocks[x][y][z] = new Block(this);
					else blocks[x][y][z].reset();
				}
	}

	public void createBlock(byte type, Int3 location) {
		blocks[location.x][location.y][location.z].reset();
		blocks[location.x][location.y][location.z].setBlockType(type);
	}

	public void addToMeshQueue() {
		if(!wait) {
			World.meshQueue.add(this);
			wait = true;
		}
	}

	public boolean hasSolidMesh() {
		return solidMesh!=null && solidMesh.vertices>0;
	}

	public boolean hasTransMesh() {
		return transMesh!=null && transMesh.vertices>0;
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

	public void addToMap() {
		World.chunkMap.put(hashCode(), this);
	}

	public Block getBlock(int x, int y, int z) {
		if(x<0 || y<0 || z<0 || x>15 || y>15 || z>15)
			return World.getBlock(x + position.x, y + position.y, z + position.z);
		return blocks[x][y][z];
	}

	public Block getBlock(Int3 int3) {
		if(int3.lessThan(0) || int3.greaterThan(15))
			return World.getBlock(int3.x + position.x, int3.y + position.y, int3.z + position.z);
		return blocks[int3.x][int3.y][int3.z];
	}
}
