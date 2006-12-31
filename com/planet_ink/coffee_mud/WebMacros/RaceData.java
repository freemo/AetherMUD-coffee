package com.planet_ink.coffee_mud.WebMacros;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;
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
public class RaceData extends StdWebMacro
{
	public String name()	{return this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);}

	// valid parms include HELP, STATS, SENSES, TRAINS, PRACS, ABILITIES,
	// HEALTHTEXTS, NATURALWEAPON, PLAYABLE, DISPOSITIONS, STARTINGEQ,
	// CLASSES, LANGS, EFFECTS

	public String runMacro(ExternalHTTPRequests httpReq, String parm)
	{
		Hashtable parms=parseParms(parm);
		String last=httpReq.getRequestParameter("RACE");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			Race R=CMClass.getRace(last);
			if(R!=null)
			{
				StringBuffer str=new StringBuffer("");
				if(parms.containsKey("HELP"))
				{
					StringBuffer s=CMLib.help().getHelpText(R.ID(),null,false);
					if(s==null)
						s=CMLib.help().getHelpText(R.name(),null,false);
					if(s!=null)
					{
						int limit=70;
						if(parms.containsKey("LIMIT")) limit=CMath.s_int((String)parms.get("LIMIT"));
						str.append(helpHelp(s,limit));
					}
				}
                if(parms.containsKey("NAME"))
                {
                    String old=httpReq.getRequestParameter("NAME");
                    if(old==null) old=R.name();
                    str.append(old+", ");
                }
                if(parms.containsKey("CAT"))
                {
                    String old=httpReq.getRequestParameter("CAT");
                    if(old==null) old=R.racialCategory();
                    str.append(old+", ");
                }
                if(parms.containsKey("VWEIGHT"))
                {
                    String old=httpReq.getRequestParameter("VWEIGHT");
                    if(old==null) old=""+R.weightVariance();
                    str.append(old+", ");
                }
                if(parms.containsKey("BWEIGHT"))
                {
                    String old=httpReq.getRequestParameter("BWEIGHT");
                    if(old==null) old=""+R.lightestWeight();
                    str.append(old+", ");
                }
                if(parms.containsKey("VHEIGHT"))
                {
                    String old=httpReq.getRequestParameter("VHEIGHT");
                    if(old==null) old=""+R.heightVariance();
                    str.append(old+", ");
                }
                if(parms.containsKey("MHEIGHT"))
                {
                    String old=httpReq.getRequestParameter("MHEIGHT");
                    if(old==null) old=""+R.shortestMale();
                    str.append(old+", ");
                }
                if(parms.containsKey("FHEIGHT"))
                {
                    String old=httpReq.getRequestParameter("FHEIGHT");
                    if(old==null) old=""+R.shortestFemale();
                    str.append(old+", ");
                }
                if(parms.containsKey("LEAVESTR"))
                {
                    String old=httpReq.getRequestParameter("LEAVESTR");
                    if(old==null) old=""+R.leaveStr();
                    str.append(old+", ");
                }
                if(parms.containsKey("ARRIVESTR"))
                {
                    String old=httpReq.getRequestParameter("ARRIVESTR");
                    if(old==null) old=""+R.arriveStr();
                    str.append(old+", ");
                }
                if(parms.containsKey("HEALTHRACE"))
                {
                    R=R.makeGenRace();
                    String old=httpReq.getRequestParameter("HEALTHRACE");
                    if(old==null) old=""+R.getStat("HEALTHRACE");
                    str.append("<OPTION VALUE=\"\" "+((old.length()==0)?"SELECTED":"")+">None");
                    Race R2=null;
                    for(Enumeration e=CMClass.races();e.hasMoreElements();)
                    {
                        R2=(Race)e.nextElement();
                        if(!R2.isGeneric())
                            str.append("<OPTION VALUE=\"\" "+((old.equalsIgnoreCase(R2.getClass().getName()))?"SELECTED":"")+">"+R2.getClass().getName());
                        else
                        {
                            String RID="com.planet_ink.coffee_mud.Races."+R2.ID();
                            if(CMClass.checkForCMClass("RACE",RID))
                                str.append("<OPTION VALUE=\"\" "+((old.equalsIgnoreCase(RID))?"SELECTED":"")+">"+RID);
                        }
                    }
                }
                if(parms.containsKey("BODY"))
                {
                    str.append("<TABLE WIDTH=100% BORDER=0>");
                    for(int i=0;i<Race.BODYPARTSTR.length;i++)
                    {
                        String old=httpReq.getRequestParameter("BODYPART"+i);
                        if(old==null) old=""+R.bodyMask()[i];
                        str.append("<TR><TD>"+Race.BODYPARTSTR[i]+"</TD><TD><INPUT TYPE=TEXT NAME=BODYPART"+i+" VALUE=\""+old+"\" SIZE=3></TD></TR>");
                    }
                    str.append("</TABLE>, ");
                }
                if(parms.containsKey("WEAR"))
                    for(int b=0;b<Item.WORN_CODES.length;b++)
                        if(CMath.bset(R.forbiddenWornBits(),Item.WORN_CODES[b]))
                            str.append(Item.WORN_DESCS[b]+", ");
                if(parms.containsKey("WEARID"))
                {
                    String old=httpReq.getRequestParameter("WEARID");
                    long mask=0;
                    if(old==null) 
                        mask=R.forbiddenWornBits();
                    else
                    {
                        mask|=CMath.s_long(old);
                        for(int i=1;;i++)
                            if(httpReq.isRequestParameter("WEARID"+(new Integer(i).toString())))
                                mask|=CMath.s_long(httpReq.getRequestParameter("WEARID"+(new Integer(i).toString())));
                            else
                                break;
                    }
                    for(int i=0;i<Item.WORN_CODES.length;i++)
                    {
                        str.append("<OPTION VALUE="+Item.WORN_CODES[i]+" ");
                        if(CMath.bset(mask,Item.WORN_CODES[i]))
                            str.append("SELECTED");
                        str.append(">"+Item.WORN_DESCS[i]);
                    }
                    str.append(", ");
                }
                if(parms.containsKey("PLAYABLEID"))
                {
                    String old=httpReq.getRequestParameter("PLAYABLEID");
                    long mask=0;
                    if(old==null) 
                        mask=R.availabilityCode();
                    else
                        mask|=CMath.s_long(old);
                    for(int i=0;i<Area.THEME_DESCS_EXT.length;i++)
                        str.append("<OPTION VALUE="+i+" "+((i==mask)?"SELECTED":"")+">"+Area.THEME_DESCS_EXT[i]);
                    str.append(", ");
                }
                
                
				if(parms.containsKey("PLAYABLE"))
					str.append(Area.THEME_DESCS_EXT[R.availabilityCode()]+", ");
				if(parms.containsKey("NATURALWEAPON"))
					str.append(R.myNaturalWeapon().name()+", ");
				MOB mob=CMClass.getMOB("StdMOB");
				MOB mob2=CMClass.getMOB("StdMOB");
				mob.setSession((Session)CMClass.getCommon("DefaultSession"));
				mob.baseCharStats().setMyRace(R);
				mob2.baseCharStats().setMyRace(CMClass.getRace("StdRace"));
				R.startRacing(mob,false);
				mob.recoverCharStats();
				mob.recoverCharStats();
				mob.recoverEnvStats();
				mob.recoverMaxState();
				mob2.recoverCharStats();
				mob2.recoverEnvStats();
				mob2.recoverMaxState();
				mob.setSession(null);
				if(parms.containsKey("STATS"))
				{
					for(int c=0;c<CharStats.NUM_STATS;c++)
					{
						int oldStat=mob2.charStats().getStat(c);
						int newStat=mob.charStats().getStat(c);
						if(oldStat>newStat)
							str.append(CharStats.STAT_DESCS[c].toLowerCase()+"-"+(oldStat-newStat)+", ");
						else
						if(newStat>oldStat)
							str.append(CharStats.STAT_DESCS[c].toLowerCase()+"+"+(newStat-oldStat)+", ");
					}
				}
				if(parms.containsKey("SENSES"))
				{
					if(!CMLib.flags().canHear(mob))
						str.append("deaf, ");
					if(!CMLib.flags().canSee(mob))
						str.append("blind, ");
					if(!CMLib.flags().canMove(mob))
						str.append("can't move, ");
					if(CMLib.flags().canSeeBonusItems(mob))
						str.append("detect magic, ");
					if(CMLib.flags().canSeeEvil(mob))
						str.append("detect evil, ");
					if(CMLib.flags().canSeeGood(mob))
						str.append("detect good, ");
					if(CMLib.flags().canSeeHidden(mob))
						str.append("see hidden, ");
					if(CMLib.flags().canSeeInDark(mob))
						str.append("darkvision, ");
					if(CMLib.flags().canSeeInfrared(mob))
						str.append("infravision, ");
					if(CMLib.flags().canSeeInvisible(mob))
						str.append("see invisible, ");
					if(CMLib.flags().canSeeMetal(mob))
						str.append("metalvision, ");
					if(CMLib.flags().canSeeSneakers(mob))
						str.append("see sneaking, ");
					if(!CMLib.flags().canSmell(mob))
						str.append("can't smell, ");
					if(!CMLib.flags().canSpeak(mob))
						str.append("can't speak, ");
					if(!CMLib.flags().canTaste(mob))
						str.append("can't eat, ");
				}
				if(parms.containsKey("DISPOSITIONS"))
				{
					if(CMLib.flags().isClimbing(mob))
						str.append("climbing, ");
					if((mob.envStats().disposition()&EnvStats.IS_EVIL)>0)
						str.append("evil, ");
					if(CMLib.flags().isFalling(mob))
						str.append("falling, ");
					if(CMLib.flags().isBound(mob))
						str.append("bound, ");
					if(CMLib.flags().isFlying(mob))
						str.append("flies, ");
					if((mob.envStats().disposition()&EnvStats.IS_GOOD)>0)
						str.append("good, ");
					if(CMLib.flags().isHidden(mob))
						str.append("hidden, ");
					if(CMLib.flags().isInDark(mob))
						str.append("darkness, ");
					if(CMLib.flags().isInvisible(mob))
						str.append("invisible, ");
					if(CMLib.flags().isGlowing(mob))
						str.append("glowing, ");
					if(CMLib.flags().isCloaked(mob))
						str.append("cloaked, ");
					if(!CMLib.flags().isSeen(mob))
						str.append("unseeable, ");
					if(CMLib.flags().isSitting(mob))
						str.append("crawls, ");
					if(CMLib.flags().isSleeping(mob))
						str.append("sleepy, ");
					if(CMLib.flags().isSneaking(mob))
						str.append("sneaks, ");
					if(CMLib.flags().isSwimming(mob))
						str.append("swims, ");
				}
				if(parms.containsKey("TRAINS"))
				{
					if(mob.getTrains()>0)
						str.append("trains+"+mob.getTrains()+", ");
				}
				if(parms.containsKey("EXPECTANCY"))
					str.append(""+R.getAgingChart()[Race.AGE_ANCIENT]+", ");
				if(parms.containsKey("PRACS"))
				{
					if(mob.getPractices()>0)
						str.append("practices+"+mob.getPractices()+", ");
				}
				if(parms.containsKey("ABILITIES"))
				{
					Vector ables=R.racialAbilities(null);
					for(int i=0;i<ables.size();i++)
					{
						Ability A=(Ability)ables.elementAt(i);
						if(A!=null)
						{
							if(A.proficiency()==0)
								str.append(A.Name()+", ");
							else
								str.append(A.Name()+"("+A.proficiency()+"%), ");
						}
					}

				}
				if(parms.containsKey("EFFECTS"))
				{
					Vector ables=R.racialEffects(null);
					for(int i=0;i<ables.size();i++)
					{
						Ability A=(Ability)ables.elementAt(i);
						if(A!=null)
							str.append(A.Name()+", ");
					}
				}
				if(parms.containsKey("LANGS"))
				{
					for(int i=0;i<mob.numLearnedAbilities();i++)
					{
						Ability A=mob.fetchAbility(i);
						if((A!=null)&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_LANGUAGE))
							if(A.proficiency()==0)
								str.append(A.Name()+", ");
							else
								str.append(A.Name()+"("+A.proficiency()+"%), ");
					}

				}
				if(parms.containsKey("STARTINGEQ"))
				{
					if(R.outfit(null)!=null)
					for(int i=0;i<R.outfit(null).size();i++)
					{
						Item I=(Item)R.outfit(null).elementAt(i);
						if(I!=null)
							str.append(I.Name()+", ");
					}
				}
				if(parms.containsKey("CLASSES"))
				{
					for(int i=0;i<CharStats.NUM_BASE_STATS;i++)
						mob.baseCharStats().setStat(i,25);
					mob.recoverCharStats();
					for(Enumeration c=CMClass.charClasses();c.hasMoreElements();)
					{
						CharClass C=(CharClass)c.nextElement();
						if((C!=null)
						&&(CMProps.isTheme(C.availabilityCode()))
						&&(C.qualifiesForThisClass(mob,true)))
							str.append(C.name()+", ");
					}
				}
				String strstr=str.toString();
				if(strstr.endsWith(", "))
					strstr=strstr.substring(0,strstr.length()-2);
                mob.destroy();
                mob2.destroy();
                return clearWebMacros(strstr);
			}
		}
		return "";
	}
}
