package edu.cmu.cs211.compression.tests;

import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import edu.cmu.cs211.compression.bw.*;

public class BurrowsWheelerCompressorTest {

	/*
	 * Note: This test checks the BurrowsWheelerCompressor, which uses your
	 * BurrowsWheelerTransformer, MoveToFrontTransformer, and HuffmanCode. You
	 * should test each of these classes independently first to make sure they
	 * all work on their own.
	 */
	
	@Test
	public void testMoveToFrontTransform() throws Exception{
		MoveToFrontTransformer mtf = new MoveToFrontTransformer();
		int i;
		byte[] input = new byte[60];
		for(i = 0; i < 60; i +=4){
			input[i] = ((Integer)(256 - (i*3))).byteValue();
		}
		for(i = 1; i < 60; i +=4){
			input[i] = ((Integer)(256 - (i*4))).byteValue();
		}
		for(i = 2; i < 60; i +=4){
			input[i] = ((Integer)(256 - (i*2))).byteValue();
		}
		for(i = 2; i < 60; i +=4){
			input[i] = 88;
		}
		byte[] output = mtf.transform(input);
		byte[] reversed = mtf.invertTransform(output);
		
		for(i = 0; i < input.length; i++){
			byte b1 = input[i];
			byte b2 = reversed[i];
			
			assertEquals(b1, b2);
		}
	}
	
	@Test
	public void testMoveToFrontTransformEmpty() throws Exception{
		MoveToFrontTransformer mtf = new MoveToFrontTransformer();
		byte[] input = new byte[0];
		byte[] output = mtf.transform(input);
		byte[]reversed = mtf.invertTransform(output);
		
		assertEquals(reversed.length, 0);
	}
	
	@Test
	public void testMoveToFrontTransformRepeats() throws Exception{
		MoveToFrontTransformer mtf = new MoveToFrontTransformer();
		byte[] input = new byte[50];
		int i;
		for(i = 0; i < 50; i++){
			input[i] = 99;
		}
		byte[] output = mtf.transform(input);
		byte[]reversed = mtf.invertTransform(output);
		
		for(i = 0; i < reversed.length; i++){
			assertEquals(reversed[i], input[i]);
		}
	}
	
	@Test
	public void testMoveToFrontTransformRandom() throws Exception{
		MoveToFrontTransformer mtf = new MoveToFrontTransformer();
		byte[] input = new byte[500];
		int i;
		Random r = new Random();
		for(i = 0; i < 500; i++){
			input[i] = ((Integer)(r.nextInt())).byteValue();
		}
		byte[] output = mtf.transform(input);
		byte[]reversed = mtf.invertTransform(output);
		
		for(i = 0; i < 500; i++){
			assertEquals(reversed[i], input[i]);
		}
	}
	
	
	@Test
	public void testBWEmpty() throws Exception{
		MoveToFrontTransformer bw = new MoveToFrontTransformer();
		byte[] input = new byte[0];
		byte[] output = bw.transform(input);
		byte[]reversed = bw.invertTransform(output);
		
		assertEquals(reversed.length, 0);
	}
	
	@Test
	public void testBWRandom() throws Exception{
		BurrowsWheelerTransformer bw = new BurrowsWheelerTransformer();
		byte[] input = new byte[500];
		int i;
		Random r = new Random();
		for(i = 0; i < 500; i++){
			input[i] = ((Integer)(r.nextInt())).byteValue();
		}
		byte[] output = bw.transform(input);
		byte[]reversed = bw.invertTransform(output);
		
		for(i = 0; i < 500; i++){
			assertEquals(reversed[i], input[i]);
		}
	}
	
	@Test 
	public void testBWTransformMethodOnly() throws Exception{
		BurrowsWheelerTransformer bw = new BurrowsWheelerTransformer();
		byte[] input = new byte[4];
		input[0] = 'l';
		input[1] = 'u';
		input[2] = 'k';
		input[3] = 'e';
		byte[] output = bw.transform(input);
		int len = output.length;
		assertEquals(len, (4+(input.length)));
		
		assertEquals(output[4], (byte)('k'));
		assertEquals(output[5], (byte)('u'));
		assertEquals(output[6], (byte)('e'));
		assertEquals(output[7], (byte)('l'));
	}
	
	@Test
	public void testRepeatBWTransformMethodOnly() throws Exception{
		BurrowsWheelerTransformer bw = new BurrowsWheelerTransformer();
		byte[] input = new byte[4];
		input[0] = 'l';
		input[1] = 'u';
		input[2] = 'l';
		input[3] = 'l';
		byte[] output = bw.transform(input);
		int len = output.length;
		int headerIndex = ( output[0]<<24 | (output[1]&0xff)<<16 | (output[2]&0xff)<<8 | (output[3]&0xff));
		
		assertEquals(2, headerIndex);
		assertEquals(len, (4+(input.length)));
		
		assertEquals(output[4], 'u');
		assertEquals(output[5], 'l');
		assertEquals(output[6], 'l');
		assertEquals(output[7], 'l');
	}
	
	@Test
	public void testBWSimple() throws Exception{
		BurrowsWheelerTransformer bw = new BurrowsWheelerTransformer();
		int i;
		String[] strings = new String[] {"aluhgttttrsls", "neeeeew", "i hate java generics", 
				"man this is fun", "junit"};
		for(String s : strings){
			byte[] input = s.getBytes("ASCII");
			byte[] output = bw.transform(input);
			byte[] reversed = bw.invertTransform(output);
			for(i = 0; i < reversed.length; i++){
				assertEquals(reversed[i], input[i]);
			}
		}
	}


	@Test
	public void random() throws Exception {
		byte[] x = new byte[1000];
		new Random(42).nextBytes(x);
		TestUtil.checkRoundTrip(new BurrowsWheelerCompressor(), x);
	}

	@Test
	public void simple() throws Exception {
		String[] tests = new String[] { "asdfddffaassdasdfs",
				"asdfaaaaaaaadaaadaaaaaaafaaaaaaaaa", "mississippi",
				"a man a plan a canal panama",
				"colorless green ideas sleep furiously" };
		for (String test : tests) {
			byte[] input = test.getBytes("ASCII");
			TestUtil.checkRoundTrip(new BurrowsWheelerCompressor(), input);
		}
	}

}