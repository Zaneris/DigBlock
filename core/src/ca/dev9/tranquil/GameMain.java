package ca.dev9.tranquil;

import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;

public class GameMain extends ApplicationAdapter {
	private PerspectiveCamera camera;
	private AssetManager assets;
	public static ShaderProgram shader;
	public static boolean mobile = false;
	public byte framesPerCycle = 10;

	private static String getShader(String path) {
		return Gdx.files.internal(path).readString();
	}

	protected void createMeshShader() {
		ShaderProgram.pedantic = false;
		if(World.TEXTURES_ON) {
			shader = new ShaderProgram(
					getShader("VertShaderTex.glsl"),
					getShader("FragShaderTex.glsl"));
		} else {
			shader = new ShaderProgram(
					getShader("VertShader.glsl"),
					getShader("FragShader.glsl"));
		}
		String log = shader.getLog();
		if (!shader.isCompiled())
			throw new GdxRuntimeException(log);
		if (log!=null && log.length()!=0)
			System.out.println("Shader Log: "+log);
	}

	protected static byte WORLD_SIZE = 16;
	private static final float CAM = World.WORLD_VCHUNK *Chunk.CHUNK_SIZE + 1f;

	@Override
	public void create () {
		createMeshShader();
		if(World.TEXTURES_ON) {
			assets = new AssetManager();
			assets.load("dirt.png", Texture.class);
		}
		camera = new PerspectiveCamera(75f,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.set(-50f, CAM, -50f);
		float halfWorld = WORLD_SIZE*8f+8f;
		camera.lookAt(halfWorld, 0f, halfWorld);
		camera.near = 1.0f;
		camera.far = 5000f;
		curWireframe = World.WIREFRAME;
		World.createNewWorld(camera);
	}

	private Texture tex;
	private boolean isLoaded = false;
	private final Int3 i = new Int3();
	private final Int3 cC = new Int3();
	private final Int3 target = new Int3();
	private final ChunkMap<Chunk> toRender = new <Chunk>ChunkMap<Chunk>();
	private final ArrayList<Chunk> garbage = new <Chunk>ArrayList<Chunk>();
	private byte frameCounter = 0;
	public static float dT;
	private boolean curWireframe;

	@Override
	public void render () {
		dT = Gdx.graphics.getDeltaTime();
		InputHandler.processInput();
		World.buildChunks();
		World.updateFaces();
		World.createMeshes();

		if(World.TEXTURES_ON) {
			if (assets.update()) {
				if (!isLoaded) {
					tex = assets.get("dirt.png", Texture.class);
					isLoaded = true;
				}
				flush();
			}
		} else
			flush();
	}

	void flush() {
		if(curWireframe!=World.WIREFRAME) {
			for(Chunk chunk:World.chunkMap.values())
				if(chunk.hasMesh)
					ChunkMeshGenerator.createMesh(chunk);
			curWireframe=World.WIREFRAME;
		}

		if(frameCounter==0) updateVisible();
		renderWorld();
		frameCounter++;
		if(frameCounter>framesPerCycle)
			frameCounter=0;
	}

	private void updateVisible() {
		cC.set(camera.position);
		cC.div(Chunk.CHUNK_SIZE);
		toRender.clear();
		toRender.putAll(World.chunkMap);
		World.chunkMap.clear();
		Chunk chunk;
		for (int r = 0; r < WORLD_SIZE; r++) {
			for (i.newLoop((-r), r); i.doneLoop(); i.loop()) {
				if (i.x >= -3 && i.z>=-3) { // TODO - Remove this to render behind you.
					target.setPlus(i, cC);
					if (target.y >= 0 && target.y < World.WORLD_VCHUNK &&
							Math.abs(target.x) < 32768 &&
							Math.abs(target.z) < 32768) {
						if (i.x == r || i.x == -r || i.y == r || i.y == -r || i.z == r || i.z == -r) {
							if(cC.distance(target)<WORLD_SIZE) {
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
									chunk.addToMap();
									toRender.remove(chunk.hashCode());
								}
							}
						}
					}
				}
			}
		}
		for(Chunk tR:toRender.values()) {
			garbage.add(tR);
		}
	}

	private void renderWorld() {
		camera.update();
		shader.begin();
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		if(World.TEXTURES_ON) {
			tex.bind();
			shader.setUniformi("u_diffuseTexture", 0);
		}
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_BACK);
		shader.setUniformMatrix("u_projTrans", camera.combined);
		for(Chunk tR:World.chunkMap.values())
			if(tR.hasMesh && tR.solidMesh!=null)
				tR.solidMesh.render();
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		for(Chunk chunk:World.chunkMap.values())
			if(chunk.hasMesh && chunk.transMesh != null)
				chunk.transMesh.render();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		shader.end();
	}
}