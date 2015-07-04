package ca.dev9.tranquil;

import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class GameMain extends ApplicationAdapter {
	private static PerspectiveCamera camera;
	private static ShaderProgram shader;
	private static AssetManager assets;

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
	private static final short WORLD_SIZE = 12;
	private static final float CAM = World.WORLD_HEIGHT*Chunk.CHUNK_SIZE;

	@Override
	public void create () {
		createMeshShader();
		assets = new AssetManager();
		assets.load("dirt.png", Texture.class);
		camera = new PerspectiveCamera(75f,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.set(-50f, CAM, -50f);
		float halfWorld = WORLD_SIZE*8f+8f;
		camera.lookAt(halfWorld,0f,halfWorld);
		camera.near = 1.0f;
		camera.far = 5000f;

		World.createNewWorld();
	}

	private static Texture tex;
	private static boolean isLoaded = false;
	private static int r;
	private static final Int3 i = new Int3();
	private static final Int3 cC = new Int3();

	@Override
	public void render () {
		World.buildChunks();
		World.updateFaces();
		World.createMeshes();
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		//this will render the triangles to GL
		if(assets.update()) {
			if (!isLoaded) {
				tex = assets.get("dirt.png", Texture.class);
				isLoaded = true;
			}
			flush();
		}
	}
	Chunk chunk;
	void flush() {
		//enable blending, for alpha
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_BACK);

		camera.update();

		//start the shader before setting any uniforms
		shader.begin();
		if(World.TEXTURES_ON) {
			tex.bind();
			shader.setUniformi("u_diffuseTexture", 0);
		}
		shader.setUniformMatrix("u_projTrans", camera.combined);

		cC.set(camera.position);
		cC.div(Chunk.CHUNK_SIZE);
		int j = 0;
		for(r = 0; r<WORLD_SIZE; r++)
			for(i.y = -r; i.y<=r; i.y++)
				if(cC.y+i.y>=0 && cC.y+i.y<World.WORLD_HEIGHT)
					for(i.x = -r; i.x<=r; i.x++)
						if(Math.abs(cC.x+i.x)<32768)
							for(i.z = -r; i.z<=r; i.z++)
								if(Math.abs(cC.z+i.z)<32768)
									if(Math.abs(i.x)==r || Math.abs(i.y)==r || Math.abs(i.z)==r) {
										chunk = World.chunkMap.get(cC.x+i.x, cC.y+i.y, cC.z+i.z);
										if(chunk == null) {
											chunk = new Chunk(cC.x + i.x, cC.y + i.y, cC.z + i.z);
											World.buildQueue.add(chunk);
											chunk.addToMap();
										}
										else if (chunk.hasMesh) {
											chunk.mesh.render(shader, GL20.GL_TRIANGLES, 0, chunk.mesh.getNumVertices());
										}
									}
		shader.end();
	}
}