package ca.dev9.tranquil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
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
	private static final VertexAttributes attribs =
			new VertexAttributes(a_position, (World.TEXTURES_ON ? a_texCoords : a_color));

	private VertexData vertexData;

	public ChunkMesh(int floats) {
		vertexData = new VertexBufferObject(true,floats,attribs);
	}

	public void setData(float[] floats, int size) {
		vertexData.setVertices(floats,0,size);
	}

	public void render() {
		vertexData.bind(GameMain.shader);
		Gdx.gl20.glDrawArrays(Gdx.gl.GL_TRIANGLES, 0, vertexData.getNumVertices());
		vertexData.unbind(GameMain.shader);
	}

	public void dispose() {
		vertexData.dispose();
	}
}
