package com.planet_ink.coffee_mud.Commands.base.sysop;

import java.io.*;
import java.util.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;

public class SysOpSkills
{
	private Room findRoom(String roomID)
	{
		for(int m=0;m<CMMap.map.size();m++)
		{
			Room thisRoom=(Room)CMMap.map.elementAt(m);
			if(thisRoom.ID().equalsIgnoreCase(roomID))
			   return thisRoom;
		}
		return null;
	}
	private MOB levelMOBup(int level, CharClass C)
	{
		MOB mob=(MOB)CMClass.getMOB("StdMOB").newInstance();
		mob.setAlignment(500);
		mob.setName("Average Joe");
		mob.baseCharStats().setMyRace(CMClass.getRace("Human"));
		mob.baseCharStats().setGender('M');
		mob.baseCharStats().setStrength(1+(int)Math.round(CharStats.AVG_VALUE));
		mob.baseCharStats().setWisdom(1+(int)Math.round(CharStats.AVG_VALUE));
		mob.baseCharStats().setIntelligence((int)Math.round(CharStats.AVG_VALUE));
		mob.baseCharStats().setDexterity(1+(int)Math.round(CharStats.AVG_VALUE));
		mob.baseCharStats().setConstitution((int)Math.round(CharStats.AVG_VALUE));
		mob.baseCharStats().setCharisma((int)Math.round(CharStats.AVG_VALUE));
		mob.baseCharStats().setMyClass(C);
		mob.baseEnvStats().setArmor(50);
		mob.baseEnvStats().setLevel(1);
		mob.baseEnvStats().setSensesMask(0);
		mob.baseState().setHitPoints(20);
		mob.baseState().setMovement(100);
		mob.baseState().setMana(100);
		mob.baseCharStats().getMyRace().newCharacter(mob);
		mob.recoverCharStats();
		mob.recoverEnvStats();
		mob.recoverMaxState();
		mob.resetToMaxState();
		mob.baseCharStats().getMyClass().newCharacter(mob,false);

		for(int lvl=1;lvl<level;lvl++)
		{
			switch(lvl % 6)
			{
			case 0:
				mob.baseCharStats().setStrength(mob.baseCharStats().getStrength()+1);
				break;
			case 1:
				mob.baseCharStats().setDexterity(mob.baseCharStats().getDexterity()+1);
				break;
			case 2:
				mob.baseCharStats().setIntelligence(mob.baseCharStats().getIntelligence()+1);
				break;
			case 3:
				mob.baseCharStats().setConstitution(mob.baseCharStats().getConstitution()+1);
				break;
			case 4:
				mob.baseCharStats().setCharisma(mob.baseCharStats().getCharisma()+1);
				break;
			case 5:
				mob.baseCharStats().setWisdom(mob.baseCharStats().getWisdom()+1);
				break;
			}
			int oldattack=mob.baseEnvStats().attackAdjustment();
			mob.charStats().getMyClass().gainExperience(mob,null,mob.getExpNeededLevel()+1);
			mob.recoverEnvStats();
			mob.recoverCharStats();
			mob.recoverMaxState();
			int newAttack=mob.baseEnvStats().attackAdjustment()-oldattack;
			mob.baseEnvStats().setArmor(mob.baseEnvStats().armor()-newAttack);
			mob.recoverEnvStats();
			mob.recoverCharStats();
			mob.recoverMaxState();
		}
		return mob;
	}

	public void averageout(MOB avgMob, int tries)
	{
		avgMob.baseCharStats().setStrength((int)Math.round(Util.div(avgMob.baseCharStats().getStrength(),tries)));
		avgMob.baseCharStats().setWisdom((int)Math.round(Util.div(avgMob.baseCharStats().getWisdom(),tries)));
		avgMob.baseCharStats().setIntelligence((int)Math.round(Util.div(avgMob.baseCharStats().getIntelligence(),tries)));
		avgMob.baseCharStats().setDexterity((int)Math.round(Util.div(avgMob.baseCharStats().getDexterity(),tries)));
		avgMob.baseCharStats().setConstitution((int)Math.round(Util.div(avgMob.baseCharStats().getConstitution(),tries)));
		avgMob.baseCharStats().setCharisma((int)Math.round(Util.div(avgMob.baseCharStats().getCharisma(),tries)));
		avgMob.baseEnvStats().setArmor((int)Math.round(Util.div(avgMob.baseEnvStats().armor(),tries)));
		avgMob.baseState().setHitPoints((int)Math.round(Util.div(avgMob.baseState().getHitPoints(),tries)));
		avgMob.baseState().setMovement((int)Math.round(Util.div(avgMob.baseState().getMovement(),tries)));
		avgMob.baseState().setMana((int)Math.round(Util.div(avgMob.baseState().getMana(),tries)));
		avgMob.recoverCharStats();
		avgMob.recoverEnvStats();
		avgMob.recoverMaxState();
		avgMob.resetToMaxState();
		avgMob.setTrains(0);
	}

