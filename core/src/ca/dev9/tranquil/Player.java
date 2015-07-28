package ca.dev9.tranquil;

import ca.dev9.tranquil.chunk.Chunk;
import ca.dev9.tranquil.utils.Int3;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import java.security.*;

/**
 * Main player of the game.
 * @author Zaneris
 */
public class Player {
	public PerspectiveCamera cam;
	private Vector3 lastPosition,tmp,out;
	private Vector2 move,rot;
	private byte rotCount = 0;
	public final Int3 currentChunk;

	public Player() {
		currentChunk = new Int3();
		tmp = new Vector3();
		out = new Vector3();
		rot = new Vector2();
		move = new Vector2();
		cam = new PerspectiveCamera(75f, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		cam.position.set(0f, 33f, 0f);
		cam.lookAt(100f, 33f, 0f);
		cam.near = 1.0f;
	}

	public void update() {
		rotateCam();
		currentChunk.set(cam.position);
		currentChunk.div(Chunk.CHUNK_SIZE);
		cam.update();
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
		if(move.len()>1.0)
			move.nor();
		out.set(cam.direction).nor().scl(move.y*3.0f*dT);
		tmp.set(cam.direction).crs(cam.up).nor().scl(move.x*3.0f*dT);
		out.add(tmp);
		cam.position.add(out);
		move.setZero();
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
