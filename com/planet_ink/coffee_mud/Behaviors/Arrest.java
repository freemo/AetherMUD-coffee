package com.planet_ink.coffee_mud.Behaviors;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;

import java.io.*;
import java.util.*;

public class Arrest extends StdBehavior
{
	public String ID(){return "Arrest";}
	protected int canImproveCode(){return Behavior.CAN_AREAS;}

	public Behavior newInstance()
	{
		return new Arrest();
	}
	private Vector oldWarrants=new Vector();
	private Vector warrants=new Vector();
	private Vector otherCrimes=new Vector();
	private Vector otherBits=new Vector();
	private Vector officerNames=new Vector();
	private Vector chitChat=new Vector();
	private Vector chitChat2=new Vector();

	private static final long ONE_REAL_DAY=1000*60*60*24;
	private static final long EXPIRATION_MILLIS=ONE_REAL_DAY*7; // 7 real days
	
	private static final int ACTION_WARN=0;
	private static final int ACTION_THREATEN=1;
	private static final int ACTION_PAROLE1=2;
	private static final int ACTION_PAROLE2=3;
	private static final int ACTION_PAROLE3=4;
	private static final int ACTION_PAROLE4=5;
	private static final int ACTION_JAIL1=6;
	private static final int ACTION_JAIL2=7;
	private static final int ACTION_JAIL3=8;
	private static final int ACTION_JAIL4=9;
	private static final int ACTION_EXECUTE=10;
	private static final int ACTION_HIGHEST=10;

	private static final int STATE_SEEKING=0;
	private static final int STATE_ARRESTING=1;
	private static final int STATE_SUBDUEING=2;
	private static final int STATE_MOVING=3;
	private static final int STATE_REPORTING=4;
	private static final int STATE_WAITING=5;
	private static final int STATE_PAROLING=6;
	private static final int STATE_JAILING=7;
	private static final int STATE_EXECUTING=8;
	private static final int STATE_MOVING2=9;
	private static final int STATE_RELEASE=10;

	private static final int BIT_CRIMELOCS=0;
	private static final int BIT_CRIMEFLAGS=1;
	private static final int BIT_CRIMENAME=2;
	private static final int BIT_SENTENCE=3;
	private static final int BIT_WARNMSG=4;

	private class ArrestWarrant implements Cloneable
	{
		public MOB criminal=null;
		public MOB victim=null;
		public MOB witness=null;
		public MOB arrestingOfficer=null;
		public Room jail=null;
		public Room releaseRoom=null;
		public String crime="";
		public int actionCode=-1;
		public int jailTime=0;
		public int state=-1;
		public int offenses=0;
		public long lastOffense=0;
		public String warnMsg=null;
		public void setArrestingOfficer(MOB mob)
		{
			if((mob==null)&&(arrestingOfficer!=null))
			{
				Ability A=arrestingOfficer.fetchAffect("Skill_Track");
				if(A!=null) A.unInvoke();
			}
			arrestingOfficer=mob;
		}
	}

	public boolean modifyBehavior(MOB mob, Object O)
	{
		if(mob!=null)
		{
			if((O!=null)&&(O instanceof MOB))
			{
				MOB framed=(MOB)O;
				ArrestWarrant W=null;
				for(int i=0;(W=getWarrant(mob,i))!=null;i++)
					if(W.criminal==mob)
						W.criminal=framed;
				return true;
			}
			else
			if((O!=null)&&(O instanceof Vector))
			{
				Vector V=(Vector)O;
				ArrestWarrant W=getWarrant(mob,0);
				if((V.size()>0)
				&&((V.firstElement() instanceof MOB))
				&&(W!=null))
				{
					MOB officer=(MOB)V.firstElement();
					if(W.arrestingOfficer==null)
					{
						W.setArrestingOfficer(officer);
						ExternalPlay.quickSay(W.arrestingOfficer,W.criminal,"You are under arrest "+restOfCharges(W.criminal)+"! Sit down on the ground immediately!",false,false);
						W.state=STATE_ARRESTING;
						return true;
					}
					else
						return false;
				}
				else
				for(int i=0;i<warrants.size();i++)
				{
					W=(ArrestWarrant)warrants.elementAt(i);
					if(isStillACrime(W))
					{
						Vector V2=new Vector();
						V2.addElement(W.criminal.name());
						if(W.victim==null) V2.addElement("");
						else V2.addElement(W.victim.name());
						if(W.witness==null) V2.addElement("");
						else V2.addElement(W.witness.name());
						V2.addElement(fixCharge(W));
						V.addElement(V2);
					}
				}
			}
			else
			if(O==null)
			{
				if((mob.isMonster())
				&&(mob.location()!=null)
				&&(isElligibleOfficer(mob,mob.location().getArea())))
					return true;
				return (getWarrant(mob,0))!=null;
			}
		}
		return super.modifyBehavior(mob,O);
	}

	private Properties getLaws()
	{
		String lawName=getParms();
		if(lawName.length()==0)	lawName="laws.ini";
		Properties laws=(Properties)Resources.getResource("LEGAL-"+lawName);
		if(laws==null)
		{
			laws=new Properties();
			try{laws.load(new FileInputStream("resources"+File.separatorChar+lawName));}
			catch(IOException e)
			{
				Log.errOut("Arrest","Unable to load: "+lawName+", legal system inoperable.");
				return laws;
			}
			Resources.submitResource("LEGAL-"+lawName,laws);
			String officers=(String)laws.get("OFFICERS");
			if((officers!=null)&&(officers.length()>0))
				officerNames=Util.parse(officers);
			if((officers!=null)&&(officers.length()>0))
				officerNames=Util.parse(officers);
			String chat=(String)laws.get("CHITCHAT");
			if((chat!=null)&&(chat.length()>0))
				chitChat=Util.parse(chat);
			String chat2=(String)laws.get("CHITCHAT2");
			if((chat2!=null)&&(chat2.length()>0))
				chitChat2=Util.parse(chat2);

			for(Enumeration e=laws.keys();e.hasMoreElements();)
			{
				String key=(String)e.nextElement();
				String words=(String)laws.get(key);
				if(key.startsWith("CRIME"))
				{
					int x=words.indexOf(";");
					if(x>0)
					{
						otherCrimes.addElement(Util.parse(words.substring(0,x)));
						otherBits.addElement(words.substring(x+1));
					}
					laws.remove(key);
				}
			}
		}
		return laws;
	}

	public String getBit(String words, int which)
	{
		int x=words.indexOf(";");
		int one=0;
		while(x>=0)
		{
			if(one==which)
				return words.substring(0,x);
			one++;
			words=words.substring(x+1);
			x=words.indexOf(";");
		}
		if(which==one)
			return words;
		return "";
	}

	public ArrestWarrant getWarrant(MOB mob, int which)
	{
		int one=0;
		for(int i=0;i<warrants.size();i++)
		{
			ArrestWarrant W=(ArrestWarrant)warrants.elementAt(i);
			if(W.criminal==mob)
			{
				if(which==one)
					return W;
				one++;
			}
		}
		return null;
	}

	public void unCuff(MOB mob)
	{
		Ability A=mob.fetchAffect("Skill_HandCuff");
		if(A!=null) A.unInvoke();
	}


	public void setFree(MOB mob)
	{
		fileAllWarrants(mob);
		unCuff(mob);
	}
	public void dismissOfficer(MOB officer)
	{
		if(officer==null) return;
		if((officer.getStartRoom()!=null)
		&&(officer.location()!=null)
		&&(officer.getStartRoom()==officer.location()))
			return;
		if(officer.isMonster())
			CoffeeUtensils.wanderAway(officer,true,true);
	}

