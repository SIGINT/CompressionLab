package edu.cmu.cs211.compression.huffman;

/**
 * Represents a node in the Huffman tree. Instances of this class that are
 * internal nodes have exactly two children. Leaf nodes hold the data.
 * 
 * You are not allowed to modify this file! Yes, we know it makes sense for this
 * class to be Comparable, but you must implement that separately.
 * 
 * @param <T>
 *            The type of objects stored at the leafs of the Huffman tree.
 */
public class HuffmanNode {

	private byte value;
	private int freq;

	private HuffmanNode l, r;
	private HuffmanNode parent;

	/**
	 * Creates a leaf node without a frequency
	 */
	public HuffmanNode(byte value) {
		this.value = value;
	}
	
	//*****************
	public String toString(){
		String result = "Byte:" + this.getValue() + "  Freq:" + this.getFreq() + "";
		return result;
	}

	/**
	 * Creates a leaf node with a given frequency
	 * 
	 * @throws IllegalArgumentException
	 *             if freq is negative
	 */
	public HuffmanNode(int freq, byte value) {
		if (freq < 0)
			throw new IllegalArgumentException(
					"frequence needs to be non-negative");

		this.value = value;
		this.freq = freq;
	}

	/**
	 * Creates an internal node with the given childern.
	 * 
	 * @param l
	 *            The left child
	 * @param r
	 *            The right child
	 * @throws IllegalArgumentException
	 *             If either l or r is null.
	 */
	public HuffmanNode(HuffmanNode l, HuffmanNode r) {
		if (l == null || r == null)
			throw new IllegalArgumentException(
					"Trying to create an internal node with a null child");

		this.l = l;
		this.r = r;

		l.parent = r.parent = this;

		this.freq = l.getFreq() + r.getFreq();
	}

	/**
	 * Checks if this is a leaf node, a node which stores a value in the Huffman
	 * tree.
	 */
	public boolean isLeaf() {
		assert (getLeft() == null) == (getRight() == null) : "Huffman trees should be full";

		return getLeft() == null;
	}

	/** Checks if this is the root of the Huffman tree. */
	public boolean isRoot() {
		return getParent() == null;
	}

	/** Gets the frequency of this node. */
	public int getFreq() {
		return freq;
	}

	/**
	 * Gets the value of this node, or null if this is an internal node
	 */
	public byte getValue() {
		return value;
	}

	/**
	 * Gets the parent of this node, or null if this is the root of the Huffman
	 * tree.
	 */
	public HuffmanNode getParent() {
		return parent;
	}

	/**
	 * Gets the left child of this node, or null if this is a leaf.
	 */
	public HuffmanNode getLeft() {
		return l;
	}

	/**
	 * Gets the right child of this node, or null if this is a leaf.
	 */
	public HuffmanNode getRight() {
		return r;
	}
}
