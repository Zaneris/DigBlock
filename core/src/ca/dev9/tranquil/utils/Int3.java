package ca.dev9.tranquil.utils;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by Zaneris on 03/07/2015.
 */
public class Int3 {
	public int x, y, z;

	public Int3() {}

	public Int3(Int3 int3) {
		this(int3.x,int3.y,int3.z);
	}

	public Int3(int x, int y, int z) {
		this.x = x + 0;
		this.y = y + 0;
		this.z = z + 0;
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

	public void set(Int3 int3) {
		set(int3.x,int3.y,int3.z);
	}

	public void set(int x, int y, int z) {
		this.x = x + 0;
		this.y = y + 0;
		this.z = z + 0;
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

	public void add(int value) {
		x+=value;
		y+=value;
		z+=value;
	}

	public void copyTo(Int3 int3) {
		int3.x = x + 0;
		int3.y = y + 0;
		int3.z = z + 0;
	}

	public void copyFrom(Int3 int3) {
		x = int3.x + 0;
		y = int3.y + 0;
		z = int3.z + 0;
	}

	public Int3 clone() {
		return new Int3(x,y,z);
	}

	public int distance(Int3 int3) {
		return (int)Math.round(Math.sqrt((x-int3.x)^2+(y-int3.y)^2+(z-int3.z)^2));
	}
}
