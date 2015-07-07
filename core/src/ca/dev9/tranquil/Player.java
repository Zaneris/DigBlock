package ca.dev9.tranquil;

import com.badlogic.gdx.graphics.PerspectiveCamera;

/**
 * Created by Zaneris on 06/07/2015.
 */
public class Player {
	PerspectiveCamera cam;

	public Player(PerspectiveCamera camera) {
		cam = camera;
	}

	public void axisInput(float x, float y) {
		cam.position.add(x*GameMain.dT*2f, 0f, x*GameMain.dT*2f);
	}
}
