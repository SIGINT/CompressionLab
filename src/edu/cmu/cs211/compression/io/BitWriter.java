package edu.cmu.cs211.compression.io;

import java.io.IOException;

/**
 * This interface describes a class that can supply data to a stream in three
 * formats. It is expected that only one of the formats is used at a time. If
 * the calls to get data are mixed between formats, unexpected results may
 * occur.
 */
public interface BitWriter {

	/**
	 * Write a bit to the stream
	 * 
	 * @param nextBit
	 * @exception IOException
	 *                thrown if there is an exception in the underlying stream
	 * @exception IllegalArgumentException
	 *                thrown if the value is not actually a bit
	 */
	public void writeBit(int nextBit) throws IOException;

	/**
	 * Write a sequence of bits in the stream. Bit 0 of the int will be the last
	 * bit written.
	 * 
	 * @param nextBits
	 *            an int containing the bits to write
	 * @param num
	 *            the number of bits to write
	 * @exception IOException
	 *                thrown if their is an exception in the underlying stream
	 * @exception IllegalArgumentException
	 *                thrown if the number of bits is out of range
	 */
	public void writeBits(int nextBits, int num) throws IOException;

	/**
	 * Write a byte to the stream
	 * 
	 * @param nextByte
	 *            a byte
	 * @exception IOException
	 *                thrown if their is an exception in the underlying stream
	 */
	public void writeByte(byte nextByte) throws IOException;

	/**
	 * Write a sequence of bytes to the stream
	 * 
	 * @param nextBytes
	 *            an array of bytes
	 * @exception IOException
	 *                thrown if their is an exception in the underlying stream
	 */
	public void writeBytes(byte[] nextBytes) throws IOException;

	public void writeInt(int v) throws IOException;

	/**
	 * Flush the stream. This makes sure all bytes and bits have been passed off
	 * to the underlying stream. In the case of bits, this means an incomplete
	 * byte will be flushed as though the remaining bits were 0's.
	 * 
	 * @exception IOException
	 *                thrown if their is an exception in the underlying stream
	 */
	public void flush() throws IOException;

}
