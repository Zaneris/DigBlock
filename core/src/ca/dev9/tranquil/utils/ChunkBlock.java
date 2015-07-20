package ca.dev9.tranquil.utils;

import ca.dev9.tranquil.chunk.Chunk;
import ca.dev9.tranquil.blocks.Block;

public class ChunkBlock
{
	public Chunk chunk;
	public Block block;
	
	public ChunkBlock(){}
	
	public ChunkBlock(Chunk chunk,Block block){
		this.chunk=chunk;
		this.block=block;
	}
	
	public void copyFrom(ChunkBlock cb){
		chunk = cb.chunk;
		block = cb.block;
	}
	
	public void setFlag(boolean add, byte face) {
		if(block.setFlag(add,face))
			chunk.addToMeshQueue();
	}
}
