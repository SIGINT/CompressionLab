package edu.cmu.cs211.compression.io;

import java.io.EOFException;
import java.io.IOException;

/**
 * An interface to read data in bit, byte, or integer form.
 */
public interface BitReader {

	/**
	 * Read the next bit in the stream
	 * 
	 * @return a single bit (either 0 or 1), or -1 if the end of the stream is
	 *         reached.
	 * @exception IOException
	 *                thrown if there is an exception in the underlying stream
	 */
	public int readBit() throws IOException;

	/**
	 * Read a sequence of bits in the stream.
	 * 
	 * @param num
	 *            the number of bits to read (max of 31)
	 * @return an integer built from the bits read in, or -1 if the end of the
	 *         stream is reached
	 * @exception IOException
	 *                thrown if their is an exception in the underlying stream
	 * @exception IllegalArgumentException
	 *                thrown if the number of bits to read is out of range
	 */
	public int readBits(int num) throws IOException;

	/**
	 * Reads the next byte of data from the input stream. The value byte is
	 * returned as an <code>int</code> in the range <code>0</code> to
	 * <code>255</code>. If no byte is available because the end of the stream
	 * has been reached, the value <code>-1</code> is returned. This method
	 * blocks until input data is available, the end of the stream is detected,
	 * or an exception is thrown.
	 * <p>
	 * A subclass must provide an implementation of this method.
	 * 
	 * @return the next byte of data, or <code>-1</code> if the end of the
	 *         stream is reached.
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	public int readByte() throws IOException;

	/**
	 * Reads up to <code>len</code> bytes of data from the input stream into an
	 * array of bytes. An attempt is made to read as many as <code>len</code>
	 * bytes, but a smaller number may be read.
	 * <p>
	 * If <code>len</code> is zero, then no bytes are read and <code>0</code> is
	 * returned; otherwise, there is an attempt to read at least one byte. If no
	 * byte is available because the stream is at end of file, the value
	 * <code>-1</code> is returned; otherwise, at least one byte is read and
	 * stored into <code>b</code>.
	 * <p>
	 * The first byte read is stored into element <code>b[off]</code>, the next
	 * one into <code>b[off+1]</code>, and so on. The number of bytes read is,
	 * at most, equal to <code>len</code>. Let <i>k</i> be the number of bytes
	 * actually read; these bytes will be stored in elements <code>b[off]</code>
	 * through <code>b[off+</code><i>k</i><code>-1]</code>, leaving elements
	 * <code>b[off+</code><i>k</i><code>]</code> through
	 * <code>b[off+len-1]</code> unaffected.
	 * <p>
	 * In every case, elements <code>b[0]</code> through <code>b[off]</code> and
	 * elements <code>b[off+len]</code> through <code>b[b.length-1]</code> are
	 * unaffected.
	 * 
	 * @param b
	 *            the buffer into which the data is read.
	 * @param off
	 *            the start offset in array <code>b</code> at which the data is
	 *            written.
	 * @param len
	 *            the maximum number of bytes to read.
	 * @return the total number of bytes read into the buffer, or
	 *         <code>-1</code> if there is no more data because the end of the
	 *         stream has been reached.
	 * @exception IOException
	 *                if an I/O error occurs.
	 * @exception NullPointerException
	 *                if <code>b</code> is <code>null</code>.
	 */
	public int readBytes(byte[] b, int off, int len) throws IOException;

	/**
	 * Reads an integer in a compressed format that favors small, positive
	 * values.
	 * 
	 * @return The integer read
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws EOFException
	 *             If the end of the stream is reached
	 */
	public int readInt() throws IOException;

	/**
	 * The length of the stream
	 */
	public int length();

	/**
	 * Resets the reader to the beginning
	 */
	public void reset() throws IOException;

}
