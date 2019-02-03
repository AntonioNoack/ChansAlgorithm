package basics;

import java.util.Iterator;

public class ArrayIterator<T> implements Iterable<T> {
	
	private T[] array;
	
	public ArrayIterator(T[] array){
		this.array = array;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			
			int index = 0;

			@Override
			public boolean hasNext() {
				return index < array.length;
			}

			@Override
			public T next() {
				return array[index++];
			}
			
		};
	}
}
