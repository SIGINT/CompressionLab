package edu.cmu.cs211.compression.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import edu.cmu.cs211.compression.huffman.HuffmanCode;
import java.util.HashMap;

import org.junit.Test;

import edu.cmu.cs211.compression.huffman.HuffmanCompressor;

public class HuffmanCodeTest {

	/*
	 * Note: We include a few example tests to check the end-to-end
	 * functionality of your code. However, you are expected to write extensive
	 * unit tests to ensure your code functions perfectly in all scenarios.
	 */

	@Test
	public void random() throws Exception {
		byte[] x = new byte[1000];
		new Random(42).nextBytes(x);
		TestUtil.checkRoundTrip(new HuffmanCompressor(), x);
	}

	@Test(expected = NullPointerException.class)
	public void testNullFreqMapNull(){
		HashMap<Byte, Integer> hashmap = null;
		HuffmanCode hc = new HuffmanCode(hashmap);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testEmptyFreqMap(){
		HashMap<Byte, Integer> hashmap = new HashMap<Byte, Integer>();
		HuffmanCode hc = new HuffmanCode(hashmap);
	}
	
	@Test
	public void simple2() throws Exception{
		int i = 0;
		byte[] x = new byte[100];
		for( ; i < 50; i += 4){
			x[i] = 88;
		}
		i = 1;
		for( ; i < 50; i += 4){
			x[i] = 19;
		}
		i = 2;
		for( ; i < 50; i += 4){
			x[i] = 34;
		}
		i = 3;
		for( ; i < 50; i += 4){
			x[i] = 77;
		}
		i = 50;
		for( ; i < 100; i++){
			x[i] = 102;
		}
		TestUtil.checkRoundTrip(new HuffmanCompressor(), x);
	}
	@Test
	public void simple() throws Exception {
		String[] tests = new String[] { "asdfddffaassdasdfs",
				"asdfaaaaaaaadaaadaaaaaaafaaaaaaaaa", "mississippi",
				"a man a plan a canal panama",
				"colorless green ideas sleep furiously" };
		for (String test : tests) {
			byte[] input = test.getBytes("ASCII");
			TestUtil.checkRoundTrip(new HuffmanCompressor(), input);
		}
	}

}
