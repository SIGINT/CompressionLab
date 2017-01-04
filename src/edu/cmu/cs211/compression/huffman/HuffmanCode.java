package edu.cmu.cs211.compression.huffman;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.Stack;
import java.util.Iterator;
import edu.cmu.cs211.compression.util.MyPriorityQueue;

import edu.cmu.cs211.compression.io.BitReader;
import edu.cmu.cs211.compression.io.BitWriter;

/**
 * Represents the Huffman code. The class supports building the Huffman tree
 * based either on the frequency of given symbols or by reading the tree from an
 * on-disk format. It also supports emitting the code word for a given symbol or
 * reading a symbol based on the code word. Lastly, it is able to write an
 * on-disk representation of the Huffman tree. For testing purposes, we can also
 * create a Huffman code with a given HuffmanNode as the root.
 */
public class HuffmanCode {

	/** Code bit for a leaf node in file-based tree representation */
	private static final int LEAF = 0;
	/** Code bit for a parent node in file-based tree representation */
	private static final int PARENT = 1;

	/** Code bit for the left child in the file-based tree representation */
	private static final int LEFT = 0;
	/** Code bit for the right child in the file-based tree representation */
	private static final int RIGHT = 1;
        /* Maps bytes to huffman nodes to easily move up the tree to extract the codeword */
	private HashMap<Byte, HuffmanNode> nodeMap;
	/* Root node of the underlying huffman tree structure */
        private HuffmanNode tree;

	/**
	 * Creates a HuffmanCode by walking through a previously constructed Huffman
	 * tree. This builds an internal map from each byte value to each
	 * HuffmanNode so we can later encode data items using the Huffman
	 * algorithm.
	 * 
	 * @param root
	 *            the root node of the Huffman tree
	 * 
	 * @throws NullPointerException
	 *             if root is null
	 */
	public HuffmanCode(HuffmanNode root) {
		if(root == null){
			throw new NullPointerException();
		}
		this.tree = root;
		this.nodeMap = new HashMap<Byte, HuffmanNode>();
		HuffmanNodeComparator comparator = new HuffmanNodeComparator();
		
		//Invoke private recursive method to traverse huffmantree, mapping the leaf nodes.
		mapNodes(root, comparator);
	}
	


	/**
	 * <p>
	 * Reads the Huffman header in from a BitReader and deserializes the data at
	 * the leafs of the tree with br.readByte(). The data format for this header
	 * is defined by <tt>writeHeader</tt>
	 * </p>
	 * 
	 * <p>
	 * Note that this is not used to read in a file you want to compress, but is
	 * used to read-in the Huffman codes from the header of an already
	 * compressed file.
	 * </p>
	 * 
	 * @param reader
	 *            BitReader to read in the header from
	 * @throws IOException
	 *             If there is a problem reading from the bit reader, if the
	 *             file ends before the full header can be read, or if the
	 *             header is not valid.
	 */
	public HuffmanCode(BitReader reader) throws IOException {
		
                HuffmanNode root;
		int firstBit = reader.readBit();
		this.nodeMap = new HashMap<Byte, HuffmanNode>();
                
		firstBit = reader.readBit();
		//firstBit tells us we have one LEAF node.
		if(firstBit == LEAF){				
			Integer intData = reader.readByte();
			Byte byteData = intData.byteValue();
			root = new HuffmanNode(byteData);
			this.tree = root;
			this.nodeMap.put(byteData, root);
			return;
		}
		
		//Recursively call helper method which returns a new node consisting of the left and right subtrees.
		this.tree = new HuffmanNode((helper(reader)), (helper(reader)));
		
		HuffmanNodeComparator comparator = new HuffmanNodeComparator();
		//Call helper method to build nodeMap and codewordMap.
		mapNodes(this.tree, comparator);
	}
        
        	/*
         * Recursive private method that traverses the tree and finds each leaf node.  At each leaf node, a mapping
	 * from byte to HuffmanNode is stored.
         */
	private void mapNodes(HuffmanNode hn, HuffmanNodeComparator comparator){
		
		//Check if hn is a leaf node; we map hn's byte to hn and to a CodeWord.
		if(hn.isLeaf()){
				this.nodeMap.put(hn.getValue(), hn);
			}	
		//Map left and right nodes recursively. 
		else{
			mapNodes(hn.getLeft(), comparator);
			mapNodes(hn.getRight(), comparator);
		}
	}
	
