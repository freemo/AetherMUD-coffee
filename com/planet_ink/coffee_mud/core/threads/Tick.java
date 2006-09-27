package com.planet_ink.coffee_mud.core.threads;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;

import java.sql.*;
import java.net.*;
import java.util.*;


/* 
   Copyright 2000-2006 Bo Zimmerman

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
public class Tick extends Thread implements TickableGroup, Cloneable
{
	public long lastStart=0;
	public long lastStop=0;
	public long milliTotal=0;
	public long tickTotal=0;
	public ThreadEngine myEngine=null;
	public boolean solitaryTicker=false;
	private static int tickObjReference=0;
    private int tickObjectCounter=0;
    public long TICK_TIME=Tickable.TIME_TICK;
	protected Vector tickers=new Vector();
	protected int numTickers=0; 
	protected Vector localItems=new Vector();
	protected boolean shutdown=false;
	
	public Tick(ThreadEngine theEngine, long sleep)
	{
		super("Tick."+(tickObjReference+1));
        tickObjectCounter=tickObjReference++;
		myEngine=theEngine;
        TICK_TIME=sleep;
	}

	public Tick copyOf()
	{
		try{
			Tick T=(Tick)this.clone();
			T.tickers=(Vector)tickers.clone();
			return T;
		}
		catch(Exception e){}
		return this;
	}
	
	protected int getIndex(Tickable T)
	{
		if(numTickers==0) return -1;
		if(T==null) return -1;
		long hashCode=T.hashCode();
		synchronized(tickers)
		{
			int start=0;
			int end=numTickers()-1;
			int mid=0;
			while(start<=end)
			{
	            mid=(end+start)/2;
	            if(((TockClient)tickers.elementAt(mid)).clientObject.hashCode()==hashCode) return mid;
	            if(((TockClient)tickers.elementAt(mid)).clientObject.hashCode()>hashCode)
	                end=mid-1;
	            else
	                start=mid+1;
			}
			if(end<0) return 0;
			if(start>=numTickers()) return numTickers()-1;
			return mid;
		}
	}
	
	public Iterator tickers(){return ((Vector)tickers.clone()).iterator();}
	public int numTickers(){return numTickers;}
	public TockClient fetchTicker(int i){
		try{
			return (TockClient)tickers.elementAt(i);
		}catch(Exception e){}
		return null;
	}
	
	public Iterator getTickSet(Tickable T, int tickID)
	{
		synchronized(tickers)
		{
			int start=getIndex(T);
			if(start<0) return null;
			if(((TockClient)tickers.elementAt(start)).clientObject.hashCode()!=T.hashCode()) return null;
			Vector V=new Vector();
			int end=start;
			while((start>1)&&(((TockClient)tickers.elementAt(start-1)).clientObject.hashCode()==T.hashCode())) start--;
			while((end<numTickers-1)&&(((TockClient)tickers.elementAt(end+1)).clientObject.hashCode()==T.hashCode())) end++;
			if(tickID<0)
			{
				for(int v=start;v<=end;v++)
					if(((TockClient)tickers.elementAt(v)).clientObject==T)
						V.addElement(tickers.elementAt(v));
			}
			else
			{
				for(int v=start;v<=end;v++)
					if((((TockClient)tickers.elementAt(v)).clientObject==T)
					&&(((TockClient)tickers.elementAt(v)).tickID==tickID))
						V.addElement(tickers.elementAt(v));
			}
			return V.iterator();
		}
	}
	
	public Vector getLocalItems(int itemTypes, Room R)
	{
		synchronized(tickers)
		{
			localItems.clear();
			TockClient C;
			for(Iterator e=tickers.iterator();e.hasNext();)
			{
				C=(TockClient)e.next();
				switch(itemTypes)
				{
				case 0:
					if(C.clientObject instanceof MOB)
					{
						if(((MOB)C.clientObject).getStartRoom()==R)
							localItems.addElement(C);
					}
					else
					if((C.clientObject instanceof ItemTicker)
					&&((((ItemTicker)C.clientObject).properLocation()==R)))
						localItems.addElement(C);
					break;
				case 1:
					if((C.clientObject instanceof ItemTicker)
					&&((((ItemTicker)C.clientObject).properLocation()==R)))
						localItems.addElement(C);
					break;
				case 2:
					if((C.clientObject instanceof MOB)
					&&(((MOB)C.clientObject).getStartRoom()==R))
						localItems.addElement(C);
					break;
				}
			}
			if(localItems.size()>0)
				return (Vector)localItems.clone();
			return null;
		}
	}
	
	
	public boolean contains(Tickable T, int tickID)
	{
		synchronized(tickers)
		{
			int start=getIndex(T);
			if(start<0) return false;
			if(((TockClient)tickers.elementAt(start)).clientObject.hashCode()!=T.hashCode()) return false;
			int end=start;
			while((start>1)&&(((TockClient)tickers.elementAt(start-1)).clientObject.hashCode()==T.hashCode())) start--;
			while((end<numTickers-1)&&(((TockClient)tickers.elementAt(end+1)).clientObject.hashCode()==T.hashCode())) end++;
			if(tickID<0)
			{
				for(int v=start;v<=end;v++)
					if(((TockClient)tickers.elementAt(v)).clientObject==T) 
						return true;
			}
			else
			for(int v=start;v<=end;v++)
				if((((TockClient)tickers.elementAt(v)).clientObject==T)
				&&(((TockClient)tickers.elementAt(v)).tickID==tickID))
					return true;
			return false;
		}
	}
    
    public int getCounter(){return tickObjectCounter;}
    
	public void delTicker(TockClient C)
	{
		synchronized(tickers)
		{
			tickers.removeElement(C);
			numTickers=tickers.size();
		}
	}
	public void addTicker(TockClient C)
	{
		synchronized(tickers)
		{
    		if((C==null)||(C.clientObject==null)) return;
    		int insertAt=getIndex(C.clientObject);
    		if(insertAt<0)
    			tickers.addElement(C);
    		else
    		{
	            if(((TockClient)tickers.elementAt(insertAt)).clientObject.hashCode()>=C.clientObject.hashCode())
	            	tickers.insertElementAt(C,insertAt);
				else
				if(insertAt==numTickers()-1)
					tickers.addElement(C);
				else
					tickers.insertElementAt(C,insertAt+1);
    		}
			numTickers=tickers.size();
		}
	}


	public Tick(long sleep)
	{
        super("Tick."+(tickObjReference+1));
        tickObjectCounter=tickObjReference++;
        TICK_TIME=sleep;
		this.start();
	}

	public Tick(String a_name, long sleep)
	{
        super("Tick."+ a_name + "." +(tickObjReference+1));
        setDaemon(true);
        tickObjectCounter=tickObjReference++;
        TICK_TIME=sleep;
		this.start();
	}

	public boolean awake=false;
	public TockClient lastClient=null;
    public Tickable lastTicked()
    {
        return lastClient!=null?lastClient.clientObject:null;
    }

	public void shutdown()
	{
		tickers.removeAllElements();
		numTickers=tickers.size();
		this.interrupt();
		try{Thread.sleep(1);}catch(Exception e){}
		if(!shutdown)
		{
			int x=100;
			while((x<100)&&(!shutdown))
				try{Thread.sleep(1);}catch(Exception e){}
			if(!shutdown) this.stop();
		}
	}

	public static boolean tickTicker(TockClient C, boolean allSuspended)
	{
		if((C.suspended)||(allSuspended))
			return false;

		if((--C.tickDown)<1)
		{
			C.tickDown=C.reTickDown;
			try
			{
				if(!C.clientObject.tick(C.clientObject,C.tickID))
					return true;
			}
			catch(Exception t)
			{
				Log.errOut("ServiceEngine",t);
			}
		}
		return false;
	}

	public void run()
	{
		lastStart=System.currentTimeMillis();
		shutdown=false;
		while(true)
		{
			try
			{
				lastStop=System.currentTimeMillis();
				milliTotal+=(lastStop-lastStart);
				tickTotal++;
				awake=false;
				Thread.sleep(TICK_TIME);
				awake=true;
				lastStart=System.currentTimeMillis();
				lastClient=null;
				if((CMProps.getBoolVar(CMProps.SYSTEMB_MUDSTARTED))
                &&(!CMLib.threads().isAllSuspended()))
				{
                    
					for(Iterator i=tickers();i.hasNext();)
					{
						TockClient client=(TockClient)i.next();
						lastClient=client;
						if((client.lastStart!=0)&&(client.lastStop!=0))
						{
							client.milliTotal+=(client.lastStop-client.lastStart);
							client.tickTotal++;
						}
						client.lastStart=System.currentTimeMillis();
						if(tickTicker(client,CMLib.threads().isAllSuspended()))
							delTicker(client);
						client.lastStop=System.currentTimeMillis();
					}
				}
			}
			catch(InterruptedException ioe)
			{
				// a perfectly good and normal thing
			}
			if(tickers.size()==0)
			{
				if(CMLib.threads() instanceof ServiceEngine)
					((ServiceEngine)CMLib.threads()).delTickGroup(this);
				break;
			}
		}
		shutdown=true;
	}
}
