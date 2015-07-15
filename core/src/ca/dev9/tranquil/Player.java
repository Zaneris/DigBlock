package ca.dev9.tranquil;

import com.badlogic.gdx.graphics.PerspectiveCamera;

/**
 * Main player of the game.
 * @author Zaneris
 */
public class Player {
	PerspectiveCamera cam;

	public Player() {
		cam = new PerspectiveCamera();
	}

	public void axisInput(float x, float y) {
		cam.position.add(x*GameMain.dT*2f, 0f, x*GameMain.dT*2f);
	}
}
