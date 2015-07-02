package ca.dev9.tranquil;

import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.blocks.Dirt;
import ca.dev9.tranquil.blocks.Grass;
import com.badlogic.gdx.graphics.Mesh;

/**
 * Created by Zaneris on 29/06/2015.
 */
public class Chunk {
    public static final byte CHUNK_SIZE = 16; // 32 max
    public final Block[][][] blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
    public int visibleFaces = 0;
    public Mesh mesh;
    public int vertexCount;
    public boolean hasMesh = false;

    public Chunk() {
        for(int y = 0; y<CHUNK_SIZE; y++)
            for(int x = 0; x<CHUNK_SIZE; x++)
                for(int z = 0; z<CHUNK_SIZE; z++) {
                    if(y==0) {
                        blocks[x][y][z] = createBlock(Block.GRASS);
                        blocks[x][y][z].setFlag(Block.FACE_TOP);
                        blocks[x][y][z].setFlag(Block.SOLID);
                        visibleFaces++;
                        if(x==0) {
                            blocks[x][y][z].setFlag(Block.FACE_WEST);
                            visibleFaces++;
                        }
                        if(z==0) {
                            blocks[x][y][z].setFlag(Block.FACE_NORTH);
                            visibleFaces++;
                        }
                    } else {
                        blocks[x][y][z] = createBlock(Block.AIR);
                    }
                }
        ChunkMeshGenerator.createMesh(this);
    }

    private Block createBlock(byte type) {
        switch (type) {
            case Block.DIRT:
                return new Dirt();
            case Block.GRASS:
                return new Grass();
            default:
                return new Block();
        }
    }
}