	public void addHimIn(MOB avgMob, MOB mob2)
	{
		avgMob.baseCharStats().setStrength(avgMob.baseCharStats().getStrength()+mob2.baseCharStats().getStrength());
		avgMob.baseCharStats().setWisdom(avgMob.baseCharStats().getWisdom()+mob2.baseCharStats().getWisdom());
		avgMob.baseCharStats().setIntelligence(avgMob.baseCharStats().getIntelligence()+mob2.baseCharStats().getIntelligence());
		avgMob.baseCharStats().setDexterity(avgMob.baseCharStats().getDexterity()+mob2.baseCharStats().getDexterity());
		avgMob.baseCharStats().setConstitution(avgMob.baseCharStats().getConstitution()+mob2.baseCharStats().getConstitution());
		avgMob.baseCharStats().setCharisma(avgMob.baseCharStats().getCharisma()+mob2.baseCharStats().getCharisma());
		avgMob.baseEnvStats().setArmor(avgMob.baseEnvStats().armor()+mob2.baseEnvStats().armor());
		avgMob.baseState().setHitPoints(avgMob.baseState().getHitPoints()+mob2.baseState().getHitPoints());
		avgMob.baseState().setMovement(avgMob.baseState().getMovement()+mob2.baseState().getMovement());
		avgMob.baseState().setMana(avgMob.baseState().getMana()+mob2.baseState().getMana());
		avgMob.recoverCharStats();
		avgMob.recoverEnvStats();
		avgMob.recoverMaxState();
		avgMob.resetToMaxState();
	}

	public MOB AverageClassMOB(MOB mob, int level, CharClass C, int numTries)
	{
		MOB avgMob=(MOB)levelMOBup(level,C);
		int tries=0;
		for(;tries<numTries;tries++)
		{
			if((tries % 20)==0)
				mob.session().print(".");
			MOB mob2=(MOB)levelMOBup(level,C);
			addHimIn(avgMob,mob2);
		}
		averageout(avgMob,tries);
		return avgMob;
	}

	public MOB AverageAllClassMOB(MOB mob, int level, int numTriesClass, int numTriesMOB)
	{
		MOB avgMob=null;
		int tries=0;
		int numClasses=0;
		for(;tries<numTriesClass;tries++)
		{
			for(int c=0;c<CMClass.charClasses.size();c++)
			{
				CharClass C=(CharClass)CMClass.charClasses.elementAt(c);
				if(C.playerSelectable())
				{
					numClasses++;
					MOB mob2=AverageClassMOB(mob,level,C,numTriesMOB);
					if(avgMob==null)
					{
						avgMob=mob2;
						numClasses--;
					}
					else
						addHimIn(avgMob,mob2);
				}
			}
		}
		averageout(avgMob,numClasses);
		return avgMob;
	}

	public boolean chargen(MOB mob, Vector commands)
	{
		if(mob.isMonster())
			return false;
		commands.removeElementAt(0);
		CharClass C=null;
		int level=-1;
		String ClassName="";
		if(commands.size()>0)
		{
			ClassName=(String)commands.elementAt(0);
			C=CMClass.getCharClass(ClassName);
			level=Util.s_int(Util.combine(commands,1));
		}

		if((C==null)&&(ClassName.toUpperCase().indexOf("ALL")<0))
		{
			mob.tell("Enter 'ALL' for all classes.");
			try
			{
				ClassName=mob.session().prompt("Enter a class name: ");
			}
			catch(Exception e){return false;}

			C=CMClass.getCharClass(ClassName);
			if((C==null)&&(ClassName.toUpperCase().indexOf("ALL")<0))
				return false;
		}

		if(level<=0)
		{
			try
			{
				level=Util.s_int(mob.session().prompt("Enter a level (1-25): "));
			}
			catch(Exception e){return false;}
			if(level<=0)
				return false;
		}

		if(C!=null)
			mob.session().print("\n\rAverage "+C.name()+"...");
		else
			mob.session().print("\n\rAverage MOB stats, across all classes...");

		MOB avgMob=null;
		if(C!=null)
			avgMob=AverageClassMOB(mob, level,C, 300);
		else
			avgMob=AverageAllClassMOB(mob,level, 20, 50);

		mob.session().println("\n\r");

		if(avgMob!=null)
		{
			avgMob.setSession(mob.session());
			ExternalPlay.score(avgMob);
			avgMob.setSession(null);
		}
		return true;
	}

