package com.planet_ink.coffee_mud.Commands.base;

import com.planet_ink.coffee_mud.utils.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import java.io.*;
import java.util.*;
public class Scoring
{
	public StringBuffer getInventory(MOB seer, MOB mob)
	{
		StringBuffer msg=new StringBuffer("");
		boolean foundAndSeen=false;
		Vector viewItems=new Vector();
		for(int i=0;i<mob.inventorySize();i++)
		{
			Item thisItem=mob.fetchInventory(i);
			if((thisItem!=null)
			&&(thisItem.location()==null)
			&&(thisItem.amWearingAt(Item.INVENTORY)))
			{
				viewItems.addElement(thisItem);
				if(Sense.canBeSeenBy(thisItem,seer))
					foundAndSeen=true;
			}
		}
		if((viewItems.size()>0)&&(!foundAndSeen))
			msg.append("(nothing you can see right now)");
		else
		{
			msg.append(niceLister(seer,viewItems,true));
			if((mob.getMoney()>0)&&(!Sense.canBeSeenBy(mob.location(),seer)))
				msg.append("(some ^ygold^? coins you can't see)");
			else
			if(mob.getMoney()>0)
				msg.append(mob.getMoney()+" ^ygold^? coins.\n\r");
		}
		return msg;
	}
	public void inventory(MOB mob)
	{
		StringBuffer msg=getInventory(mob,mob);
		if(msg.length()==0)
			mob.tell("^HYou are carrying:\n\r^BNothing!^?\n\r");
		else
		if(!mob.isMonster())
			mob.session().unfilteredPrintln("^HYou are carrying:^?\n\r"+msg.toString());
	}

	public StringBuffer niceLister(MOB mob, Vector items, boolean useName)
	{
		StringBuffer say=new StringBuffer("");
		while(items.size()>0)
		{
			Item item=(Item)items.elementAt(0);
			String str=(useName)?item.name():item.displayText();
			int reps=0;
			items.removeElement(item);
			int here=0;
			while(here<items.size())
			{
				Item item2=(Item)items.elementAt(here);
				if(item2==null)
					break;
				else
				{
					String str2=(useName)?item2.name():item2.displayText();
					if(str2.length()==0)
						items.removeElement(item2);
					else
					if(str.equals(str2))
					{
						reps++;
						items.removeElement(item2);
					}
					else
						here++;
				}
			}
			if((Sense.canBeSeenBy(item,mob))
			&&(((item.displayText().length()>0)||useName||((mob.getBitmap()&MOB.ATT_SYSOPMSGS)>0))))
			{
				if(reps==0)	say.append("      ");
				else
				if(reps>0)	say.append(" ("+Util.padLeft(""+(reps+1),2)+") ");
				if((mob.getBitmap()&MOB.ATT_SYSOPMSGS)>0)
					say.append("^H("+CMClass.className(item)+")^N ");
				say.append("^I");
				if(item.envStats().replacementName()!=null)
				{
					if(useName)
						say.append(item.envStats().replacementName());
					else
					if(item.displayText().length()>0)
						say.append(item.envStats().replacementName()+" is here");
					else
						say.append(item.envStats().replacementName());
				}
				else
				{
					if(useName)
						say.append(item.name());
					else
					if(item.displayText().length()>0)
						say.append(item.displayText());
					else
						say.append(item.name());
				}
				say.append(" "+Sense.colorCodes(item,mob)+"^N\n\r");
			}
		}
		return say;
	}
	