	public MOB getAWitnessHere(Room R)
	{
		if(R!=null)
		for(int i=0;i<R.numInhabitants();i++)
		{
			MOB M=R.fetchInhabitant(i);
			if(M.isMonster()
			&&(M.charStats().getStat(CharStats.INTELLIGENCE)>3)
			&&(Dice.rollPercentage()<=(M.getAlignment()/10)))
				return M;
		}
		return null;
	}

	public MOB getWitness(Area A, MOB mob)
	{
		Room R=mob.location();

		if((A!=null)&&(R.getArea()!=A))
			return null;
		MOB M=getAWitnessHere(R);
		if(M!=null) return M;

		if(R!=null)
		for(int i=0;i<Directions.NUM_DIRECTIONS;i++)
		{
			Room R2=R.getRoomInDir(i);
			M=getAWitnessHere(R2);
			if(M!=null) return M;
		}
		return null;
	}

	public boolean isStillACrime(ArrestWarrant W)
	{
		// will witness talk, or victim press charges?
		Hashtable H=W.criminal.getGroupMembers(new Hashtable());
		if(W.witness.amDead()) return false;
		if(W.arrestingOfficer!=null)
		{
			if(W.witness==W.arrestingOfficer)
				return true;
			if((W.victim!=null)&&(W.victim==W.arrestingOfficer))
				return true;
		}

		if(H.containsKey(W.witness)) return false;
		if((W.victim!=null)&&(H.containsKey(W.victim))) return false;
		// crimes expire after three real days
		if((W.lastOffense>0)&&((System.currentTimeMillis()-W.lastOffense)>EXPIRATION_MILLIS))
			return false;
		return true;
	}

	public int highestCrimeAction(MOB criminal)
	{
		int num=0;
		int highest=-1;
		for(int w2=0;w2<warrants.size();w2++)
		{
			ArrestWarrant W2=(ArrestWarrant)warrants.elementAt(w2);
			if(W2.criminal==criminal)
			{
				num++;
				if((W2.actionCode+W2.offenses)>highest)
					highest=(W2.actionCode+W2.offenses);
			}
		}
		highest+=num;
		highest--;
		if(highest>ACTION_HIGHEST) highest=ACTION_HIGHEST;
		int adjusted=highest;
		if((criminal.getAlignment()>650)&&(adjusted>0))
			adjusted--;
		return adjusted;
	}

	public boolean judgeMe(MOB judge, MOB officer, MOB criminal, ArrestWarrant W)
	{
		Properties laws=getLaws();
		switch(highestCrimeAction(criminal))
		{
		case ACTION_WARN:
			{
			if((judge==null)&&(officer!=null)) judge=officer;
			StringBuffer str=new StringBuffer("");
			str.append(criminal.name()+", you are in trouble for "+restOfCharges(criminal)+".  ");
			for(int w2=0;w2<warrants.size();w2++)
			{
				ArrestWarrant W2=(ArrestWarrant)warrants.elementAt(w2);
				if(W2.criminal==criminal)
				{
					str.append("The charge of "+fixCharge(W2)+" was witnessed by "+W2.witness.name()+".  ");
					if((W2.warnMsg!=null)&&(W2.warnMsg.length()>0))
						str.append(W2.warnMsg+"  ");
					if((W2.offenses>0)&&(laws.get("PREVOFFMSG")!=null)&&(((String)laws.get("PREVOFFMSG")).length()>0))
						str.append(((String)laws.get("PREVOFFMSG"))+"  ");
				}
			}
			if((laws.get("WARNINGMSG")!=null)&&(((String)laws.get("WARNINGMSG")).length()>0))
				str.append(((String)laws.get("WARNINGMSG"))+"  ");
			ExternalPlay.quickSay(judge,criminal,str.toString(),false,false);
			}
			return true;
		case ACTION_THREATEN:
			{
			if((judge==null)&&(officer!=null)) judge=officer;
			StringBuffer str=new StringBuffer("");
			str.append(criminal.name()+", you are in trouble for "+restOfCharges(criminal)+".  ");
			for(int w2=0;w2<warrants.size();w2++)
			{
				ArrestWarrant W2=(ArrestWarrant)warrants.elementAt(w2);
				if(W2.criminal==criminal)
				{
					str.append("The charge of "+fixCharge(W2)+" was witnessed by "+W2.witness.name()+".  ");
					if((W2.warnMsg!=null)&&(W2.warnMsg.length()>0))
						str.append(W2.warnMsg+"  ");
					if((W2.offenses>0)&&(laws.get("PREVOFFMSG")!=null)&&(((String)laws.get("PREVOFFMSG")).length()>0))
						str.append(((String)laws.get("PREVOFFMSG"))+"  ");
				}
			}
			if((laws.get("THREATMSG")!=null)&&(((String)laws.get("THREATMSG")).length()>0))
				str.append(((String)laws.get("THREATMSG"))+"  ");
			ExternalPlay.quickSay(judge,criminal,str.toString(),false,false);
			}
			return true;
		case ACTION_PAROLE1:
			if(judge!=null)
			{
				if((W.offenses>0)&&(laws.get("PREVOFFMSG")!=null)&&(((String)laws.get("PREVOFFMSG")).length()>0))
					ExternalPlay.quickSay(judge,W.criminal,(String)laws.get("PREVOFFMSG"),false,false);
				if((laws.get("PAROLE1MSG")!=null)&&(((String)laws.get("PAROLE1MSG")).length()>0))
					ExternalPlay.quickSay(judge,criminal,(String)laws.get("PAROLE1MSG"),false,false);
				W.jailTime=Util.s_int((String)laws.get("PAROLE1TIME"));
				W.state=STATE_PAROLING;
			}
			return false;
		case ACTION_PAROLE2:
			if(judge!=null)
			{
				if((W.offenses>0)&&(laws.get("PREVOFFMSG")!=null)&&(((String)laws.get("PREVOFFMSG")).length()>0))
					ExternalPlay.quickSay(judge,W.criminal,(String)laws.get("PREVOFFMSG"),false,false);
				if((laws.get("PAROLE2MSG")!=null)&&(((String)laws.get("PAROLE2MSG")).length()>0))
					ExternalPlay.quickSay(judge,criminal,(String)laws.get("PAROLE2MSG"),false,false);
				W.jailTime=Util.s_int((String)laws.get("PAROLE2TIME"));
				W.state=STATE_PAROLING;
			}
			return false;
		case ACTION_PAROLE3:
			if(judge!=null)
			{
				if((W.offenses>0)&&(laws.get("PREVOFFMSG")!=null)&&(((String)laws.get("PREVOFFMSG")).length()>0))
					ExternalPlay.quickSay(judge,W.criminal,(String)laws.get("PREVOFFMSG"),false,false);
				if((laws.get("PAROLE3MSG")!=null)&&(((String)laws.get("PAROLE3MSG")).length()>0))
					ExternalPlay.quickSay(judge,criminal,(String)laws.get("PAROLE3MSG"),false,false);
				W.jailTime=Util.s_int((String)laws.get("PAROLE3TIME"));
				W.state=STATE_PAROLING;
			}
			return false;
		case ACTION_PAROLE4:
			if(judge!=null)
			{
				if((W.offenses>0)&&(laws.get("PREVOFFMSG")!=null)&&(((String)laws.get("PREVOFFMSG")).length()>0))
					ExternalPlay.quickSay(judge,W.criminal,(String)laws.get("PREVOFFMSG"),false,false);
				if((laws.get("PAROLE4MSG")!=null)&&(((String)laws.get("PAROLE4MSG")).length()>0))
					ExternalPlay.quickSay(judge,criminal,(String)laws.get("PAROLE4MSG"),false,false);
				W.jailTime=Util.s_int((String)laws.get("PAROLE4TIME"));
				W.state=STATE_PAROLING;
			}
			return false;
		case ACTION_JAIL1:
			if(judge!=null)
			{
				if((W.offenses>0)&&(laws.get("PREVOFFMSG")!=null)&&(((String)laws.get("PREVOFFMSG")).length()>0))
					ExternalPlay.quickSay(judge,W.criminal,(String)laws.get("PREVOFFMSG"),false,false);
				if((laws.get("JAIL1MSG")!=null)&&(((String)laws.get("JAIL1MSG")).length()>0))
					ExternalPlay.quickSay(judge,criminal,(String)laws.get("JAIL1MSG"),false,false);
				W.jailTime=Util.s_int((String)laws.get("JAIL1TIME"));
				W.state=STATE_JAILING;
			}
			return false;
		case ACTION_JAIL2:
			if(judge!=null)
			{
				if((W.offenses>0)&&(laws.get("PREVOFFMSG")!=null)&&(((String)laws.get("PREVOFFMSG")).length()>0))
					ExternalPlay.quickSay(judge,criminal,(String)laws.get("PREVOFFMSG"),false,false);
				if((laws.get("JAIL2MSG")!=null)&&(((String)laws.get("JAIL2MSG")).length()>0))
					ExternalPlay.quickSay(judge,criminal,(String)laws.get("JAIL2MSG"),false,false);
				W.jailTime=Util.s_int((String)laws.get("JAIL2TIME"));
				W.state=STATE_JAILING;
			}
			return false;
		case ACTION_JAIL3:
			if(judge!=null)
			{
				if((W.offenses>0)&&(laws.get("PREVOFFMSG")!=null)&&(((String)laws.get("PREVOFFMSG")).length()>0))
					ExternalPlay.quickSay(judge,criminal,(String)laws.get("PREVOFFMSG"),false,false);
				if((laws.get("JAIL3MSG")!=null)&&(((String)laws.get("JAIL3MSG")).length()>0))
					ExternalPlay.quickSay(judge,criminal,(String)laws.get("JAIL3MSG"),false,false);
				W.jailTime=Util.s_int((String)laws.get("JAIL3TIME"));
				W.state=STATE_JAILING;
			}
			return false;
		case ACTION_JAIL4:
			if(judge!=null)
			{
				if((W.offenses>0)&&(laws.get("PREVOFFMSG")!=null)&&(((String)laws.get("PREVOFFMSG")).length()>0))
					ExternalPlay.quickSay(judge,criminal,(String)laws.get("PREVOFFMSG"),false,false);
				if((laws.get("JAIL4MSG")!=null)&&(((String)laws.get("JAIL4MSG")).length()>0))
					ExternalPlay.quickSay(judge,criminal,(String)laws.get("JAIL4MSG"),false,false);
				W.jailTime=Util.s_int((String)laws.get("JAIL4TIME"));
				W.state=STATE_JAILING;
			}
			return false;
		case ACTION_EXECUTE:
			if(judge!=null)
			{
				if((W.offenses>0)&&(laws.get("PREVOFFMSG")!=null)&&(((String)laws.get("PREVOFFMSG")).length()>0))
					ExternalPlay.quickSay(judge,criminal,(String)laws.get("PREVOFFMSG"),false,false);
				if((laws.get("EXECUTEMSG")!=null)&&(((String)laws.get("EXECUTEMSG")).length()>0))
					ExternalPlay.quickSay(judge,criminal,(String)laws.get("EXECUTEMSG"),false,false);
				W.state=STATE_EXECUTING;
			}
			return false;
		}
		return true;
	}

