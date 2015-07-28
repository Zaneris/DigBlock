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
	private static TextureLoader.TextureParameter param;
	private static AssetManager assets;
	private static ArrayList<Texture> textures;
	private static FrameBuffer frameBuffer;
	private static SpriteBatch batch;

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
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,Config.DRAW_DIST*102,Config.DRAW_DIST*102,true);
	}
	
	public static boolean checkAssets() {
		if(textures!=null)
			return true;
		if (assets.update()) {
			if(textures==null) {
				textures = new ArrayList<>();
				textures.add(assets.get("textures/Water.png", Texture.class));
				textures.add(assets.get("textures/Dirt.png", Texture.class));
				textures.add(assets.get("textures/GrassSide.png", Texture.class));
				textures.add(assets.get("textures/GrassTop.png", Texture.class));
				for (Texture tex : textures)
					tex.setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.Nearest);
			}
			return true;
		}
		return false;
	}

	public static Texture updateDepthMap(Camera lightSource, ArrayList<ChunkMesh> meshSource, ChunkMap<Chunk> chunkMap, boolean clear) {
		frameBuffer.begin();
		if(clear) {
			Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		}
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_BACK);
			shaderDepth.begin();
				shaderDepth.setUniformMatrix("u_LightMatrix", lightSource.combined);
				if(clear) {
					Chunk chunk;
					for(IntMap.Entry entry:chunkMap) {
						chunk = (Chunk)entry.value;
						if (chunk.built && chunk.hasSolidMesh())
							chunk.solidMesh.render(shaderDepth);
					}
				} else for (ChunkMesh tR : meshSource)
					if (tR.vertices > 0)
						tR.render(shaderDepth);
			shaderDepth.end();
			Gdx.gl.glDisable(GL20.GL_CULL_FACE);
			Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		frameBuffer.end();
		return frameBuffer.getColorBufferTexture();
	}
	
	private static void bindTextures(ShaderProgram shaderOut, Texture depthMap) {
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

	public static void renderChunks(Camera playerCam, Camera lightSource, ArrayList<ChunkMesh> solidMeshes, ArrayList<ChunkMesh> transMeshes, Texture depthMap) {
		ShaderProgram shaderOut;
		if(Config.WIREFRAME)
			shaderOut = shaderWire;
		else
			shaderOut = shaderTex;
		if(Config.WIREFRAME) Gdx.gl.glClearColor(0f,0f,0f,1f);
		else Gdx.gl.glClearColor(0.494f, 0.753f, 0.93f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		shaderOut.begin();
		shaderOut.setUniformMatrix("u_CamMatrix", playerCam.combined);
		if(!Config.WIREFRAME) {
			shaderOut.setUniformMatrix("u_LightMatrix", lightSource.combined);
			shaderOut.setUniformf("u_LightVector", lightSource.direction);
			bindTextures(shaderOut, depthMap);
			shaderOut.setUniformf("u_Alpha", 1f);
		}
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_BACK);
		for(ChunkMesh tR:solidMeshes)
			if (tR.vertices>0)
				tR.render(shaderOut);
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		if(!Config.WIREFRAME)
			shaderOut.setUniformf("u_Alpha", 0.7f);
		for(ChunkMesh tR:transMeshes)
			if(tR.vertices>0)
				tR.render(shaderOut);
		Gdx.gl.glDisable(GL20.GL_BLEND);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		shaderOut.end();
	}
}
