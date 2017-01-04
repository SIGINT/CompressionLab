package edu.cmu.cs211.compression.bw;

import java.io.IOException;

import edu.cmu.cs211.compression.Compressor;
import edu.cmu.cs211.compression.io.BitReader;
import edu.cmu.cs211.compression.io.BitWriter;

import edu.cmu.cs211.compression.huffman.*;

/**
 * A compressor implementing Burrows-Wheeler compression.
 */
public class BurrowsWheelerCompressor extends Compressor {

	@Override
	public void compress(BitReader reader, BitWriter writer) throws IOException {
		// apply the Burrows-Wheeler transformation
		BurrowsWheelerTransformer bwt = new BurrowsWheelerTransformer();
		byte[] bwtTransformedResult = bwt.transform(reader);

		// apply the Move-to-Front transformation
		MoveToFrontTransformer mtf = new MoveToFrontTransformer();
		byte[] mtfTransformedResult = mtf.transform(bwtTransformedResult);

		// apply Huffman compression
		HuffmanCompressor hc = new HuffmanCompressor();
		hc.compress(mtfTransformedResult, writer);
	}

	@Override
	public void expand(BitReader reader, BitWriter writer) throws IOException {
		HuffmanCompressor hc = new HuffmanCompressor();
		byte[] expandedFromHC = hc.expand(reader);

		MoveToFrontTransformer mtf = new MoveToFrontTransformer();
		byte[] invertedFromMTF = mtf.invertTransform(expandedFromHC);

		BurrowsWheelerTransformer bwt = new BurrowsWheelerTransformer();
		bwt.invertTransform(invertedFromMTF, writer);
	}

}
