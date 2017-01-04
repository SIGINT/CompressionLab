package edu.cmu.cs211.compression.io;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This implementation of BitWriter operates on a file. An instance is created
 * from a named file which can be either an absolute path or a file relative to
 * directory from which the command line is invoked.
 */
public class OutputStreamBitWriter implements BitWriter {
	private OutputStream stream;

	private int bitCount = 0;
	private int currentByte = 0;

	/** Creates a OutputStreamBitWriter from an OutputStream */
	public OutputStreamBitWriter(OutputStream s) {
		stream = s;
	}

	/**
	 * Create a OutputStreamBitWriter
	 * 
	 * @param filename
	 *            an absolute or relative filename
	 * @exception FileNotFoundException
	 *                thrown if the file is not writable
	 */
	public OutputStreamBitWriter(String filename) throws FileNotFoundException {
		this(new BufferedOutputStream(new FileOutputStream(filename)));
	}

	public void writeBit(int bit) throws IOException {
		if (bit != 0 && bit != 1)
			throw new IllegalArgumentException("A bit must be 0 or 1");

		currentByte = currentByte << 1 | bit;
		bitCount++;
		if (bitCount == 8) {
			stream.write(currentByte);
			currentByte = 0;
			bitCount = 0;
		}

	}

	public void writeBits(int bits, int num) throws IOException {
		if ((num < 0) || (num > 32))
			throw new IllegalArgumentException("Number of bits is out of range");

		while (num > 0) {
			// write out byte by byte
			int cbit = Math.min(num, (8 - bitCount));

			currentByte = (currentByte << cbit)
					| ((bits >>> (num - cbit)) & ((1 << cbit) - 1));

			bitCount += cbit;
			num -= cbit;

			// flush to output
			if (bitCount == 8) {
				stream.write(currentByte);
				currentByte = 0;
				bitCount = 0;
			}
		}
	}

	public void writeByte(byte nextByte) throws IOException {
		// fast path
		if (bitCount == 0)
			stream.write(nextByte);
		else
			writeBits(nextByte, 8);
	}

	public void writeBytes(byte[] bytes) throws IOException {
		if (bitCount == 0)
			stream.write(bytes);
		else {
			for (byte b : bytes)
				writeByte(b);
		}
	}

	public void writeInt(int value) throws IOException {
		// Code was taken from Mono's BinaryWriter. For more about this format:
		// http://msdn2.microsoft.com/en-US/library/system.io.binarywriter.write7bitencodedint.aspx
		do {
			int high = (value >>> 7) & 0x01ffffff;
			byte b = (byte) (value & 0x7f);

			if (high != 0) {
				b = (byte) (b | 0x80);
			}

			writeByte(b);
			value = high;
		} while (value != 0);
	}

	public void flush() throws IOException {
		while (bitCount > 0)
			writeBit(0);
		stream.flush();
	}

}
