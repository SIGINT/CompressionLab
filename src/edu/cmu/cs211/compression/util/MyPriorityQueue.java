package edu.cmu.cs211.compression.util;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Queue;
import java.util.NoSuchElementException;


/**
 * An unbounded priority {@linkplain Queue queue}. This queue orders elements
 * according to an order specified at construction time, which is specified
 * either according to their <i>natural order</i> (see {@link Comparable}), or
 * according to a {@link java.util.Comparator}, depending on which constructor
 * is used. A priority queue does not permit <tt>null</tt> elements. A priority
 * queue relying on natural ordering also does not permit insertion of
 * non-comparable objects (doing so may result in <tt>ClassCastException</tt>).
 * <p>
 * The <em>head</em> of this queue is the <em>least</em> element with respect to
 * the specified ordering. If multiple elements are tied for least value, the
 * head is one of those elements -- ties are broken arbitrarily. The queue
 * retrieval operations <tt>poll</tt>, <tt>remove</tt>, <tt>peek</tt>, and
 * <tt>element</tt> access the element at the head of the queue.
 * <p>
 * A priority queue is unbounded, but has an internal <i>capacity</i> governing
 * the size of an array used to store the elements on the queue. It is always at
 * least as large as the queue size. As elements are added to a priority queue,
 * its capacity grows automatically. The details of the growth policy are not
 * specified.
 * <p>
 * This class and its iterator implement all of the <em>optional</em> methods of
 * the {@link Collection} and {@link Iterator} interfaces. The Iterator provided
 * in method {@link #iterator()} is <em>not</em> guaranteed to traverse the
 * elements of the MyPriorityQueue in any particular order. If you need ordered
 * traversal, consider using <tt>Arrays.sort(pq.toArray())</tt>.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> Multiple
 * threads should not access a <tt>MyPriorityQueue</tt> instance concurrently if
 * any of the threads modifies the list structurally. Instead, use the
 * thread-safe {@link java.util.concurrent.PriorityBlockingQueue} class.
 * <p>
 * Implementation note: this implementation provides O(log(n)) time for the
 * insertion methods (<tt>offer</tt>, <tt>poll</tt>, <tt>remove()</tt> and
 * <tt>add</tt>) methods; linear time for the <tt>remove(Object)</tt> and
 * <tt>contains(Object)</tt> methods; and constant time for the retrieval
 * methods (<tt>peek</tt>, <tt>element</tt>, and <tt>size</tt>).
 */
public class MyPriorityQueue<T> extends AbstractQueue<T> {

	private static final int DEFAULT_INITIAL_CAPACITY = 11;
	private T[] heap;
	private int size;
	private int capacity;
	private Comparator<? super T> override;

	/**
	 * Creates a <tt>MyPriorityQueue</tt> with the default initial capacity (11)
	 * that orders its elements according to their natural ordering (using
	 * <tt>Comparable</tt>).
	 */
	public MyPriorityQueue() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	/**
	 * Creates a <tt>MyPriorityQueue</tt> with the specified initial capacity
	 * that orders its elements according to their natural ordering (using
	 * <tt>Comparable</tt>).
	 * 
	 * @param initialCapacity
	 *            the initial capacity for this priority queue.
	 * @throws IllegalArgumentException
	 *             if <tt>initialCapacity</tt> is less than 1
	 */
	public MyPriorityQueue(int initialCapacity) {
		if(initialCapacity < 1){
			throw new IllegalArgumentException();
		}
		this.capacity = initialCapacity;
		this.size = 0;
		this.heap = (T[])(new Object[this.capacity + 1]);
		this.override = null;
	}

	/**
	 * Creates a <tt>MyPriorityQueue</tt> with the specified initial capacity
	 * that orders its elements according to the specified comparator.
	 * 
	 * @param initialCapacity
	 *            the initial capacity for this priority queue.
	 * @param comparator
	 *            the comparator used to order this priority queue. If
	 *            <tt>null</tt> then the order depends on the elements' natural
	 *            ordering.
	 * @throws IllegalArgumentException
	 *             if <tt>initialCapacity</tt> is less than 1
	 */
	public MyPriorityQueue(int initialCapacity, Comparator<? super T> comparator) {
		if(initialCapacity < 1){
			throw new IllegalArgumentException();
		}
		this.capacity = initialCapacity;
		this.size = 0;
		this.heap = (T[])(new Object[this.capacity + 1]);
		this.override = comparator;
	}

	/**
	 * Creates a <tt>MyPriorityQueue</tt> that orders its elements according to
	 * the specified comparator.
	 * 
	 * @param comparator
	 *            the comparator used to order this priority queue. If
	 *            <tt>null</tt> then the order depends on the elements' natural
	 *            ordering.
	 */
	public MyPriorityQueue(Comparator<? super T> comparator) {
		this(DEFAULT_INITIAL_CAPACITY, comparator);
	}

	/**
	 * Creates a <tt>MyPriorityQueue</tt> containing the elements in the
	 * specified collection. The priority queue has an initial capacity of 110%
	 * of the size of the specified collection or 1 if the collection is empty.
	 * If the specified collection is an instance of a
	 * {@link java.util.SortedSet} or is another <tt>MyPriorityQueue</tt>, the
	 * priority queue will be sorted according to the same comparator, or
	 * according to its elements' natural order if the collection is sorted
	 * according to its elements' natural order. Otherwise, the priority queue
	 * is ordered according to its elements' natural order.
	 * 
	 * @param c
	 *            the collection whose elements are to be placed into this
	 *            priority queue.
	 * @throws ClassCastException
	 *             if elements of the specified collection cannot be compared to
	 *             one another according to the priority queue's ordering.
	 */
	public MyPriorityQueue(Collection<? extends T> c) throws ClassCastException {
		Iterator< ? extends T> itr = c.iterator();
		this.capacity = DEFAULT_INITIAL_CAPACITY;
		this.heap = (T[])(new Object[this.capacity +1]);
		
		while(itr.hasNext()){
			this.offer(itr.next());
		}
	}

