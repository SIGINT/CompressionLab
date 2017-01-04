package edu.cmu.cs211.compression.io;

import java.io.IOException;

/**
 * This implementation of BitWriter prints to System.out
 */
public class DebugWriter implements BitWriter {

	private int numWritten = 0;

	public void writeBit(int nextBit) {
		if (nextBit != 1 && nextBit != 0)
			throw new IllegalArgumentException("A bit must be 0 or 1");

		System.out.print(nextBit + " ");
		if (++numWritten % 16 == 0)
			System.out.println();
	}

	public void writeBits(int nextBits, int num) {
		if (num < 0 || num > 32)
			throw new IllegalArgumentException("Number of bits is out of range");

		for (int i = num - 1; i >= 0; i--)
			writeBit((nextBits & (1 << i)) >>> i);
	}

	public void writeByte(byte nextByte) {
		System.out.print("" + convertByteToHex(nextByte) + " ");

		if (++numWritten % 16 == 0)
			System.out.println();
	}

	public void writeBytes(byte[] nextBytes) {
		for (byte element : nextBytes)
			writeByte(element);
	}

	public void writeInt(int v) throws IOException {
		System.out.println();
		System.out.println("Int: " + v + " = " + Integer.toHexString(v));
	}

	public void flush() {
		System.out.println();
	}

	private String convertByteToHex(byte b) {
		return Integer.toHexString((b) & 0xff);
	}

}
