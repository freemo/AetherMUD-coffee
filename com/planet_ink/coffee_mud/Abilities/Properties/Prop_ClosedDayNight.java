package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Prop_ClosedDayNight extends Property
{
	public String ID() { return "Prop_ClosedDayNight"; }
	public String name(){ return "Day/Night Visibility";}
	protected int canAffectCode(){return Ability.CAN_ITEMS|Ability.CAN_MOBS|Ability.CAN_EXITS|Ability.CAN_ROOMS;}
	private boolean doneToday=false;
	private int lastClosed=-1;
	private boolean dayFlag=false;
	private boolean sleepFlag=false;
	private boolean sitFlag=false;
	private boolean lockupFlag=false;
	private int openTime=-1;
	private int closeTime=-1;
	private String Home=null;
	private String shopMsg=null;
	public Environmental newInstance()
	{	Prop_ClosedDayNight newOne=new Prop_ClosedDayNight(); newOne.setMiscText(text());return newOne; }

	public String accountForYourself()
	{ return "";	}

	public void setMiscText(String text)
	{
		super.setMiscText(text);
		Vector V=Util.parse(text);
		dayFlag=false;
		doneToday=false;
		lockupFlag=false;
		sleepFlag=false;
		sitFlag=false;
		openTime=-1;
		closeTime=-1;
		lastClosed=-1;
		Home=null;
		shopMsg=null;
		for(int v=0;v<V.size();v++)
		{
			String s=((String)V.elementAt(v)).toUpperCase();
			if(s.equals("DAY"))
				dayFlag=true;
			else
			if(s.equals("SLEEP"))
				sleepFlag=true;
			else
			if(s.equals("LOCKUP"))
				lockupFlag=true;
			else
			if(s.equals("SIT"))
				sitFlag=true;
			else
			if(s.startsWith("HOURS="))
			{
				s=s.substring(6);
				int x=s.indexOf("-");
				if(x>=0)
				{
					openTime=Util.s_int(s.substring(0,x));
					closeTime=Util.s_int(s.substring(x+1));
				}
			}
			else
			if(s.startsWith("HOME="))
				Home=((String)V.elementAt(v)).substring(5);
			else
			if(s.startsWith("SHOPMSG="))
				shopMsg=((String)V.elementAt(v)).substring(8);
		}
	}

	private boolean closed()
	{
		boolean closed=false;
		if((openTime<0)&&(closeTime<0))
		{
			closed=(CMMap.getFirstArea().getTODCode()==Area.TIME_NIGHT);
			if(dayFlag) closed=!closed;
		}
		else
		{
			if(openTime<closeTime)
				closed=(CMMap.getFirstArea().getTimeOfDay()<openTime)
					||(CMMap.getFirstArea().getTimeOfDay()>closeTime);
			else
				closed=(CMMap.getFirstArea().getTimeOfDay()>closeTime)
					&&(CMMap.getFirstArea().getTimeOfDay()<openTime);
		}
		return closed;
	}
	
	public boolean okAffect(Environmental E, Affect msg)
	{
		if(!super.okAffect(E,msg)) 
			return false;
		
		if((affected!=null)
		&&(affected instanceof MOB)
		&&(closed())
		&&(Home!=null)
		&&(!Sense.isSleeping(affected))
		&&((msg.targetMinor()==Affect.TYP_BUY)
		   ||(msg.targetMinor()==Affect.TYP_SELL)
		   ||(msg.targetMinor()==Affect.TYP_VALUE)
		   ||(msg.targetMinor()==Affect.TYP_DEPOSIT)
		   ||(msg.targetMinor()==Affect.TYP_WITHDRAW)
		   ||(msg.targetMinor()==Affect.TYP_VIEW)))
		{
			ShopKeeper sk=CoffeeUtensils.getShopKeeper((MOB)affected);
			if(sk!=null)
				ExternalPlay.quickSay((MOB)affected,msg.source(),(shopMsg!=null)?shopMsg:"Sorry, I'm off right now.  Try me tomorrow.",false,false);
			return false;
		}
		return true;
	}

	private Room getHomeRoom()
	{
		if(Home==null) return null;
		Room R=CMMap.getRoom(Home);
		if(R!=null) return R;
		if((affected!=null)&&(affected instanceof MOB))
		{
			MOB mob=(MOB)affected;
			if(mob.location()!=null)
				for(Enumeration e=mob.location().getArea().getMap();e.hasMoreElements();)
				{
					Room R2=(Room)e.nextElement();
					if((R2.roomID().indexOf(Home)>=0)
					||CoffeeUtensils.containsString(R2.name(),Home)
					||CoffeeUtensils.containsString(R2.displayText(),Home)
					||CoffeeUtensils.containsString(R2.description(),Home))
					{ R=R2; break;}
					if(R2.fetchInhabitant(Home)!=null)
					{ R=R2; break;}
				}
			if(R!=null) return R;
			for(Enumeration e=CMMap.rooms();e.hasMoreElements();)
			{
				Room R2=(Room)e.nextElement();
				if((R2.roomID().indexOf(Home)>=0)
				||CoffeeUtensils.containsString(R2.name(),Home)
				||CoffeeUtensils.containsString(R2.displayText(),Home)
				||CoffeeUtensils.containsString(R2.description(),Home))
				{ R=R2; break;}
				if(R2.fetchInhabitant(Home)!=null)
				{ R=R2; break;}
			}
		}
		return R;
	}
	
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if((affected!=null)
		&&(affected instanceof MOB)
		&&(!((MOB)affected).amDead())
		&&((lastClosed<0)||(closed()!=(lastClosed==1))))
		{
			MOB mob=(MOB)affected;
			if(closed())
			{
				ExternalPlay.standIfNecessary(mob);
				if(!Sense.aliveAwakeMobile(mob,true)||(mob.isInCombat()))
					return true;
				
				if((mob.location()==mob.getStartRoom())
				&&(lockupFlag))
					for(int d=0;d<Directions.NUM_DIRECTIONS;d++)
					{
						Exit E=mob.location().getExitInDir(d);
						Room R2=mob.location().getRoomInDir(d);
						if((E!=null)&&(R2!=null)&&(E.hasADoor())&&(E.hasALock()))
						{
							FullMsg msg=null;
							if(E.isOpen())
							{
								msg=new FullMsg(mob,E,null,Affect.MSG_OK_VISUAL,Affect.MSG_OK_VISUAL,Affect.MSG_OK_VISUAL,null);
								if(R2.okAffect(mob,msg))
								{
									msg=new FullMsg(mob,E,null,Affect.MSG_OK_VISUAL,Affect.MSG_CLOSE,Affect.MSG_OK_VISUAL,"<S-NAME> "+E.closeWord()+"(s) <T-NAMESELF>.");
									ExternalPlay.roomAffectFully(msg,mob.location(),d);
								}
							}
							if(!E.isLocked())
							{
								msg=new FullMsg(mob,E,null,Affect.MSG_OK_VISUAL,Affect.MSG_OK_VISUAL,Affect.MSG_OK_VISUAL,null);
								if(R2.okAffect(mob,msg))
								{
									msg=new FullMsg(mob,E,null,Affect.MSG_OK_VISUAL,Affect.MSG_LOCK,Affect.MSG_OK_VISUAL,"<S-NAME> lock(s) <T-NAMESELF>.");
									ExternalPlay.roomAffectFully(msg,mob.location(),d);
								}
							}
						}
					}
				
				if(Home!=null)
				{
					Room R=getHomeRoom();
					if((R!=null)&&(R!=mob.location()))
					{
						// still tracking...
						if(mob.fetchAffect("Skill_Track")!=null)
							return true;
						ShopKeeper sk=CoffeeUtensils.getShopKeeper((MOB)affected);
						if(sk!=null)
							ExternalPlay.quickSay((MOB)affected,null,(shopMsg!=null)?shopMsg:"Sorry, I'm off right now.  Try me tomorrow.",false,false);
						Ability A=CMClass.getAbility("Skill_Track");
						if(A!=null)	A.invoke(mob,R,true);
						return true;
					}
				}
				
				try{
					if(sleepFlag)
						ExternalPlay.doCommand(mob,Util.parse("SLEEP"));
					else
					if(sitFlag)
						ExternalPlay.doCommand(mob,Util.parse("SIT"));
				}
				catch(Exception e){}
				lastClosed=1;
			}
			else
			{
				ExternalPlay.standIfNecessary(mob);
				if(!Sense.aliveAwakeMobile(mob,true)||(mob.isInCombat()))
					return true;
				if(Home!=null)
				{
					if(mob.location()!=mob.getStartRoom())
					{
						// still tracking...
						if(mob.fetchAffect("Skill_Track")!=null)
							return true;
						Ability A=CMClass.getAbility("Skill_Track");
						if(A!=null)	A.invoke(mob,mob.getStartRoom(),true);
						return true;
					}
				}
				lastClosed=0;
				
				if((mob.location()==mob.getStartRoom())
				&&(lockupFlag))
					for(int d=0;d<Directions.NUM_DIRECTIONS;d++)
					{
						Exit E=mob.location().getExitInDir(d);
						Room R2=mob.location().getRoomInDir(d);
						if((E!=null)&&(R2!=null)&&(E.hasADoor())&&(E.hasALock()))
						{
							FullMsg msg=null;
							if((E.isLocked())&&(!E.defaultsLocked()))
							{
								msg=new FullMsg(mob,E,null,Affect.MSG_OK_VISUAL,Affect.MSG_OK_VISUAL,Affect.MSG_OK_VISUAL,null);
								if(R2.okAffect(mob,msg))
								{
									msg=new FullMsg(mob,E,null,Affect.MSG_OK_VISUAL,Affect.MSG_UNLOCK,Affect.MSG_OK_VISUAL,"<S-NAME> unlock(s) <T-NAMESELF>.");
									ExternalPlay.roomAffectFully(msg,mob.location(),d);
								}
							}
							if((!E.isOpen())&&(!E.defaultsClosed()))
							{
								msg=new FullMsg(mob,E,null,Affect.MSG_OK_VISUAL,Affect.MSG_OK_VISUAL,Affect.MSG_OK_VISUAL,null);
								if(R2.okAffect(mob,msg))
								{
									msg=new FullMsg(mob,E,null,Affect.MSG_OK_VISUAL,Affect.MSG_OPEN,Affect.MSG_OK_VISUAL,"<S-NAME> "+E.openWord()+"(s) <T-NAMESELF>.");
									ExternalPlay.roomAffectFully(msg,mob.location(),d);
								}
							}
						}
					}
			}
		}
		return true;
	}
	
	
	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		if(affected==null) return;
		if((affected instanceof MOB)
		||(affected instanceof Item))
		{
			if((closed())
			&&(Home==null)
			&&(!sleepFlag)
			&&(!sitFlag)
			&&((!(affected instanceof MOB))||(!((MOB)affected).isInCombat())))
			{
				affectableStats.setDisposition(affectableStats.disposition()|EnvStats.IS_NOT_SEEN);
				affectableStats.setSensesMask(affectableStats.sensesMask()|EnvStats.CAN_NOT_SEE);
				affectableStats.setSensesMask(affectableStats.sensesMask()|EnvStats.CAN_NOT_MOVE);
				affectableStats.setSensesMask(affectableStats.sensesMask()|EnvStats.CAN_NOT_SPEAK);
				affectableStats.setSensesMask(affectableStats.sensesMask()|EnvStats.CAN_NOT_HEAR);
			}
		}
		else
		if((affected instanceof Room)&&(closed()))
			affectableStats.setDisposition(affectableStats.disposition()|EnvStats.IS_DARK);
		else
		if(affected instanceof Exit)
		{
			if(closed())
			{
				if(!doneToday)
				{
					doneToday=true;
					Exit e=((Exit)affected);
					e.setDoorsNLocks(e.hasADoor(),false,e.defaultsClosed(),e.hasALock(),e.hasALock(),e.defaultsLocked());
				}
			}
			else
			{
				if(doneToday)
				{
					doneToday=false;
					Exit e=((Exit)affected);
					e.setDoorsNLocks(e.hasADoor(),!e.defaultsClosed(),e.defaultsClosed(),e.hasALock(),e.defaultsLocked(),e.defaultsLocked());
				}
			}
		}
		
	}
}
