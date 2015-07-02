package ca.dev9.tranquil;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
        ShaderProgram shader = new ShaderProgram(
                getShader("VertShader.glsl"),
                getShader("FragShader.glsl"));
        String log = shader.getLog();
        if (!shader.isCompiled())
            throw new GdxRuntimeException(log);
        if (log!=null && log.length()!=0)
            System.out.println("Shader Log: "+log);
        return shader;
    }

    ShaderProgram shader;
    private Chunk chunk;

	@Override
	public void create () {
        shader = createMeshShader();
		camera = new PerspectiveCamera(75f,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.set(-3f, 2f, -3f);
		camera.lookAt(0f, 0f, 0f);
		camera.near = 0.1f;
		camera.far = 300f;

        chunk = new Chunk();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        //this will render the triangles to GL
        flush();
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

        //update the projection matrix
        shader.setUniformMatrix("u_projTrans", camera.combined);

        //render the mesh
        chunk.mesh.render(shader, GL20.GL_TRIANGLES, 0, chunk.vertexCount);

        shader.end();
    }
}
