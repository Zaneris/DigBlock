package ca.dev9.tranquil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.graphics.glutils.VertexData;

/**
 * Created by Zaneris on 06/07/2015.
 */
public class ChunkMesh {
	private static final VertexAttribute a_position =
			new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position");
	private static final VertexAttribute a_color =
			new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color");
	private static final VertexAttribute a_texCoords =
			new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoords");
	private static final VertexAttribute a_tex =
			new VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_tex");

	private VertexData vertexData;
	public int vertices;

	public void setData(float[] floats, int size) {
		if(vertexData!=null)
			vertexData.dispose();
		vertexData = new VertexBufferObject(true, size, a_position,
				World.TEXTURES_ON && !World.WIREFRAME ? a_texCoords : a_color, a_tex);
		vertexData.setVertices(floats,0,size);
		vertices = size/(World.TEXTURES_ON && !World.WIREFRAME ? 6 : 5);
	}

	public void render(ShaderProgram shader) {
		vertexData.bind(shader);
		if(World.WIREFRAME)
			Gdx.gl20.glDrawArrays(GL20.GL_LINES, 0, vertices);
		else
			Gdx.gl20.glDrawArrays(GL20.GL_TRIANGLES, 0, vertices);
		vertexData.unbind(shader);
	}

	public void reset() {
		vertices = 0;
		if(vertexData!=null)
			vertexData.dispose();
		vertexData = null;
	}
}
