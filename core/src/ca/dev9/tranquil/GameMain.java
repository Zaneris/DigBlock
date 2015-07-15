package ca.dev9.tranquil;

import ca.dev9.tranquil.chunk.Chunk;
import ca.dev9.tranquil.chunk.ChunkMap;
import ca.dev9.tranquil.chunk.ChunkMesh;
import ca.dev9.tranquil.input.InputHandler;
import ca.dev9.tranquil.screens.Screen;
import ca.dev9.tranquil.screens.ScreenHandler;
import ca.dev9.tranquil.screens.ScreenInterface;
import ca.dev9.tranquil.screens.World;
import ca.dev9.tranquil.utils.ChunkMeshGenerator;
import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class GameMain extends ApplicationAdapter {
	public static ScreenHandler<ScreenInterface> screenHandler;
	private PerspectiveCamera camera;
	private OrthographicCamera sun;
	public boolean mobile = false;
	protected byte framesPerCycle = 10;
	protected byte WORLD_SIZE = 20;
	private FrameBuffer frameBuffer;
	private short depth;

	private static final float CAM = World.WORLD_VCHUNK * Chunk.CHUNK_SIZE + 1f;

	public GameMain(boolean mobile) {
		Config.MOBILE = mobile;
		screenHandler = new ScreenHandler<>();
	}

	@Override
	public void create () {
		Graphics.loadShaders();
		Graphics.loadTextures();
		updateWorldSize(WORLD_SIZE);
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,2048,2048,true);
		camera = new PerspectiveCamera(75f,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.set(-50f, CAM, -50f);
		float halfWorld = WORLD_SIZE*8f+8f;
		camera.lookAt(halfWorld, 0f, halfWorld);
		camera.near = 1.0f;
		camera.far = depth;
		curWireframe = World.WIREFRAME;
		World.createNewWorld(camera);
	}

	private boolean isLoaded = false;
	private final Int3 i = new Int3();
	private final Int3 cC = new Int3();
	private final Int3 lastChunk = new Int3();
	private final Int3 target = new Int3();
	private final ArrayList<ChunkMesh> solidMeshes = new <ChunkMesh>ArrayList<ChunkMesh>();
	private final ArrayList<ChunkMesh> transMeshes = new <ChunkMesh>ArrayList<ChunkMesh>();
	private final ChunkMap<Chunk> toRender = new <Chunk>ChunkMap<Chunk>();
	private final ArrayList<Chunk> garbage = new <Chunk>ArrayList<Chunk>();
	private final ArrayList<Texture> textures = new <Texture>ArrayList<Texture>();
	private Texture depthMap;
	private byte frameCounter = 0;
	public static float dT;
	protected boolean curWireframe;

	@Override
	public void render () {
		dT = Gdx.graphics.getDeltaTime();
		InputHandler.processInput();
		World.buildChunks();
		World.updateFaces();
		World.createMeshes();
		camera.update();

		if(World.TEXTURES_ON) {
			if (assets.update()) {
				if (!isLoaded) {
					textures.clear();
					textures.add(assets.get("textures/Water.png", Texture.class));
					textures.add(assets.get("textures/Dirt.png", Texture.class));
					textures.add(assets.get("textures/GrassSide.png", Texture.class));
					textures.add(assets.get("textures/GrassTop.png", Texture.class));
					for(Texture tex:textures)
						tex.setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.Nearest);
					isLoaded = true;
				}
				flush();
			}
		} else
			flush();
	}

	void flush() {
		if(frameCounter==0) {
			updateVisible();
			updateShadows();
		}
		renderWorld();
		frameCounter++;
		if(frameCounter>framesPerCycle)
			frameCounter=0;
	}

	private void updateVisible() {
		cC.set(camera.position);
		cC.div(Chunk.CHUNK_SIZE);
		solidMeshes.clear();
		transMeshes.clear();
		toRender.putAll(World.chunkMap);
		World.chunkMap.clear();
		Chunk chunk;
		boolean wireChange = false;
		if(curWireframe!=World.WIREFRAME) {
			World.WIREFRAME = !World.WIREFRAME;
			wireChange = true;
		}
		for (int r = 0; r < WORLD_SIZE; r++) {
			for (i.newLoop((-r), r); i.doneLoop(); i.cubeLoop()) {
				if (i.x >= -3 && i.z >= -3) { // TODO - Remove this to render behind you.
					target.setPlus(i, cC);
					if (target.y >= 0 && target.y < World.WORLD_VCHUNK &&
							Math.abs(target.x) < 32768 &&
							Math.abs(target.z) < 32768) {
						if (cC.distance(target) < WORLD_SIZE) {
							chunk = toRender.get(target.x, target.y, target.z);
							if (chunk == null) {
								if (World.buildQueue.size() < framesPerCycle) {
									if (garbage.isEmpty())
										chunk = new Chunk();
									else {
										chunk = garbage.get(0);
										garbage.remove(0);
									}
									chunk.set(target.x, target.y, target.z);
									World.buildQueue.add(chunk);
									chunk.addToMap();
								}
							} else {
								if (wireChange)
									ChunkMeshGenerator.createMesh(chunk);
								if (chunk.hasSolidMesh())
									solidMeshes.add(chunk.solidMesh);
								if (chunk.hasTransMesh())
									transMeshes.add(chunk.transMesh);
								chunk.addToMap();
								toRender.remove(chunk.hashCode());
							}
						}
					}
				}
			}
		}
		for(Chunk tR:toRender.values()) {
			tR.reset();
			garbage.add(tR);
		}
		toRender.clear();
	}

	private void updateShadows() {
		if(!cC.equals(lastChunk)) {
			sun.position.set(camera.position);
			sun.position.y += depth / 2;
			sun.rotateAround(camera.position, Vector3.X, 10f);
			sun.rotateAround(camera.position, Vector3.Z, 75f);
			sun.lookAt(camera.position);
			sun.update();
			lastChunk.copyFrom(cC);
		}

		frameBuffer.begin();
			Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glDisable(GL20.GL_CULL_FACE);
			shaderDepth.begin();
				shaderDepth.setUniformf("u_WorldSize", depth);
				shaderDepth.setUniformMatrix("u_LightMatrix", sun.combined);
				for (ChunkMesh tR : solidMeshes)
					if (tR.vertices > 0)
						tR.render(shaderDepth);
			shaderDepth.end();
		frameBuffer.end();
		depthMap = frameBuffer.getColorBufferTexture();
	}

	private void renderWorld() {
		ShaderProgram shaderOut;
		if(World.TEXTURES_ON && !World.WIREFRAME)
			shaderOut = shaderTex;
		else
			shaderOut = shaderWire;
		if(World.WIREFRAME) Gdx.gl.glClearColor(0f,0f,0f,1f);
		else Gdx.gl.glClearColor(0.494f, 0.753f, 0.93f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		shaderOut.begin();
			shaderOut.setUniformf("u_WorldSize", depth);
			shaderOut.setUniformMatrix("u_CamMatrix", camera.combined);
			shaderOut.setUniformMatrix("u_LightMatrix", sun.combined);
			if(World.TEXTURES_ON && !World.WIREFRAME) {
				shaderOut.setUniformf("u_LightVector", sun.direction);
				bindTextures(shaderOut);
				shaderOut.setUniformf("u_Alpha", 1f);
			}
			Gdx.gl.glEnable(GL20.GL_CULL_FACE);
			Gdx.gl.glCullFace(GL20.GL_BACK);
			for(ChunkMesh tR:solidMeshes)
				if(tR.vertices>0)
					tR.render(shaderOut);
			Gdx.gl.glDisable(GL20.GL_CULL_FACE);
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			if(World.TEXTURES_ON && !World.WIREFRAME)
				shaderOut.setUniformf("u_Alpha", 0.7f);
			for(ChunkMesh tR:transMeshes)
				if(tR.vertices>0)
					tR.render(shaderOut);
			Gdx.gl.glDisable(GL20.GL_BLEND);
			Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		shaderOut.end();
	}

	private void bindTextures(ShaderProgram shaderOut) {
		depthMap.bind(0);
		shaderOut.setUniformi("u_DepthMap", 0);
		textures.get(0).bind(1);
		shaderOut.setUniformi("u_Water", 1);
		textures.get(1).bind(2);
		shaderOut.setUniformi("u_Dirt", 2);
		textures.get(2).bind(3);
		shaderOut.setUniformi("u_GrassSide", 3);
		textures.get(3).bind(4);
		shaderOut.setUniformi("u_GrassTop", 4);
	}

	private void updateWorldSize(byte size) {
		WORLD_SIZE = size;
		depth = (short)(WORLD_SIZE*16*2);
		if(sun==null) {
			sun = new OrthographicCamera();
			sun.near = 1.0f;
			sun.far = depth;
		}
		sun.viewportHeight = depth;
		sun.viewportWidth = depth;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		InputHandler.updateScreenDimensions(width,height);
	}
}