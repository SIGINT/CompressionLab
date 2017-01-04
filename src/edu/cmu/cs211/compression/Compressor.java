package edu.cmu.cs211.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.cmu.cs211.compression.io.BitReader;
import edu.cmu.cs211.compression.io.BitWriter;
import edu.cmu.cs211.compression.io.InputStreamBitReader;
import edu.cmu.cs211.compression.io.OutputStreamBitWriter;

/**
 * An algorithm that injectively maps a stream of bits on to another stream of
 * bits. If the stream of bits passed consists of redundant data, then the
 * mapped version, known as the <q>compressed</q> version, will take up less
 * space than the original.
 */
public abstract class Compressor {

	/**
	 * The compress() method will read input from the given BitReader, perform a
	 * compression algorithm, and write back to the given BitWriter.
	 * 
	 * @param reader
	 *            a BitReader implementation
	 * @param writer
	 *            a BitWriter implementation
	 * @throws IOException
	 *             if there is a problem with the underlying BitReader or
	 *             BitWriter
	 * @throws IllegalArgumentException
	 *             If the input file cannot be handled (for example, the file is
	 *             too long and the compressor doesn't have support for long
	 *             files).
	 * @throws NullPointerException
	 *             if reader or writer is null
	 */
	public abstract void compress(BitReader reader, BitWriter writer)
			throws IOException;

	/**
	 * The expand() method will read input from the given BitReader, perform a
	 * decompression algorithm, and write back to the given BitWriter.
	 * 
	 * @param reader
	 *            a BitReader implementation
	 * @param writer
	 *            a BitWriter implementation
	 * @throws IOException
	 *             if there is a problem related to the underlying BitReader or
	 *             BitWriter. Also thrown if the file in reader is somehow
	 *             invalid (for example, if it has been corrupted and is no
	 *             longer a valid compressed file). If this is the case, then
	 *             the exception might be thrown after data has been written to
	 *             writer.
	 * @throws NullPointerException
	 *             if reader or writer is null
	 */
	public abstract void expand(BitReader reader, BitWriter writer)
			throws IOException;

	/**
	 * Helper version of compress (converts Input/OutputStream to
	 * BitReader/Writer)
	 */
	public void compress(InputStream in, OutputStream out) throws IOException {
		compress(new InputStreamBitReader(in), new OutputStreamBitWriter(out));
	}

	/**
	 * Helper version of expand (converts Input/OutputStream to
	 * BitReader/Writer)
	 */
	public void expand(InputStream in, OutputStream out) throws IOException {
		expand(new InputStreamBitReader(in), new OutputStreamBitWriter(out));
	}

	/**
	 * Helper version of compress (converts files whose paths are expressed by
	 * fileIn and fileOut into InputStream and OutputStream)
	 */
	public void compress(String fileIn, String fileOut) throws IOException {
		compress(new InputStreamBitReader(fileIn), new OutputStreamBitWriter(
				fileOut));
	}

	/**
	 * Helper version of expand (converts files whose paths are expressed by
	 * fileIn and fileOut into InputStream and OutputStream)
	 */
	public void expand(String fileIn, String fileOut) throws IOException {
		expand(new InputStreamBitReader(fileIn), new OutputStreamBitWriter(
				fileOut));
	}

	/** Helper version of compress (for raw byte arrays) */
	public byte[] compress(byte[] in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		compress(new ByteArrayInputStream(in), out);
		return out.toByteArray();
	}

	/** Helper version of expand (for raw byte arrays) */
	public byte[] expand(byte[] in) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		expand(new ByteArrayInputStream(in), outputStream);
		return outputStream.toByteArray();
	}

	/** Helper version of compress */
	public void compress(byte[] in, BitWriter writer) throws IOException {
		compress(new InputStreamBitReader(new ByteArrayInputStream(in)), writer);
	}

	/** Helper version of expand */
	public void expand(byte[] in, BitWriter writer) throws IOException {
		expand(new InputStreamBitReader(new ByteArrayInputStream(in)), writer);
	}

	/** Helper version of expand */
	public byte[] expand(BitReader reader) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		expand(reader, new OutputStreamBitWriter(outputStream));
		return outputStream.toByteArray();
	}
}