	/*Recursive helper method for HuffmanCode( BitReader ) constructor.  Headers are written recursively in 
	 * preorder ( write(root) -> write(left) -> write(right) ).  It made sense to define the function that 
	 * needs to read this header recursively.  After a PARENT bit is read, the next two nodes will be its LEFT
	 * and RIGHT children.*/
	private HuffmanNode helper(BitReader reader) throws IOException{
			int nextBit = reader.readBit();
			
			//BASE CASE: nextBit tells us we have a LEAF node.
			if(nextBit == LEAF){
				Integer intData = reader.readByte();
				Byte byteData = intData.byteValue();
				HuffmanNode leftNode = new HuffmanNode(byteData);
				return leftNode;
			}
			else{
				return new HuffmanNode((helper(reader)),(helper(reader)));
			}
	}

	/**
	 * Takes a list of (Byte, Frequency) pairs (here represented as a map) and
	 * builds a tree for encoding the data items using the Huffman algorithm.
	 * 
	 * @throws NullPointerException
	 *             If freqs is null
	 * @throws IllegalArgumentException
	 *             if freqs is empty
	 */
	public HuffmanCode(Map<Byte, Integer> freqs){
		if(freqs == null){
			throw new NullPointerException();
		}
		if(freqs.isEmpty()){
			throw new IllegalArgumentException();
		}
		this.nodeMap = new HashMap<Byte, HuffmanNode>();
		Comparator<HuffmanNode> hnComparator = new HuffmanNodeComparator();
		MyPriorityQueue<HuffmanNode> PQ = new MyPriorityQueue<HuffmanNode>(hnComparator);
		
		//Set of all bytes in freqs map.
		Set<Byte> byteSet = freqs.keySet();
		Iterator<Byte> itr = byteSet.iterator();
		
		/*Iterate through all of the bytes.  For each byte, declare and initialize a new HuffmanNode
		 * with the corresponding frequency.  Store the mapping from byte to HuffmanNode in our nodeMap.
		 * Fill the priority queue with these HuffmanNodes (one HuffmanNode for each mapped byte).*/
		while(itr.hasNext()){
			byte temp = itr.next();
			Integer frequency = freqs.get(temp);
			HuffmanNode node = new HuffmanNode(frequency, temp);
			this.nodeMap.put(temp, node);
			PQ.offer(node);
		}
		
		/*Poll the two minimum HuffmanNodes (comparator based on byte frequencies).  Merge these
		 * two HuffmanNodes into a newNode with sum of frequencies.  Insert newNode into Priority Queue.
		 * We repeat this process until our Priority Queue contains 1 single HuffmanNode (the root of our tree).*/
		while(PQ.size() > 1){
			HuffmanNode min = PQ.poll();
			HuffmanNode secondMin = PQ.poll();
			HuffmanNode newNode = new HuffmanNode(min, secondMin);
			PQ.offer(newNode);
		}
		
		this.tree = PQ.peek();	
		HuffmanNodeComparator comparator = new HuffmanNodeComparator();
		mapNodes(this.tree, comparator);
	}

	/**
	 * <p>
	 * Turns this Huffman code into a stream of bits suitable for including in a
	 * compressed file.
	 * </p>
	 * 
	 * <p>
	 * The format for the tree is defined recursively. To emit the entire tree,
	 * you start by emitting the root. When you emit a node, if the node is a
	 * leaf node, you write the bit <tt>LEAF</tt> and then call the
	 * <tt>writeByte</tt> method of <tt>BitWriter</tt> on the nodes value.
	 * Otherwise, you emit the bit <tt>PARENT</tt>, then emit the left and right
	 * node.
	 * </p>
	 * 
	 * @param writer
	 *            BitWriter to write the header to
	 * @throws NullPointerException
	 *             If writer is null
	 * @throws IOException
	 *             If there is a problem writing to the underlying stream
	 */
	public void writeHeader(BitWriter writer) throws IOException {
		if(writer == null){
			throw new NullPointerException();
		}
		emit(writer, this.tree);
	}
	
