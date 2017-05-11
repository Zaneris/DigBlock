package ca.valacware.digblock.screens;

import ca.valacware.digblock.Config;
import ca.valacware.digblock.Graphics;
import ca.valacware.digblock.chunk.*;
import ca.valacware.digblock.Player;
import ca.valacware.digblock.utils.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;

import java.util.ArrayList;
import java.util.Iterator;

import ca.valacware.digblock.input.*;

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
	
	public final ChunkMap<Chunk> chunkMap = new ChunkMap<>();
	private final ChunkMap<Chunk> oldMap = new ChunkMap<>();
	private final ArrayList<Chunk> buildQueue = new ArrayList<>();
	private final ArrayList<Chunk> faceQueue = new ArrayList<>();
	public  final ArrayList<Chunk> meshQueue = new ArrayList<>();
	private final ArrayList<Chunk> garbage = new ArrayList<>();
	private final ArrayList<ChunkMesh> transMesh = new ArrayList<>();
	private final OrthographicCamera lightSource;
	private final OrthographicCamera lightSource2;
	private final Vector3 light2Target = new Vector3();
	private final ChunkBlock cb = new ChunkBlock();
	private final Int3 target = new Int3();
	private final Int3 cC = new Int3();
	private final Int3 i = new Int3();
	private final Player player;
	private byte frameCounter;
	private Texture depthMap;
	private Texture depthMap2;
	private double seed;
	private short depth;
	private float tod = 80f;
	private float tod2 = tod;
	private short todCount = 0;
	private boolean flip = false;

	/**
	 * Create the world.
	 */
	public World() {
		super(false,true);
		world = this;
		seed = Math.random()*10000d;
		lightSource = new OrthographicCamera();
		lightSource2 = new OrthographicCamera();
		frameCounter = FPC;
		player = new Player();
		updateDepth();
	}

	private void updateDepth() {
		depth = (short)(Config.DRAW_DIST*16*2);
		lightSource.viewportHeight = 24;
		lightSource.viewportWidth = 24;
		lightSource.far = depth*2;
		lightSource2.viewportHeight = depth;
		lightSource2.viewportWidth = depth;
		lightSource2.far = depth*2;
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
		lightSource.position.y += depth;
		lightSource.lookAt(player.cam.position);
		lightSource.rotateAround(player.cam.position, Vector3.X, 3f);
		lightSource.rotateAround(player.cam.position, Vector3.Z, tod);
		lightSource.update();
		lightSource2.position.set(light2Target);
		lightSource2.position.y += depth;
		lightSource2.lookAt(light2Target);
		lightSource2.rotateAround(light2Target, Vector3.Z, tod2);
		lightSource2.update();
		tod -= .001f*(flip?-1f:1f);
		todCount++;
		if(todCount>200) {
			if(light2Target.dst(player.cam.position) > 32f)
				light2Target.set(player.cam.position);
			todCount = 0;
			tod2 = tod;
		}
		if(tod<-80f)
			flip = true;
		else if(tod>80f)
			flip = false;
	}
	
	private void updateVisible() {
		oldMap.putAll(chunkMap);
		chunkMap.clear();
		Chunk chunk;
		Graphics.startRender(player.cam, lightSource, lightSource2, depthMap, depthMap2);
		Graphics.startSolid();
		for (int r = 0; r < Config.DRAW_DIST; r++) {
			for (i.newLoop(-r, r); i.doneLoop(); i.cubeLoop()) {
				target.setPlus(i, player.currentChunk);
				if (target.y >= 0 && target.y < World.WORLD_VCHUNK) {
					if (player.currentChunk.distance(target) < Config.DRAW_DIST) {
						chunk = oldMap.remove(target);
						if(chunk!=null) {
							if (inFrustum(target)) {
								if (chunk.hasSolidMesh())
									Graphics.renderMesh(chunk.solidMesh);
								if (chunk.hasTransMesh())
									transMesh.add(chunk.transMesh);
							}
							chunkMap.add(chunk);
						} else if (buildQueue.size() < 10) {
							if(inFrustum(target) || r<2) {
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
		Graphics.endSolid();
		renderTrans();
		Graphics.endRender();
		for(Chunk tR:oldMap.values()) {
			tR.reset();
			garbage.add(tR);
			tR.garbage = true;
		}
		oldMap.clear();
	}
	
	private void renderTrans() {
		Graphics.startTrans();
		for(ChunkMesh mesh:transMesh)
			Graphics.renderMesh(mesh);
		transMesh.clear();
		Graphics.endTrans();
	}
	
	private boolean inFrustum(Int3 int3) {
		return player.cam.frustum.sphereInFrustumWithoutNearFar(
			int3.x*16+8, int3.y*16+8, int3.z*16+8, 13.86f);
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
		IntSet.IntSetIterator iter = keys.iterator();
		int key;
		while(iter.hasNext) {
			key = iter.next();
			if (key==Config.Keys.XRAY[0]) {
				Config.WIREFRAME = !Config.WIREFRAME;
			}
		}
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
				player.axisInput(-deltaX/100f,deltaY/100f);
			} else {
				if(player.setRot(deltaX,deltaY)) {
					value.x = xy.x;
					value.y = xy.y;
					player.jumpCount();
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
		} else {
			switch(frameCounter%3) {
				case 0: createMeshes(); break;
				case 1: buildChunks(); break;
				case 2: updateFaces();
			}
			Chunk chunk;
			Graphics.startRender(player.cam,lightSource,lightSource2,depthMap,depthMap2);
			Graphics.startSolid();
			for(IntMap.Entry entry:chunkMap) {
				chunk = (Chunk)entry.value;
				if(chunk.built && chunk.inDepth && inFrustum(chunk.id)) {
					if(chunk.hasSolidMesh())
						Graphics.renderMesh(chunk.solidMesh);
					if(chunk.hasTransMesh())
						transMesh.add(chunk.transMesh);
				}
			}
			Graphics.endSolid();
			renderTrans();
			Graphics.endRender();
			updateWorldTime();
			if(todCount==1) {
				Graphics.startDepth2(lightSource2);
				for (IntMap.Entry entry : chunkMap) {
					chunk = (Chunk) entry.value;
					if (chunk.built) {
						if (chunk.hasSolidMesh()) {
							Graphics.renderMesh(chunk.solidMesh);
							chunk.inDepth = true;
						} else if (chunk.hasTransMesh())
							chunk.inDepth = true;
					}
				}
				depthMap2 = Graphics.endDepth2();
			} else {
				Graphics.startDepth(lightSource);
				for(IntMap.Entry entry:chunkMap) {
					chunk = (Chunk)entry.value;
					if(chunk.built) {
						if(chunk.hasSolidMesh()) {
							Graphics.renderMesh(chunk.solidMesh);
							chunk.inDepth = true;
						} else if (chunk.hasTransMesh())
							chunk.inDepth = true;
					}
				}
				depthMap = Graphics.endDepth();
			}
		}
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
