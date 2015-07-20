package ca.dev9.tranquil.utils;

import com.badlogic.gdx.math.Vector3;

/**
 * For 3 dimensional integer objects.
 * @author Zaneris
 */
public class Int3 {
	public int x, y, z;
	private int loopTarget;
	private int loopStart;

	public Int3() {}

	public Int3(Int3 int3) {
		this(int3.x,int3.y,int3.z);
	}

	public Int3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null || this.getClass() != obj.getClass())
			return false;
		return (((Int3)obj).x == this.x &&
				((Int3)obj).y == this.y &&
				((Int3)obj).z == this.z);
	}

	public void set(Vector3 v3) {
		set((int)Math.floor(v3.x), (int)Math.floor(v3.y), (int)Math.floor(v3.z));
	}

	public void setPlus(Int3 value1, Int3 value2) {
		set(value1.x+value2.x, value1.y+value2.y, value1.z+value2.z);
	}

	public void set(Int3 int3) {
		set(int3.x, int3.y, int3.z);
	}

	public void set(int value) {
		this.x = value;
		this.y = value;
		this.z = value;
	}

	public void set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void mod(int value) {
		x %= value;
		y %= value;
		z %= value;
		if(x<0)
			x+=value;
		if(y<0)
			y+=value;
		if(z<0)
			z+=value;
	}

	public void div(int divisor) {
		x = x%divisor<0?x/divisor-1:x/divisor;
		y = y%divisor<0?y/divisor-1:y/divisor;
		z = z%divisor<0?z/divisor-1:z/divisor;
	}

	public void mult(int multiple) {
		x*=multiple;
		y*=multiple;
		z*=multiple;
	}

	public void add(Int3 int3) {
		x+=int3.x;
		y+=int3.y;
		z+=int3.z;
	}

	public void add(int value) {
		x+=value;
		y+=value;
		z+=value;
	}

	public void subt(int value) {
		x-=value;
		y-=value;
		z-=value;
	}

	public boolean lessThan(int value) {
		return x<value || y<value || z<value;
	}

	public boolean greaterThan(int value) {
		return x>value || y>value || z>value;
	}

	/**
	 * Simplify the code for 3D loops.
	 * @param start Starting value for x, y, and z.
	 * @param target Target value for x, y, and z.
	 */
	public void newLoop(int start, int target) {
		set(start);
		loopStart = start;
		loopTarget = target;
	}

	/**
	 * Iterate through a hollow 3D expanding cube.
	 */
	public void cubeLoop() {
		if(y<loopTarget) {
			if(Math.abs(z)==loopTarget || Math.abs(x)==loopTarget)
				y++;
			else y = loopTarget;
		} else if(x<loopTarget) {
			y = loopStart;
			if(Math.abs(z)==loopTarget || Math.abs(y)==loopTarget)
				x++;
			else x = loopTarget;
		} else {
			y = loopStart;
			x = loopStart;
			z++;
		}
	}

	/**
	 * Iterate through the 3D loop.
	 */
	public void loop() {
		if(y<loopTarget) {
			y++;
		} else if(x<loopTarget) {
			y = loopStart;
			x++;
		} else {
			y = loopStart;
			x = loopStart;
			z++;
		}
	}

	/**
	 * Check if the loop has met its target.
	 * @return Met target?
	 */
	public boolean doneLoop() {
		return z<=loopTarget;
	}

	public void reset() {
		x = 0;
		y = 0;
		z = 0;
	}

	public void copyFrom(Int3 int3) {
		x = int3.x;
		y = int3.y;
		z = int3.z;
	}

	public void copyPlus(Int3 copy, Int3 plus) {
		x = copy.x + plus.x;
		y = copy.y + plus.y;
		z = copy.z + plus.z;
	}

	public Int3 clone() {
		return new Int3(x,y,z);
	}

	public int distance(Int3 int3) {
		return (int)Math.round(Math.sqrt(((x-int3.x)*(x-int3.x))+((y-int3.y)*(y-int3.y))+((z-int3.z)*(z-int3.z))));
	}

	@Override
	public String toString() {
		return ("x:" + x + " y:" + y + " z:" + z);
	}
}
