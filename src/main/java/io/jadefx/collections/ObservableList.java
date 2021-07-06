package io.jadefx.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

import io.jadefx.event.ElementCallback;

public class ObservableList<E> implements List<E>, Iterable<E> {
	private List<E> internal;
	
	private List<ElementCallback<E>> addCallbacks; 
	private List<ElementCallback<E>> removeCallbacks;

	public ObservableList(ObservableList<E> array) {
		this();
		
		for (int i = 0; i < array.size(); i++) {
			this.add(array.get(i));
		}
	}
	
	@SuppressWarnings("unchecked")
	public ObservableList(E... elements) {
		this();
		for (int i = 0; i < elements.length; i++) {
			this.add(elements[i]);
		}
	}

	public ObservableList() {
		this.internal = new ArrayList<>();
		this.addCallbacks = new ArrayList<>();
		this.removeCallbacks = new ArrayList<>();
	}

	public void setAddCallback( ElementCallback<E> e ) {
		this.addCallbacks.add(e);
	}

	public void setRemoveCallback( ElementCallback<E> e ) {
		this.removeCallbacks.add(e);
	}
	
	public void clearAddCallbacks() {
		this.addCallbacks.clear();
	}
	
	public void clearRemoveCallbacks() {
		this.removeCallbacks.clear();
	}

	@SuppressWarnings("unchecked")
	public boolean addAll(E... elements) {
		for (int i = 0; i < elements.length; i++) {
			add(elements[i]);
		}
		
		return true;
	}
	
	public void add(int index, E element) {
		if (element == null) {
			System.err.println("WARNING: Attempted to add null element to ObservableList.");
			Thread.dumpStack();
			return;
		}
		
		internal.add(index, element);

		if (!addCallbacks.isEmpty()) {
			for (ElementCallback<E> e : addCallbacks)
				e.onEvent(element);
		}
		
		return;
	}
	
	public boolean add(E element) {
		add( internal.size(), element );
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void removeAll(E... elements) {
		for (int i = 0; i < elements.length; i++) {
			remove(elements[i]);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean remove(Object element) {
		if (internal.remove(element) && !removeCallbacks.isEmpty()) {
			for (ElementCallback<E> e : removeCallbacks)
				e.onEvent((E) element);
		}
		
		return true;
	}
	
	public E remove(int index) {
		E el = internal.remove(index);
		if (el != null && !removeCallbacks.isEmpty()) {
			for (ElementCallback<E> e : removeCallbacks)
				e.onEvent((E) el);
		}
		
		return el;
	}

	public int size() {
		return internal.size();
	}

	public E get(int i) {
		return internal.get(i);
	}

	public void clear() {
		while(internal.size()>0) {
			E obj = internal.get(0);
			remove(obj);
		}
	}

	public boolean contains(Object element) {
		return internal.contains(element);
	}

	public Stream<E> stream() {
		return this.internal.stream();
	}

	public Stream<E> parallelStream() {
		return this.internal.parallelStream();
	}

	@Override
	public Iterator<E> iterator() {
		return internal.iterator();
	}

	@Override
	public boolean isEmpty() {
		return internal.size() == 0;
	}

	@Override
	public Object[] toArray() {
		return internal.toArray();
	}

	@Override
    public <T> T[] toArray(T[] a) {
    	return internal.toArray(a);
    }

	@Override
	public boolean containsAll(Collection<?> c) {
		return internal.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		for (E o : c )
			add(o);
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		int i = index;
		for (E o : c )
			add(i++, o);
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object o : c )
			remove(o);
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return internal.retainAll(c);
	}

	@Override
	public E set(int index, E element) {
		boolean toAdd = !this.contains(element);
			
		E e = internal.set(index, element);
		
		if ( toAdd && !addCallbacks.isEmpty()) {
			for (ElementCallback<E> callback : addCallbacks)
				callback.onEvent(element);
		}
		
		return e;
	}

	@Override
	public int indexOf(Object o) {
		return internal.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return internal.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return internal.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return internal.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return internal.subList(fromIndex, toIndex);
	}
}