	public void score(MOB mob)
	{
		StringBuffer msg=getScore(mob);
		if(!mob.isMonster())
			mob.session().unfilteredPrintln(msg.toString());
	}
	public StringBuffer getScore(MOB mob)
	{
		int adjustedAttack=mob.adjustedAttackBonus();
		int adjustedArmor=(-mob.adjustedArmor())+50;

		StringBuffer msg=new StringBuffer("");

		msg.append("You are ^H"+mob.name()+"^? the level ^B"+mob.envStats().level()+" "+mob.charStats().getMyClass().name()+"^?.\n\r");
		msg.append("You are a ^B"+((mob.charStats().getStat(CharStats.GENDER)=='M')?"male":"female")+" "+mob.charStats().getMyRace().name() + "^?");
		if(mob.getLeigeID().length()>0)
			msg.append(" who serves ^H"+mob.getLeigeID()+"^?");
		if(mob.getLeigeID().length()>0)
			msg.append(" worshipping ^H"+mob.getWorshipCharID()+"^?");
		msg.append(".\n\r");
		msg.append("\n\rYour stats are: \n\r^B"+mob.charStats().getStats(mob.charStats().getMyClass().maxStat())+"^?\n\r");
		msg.append("You have ^H"+mob.curState().getHitPoints()+"/"+mob.maxState().getHitPoints()+"^? hit points, ^H");
		msg.append(mob.curState().getMana()+"/"+mob.maxState().getMana()+"^? mana, and ^H");
		msg.append(mob.curState().getMovement()+"/"+mob.maxState().getMovement()+"^? movement.\n\r");
		msg.append("You are "+mob.envStats().height()+" inches tall and weigh "+mob.baseEnvStats().weight()+" pounds.\n\r");
		msg.append("You have ^B"+mob.envStats().weight()+"^?/^B"+mob.maxCarry()+"^? pounds of encumbrance.\n\r");
		msg.append("You have ^B"+mob.getPractices()+"^? practices, ^B"+mob.getTrains()+"^? training sessions, and ^H"+mob.getQuestPoint()+"^? quest points.\n\r");
		msg.append("You have scored ^B"+mob.getExperience()+"^? experience points, and have been online for ^B"+Math.round(Util.div(mob.getAgeHours(),60.0))+"^? hours.\n\r");
		msg.append("You need ^B"+(mob.getExpNeededLevel())+"^? experience points to advance to the next level.\n\r");
		msg.append("Your alignment is      : ^H"+CommonStrings.alignmentStr(mob.getAlignment())+" ("+mob.getAlignment()+")^?.\n\r");
		msg.append("Your armored defense is: ^H"+CommonStrings.armorStr(adjustedArmor)+"^?.\n\r");
		msg.append("Your combat prowess is : ^H"+CommonStrings.fightingProwessStr(adjustedAttack)+"^?.\n\r");
		msg.append("Wimpy is set to ^B"+mob.getWimpHitPoint()+"^? hit points.\n\r");

		if(Sense.isFalling(mob))
			msg.append("^BYou are falling!!!^?\n\r");
		else
		if(Sense.isSleeping(mob))
			msg.append("^BYou are sleeping.^?\n\r");
		else
		if(Sense.isSitting(mob))
			msg.append("^BYou are resting.^?\n\r");
		else
		if(Sense.isSwimming(mob))
			msg.append("^BYou are swimming.^?\n\r");
		else
		if(Sense.isClimbing(mob))
			msg.append("^BYou are climbing.^?\n\r");
		else
		if(Sense.isFlying(mob))
			msg.append("^BYou are flying.^?\n\r");
		else
			msg.append("^BYou are standing.^?\n\r");
		
		if(mob.riding()!=null)
			msg.append("^BYou are "+mob.riding().stateString()+" "+mob.riding().name()+".^?\n\r");

		if(Sense.isInvisible(mob))
			msg.append("^BYou are invisible.^?\n\r");
		if(Sense.isHidden(mob))
			msg.append("^BYou are hidden.^?\n\r");
		if(Sense.isSneaking(mob))
			msg.append("^BYou are sneaking.^?\n\r");

		if(mob.curState().getHunger()<1)
			msg.append("^BYou are hungry.^?\n\r");
		if(mob.curState().getThirst()<1)
			msg.append("^BYou are thirsty.^?\n\r");
		msg.append("\n\r^BYou are affected by:^? "+getAffects(mob)+"\n\r");

		return msg;
	}

	public void affected(MOB mob)
	{
		StringBuffer msg=new StringBuffer("");
		msg.append("\n\r^BYou are affected by:^? "+getAffects(mob)+"\n\r");
		if(!mob.isMonster())
			mob.session().colorOnlyPrintln(msg.toString());
	}

	public void skills(MOB mob)
	{
		StringBuffer msg=new StringBuffer("");
		Vector V=new Vector();
		V.addElement(new Integer(Ability.THIEF_SKILL));
		V.addElement(new Integer(Ability.SKILL));
		V.addElement(new Integer(Ability.COMMON_SKILL));
		msg.append("\n\r^HYour skills:^? "+getAbilities(mob,V,-1)+"\n\r");
		if(!mob.isMonster())
			mob.session().unfilteredPrintln(msg.toString());
	}

