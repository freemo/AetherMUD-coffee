package com.planet_ink.coffee_mud.Behaviors;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;

import java.io.*;
import java.util.*;

public class Arrest extends StdBehavior
{
	public String ID(){return "Arrest";}
	
	public Behavior newInstance()
	{
		return new Arrest();
	}
	
	private Vector warrants=new Vector();
	private Properties laws=null;
	private Vector otherCrimes=new Vector();
	private Vector otherBits=new Vector();
	private Vector officerNames=new Vector();
	private Vector chitChat=new Vector();
	
	private static final int ACTION_WARN=0;
	private static final int ACTION_THREATEN=1;
	private static final int ACTION_JAIL1=2;
	private static final int ACTION_JAIL2=3;
	private static final int ACTION_JAIL3=4;
	private static final int ACTION_JAIL4=5;
	private static final int ACTION_EXECUTE=6;
	private static final int ACTION_HIGHEST=6;
	
	private static final int STATE_SEEKING=0;
	private static final int STATE_ARRESTING=1;
	private static final int STATE_SUBDUEING=2;
	private static final int STATE_MOVING=3;
	private static final int STATE_REPORTING=4;
	private static final int STATE_WAITING=5;
	private static final int STATE_JAILING=6;
	private static final int STATE_EXECUTING=7;
	
	private static final int BIT_CRIMELOCS=0;
	private static final int BIT_CRIMENAME=1;
	private static final int BIT_SENTENCE=2;
	
	private class ArrestWarrant implements Cloneable
	{
		public MOB criminal=null;
		public MOB victim=null;
		public MOB witness=null;
		public MOB arrestingOfficer=null;
		public String crime="";
		public int actionCode=-1;
		public int jailTime=0;
		public int state=-1;
	}

	private Properties getLaws()
	{
		if(laws==null)
		{
			String lawName=getParms();
			if(lawName.length()==0)
				lawName="laws.ini";
			laws=new Properties();
			try{laws.load(new FileInputStream("resources"+File.separatorChar+lawName));}catch(IOException e){Log.errOut("Arrest",e);}
			String officers=(String)laws.get("OFFICERS");
			if((officers!=null)&&(officers.length()>0))
				officerNames=Util.parse(officers);
			if((officers!=null)&&(officers.length()>0))
				officerNames=Util.parse(officers);
			String chat=(String)laws.get("CHITCHAT");
			if((chat!=null)&&(chat.length()>0))
				chitChat=Util.parse(chat);
			
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
		unCuff(mob);
		for(int w=warrants.size()-1;w>=0;w--)
		{
			ArrestWarrant W=(ArrestWarrant)warrants.elementAt(w);
			if(W.criminal==mob)
				warrants.removeElement(W);
		}
	}
	public void dismissOfficer(MOB officer)
	{
		if(officer==null) return;
		Behavior B=null;
		for(int i=0;i<officer.numBehaviors();i++)
		{
			Behavior B2=officer.fetchBehavior(i);
			if(B2 instanceof Mobile)
			{
				B=B2;
				if(B2.ID().equalsIgnoreCase("Mobile"))
					break;
			}
		}
		if(B!=null)
		for(int i=0;i<20;i++)
			B.tick(officer,Host.MOB_TICK);
		if(officer.getStartRoom()!=null)
			officer.getStartRoom().bringMobHere(officer,false);
	}
	
	public MOB getAWitnessHere(Room R)
	{
		if(R!=null)
		for(int i=0;i<R.numInhabitants();i++)
		{
			MOB M=R.fetchInhabitant(i);
			if(M.isMonster()
			   &&(M.charStats().getStat(CharStats.INTELLIGENCE)>3)
			   &&(Dice.rollPercentage()<=(M.getAlignment()/10))
			   )
				return M;
		}
		return null;
	}
	
	public MOB getWitness(MOB mob)
	{
		Room R=mob.location();
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
		if(H.containsKey(W.witness)) return false;
		if((W.victim!=null)&&(H.containsKey(W.victim))) return false;
		return true;
	}
	
	public void fillOutWarrant(MOB mob, 
							   Environmental target, 
							   String crimeLocs,
							   String crime,
							   String sentence)
	{
		if(mob.amDead()) return;
		if(mob.location()==null) return;

		// is there a witness
		MOB witness=getWitness(mob);
		if(witness==null) return;
		
		// is there a victim (if necessary)
		MOB victim=null;
		if((target!=null)&&(target instanceof MOB))
			victim=(MOB)target;
		
		if(mob==victim) return;
		
		// is the location significant to this crime?
		if(crimeLocs.trim().length()>0)
		{
			boolean aCrime=false;
			Vector V=Util.parse(crimeLocs);
			for(int v=0;v<V.size();v++)
			{
				if(CoffeeUtensils.containsString(mob.location().displayText(),(String)V.elementAt(v)))
				{ aCrime=true;break;}
			}
			if(!aCrime) return;
		}
		
		// is the victim a protected race?
		if(victim!=null)
		{
			String races=(String)laws.get("PROTECTED");
			if((races!=null)&&(races.length()>0)&&(!CoffeeUtensils.containsString(races,victim.charStats().getMyRace().name())))
			   return;
		}
		
		// does a warrant already exist?
		ArrestWarrant W=null;
		for(int i=0;(W=getWarrant(mob,i))!=null;i++)
		{
			if((W.criminal==mob)
			   &&(W.victim==victim)
			   &&(W.crime.equals(crime)))
				return;
		}
		if(W==null) W=new ArrestWarrant();
		
		// fill out the warrant!
		W.criminal=mob;
		W.victim=victim;
		W.crime=crime;
		W.state=STATE_SEEKING;
		W.witness=witness;
		sentence=sentence.trim();
		if(sentence.equalsIgnoreCase("warning"))
			W.actionCode=ACTION_WARN;
		else
		if(sentence.equalsIgnoreCase("threat"))
			W.actionCode=ACTION_THREATEN;
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
			return;
		}
		
		if((isStillACrime(W))
		&&(Sense.canBeSeenBy(W.criminal,W.witness)))
			warrants.addElement(W);
	}
	
