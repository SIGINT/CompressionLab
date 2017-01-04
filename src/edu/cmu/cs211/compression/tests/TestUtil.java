package edu.cmu.cs211.compression.tests;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import edu.cmu.cs211.compression.Compressor;

/**
 * A utility class to help you write tests
 */
public class TestUtil {

	/**
	 * Checks that compressing and subsequently decompressing a sequence of
	 * bytes returns the original sequence of bytes.
	 */
	public static void checkRoundTrip(Compressor comp, byte[] bytes)
			throws Exception {
		byte[] compressed = comp.compress(bytes);
		byte[] decompressed = comp.expand(compressed);
		assertTrue("Round trip failed", Arrays.equals(bytes, decompressed));
	}

}