	public void qualify(MOB mob, Vector commands)
	{
		StringBuffer msg=new StringBuffer("");
		String qual=Util.combine(commands,1);
		if((qual.length()==0)||(qual.equalsIgnoreCase("SKILLS"))||(qual.equalsIgnoreCase("SKILL")))
			msg.append("\n\r^HGeneral Skills:^? "+getQualifiedAbilities(mob,Ability.SKILL,-1)+"\n\r");
		if((qual.length()==0)||(qual.equalsIgnoreCase("COMMON SKILLS"))||(qual.equalsIgnoreCase("COMMON")))
			msg.append("\n\r^HCommon Skills:^? "+getQualifiedAbilities(mob,Ability.COMMON_SKILL,-1)+"\n\r");
		if((qual.length()==0)||(qual.equalsIgnoreCase("THIEVES"))||(qual.equalsIgnoreCase("THIEF"))||(qual.equalsIgnoreCase("THIEF SKILLS")))
			msg.append("\n\r^HThief Skills:^? "+getQualifiedAbilities(mob,Ability.THIEF_SKILL,-1)+"\n\r");
		if((qual.length()==0)||(qual.equalsIgnoreCase("SPELLS"))||(qual.equalsIgnoreCase("SPELL"))||(qual.equalsIgnoreCase("MAGE")))
			msg.append("\n\r^HSpells:^? "+getQualifiedAbilities(mob,Ability.SPELL,-1)+"\n\r");
		if((qual.length()==0)||(qual.equalsIgnoreCase("PRAYERS"))||(qual.equalsIgnoreCase("PRAYER"))||(qual.equalsIgnoreCase("CLERIC")))
			msg.append("\n\r^HPrayers:^? "+getQualifiedAbilities(mob,Ability.PRAYER,-1)+"\n\r");
		if((qual.length()==0)||(qual.equalsIgnoreCase("CHANTS"))||(qual.equalsIgnoreCase("CHANT"))||(qual.equalsIgnoreCase("DRUID")))
			msg.append("\n\r^HDruidic Chants:^? "+getQualifiedAbilities(mob,Ability.CHANT,-1)+"\n\r");
		if((qual.length()==0)||(qual.equalsIgnoreCase("SONGS"))||(qual.equalsIgnoreCase("SONG"))||(qual.equalsIgnoreCase("BARD")))
			msg.append("\n\r^HSongs:^? "+getQualifiedAbilities(mob,Ability.SONG,-1)+"\n\r");
		if((qual.length()==0)||(qual.equalsIgnoreCase("LANGS"))||(qual.equalsIgnoreCase("LANG"))||(qual.equalsIgnoreCase("LANGUAGES")))
			msg.append("\n\r^HLanguages:^? "+getQualifiedAbilities(mob,Ability.LANGUAGE,-1)+"\n\r");
		if(msg.length()==0)
			mob.tell("Valid parameters to the QUALIFY command include SKILLS, THIEF, COMMON, SPELLS, PRAYERS, CHANTS, SONGS, or LANGS.");
		else
		if(!mob.isMonster())
			mob.session().unfilteredPrintln("^BYou now qualify for the following:^?"+msg.toString());
	}

	public void prayers(MOB mob)
	{
		StringBuffer msg=new StringBuffer("");
		msg.append("\n\r^HPrayers known:^? "+getAbilities(mob,Ability.PRAYER,-1)+"\n\r");
		if(!mob.isMonster())
			mob.session().unfilteredPrintln(msg.toString());
	}

	public void chants(MOB mob)
	{
		StringBuffer msg=new StringBuffer("");
		msg.append("\n\r^HDruidic Chants known:^? "+getAbilities(mob,Ability.CHANT,-1)+"\n\r");
		if(!mob.isMonster())
			mob.session().unfilteredPrintln(msg.toString());
	}

	public void songs(MOB mob)
	{
		StringBuffer msg=new StringBuffer("");
		msg.append("\n\r^HSongs known:^? "+getAbilities(mob,Ability.SONG,-1)+"\n\r");
		if(!mob.isMonster())
			mob.session().unfilteredPrintln(msg.toString());
	}

	public void languages(MOB mob)
	{
		StringBuffer msg=new StringBuffer("");
		msg.append("\n\r^HLanguages known:^? "+getAbilities(mob,Ability.LANGUAGE,-1)+"\n\r");
		if(!mob.isMonster())
			mob.session().unfilteredPrintln(msg.toString());
	}

