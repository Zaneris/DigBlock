package ca.dev9.tranquil.screens;

import ca.dev9.tranquil.Config;
import ca.dev9.tranquil.Graphics;
import ca.dev9.tranquil.chunk.*;
import ca.dev9.tranquil.Player;
import ca.dev9.tranquil.utils.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The world!
 * @author Zaneris
 */
public class World extends InputScreen {
	public static World world;
	private static final byte FPC = 30; // Frames per cycle
	private static final byte CHUNK_SIZE = Chunk.CHUNK_SIZE;
	private static final byte WORLD_VCHUNK = WorldBuilder.WORLD_VCHUNK;
	private static final byte WORLD_VBLOCK = WorldBuilder.WORLD_VBLOCK;
	
	private final ChunkMap<Chunk> chunkMap = new ChunkMap<>();
	private final ChunkMap<Chunk> oldMap = new ChunkMap<>();
	private final ArrayList<Chunk> buildQueue = new ArrayList<>();
	private final ArrayList<Chunk> faceQueue = new ArrayList<>();
	public  final ArrayList<Chunk> meshQueue = new ArrayList<>();
	private final ArrayList<Chunk> garbage = new ArrayList<>();
	private final ArrayList<ChunkMesh> solidMeshes = new ArrayList<>();
	private final ArrayList<ChunkMesh> transMeshes = new ArrayList<>();
	private final OrthographicCamera lightSource;
	private final ChunkBlock cb = new ChunkBlock();
	private final Int3 target = new Int3();
	private final Int3 cC = new Int3();
	private final Int3 i = new Int3();
	private final Player player;
	private boolean curWireframe;
	private byte frameCounter;
	private Texture depthMap;
	private double seed;
	private short depth;

	/**
	 * Create the world.
	 */
	public World() {
		super(false);
		world = this;
		seed = Math.random()*10000d;
		curWireframe = Config.WIREFRAME;
		lightSource = new OrthographicCamera();
		lightSource.near = 1.0f;
		frameCounter = FPC;
		player = new Player();
		updateDepth();
	}

	private void updateDepth() {
		depth = (short)(Config.DRAW_DIST*16*2);
		lightSource.far = depth;
		lightSource.viewportHeight = depth;
		lightSource.viewportWidth = depth;
		player.cam.far = depth;
	}

	private void buildChunks() {
		if(!buildQueue.isEmpty()) {
			Chunk chunk = buildQueue.get(0);
			WorldBuilder.buildChunk(chunk,seed);
			faceQueue.add(chunk);
			chunk.built = true;
			buildQueue.remove(0);
		}
	}

	private void createMeshes() {
		if(!meshQueue.isEmpty()) {
			Chunk chunk = meshQueue.get(0);
			ChunkMeshGenerator.createMesh(chunk);
			chunk.wait = false;
			meshQueue.remove(0);
		}
	}

	private void updateFaces() {
		if(!faceQueue.isEmpty()) {
			Chunk chunk = faceQueue.get(0);
			chunk.updateFaces();
			faceQueue.remove(0);
		}
	}

	public ChunkBlock getBlock(int x, int y, int z) {
		target.set(x, y, z);
		return getBlock(target);
	}
	
	public ChunkBlock getBlock(Int3 int3) {
		if(int3.y>WORLD_VBLOCK) return null;
		target.copyFrom(int3);
		cC.copyFrom(int3);
		target.mod(CHUNK_SIZE);
		cC.div(CHUNK_SIZE);
		if(cb.chunk == null || !cb.chunk.id.equals(cC))
			cb.chunk = chunkMap.get(cC);
		if(cb.chunk==null || !cb.chunk.built)
			return null;
		cb.block = cb.chunk.blocks[target.x][target.y][target.z];
		return cb;
	}

	private void updateWorldTime() {
		lightSource.position.set(player.cam.position);
		lightSource.position.y += depth/2;
		lightSource.rotateAround(player.cam.position, Vector3.Z, 75f);
		lightSource.lookAt(player.cam.position);
		lightSource.update();
	}
	
	private void updateVisible() {
		solidMeshes.clear();
		transMeshes.clear();
		oldMap.putAll(chunkMap);
		chunkMap.clear();
		Chunk chunk;
		boolean wireChange = false;
		if(curWireframe!=Config.WIREFRAME) {
			Config.WIREFRAME = !Config.WIREFRAME;
			wireChange = true;
		}
		for (int r = 0; r < Config.DRAW_DIST; r++) {
			for (i.newLoop((-r), r); i.doneLoop(); i.cubeLoop()) {
				if (i.x >= -3 && i.z >= -3) { // TODO - Remove this to render behind you.
					target.setPlus(i, player.currentChunk);
					if (target.y >= 0 && target.y < World.WORLD_VCHUNK) {
						if (player.currentChunk.distance(target) < Config.DRAW_DIST) {
							chunk = oldMap.remove(target);
							if (chunk == null) {
								if (buildQueue.size() <= 10) {
									if (garbage.isEmpty())
										chunk = new Chunk();
									else {
										chunk = garbage.get(0);
										garbage.remove(0);
									}
									chunk.set(target);
									buildQueue.add(chunk);
									chunkMap.add(chunk);
								}
							} else {
								if (wireChange)
									ChunkMeshGenerator.createMesh(chunk);
								if (chunk.hasSolidMesh())
									solidMeshes.add(chunk.solidMesh);
								if (chunk.hasTransMesh())
									transMeshes.add(chunk.transMesh);
								chunkMap.add(chunk);
							}
						}
					}
				}
			}
		}
		for(Chunk tR:oldMap.values()) {
			tR.reset();
			garbage.add(tR);
		}
		oldMap.clear();
	}

	@Override
	public void processKeysDown(IntSet keysDown) {
		player.axisInput(2f,2f);
	}

	@Override
	public void processKeysTyped(IntSet keys) {

	}

	@Override
	public void processTouchDown(IntMap<Int2> touch) {
		player.axisInput(2f,2f);
	}

	@Override
	public void processTouchDrag(IntMap<Int2> touch) {

	}

	@Override
	public void processMouseMove(IntMap<Int2> move) {

	}

	@Override
	public void run() {
		player.update();
		frameCounter++;
		if(frameCounter>=FPC) {
			frameCounter = 0;
			updateVisible();
			if(player.moved32()) {
				updateWorldTime();
				player.updateLastPosition();
			}
			depthMap = Graphics.updateDepthMap(lightSource,solidMeshes);
		} else switch(frameCounter%3) {
			case 0: createMeshes(); break;
			case 1: buildChunks(); break;
			case 2: updateFaces();
		}
		Graphics.renderChunks(player.cam, lightSource, solidMeshes, transMeshes, depthMap);
	}
	
	@Override
	public void dispose() {
		world = null;
		Chunk.dispose();
	}

	@Override
	public void resize(int width, int height) {
		player.cam.viewportWidth = width;
		player.cam.viewportHeight = height;
	}
}
