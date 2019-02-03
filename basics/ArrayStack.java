package basics;

import java.util.ArrayList;
import java.util.Iterator;

public class ArrayStack<T> implements Iterable<T> {

	private int size = 0;
	private ArrayList<T> list;
	
	public ArrayStack(int n){
		list = new ArrayList<T>(n);
	}
	
	// returns the n'th item down (zero-relative) from the top of this stack without removing it.
	public T peek(int n){
		return list.get(size - n - 1);
	}
	
	public void push(T t){
		if(size < list.size()){
			list.set(size, t);
		} else {
			list.add(t);
		};size++;
	}
	
	public void pop(){
		size--;
	}
	
	public int size(){
		return size;
	}
	
	public T[] toArray(T[] empty){
		return list.subList(0, size).toArray(empty);
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			
			int index = 0;

			@Override
			public boolean hasNext() {
				return index < size;
			}

			@Override
			public T next() {
				return list.get(index++);
			}
			
		};
	}
	
}
