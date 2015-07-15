package ca.dev9.tranquil.screens;

import ca.dev9.tranquil.Config;
import ca.dev9.tranquil.chunk.Chunk;
import ca.dev9.tranquil.chunk.ChunkMap;
import ca.dev9.tranquil.input.Input;
import ca.dev9.tranquil.Player;
import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.utils.*;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;

import java.util.ArrayList;

/**
 * The world!
 * @author Zaneris
 */
public class World extends InputScreen {
	private static final byte CHUNK_SIZE = Chunk.CHUNK_SIZE;
	public final ChunkMap<Chunk> chunkMap;
	public final ArrayList<Chunk> buildQueue;
	public final ArrayList<Chunk> faceQueue;
	public final ArrayList<Chunk> meshQueue;
	public final Player player;
	public double seed;

	/**
	 * Create the world.
	 */
	public World() {
		super(false);
		seed = Math.random()*10000d;
		player = new Player();
		chunkMap = new ChunkMap<>();
		buildQueue = new ArrayList<>();
		faceQueue = new ArrayList<>();
		meshQueue = new ArrayList<>();
	}

	public void buildChunks() {
		if(!buildQueue.isEmpty()) {
			Chunk chunk = buildQueue.get(0);
			WorldBuilder.buildChunk(chunk,seed);
			faceQueue.add(chunk);
			chunk.built = true;
			buildQueue.remove(0);
		}
	}

	public void createMeshes() {
		if(!meshQueue.isEmpty()) {
			Chunk chunk = meshQueue.get(0);
			ChunkMeshGenerator.createMesh(chunk);
			chunk.wait = false;
			meshQueue.remove(0);
		}
	}

	public void updateFaces() {
		if(!faceQueue.isEmpty()) {
			Chunk chunk = faceQueue.get(0);
			chunk.updateFaces();
			faceQueue.remove(0);
		}
	}

	private final Int3 temp = new Int3();
	public Block getBlock(int x, int y, int z) {
		temp.set(x, y, z);
		return getBlock(temp);
	}

	private final Int3 inner = new Int3();
	private final Int3 cC = new Int3();
	public Block getBlock(Int3 int3) {
		if(int3.y>WorldBuilder.WORLD_VBLOCK) return null;
		inner.copyFrom(int3);
		cC.copyFrom(int3);
		inner.mod(CHUNK_SIZE);
		cC.div(CHUNK_SIZE);
		Chunk temp = chunkMap.get(cC);
		if(temp==null)
			return null;
		return temp.blocks[inner.x][inner.y][inner.z];
	}

	@Override
	public void processKeysDown(IntSet keysDown) {

	}

	@Override
	public void processKeysTyped(IntSet keys) {

	}

	@Override
	public void processTouchDown(IntMap<Int2> touch) {

	}

	@Override
	public void processTouchDrag(IntMap<Int2> touch) {

	}

	@Override
	public void processMouseMove(IntMap<Int2> move) {

	}

	@Override
	public void render() {

	}
}