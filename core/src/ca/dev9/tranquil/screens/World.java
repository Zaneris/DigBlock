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

import ca.dev9.tranquil.input.*;

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
		super(false,true);
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
		Chunk chunk;
		for(int i = 0; i < buildQueue.size(); i++) {
			chunk = buildQueue.remove(i);
			if(!chunk.garbage) {
				WorldBuilder.buildChunk(chunk,seed);
				faceQueue.add(chunk);
				chunk.built = true;
				return;
			}
		}
	}

	private void updateFaces() {
		Chunk chunk;
		for(int i = 0; i < faceQueue.size(); i++) {
			chunk = faceQueue.remove(i);
			if(!chunk.garbage && chunk.built) {
				chunk.updateFaces();
				return;
			}
		}
	}

	private void createMeshes() {
		Chunk chunk;
		for(int i = 0; i < meshQueue.size(); i++) {
			chunk = meshQueue.remove(i);
			if(!chunk.garbage && chunk.built && chunk.wait) {
				ChunkMeshGenerator.createMesh(chunk);
				chunk.wait = false;
				return;
			}
		}
	}

	public ChunkBlock getChunkBlock(int x, int y, int z) {
		target.set(x, y, z);
		return getChunkBlock(target);
	}
	
	public ChunkBlock getChunkBlock(Int3 int3) {
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
		lightSource.rotateAround(player.cam.position, Vector3.Z, -78f);
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
		boolean inFrustum;
		if(curWireframe!=Config.WIREFRAME) {
			Config.WIREFRAME = !Config.WIREFRAME;
			wireChange = true;
		}
		for (int r = 0; r < Config.DRAW_DIST; r++) {
			for (i.newLoop((-r), r); i.doneLoop(); i.cubeLoop()) {
				target.setPlus(i, player.currentChunk);
				if (target.y >= 0 && target.y < World.WORLD_VCHUNK) {
					if (player.currentChunk.distance(target) < Config.DRAW_DIST) {
						chunk = oldMap.remove(target);
						inFrustum = player.cam.frustum.sphereInFrustumWithoutNearFar(
								target.x*16+8, target.y*16+8, target.z*16+8, 13.86f);
						if(chunk!=null) {
							if (inFrustum) {
								if (wireChange)
									ChunkMeshGenerator.createMesh(chunk);
								if (chunk.hasSolidMesh())
									solidMeshes.add(chunk.solidMesh);
								if (chunk.hasTransMesh())
									transMeshes.add(chunk.transMesh);
							}
							chunkMap.add(chunk);
						} else if (buildQueue.size() < 10) {
							if(inFrustum || r<2) {
								if (garbage.isEmpty())
									chunk = new Chunk();
								else chunk = garbage.remove(0);
								chunk.set(target);
								buildQueue.add(chunk);
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
			tR.garbage = true;
		}
		oldMap.clear();
	}

	@Override
	public void processKeysDown(IntSet keysDown) {
		IntSet.IntSetIterator iter = keysDown.iterator();
		int key;
		while(iter.hasNext) {
			key = iter.next();
			if(key==Config.Keys.UP[0] || key==Config.Keys.UP[1]) {
				player.axisY(1f);
			} else if (key==Config.Keys.DOWN[0] || key==Config.Keys.DOWN[1]) {
				player.axisY(-1f);
			} else if (key==Config.Keys.LEFT[0] || key==Config.Keys.LEFT[1]) {
				player.axisX(-1f);
			} else if (key==Config.Keys.RIGHT[0] || key==Config.Keys.RIGHT[1]) {
				player.axisX(1f);
			} else if (key==Config.Keys.JUMP[0] || key==Config.Keys.JUMP[1]) {
				player.jump = true;
			} else if (key==Config.Keys.QUIT[0] || key==Config.Keys.QUIT[1]) {
				Gdx.app.exit();
			}
		}
	}

	@Override
	public void processKeysTyped(IntSet keys) {

	}

	@Override
	public void processTouch(IntMap<Int2> touch) {
		int deltaX, deltaY;
		for(IntMap.Entry entry:touch.entries()) {
			Int2 value = (Int2)entry.value;
			Int2 xy = InputHandler.getXY(entry.key);
			deltaX = value.x-xy.x;
			deltaY = value.y-xy.y;
			if(value.x < InputHandler.vWidth/2) {
				player.axisInput(-deltaX/200f,deltaY/200f);
			} else {
				player.jumpCount();
				if(player.setRot(deltaX,deltaY)) {
					value.x = xy.x;
					value.y = xy.y;
				}
			}
		}
	}

	@Override
	public void processMouseMove(int x, int y) {
		int deltaX = InputHandler.vWidth/2 - x;
		int deltaY = InputHandler.vHeight/2 - y;
		if(player.setRot(deltaX,deltaY))
			InputHandler.centerMouse();
	}

	@Override
	public void run(float deltaTime) {
		player.move(deltaTime);
		player.update();
		frameCounter++;
		if(frameCounter>=FPC) {
			frameCounter = 0;
			updateVisible();
			boolean clear = false;
			if(player.moved32()) {
				updateWorldTime();
				player.updateLastPosition();
				clear = true;
			}
			depthMap = Graphics.updateDepthMap(lightSource,solidMeshes,chunkMap,clear);
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
