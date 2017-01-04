package edu.cmu.cs211.compression;

import java.io.IOException;

import edu.cmu.cs211.compression.io.BitReader;
import edu.cmu.cs211.compression.io.BitWriter;

/* A class that implements the Transformer interface can transform a byte array
 * into another byte array.
 */
public abstract class Transformer {

	/**
	 * The transform() method will read an input byte array and apply the
	 * transformation on this sequence.
	 * 
	 * @param input
	 *            a byte array
	 * @return a byte array after transforming the input array
	 */
	public abstract byte[] transform(byte[] input);

	/**
	 * The invertTransform() method will read an input byte array and apply the
	 * inverted transformation.
	 * 
	 * @param input
	 *            a byte array which is the output of transform()
	 * @return a byte array after the inverted transformation
	 * @throws IllegalArgumentException
	 *             If the input byte array cannot be inverted (i.e.-missing a
	 *             header)
	 */
	public abstract byte[] invertTransform(byte[] input);

	/**
	 * Helper method for transform()
	 * 
	 * @param reader
	 *            a BitReader to be transformed
	 * @return transformed byte array
	 * @throws IOException
	 */
	public final byte[] transform(BitReader reader) throws IOException {
		int inputLength = reader.length();
		byte[] input = new byte[inputLength];
		reader.readBytes(input, 0, inputLength);
		return transform(input);
	}

	/**
	 * Helper method for invertTransform()
	 * 
	 * @param input
	 *            array
	 * @param BitWriter
	 * @throws IOException
	 */
	public final void invertTransform(byte[] input, BitWriter writer)
			throws IOException {
		byte[] output = invertTransform(input);
		writer.writeBytes(output);
		writer.flush();
	}
}
