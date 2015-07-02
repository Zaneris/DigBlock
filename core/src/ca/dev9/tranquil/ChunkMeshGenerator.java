package ca.dev9.tranquil;

import ca.dev9.tranquil.blocks.Block;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Zaneris on 29/06/2015.
 */
public final class ChunkMeshGenerator {
    private static final float CUBE_SIZE = 1f;
    private static final byte POSITION_COMPONENTS = 3;
    private static final byte COLOR_COMPONENTS = 4;
    private final static byte NUM_COMPONENTS = POSITION_COMPONENTS + COLOR_COMPONENTS;
    private static final byte VERTS_PER_TRI = 3;
    private static final byte TRIS_PER_FACE = 2;
    private static final byte FACES_PER_CUBE = 6;
    private static final int MAX_VERTS = VERTS_PER_TRI * TRIS_PER_FACE * FACES_PER_CUBE
            * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * NUM_COMPONENTS;

    private static final VertexAttribute a_position =
            new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, "a_position");
    private static final VertexAttribute a_color =
            new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, COLOR_COMPONENTS, "a_color");

    private static final float[] verts = new float[MAX_VERTS];
    private static int numFloats;
    private static Block block;
    private static byte x;
    private static byte y;
    private static byte z;
    private static int i;

    public static void createMesh(Chunk chunk) {
        if (chunk.hasMesh) chunk.mesh.dispose();
        numFloats = chunk.visibleFaces * TRIS_PER_FACE * VERTS_PER_TRI * NUM_COMPONENTS;
        System.out.println(numFloats + " Max floats");
        chunk.mesh = new Mesh(true, numFloats, 0, a_position, a_color);

        i = 0;
        for(x=0;x<Chunk.CHUNK_SIZE;x++) {
            for(y=0;y<Chunk.CHUNK_SIZE;y++) {
                for(z=0;z<Chunk.CHUNK_SIZE;z++) {
                    if(chunk.blocks[x][y][z].visibleFaces>0) {
                        block = chunk.blocks[x][y][z];
                        if (block.hasFlag(Block.FACE_TOP))
                            addFace(x, y, z, Block.FACE_TOP,block.getTopColor());
                        if (block.hasFlag(Block.FACE_NORTH))
                            addFace(x,y,z,Block.FACE_NORTH,block.getSideColor());
                        if (block.hasFlag(Block.FACE_SOUTH))
                            addFace(x,y,z,Block.FACE_SOUTH,block.getSideColor());
                        if (block.hasFlag(Block.FACE_EAST))
                            addFace(x,y,z,Block.FACE_EAST,block.getSideColor());
                        if (block.hasFlag(Block.FACE_WEST))
                            addFace(x,y,z,Block.FACE_WEST,block.getSideColor());
                        if (block.hasFlag(Block.FACE_BOTTOM))
                            addFace(x,y,z,Block.FACE_BOTTOM,block.getSideColor());
                    }
                }
            }
        }
        chunk.mesh.setVertices(verts,0,i);
        chunk.vertexCount = i;
        System.out.println(i + " Floats set");
        chunk.hasMesh = true;
    }

    private static final Vector3 south = new Vector3(1f,1f,0f).scl(CUBE_SIZE);
    private static final Vector3 west = new Vector3(0f,1f,1f).scl(CUBE_SIZE);
    private static final Vector3 east = new Vector3(0f,1f,-1f).scl(CUBE_SIZE);
    private static final Vector3 north = new Vector3(-1f,1f,0f).scl(CUBE_SIZE);
    private static final Vector3 top = new Vector3(1f,0f,-1f).scl(CUBE_SIZE);
    private static final Vector3 bottom = new Vector3(1f,0f,1f).scl(CUBE_SIZE);
    private static Vector3 d;

    private static void addFace(byte x, byte y, byte z, byte face, Color color) {
        switch(face) {
            case Block.FACE_SOUTH:
                d = south;
                break;
            case Block.FACE_WEST:
                z--;
                d = west;
                break;
            case Block.FACE_TOP:
                y++;
                d = top;
                break;
            case Block.FACE_EAST:
                x++;
                d = east;
                break;
            case Block.FACE_NORTH:
                x++;
                z--;
                d = north;
                break;
            case Block.FACE_BOTTOM:
            default:
                z--;
                d = bottom;
                break;
        }

        //bottom left vertex
        verts[i++] = x;         //Position(x, y)
        verts[i++] = y;
        verts[i++] = z;
        addColor(color);

        //top right vertex
        verts[i++] = x + d.x;   //Position(x, y)
        verts[i++] = y + d.y;
        verts[i++] = z + d.z;
        addColor(color);

        //top left vertex
        verts[i++] = x;         //Position(x, y)
        verts[i++] = y + d.y;
        verts[i++] = (face & Block.FACE_BOTTOM) + (face & Block.FACE_TOP) > 0 ? z + d.z : z;
        addColor(color);

        //bottom left vertex
        verts[i++] = x;         //Position(x, y)
        verts[i++] = y;
        verts[i++] = z;
        addColor(color);

        //bottom right vertex
        verts[i++] = x + d.x;   //Position(x, y)
        verts[i++] = y;
        verts[i++] = (face & Block.FACE_BOTTOM) + (face & Block.FACE_TOP) > 0 ? z : z + d.z;
        addColor(color);

        //top right vertex
        verts[i++] = x + d.x;   //Position(x, y)
        verts[i++] = y + d.y;
        verts[i++] = z + d.z;
        addColor(color);
    }

    private static void addColor(Color color) {
        verts[i++] = color.r;   //Color(r, g, b, a)
        verts[i++] = color.g;
        verts[i++] = color.b;
        verts[i++] = color.a;
    }
}