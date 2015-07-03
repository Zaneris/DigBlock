package ca.dev9.tranquil;

import ca.dev9.tranquil.blocks.Block;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Zaneris on 29/06/2015.
 */
public final class ChunkMeshGenerator {
	private static final byte POSITION_COMPONENTS = 3;
	private static final byte COLOR_COMPONENTS = 1;
	private static final byte TEXTURE_COORDS = 2;
	private final static byte NUM_COMPONENTS = POSITION_COMPONENTS
			+ (World.TEXTURES_ON ? TEXTURE_COORDS : COLOR_COMPONENTS);
	private static final byte VERTS_PER_TRI = 3;
	private static final byte TRIS_PER_FACE = 2;
	private static final byte FACES_PER_CUBE = 6;
	private static final int MAX_VERTS = VERTS_PER_TRI * TRIS_PER_FACE * FACES_PER_CUBE
			* Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * NUM_COMPONENTS;

	private static final VertexAttribute a_position =
			new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, "a_position");
	private static final VertexAttribute a_color =
			new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color");
	private static final VertexAttribute a_texCoords =
			new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, TEXTURE_COORDS, "a_texCoords");

	private static final float[] verts = new float[MAX_VERTS];
	private static int numFloats;
	private static Block block;
	private static int x;
	private static int y;
	private static int z;
	private static int i;

	public static void createMesh(Chunk chunk) {
		if (chunk.hasMesh) chunk.mesh.dispose();
		numFloats = chunk.visibleFaces * TRIS_PER_FACE * VERTS_PER_TRI * NUM_COMPONENTS;
		//System.out.println(numFloats + " Max floats");
		chunk.mesh = new Mesh(true, numFloats, 0, a_position, (World.TEXTURES_ON ? a_texCoords : a_color));

		i = 0;

		for(y=chunk.yOff;y<chunk.yOff+Chunk.CHUNK_SIZE;y++) {
			for(x=chunk.xOff;x<chunk.xOff+Chunk.CHUNK_SIZE;x++) {
				for(z=chunk.zOff;z<chunk.zOff+Chunk.CHUNK_SIZE;z++) {
					if(chunk.blocks[x-chunk.xOff][y-chunk.yOff][z-chunk.zOff].visibleFaces>0) {
						block = chunk.blocks[x-chunk.xOff][y-chunk.yOff][z-chunk.zOff];
						if (block.hasFlag(Block.FACE_BOTTOM))
							addFace(x,y,z,Block.FACE_BOTTOM,block.getSideColor());
						if (block.hasFlag(Block.FACE_NORTH))
							addFace(x,y,z,Block.FACE_NORTH,block.getSideColor());
						if (block.hasFlag(Block.FACE_SOUTH))
							addFace(x,y,z,Block.FACE_SOUTH,block.getSideColor());
						if (block.hasFlag(Block.FACE_EAST))
							addFace(x,y,z,Block.FACE_EAST,block.getSideColor());
						if (block.hasFlag(Block.FACE_WEST))
							addFace(x,y,z,Block.FACE_WEST,block.getSideColor());
						if (block.hasFlag(Block.FACE_TOP))
							addFace(x, y, z, Block.FACE_TOP,block.getTopColor());
					}
				}
			}
		}
		chunk.mesh.setVertices(verts,0,i);
		chunk.hasMesh = true;
	}

	private static final Vector3 north 	= new Vector3(-1f,1f, 0f);
	private static final Vector3 south 	= new Vector3( 1f,1f, 0f);
	private static final Vector3 east 	= new Vector3( 0f,1f,-1f);
	private static final Vector3 west 	= new Vector3( 0f,1f, 1f);
	private static final Vector3 top 	= new Vector3( 1f,0f, 1f);
	private static final Vector3 bottom = new Vector3( 1f,0f,-1f);

	private static Vector3 d;
	private static int idx = 0;

	private static void addFace(float x, float y, float z, byte face, float c) {
		switch(face) {
			case Block.FACE_SOUTH:
				d = south;
				break;
			case Block.FACE_EAST:
				d = east;
				z+=1f;
				break;
			case Block.FACE_NORTH:
				d = north;
				z+=1f;
				x+=1f;
				break;
			case Block.FACE_WEST:
				d = west;
				x+=1f;
				break;
			case Block.FACE_TOP:
				y+=1f;
				d = top;
				break;
			case Block.FACE_BOTTOM:
			default:
				z+=1f;
				d = bottom;
				break;
		}

		for(idx = 0; idx<6; idx++) {
			switch (idx) {
				case 0:
				case 3:
					verts[i++] = x;
					verts[i++] = y;
					verts[i++] = z;
					if (World.TEXTURES_ON) {
						verts[i++] = 1f;
						verts[i++] = 0f;
					} else
						verts[i++] = c;
					break;
				case 1:
					verts[i++] = x;
					verts[i++] = y + d.y;
					verts[i++] = (face & Block.FACE_BOTTOM) + (face & Block.FACE_TOP) > 0 ? z + d.z : z;
					if (World.TEXTURES_ON) {
						verts[i++] = 1f;
						verts[i++] = 1f;
					} else
						verts[i++] = c;
					break;
				case 2:
				case 4:
					verts[i++] = x + d.x;
					verts[i++] = y + d.y;
					verts[i++] = z + d.z;
					if (World.TEXTURES_ON) {
						verts[i++] = 0f;
						verts[i++] = 1f;
					} else
						verts[i++] = c;
					break;
				case 5:
					verts[i++] = x + d.x;
					verts[i++] = y;
					verts[i++] = (face & Block.FACE_BOTTOM) + (face & Block.FACE_TOP) > 0 ? z : z + d.z;
					if (World.TEXTURES_ON) {
						verts[i++] = 0f;
						verts[i++] = 0f;
					} else
						verts[i++] = c;
					break;
			}
		}
	}
}