	/**
	 * Returns the comparator used to order the elements in this queue, or
	 * {@code null} if this queue is sorted according to the
	 * {@linkplain Comparable natural ordering} of its elements.
	 * 
	 * @return the comparator used to order this queue, or {@code null} if this
	 *         queue is sorted according to the natural ordering of its elements
	 */
	public Comparator<? super T> comparator() {
		if(this.override != null){
			return this.override;
		}
		else{
			return null;
		}
	}

	/**
	 * Returns the number of elements in this collection. If this collection
	 * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 * 
	 * @return the number of elements in this collection
	 */
	public int size() {
		if(this.size >= Integer.MAX_VALUE){
			return Integer.MAX_VALUE;
		}
		else{
			return this.size;
		}
			
	}

	/**
	 * Inserts the specified element into this priority queue.
	 * 
	 * @return {@code true} (as specified by {@link Queue#offer})
	 * @throws ClassCastException
	 *             if the specified element cannot be compared with elements
	 *             currently in this priority queue according to the priority
	 *             queue's ordering
	 * @throws NullPointerException
	 *             if the specified element is null
	 */
	public boolean offer(T o) throws ClassCastException{
		if(o == null)
			throw new NullPointerException();
		
		//Check if PQ is full.
		if(this.size >= this.capacity-1)
			this.resize();
		int hole = (this.size) + 1;
		
		//Perc Up. Principle comparison: o < heap[hole/2].
		for( ; ((hole > 1) && (this.thisCompare(o, heap[hole/2]) < 0)); hole /= 2){
			heap[hole] = heap[hole/2];
		}
		heap[hole] = o;
		(this.size)++;
		return true;
	}
	
	/*Private compare method that decides whether natural ordering has been overridden or 
	 * comparator has been predefined.
	 */
	private int thisCompare(T t1, T t2) throws ClassCastException{
		
		//Use natural ordering (compareTo).
		if((this.override) == null){
			Comparable<T> newT1 = (Comparable<T>)t1;
			return newT1.compareTo(t2);
		}
		else{
			return (this.override.compare(t1, t2));
		}
	}

	/**
	 * Retrieves and removes the head of this queue, or returns <tt>null</tt> if
	 * this queue is empty.
	 * 
	 * @return the head of this queue, or <tt>null</tt> if this queue is empty
	 */
	public T poll() {
		if(this.isEmpty()){
			return null;
		}
		//Store minimum item.
		T min = heap[1];
		heap[1] = heap[(this.size)--];
		percDown(1);
		return min;
	}

	/**
	 * Retrieves, but does not remove, the head of this queue, or returns
	 * <tt>null</tt> if this queue is empty.
	 * 
	 * @return the head of this queue, or <tt>null</tt> if this queue is empty
	 */
	public T peek() {
		if(this.size == 0){
			return null;
		}
		return heap[1];
	}

	/**
	 * Returns an iterator over the elements in this queue. The iterator does
	 * not return the elements in any particular order.
	 * 
	 * @return an iterator over the elements in this queue
	 */
	@Override
	public Iterator<T> iterator() {
		return new MyPriorityQueueIterator();
	}
	
	private void percDown( int hole ){
		int child;
		T temp = heap[hole];
		
		for( ; hole*2<=size; hole = child){
			child = hole*2;
			
			//Select smaller child.
			if((child != size) && (override.compare(heap[child+1], heap[child]) < 0)){
				child++;
			}
	
			if(override.compare(heap[child], temp) < 0){
				heap[hole] = heap[child];
			}
			else
				break;
		}
		heap[hole] = temp;
	}
	//***********
	public void printPQ(){
		int i;
		for (i = 1; i <= this.size; i++){
			System.out.println("HEAP INDEX: " + i + "  " + this.heap[i].toString());
		}
	}
	
	/*Private method for resizing underlying array that is backbone of MyPriorityQueue.  
	 * Replaces old array with new array that is twice the capacity of old array.  Uses 
	 * MyPriorityQueue's iterator to iterate through all of the items, inserting them into
	 * the new array.  Resets MyPriorityQueue's capacity to twice original value.
	 */
	private void resize(){
		int newCap = 2*(this.capacity);
		T[] newArray = (T[])(new Object[newCap]);
		int i = 1;
		Iterator<T> itr = this.iterator();
		while(itr.hasNext()){
			newArray[i++] = itr.next();
		}
		this.capacity = newCap;
		this.heap = newArray;
	}
	
	private class MyPriorityQueueIterator implements Iterator<T>{
		private int visited;
		
		public MyPriorityQueueIterator(){
			visited = 0;
		}
		
		public boolean hasNext(){
			return (visited < size);
		}
		
		public T next(){
			if(!(hasNext())){
				throw new NoSuchElementException();
			}
			return heap[++visited];
		}
		
		public void remove(){
			throw new UnsupportedOperationException();
		}
	}
	
}