	public boolean isTroubleMaker(MOB M)
	{
		if(M==null) return false;
		for(int b=0;b<M.numBehaviors();b++)
		{
			Behavior B=M.fetchBehavior(b);
			if((B!=null)&&(Util.bset(flags(),Behavior.FLAG_TROUBLEMAKING)))
				return true;
		}
		return false;
	}
	
	public Room getRoom(Area A, String roomstr)
	{
		Room jail=null;
		Vector V=Util.parseSemicolons(roomstr);
		if(V.size()==0) return jail;
		String which=(String)V.elementAt(Dice.roll(1,V.size(),-1));
		jail=CMMap.getRoom(which);
		if(jail==null)
		for(Enumeration r=A.getMap();r.hasMoreElements();)
		{
			Room R=(Room)r.nextElement();
			if(CoffeeUtensils.containsString(R.displayText(),which))
			{ jail=R; break; }
		}
		if(jail==null)
		for(Enumeration r=A.getMap();r.hasMoreElements();)
		{
			Room R=(Room)r.nextElement();
			if(CoffeeUtensils.containsString(R.description(),which))
			{ jail=R; break; }
		}
		return jail;
	}

	public void fileAllWarrants(MOB mob)
	{
		ArrestWarrant W=null;
		Vector V=new Vector();
		for(int i=0;(W=getWarrant(mob,i))!=null;i++)
			if(W.criminal==mob)
				V.addElement(W);
		for(int v=0;v<V.size();v++)
		{
			W=(ArrestWarrant)V.elementAt(v);
			warrants.removeElement(W);
			if(W.crime!=null)
			{
				boolean found=false;
				for(int w=0;w<oldWarrants.size();w++)
				{
					ArrestWarrant oW=(ArrestWarrant)oldWarrants.elementAt(w);
					if((oW.criminal==mob)
					&&(oW.crime!=null)
					&&(oW.crime.equals(W.crime)))
						found=true;
				}
				if(!found)
				{
					W.offenses++;
					oldWarrants.addElement(W);
				}
			}
		}

	}

	public ArrestWarrant getOldWarrant(MOB criminal, String crime, boolean pull)
	{
		ArrestWarrant W=null;
		for(int i=0;i<oldWarrants.size();i++)
		{
			ArrestWarrant W2=(ArrestWarrant)oldWarrants.elementAt(i);
			if((W2.criminal==criminal)&&(W2.crime.equals(crime)))
			{
				W=W2;
				if(pull) oldWarrants.removeElement(W2);
				break;
			}
		}
		return W;
	}

