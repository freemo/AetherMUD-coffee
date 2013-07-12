package com.planet_ink.coffee_mud.Libraries;
import java.util.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.CatalogLibrary.CataData;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.CMSecurity.DbgFlag;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.core.interfaces.*;

/* 
   Copyright 2000-2013 Bo Zimmerman

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
public class Sessions extends StdLibrary implements SessionsList
{
	public String ID(){return "Sessions";}
	
	private TickClient serviceClient=null;
	private volatile long nextSweepTime = System.currentTimeMillis(); 
	
	public final SLinkedList<Session> all=new SLinkedList<Session>();
	
	private final static Filterer<Session> localOnlineFilter=new Filterer<Session>(){
		public boolean passesFilter(Session obj) { 
			if((obj!=null) && (!obj.isStopped()) && (((obj.getStatus())==Session.SessionStatus.MAINLOOP)))
			{
				MOB M=obj.mob();
				return ((M!=null)&&M.amActive()&&(CMLib.flags().isInTheGame(M,true)));
			}
			return false;
		}
	};
	
	public TickClient getServiceClient() { return serviceClient;}
	
	public Iterator<Session> all(){return all.iterator();}
	public Iterable<Session> allIterable(){return all;}
	public Iterator<Session> localOnline(){
		return new FilteredIterator<Session>(all.iterator(),localOnlineFilter);
	}
	public Iterable<Session> localOnlineIterable(){
		return new FilteredIterable<Session>(all,localOnlineFilter);
	}
	
	public int getCountAll()
	{
		return getCount(all());
	}
	
	public int getCountLocalOnline()
	{
		return getCount(localOnline());
	}
	
	protected int getCount(Iterator<Session> i)
	{
		int xt=0;
		for(;i.hasNext();)
		{
			i.next();
			xt++;
		}
		return xt;
	}
	
	public Session getAllSessionAt(int index)
	{
		return getAllSessionAt(all(),index);
	}
	
	protected Session getAllSessionAt(Iterator<Session> i, int index)
	{
		int xt=0;
		Session S;
		for(;i.hasNext();)
		{
			S=i.next();
			if(xt==index)
				return S;
			xt++;
		}
		return null;
	}
	
	public synchronized void add(Session s)
	{
		if(!all.contains(s))
			all.add(s);
	}
	
	public synchronized void remove(Session s)
	{
		all.remove(s);
	}
	
	public void stopSessionAtAllCosts(Session S)
	{
		if(S==null) return;
		S.stopSession(true,true,false);
		if(all.contains(S))
		{
			S.run();
			if(all.contains(S))
			{
				S.stopSession(true,true,true);
				remove(S);
			}
		}
	}
	
	protected void sessionCheck()
	{
		setThreadStatus(serviceClient,"checking player sessions.");
		for(Session S : all)
		{
			long time=System.currentTimeMillis()-S.getInputLoopTime();
			if(time>0)
			{
				if((S.mob()!=null)||((S.getStatus())==Session.SessionStatus.ACCOUNT_MENU)||((S.getStatus())==Session.SessionStatus.CHARCREATE))
				{
					long check=60000;

					if((S.getPreviousCMD()!=null)
					&&(S.getPreviousCMD().size()>0)
					&&(S.getPreviousCMD().get(0).equalsIgnoreCase("IMPORT")
					   ||S.getPreviousCMD().get(0).equalsIgnoreCase("EXPORT")
					   ||S.getPreviousCMD().get(0).equalsIgnoreCase("CHARGEN")
					   ||S.getPreviousCMD().get(0).equalsIgnoreCase("MERGE")))
						check=check*600;
					else
					if((S.mob()!=null)&&(CMSecurity.isAllowed(S.mob(),S.mob().location(),CMSecurity.SecFlag.CMDROOMS)))
						check=check*15;
					else
					if((S.getStatus())==Session.SessionStatus.LOGIN)
						check=check*5;

					if(time>(check*10))
					{
						String roomID=S.mob()!=null?CMLib.map().getExtendedRoomID(S.mob().location()):"";
						if((S.getPreviousCMD()==null)||(S.getPreviousCMD().size()==0)
						||((S.getStatus())==Session.SessionStatus.LOGIN)
						||((S.getStatus())==Session.SessionStatus.ACCOUNT_MENU)
						||((S.getStatus())==Session.SessionStatus.CHARCREATE))
							Log.sysOut(serviceClient.getName(),"Kicking out: "+((S.mob()==null)?"Unknown":S.mob().Name())+" who has spent "+time+" millis out-game.");
						else
						{
							Log.errOut(serviceClient.getName(),"KILLING DEAD Session: "+((S.mob()==null)?"Unknown":S.mob().Name())+" ("+roomID+"), out for "+time);
							Log.errOut(serviceClient.getName(),"STATUS  was :"+S.getStatus()+", LASTCMD was :"+((S.getPreviousCMD()!=null)?S.getPreviousCMD().toString():""));
							if(S instanceof Thread)
								CMLib.threads().debugDumpStack("Sessions",(Thread)S);
						}
						setThreadStatus(serviceClient,"killing session ");
						stopSessionAtAllCosts(S);
						setThreadStatus(serviceClient,"checking player sessions.");
					}
					else
					if(time>check)
					{
						if((S.mob()==null)||(S.mob().Name()==null)||(S.mob().Name().length()==0))
							stopSessionAtAllCosts(S);
						else
						if((S.getPreviousCMD()!=null)&&(S.getPreviousCMD().size()>0))
						{
							String roomID=S.mob()!=null?CMLib.map().getExtendedRoomID(S.mob().location()):"";
							if((S.isLockedUpWriting())
							&&(CMLib.flags().isInTheGame(S.mob(),true)))
							{
								Log.errOut(serviceClient.getName(),"LOGGED OFF Session: "+((S.mob()==null)?"Unknown":S.mob().Name())+" ("+roomID+"), out for "+time+": "+S.isLockedUpWriting());
								stopSessionAtAllCosts(S);
							}
							else
								Log.errOut(serviceClient.getName(),"Suspect Session: "+((S.mob()==null)?"Unknown":S.mob().Name())+" ("+roomID+"), out for "+time);
							if(((S.getStatus())!=Session.SessionStatus.LOGIN)
							||((S.getPreviousCMD()!=null)&&(S.getPreviousCMD().size()>0)))
								Log.errOut(serviceClient.getName(),"STATUS  is :"+S.getStatus()+", LASTCMD was :"+((S.getPreviousCMD()!=null)?S.getPreviousCMD().toString():""));
							else
								Log.errOut(serviceClient.getName(),"STATUS  is :"+S.getStatus()+", no last command available.");
						}
					}
				}
				else
				if(time>(60000))
				{
					String roomID=S.mob()!=null?CMLib.map().getExtendedRoomID(S.mob().location()):"";
					if((S.getStatus())==Session.SessionStatus.LOGIN)
						Log.sysOut(serviceClient.getName(),"Kicking out login session after "+time+" millis.");
					else
					{
						Log.errOut(serviceClient.getName(),"KILLING DEAD Session: "+((S.mob()==null)?"Unknown":S.mob().Name())+" ("+roomID+"), out for "+time);
						if(S instanceof Thread)
							CMLib.threads().debugDumpStack("Sessions",(Thread)S);
					}
					if(((S.getStatus())!=Session.SessionStatus.LOGIN)
					||((S.getPreviousCMD()!=null)&&(S.getPreviousCMD().size()>0)))
						Log.errOut(serviceClient.getName(),"STATUS  was :"+S.getStatus()+", LASTCMD was :"+((S.getPreviousCMD()!=null)?S.getPreviousCMD().toString():""));
					setThreadStatus(serviceClient,"killing session ");
					stopSessionAtAllCosts(S);
					setThreadStatus(serviceClient,"checking player sessions");
				}
			}
		}
	}
	
	public boolean activate() 
	{
		nextSweepTime = System.currentTimeMillis()+MudHost.TIME_UTILTHREAD_SLEEP;
		if(serviceClient==null)
			serviceClient=CMLib.threads().startTickDown(new Tickable(){
				private volatile long tickStatus=Tickable.STATUS_NOT;
				@Override public String ID() { return "THSessions"+Thread.currentThread().getThreadGroup().getName().charAt(0); }
				@Override public CMObject newInstance() { return this; }
				@Override public CMObject copyOf() { return this; }
				@Override public void initializeClass() { }
				@Override public int compareTo(CMObject o) { return (o==this)?0:1; }
				@Override public String name() { return ID(); }
				@Override public long getTickStatus() { return tickStatus; }
				@Override public boolean tick(Tickable ticking, int tickID) {
					tickStatus=Tickable.STATUS_ALIVE;
					final double numThreads=all.size();
					if(numThreads>0.0)
					{
						for(final Session S : all)
						{
							if(!S.isRunning()) 
							{
								CMLib.threads().executeRunnable(S);
							}
						}
					}
					if(System.currentTimeMillis() >= nextSweepTime)
					{
						nextSweepTime = System.currentTimeMillis()+MudHost.TIME_UTILTHREAD_SLEEP;
						if((!CMSecurity.isDisabled(CMSecurity.DisFlag.UTILITHREAD))
						&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.SESSIONTHREAD)))
						{
							isDebugging=CMSecurity.isDebugging(DbgFlag.UTILITHREAD);
							sessionCheck();
						}
					}
					tickStatus=Tickable.STATUS_NOT;
					setThreadStatus(serviceClient,"sleeping");
					return true;
				}
			}, Tickable.TICKID_SUPPORT|Tickable.TICKID_SOLITARYMASK, 100, 1);
		return true;
	}
	
	public boolean shutdown() 
	{
		if((serviceClient!=null)&&(serviceClient.getClientObject()!=null))
		{
			CMLib.threads().deleteTick(serviceClient.getClientObject(), Tickable.TICKID_SUPPORT|Tickable.TICKID_SOLITARYMASK);
			serviceClient=null;
		}
		return true;
	}
	
	public MOB findPlayerOnline(String srchStr, boolean exactOnly)
	{
		Session S=findPlayerSessionOnline(srchStr, exactOnly);
		if(S==null) return null;
		return S.mob();
	}
	
	public Session findPlayerSessionOnline(String srchStr, boolean exactOnly)
	{
		// then look for players
		for(Session S : localOnlineIterable())
			if(S.mob().Name().equalsIgnoreCase(srchStr))
				return S;
		for(Session S : localOnlineIterable())
			if(S.mob().name().equalsIgnoreCase(srchStr))
				return S;
		// keep looking for players
		if(!exactOnly)
		{
			for(Session S : localOnlineIterable())
				if(CMLib.english().containsString(S.mob().Name(),srchStr))
					return S;
			for(Session S : localOnlineIterable())
				if(CMLib.english().containsString(S.mob().name(),srchStr))
					return S;
		}
		return null;
	}
}
