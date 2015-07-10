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
	private static final VertexAttribute a_Position =
			new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_Position");
	private static final VertexAttribute a_Color =
			new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_Color");
	private static final VertexAttribute a_TexCoords =
			new VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_TexCoords");
	private static final VertexAttribute a_TexNormal =
			new VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_TexNormal");

	private VertexData vertexData;
	public int vertices;

	public void setData(float[] floats, int size) {
		if(vertexData!=null)
			vertexData.dispose();
		if(World.TEXTURES_ON && !World.WIREFRAME)
			vertexData = new VertexBufferObject(true,size,a_Position,a_TexNormal);
		else
			vertexData = new VertexBufferObject(true,size,a_Position,a_Color);
		vertexData.setVertices(floats,0,size);
		vertices = size/4;
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
