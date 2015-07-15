package ca.dev9.tranquil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

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

	private static String getShader(String path) {
		return Gdx.files.internal(path).readString();
	}

	public static void loadShaders() {
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

	public static void loadTextures() {
		param = new TextureLoader.TextureParameter();
		param.genMipMaps = true;
		assets = new AssetManager();
		assets.load("textures/Water.png", Texture.class, param);
		assets.load("textures/Dirt.png", Texture.class, param);
		assets.load("textures/GrassSide.png", Texture.class, param);
		assets.load("textures/GrassTop.png", Texture.class, param);
	}
}
