package ca.dev9.tranquil;

import ca.dev9.tranquil.blocks.Block;
import ca.dev9.tranquil.chunk.Chunk;
import ca.dev9.tranquil.screens.World;
import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Main player of the game.
 * @author Zaneris
 */
public class Player {
	public PerspectiveCamera cam;
	private Vector3 lastPosition,tmp,out;
	private final Int3 newBlk,curBlk;
	private Vector2 move,rot;
	private byte rotCount;
	private long jumpCount;
	public final Int3 currentChunk;
	public Chunk chunk;
	public boolean jump,falling,jumpTouch,jumpReady;

	public Player() {
		rotCount=0;
		jumpCount=0;
		tmp = new Vector3();
		out = new Vector3();
		rot = new Vector2();
		move = new Vector2();
		newBlk = new Int3();
		curBlk = new Int3();
		jump = false;
		jumpReady = false;
		falling = false;
		currentChunk = new Int3();
		cam = new PerspectiveCamera(75f, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		cam.position.set(0f, 35f, 0f);
		cam.direction.set(0f, 0f, 1f);
		cam.near = 0.1f;
	}

	public void update() {
		rotateCam();
		newBlk.set(cam.position);
		newBlk.div(Chunk.CHUNK_SIZE);
		if(!newBlk.equals(currentChunk)) {
			currentChunk.set(newBlk);
			chunk = World.world.chunkMap.get(currentChunk);
		}
		cam.update();
	}

	public void jumpCount() {
		if(jumpReady) {
			if(TimeUtils.timeSinceMillis(jumpCount)<600) {
				jump = true;
			} else {
				jumpCount = TimeUtils.millis();
			}
			jumpReady = false;
		}
		jumpTouch = true;
	}

	private void checkTouchJump() {
		if(rotCount==0) {
			if (!jumpTouch)
				jumpReady = true;
			else
				jumpReady = false;
			jumpTouch = false;
		}
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
		checkTouchJump();
		if(dT>0.16f)
			dT = 0.16f;
		if(!falling) {
			out.x *= 0.5f;
			out.z *= 0.5f;
		}
		falling = true;
		out.y -= Config.GRAVITY*dT;
		if(out.y<Config.TERM_VELOCITY)
			out.y = Config.TERM_VELOCITY;
		boolean onGround = blockCollision(dT, -1.5f) && out.y<0f;
		if(move.len()>1.0)
			move.nor();
		if(onGround) {
			cam.position.y = (float)Math.floor(cam.position.y) + 0.5f;
			out.set(cam.direction.x, 0f, cam.direction.z).nor().scl(move.y * 3.0f);
			tmp.set(cam.direction).crs(cam.up).nor().scl(move.x * 3.0f);
			out.add(tmp);
			if(jump) {
				out.scl(0.5f);
				out.add(0f,5f,0f);
			} else falling = false;
		} else {
			tmp.set(cam.direction.x, 0f, cam.direction.z).nor().scl(move.y/100f);
			out.add(tmp);
			tmp.set(cam.direction).crs(cam.up).nor().scl(move.x/100f);
			out.add(tmp);
		}
		setInt3(curBlk, -1.2f);
		if(blockCollision(dT, -1.2f)) {
			if(curBlk.x!=newBlk.x && curBlk.z!=newBlk.z) {
				float x = out.x;
				out.x = 0f;
				if(blockCollision(dT, -1.2f)) {
					out.x = x;
					out.z = 0f;
					if(blockCollision(dT, -1.2f)) {
						out.x = 0f;
					}
				}
			} else if(curBlk.x!=newBlk.x) {
				out.x = 0f;
			} else if(curBlk.z!=newBlk.z)
				out.z = 0f;
			tmp.set(out);
			tmp.scl(dT);
		}
		cam.position.add(tmp);
		move.setZero();
		jump = false;
	}

	private boolean blockCollision(float dT, float yOff) {
		tmp.set(out);
		tmp.scl(dT);
		setInt3Plus(newBlk, tmp, yOff);
		Block block = getBlock(newBlk);
		return block!=null && block.blockType!=Block.AIR;
	}

	private void setInt3Plus(Int3 int3, Vector3 plus, float yOff) {
		int3.set(cam.position.x+plus.x,
				cam.position.y+plus.y+yOff,
				cam.position.z+plus.z);
	}

	private void setInt3(Int3 int3, float yOff) {
		int3.set(cam.position.x,
				cam.position.y+yOff,
				cam.position.z);
	}
	
	public boolean setRot(int deltaX,int deltaY) {
		if(rotCount!=0)
			return false;
		rotCount = 10;
		if(Config.MOBILE) rot.set(deltaX*.05f,deltaY*.05f);
		else rot.set(deltaX*.02f,deltaY*.02f);
		return true;
	}
	
	private Block getBlock(Int3 int3) {
		if(chunk==null)
			return null;
		else return chunk.getWorldBlock(int3);
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
