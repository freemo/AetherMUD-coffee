package com.planet_ink.coffee_mud.utils;
import java.util.*;

public class DVector implements Cloneable
{
	private int dimensions=1;
	private Vector[] stuff=null;
	public DVector(int dim)
	{
		if(dim<1) throw new java.lang.IndexOutOfBoundsException();
		dimensions=dim;
		stuff=new Vector[dimensions];
		for(int i=0;i<dimensions;i++)
			stuff[i]=new Vector();
	}
	
	public void clear()
	{
		if(stuff==null) return;
		
		synchronized(stuff)
		{
			for(int i=0;i<stuff.length;i++)
				stuff[i].clear();
		}
	}
	
	public int indexOf(Object O)
	{
		if(stuff==null) return -1;
		return stuff[0].indexOf(O);
	}
	
	public DVector copyOf()
	{
		try{
			return (DVector)this.clone();
		}
		catch(CloneNotSupportedException e){}
		{
			DVector V=new DVector(dimensions);
			V.stuff=stuff;
			return V;
		}
	}
	
	public void addElement(Object O)
	{
		if(dimensions!=1) throw new java.lang.IndexOutOfBoundsException();
		synchronized(stuff)
		{
			stuff[0].addElement(O);
		}
	}
	public void addElement(Object O, Object O1)
	{
		if(dimensions!=2) throw new java.lang.IndexOutOfBoundsException();
		synchronized(stuff)
		{
			stuff[0].addElement(O);
			stuff[1].addElement(O1);
		}
	}
	public void addElement(Object O, Object O1, Object O2)
	{
		if(dimensions!=3) throw new java.lang.IndexOutOfBoundsException();
		synchronized(stuff)
		{
			stuff[0].addElement(O);
			stuff[1].addElement(O1);
			stuff[2].addElement(O2);
		}
	}
	public void addElement(Object O, Object O1, Object O2, Object O3)
	{
		if(dimensions!=4) throw new java.lang.IndexOutOfBoundsException();
		synchronized(stuff)
		{
			stuff[0].addElement(O);
			stuff[1].addElement(O1);
			stuff[2].addElement(O2);
			stuff[4].addElement(O3);
		}
	}
	public int getIndex(Object O)
	{
		if(stuff[0].contains(O))
			return stuff[0].indexOf(O);
		return -1;
	}
	public Vector set(int dim)
	{
		if(dimensions<dim) throw new java.lang.IndexOutOfBoundsException();
		return stuff[dim-1];
	}
	public boolean contains(Object O){return stuff[0].contains(O);}
	public int size(){
		if(stuff==null) return 0;
		return stuff[0].size();
	}
	public void removeElementAt(int i)
	{
		for(int d=0;d<dimensions;d++)
			stuff[d].removeElementAt(i);
	}
	public void removeElement(Object O)
	{
		synchronized(stuff)
		{
			for(int i=stuff[0].size()-1;i>=0;i--)
			{
				if((O==stuff[0].elementAt(i))||(O.equals(stuff[0].elementAt(i))))
				for(int d=0;d<dimensions;d++)
					stuff[d].removeElementAt(i);
			}
		}
	}
	public Object elementAt(int i, int dim)
	{
		synchronized(stuff)
		{
			if(dimensions<dim) throw new java.lang.IndexOutOfBoundsException();
			return stuff[dim-1].elementAt(i);
		}
	}
	
	public void insertElementAt(int here, Object O)
	{
		if(dimensions!=1) throw new java.lang.IndexOutOfBoundsException();
		synchronized(stuff)
		{
			stuff[0].insertElementAt(O,here);
		}
	}
	public void setElementAt(int index, int dim, Object O)
	{
		if(dimensions!=1) throw new java.lang.IndexOutOfBoundsException();
		synchronized(stuff)
		{
			stuff[dim-1].setElementAt(O,index);
		}
	}
	public void insertElementAt(int here, Object O, Object O1)
	{
		if(dimensions!=2) throw new java.lang.IndexOutOfBoundsException();
		synchronized(stuff)
		{
			stuff[0].insertElementAt(O,here);
			stuff[1].insertElementAt(O1,here);
		}
	}
	public void insertElementAt(int here, Object O, Object O1, Object O2)
	{
		if(dimensions!=3) throw new java.lang.IndexOutOfBoundsException();
		synchronized(stuff)
		{
			stuff[0].insertElementAt(O,here);
			stuff[1].insertElementAt(O1,here);
			stuff[2].insertElementAt(O2,here);
		}
	}
	public void insertElementAt(int here, Object O, Object O1, Object O2, Object O3)
	{
		if(dimensions!=4) throw new java.lang.IndexOutOfBoundsException();
		synchronized(stuff)
		{
			stuff[0].insertElementAt(O,here);
			stuff[1].insertElementAt(O1,here);
			stuff[2].insertElementAt(O2,here);
			stuff[4].insertElementAt(O3,here);
		}
	}
}