	public boolean fillOutWarrant(MOB mob,
								Area myArea,
								Environmental target,
								String crimeLocs,
								String crimeFlags,
								String crime,
								String sentence,
								String warnMsg)
	{
		if(mob.amDead()) return false;
		if(mob.location()==null) return false;
		if((myArea!=null)&&(mob.location().getArea()!=myArea))
			return false;

		if(isAnyKindOfOfficer(mob)
		||(isTheJudge(mob))
		||(mob.isASysOp(mob.location())))
			return false;
		
		// is there a witness
		MOB witness=getWitness(myArea,mob);
		if(witness==null) return false;

		// is there a victim (if necessary)
		MOB victim=null;
		if((target!=null)&&(target instanceof MOB))
			victim=(MOB)target;
		if(mob==victim) return false;

		// is the location significant to this crime?
		if(crimeLocs.trim().length()>0)
		{
			boolean aCrime=false;
			Vector V=Util.parse(crimeLocs);
			String display=mob.location().displayText().toUpperCase().trim();
			for(int v=0;v<V.size();v++)
			{
				String str=((String)V.elementAt(v)).toUpperCase();
				if(str.endsWith("INDOORS")&&(str.length()<9))
				{
					if((mob.location().domainType()&Room.INDOORS)>0)
					{
						if(str.startsWith("!")) return false;
					}
					else
						if(!str.startsWith("!")) return false;
					aCrime=true;
				}
				else
				if(str.endsWith("HOME")&&(str.length()<6))
				{
					for(int a=0;a<mob.location().numAffects();a++)
					{
						Ability A=mob.location().fetchAffect(a);
						if((A!=null)&&(A instanceof LandTitle))
						{
							LandTitle L=(LandTitle)A;
							if(L.landOwner().equals(mob.Name()))
								if(str.startsWith("!")) return false;
						}
					}
					if(!str.startsWith("!")) return false;
					aCrime=true;
				}
				else
				if(str.startsWith("!")&&(CoffeeUtensils.containsString(display,str.substring(1))))
					return false;
				else
				if(CoffeeUtensils.containsString(display,str))
				{ aCrime=true; break;}
			}
			if(!aCrime) return false;
		}

		// any special circumstances?
		if(crimeFlags.trim().length()>0)
		{
			Vector V=Util.parse(crimeFlags);
			for(int v=0;v<V.size();v++)
			{
				String str=((String)V.elementAt(v)).toUpperCase();
				if(str.endsWith("WITNESS")&&(str.length()<9))
				{
					if((witness!=null)&&(witness.location()==mob.location()))
					{
						if(str.startsWith("!"))	return false;
					}
					else
						if(!str.startsWith("!")) return false;
				}
				else
				if(str.endsWith("COMBAT")&&(str.length()<8))
				{
					if(mob.isInCombat())
					{
						if(str.startsWith("!")) return false;
					}
					else
						if(!str.startsWith("!")) return false;

				}
				else
				if(str.endsWith("RECENTLY")&&(str.length()<10))
				{
					ArrestWarrant W=getOldWarrant(mob,crime,false);
					long thisTime=System.currentTimeMillis();
					if((W!=null)&&((thisTime-W.lastOffense)<600000))
					{
						if(str.startsWith("!")) return false;
					}
					else
						if(!str.startsWith("!")) return false;
				}
			}
		}

		// is the victim a protected race?
		if(victim!=null)
		{
			String races=(String)getLaws().get("PROTECTED");
			if((races!=null)&&(races.length()>0)&&(!CoffeeUtensils.containsString(races,victim.charStats().getMyRace().racialCategory())))
			   return false;
		}

		// does a warrant already exist?
		ArrestWarrant W=null;
		for(int i=0;(W=getWarrant(mob,i))!=null;i++)
		{
			if((W.criminal==mob)
			&&(W.victim==victim)
			&&(W.crime.equals(crime)))
				return false;
		}
		if(W==null) W=getOldWarrant(mob,crime,true);
		if(W==null) W=new ArrestWarrant();

		// fill out the warrant!
		W.criminal=mob;
		W.victim=victim;
		W.crime=crime;
		W.state=STATE_SEEKING;
		W.witness=witness;
		W.lastOffense=System.currentTimeMillis();
		W.warnMsg=warnMsg;
		sentence=sentence.trim();
		if(sentence.equalsIgnoreCase("warning"))
			W.actionCode=ACTION_WARN;
		else
		if(sentence.equalsIgnoreCase("threat"))
			W.actionCode=ACTION_THREATEN;
		else
		if(sentence.equalsIgnoreCase("parole1"))
			W.actionCode=ACTION_PAROLE1;
		else
		if(sentence.equalsIgnoreCase("parole2"))
			W.actionCode=ACTION_PAROLE2;
		else
		if(sentence.equalsIgnoreCase("parole3"))
			W.actionCode=ACTION_PAROLE3;
		else
		if(sentence.equalsIgnoreCase("parole4"))
			W.actionCode=ACTION_PAROLE4;
		else
		if(sentence.equalsIgnoreCase("jail1"))
			W.actionCode=ACTION_JAIL1;
		else
		if(sentence.equalsIgnoreCase("jail2"))
			W.actionCode=ACTION_JAIL2;
		else
		if(sentence.equalsIgnoreCase("jail3"))
			W.actionCode=ACTION_JAIL3;
		else
		if(sentence.equalsIgnoreCase("jail4"))
			W.actionCode=ACTION_JAIL4;
		else
		if(sentence.equalsIgnoreCase("death"))
			W.actionCode=ACTION_EXECUTE;
		else
		{
			Log.errOut("Arrest","Unknown sentence: "+sentence+" for crime "+crime);
			return false;
		}

		if((W.victim!=null)&&(isTroubleMaker(W.victim)))
			W.actionCode=W.actionCode/2;
		
		if((isStillACrime(W))
		&&(Sense.canBeSeenBy(W.criminal,W.witness)))
			warrants.addElement(W);
		return true;
	}

