package ca.dev9.tranquil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.graphics.glutils.VertexData;

/**
 * Created by Zaneris on 06/07/2015.
 */
public class ChunkMesh {
	private static final int MAX_FLOATS = ChunkMeshGenerator.MAX_FLOATS;
	private static final VertexAttribute a_position =
			new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position");
	private static final VertexAttribute a_color =
			new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color");
	private static final VertexAttribute a_texCoords =
			new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoords");
	private static final VertexAttributes attribs =
			new VertexAttributes(a_position, (World.TEXTURES_ON ? a_texCoords : a_color));

	private VertexData vertexData;
	private int vertices;

	public void setData(float[] floats, int size) {
		if(vertexData!=null)
			vertexData.dispose();
		vertexData = new VertexBufferObject(true, size, attribs);
		vertexData.setVertices(floats,0,size);
		vertices = size/ChunkMeshGenerator.NUM_COMPONENTS;
	}

	public void render() {
		vertexData.bind(GameMain.shader);
		if(World.WIREFRAME)
			Gdx.gl20.glDrawArrays(GL20.GL_LINES, 0, vertices);
		else
			Gdx.gl20.glDrawArrays(GL20.GL_TRIANGLES, 0, vertices);
		vertexData.unbind(GameMain.shader);
	}

	public void reset() {
		vertices = 0;
	}
}
