package com.planet_ink.coffee_mud.common;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.utils.Util;
import java.util.*;

/* 
   Copyright 2000-2004 Bo Zimmerman

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
public class DefaultCharStats implements Cloneable, CharStats
{

	// competency characteristics
	protected int[] stats=new int[NUM_STATS];
	protected int[] partials=null;
	protected CharClass[] myClasses=null;
	protected Integer[] myLevels=null;
	protected Race myRace=null;
	protected String raceName=null;
	protected String genderName=null;
	protected String displayClassName=null;
	protected String displayClassLevel=null;
	protected int[] bodyAlterations=null;
	
	public DefaultCharStats()
	{
		for(int i=0;i<NUM_BASE_STATS;i++)
			stats[i]=10;
		stats[GENDER]='M';
	}
	public DefaultCharStats(int def)
	{
		for(int i=0;i<NUM_STATS;i++)
			stats[i]=def;
	}

	public void setMyClasses(String classes)
	{
		int x=classes.indexOf(";");
		Vector MyClasses=new Vector();
		while(x>=0)
		{
			String theClass=classes.substring(0,x).trim();
			classes=classes.substring(x+1);
			if(theClass.length()>0)
				MyClasses.addElement(CMClass.getCharClass(theClass));
			x=classes.indexOf(";");
		}
		if(classes.trim().length()>0)
			MyClasses.addElement(CMClass.getCharClass(classes.trim()));
		myClasses=new CharClass[MyClasses.size()];
		for(int i=0;i<MyClasses.size();i++)
			myClasses[i]=(CharClass)MyClasses.elementAt(i);
	}
	public void setMyLevels(String levels)
	{
		int x=levels.indexOf(";");
		Vector MyLevels=new Vector();
		while(x>=0)
		{
			String theLevel=levels.substring(0,x).trim();
			levels=levels.substring(x+1);
			if(theLevel.length()>0)
				MyLevels.addElement(new Integer(Util.s_int(theLevel)));
			x=levels.indexOf(";");
		}
		if(levels.trim().length()>0)
			MyLevels.addElement(new Integer(Util.s_int(levels)));
		Integer[] myNewLevels=new Integer[MyLevels.size()];
		for(int i=0;i<MyLevels.size();i++)
			myNewLevels[i]=(Integer)MyLevels.elementAt(i);
		myLevels=myNewLevels;
	}
	public String getMyClassesStr()
	{
		if(myClasses==null)	return "StdCharClass";
		String classStr="";
		for(int i=0;i<myClasses.length;i++)
			classStr+=";"+myClasses[i].ID();
		if(classStr.length()>0)
			classStr=classStr.substring(1);
		return classStr;
	}
	public String getMyLevelsStr()
	{
		if(myLevels==null) return "";
		String levelStr="";
		for(int i=0;i<myLevels.length;i++)
			levelStr+=";"+myLevels[i].intValue();
		if(levelStr.length()>0)
			levelStr=levelStr.substring(1);
		return levelStr;
	}
	public int numClasses()
	{
		if(myClasses==null) return 0;
		return myClasses.length;
	}
	public int combinedSubLevels()
	{
		if((myClasses==null)
		   ||(myLevels==null)
		   ||(myClasses.length<2))
			return 0;
		
		int combined=0;
		for(int i=0;i<myLevels.length-1;i++)
			combined+=myLevels[i].intValue();
		return combined;
	}
	public void setDisplayClassName(String newName){displayClassName=newName;}
	public String displayClassName()
	{	
		if(displayClassName!=null) return displayClassName;
		return getCurrentClass().name();
	}
	public void setDisplayClassLevel(String newLevel){displayClassLevel=newLevel;}
	public String displayClassLevel(MOB mob, boolean shortForm)
	{
		if(displayClassLevel!=null)
		{
			if(shortForm)
				return displayClassName()+" "+displayClassLevel;
			else
				return "level "+displayClassLevel+" "+displayClassName;
		}
		int classLevel=getClassLevel(getCurrentClass());
		String levelStr=null;
		if(classLevel>=mob.envStats().level())
			levelStr=""+mob.envStats().level();
		else
			levelStr=classLevel+"/"+mob.envStats().level();
		if(shortForm)
			return displayClassName()+" "+levelStr;
		else
			return "level "+levelStr+" "+displayClassName();
	}
	public String displayClassLevelOnly(MOB mob)
	{
		if(displayClassLevel!=null)
			return displayClassLevel;
		int classLevel=getClassLevel(getCurrentClass());
		String levelStr=null;
		if(classLevel>=mob.envStats().level())
			levelStr=""+mob.envStats().level();
		else
			levelStr=classLevel+"/"+mob.envStats().level();
		return levelStr;
	}

	public String getSavesStr()
	{
		StringBuffer str=new StringBuffer("");
		for(int x=CharStats.NUM_SAVE_START;x<CharStats.NUM_STATS;x++)
			str.append(stats[x]+";");
		return str.toString();
	}
	public void setSaves(String str)
	{
		Vector V=Util.parseSemicolons(str,false);
		for(int x=CharStats.NUM_SAVE_START;x<CharStats.NUM_STATS;x++)
		{
			int vnum=x-CharStats.NUM_SAVE_START;
			if((vnum<V.size())&&(vnum>=0))
				stats[x]=Util.s_int((String)V.elementAt(vnum));
		}
	}
	public void setRaceName(String newRaceName){raceName=newRaceName;}
	public String raceName(){
		if(raceName!=null) return raceName;
		if(myRace!=null) return myRace.name();
		return "MOB";
	}
	public CharClass getMyClass(int i)
	{
		if((myClasses==null)
		||(i<0)
		||(i>=myClasses.length)) 
			return CMClass.getCharClass("StdCharClass");
		return myClasses[i];
	}
	public int getClassLevel(String aClass)
	{
		if(myClasses==null)	return -1;
		for(int i=0;i<myClasses.length;i++)
			if((myClasses[i]!=null)
			&&(myClasses[i].ID().equals(aClass)))
			   return myLevels[i].intValue();
		return -1;
	}
	public int getClassLevel(CharClass aClass)
	{
		if(myClasses==null)	return -1;
		for(int i=0;i<myClasses.length;i++)
			if((myClasses[i]!=null)
			&&(myClasses[i]==aClass))
			   return myLevels[i].intValue();
		return -1;
	}
	
	public void setClassLevel(CharClass aClass, int level)
	{
		if(myClasses==null)
		{
			myClasses=new CharClass[1];
			myLevels=new Integer[1];
			myClasses[0]=aClass;
			myLevels[0]=new Integer(level);
		}
		else
		{
			if((level<0)&&(myClasses.length>1))
			{
				CharClass[] oldClasses=myClasses;
				Integer[] oldLevels=myLevels;
				CharClass[] myNewClasses=new CharClass[oldClasses.length-1];
				Integer[] myNewLevels=new Integer[oldClasses.length-1];
				for(int c=0;c<myNewClasses.length;c++)
				{
					myNewClasses[c]=oldClasses[c];
					if(c<oldLevels.length)
						myNewLevels[c]=oldLevels[c];
					else
						myNewLevels[c]=new Integer(0);
				}
				myClasses=myNewClasses;
				myLevels=myNewLevels;
			}
			else
			if(getClassLevel(aClass)<0)
				setCurrentClass(aClass);
			for(int i=0;i<numClasses();i++)
			{
				CharClass C=getMyClass(i);
				if((C==aClass)&&(myLevels[i].intValue()!=level))
				{
					myLevels[i]=new Integer(level);
					break;
				}
			}
		}
	}
	
	public void setCurrentClass(CharClass aClass)
	{
		if(((myClasses==null)||(myLevels==null))
		||((numClasses()==1)&&(myClasses[0].ID().equals("StdCharClass"))))
		{
			myClasses=new CharClass[1];
			myLevels=new Integer[1];
			myClasses[0]=aClass;
			myLevels[0]=new Integer(0);
			return;
		}
		
		int level=getClassLevel(aClass);
		if(level<0)
		{
			CharClass[] oldClasses=myClasses;
			Integer[] oldLevels=myLevels;
			CharClass[] myNewClasses=new CharClass[oldClasses.length+1];
			Integer[] myNewLevels=new Integer[oldClasses.length+1];
			for(int c=0;c<oldClasses.length;c++)
			{
				myNewClasses[c]=oldClasses[c];
				myNewLevels[c]=oldLevels[c];
			}
			myNewClasses[oldClasses.length]=aClass;
			myNewLevels[oldClasses.length]=new Integer(0);
			myClasses=myNewClasses;
			myLevels=myNewLevels;
		}
		else
		{
			if(myClasses[myClasses.length-1]==aClass)
				return;
			Integer oldI=new Integer(level);
			boolean go=false;
			CharClass[] myNewClasses=(CharClass[])myClasses.clone();
			Integer[] myNewLevels=(Integer[])myLevels.clone();
			for(int i=0;i<myNewClasses.length-1;i++)
			{
				CharClass C=getMyClass(i);
				if((C==aClass)||(go))
				{
					go=true;
					myNewClasses[i]=myNewClasses[i+1];
					myNewLevels[i]=myNewLevels[i+1];
				}
			}
			myNewClasses[myNewClasses.length-1]=aClass;
			myNewLevels[myNewClasses.length-1]=oldI;
			myClasses=myNewClasses;
			myLevels=myNewLevels;
		}
	}
	public CharClass getCurrentClass()
	{
		if(myClasses==null)
			setCurrentClass(CMClass.getCharClass("StdCharClass"));
		return myClasses[myClasses.length-1];
	}
	
	public Race getMyRace()
	{
		if(myRace==null) 
			myRace=CMClass.getRace("StdRace");
		return myRace;
	}
	
	public void setMyRace(Race newVal){myRace=newVal;}
	public int getBodyPart(int racialPartNumber)
	{
		int num=getMyRace().bodyMask()[racialPartNumber];
		if((num<0)||(bodyAlterations==null)) return num;
		num+=bodyAlterations[racialPartNumber];
		if(num<0) return 0;
		return num;
	}
	
	public String getBodyPartStr()
	{
		StringBuffer str=new StringBuffer("");
		for(int i=0;i<getMyRace().bodyMask().length;i++)
			str.append(getBodyPart(i)+";");
		return str.toString();
	}
	
	public void setBodyPartStrAfterRace(String str)
	{
		Vector V=Util.parseSemicolons(str,true);
		bodyAlterations=null;
		for(int i=0;i<getMyRace().bodyMask().length;i++)
		{
			if(V.size()<=i) break;
			int val=Util.s_int((String)V.elementAt(i));
			int num=getMyRace().bodyMask()[i];
			if(num!=val) alterBodypart(i,val-num);
		}
	}
	
	public void alterBodypart(int racialPartNumber, int deviation)
	{
		if(bodyAlterations==null) bodyAlterations=new int[Race.BODY_PARTS];
		bodyAlterations[racialPartNumber]=deviation;
	}
	
	public int ageCategory()
	{
		int age=getStat(CharStats.AGE);
		int cat=Race.AGE_INFANT;
		int[] chart=getMyRace().getAgingChart();
		if(age<chart[1]) return cat;
		while((cat<=Race.AGE_ANCIENT)&&(age>=chart[cat]))
			cat++;
		return cat-1;
	}

	public String ageName()
	{
		int cat=ageCategory();
		if(cat<Race.AGE_ANCIENT) return Race.AGE_DESCS[cat];
		int age=getStat(CharStats.AGE);
		int[] chart=getMyRace().getAgingChart();
		int diff=chart[Race.AGE_ANCIENT]-chart[Race.AGE_VENERABLE];
		age=age-chart[Race.AGE_ANCIENT];
		int num=(diff>0)?(int)Math.abs(Math.floor(Util.div(age,diff))):0;
		if(num<=0) return Race.AGE_DESCS[cat];
		return Race.AGE_DESCS[cat]+" "+Util.convertToRoman(num);
	}
	
	public int getSave(int which)
	{
		switch(which)
		{
		case SAVE_PARALYSIS:
			return getStat(SAVE_PARALYSIS)+(int)Math.round(Util.div(getStat(CONSTITUTION)+getStat(STRENGTH),2.0));
		case SAVE_FIRE:
			return getStat(SAVE_FIRE)+(int)Math.round(Util.div(getStat(CONSTITUTION)+getStat(DEXTERITY),2.0));
		case SAVE_COLD:
			return getStat(SAVE_COLD)+(int)Math.round(Util.div(getStat(CONSTITUTION)+getStat(DEXTERITY),2.0));
		case SAVE_WATER:
			return getStat(SAVE_WATER)+(int)Math.round(Util.div(getStat(CONSTITUTION)+getStat(DEXTERITY),2.0));
		case SAVE_GAS:
			return getStat(SAVE_GAS)+(int)Math.round(Util.div(getStat(CONSTITUTION)+getStat(STRENGTH),2.0));
		case SAVE_MIND:
			return getStat(SAVE_MIND)+(int)Math.round(Util.div(getStat(WISDOM)+getStat(INTELLIGENCE)+getStat(CHARISMA),3.0));
		case SAVE_GENERAL:
			return getStat(SAVE_GENERAL)+getStat(CONSTITUTION);
		case SAVE_JUSTICE:
			return getStat(SAVE_JUSTICE)+getStat(CHARISMA);
		case SAVE_ACID:
			return getStat(SAVE_ACID)+(int)Math.round(Util.div(getStat(CONSTITUTION)+getStat(DEXTERITY),2.0));
		case SAVE_ELECTRIC:
			return getStat(SAVE_ELECTRIC)+(int)Math.round(Util.div(getStat(CONSTITUTION)+getStat(DEXTERITY),2.0));
		case SAVE_POISON:
			return getStat(SAVE_POISON)+getStat(CONSTITUTION);
		case SAVE_UNDEAD:
			return getStat(SAVE_UNDEAD)+getStat(WISDOM);
		case SAVE_DISEASE:
			return getStat(SAVE_DISEASE)+getStat(CONSTITUTION);
		case SAVE_MAGIC:
			return getStat(SAVE_MAGIC)+getStat(INTELLIGENCE);
		case SAVE_TRAPS:
			return getStat(SAVE_TRAPS)+getStat(DEXTERITY);
		}
		return 0;
	}

	// create a new one of these
	public CharStats cloneCharStats()
	{
		DefaultCharStats newOne=new DefaultCharStats();
		if(myClasses!=null)
			newOne.myClasses=(CharClass[])myClasses.clone();
		newOne.myRace=myRace;
		if(myLevels!=null)
			newOne.myLevels=(Integer[])myLevels.clone();
		newOne.stats=(int[])stats.clone();
		return newOne;
	}

	public void setGenderName(String gname)
	{
		genderName=gname;
	}
	
	public String genderName()
	{
		if(genderName!=null) 
			return genderName;
		switch(getStat(GENDER))
		{
		case 'M': return "male";
		case 'F': return "female";
		default: return "neuter";
		}
	}
	public String himher()
	{
		char c=(char)getStat(GENDER);
		if((genderName!=null)&&(genderName.length()>0))
			c=Character.toUpperCase(genderName.charAt(0));
		switch(c)
		{
		case 'M': return "him";
		case 'F': return "her";
		default: return "it";
		}
	}

	public String hisher()
	{
		char c=(char)getStat(GENDER);
		if((genderName!=null)&&(genderName.length()>0))
			c=Character.toUpperCase(genderName.charAt(0));
		switch(c)
		{
		case 'M': return "his";
		case 'F': return "her";
		default: return "its";
		}
	}

	public String heshe()
	{
		char c=(char)getStat(GENDER);
		if((genderName!=null)&&(genderName.length()>0))
			c=Character.toUpperCase(genderName.charAt(0));
		switch(c)
		{
		case 'M': return "he";
		case 'F': return "she";
		default: return "it";
		}
	}

	public String HeShe()
	{
		char c=(char)getStat(GENDER);
		if((genderName!=null)&&(genderName.length()>0))
			c=Character.toUpperCase(genderName.charAt(0));
		switch(c)
		{
		case 'M': return "He";
		case 'F': return "She";
		default: return "It";
		}
	}

	public StringBuffer getStats()
	{
		int max=CommonStrings.getIntVar(CommonStrings.SYSTEMI_BASEMAXSTAT);
		StringBuffer statstr=new StringBuffer("");
		statstr.append(Util.padRight("Strength",15)+": "+Util.padRight(Integer.toString(getStat(STRENGTH)),2)+"/"+(max+getStat(MAX_STRENGTH_ADJ))+"\n\r");
		statstr.append(Util.padRight("Intelligence",15)+": "+Util.padRight(Integer.toString(getStat(INTELLIGENCE)),2)+"/"+(max+getStat(MAX_INTELLIGENCE_ADJ))+"\n\r");
		statstr.append(Util.padRight("Dexterity",15)+": "+Util.padRight(Integer.toString(getStat(DEXTERITY)),2)+"/"+(max+getStat(MAX_DEXTERITY_ADJ))+"\n\r");
		statstr.append(Util.padRight("Wisdom",15)+": "+Util.padRight(Integer.toString(getStat(WISDOM)),2)+"/"+(max+getStat(MAX_WISDOM_ADJ))+"\n\r");
		statstr.append(Util.padRight("Constitution",15)+": "+Util.padRight(Integer.toString(getStat(CONSTITUTION)),2)+"/"+(max+getStat(MAX_CONSTITUTION_ADJ))+"\n\r");
		statstr.append(Util.padRight("Charisma",15)+": "+Util.padRight(Integer.toString(getStat(CHARISMA)),2)+"/"+(max+getStat(MAX_CHARISMA_ADJ))+"\n\r");
		return statstr;
	}


	public int getStat(int abilityCode)
	{
		return stats[abilityCode];
	}

	public void setPermaStat(int abilityCode, int value)
	{
		setStat(abilityCode,value);
		if(abilityCode<CharStats.NUM_BASE_STATS)
		{
			setStat(CharStats.MAX_STRENGTH_ADJ+abilityCode,
					value-CommonStrings.getIntVar(CommonStrings.SYSTEMI_BASEMAXSTAT));
		}
	}
	public void setStat(int abilityCode, int value)
	{
		stats[abilityCode]=value;
	}

	public int getStat(String abilityName)
	{
		for(int i=0;i<20;i++)
			if(TRAITS[i].startsWith(abilityName))
				return getStat(i);
		return -1;
	}

	public int getCode(String abilityName)
	{
		for(int i=0;i<20;i++)
			if(TRAITS[i].startsWith(abilityName))
				return i;
		return -1;
	}

}
