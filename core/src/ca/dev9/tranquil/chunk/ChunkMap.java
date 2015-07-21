package ca.dev9.tranquil.chunk;

import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.utils.IntMap;

/**
 * Some additional tools added to our IntMap.
 * @author Zaneris
 */
public class ChunkMap<V> extends IntMap<V> {
	public boolean contains(Int3 int3) {
		return containsKey(Chunk.generateHash(int3));
	}

	public boolean contains(int x, int y, int z) {
		return containsKey(Chunk.generateHash(x,y,z));
	}

	public V get(Int3 int3) {
		return get(int3.x, int3.y, int3.z);
	}

	public V get(int x, int y, int z) {
		return get(Chunk.generateHash(x, y, z));
	}

	public V remove(Int3 int3) {
		return remove(int3.x,int3.y,int3.z);
	}

	public V remove(int x, int y, int z) {
		return remove(Chunk.generateHash(x,y,z));
	}
	
	public void add(V chunk) {
		put(chunk.hashCode(), chunk);
	}
}
