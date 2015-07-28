package ca.dev9.tranquil;

import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.chunk.Chunk;
import ca.dev9.tranquil.screens.World;
import ca.dev9.tranquil.utils.ChunkBlock;
import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.utils.TimeUtils;

import java.security.*;

/**
 * Main player of the game.
 * @author Zaneris
 */
public class Player {
	public PerspectiveCamera cam;
	private Vector3 lastPosition,tmp,out;
	private final Int3 int31,int32;
	private Vector2 move,rot;
	private byte rotCount;
	private long jumpCount;
	public boolean jump,falling,jumpTouch,jumpReady;
	public final Int3 currentChunk;

	public Player() {
		currentChunk = new Int3();
		rotCount = 0;
		jumpCount=0;
		tmp = new Vector3();
		out = new Vector3();
		rot = new Vector2();
		move = new Vector2();
		int31 = new Int3();
		int32 = new Int3();
		jump = false;
		jumpReady = false;
		falling = false;
		cam = new PerspectiveCamera(75f, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		cam.position.set(0f, 50f, 0f);
		cam.direction.set(0f, 0f, 1f);
		cam.near = 0.1f;
	}

	public void update() {
		rotateCam();
		currentChunk.set(cam.position);
		currentChunk.div(Chunk.CHUNK_SIZE);
		cam.update();
	}

	public void jumpCount() {
		if(jumpReady) {
			if((TimeUtils.millis()-jumpCount)<500)
				jump = true;
			else
				jumpCount = TimeUtils.millis();
		}
		jumpTouch = true;
	}

	public boolean moved32() {
		return (lastPosition == null || cam.position.dst2(lastPosition)>1024);
	}

	public void updateLastPosition() {
		if(lastPosition==null)
			lastPosition = new Vector3();
		lastPosition.set(cam.position);
	}

	public void axisInput(float x, float y) {
		move.set(x,y);
	}

	public void axisX(float x) {
		move.x = x;
	}

	public void axisY(float y) {
		move.y = y;
	}
	
	public void move(float dT) {
		if(!jumpTouch)
			jumpReady = true;
		else
			jumpReady = false;
		jumpTouch = false;
		if(dT>0.16f)
			dT = 0.16f;
		if(!falling) {
			out.x *= 0.8f;
			out.z *= 0.8f;
		}
		falling = true;
		out.y -= Config.GRAVITY*dT;
		if(out.y<-Config.GRAVITY*2f)
			out.y = -Config.GRAVITY*2f;
		tmp.set(out);
		tmp.scl(dT);
		int31.set((int) Math.floor(tmp.x + cam.position.x),
				(int) Math.floor(tmp.y + cam.position.y - 1.5f),
				(int) Math.floor(tmp.z + cam.position.z));
		int32.set((int)Math.floor(cam.position.x),
				(int)Math.floor(cam.position.y-1.5f),
				(int)Math.floor(cam.position.z));
		ChunkBlock cB = World.world.getChunkBlock(int31);
		if(cB!=null && cB.block.blockType!=Block.AIR && out.y<0f) {
			cam.position.y = (float)Math.floor(cam.position.y) + (cB.block.blockType==Block.WATER?0.3f:0.5f);
			if(move.len()>1.0)
				move.nor();
			out.set(cam.direction.x, 0f, cam.direction.z).nor().scl(move.y * 3.0f);
			tmp.set(cam.direction).crs(cam.up).nor().scl(move.x * 3.0f);
			out.add(tmp);
			if(jump) {
				out.scl(0.8f);
				out.add(0f,5f,0f);
			} else falling = false;
			tmp.set(out);
			tmp.scl(dT);
			int31.set((int) Math.floor(tmp.x + cam.position.x),
					(int) Math.floor(tmp.y + cam.position.y - 1.2f),
					(int) Math.floor(tmp.z + cam.position.z));
			int32.set((int) Math.floor(cam.position.x),
					(int) Math.floor(cam.position.y - 1.2f),
					(int) Math.floor(cam.position.z));
			cB = World.world.getChunkBlock(int31);
		}
		if(cB!=null && cB.block.blockType!=Block.AIR) {
			if(int32.x!=int31.x)
				out.x = 0f;
			if(int32.y!=int31.y)
				out.y = 0f;
			if(int32.z!=int31.z)
				out.z = 0f;
			tmp.set(out);
			tmp.scl(dT);
		}
		cam.position.add(tmp);
		move.setZero();
		jump = false;
	}
	
	public boolean setRot(int deltaX,int deltaY) {
		if(rotCount!=0)
			return false;
		rotCount = 10;
		if(Config.MOBILE) rot.set(deltaX*.05f,deltaY*.05f);
		else rot.set(deltaX*.02f,deltaY*.02f);
		return true;
	}
	
	private void rotateCam() {
		if(rotCount>0) {
			cam.direction.rotate(cam.up, rot.x);
			float temp = rot.y/360f+cam.direction.y;
			if(temp>-1f && temp<1f) {
				tmp.set(cam.direction).crs(cam.up).nor();
				cam.direction.rotate(tmp, rot.y);
			} else if (cam.direction.y < 0f) {
				cam.direction.y = -1f;
			} else {
				cam.direction.y =  1f;
			}
			rotCount--;
		}
	}
}
