package ca.valacware.digblock.utils;

/**
 * For 2 dimensional integer objects.
 * @author Zaneris
 */
public class Int2 {
	public int x;
	public int y;

	public Int2() {}
	public Int2(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(int x,int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public int hashCode() {
		return generateHash(x, y);
	}

	public static int generateHash(Int2 int2) {
		return generateHash(int2.x,int2.y);
	}
	
	public static int generateHash(int x, int y) {
		return x*1009+y;
	}
}