	public void spells(MOB mob, Vector commands)
	{
		String qual=Util.combine(commands,1).toUpperCase();
		int domain=-1;
		String domainName="";
		if(qual.length()>0)
		for(int i=1;i<Ability.DOMAIN_DESCS.length;i++)
			if(Ability.DOMAIN_DESCS[i].startsWith(qual))
			{ domain=i<<5; break;}
			else
			if((Ability.DOMAIN_DESCS[i].indexOf("/")>=0)
			&&(Ability.DOMAIN_DESCS[i].substring(Ability.DOMAIN_DESCS[i].indexOf("/")+1).startsWith(qual)))
			{ domain=i<<5; break;}
		if(domain>0)
			domainName=Ability.DOMAIN_DESCS[domain>>5].toLowerCase();
		StringBuffer spells=new StringBuffer("");
		if((domain<0)&&(qual.length()>0))
		{
			spells.append("\n\rValid schools are: ");
			for(int i=1;i<Ability.DOMAIN_DESCS.length;i++)
				spells.append(Ability.DOMAIN_DESCS[i]+" ");
			
		}
		else
			spells.append("\n\r^HYour "+domainName+" spells:^? "+getAbilities(mob,Ability.SPELL,domain));
		if(!mob.isMonster())
			mob.session().unfilteredPrintln(spells.toString()+"\n\r");
	}


	public StringBuffer getAffects(MOB affected)
	{
		StringBuffer msg=new StringBuffer("");
		for(int a=0;a<affected.numAffects();a++)
		{
			Ability thisAffect=affected.fetchAffect(a);
			if((thisAffect!=null)&&(thisAffect.displayText().length()>0))
				msg.append("\n\r"+thisAffect.displayText());
		}
		if(msg.length()==0)
			msg.append("Nothing!");
		return msg;
	}


	public StringBuffer getAbilities(MOB able, int ofType, int ofDomain)
	{
		Vector V=new Vector();
		int mask=Ability.ALL_CODES;
		if(ofDomain>=0)
		{
			mask=Ability.ALL_CODES|Ability.ALL_DOMAINS;
			ofType=ofType|ofDomain;
		}
		V.addElement(new Integer(ofType));
		return getAbilities(able,V,mask);
	}
	public StringBuffer getAbilities(MOB able, Vector ofTypes, int mask)
	{
		int highestLevel=0;
		int lowestLevel=able.envStats().level()+1;
		StringBuffer msg=new StringBuffer("");
		for(int a=0;a<able.numAbilities();a++)
		{
			Ability thisAbility=able.fetchAbility(a);
			if((thisAbility!=null)
			&&(thisAbility.envStats().level()>highestLevel)
			&&(thisAbility.envStats().level()<lowestLevel)
			&&(ofTypes.contains(new Integer(thisAbility.classificationCode()&mask))))
				highestLevel=thisAbility.envStats().level();
		}
		for(int l=0;l<=highestLevel;l++)
		{
			StringBuffer thisLine=new StringBuffer("");
			int col=0;
			for(int a=0;a<able.numAbilities();a++)
			{
				Ability thisAbility=able.fetchAbility(a);
				if((thisAbility!=null)
				&&(thisAbility.envStats().level()==l)
				&&(ofTypes.contains(new Integer(thisAbility.classificationCode()&mask))))
				{
					if(thisLine.length()==0)
						thisLine.append("\n\rLevel ^B"+l+"^?:\n\r");
					if((++col)>3)
					{
						thisLine.append("\n\r");
						col=1;
					}
					thisLine.append("^N[^H"+Util.padRight(Integer.toString(thisAbility.profficiency()),3)+"%^?] ^N"+Util.padRight(thisAbility.name(),(col==3)?18:19));
				}
			}
			if(thisLine.length()>0)
				msg.append(thisLine);
		}
		if(msg.length()==0)
			msg.append("^BNone!^?");
		return msg;
	}


	public StringBuffer getQualifiedAbilities(MOB able, int ofType, int ofDomain)
	{
		Vector V=new Vector();
		int mask=Ability.ALL_CODES;
		if(ofDomain>=0)
		{
			mask=Ability.ALL_CODES|Ability.ALL_DOMAINS;
			ofType=ofType|ofDomain;
		}
		V.addElement(new Integer(ofType));
		return getQualifiedAbilities(able,V,mask);
	}
	