	public void affect(Environmental affecting, Affect affect)
	{
		super.affect(affecting, affect);
		if(!(affecting instanceof Area)) return;
		Area myArea=(Area)affecting;
		Properties laws=getLaws();
		if(affect.source()==null) return;

		// the archons pardon
		if((affect.sourceMinor()==Affect.TYP_SPEAK)
		&&(affect.sourceMessage()!=null)
		&&((affect.source().isASysOp(affect.source().location()))
		   ||(isTheJudge(affect.source()))))
		{
			int x=affect.sourceMessage().toUpperCase().indexOf("I HEREBY PARDON ");
			if(x>0)
			{
				int y=affect.sourceMessage().lastIndexOf("'");
				if(y<x)	y=affect.sourceMessage().lastIndexOf("`");
				String name=null;
				if(y>x)
					name=affect.sourceMessage().substring(x+16,y).trim();
				else
					name=affect.sourceMessage().substring(x+16).trim();
				if(name.length()>0)
				for(int i=warrants.size()-1;i>=0;i--)
				{
					ArrestWarrant W=(ArrestWarrant)warrants.elementAt(i);
					if((W.criminal!=null)&&(CoffeeUtensils.containsString(W.criminal.Name(),name)))
					{
						if(W.arrestingOfficer!=null)
							dismissOfficer(W.arrestingOfficer);
						warrants.removeElement(W);
					}
				}
			}
		}
		   
		if((affect.sourceMinor()==Affect.TYP_DEATH)
		&&(affect.tool()!=null)
		&&(affect.tool() instanceof MOB))
		{
			String info=(String)laws.get("MURDER");
			MOB criminal=(MOB)affect.tool();
			if((info!=null)&&(info.length()>0))
			{
				for(int i=warrants.size()-1;i>=0;i--)
				{
					ArrestWarrant W=(ArrestWarrant)warrants.elementAt(i);
					if((W.victim!=null)
					&&(W.criminal!=null)
					&&(W.victim==affect.source())
					&&(W.criminal==criminal))
						warrants.removeElement(W);
				}
				fillOutWarrant(criminal,
							   myArea,
							   affect.source(),
							   getBit(info,BIT_CRIMELOCS),
							   getBit(info,BIT_CRIMEFLAGS),
							   getBit(info,BIT_CRIMENAME),
							   getBit(info,BIT_SENTENCE),
							   getBit(info,BIT_WARNMSG));
				return;
			}
		}
		boolean arrestMobs=false;
		if(getLaws().get("ARRESTMOBS")!=null)
			arrestMobs=((String)getLaws().get("ARRESTMOBS")).equalsIgnoreCase("true");

		if((affect.source().isMonster())&&(!arrestMobs)) 
			return;
		
		if(isAnyKindOfOfficer(affect.source())||(isTheJudge(affect.source())))
			return;

		if(!Sense.aliveAwakeMobile(affect.source(),true))
			return;

		if(affect.source().location()==null) return;

		if((affect.tool()!=null)
		   &&(affect.tool() instanceof Ability)
		   &&(affect.othersMessage()!=null))
		{
			String info=(String)laws.get(affect.tool().ID().toUpperCase());
			if((info!=null)&&(info.length()>0))
				fillOutWarrant(affect.source(),
								myArea,
								affect.target(),
								getBit(info,BIT_CRIMELOCS),
								getBit(info,BIT_CRIMEFLAGS),
								getBit(info,BIT_CRIMENAME),
								getBit(info,BIT_SENTENCE),
								getBit(info,BIT_WARNMSG));
		}
		
		for(int a=0;a<affect.source().numAffects();a++)
		{
			Ability A=affect.source().fetchAffect(a);
			if((A!=null)
			&&(!A.isAutoInvoked())
			&&(laws.get("$"+A.ID().toUpperCase())!=null))
			{
				String info=(String)laws.get("$"+A.ID().toUpperCase());
				if((info!=null)&&(info.length()>0))
					fillOutWarrant(affect.source(),
									myArea,
									null,
									getBit(info,BIT_CRIMELOCS),
									getBit(info,BIT_CRIMEFLAGS),
									getBit(info,BIT_CRIMENAME),
									getBit(info,BIT_SENTENCE),
									getBit(info,BIT_WARNMSG));
			}
		}

		if((Util.bset(affect.targetCode(),Affect.MASK_MALICIOUS))
		&&(affect.target()!=null)
		&&((affect.tool()==null)||(affect.source().isMine(affect.tool())))
		&&(affect.target()!=affect.source())
		&&(!affect.target().name().equals(affect.source().name()))
		&&(affect.target() instanceof MOB)
		&&(!isTheJudge((MOB)affect.target())))
		{
			boolean justResisting=false;
			if(isAnyKindOfOfficer((MOB)affect.target()))
				for(int i=warrants.size()-1;i>=0;i--)
				{
					ArrestWarrant W=(ArrestWarrant)warrants.elementAt(i);
					if((W.criminal==affect.source())
					&&(W.arrestingOfficer!=null)
					&&(W.criminal.location()!=null)
					&&(W.criminal.location().isInhabitant(W.arrestingOfficer)))
					{
						justResisting=true;
						break;
					}
				}
			if(justResisting)
			{
				String info=(String)laws.get("RESISTINGARREST");
				if((info!=null)&&(info.length()>0))
					fillOutWarrant(affect.source(),
									myArea,
									null,
									getBit(info,BIT_CRIMELOCS),
									getBit(info,BIT_CRIMEFLAGS),
									getBit(info,BIT_CRIMENAME),
									getBit(info,BIT_SENTENCE),
									getBit(info,BIT_WARNMSG));
			}
			else
			{
				String info=(String)laws.get("ASSAULT");
				if((info!=null)&&(info.length()>0))
					fillOutWarrant(affect.source(),
									myArea,
									affect.target(),
									getBit(info,BIT_CRIMELOCS),
									getBit(info,BIT_CRIMEFLAGS),
									getBit(info,BIT_CRIMENAME),
									getBit(info,BIT_SENTENCE),
									getBit(info,BIT_WARNMSG));
			}
		}

		if((affect.othersCode()!=Affect.NO_EFFECT)
		   &&(affect.othersMessage()!=null))
		{
			if(affect.sourceMinor()==Affect.TYP_EXAMINESOMETHING)
			{
				String nudity=(String)laws.get("NUDITY");
				if((nudity!=null)
				&&((!affect.source().isMonster())||(arrestMobs))
				&&(nudity.length()>0)
				&&(affect.source().fetchWornItem(Item.ON_LEGS)==null)
				&&(affect.source().fetchWornItem(Item.ON_WAIST)==null)
				&&(affect.source().fetchWornItem(Item.ABOUT_BODY)==null))
					fillOutWarrant(affect.source(),
								   myArea,
								   null,
								   getBit(nudity,BIT_CRIMELOCS),
								   getBit(nudity,BIT_CRIMEFLAGS),
								   getBit(nudity,BIT_CRIMENAME),
								   getBit(nudity,BIT_SENTENCE),
								   getBit(nudity,BIT_WARNMSG));

				String illegalRaces=(String)laws.get("TRESPASSERS");
				String trespassing=(String)laws.get("TRESPASSING");
				if((illegalRaces!=null)
				&&(illegalRaces.length()>0)
				&&(trespassing!=null)
				&&(trespassing.length()>0))
				{
					Vector V=Util.parse(illegalRaces);
					String myRace=affect.source().charStats().getMyRace().racialCategory();
					for(int v=0;v<V.size();v++)
					{
						if(myRace.equalsIgnoreCase((String)V.elementAt(v)))
						{
							fillOutWarrant(affect.source(),
										   myArea,
										   null,
										   getBit(trespassing,BIT_CRIMELOCS),
										   getBit(trespassing,BIT_CRIMEFLAGS),
										   getBit(trespassing,BIT_CRIMENAME),
										   getBit(trespassing,BIT_SENTENCE),
										   getBit(trespassing,BIT_WARNMSG));
							break;
						}
					}
				}


				String armed=(String)laws.get("ARMED");
				Item w=null;
				if((armed!=null)
				&&((!affect.source().isMonster())||(arrestMobs))
				&&(armed.length()>0)
				&&((w=affect.source().fetchWieldedItem())!=null)
				&&(w instanceof Weapon)
				&&(((Weapon)w).weaponClassification()!=Weapon.CLASS_NATURAL)
				&&(((Weapon)w).weaponClassification()!=Weapon.CLASS_HAMMER)
				&&(((Weapon)w).weaponClassification()!=Weapon.CLASS_STAFF)
				&&(Sense.isSeen(w))
				&&(!Sense.isHidden(w))
				&&(!Sense.isInvisible(w)))
					fillOutWarrant(affect.source(),
								   myArea,
								   null,
								   getBit(armed,BIT_CRIMELOCS),
								   getBit(armed,BIT_CRIMEFLAGS),
								   getBit(armed,BIT_CRIMENAME),
								   getBit(armed,BIT_SENTENCE),
								   getBit(armed,BIT_WARNMSG));
			}
			for(int i=0;i<otherCrimes.size();i++)
			{
				Vector V=(Vector)otherCrimes.elementAt(i);
				for(int v=0;v<V.size();v++)
				{
					if(CoffeeUtensils.containsString(affect.othersMessage(),(String)V.elementAt(v)))
					{
						String info=(String)otherBits.elementAt(i);
						fillOutWarrant(affect.source(),
										myArea,
										affect.target(),
										getBit(info,BIT_CRIMELOCS),
										getBit(info,BIT_CRIMEFLAGS),
										getBit(info,BIT_CRIMENAME),
										getBit(info,BIT_SENTENCE),
										getBit(info,BIT_WARNMSG));
					}
				}
			}
		}
	}


	public boolean isBusyWithJustice(MOB M)
	{
		for(int w=0;w<warrants.size();w++)
		{
			ArrestWarrant W=(ArrestWarrant)warrants.elementAt(w);
			if(W.arrestingOfficer!=null)
			{
				if(W.criminal==M) return true;
				else
				if(W.arrestingOfficer==M) return true;
			}
		}
		return false;
	}
	
	public boolean isAnyKindOfOfficer(MOB M)
	{
		if((M.isMonster())&&(M.location()!=null))
		{
			for(int i=0;i<officerNames.size();i++)
				if(CoffeeUtensils.containsString(M.displayText(),(String)officerNames.elementAt(i)))
				{
					for(int b=0;b<M.numBehaviors();b++)
					{
						Behavior B=M.fetchBehavior(b);
						if((B!=null)&&(Util.bset(B.flags(),Behavior.FLAG_MOBILITY)))
							return true;
					}
				}
		}
		return false;
	}

	public boolean isTheJudge(MOB M)
	{
		if((M.isMonster())&&(M.location()!=null))
		{
			String judgeName=(String)getLaws().get("JUDGE");
			if((CoffeeUtensils.containsString(M.Name(),judgeName))
			||(CoffeeUtensils.containsString(M.displayText(),judgeName))
			||(CoffeeUtensils.containsString(M.name(),judgeName))
			||(CoffeeUtensils.containsString(M.ID(),judgeName)))
				return true;
		}
		return false;
	}

