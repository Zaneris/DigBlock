package ca.dev9.tranquil.utils;

import ca.dev9.tranquil.chunk.Chunk;
import ca.dev9.tranquil.blocks.Block;

public class ChunkBlock
{
	public Chunk chunk;
	public Block block;
	
	public void setFlag(boolean add, byte face) {
		if(block.setFlag(add,face))
			chunk.addToMeshQueue();
	}
}
