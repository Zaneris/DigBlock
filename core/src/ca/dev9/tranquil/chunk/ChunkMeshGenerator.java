package ca.dev9.tranquil.chunk;

import ca.dev9.tranquil.Config;
import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Zaneris on 29/06/2015.
 */
public final class ChunkMeshGenerator {
	private static final byte CHUNK_SIZE = Chunk.CHUNK_SIZE;
	private static final byte MAX_COMPONENTS = 9;
	private static final byte VERTS_PER_TRI = 3;
	private static final byte TRIS_PER_FACE = 2;
	private static final byte FACES_PER_CUBE = 6;

	// Max visible faces would be every other cube rendered.
	public static final int MAX_FLOATS = VERTS_PER_TRI * TRIS_PER_FACE * FACES_PER_CUBE
			* (CHUNK_SIZE/2) * (CHUNK_SIZE/2) * (CHUNK_SIZE/2) * MAX_COMPONENTS;

	private static float[] verts = new float[MAX_FLOATS];
	private static Block block;
	private static int j;
	private static Int3 p;
	private static final Int3 i = new Int3();
	private static final Int3 target = new Int3();
	private static boolean addUV;

	public static void createMesh(Chunk chunk) {
		p = chunk.position;
		addUV = Config.TEXTURES_ON && !Config.WIREFRAME;
		buildMesh(chunk, true);
		buildMesh(chunk, false);
	}

	private static void buildMesh(Chunk chunk, boolean solid) {
		j = 0; // Reset number of floats/vertices
		for (i.newLoop(0, CHUNK_SIZE - 1); i.doneLoop(); i.loop()) {
			block = chunk.getBlock(i).block;
			if ((block.hasFaces() && block.hasFlag(Block.SOLID) && solid) ||
					(block.hasFaces() && !solid && !block.hasFlag(Block.SOLID))) {
				target.copyPlus(i, p);
				addFaces(block.getSideColor(), block.getTopColor(),
						block.getSideTexture(), block.getTopTexture(),
						block.copyFaces(), solid);
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

	private static final byte n_Normal = 0;
	private static final byte s_Normal = 1;
	private static final byte e_Normal = 2;
	private static final byte w_Normal = 3;
	private static final byte t_Normal = 4;
	private static final byte b_Normal = 5;

	private static Vector3 d;
	private static int idx = 0;

	private static float x,y,z,c;
	private static int tex;
	private static void addFaces(float sideColor, float topColor, byte sideTex, byte topTex, byte faces, boolean solid) {
		do {
			x = target.x;
			y = target.y;
			z = target.z;
			c = sideColor;
			if(hasFlag(faces,Block.FACE_SOUTH)) {
				d = south;
				tex = sideTex*6+s_Normal;
				faces = removeFlag(faces, Block.FACE_SOUTH);
			} else if(hasFlag(faces, Block.FACE_EAST)) {
				d = east;
				tex = sideTex*6+e_Normal;
				z += 1f;
				faces = removeFlag(faces, Block.FACE_EAST);
			} else if(hasFlag(faces, Block.FACE_NORTH)) {
				d = north;
				tex = sideTex*6+n_Normal;
				z += 1f;
				x += 1f;
				faces = removeFlag(faces, Block.FACE_NORTH);
			} else if(hasFlag(faces, Block.FACE_WEST)) {
				d = west;
				tex = sideTex*6+w_Normal;
				x += 1f;
				faces = removeFlag(faces, Block.FACE_WEST);
			} else if(hasFlag(faces, Block.FACE_TOP)) {
				y += (solid ? 1f : 0.7f);
				d = top;
				tex = topTex*6+t_Normal;
				c = topColor;
				faces = removeFlag(faces, Block.FACE_TOP);
			} else if(hasFlag(faces,Block.FACE_BOTTOM)) {
				z += 1f;
				d = bottom;
				tex = sideTex*6+b_Normal;
				faces = removeFlag(faces,Block.FACE_BOTTOM);
			} else break; // <-- Should never actually occur.

			if(Config.WIREFRAME) {
				for(idx = 0; idx < 4; idx++) {
					if(idx==0) {
						addBottomRight();
						addTopRight();
					} else if (idx==1 && !(d!=top && d!=bottom && hasFlag(faces, Block.FACE_TOP))) {
						addTopRight();
						addTopLeft();
					} else if(idx==2) {
						addTopLeft();
						addBottomLeft();
					} else if (idx==3 && d==top) {
						addBottomLeft();
						addBottomRight();
					}
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
				}
			}
		} while (faces>0);
	}

	private static void addBottomRight() {
		verts[j++] = x;
		verts[j++] = y;
		verts[j++] = z;
		if (addUV) {
			verts[j++] = tex*4+3;
		} else {
			verts[j++] = c;
		}
 	}

	private static void addTopRight() {
		verts[j++] = x;
		verts[j++] = y + d.y;
		verts[j++] = d==top || d==bottom ? z + d.z : z;
		if (addUV) {
			verts[j++] = tex*4+2;
		} else {
			verts[j++] = c;
		}
	}

	private static void addTopLeft() {
		verts[j++] = x + d.x;
		verts[j++] = y + d.y;
		verts[j++] = z + d.z;
		if (addUV) {
			verts[j++] = tex*4;
		} else {
			verts[j++] = c;
		}
	}

	private static void addBottomLeft() {
		verts[j++] = x + d.x;
		verts[j++] = y;
		verts[j++] = d==top || d==bottom ? z : z + d.z;
		if (addUV) {
			verts[j++] = tex*4+1;
		} else {
			verts[j++] = c;
		}
	}

	private static byte removeFlag(byte faces, byte flag) {
		return (byte)(faces^flag);
	}

	private static boolean hasFlag(byte faces, byte flag) {
		return (byte)(faces&flag)>0;
	}
}