	public boolean isElligibleOfficer(MOB M, Area myArea)
	{
		if((M.isMonster())&&(M.location()!=null))
		{
			if((myArea!=null)&&(M.location().getArea()!=myArea)) return false;

			if(isAnyKindOfOfficer(M)
			&&(!isBusyWithJustice(M))
			&&(Sense.aliveAwakeMobile(M,true))
			&&(!M.isInCombat()))
				return true;
		}
		return false;
	}

	public MOB getElligibleOfficerHere(Area myArea, Room R, MOB criminal, MOB victim)
	{
		if(R==null) return null;
		for(int i=0;i<R.numInhabitants();i++)
		{
			MOB M=R.fetchInhabitant(i);
			if((M!=criminal)
			   &&(M.location()!=null)
			   &&(M.location().getArea()==myArea)
			   &&((victim==null)||(M!=victim))
			   &&(isElligibleOfficer(M,myArea))
			   &&(Sense.canBeSeenBy(criminal,M)))
				return M;
		}
		return null;
	}

	public MOB getAnyElligibleOfficer(Area myArea, MOB criminal, MOB victim)
	{
		Room R=criminal.location();
		if(R==null) return null;
		if((myArea!=null)&&(R.getArea()!=myArea)) return null;
		MOB M=getElligibleOfficerHere(myArea,R,criminal,victim);
		if(M==null)
		for(Enumeration e=myArea.getMap();e.hasMoreElements();)
		{
			Room R2=(Room)e.nextElement();
			M=getElligibleOfficerHere(myArea,R2,criminal,victim);
			if(M!=null) break;
		}
		return M;
	}

	public MOB getElligibleOfficer(Area myArea, MOB criminal, MOB victim)
	{
		Room R=criminal.location();
		if(R==null) return null;
		if((myArea!=null)&&(R.getArea()!=myArea)) return null;
		MOB M=getElligibleOfficerHere(myArea,R,criminal,victim);
		if(M!=null) return M;
		for(int d=0;d<Directions.NUM_DIRECTIONS;d++)
		{
			Room R2=R.getRoomInDir(d);
			if(R2!=null)
			{
				M=getElligibleOfficerHere(myArea,R2,criminal,victim);
				if(M!=null)
				{
					int direction=Directions.getOpDirectionCode(d);
					ExternalPlay.move(M,direction,false,false);
					if(M.location()==R) return M;
				}
			}
		}
		return null;
	}

	public String fixCharge(ArrestWarrant W)
	{
		if(W==null) return "";
		String charge=W.crime;
		if(W.victim==null) return charge;
		if(charge.indexOf("<T-NAME>")<0) return charge;
		return charge.replaceFirst("<T-NAME>",W.victim.name());
	}

	public String restOfCharges(MOB mob)
	{
		StringBuffer msg=new StringBuffer("");
		for(int w=0;(getWarrant(mob,w)!=null);w++)
		{
			ArrestWarrant W=getWarrant(mob,w);
			if(W!=null)
			{
				if(w==0)
					msg.append("for "+fixCharge(W));
				else
				if(getWarrant(mob,w+1)==null)
					msg.append(", and for "+fixCharge(W));
				else
					msg.append(", for "+fixCharge(W));
			}
		}
		return msg.toString();
	}

