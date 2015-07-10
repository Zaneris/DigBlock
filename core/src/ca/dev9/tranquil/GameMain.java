package ca.dev9.tranquil;

import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;

public class GameMain extends ApplicationAdapter {
	private PerspectiveCamera camera;
	private AssetManager assets;
	private static ShaderProgram shader;
	private static ShaderProgram texShader;
	public boolean mobile = false;
	protected byte framesPerCycle = 10;
	protected byte WORLD_SIZE = 20;
	private final TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();

	private static String getShader(String path) {
		return Gdx.files.internal(path).readString();
	}

	protected void createMeshShader() {
		ShaderProgram.pedantic = false;
		texShader = new ShaderProgram(
					getShader("VertShaderTex.glsl"),
					getShader("FragShaderTex.glsl"));
		shader = new ShaderProgram(
					getShader("VertShader.glsl"),
					getShader("FragShader.glsl"));
		String log = shader.getLog();
		if (!shader.isCompiled())
			throw new GdxRuntimeException(log);
		if (log!=null && log.length()!=0)
			System.out.println("Shader Log: "+log);
		log = texShader.getLog();
		if (!texShader.isCompiled())
			throw new GdxRuntimeException(log);
		if (log!=null && log.length()!=0)
			System.out.println("Shader Log: "+log);
	}
	private static final float CAM = World.WORLD_VCHUNK *Chunk.CHUNK_SIZE + 1f;

	@Override
	public void create () {
		createMeshShader();
		if(World.TEXTURES_ON) {
			param.genMipMaps = true;
			assets = new AssetManager();
			assets.load("textures/water.png", Texture.class, param);
			assets.load("textures/dirt.png", Texture.class, param);
			assets.load("textures/grassSide.png", Texture.class, param);
			assets.load("textures/grassTop.png", Texture.class, param);
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

	private boolean isLoaded = false;
	private final Int3 i = new Int3();
	private final Int3 cC = new Int3();
	private final Int3 target = new Int3();
	private final ArrayList<ChunkMesh> solidMeshes = new <ChunkMesh>ArrayList<ChunkMesh>();
	private final ArrayList<ChunkMesh> transMeshes = new <ChunkMesh>ArrayList<ChunkMesh>();
	private final ChunkMap<Chunk> toRender = new <Chunk>ChunkMap<Chunk>();
	private final ArrayList<Chunk> garbage = new <Chunk>ArrayList<Chunk>();
	private final ArrayList<Texture> textures = new <Texture>ArrayList<Texture>();
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

		if(World.TEXTURES_ON) {
			if (assets.update()) {
				if (!isLoaded) {
					textures.clear();
					textures.add(assets.get("textures/water.png", Texture.class));
					textures.add(assets.get("textures/dirt.png", Texture.class));
					textures.add(assets.get("textures/grassSide.png", Texture.class));
					textures.add(assets.get("textures/grassTop.png", Texture.class));
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
		if(frameCounter==0) updateVisible();
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

	private void renderWorld() {
		camera.update();
		ShaderProgram shaderOut;
		if(World.TEXTURES_ON && !World.WIREFRAME)
			shaderOut = texShader;
		else
			shaderOut = shader;
		shaderOut.begin();
		if(World.WIREFRAME) Gdx.gl.glClearColor(0f,0f,0f,1f);
		else Gdx.gl.glClearColor(0.494f, 0.753f, 0.93f, 1f);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		shaderOut.setUniformMatrix("u_ProjTrans", camera.combined);
		if(World.TEXTURES_ON && !World.WIREFRAME) {
			shaderOut.setUniformf("u_VectorToLight", World.SUNLIGHT);
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
		textures.get(0).bind(0);
		shaderOut.setUniformi("u_Water", 0);
		textures.get(1).bind(1);
		shaderOut.setUniformi("u_Dirt", 1);
		textures.get(2).bind(2);
		shaderOut.setUniformi("u_GrassSide", 2);
		textures.get(3).bind(3);
		shaderOut.setUniformi("u_GrassTop", 3);
	}
}