package ca.dev9.tranquil;

import ca.dev9.tranquil.chunk.Chunk;
import ca.dev9.tranquil.chunk.ChunkMap;
import ca.dev9.tranquil.chunk.ChunkMesh;
import ca.dev9.tranquil.screens.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.graphics.glutils.VertexData;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;

import java.util.ArrayList;

/**
 * Manages all rendering within the engine.
 * @author Zaneris
 */
public final class Graphics {
	private static final boolean DEBUG = Config.DEBUG;

	private static ShaderProgram shaderWire;
	private static ShaderProgram shaderTex;
	private static ShaderProgram shaderDepth;
	private static ShaderProgram shaderOut;
	private static TextureLoader.TextureParameter param;
	private static AssetManager assets;
	private static ArrayList<Texture> textures;
	private static FrameBuffer frameBuffer;

	private static String getShader(String path) {
		return Gdx.files.internal(path).readString();
	}

	public static void loadShaders() {
		ShaderProgram.pedantic = false;
		shaderTex = new ShaderProgram(
				getShader("shaders/VertTex.glsl"),
				getShader("shaders/FragTex.glsl"));
		shaderWire = new ShaderProgram(
				getShader("shaders/VertWire.glsl"),
				getShader("shaders/FragWire.glsl"));
		shaderDepth = new ShaderProgram(
				getShader("shaders/VertDepth.glsl"),
				getShader("shaders/FragDepth.glsl"));
		if(DEBUG) {
			String log = shaderTex.getLog();
			if (!shaderTex.isCompiled())
				throw new GdxRuntimeException(log);
			log = shaderWire.getLog();
			if (!shaderWire.isCompiled())
				throw new GdxRuntimeException(log);
			log = shaderDepth.getLog();
			if (!shaderDepth.isCompiled())
				throw new GdxRuntimeException(log);
		}
	}

	public static void loadAssets() {
		param = new TextureLoader.TextureParameter();
		param.genMipMaps = true;
		assets = new AssetManager();
		assets.load("textures/Water.png", Texture.class, param);
		assets.load("textures/Dirt.png", Texture.class, param);
		assets.load("textures/GrassSide.png", Texture.class, param);
		assets.load("textures/GrassTop.png", Texture.class, param);
		if(Config.MOBILE)
			frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,1024,1024,true);
		else
			frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,2048,2048,true);
	}
	
	public static boolean checkAssets() {
		if(textures!=null)
			return true;
		if (assets.update()) {
			textures = new ArrayList<>();
			textures.add(assets.get("textures/Water.png", Texture.class));
			textures.add(assets.get("textures/Dirt.png", Texture.class));
			textures.add(assets.get("textures/GrassSide.png", Texture.class));
			textures.add(assets.get("textures/GrassTop.png", Texture.class));
			for (Texture tex : textures)
				tex.setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.Nearest);
			return true;
		}
		return false;
	}
	
	public static void startRender(Camera cam, Camera light, Texture depthMap) {
		shaderOut = shaderTex;
		shaderTex.begin();
		bindTextures(depthMap);
		shaderTex.setUniformMatrix("u_CamMatrix", cam.combined);
		shaderTex.setUniformMatrix("u_LightMatrix", light.combined);
		shaderTex.setUniformf("u_LightVector", light.direction);
		shaderTex.setUniformf("u_Alpha", 1f);
	}
	
	public static void endRender() {
		shaderTex.end();
		shaderOut = null;
	}
	
	public static void startDepth(Camera light) {
		frameBuffer.begin();
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_BACK);
		shaderOut = shaderDepth;
		shaderDepth.begin();
		shaderDepth.setUniformMatrix("u_LightMatrix", light.combined);
	}
	
	public static Texture endDepth() {
		shaderDepth.end();
		shaderOut = null;
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		frameBuffer.end();
		return frameBuffer.getColorBufferTexture();
	}
	
	private static void bindTextures(Texture depthMap) {
		if(depthMap!=null) {
			depthMap.bind(0);
			shaderTex.setUniformi("u_DepthMap", 0);
		}
		textures.get(0).bind(1);
		shaderTex.setUniformi("u_Water", 1);
		textures.get(1).bind(2);
		shaderTex.setUniformi("u_Dirt", 2);
		textures.get(2).bind(3);
		shaderTex.setUniformi("u_GrassSide", 3);
		textures.get(3).bind(4);
		shaderTex.setUniformi("u_GrassTop", 4);
	}
	
	public static void startSolid() {
		Gdx.gl.glClearColor(0.494f, 0.753f, 0.93f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_BACK);
	}
	
	public static void renderMesh(ChunkMesh mesh) {
		mesh.render(shaderOut);
	}
	
	public static void endSolid() {
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
	}
	
	public static void startTrans() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shaderTex.setUniformf("u_Alpha", 0.7f);
	}
	
	public static void endTrans() {
		Gdx.gl.glDisable(GL20.GL_BLEND);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
	}
}