	public boolean gotoCmd(MOB mob, Vector commands)
	{

		Room room=null;
		if(commands.size()<2)
		{
			mob.tell("Go where?  You need the Room ID or a player name!");
			return false;
		}
		commands.removeElementAt(0);
		Room curRoom=mob.location();
		StringBuffer cmd = new StringBuffer(Util.combine(commands,0));

		room = findRoom(cmd.toString());
		if(room==null)
		{
			if((cmd.charAt(0)=='#')&&(curRoom!=null))
			{
				cmd.insert(0,curRoom.getArea().name());
				room = findRoom(cmd.toString());
			}
			else
			{
				for(int s=0;s<Sessions.size();s++)
				{
					Session thisSession=(Session)Sessions.elementAt(s);
					if((thisSession.mob()!=null) && (!thisSession.killFlag())
					&&(thisSession.mob().location()!=null)
					&&(thisSession.mob().name().equalsIgnoreCase(cmd.toString())))
					{
						room = thisSession.mob().location();
						break;
					}
				}
				if(room==null)
					for(int s=0;s<Sessions.size();s++)
					{
						Session thisSession=(Session)Sessions.elementAt(s);
						if((thisSession.mob()!=null)&&(!thisSession.killFlag())
						&&(thisSession.mob().location()!=null)
						&&(CoffeeUtensils.containsString(thisSession.mob().name(),cmd.toString())))
						{
							room = thisSession.mob().location();
							break;
						}
					}
			}
		}
		if(room==null)
		{
			mob.tell("Go where?  You need the Room ID or a player name!");
			return false;
		}
		if(!mob.isASysOp(room))
		{
			mob.tell("You aren't powerful enough to go there.");
		}
		if(curRoom==room)
		{
			mob.tell("Done.");
			return true;
		}
		else
		{
			room.bringMobHere(mob,true);
			mob.tell("Done.");
			return true;
		}
	}
	public MOB getTarget(MOB mob, Vector commands, boolean quiet)
	{
		String targetName=Util.combine(commands,0);
		MOB target=null;
		if(targetName.length()>0)
		{
			target=mob.location().fetchInhabitant(targetName);
			if(target==null)
			{
				Environmental t=mob.location().fetchFromRoomFavorItems(null,targetName);
				if((t!=null)&&(!(t instanceof MOB)))
				{
					if(!quiet)
						mob.tell("You can't do that to '"+targetName+"'.");
					return null;
				}
			}
		}

		if(target!=null)
			targetName=target.name();

		if((target==null)||((!Sense.canBeSeenBy(target,mob))&&((!Sense.canBeHeardBy(target,mob))||(!target.isInCombat()))))
		{
			if(!quiet)
			{
				if(targetName.trim().length()==0)
					mob.tell("You don't see them here.");
				else
					mob.tell("You don't see '"+targetName+"' here.");
			}
			return null;
		}

		return target;
	}

	public boolean possess(MOB mob, Vector commands)
	{
		String MOBname=Util.combine(commands,1);

		MOB target=getTarget(mob,commands,true);
		if((target==null)||((target!=null)&&(!target.isMonster())))
		{
			Vector V=mob.isASysOp(null)?CMMap.map:mob.location().getArea().getMyMap();
			for(int m=0;m<V.size();m++)
			{
				Room room=(Room)V.elementAt(m);
				MOB mob2=room.fetchInhabitant(MOBname);
				if((mob2!=null)&&(mob2.isMonster()))
				{
					target=mob2;
					break;
				}
			}
		}
		if((target==null)||(!target.isMonster()))
		{
			mob.tell("You can't possess '"+MOBname+"' right now.");
			return false;
		}

		mob.location().showOthers(mob,null,Affect.MSG_OK_VISUAL,"<S-NAME> get(s) a far away look, then seem(s) to fall limp.");

		Session s=mob.session();
		s.setMob(target);
		target.setSession(s);
		target.setSoulMate(mob);
		mob.setSession(null);
		ExternalPlay.look(target,null,true);
		target.tell("^HYour spirit has changed bodies...");
		return true;
	}

	public void dispossess(MOB mob)
	{
		if(mob.soulMate()==null)
		{
			mob.tell("Huh?");
			return;
		}
		Session s=mob.session();
		s.setMob(mob.soulMate());
		mob.soulMate().setSession(s);
		mob.setSession(null);
		mob.soulMate().tell("^HYour spirit has returned to your body...\n\r\n\r^N");
		ExternalPlay.look(mob.soulMate(),null,true);
		mob.setSoulMate(null);
	}

	public boolean wizinv(MOB mob, Vector commands)
	{
		commands.removeElementAt(0);
		String str="Prop_WizInvis";
		Ability A=mob.fetchAffect(str);
		if((A!=null)&&(Util.combine(commands,0).trim().equalsIgnoreCase("OFF")))
		{
		   A.unInvoke();
		   return true;
		}
		else
		if(A!=null)
		{
			mob.tell("You have already faded from view!");
			return false;
		}

		// it worked, so build a copy of this ability,
		// and add it to the affects list of the
		// affected MOB.  Then tell everyone else
		// what happened.
		A=CMClass.getAbility(str);
		if(A!=null)
		{
			mob.location().show(mob,null,Affect.MSG_OK_VISUAL,"<S-NAME> fade(s) from view!");
			mob.addAffect((Ability)A.copyOf());
			mob.recoverEnvStats();
			mob.location().recoverRoomStats();
			mob.tell("You may uninvoke WIZINV with 'WIZINV OFF'.");
			return true;
		}
		else
		{
			mob.tell("Wizard invisibility is not available!");
			return false;
		}
	}
}
