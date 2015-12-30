package com.planet_ink.coffee_mud.core.collections;

import java.util.*;

/*
   Copyright 2000-2015 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
public class SLinkedHashtable<K, F> implements java.util.Map<K, F>, java.io.Serializable, SafeCollectionHost
{
	private static final long	         serialVersionUID	= 6687178785122561993L;
	private volatile LinkedHashMap<K, F>	H;
	private final Date lastIteratorCall = new Date(0);

	public SLinkedHashtable()
	{
		super();
		H = new LinkedHashMap<K, F>();
	}

	public SLinkedHashtable(int size)
	{
		super();
		H = new LinkedHashMap<K, F>(size);
	}

	public SLinkedHashtable(Map<K, F> H)
	{
		super();
		this.H = new LinkedHashMap<K, F>();
		if (H != null)
		{
			for (final K o : H.keySet())
				put(o, H.get(o));
		}
	}

	@SuppressWarnings("unchecked")
	public SLinkedHashtable(Object[][] H)
	{
		super();
		this.H = new LinkedHashMap<K, F>();
		if (H != null)
		{
			for (final Object[] o : H)
				this.H.put((K) o[0], (F) o[1]);
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized Hashtable<K, F> toHashtable()
	{
		return (Hashtable<K, F>) H.clone();
	}

	public synchronized Vector<String> toStringVector(String divider)
	{
		final Vector<String> V = new Vector<String>(size());
		for (final Object S : keySet())
		{
			if (S != null)
			{
				final Object O = get(S);
				if (O == null)
					V.add(S.toString() + divider);
				else
					V.add(S.toString() + divider + O.toString());
			}
		}
		return V;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void clear()
	{
		if (doClone())
			H = (LinkedHashMap<K, F>) H.clone();
		H.clear();
	}

	@SuppressWarnings("unchecked")
	public synchronized SLinkedHashtable<K, F> copyOf()
	{
		final SLinkedHashtable<K, F> SH = new SLinkedHashtable<K, F>();
		SH.H = (LinkedHashMap<K, F>) H.clone();
		return SH;
	}

	public synchronized boolean contains(Object arg0)
	{
		return H.containsKey(arg0);
	}

	@Override
	public synchronized boolean containsKey(Object arg0)
	{
		return H.containsKey(arg0);
	}

	@Override
	public synchronized boolean containsValue(Object arg0)
	{
		return H.containsValue(arg0);
	}

	public synchronized Enumeration<F> elements()
	{
		return new SafeFeedbackEnumeration<F>(new IteratorEnumeration<F>(H.values().iterator()), this);
	}

	public synchronized Enumeration<Map.Entry<K, F>> entries()
	{
		return new SafeFeedbackEnumeration<Map.Entry<K, F>>(new IteratorEnumeration<Map.Entry<K, F>>(H.entrySet().iterator()), this);
	}

	@Override
	public synchronized Set<java.util.Map.Entry<K, F>> entrySet()
	{
		return new SafeChildSet<java.util.Map.Entry<K, F>>(H.entrySet(), this);
	}

	@Override
	public boolean equals(Object arg0)
	{
		return this == arg0;
	}

	@Override
	public synchronized F get(Object arg0)
	{
		return H.get(arg0);
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

	@Override
	public synchronized boolean isEmpty()
	{
		return H.isEmpty();
	}

	public synchronized Enumeration<K> keys()
	{
		return new SafeFeedbackEnumeration<K>(new IteratorEnumeration<K>(H.keySet().iterator()), this);
	}

	@Override
	public synchronized Set<K> keySet()
	{
		return new SafeChildSet<K>(H.keySet(), this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized F put(K arg0, F arg1)
	{
		if (doClone())
			H = (LinkedHashMap<K, F>) H.clone();
		return H.put(arg0, arg1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized F remove(Object arg0)
	{
		if (doClone())
			H = (LinkedHashMap<K, F>) H.clone();
		return H.remove(arg0);
	}

	@Override
	public synchronized int size()
	{
		return H.size();
	}

	@Override
	public String toString()
	{
		return super.toString();
	}

	@Override
	public synchronized Collection<F> values()
	{
		return new SafeChildCollection<F>(H.values(), this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void putAll(Map<? extends K, ? extends F> arg0)
	{
		if (doClone())
			H = (LinkedHashMap<K, F>) H.clone();
		H.putAll(arg0);
	}

	private boolean doClone()
	{
		synchronized(this.lastIteratorCall)
		{
			return System.currentTimeMillis() < this.lastIteratorCall.getTime();
		}
	}
	
	@Override
	public void returnIterator(Object iter) 
	{
	}

	@Override
	public void submitIterator(Object iter) 
	{
		synchronized(this.lastIteratorCall)
		{
			this.lastIteratorCall.setTime(System.currentTimeMillis() + ITERATOR_TIMEOUT_MS);
		}
	}
}
