package edu.cmu.cs211.compression.huffman;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.cmu.cs211.compression.Compressor;
import edu.cmu.cs211.compression.io.BitReader;
import edu.cmu.cs211.compression.io.BitWriter;

/**
 * A compressor that uses Huffman encoding as a mapping.
 */
public class HuffmanCompressor extends Compressor {

	// @see Compressor#compress(io.BitReader, io.BitWriter)
	@Override
	public void compress(BitReader reader, BitWriter writer) throws IOException {
		int fileBytes = reader.length();
		if (fileBytes == 0)
			return;

		HuffmanCode code = calcHuffmanCode(reader);
		code.writeHeader(writer);
		writer.writeInt(fileBytes);

		reader.reset();

		for (int i = 0; i < fileBytes; i++)
			code.encode((byte) reader.readByte(), writer);

		writer.flush();
	}

	/**
	 * Calculates a Huffman code for a given set of bits
	 */
	public static HuffmanCode calcHuffmanCode(BitReader reader)
			throws IOException {
		int[] freqArray = new int[256];

		int fileBytes = reader.length();
		for (int i = 0; i < fileBytes; i++)
			freqArray[reader.readByte() & 0xff]++;

		Map<Byte, Integer> freqMap = new HashMap<Byte, Integer>();
		for (int i = 0; i < freqArray.length; i++) {
			if (freqArray[i] != 0)
				freqMap.put((byte) i, freqArray[i]);
		}

		return new HuffmanCode(freqMap);
	}

	// @see Compressor#expand(io.BitReader, io.BitWriter)
	@Override
	public void expand(BitReader reader, BitWriter writer) throws IOException {
		if (reader.length() == 0)
			return;

		HuffmanCode code = new HuffmanCode(reader);

		int fileBytes = reader.readInt();
		for (int i = 0; i < fileBytes; i++) {
			byte value = code.decode(reader);
			writer.writeByte(value);
		}

		writer.flush();
	}

}
