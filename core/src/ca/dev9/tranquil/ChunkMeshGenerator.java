package ca.dev9.tranquil;

import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Zaneris on 29/06/2015.
 */
public final class ChunkMeshGenerator {
	private static final byte CHUNK_SIZE = World.CHUNK_SIZE;
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
	private static int j;
	private static final Int3 p = new Int3();
	private static final Int3 i = new Int3();
	private static final Int3 target = new Int3();
	private static byte faces;

	public static void createMesh(Chunk chunk) {
		if (chunk.mesh != null) chunk.mesh.dispose();
		numFloats = chunk.visibleFaces * TRIS_PER_FACE * VERTS_PER_TRI * NUM_COMPONENTS;
		chunk.mesh = new Mesh(true, numFloats, 0, a_position, (World.TEXTURES_ON ? a_texCoords : a_color));

		j = 0; // Reset number of floats/vertices
		p.copyFrom(chunk.getChunkPosition());
		for(i.newLoop(0,CHUNK_SIZE-1);i.doneLoop();i.loop()) {
			block = chunk.getBlock(i);
			if (block.hasFaces()) {
				faces = block.copyFaces();
				target.copyPlus(i, p);
				addFaces(block.getSideColor(), block.getTopColor());
			}
		}
		// Add vertices to new mesh
		chunk.mesh.setVertices(verts,0,j);
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

	private static float x,y,z,c;
	private static void addFaces(float sideColor, float topColor) {
		do {
			x = target.x;
			y = target.y;
			z = target.z;
			if(hasFlag(Block.FACE_BOTTOM)) {
				z += 1f;
				d = bottom;
				c = sideColor;
				removeFlag(Block.FACE_BOTTOM);
			} else if(hasFlag(Block.FACE_SOUTH)) {
				d = south;
				c = sideColor;
				removeFlag(Block.FACE_SOUTH);
			} else if(hasFlag(Block.FACE_EAST)) {
				d = east;
				z += 1f;
				c = sideColor;
				removeFlag(Block.FACE_EAST);
			} else if(hasFlag(Block.FACE_NORTH)) {
				d = north;
				z += 1f;
				x += 1f;
				c = sideColor;
				removeFlag(Block.FACE_NORTH);
			} else if(hasFlag(Block.FACE_WEST)) {
				d = west;
				x += 1f;
				c = sideColor;
				removeFlag(Block.FACE_WEST);
			} else if(hasFlag(Block.FACE_TOP)) {
				y += 1f;
				d = top;
				c = topColor;
				removeFlag(Block.FACE_TOP);
			} else break; // <-- Should never actually occur.

			for (idx = 0; idx < 6; idx++) {
				switch (idx) {
					case 0:
					case 3:
						verts[j++] = x;
						verts[j++] = y;
						verts[j++] = z;
						if (World.TEXTURES_ON) {
							verts[j++] = 1f;
							verts[j++] = 0f;
						} else
							verts[j++] = c;
						break;
					case 1:
						verts[j++] = x;
						verts[j++] = y + d.y;
						verts[j++] = d==top || d==bottom ? z + d.z : z;
						if (World.TEXTURES_ON) {
							verts[j++] = 1f;
							verts[j++] = 1f;
						} else
							verts[j++] = c;
						break;
					case 2:
					case 4:
						verts[j++] = x + d.x;
						verts[j++] = y + d.y;
						verts[j++] = z + d.z;
						if (World.TEXTURES_ON) {
							verts[j++] = 0f;
							verts[j++] = 1f;
						} else
							verts[j++] = c;
						break;
					case 5:
						verts[j++] = x + d.x;
						verts[j++] = y;
						verts[j++] = d==top || d==bottom ? z : z + d.z;
						if (World.TEXTURES_ON) {
							verts[j++] = 0f;
							verts[j++] = 0f;
						} else
							verts[j++] = c;
						break;
				}
			}
		} while (faces>0);
	}

	private static void removeFlag(byte flag) {
		faces = (byte)(faces^flag);
	}

	private static boolean hasFlag(byte flag) {
		return (byte)(faces&flag)>0;
	}
}