	private void emit(BitWriter writer, HuffmanNode node) throws IOException{
		//Base Case: We have leaf node; write LEAF bit and write the node's byte.
		if(node.isLeaf()){
				writer.writeBit(LEAF);
				writer.writeByte(node.getValue());
		}
		//Write PARENT bit and emit left node and right node.
		else{
			writer.writeBit(PARENT);
			emit(writer, node.getLeft());
			emit(writer, node.getRight());
		}
	}

	/**
	 * This method reads bits from the reader until a complete codeword (from
	 * the given BitReader) has been read in. It returns the data item that the
	 * codeword corresponds to. The data format for this is defined by
	 * <tt>encode</tt>
	 * 
	 * @param reader
	 *            BitReader to read in the next codeword from
	 * @throws IOException
	 *             If there is an I/O error in the underlying reader. Also, if
	 *             the file contains invalid data or ends unexpectedly
	 * @throws NullPointerException
	 *             if reader is null
	 * @return The data item that the codeword corresponds to
	 */
	public Byte decode(BitReader reader) throws IOException {
		if(reader == null){
			throw new NullPointerException();
		}

		//Check edge case: Huffman Tree is one single node. 
		if(this.tree.isLeaf()){
			return this.tree.getValue();
		}
		return lookup(reader, this.tree);
	}
	
	
	/*Private recursive method that reads the given code word in the form of a BitReader.  Each time a bit
	 * is read, we know whether to go LEFT or RIGHT down the Huffman Tree.  When we reach a leaf node, we know
	 * that we have reached our destination byte.*/
	private Byte lookup(BitReader reader, HuffmanNode hn) throws IOException{
		
		HuffmanNode temp = hn;
		int nextBit;
		nextBit = reader.readBit();
		if(nextBit == LEFT){
				temp = temp.getLeft();
				//Leaf node is reached.
				if(temp.isLeaf()){
					return temp.getValue();
				}
				else{
					return lookup( reader, temp );
				}
			}
			else{
				temp = temp.getRight();
				//Leaf node is reached.
				if(temp.isLeaf()){
					return temp.getValue();
				}
				else{
					return lookup( reader, temp );
				}
			}
	}


	/**
	 * This method takes a data item and emits the corresponding codeword
	 * (Huffman code for that string). The bits <tt>LEFT</tt> and <tt>RIGHT</tt>
	 * are written so that if one takes that path in the Huffman tree they will
	 * get to the leaf node representing the data item.
	 * 
	 * @param item
	 *            value to encode
	 * @param writer
	 *            BitWriter to write the codeword to
	 * @throws NullPointerException
	 *             if the item or writer is null.
	 * @throws IllegalArgumentException
	 *             if the item doesn't exist in this Huffman coding
	 */
	public void encode(Byte item, BitWriter writer) throws IOException {
		if(writer == null){
			throw new NullPointerException();
		}
		if(!(this.nodeMap.containsKey(item))){
			throw new IllegalArgumentException();
		}
		
		Stack<Integer> codewordReversal = new Stack<Integer>();
		HuffmanNode node = this.nodeMap.get(item);
		
		//Walk up the tree and push the appropriate bit onto the stack at each step.
		while(node.getParent() != null){
			HuffmanNode parent = node.getParent();
			HuffmanNode left = parent.getLeft();
			if(node == left){
				codewordReversal.push(LEFT);
			}
			else{
				codewordReversal.push(RIGHT);
			}
			node = parent;
		}
		
		//Pop the stack until it is empty, writing each bit to the writer.
		while(!(codewordReversal.isEmpty())){
			writer.writeBit(codewordReversal.pop());
		}
	}

	/**
	 * Gets the root of the Huffman tree. This is helpful for testing.
	 */
	public HuffmanNode getCodeTreeRoot() {
		return this.tree;
	}
	
	
	/*Private comparator class that orders HuffmanNodes based on their corresponding FREQUENCIES.
	 */
	 private static class HuffmanNodeComparator implements Comparator<HuffmanNode>{
		
		public int compare(HuffmanNode hn1, HuffmanNode hn2){
			if((!(hn1 instanceof HuffmanNode)) || (!(hn2 instanceof HuffmanNode))){
				throw new ClassCastException();
			}
			return ((hn1.getFreq()) - (hn2.getFreq()));
		}
	}
}