	public void makePeace(Room R)
	{
		if(R==null) return;
		for(int i=0;i<R.numInhabitants();i++)
		{
			MOB inhab=R.fetchInhabitant(i);
			if((inhab!=null)&&(inhab.isInCombat()))
				inhab.makePeace();
		}
	}

	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);

		if(tickID!=Host.AREA_TICK) return true;
		if(!(ticking instanceof Area)) return true;
		Area myArea=(Area)ticking;
		Properties laws=getLaws();


		Hashtable handled=new Hashtable();
		for(int w=warrants.size()-1;w>=0;w--)
		{
			ArrestWarrant W=null;
			try{ W=(ArrestWarrant)warrants.elementAt(w);
			} catch(Exception e){ continue;}

			if(!handled.contains(W.criminal))
			{
				if(!isStillACrime(W))
				{
					unCuff(W.criminal);
					if(W.arrestingOfficer!=null)
						dismissOfficer(W.arrestingOfficer);
					W.setArrestingOfficer(null);
					W.offenses++;
					oldWarrants.addElement(W);
					warrants.removeElement(W);
					continue;
				}
				handled.put(W.criminal,W.criminal);
				switch(W.state)
				{
				case STATE_SEEKING:
					{
						MOB officer=W.arrestingOfficer;
						if((officer==null)||(!W.criminal.location().isInhabitant(officer)))
						   officer=null;
						if(officer==null)
							officer=getElligibleOfficer(myArea,W.criminal,W.victim);
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(W.criminal.location().isInhabitant(W.criminal))
						&&(Sense.canBeSeenBy(W.criminal,officer)))
						{
							if(W.criminal.isASysOp(W.criminal.location()))
							{
								ExternalPlay.quickSay(officer,W.criminal,"Damn, I can't arrest you.",false,false);
								if(W.criminal.isASysOp(null))
								{
									setFree(W.criminal);
									W.setArrestingOfficer(null);
								}
							}
							else
							if(judgeMe(null,officer,W.criminal,W))
							{
								setFree(W.criminal);
								dismissOfficer(officer);
								W.setArrestingOfficer(null);
							}
							else
							{
								W.setArrestingOfficer(officer);
								ExternalPlay.quickSay(W.arrestingOfficer,W.criminal,"You are under arrest "+restOfCharges(W.criminal)+"! Sit down on the ground immediately!",false,false);
								W.state=STATE_ARRESTING;
							}
						}
					}
					break;
				case STATE_ARRESTING:
					{
						MOB officer=W.arrestingOfficer;
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(W.criminal.location().isInhabitant(W.criminal))
						&&(Sense.aliveAwakeMobile(officer,true))
						&&(Sense.canBeSeenBy(W.criminal,officer)))
						{
							if(officer.isInCombat())
							{
								if(officer.getVictim()==W.criminal)
								{
									ExternalPlay.quickSay(officer,W.criminal,(String)laws.get("RESISTFIGHTMSG"),false,false);
									W.state=STATE_SUBDUEING;
								}
								else
								{
									W.setArrestingOfficer(null);
									W.state=STATE_SEEKING;
								}
							}
							else
							{
								if((W.criminal.isMonster())
								&&(!Sense.isEvil(W.criminal))
								&&(!Sense.isSitting(W.criminal)))
								{
									W.criminal.makePeace();
									try{ExternalPlay.doCommand(W.criminal,Util.parse("SIT"));}catch(Exception e){}
								}
								if(Sense.isSitting(W.criminal)||Sense.isSleeping(W.criminal))
									ExternalPlay.quickSay(officer,W.criminal,(String)laws.get("NORESISTMSG"),false,false);
								else
									ExternalPlay.quickSay(officer,W.criminal,(String)laws.get("RESISTWARNMSG"),false,false);
								W.state=STATE_SUBDUEING;
							}
						}
						else
						{
							W.setArrestingOfficer(null);
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_SUBDUEING:
					{
						MOB officer=W.arrestingOfficer;
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(W.criminal.location().isInhabitant(W.criminal))
						&&(Sense.aliveAwakeMobile(officer,true))
						&&(Sense.canBeSeenBy(W.criminal,officer)))
						{
							if((W.criminal.isMonster())
							&&(!Sense.isEvil(W.criminal))
							&&(!Sense.isSitting(W.criminal)))
							{
								W.criminal.makePeace();
								try{ExternalPlay.doCommand(W.criminal,Util.parse("SIT"));}catch(Exception e){}
							}
							if(!Sense.isSitting(W.criminal)&&(!Sense.isSleeping(W.criminal)))
							{
								if(!W.arrestingOfficer.isInCombat())
									ExternalPlay.quickSay(officer,W.criminal,(String)laws.get("RESISTMSG"),false,false);

								Ability A=CMClass.getAbility("Fighter_Whomp");
								if(A!=null){
									int curPoints=W.criminal.curState().getHitPoints();
									double pct=Util.div(curPoints,W.criminal.maxState().getHitPoints());
									A.setProfficiency((int)(100-Math.round(Util.mul(pct,50))));
									if(!A.invoke(officer,W.criminal,(curPoints<25)))
									{
										A=CMClass.getAbility("Skill_Trip");
										curPoints=W.criminal.curState().getHitPoints();
										pct=Util.div(curPoints,W.criminal.maxState().getHitPoints());
										A.setProfficiency((int)(100-Math.round(Util.mul(pct,50))));
										if(!A.invoke(officer,W.criminal,(curPoints<25)))
											ExternalPlay.postAttack(officer,W.criminal,officer.fetchWieldedItem());
									}
								}
							}
							if(Sense.isSitting(W.criminal)||(Sense.isSleeping(W.criminal)))
							{
								makePeace(officer.location());

								// cuff him!
								W.state=STATE_MOVING;
								Ability A=CMClass.getAbility("Skill_HandCuff");
								if(A!=null)	A.invoke(officer,W.criminal,true);
								W.criminal.makePeace();
								makePeace(officer.location());
								A=W.criminal.fetchAffect("Fighter_Whomp");
								if(A!=null)A.unInvoke();
								A=W.criminal.fetchAffect("Skill_Trip");
								if(A!=null)A.unInvoke();
								makePeace(officer.location());
								ExternalPlay.standIfNecessary(W.criminal);
								A=CMClass.getAbility("Skill_Track");
								if(A!=null)	A.invoke(officer,Util.parse((String)laws.get("JUDGE")),null,true);
								makePeace(officer.location());
							}
						}
						else
						{
							unCuff(W.criminal);
							W.setArrestingOfficer(null);
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_MOVING:
					{
						MOB officer=W.arrestingOfficer;

						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(W.criminal.location().isInhabitant(W.criminal))
						&&(Sense.aliveAwakeMobile(officer,true)))
						{
							if(W.criminal.curState().getMovement()<50)
								W.criminal.curState().setMovement(50);
							if(officer.curState().getMovement()<50)
								officer.curState().setMovement(50);
							makePeace(officer.location());
							if(officer.isMonster())
								ExternalPlay.look(officer,null,true);
							if(officer.location().fetchInhabitant((String)laws.get("JUDGE"))!=null)
								W.state=STATE_REPORTING;
							else
							if(W.arrestingOfficer.fetchAffect("Skill_Track")==null)
							{
								Ability A=CMClass.getAbility("Skill_Track");
								if(A!=null)	A.invoke(officer,Util.parse((String)laws.get("JUDGE")),null,true);
							}
							else
							if((Dice.rollPercentage()>75)&&(chitChat.size()>0))
								ExternalPlay.quickSay(officer,W.criminal,(String)chitChat.elementAt(Dice.roll(1,chitChat.size(),-1)),false,false);
						}
						else
						{
							unCuff(W.criminal);
							W.setArrestingOfficer(null);
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_REPORTING:
					{
						MOB officer=W.arrestingOfficer;
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(W.criminal.location().isInhabitant(W.criminal))
						&&(Sense.aliveAwakeMobile(officer,true)))
						{
							MOB judge=officer.location().fetchInhabitant((String)laws.get("JUDGE"));
							if(judge==null)
							{
								W.state=STATE_MOVING;
								Ability A=W.arrestingOfficer.fetchAffect("Skill_Track");
								if(A!=null) officer.delAffect(A);
								A=CMClass.getAbility("Skill_Track");
								if(A!=null)	A.invoke(officer,Util.parse((String)laws.get("JUDGE")),null,true);
							}
							else
							if(Sense.aliveAwakeMobile(judge,true))
							{

								String sirmaam="Sir";
								if(Character.toString((char)judge.charStats().getStat(CharStats.GENDER)).equalsIgnoreCase("F"))
									sirmaam="Ma'am";
								ExternalPlay.quickSay(officer,judge,sirmaam+", "+W.criminal.name()+" has been arrested "+restOfCharges(W.criminal)+".",false,false);
								for(int w2=0;w2<warrants.size();w2++)
								{
									ArrestWarrant W2=(ArrestWarrant)warrants.elementAt(w2);
									if(W2.criminal==W.criminal)
										ExternalPlay.quickSay(officer,judge,"The charge of "+fixCharge(W2)+" was witnessed by "+W2.witness.name()+".",false,false);
								}
								W.state=STATE_WAITING;
							}
							else
							{
								unCuff(W.criminal);
								W.setArrestingOfficer(null);
								W.state=STATE_SEEKING;
							}
						}
						else
						{
							unCuff(W.criminal);
							W.setArrestingOfficer(null);
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_WAITING:
					{
						MOB officer=W.arrestingOfficer;
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(W.criminal.location().isInhabitant(W.criminal))
						&&(Sense.aliveAwakeMobile(officer,true)))
						{
							MOB judge=officer.location().fetchInhabitant((String)laws.get("JUDGE"));
							if(judge==null)
							{
								W.state=STATE_MOVING;
								Ability A=W.arrestingOfficer.fetchAffect("Skill_Track");
								if(A!=null) officer.delAffect(A);
								A=CMClass.getAbility("Skill_Track");
								if(A!=null) A.invoke(officer,Util.parse((String)laws.get("JUDGE")),null,true);
							}
							else
							if(Sense.aliveAwakeMobile(judge,true))
							{
								if(judgeMe(judge,officer,W.criminal,W))
								{
									unCuff(W.criminal);
									dismissOfficer(officer);
									setFree(W.criminal);
									W.setArrestingOfficer(null);
								}
								// else, still stuff to do
							}
							else
							{
								unCuff(W.criminal);
								W.setArrestingOfficer(null);
								W.state=STATE_SEEKING;
							}
						}
						else
						{
							unCuff(W.criminal);
							W.setArrestingOfficer(null);
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_PAROLING:
					{
						MOB officer=W.arrestingOfficer;
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(W.criminal.location().isInhabitant(W.criminal))
						&&(Sense.aliveAwakeMobile(officer,true))
						&&(Sense.canBeSeenBy(W.criminal,officer)))
						{
							MOB judge=officer.location().fetchInhabitant((String)laws.get("JUDGE"));
							setFree(W.criminal);
							if((judge!=null)
							&&(Sense.aliveAwakeMobile(judge,true)))
							{
								judge.location().show(judge,W.criminal,Affect.MSG_OK_VISUAL,"<S-NAME> put(s) <T-NAME> on parole!");
								Ability A=CMClass.getAbility("Prisoner");
								A.startTickDown(judge,W.criminal,W.jailTime);
								W.criminal.recoverEnvStats();
								W.criminal.recoverCharStats();
								ExternalPlay.quickSay(judge,W.criminal,(String)laws.get("PAROLEDISMISS"),false,false);
								dismissOfficer(officer);
								W.setArrestingOfficer(null);
								W.criminal.tell("\n\r\n\r");
							}
							else
							{
								unCuff(W.criminal);
								if(W.arrestingOfficer!=null)
									dismissOfficer(W.arrestingOfficer);
								W.setArrestingOfficer(null);
								W.state=STATE_SEEKING;
							}
						}
						else
						{
							unCuff(W.criminal);
							W.setArrestingOfficer(null);
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_JAILING:
					{
						MOB officer=W.arrestingOfficer;
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(W.criminal.location().isInhabitant(W.criminal))
						&&(Sense.aliveAwakeMobile(officer,true))
						&&(Sense.canBeSeenBy(W.criminal,officer)))
						{
							MOB judge=officer.location().fetchInhabitant((String)laws.get("JUDGE"));
							if((judge!=null)
							&&(Sense.aliveAwakeMobile(judge,true)))
							{
								Room jail=getRoom(judge.location().getArea(),(String)laws.get("JAIL"));
								if(jail!=null)
								{
									makePeace(officer.location());
									W.jail=jail;
									// cuff him!
									W.state=STATE_MOVING2;
									Ability A=CMClass.getAbility("Skill_HandCuff");
									if(A!=null)	A.invoke(officer,W.criminal,true);
									W.criminal.makePeace();
									makePeace(officer.location());
									ExternalPlay.standIfNecessary(W.criminal);
									A=CMClass.getAbility("Skill_Track");
									if(A!=null)	A.invoke(officer,Util.parse(CMMap.getExtendedRoomID(jail)),jail,true);
									makePeace(officer.location());
								}
								else
								{
									setFree(W.criminal);
									ExternalPlay.quickSay(judge,W.criminal,"But since there IS no jail, I will let you go.",false,false);
									dismissOfficer(officer);
									W.setArrestingOfficer(null);
								}
							}
							else
							{
								unCuff(W.criminal);
								if(W.arrestingOfficer!=null)
									dismissOfficer(W.arrestingOfficer);
								W.setArrestingOfficer(null);
								W.state=STATE_SEEKING;
							}
						}
						else
						{
							unCuff(W.criminal);
							W.setArrestingOfficer(null);
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_EXECUTING:
					{
						MOB officer=W.arrestingOfficer;
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(W.criminal))
						&&(W.criminal.location().isInhabitant(officer))
						&&(Sense.aliveAwakeMobile(officer,true))
						&&(Sense.canBeSeenBy(W.criminal,officer)))
						{
							MOB judge=officer.location().fetchInhabitant((String)laws.get("JUDGE"));
							if((judge!=null)&&(Sense.aliveAwakeMobile(judge,true))&&(judge.location()==W.criminal.location()))
							{
								setFree(W.criminal);
								dismissOfficer(officer);
								Ability A=CMClass.getAbility("Prisoner");
								A.startTickDown(judge,W.criminal,100);
								W.criminal.recoverEnvStats();
								W.criminal.recoverCharStats();
								ExternalPlay.postAttack(judge,W.criminal,judge.fetchWieldedItem());
								W.setArrestingOfficer(null);
							}
							else
							{
								unCuff(W.criminal);
								if(W.arrestingOfficer!=null)
									dismissOfficer(W.arrestingOfficer);
								W.setArrestingOfficer(null);
								W.state=STATE_SEEKING;
							}
						}
						else
						{
							unCuff(W.criminal);
							W.setArrestingOfficer(null);
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_MOVING2:
					{
						MOB officer=W.arrestingOfficer;
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(W.criminal.location().isInhabitant(W.criminal))
						&&(Sense.aliveAwakeMobile(officer,true))
						&&(W.jail!=null))
						{
							if(W.criminal.curState().getMovement()<50)
								W.criminal.curState().setMovement(50);
							if(officer.curState().getMovement()<50)
								officer.curState().setMovement(50);
							makePeace(officer.location());
							ExternalPlay.look(officer,null,true);
							if(W.jail==W.criminal.location())
							{
								unCuff(W.criminal);
								Ability A=CMClass.getAbility("Prisoner");
								A.startTickDown(officer,W.criminal,W.jailTime);
								W.criminal.recoverEnvStats();
								W.criminal.recoverCharStats();
								dismissOfficer(officer);
								if(W.criminal.fetchAffect("Prisoner")==null)
									setFree(W.criminal);
								else
									W.state=STATE_RELEASE;
							}
							else
							if(W.arrestingOfficer.fetchAffect("Skill_Track")==null)
							{
								Ability A=CMClass.getAbility("Skill_Track");
								if(A!=null)	A.invoke(officer,Util.parse(CMMap.getExtendedRoomID(W.jail)),W.jail,true);
							}
							else
							if((Dice.rollPercentage()>75)&&(chitChat2.size()>0))
								ExternalPlay.quickSay(officer,W.criminal,(String)chitChat2.elementAt(Dice.roll(1,chitChat2.size(),-1)),false,false);
						}
						else
						{
							unCuff(W.criminal);
							W.setArrestingOfficer(null);
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_RELEASE:
					{
						if((W.criminal.fetchAffect("Prisoner")==null)
						&&(W.criminal.location()!=null)
						&&(W.jail!=null))
						{
							if(W.criminal.location()==W.jail)
							{
								MOB officer=W.arrestingOfficer;
								if((officer==null)
								||(!Sense.aliveAwakeMobile(officer,true))
								||(!W.criminal.location().isInhabitant(W.criminal))
								||(!W.criminal.location().isInhabitant(officer)))
								{
									W.arrestingOfficer=getAnyElligibleOfficer(W.jail.getArea(),W.criminal,W.victim);
									if(W.arrestingOfficer==null) break;
									officer=W.arrestingOfficer;
									W.jail.bringMobHere(officer,false);
									W.jail.show(officer,W.criminal,Affect.MSG_QUIETMOVEMENT,"<S-NAME> arrive(s) to release <T-NAME>.");
									Ability A=CMClass.getAbility("Skill_HandCuff");
									if(A!=null)	A.invoke(officer,W.criminal,true);
								}
								if((W.criminal.isMonster())&&(W.criminal.getStartRoom()!=null))
									W.releaseRoom=W.criminal.getStartRoom();
								else
									W.releaseRoom=getRoom(W.criminal.location().getArea(),(String)laws.get("RELEASEROOM"));
								if(W.releaseRoom==null) W.releaseRoom=getRoom(W.criminal.location().getArea(),(String)laws.get("JUDGE"));
								W.criminal.makePeace();
								makePeace(officer.location());
								Ability A=CMClass.getAbility("Skill_Track");
								if(A!=null)	A.invoke(officer,Util.parse(CMMap.getExtendedRoomID(W.releaseRoom)),W.releaseRoom,true);
							}
							else
							if(W.releaseRoom!=null)
							{
								MOB officer=W.arrestingOfficer;
								if(W.criminal.location()==W.releaseRoom)
								{
									setFree(W.criminal);

									if(officer!=null)
									{
										if((Sense.aliveAwakeMobile(officer,true))
										&&(W.criminal.location().isInhabitant(officer)))
											ExternalPlay.quickSay(officer,null,(String)laws.get("LAWFREE"),false,false);
										dismissOfficer(officer);
									}
								}
								else
								{
									if((officer!=null)
									&&(Sense.aliveAwakeMobile(officer,true))
									&&(W.criminal.location().isInhabitant(officer)))
									{
										ExternalPlay.look(officer,null,true);
										if(W.criminal.curState().getMovement()<20)
											W.criminal.curState().setMovement(20);
										if(officer.curState().getMovement()<20)
											officer.curState().setMovement(20);
										if(W.arrestingOfficer.fetchAffect("Skill_Track")==null)
										{
											Ability A=CMClass.getAbility("Skill_Track");
											if(A!=null)	A.invoke(officer,Util.parse(CMMap.getExtendedRoomID(W.releaseRoom)),W.releaseRoom,true);
										}
									}
									else
									{
										setFree(W.criminal);
										if(officer!=null)
											dismissOfficer(officer);
									}
								}
							}
							else
							{
								setFree(W.criminal);
								if(W.arrestingOfficer!=null)
									dismissOfficer(W.arrestingOfficer);
							}
						}
					}
					break;
				}
			}

		}
		return true;
	}
}
