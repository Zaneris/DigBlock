package ca.dev9.tranquil;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class GameMain extends ApplicationAdapter {
	private PerspectiveCamera camera;

	private static String getShader(String path) {
		return Gdx.files.local(path).readString();
	}

	protected static ShaderProgram createMeshShader() {
		ShaderProgram.pedantic = false;
		ShaderProgram shader;
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
		return shader;
	}

	ShaderProgram shader;
	AssetManager assets;
	private static final short WORLD_SIZE = 32;
	private static final float CAM = World.WORLD_HEIGHT*Chunk.CHUNK_SIZE;

	@Override
	public void create () {
		shader = createMeshShader();
		assets = new AssetManager();
		assets.load("dirt.png", Texture.class);
		camera = new PerspectiveCamera(75f,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.set(10f, CAM, 10f);
		float halfWorld = WORLD_SIZE*8f+8f;
		camera.lookAt(halfWorld,0f,halfWorld);
		camera.near = 1.0f;
		camera.far = 5000f;

		World.createWorld(WORLD_SIZE,WORLD_SIZE);
	}

	Texture tex;
	boolean isLoaded = false;

	@Override
	public void render () {
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

		//render the mesh
		for(Chunk chunks[][]:World.chunks)
			for(Chunk chunks2[]:chunks)
				for(Chunk chunk: chunks2)
					chunk.mesh.render(shader, GL20.GL_TRIANGLES, 0, chunk.mesh.getNumVertices());

		shader.end();
	}
}
