package ca.dev9.tranquil;

import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Zaneris on 29/06/2015.
 */
public final class ChunkMeshGenerator {
	private static final byte CHUNK_SIZE = World.CHUNK_SIZE;
	private static final byte POSITION_COMPONENTS = 3;
	private static final byte COLOR_COMPONENTS = 1;
	private static final byte TEXTURE_COORDS = 2;
	public final static byte NUM_COMPONENTS = POSITION_COMPONENTS
			+ (World.TEXTURES_ON ? TEXTURE_COORDS : COLOR_COMPONENTS);
	private static final byte VERTS_PER_TRI = 3;
	private static final byte TRIS_PER_FACE = 2;
	private static final byte FACES_PER_CUBE = 6;

	// Max visible faces would be every other cube rendered.
	public static final int MAX_FLOATS = VERTS_PER_TRI * TRIS_PER_FACE * FACES_PER_CUBE
			* (CHUNK_SIZE/2) * (CHUNK_SIZE/2) * (CHUNK_SIZE/2) * NUM_COMPONENTS;

	private static float[] verts = new float[MAX_FLOATS];
	private static int numFloats;
	private static Block block;
	private static int j;
	private static Int3 p;
	private static final Int3 i = new Int3();
	private static final Int3 target = new Int3();

	public static void createMesh(Chunk chunk) {
		p = chunk.position;
		if(chunk.visSolidFaces>0)
			buildMesh(chunk, chunk.visSolidFaces, true);
		if(chunk.visTransFaces>0)
			buildMesh(chunk, chunk.visTransFaces, false);
		chunk.hasMesh = true;
	}

	private static void buildMesh(Chunk chunk, int faces, boolean solid) {
		numFloats = faces*TRIS_PER_FACE*VERTS_PER_TRI*NUM_COMPONENTS;

		j = 0; // Reset number of floats/vertices
		for (i.newLoop(0, CHUNK_SIZE - 1); i.doneLoop(); i.loop()) {
			block = chunk.getBlock(i);
			if ((block.hasFaces() && block.hasFlag(Block.SOLID) && solid) ||
					(block.hasFaces() && !solid && !block.hasFlag(Block.SOLID))) {
				target.copyPlus(i, p);
				addFaces(block.getSideColor(), block.getTopColor(), block.copyFaces(), solid);
			}
		}
		if(solid) {
			if(chunk.solidMesh==null)
				chunk.solidMesh=new ChunkMesh();
			chunk.solidMesh.setData(verts,j);
		} else {
			if(chunk.transMesh==null)
				chunk.transMesh=new ChunkMesh();
			chunk.transMesh.setData(verts,j);
		}
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
	private static void addFaces(float sideColor, float topColor, byte faces, boolean solid) {
		do {
			x = target.x;
			y = target.y;
			z = target.z;
			if(hasFlag(faces,Block.FACE_BOTTOM)) {
				z += 1f;
				d = bottom;
				c = sideColor;
				faces = removeFlag(faces,Block.FACE_BOTTOM);
			} else if(hasFlag(faces,Block.FACE_SOUTH)) {
				d = south;
				c = sideColor;
				faces = removeFlag(faces, Block.FACE_SOUTH);
			} else if(hasFlag(faces, Block.FACE_EAST)) {
				d = east;
				z += 1f;
				c = sideColor;
				faces = removeFlag(faces, Block.FACE_EAST);
			} else if(hasFlag(faces, Block.FACE_NORTH)) {
				d = north;
				z += 1f;
				x += 1f;
				c = sideColor;
				faces = removeFlag(faces, Block.FACE_NORTH);
			} else if(hasFlag(faces, Block.FACE_WEST)) {
				d = west;
				x += 1f;
				c = sideColor;
				faces = removeFlag(faces, Block.FACE_WEST);
			} else if(hasFlag(faces, Block.FACE_TOP)) {
				y += (solid ? 1f : 0.8f);
				d = top;
				c = topColor;
				faces = removeFlag(faces, Block.FACE_TOP);
			} else break; // <-- Should never actually occur.

			if(World.WIREFRAME) {
				for(idx = 0; idx < 8; idx++) {
					switch (idx) {
						case 0:
						case 7:
							addBottomRight();
							break;
						case 1:
						case 2:
							addTopRight();
							break;
						case 3:
						case 4:
							addTopLeft();
							break;
						case 5:
						case 6:
							addBottomLeft();
					}
					verts[j++] = c;
				}
			} else {
				for (idx = 0; idx < 6; idx++) {
					switch (idx) {
						case 0:
						case 3:
							addBottomRight();
							break;
						case 1:
							addTopRight();
							break;
						case 2:
						case 4:
							addTopLeft();
							break;
						case 5:
							addBottomLeft();
					}
					if (!World.TEXTURES_ON)
						verts[j++] = c;
				}
			}
		} while (faces>0);
	}

	private static void addBottomRight() {
		verts[j++] = x;
		verts[j++] = y;
		verts[j++] = z;
		if (World.TEXTURES_ON) {
			verts[j++] = 1f;
			verts[j++] = 0f;
		}
	}

	private static void addTopRight() {
		verts[j++] = x;
		verts[j++] = y + d.y;
		verts[j++] = d==top || d==bottom ? z + d.z : z;
		if (World.TEXTURES_ON) {
			verts[j++] = 1f;
			verts[j++] = 1f;
		}
	}

	private static void addTopLeft() {
		verts[j++] = x + d.x;
		verts[j++] = y + d.y;
		verts[j++] = z + d.z;
		if (World.TEXTURES_ON) {
			verts[j++] = 0f;
			verts[j++] = 1f;
		}
	}

	private static void addBottomLeft() {
		verts[j++] = x + d.x;
		verts[j++] = y;
		verts[j++] = d==top || d==bottom ? z : z + d.z;
		if (World.TEXTURES_ON) {
			verts[j++] = 0f;
			verts[j++] = 0f;
		}
	}

	private static byte removeFlag(byte faces, byte flag) {
		return (byte)(faces^flag);
	}

	private static boolean hasFlag(byte faces, byte flag) {
		return (byte)(faces&flag)>0;
	}
}