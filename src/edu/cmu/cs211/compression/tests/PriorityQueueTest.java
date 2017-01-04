package edu.cmu.cs211.compression.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.lang.NullPointerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import org.junit.Test;

import edu.cmu.cs211.compression.util.MyPriorityQueue;

/*
 * Some of the provided tests were left for you to finish implementing. You should
 * also consider adding additional unit tests.
 */
public class PriorityQueueTest {

	/** A simple test that offers one element into the priority queue */
	@Test
	public void testOffer() {
		Queue<String> pq = new MyPriorityQueue<String>();
		boolean ret = pq.offer("blah");
		assertEquals("offer on queue returns true", true, ret);
		assertEquals("size on queue returns 1 after insert", 1, pq.size());
	}
	
	public void testOffer2(){
		MyPriorityQueue<Integer> PQ = new MyPriorityQueue<Integer>();
		PriorityQueue<Integer> javaPQ = new PriorityQueue<Integer>();
		Random r = new Random();
		int i;
		
		for(i = 0; i < 250; i++){
			Integer num = r.nextInt();
			PQ.offer(num);
			javaPQ.offer(num);
		}
		assertEqualsPQ(PQ, javaPQ);
	}

	/** The priority queue should not allow a negative initial capacity */
	@Test(expected = IllegalArgumentException.class)
	public void capacityIllegalNeg() {
		new MyPriorityQueue<Object>(-2);
	}

	@Test (expected = NullPointerException.class)
	public void nullTest() {
		MyPriorityQueue <Object> PQ = new MyPriorityQueue <Object> ();
		Object o = null;
		PQ.offer(o);
	}

	@Test
	public void stressTest() {
		PriorityQueue<Integer> java_pq = new PriorityQueue<Integer>();
		MyPriorityQueue<Integer> my_pq = new MyPriorityQueue<Integer>();
		
		Random r = new Random(42);
		for (int i = 0; i < 10000; ++i) {
			// Create an element to potentially add to the PQ
			Integer element = Integer.valueOf(r.nextInt(100));

			/*
			 * TODO: Randomly select an operation that mutates the queue. We
			 * want to be sure that the queue grows, so we add with higher
			 * probability than removing.
			 */
			if (r.nextFloat() < 0.66f) {

			} else {

			}

			// Make sure the state is still consistent
			assertEqualsPQ(my_pq, java_pq);
		}
	}

	/**
	 * It is often helpful when you are unit testing a data structure to write
	 * helper methods that make sure that the state of your data structure are
	 * consistent at any given point in time.
	 * 
	 * This method takes two priority queues and compares them for equality.
	 */
	private <E> void assertEqualsPQ(MyPriorityQueue<E> pq1, PriorityQueue<E> pq2) {

		assertEquals(pq1.peek(), pq2.peek());
		assertEquals(pq1.size(), pq2.size());
		/*
		 * Array list uses the iterator of PQ to get all the elements so this
		 * tests that your iterator returns all of the elements in the PQ
		 */
		List<E> actual_elems = new ArrayList<E>(pq1);
		List<E> expected_elems = new ArrayList<E>(pq2);
		

		/*
		 * Because the order of elements returned by the iterator is
		 * unspecified, we restore a canonical ordering for the elements by
		 * sorting them.
		 */
		Collections.sort(actual_elems, pq1.comparator());
		Collections.sort(expected_elems, pq2.comparator());

		assertEquals(actual_elems, expected_elems);
	}
}
