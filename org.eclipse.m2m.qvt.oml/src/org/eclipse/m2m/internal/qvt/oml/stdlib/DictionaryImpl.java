/*******************************************************************************
 * Copyright (c) 2009, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.stdlib;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.m2m.qvt.oml.util.Dictionary;

public class DictionaryImpl<KeyT, T> implements Dictionary<KeyT, T> {

	private final Map<KeyT, T> fMap;
	
	public DictionaryImpl() {
		fMap = new HashMap<KeyT, T>();
	}

	public DictionaryImpl(DictionaryImpl<KeyT, T> source) {
		fMap = new HashMap<KeyT, T>(source.fMap);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.m2m.qvt.oml.util.Dictionary#get(KeyT)
	 */
	public T get(KeyT k) {
		return fMap.get(k);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.m2m.qvt.oml.util.Dictionary#put(KeyT, T)
	 */
	public void put(KeyT k, T v) {
		fMap.put(k, v);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.m2m.qvt.oml.util.Dictionary#hasKey(KeyT)
	 */
	public boolean hasKey(KeyT k) {
		return fMap.containsKey(k);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.m2m.qvt.oml.util.Dictionary#defaultget(KeyT, T)
	 */
	public T defaultget(KeyT k, T v) {
		return fMap.containsKey(k) ? fMap.get(k) : v;
	}	

	public void clear() {
		fMap.clear();
	}

	public int size() {
		return fMap.size();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.m2m.qvt.oml.util.Dictionary#keys()
	 */
	public List<KeyT> keys() {
		return new MutableListImpl<KeyT>(fMap.keySet());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.m2m.qvt.oml.util.Dictionary#values()
	 */
	public List<T> values() {
		return new MutableListImpl<T>(fMap.values());
	}	
	
	////// Collection interface only
	
	@SuppressWarnings("unchecked")
	public boolean addAll(Collection<? extends T> c) {
		if(c instanceof DictionaryImpl) {
			DictionaryImpl<KeyT, T> anotherDict = (DictionaryImpl<KeyT, T>) c;
			fMap.putAll(anotherDict.fMap);
		}
		else {
			for (T o : c) {
				add(o);
			}
		}
		return true;
	}	
	
	public boolean add(T o) {
		boolean canAdd = fMap.containsKey(null) ? fMap.get(null) != o : true;
		if(canAdd) {
			put(null, o);
		}
		return canAdd;
	}

	public boolean contains(Object o) {
		return fMap.containsValue(o);
	}

	public boolean containsAll(Collection<?> c) {
		return fMap.values().containsAll(c);
	}

	public boolean isEmpty() {
		return fMap.isEmpty();
	}

	public Iterator<T> iterator() {
		return fMap.values().iterator();
	}

	public boolean remove(Object o) {
		return fMap.values().remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return fMap.values().removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return fMap.values().retainAll(c);
	}

	public Object[] toArray() {
		return fMap.values().toArray();
	}

	public <E> E[] toArray(E[] a) {
		return fMap.values().toArray(a);
	}
	
	@Override
	public String toString() {	
		return Dictionary.SINGLETON_NAME + fMap.toString();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DictionaryImpl))
            return false;

        return fMap.equals(((DictionaryImpl) o).fMap);
    }
	
	@Override
    public int hashCode() {
		return fMap.hashCode();
    }
    
}
