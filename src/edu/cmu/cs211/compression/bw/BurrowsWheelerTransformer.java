package edu.cmu.cs211.compression.bw;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;

import edu.cmu.cs211.compression.Transformer;

public class BurrowsWheelerTransformer extends Transformer {

	/**
	 * Transform a byte sequence using the Burrows-Wheeler transformation
	 * 
	 * The transformed byte sequence will be longer than the original as it
	 * includes a header. The header is an integer encodes as four bytes and
	 * represents the position of the first element in the sorted sequence.
	 * 
	 * @param input
	 *            a byte sequence
	 * 
	 * @return transformed byte sequence
	 * 
	 * @throws NullPointerException
	 */
	@Override
	public byte[] transform(final byte[] input) {
		if(input == null){
			throw new NullPointerException();
		}
		if(input.length == 0){
			return new byte[0];
		}
		
		int length = input.length;
		int i, c;
		byte[] finalResult = new byte[length+4];
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for(i = 0; i < length; i++){
			indices.add(i);
		}
		
		BWComparator comparator = new BWComparator( input );
		
		Collections.sort(indices, comparator);
		
		for(c = 0; c < indices.size(); c++ ){
				if(indices.get(c) == 0 ){
					break;
				}
			}
		//c now holds the index where the original input resides within the BW matrix.
		
		//Write the header to represent the integer in a 4-byte format.
        for (i = 0; i < 4; i++) {
            int offset = (3 - i) * 8;
            finalResult[i] = (byte) ((c >>> offset) & 0xFF);
        }
	
		i = 0;
		c = 4;
		for( ; i < length; i++, c++){
			finalResult[c] = input[((indices.get(i) + (length - 1)) % length)];
		}
		
		return finalResult;
	}
	
	final class BWComparator implements Comparator<Integer>{
		byte[] bytes;
		
		public BWComparator( byte[] bytes ){
			this.bytes = bytes;
		}
		
		public int compare( Integer i1, Integer i2 ){
			int first = i1;
			int second = i2;
			
			while(bytes[first] == bytes[second]){
				first = increment(first, bytes.length);
				second = increment(second, bytes.length);
			}
			
			return (bytes[first] - bytes[second]);
	}
	}
		
		//Private helper method.  Returns incremented i based on len to preserve wraparound.
		private int increment(int i, int len){
			if(++i >= len){
				i = 0;
			}
			return i;
		}

	/**
	 * Invert the Burrows-Wheeler transformation
	 * 
	 * @param input
	 *            a byte sequence from the output of transform()
	 * 
	 * @return byte sequence after inverting the transformation
	 * 
	 * @throws NullPointerException
	 */
	@Override
	public byte[] invertTransform(final byte[] input) {
		if( input == null ){
			throw new NullPointerException();
		}
		if(input.length == 4){
			return new byte[0];
		}
		
		   /*int indexOfOrig2 = 0;
	        for (int i = 0; i < 4; i++) {
	            int shift = (4 - 1 - i) * 8;
	            indexOfOrig += (input[i] & 0x000000FF) << shift;
	        }*/
		
		int indexOfOrig = ( input[0]<<24 | (input[1]&0xff)<<16 | (input[2]&0xff)<<8 | (input[3]&0xff));
		int lengthWithoutHeader = (input.length)-4;
		int i,c;
		byte[] finalResult = new byte[lengthWithoutHeader];
		byte[] inputWithoutHeader = new byte[lengthWithoutHeader];
		ArrayList<Integer> indices = new ArrayList<Integer>(lengthWithoutHeader+1);
		
		i = 0;
		c = 4;
		//Fill arrayList with indices and fill inputWithoutHeader.
		for( ; i < lengthWithoutHeader; i++, c++){
			indices.add(i);
			inputWithoutHeader[i] = input[c];
		}
		
		BWComparatorInverse comparator = new BWComparatorInverse( inputWithoutHeader );
		//Sort the Bytes by their natural ordering.
		Collections.sort(indices, comparator);
		
		int currentIndex = indices.get(indexOfOrig);
		for(i = 0; i < lengthWithoutHeader; i++){
			finalResult[i] = inputWithoutHeader[currentIndex];
			currentIndex = indices.get(currentIndex);
		}
		
		return finalResult;
		
	}
	
	final class BWComparatorInverse implements Comparator<Integer>{
		byte[] bytes;
		
		public BWComparatorInverse( byte[] bytes ){
			this.bytes = bytes;
		}
		
		public int compare( Integer i1, Integer i2 ){
			Byte b1 = bytes[i1];
			Byte b2 = bytes[i2];
			return b1.compareTo(b2);
		}
	}
	
}
