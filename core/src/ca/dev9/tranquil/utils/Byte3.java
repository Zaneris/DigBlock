package ca.dev9.tranquil.utils;

/**
 * Created by Zaneris on 06/07/2015.
 */
public class Byte3 {
	public byte x;
	public byte y;
	public byte z;

	public Byte3(int x, int y, int z) {
		this((byte)x,(byte)y,(byte)z);
	}

	public Byte3(byte x, byte y, byte z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void copyFrom (Byte3 byte3) {
		x = byte3.x;
		y = byte3.y;
		z = byte3.z;
	}
}