	public void affect(Environmental affecting, Affect affect)
	{
		super.affect(affecting, affect);
		
		if(laws==null) laws=getLaws();
		if(affect.source()==null) return;
		
		if(affect.sourceMinor()==Affect.TYP_DEATH)
		{
			String info=(String)laws.get("MURDER");
			if((info!=null)&&(info.length()>0))
			for(int i=warrants.size()-1;i>=0;i--)
			{
				ArrestWarrant W=(ArrestWarrant)warrants.elementAt(i);
				if((W.victim!=null)&&(W.victim==affect.source()))
				{
					fillOutWarrant(W.criminal,
								   W.victim,
								   getBit(info,BIT_CRIMELOCS),
								   getBit(info,BIT_CRIMENAME),
								   getBit(info,BIT_SENTENCE));
				}
				else
				if(W.criminal==affect.source())
					warrants.removeElement(W);
			}
		}
		
		if(affect.source().isMonster()) return;

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
							   affect.target(),
							   getBit(info,BIT_CRIMELOCS),
							   getBit(info,BIT_CRIMENAME),
							   getBit(info,BIT_SENTENCE));
		}
		
		if((Util.bset(affect.targetCode(),Affect.MASK_HURT))
		   &&(affect.target()!=affect.source()))
		{
			String info=(String)laws.get("ASSAULT");
			if((info!=null)&&(info.length()>0))
				fillOutWarrant(affect.source(),
							   affect.target(),
							   getBit(info,BIT_CRIMELOCS),
							   getBit(info,BIT_CRIMENAME),
							   getBit(info,BIT_SENTENCE));
		}
		
		if((affect.othersCode()!=Affect.NO_EFFECT)
		   &&(affect.othersMessage()!=null))
		for(int i=0;i<otherCrimes.size();i++)
		{
			Vector V=(Vector)otherCrimes.elementAt(i);
			for(int v=0;v<V.size();v++)
			{
				if(CoffeeUtensils.containsString(affect.othersMessage(),(String)V.elementAt(v)))
				{
					String info=(String)otherBits.elementAt(i);
					fillOutWarrant(affect.source(),
								   affect.target(),
								   getBit(info,BIT_CRIMELOCS),
								   getBit(info,BIT_CRIMENAME),
								   getBit(info,BIT_SENTENCE));
					return;
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
	
	public boolean isElligibleOfficer(MOB M)
	{
		if(M.isMonster())
		{
			for(int i=0;i<officerNames.size();i++)
				if(CoffeeUtensils.containsString(M.displayText(),(String)officerNames.elementAt(i)))
				{
					if((!isBusyWithJustice(M))
					   &&(Sense.aliveAwakeMobile(M,true))
					   &&(!M.isInCombat()))
					{
						for(int b=0;b<M.numBehaviors();b++)
						{
							Behavior B=M.fetchBehavior(b);
							if((B!=null)&&(B instanceof Mobile))
								return true;
						}
					}
					return false;
				}
		}
		return false;
	}
	
	public MOB getElligibleOfficerHere(Room R, MOB criminal, MOB victim)
	{
		if(R==null) return null;
		for(int i=0;i<R.numInhabitants();i++)
		{
			MOB M=R.fetchInhabitant(i);
			if((M!=criminal)
			   &&((victim==null)||(M!=victim))
			   &&(isElligibleOfficer(M))
			   &&(Sense.canBeSeenBy(criminal,M)))
				return M;
		}
		return null;
	}
	
	public MOB getElligibleOfficer(MOB criminal, MOB victim)
	{
		Room R=criminal.location();
		if(R==null) return null;
		MOB M=getElligibleOfficerHere(R,criminal,victim);
		if(M!=null) return M;
		for(int d=0;d<Directions.NUM_DIRECTIONS;d++)
		{
			Room R2=R.getRoomInDir(d);
			if(R2!=null)
			{
				M=getElligibleOfficerHere(R2,criminal,victim);
				if(M!=null)
				{
					int direction=Directions.getOpDirectionCode(d);
					ExternalPlay.move(M,direction,false);
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
	
	public void tick(Environmental ticking, int tickID)
	{
		super.tick(ticking,tickID);

		if(tickID!=Host.AREA_TICK) return;
		if(laws==null) laws=getLaws();
		
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
							officer=getElligibleOfficer(W.criminal,W.victim);
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(Sense.canBeSeenBy(W.criminal,officer)))
						{
							if(W.criminal.isASysOp(W.criminal.location()))
							{
								ExternalPlay.quickSay(officer,W.criminal,"Damn, I can't arrest you.",false,false);
								if(W.criminal.isASysOp(null))
									setFree(W.criminal);
							}
							else
							{
								W.arrestingOfficer=officer;
								ExternalPlay.quickSay(W.arrestingOfficer,W.criminal,"You are under arrest "+restOfCharges(W.criminal)+"! Lie down immediately!",false,false);
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
									W.arrestingOfficer=null;
									W.state=STATE_SEEKING;
								}
							}
							else
							{
								if(Sense.isSitting(W.criminal)||Sense.isSleeping(W.criminal))
									ExternalPlay.quickSay(officer,W.criminal,(String)laws.get("NORESISTMSG"),false,false);
								else
									ExternalPlay.quickSay(officer,W.criminal,(String)laws.get("RESISTWARNMSG"),false,false);
								W.state=STATE_SUBDUEING;
							}
						}
						else
						{
							W.arrestingOfficer=null;
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_SUBDUEING:
					{
						MOB officer=W.arrestingOfficer;
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(Sense.aliveAwakeMobile(officer,true))
						&&(Sense.canBeSeenBy(W.criminal,officer)))
						{
							if(!Sense.isSitting(W.criminal)&&(!Sense.isSleeping(W.criminal)))
							{
								if(!W.arrestingOfficer.isInCombat())
									ExternalPlay.quickSay(officer,W.criminal,(String)laws.get("RESISTMSG"),false,false);
								
								Ability A=CMClass.getAbility("Fighter_Whomp");
								if(A!=null){
									int curPoints=W.criminal.curState().getHitPoints();
									double pct=Util.div(curPoints,W.criminal.maxState().getHitPoints());
									A.setProfficiency((int)(100-Math.round(Util.mul(pct,50))));
									A.invoke(officer,W.criminal,(curPoints<25));
								}
							}
							if(Sense.isSitting(W.criminal)||(Sense.isSleeping(W.criminal)))
							{
								makePeace(officer.location());
								
								// cuff him!
								W.state=STATE_MOVING;
								Ability A=CMClass.getAbility("Skill_HandCuff");
								if(A!=null)	A.invoke(officer,W.criminal,true);
								A=W.criminal.fetchAffect("Fighter_Whomp");
								if(A!=null)A.unInvoke();
								A=W.criminal.fetchAffect("Skill_Trip");
								if(A!=null)A.unInvoke();
								makePeace(officer.location());
								ExternalPlay.standIfNecessary(W.criminal);
								A=CMClass.getAbility("Ranger_Track");
								if(A!=null)	A.invoke(officer,Util.parse((String)laws.get("JUDGE")),null,true);
							}
						}
						else
						{
							unCuff(W.criminal);
							W.arrestingOfficer=null;
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_MOVING:
					{
						MOB officer=W.arrestingOfficer;
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(Sense.aliveAwakeMobile(officer,true))
						&&(Sense.canBeSeenBy(W.criminal,officer)))
						{
							if(W.criminal.curState().getMovement()<20)
								W.criminal.curState().setMovement(20);
							if(officer.curState().getMovement()<20)
								officer.curState().setMovement(20);
							makePeace(officer.location());
							ExternalPlay.look(officer,null,true);
							if(officer.location().fetchInhabitant((String)laws.get("JUDGE"))!=null)
								W.state=STATE_REPORTING;
							else
							if(W.arrestingOfficer.fetchAffect("Ranger_Track")==null)
							{
								Ability A=CMClass.getAbility("Ranger_Track");
								if(A!=null)	A.invoke(officer,Util.parse((String)laws.get("JUDGE")),null,true);
							}
							else
							if((Dice.rollPercentage()>75)&&(chitChat.size()>0))
								ExternalPlay.quickSay(officer,W.criminal,(String)chitChat.elementAt(Dice.roll(1,chitChat.size(),-1)),false,false);
						}
						else
						{
							unCuff(W.criminal);
							W.arrestingOfficer=null;
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_REPORTING:
					{
						MOB officer=W.arrestingOfficer;
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(Sense.aliveAwakeMobile(officer,true))
						&&(Sense.canBeSeenBy(W.criminal,officer)))
						{
							MOB judge=officer.location().fetchInhabitant((String)laws.get("JUDGE"));
							if(judge==null)
							{
								W.state=STATE_MOVING;
								Ability A=W.arrestingOfficer.fetchAffect("Ranger_Track");
								if(A!=null) officer.delAffect(A);
								A=CMClass.getAbility("Ranger_Track");
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
								W.arrestingOfficer=null;
								W.state=STATE_SEEKING;
							}
						}
						else
						{
							unCuff(W.criminal);
							W.arrestingOfficer=null;
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_WAITING:
					{
						MOB officer=W.arrestingOfficer;
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(Sense.aliveAwakeMobile(officer,true))
						&&(Sense.canBeSeenBy(W.criminal,officer)))
						{
							MOB judge=officer.location().fetchInhabitant((String)laws.get("JUDGE"));
							if(judge==null)
							{
								W.state=STATE_MOVING;
								Ability A=W.arrestingOfficer.fetchAffect("Ranger_Track");
								if(A!=null) officer.delAffect(A);
								A=CMClass.getAbility("Ranger_Track");
								A.invoke(officer,Util.parse((String)laws.get("JUDGE")),null,true);
							}
							else
							if(Sense.aliveAwakeMobile(judge,true))
							{
								int num=0;
								int highest=-1;
								for(int w2=0;w2<warrants.size();w2++)
								{
									ArrestWarrant W2=(ArrestWarrant)warrants.elementAt(w2);
									if(W2.criminal==W.criminal)
									{
										num++;
										if(W2.actionCode>highest)
											highest=W2.actionCode;
									}
								}
								highest+=num;
								highest--;
								if(highest>ACTION_HIGHEST) highest=ACTION_HIGHEST;
								int adjusted=highest;
								if((W.criminal.getAlignment()>650)&&(adjusted>0))
									adjusted--;
								switch(adjusted)
								{
								case ACTION_WARN:
									ExternalPlay.quickSay(judge,W.criminal,(String)laws.get("WARNINGMSG"),false,false);
									setFree(W.criminal);
									dismissOfficer(officer);
									break;
								case ACTION_THREATEN:
									ExternalPlay.quickSay(judge,W.criminal,(String)laws.get("THREATMSG"),false,false);
									setFree(W.criminal);
									dismissOfficer(officer);
									break;
								case ACTION_JAIL1:
									ExternalPlay.quickSay(judge,W.criminal,(String)laws.get("JAIL1MSG"),false,false);
									W.jailTime=Util.s_int((String)laws.get("JAIL1TIME"));
									W.state=STATE_JAILING;
									break;
								case ACTION_JAIL2:
									ExternalPlay.quickSay(judge,W.criminal,(String)laws.get("JAIL2MSG"),false,false);
									W.jailTime=Util.s_int((String)laws.get("JAIL2TIME"));
									W.state=STATE_JAILING;
									break;
								case ACTION_JAIL3:
									ExternalPlay.quickSay(judge,W.criminal,(String)laws.get("JAIL3MSG"),false,false);
									W.jailTime=Util.s_int((String)laws.get("JAIL3TIME"));
									W.state=STATE_JAILING;
									break;
								case ACTION_JAIL4:
									ExternalPlay.quickSay(judge,W.criminal,(String)laws.get("JAIL4MSG"),false,false);
									W.jailTime=Util.s_int((String)laws.get("JAIL4TIME"));
									W.state=STATE_JAILING;
									break;
								case ACTION_EXECUTE:
									ExternalPlay.quickSay(judge,W.criminal,(String)laws.get("EXECUTEMSG"),false,false);
									W.state=STATE_EXECUTING;
									break;
								}
							}
							else
							{
								unCuff(W.criminal);
								W.arrestingOfficer=null;
								W.state=STATE_SEEKING;
							}
						}
						else
						{
							unCuff(W.criminal);
							W.arrestingOfficer=null;
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_JAILING:
					{
						MOB officer=W.arrestingOfficer;
						if((officer!=null)
						&&(W.criminal.location().isInhabitant(officer))
						&&(Sense.aliveAwakeMobile(officer,true))
						&&(Sense.canBeSeenBy(W.criminal,officer)))
						{
							MOB judge=officer.location().fetchInhabitant((String)laws.get("JUDGE"));
							setFree(W.criminal);
							if((judge!=null)
							&&(Sense.aliveAwakeMobile(judge,true)))
							{
								Vector V=judge.location().getArea().getMyMap();
								Room jail=null;
								for(int v=0;v<V.size();v++)
								{
									Room R=(Room)V.elementAt(v);
									if(CoffeeUtensils.containsString(R.displayText(),(String)laws.get("JAIL")))
									{ jail=R; break; }
								}
								if(jail==null)
								for(int v=0;v<V.size();v++)
								{
									Room R=(Room)V.elementAt(v);
									if(CoffeeUtensils.containsString(R.description(),(String)laws.get("JAIL")))
									{ jail=R; break; }
								}
								if(jail!=null)
								{
									judge.location().show(judge,W.criminal,Affect.MSG_OK_VISUAL,"<S-NAME> banish(es) <T-NAME> to the jail!");
									jail.bringMobHere(W.criminal,false);
									if(W.criminal.location()==jail)
									{
										Ability A=CMClass.getAbility("Prisoner");
										A.startTickDown(W.criminal,W.jailTime);
										W.criminal.recoverEnvStats();
										W.criminal.recoverCharStats();
									}
									dismissOfficer(officer);
									W.criminal.tell("\n\r\n\r");
									ExternalPlay.look(W.criminal,null,true);
								}
								else
								{
									ExternalPlay.quickSay(judge,W.criminal,"But since there IS no jail, I will let you go.",false,false);
									dismissOfficer(officer);
								}
							}
							else
							{
								unCuff(W.criminal);
								if(W.arrestingOfficer!=null)
									dismissOfficer(W.arrestingOfficer);
								W.state=STATE_SEEKING;
							}
						}
						else
						{
							unCuff(W.criminal);
							W.arrestingOfficer=null;
							W.state=STATE_SEEKING;
						}
					}
					break;
				case STATE_EXECUTING:
					{
						MOB officer=W.arrestingOfficer;
						if((officer!=null)
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
								A.startTickDown(W.criminal,100);
								W.criminal.recoverEnvStats();
								W.criminal.recoverCharStats();
								ExternalPlay.postAttack(judge,W.criminal,judge.fetchWieldedItem());
							}
							else
							{
								unCuff(W.criminal);
								if(W.arrestingOfficer!=null)
									dismissOfficer(W.arrestingOfficer);
								W.state=STATE_SEEKING;
							}
						}
						else
						{
							unCuff(W.criminal);
							W.arrestingOfficer=null;
							W.state=STATE_SEEKING;
						}
					}
					break;
				}
			}
		}
		
	}
}
