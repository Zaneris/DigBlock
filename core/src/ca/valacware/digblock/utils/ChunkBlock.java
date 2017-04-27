package ca.valacware.digblock.utils;

import ca.valacware.digblock.chunk.Chunk;
import ca.valacware.digblock.blocks.Block;

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
}