	public StringBuffer getQualifiedAbilities(MOB able, Vector ofTypes, int mask)
	{
		int highestLevel=0;
		int lowestLevel=able.envStats().level()+1;
		StringBuffer msg=new StringBuffer("");
		for(int a=0;a<CMClass.abilities.size();a++)
		{
			Ability thisAbility=(Ability)CMClass.abilities.elementAt(a);
			int level=thisAbility.qualifyingLevel(able);
			if((thisAbility.qualifiesByLevel(able))
			&&(level>highestLevel)
			&&(level<lowestLevel)
			&&(ofTypes.contains(new Integer(thisAbility.classificationCode()&mask))))
				highestLevel=level;
		}
		int col=0;
		for(int l=0;l<=highestLevel;l++)
		{
			StringBuffer thisLine=new StringBuffer("");
			for(int a=0;a<CMClass.abilities.size();a++)
			{
				Ability thisAbility=(Ability)CMClass.abilities.elementAt(a);
				if((thisAbility.qualifiesByLevel(able))
				   &&(thisAbility.qualifyingLevel(able)==l)
				   &&(ofTypes.contains(new Integer(thisAbility.classificationCode()&mask))))
				{
					if((++col)>3)
					{
						thisLine.append("\n\r");
						col=1;
					}
					thisLine.append("^N[^H"+Util.padRight(""+l,3)+"^?] "+Util.padRight(thisAbility.name(),(col==3)?19:20));
				}
			}
			if(thisLine.length()>0)
			{
				if(msg.length()==0)
					msg.append("\n\r^N[^HLvl^?]                     [^HLvl^?]                     [^HLvl^?]\n\r");
				msg.append(thisLine);
			}
		}
		if(msg.length()==0)
			msg.append("None!");
		return msg;
	}


	public StringBuffer getEquipment(MOB seer, MOB mob)
	{
		StringBuffer msg=new StringBuffer("");
		boolean foundButUnseen=false;
		for(int l=0;l<16;l++)
		{
			int wornCode=1<<l;
			String header="^N(^H"+Sense.wornLocation(wornCode)+"^?)";
			header+=Util.SPACES.substring(0,26-header.length())+": ^B";
			for(int i=0;i<mob.inventorySize();i++)
			{
				Item thisItem=mob.fetchInventory(i);
				if((thisItem.location()==null)&&(thisItem.amWearingAt(wornCode)))
				{
					if(Sense.canBeSeenBy(thisItem,seer))
					{
						String name=thisItem.name();
						if(name.length()>53) name=name.substring(0,50)+"...";
						msg.append(header+name+Sense.colorCodes(thisItem,seer)+"^?\n\r");
					}
					else
						foundButUnseen=true;
				}
			}
		}
		if(foundButUnseen)
			msg.append("(nothing you can see right now)");
		else
		if(msg.length()==0)
			msg.append("^B(nothing)^?\n\r");

		return msg;
	}
	public void equipment(MOB mob)
	{
		if(!mob.isMonster())
			mob.session().unfilteredPrintln("You are wearing:\n\r"+getEquipment(mob,mob));
	}
	public void commands(MOB mob, CommandSet commandSet)
	{
		if(!mob.isMonster())
			mob.session().colorOnlyPrintln("^HComplete commands list:^?\n\r"+commandSet.commandList());
	}
	public void socials(MOB mob, Socials socials)
	{
		if(!mob.isMonster())
			mob.session().colorOnlyPrintln("^HComplete socials list:^?\n\r"+socials.getSocialsList());
	}

	public void areas(MOB mob)
	{
		StringBuffer areasList=(StringBuffer)Resources.getResource("areasList");
		if(areasList==null)
		{

			Vector areasVec=new Vector();
			for(int a=0;a<CMMap.numAreas();a++)
				areasVec.addElement((CMMap.getArea(a)).name());
			Collections.sort((List)areasVec);
			StringBuffer msg=new StringBuffer("^HComplete areas list:^?\n\r");
			int col=0;
			for(int i=0;i<areasVec.size();i++)
			{
				if((++col)>3)
				{
					msg.append("\n\r");
					col=1;
				}

				msg.append(Util.padRight((String)areasVec.elementAt(i),25));
			}
			msg.append("\n\r\n\r^HEnter 'HELP (AREA NAME) for more information.^?");
			Resources.submitResource("areasList",msg);
			areasList=msg;
		}



		if(!mob.isMonster())
			mob.session().colorOnlyPrintln(areasList.toString());
	}
}
