package edu.cmu.cs211.compression.bw;

import java.util.ArrayList;
import java.util.Iterator;

import edu.cmu.cs211.compression.Transformer;

/**
 * A transformer that implements the Move-to-Front algorithm
 * 
 * The initial list should start at 0x00 and increment to 0xFF
 */
public class MoveToFrontTransformer extends Transformer {

	/**
	 * Transform a byte sequence using the Move-To-Front transformation
	 * 
	 * @param input
	 *            a byte sequence
	 * 
	 * @return transformed byte sequence
	 * 
	 * @throws NullPointerException
	 */
	@Override
	public byte[] transform(byte[] input) {
		ArrayList<Byte> symbolList = new ArrayList<Byte>(256);
		byte[] result = new byte[input.length];
		int i = 0;
		for( ; i <= 255; i++){
			byte b = (byte)i;
			Byte b2 = new Byte(b);
			symbolList.add(b2);
		}
		for(i = 0; i < input.length; i++){
			Byte current = input[i];
			int output = symbolList.indexOf(current);
			result[i] = (byte)output;
			symbolList.remove(current);
			symbolList.add(0,current);
		}
		return result;
	}

	/**
	 * Invert the Move-To-Front transformation
	 * 
	 * @param input
	 *            a byte sequence from the output of transform()
	 * 
	 * @return byte sequence after inverting the transformation
	 * 
	 * @throws NullPointerException
	 */
	@Override
	public byte[] invertTransform(byte[] input){
		ArrayList<Byte> symbolList = new ArrayList<Byte>(256);
		byte[] result = new byte[input.length];
		int i = 0;
		for( ; i <= 255; i++){
			byte b = (byte)i;
			Byte b2 = new Byte(b);
			symbolList.add(b2);
		}
		for(i = 0; i < input.length; i++){
			byte current = input[i];
			int index = current & 0xFF;
			Byte val = symbolList.get(index);
			result[i] = val;
			symbolList.remove(index);
			symbolList.add(0, val);
		}
		return result;
	}

}
