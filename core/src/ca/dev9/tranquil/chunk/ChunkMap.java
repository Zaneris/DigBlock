package ca.dev9.tranquil.chunk;

import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.utils.IntMap;

/**
 * Created by Zaneris on 03/07/2015.
 */
public class ChunkMap<V> extends IntMap<V> {
	public boolean contains(int x, int y, int z) {
		return containsKey(Chunk.generateHash(x,y,z));
	}

	public V get(Int3 int3) {
		return get(int3.x,int3.y,int3.z);
	}

	public V get(int x, int y, int z) {
		return get((short)x,(short)y,(short)z);
	}

	public V get(short x, short y, short z) {
		return get(Chunk.generateHash(x,y,z));
	}
	
	public void add(V chunk) {
		put(chunk.hashCode(), chunk);
	}
}
