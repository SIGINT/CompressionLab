package edu.cmu.cs211.compression.io;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * This implementation of BitReader operates on a InputStream.
 */
public class InputStreamBitReader implements BitReader {
	private InputStream stream;

	// Number of bits left in the byte already read
	private int bitsLeft = 0;
	private int currentByte = 0;

	LengthResetHelper streamSeeker;

	// HACKS AHEAD! Java provides almost 0 support
	// for doing essential operations with streams.
	// We must hack around this.

	private interface LengthResetHelper {
		public long length();

		public void reset() throws IOException;
	}

	public InputStreamBitReader(final InputStream is) throws IOException {
		if (!is.markSupported())
			throw new RuntimeException("InputStream must support marking");

		stream = is;
		is.mark(0);
		// skip() will skip ahead, but also return how many
		// bytes it was able to skip.
		final long length = is.skip(Long.MAX_VALUE);
		// now just reset it back
		is.reset();

		streamSeeker = new LengthResetHelper() {
			public long length() {
				return length;
			}

			public void reset() throws IOException {
				is.reset();
			}
		};
	}

	/**
	 * Create a InputStreamBitReader
	 * 
	 * @param filename
	 *            an absolute or relative filename
	 * @exception FileNotFoundException
	 *                thrown if the file is not found or is not readable
	 */
	public InputStreamBitReader(String filename) throws FileNotFoundException {
		this(new File(filename));
	}

	public InputStreamBitReader(File file) throws FileNotFoundException {
		this(new FileInputStream(file));
	}

	public InputStreamBitReader(final FileInputStream file)
			throws FileNotFoundException {
		stream = new BufferedInputStream(file);
		// FileInputStreams have a different set of limitations. Oh boy!
		streamSeeker = new LengthResetHelper() {
			public long length() {
				try {
					return file.getChannel().size();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			public void reset() {
				try {
					file.getChannel().position(0);
					stream = new BufferedInputStream(file);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	public int readBit() throws EOFException, IOException {
		if (bitsLeft == 0) {
			currentByte = stream.read();
			if (currentByte == -1)
				return -1;
			bitsLeft = 8;
		}
		bitsLeft--;
		return (currentByte >>> bitsLeft) & 1;
	}

	public int readBits(int num) throws IOException {
		if ((num < 0) || (num > 31)) {
			throw new IllegalArgumentException("Number of bits is out of range");
		}

		int bits = 0;
		while (num > 0) {
			if (bitsLeft == 0) {
				currentByte = stream.read();
				if (currentByte == -1)
					return -1;
				bitsLeft = 8;
			}

			int cbit = Math.min(num, bitsLeft);
			bits = (bits << cbit)
					| ((currentByte >>> (bitsLeft - cbit) & ((1 << cbit) - 1)));
			num -= cbit;
			bitsLeft -= cbit;

		}

		return bits;
	}

	public int readByte() throws IOException {
		if (bitsLeft == 0)
			return stream.read();

		return readBits(8);
	}

	public int readBytes(byte[] buf, int start, int count) throws IOException {
		if (bitsLeft == 0)
			return stream.read(buf, start, count);

		for (int i = 0; i < count; i++) {
			int b = readByte();
			if (b == -1)
				return i == 0 ? -1 : i;

			buf[start + i] = (byte) b;
		}
		return count;
	}

	public int readInt() throws IOException {
		int ret = 0;
		int shift = 0;
		int b;

		do {
			b = readByte();
			if (b == -1)
				throw new EOFException();

			ret |= ((b & 0x7f) << shift);
			shift += 7;
		} while ((b & 0x80) == 0x80);

		return ret;
	}

	public int length() {
		long l = streamSeeker.length();
		if (l > Integer.MAX_VALUE)
			throw new RuntimeException(
					"No support for 64 bit file IO. 640K ought to be enough for anybody.");
		return (int) l;
	}

	public void reset() throws IOException {
		streamSeeker.reset();
	}
}
