package network;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ByteConversions {

	/*--------------*/
	/* DATA -> BYTE */
	/*--------------*/
	
	public static final byte[] shortToByteArray(short value) {
		return new byte[] { (byte) (value >>> 8), (byte) value };
	}

	public static final byte[] intToByteArray(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
				(byte) (value >>> 8), (byte) value };
	}
	
	public static final byte[] floatToByteArray(float f) {
		return intToByteArray(Float.floatToIntBits(f));
	}
	
	public static final byte[] String32toByteArray(String s) {
		try {
			byte[] b = s.getBytes( "UTF-8" /* encoding */ );
			return Arrays.copyOf(b, 64);
		} catch (UnsupportedEncodingException e) {
			return new byte[64];
		}
	}
	
	public static final byte BooleanToByte(boolean b) {
		if(b) return Byte.MAX_VALUE;
		return Byte.MIN_VALUE;
	}
	
	/*--------------*/
	/* BYTE -> DATA */
	/*--------------*/
	
	public static final short byteArrayToShort(byte[] b, int start) {
		return (short) ((b[0 + start] << 8) + (b[1 + start] & 0xFF));
	}

	public static final int byteArrayToInt(byte[] b, int start) {
		return (b[0 + start] << 24) + ((b[1 + start] & 0xFF) << 16)
				+ ((b[2 + start] & 0xFF) << 8) + (b[3 + start] & 0xFF);
	}

	public static final String byteToString32(byte[] b, int start) {
		try {
			if(b.length-start>64) {
				byte[] name = new byte[64];
				for (int i = 0; i < 64; i++) {
					name[i] = b[i + start];
				}
				return new String(name,0,64,"UTF-8"/* encoding */).trim();
			} else {
				byte[] name = new byte[b.length-start];
				for (int i = 0; i < b.length-start; i++) {
					name[i] = b[i + start];
				}
				return new String( name,0,64,"UTF-8"/* encoding */).trim();
			}
		} catch(UnsupportedEncodingException e) {
			return "(unsupported encoding)";
		}
	}

	
	public static final float byteArrayToFloat(byte[] b, int start) {
		return Float.intBitsToFloat(byteArrayToInt(b, start));
	}
	
	public static final boolean byteToBoolean(Byte b) {
		return (b.equals(Byte.MAX_VALUE));
	}
}
