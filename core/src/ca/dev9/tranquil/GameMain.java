package ca.dev9.tranquil;

import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;

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
	private static final float CAM = World.WORLD_HEIGHT*Chunk.CHUNK_SIZE + 1f;

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

		World.createNewWorld(camera);
	}

	private Texture tex;
	private boolean isLoaded = false;
	private int r;
	private final Int3 i = new Int3();
	private final Int3 cC = new Int3();
	private final Int3 target = new Int3();
	private Chunk chunk;
	//private final ChunkMap<Integer,Chunk> toRender = new ChunkMap<Integer,Chunk>();
	private final ChunkMap<Chunk> toRender = new ChunkMap<Chunk>();
	private final ArrayList<Chunk> garbage = new ArrayList<Chunk>();
	private byte frameCounter = 0;
	public static float dT;

	@Override
	public void render () {
		dT = Gdx.graphics.getDeltaTime();
		InputHandler.processInput();
		World.buildChunks();
		World.updateFaces();
		World.createMeshes();
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		//this will render the triangles to GL
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
		// Enable alpha blending
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_BACK);

		camera.update();

		// Shader must be started prior to setting any uniforms
		shader.begin();
		if(World.TEXTURES_ON) {
			tex.bind();
			shader.setUniformi("u_diffuseTexture", 0);
		}
		shader.setUniformMatrix("u_projTrans", camera.combined);
		cC.set(camera.position);
		cC.div(Chunk.CHUNK_SIZE);
		if(frameCounter==0) {
			toRender.clear();
			toRender.putAll(World.chunkMap);
			World.chunkMap.clear();
			for (r = 0; r < WORLD_SIZE; r++) {
				for (i.newLoop((-r), r); i.doneLoop(); i.loop()) {
					if (i.x >= -1 || i.z>=-1) { // TODO - Remove this to render behind you.
						target.setPlus(i, cC);
						if (target.y >= 0 && target.y < World.WORLD_HEIGHT &&
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
									} else if (chunk.hasMesh && chunk.solidMesh != null) {
										chunk.solidMesh.render();
										chunk.addToMap();
										toRender.remove(chunk.hashCode());
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
			for(Chunk chunk:toRender.values()) {
				garbage.add(chunk);
			}
		} else {
			for(Chunk chunk:World.chunkMap.values())
				if(chunk.hasMesh && chunk.solidMesh!=null)
					chunk.solidMesh.render();
		}

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		for(Chunk chunk:World.chunkMap.values())
			if(chunk.hasMesh && chunk.transMesh != null)
				chunk.transMesh.render();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		shader.end();
		frameCounter++;
		if(frameCounter>framesPerCycle)
			frameCounter=0;
	}
}