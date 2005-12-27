package com.planet_ink.coffee_mud.Items.MiscMagic;
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
public class GenSuperPill extends GenPill
{
	public String ID(){	return "GenSuperPill";}
	public GenSuperPill()
	{
		super();

		setName("a pill");
		baseEnvStats.setWeight(1);
		setDisplayText("A strange pill lies here.");
		setDescription("Large and round, with strange markings.");
		secretIdentity="";
		baseGoldValue=200;
		recoverEnvStats();
		material=RawMaterial.RESOURCE_CORN;
	}


	public boolean isGeneric(){return true;}

	public String secretIdentity()
	{
		String tx=StdScroll.makeSecretIdentity("super pill",super.secretIdentity(),"",getSpells());
		String id=readableText;
		int x=id.toUpperCase().indexOf("ARM");
		for(StringBuffer ID=new StringBuffer(id);((x>0)&&(x<id.length()));x++)
			if(id.charAt(x)=='-')
			{
				ID.setCharAt(x,'+');
				id=ID.toString();
				break;
			}
			else
			if(id.charAt(x)=='+')
			{
				ID.setCharAt(x,'-');
				id=ID.toString();
				break;
			}
			else
			if(Character.isDigit(id.charAt(x)))
				break;
		x=id.toUpperCase().indexOf("DIS");
		if(x>=0)
		{
			long val=CMParms.getParmPlus(id,"dis");
			int y=id.indexOf(""+val,x);
			if((val!=0)&&(y>x))
			{
				StringBuffer middle=new StringBuffer("");
				for(int num=0;num<EnvStats.dispositionsVerb.length;num++)
					if(CMath.bset(val,CMath.pow(2,num)))
						middle.append(EnvStats.dispositionsVerb[num]+" ");
				id=id.substring(0,x)+middle.toString().trim()+id.substring(y+((""+val).length()));
			}
		}
		x=id.toUpperCase().indexOf("SEN");
		if(x>=0)
		{
			long val=CMParms.getParmPlus(id,"sen");
			int y=id.indexOf(""+val,x);
			if((val!=0)&&(y>x))
			{
				StringBuffer middle=new StringBuffer("");
				for(int num=0;num<EnvStats.sensesVerb.length;num++)
					if(CMath.bset(val,CMath.pow(2,num)))
						middle.append(EnvStats.sensesVerb[num]+" ");
				id=id.substring(0,x)+middle.toString().trim()+id.substring(y+((""+val).length()));
			}
		}
		return tx+"\n("+id+")\n";
	}

	public void EATME(MOB mob)
	{
		boolean redress=false;
		if(getSpells().size()>0)
			eatIfAble(mob);
		if((CMParms.getParmPlus(readableText,"beacon")>0)
		&&(mob.location()!=null))
			mob.setStartRoom(mob.location());
		mob.baseEnvStats().setAbility(mob.baseEnvStats().ability()+CMParms.getParmPlus(readableText,"abi"));
		mob.baseEnvStats().setArmor(mob.baseEnvStats().armor()+CMParms.getParmPlus(readableText,"arm"));
		mob.baseEnvStats().setAttackAdjustment(mob.baseEnvStats().attackAdjustment()+CMParms.getParmPlus(readableText,"att"));
		mob.baseEnvStats().setDamage(mob.baseEnvStats().damage()+CMParms.getParmPlus(readableText,"dam"));
		mob.baseEnvStats().setDisposition(mob.baseEnvStats().disposition()|CMParms.getParmPlus(readableText,"dis"));
		mob.baseEnvStats().setLevel(mob.baseEnvStats().level()+CMParms.getParmPlus(readableText,"lev"));
		mob.baseEnvStats().setRejuv(mob.baseEnvStats().rejuv()+CMParms.getParmPlus(readableText,"rej"));
		mob.baseEnvStats().setSensesMask(mob.baseEnvStats().sensesMask()|CMParms.getParmPlus(readableText,"sen"));
		mob.baseEnvStats().setSpeed(mob.baseEnvStats().speed()+CMParms.getParmPlus(readableText,"spe"));
		mob.baseEnvStats().setWeight(mob.baseEnvStats().weight()+CMParms.getParmPlus(readableText,"wei"));
		if(CMParms.getParmPlus(readableText,"wei")!=0) redress=true;
		mob.baseEnvStats().setHeight(mob.baseEnvStats().height()+CMParms.getParmPlus(readableText,"hei"));
		if(CMParms.getParmPlus(readableText,"hei")!=0) redress=true;

		String val=CMParms.getParmStr(readableText,"gen","").toUpperCase();
		if((val.length()>0)&&((val.charAt(0)=='M')||(val.charAt(0)=='F')||(val.charAt(0)=='N')))
			mob.baseCharStats().setStat(CharStats.STAT_GENDER,val.charAt(0));
		val=CMParms.getParmStr(readableText,"cla","").toUpperCase();
		if((val.length()>0)&&(CMClass.findCharClass(val)!=null)&&(!val.equalsIgnoreCase("Archon")))
		{
			mob.baseCharStats().setCurrentClass(CMClass.findCharClass(val));
			if((!mob.isMonster())&&(mob.soulMate()==null))
				CMLib.coffeeTables().bump(mob,CoffeeTableRow.STAT_CLASSCHANGE);
		}
		if(CMParms.getParmPlus(readableText,"lev")!=0)
			mob.baseCharStats().setClassLevel(mob.baseCharStats().getCurrentClass(),mob.baseCharStats().getClassLevel(mob.baseCharStats().getCurrentClass())+CMParms.getParmPlus(readableText,"lev"));
		val=CMParms.getParmStr(readableText,"rac","").toUpperCase();
		if((val.length()>0)&&(CMClass.getRace(val)!=null))
		{
			redress=true;
			mob.baseCharStats().setMyRace(CMClass.getRace(val));
			mob.baseCharStats().getMyRace().startRacing(mob,false);
		}
		mob.baseCharStats().setStat(CharStats.STAT_STRENGTH,mob.baseCharStats().getStat(CharStats.STAT_STRENGTH)+CMParms.getParmPlus(readableText,"str"));
		mob.baseCharStats().setStat(CharStats.STAT_WISDOM,mob.baseCharStats().getStat(CharStats.STAT_WISDOM)+CMParms.getParmPlus(readableText,"wis"));
		mob.baseCharStats().setStat(CharStats.STAT_CHARISMA,mob.baseCharStats().getStat(CharStats.STAT_CHARISMA)+CMParms.getParmPlus(readableText,"cha"));
		mob.baseCharStats().setStat(CharStats.STAT_CONSTITUTION,mob.baseCharStats().getStat(CharStats.STAT_CONSTITUTION)+CMParms.getParmPlus(readableText,"con"));
		mob.baseCharStats().setStat(CharStats.STAT_DEXTERITY,mob.baseCharStats().getStat(CharStats.STAT_DEXTERITY)+CMParms.getParmPlus(readableText,"dex"));
		mob.baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,mob.baseCharStats().getStat(CharStats.STAT_INTELLIGENCE)+CMParms.getParmPlus(readableText,"int"));
		mob.baseCharStats().setStat(CharStats.STAT_MAX_STRENGTH_ADJ,mob.baseCharStats().getStat(CharStats.STAT_MAX_STRENGTH_ADJ)+CMParms.getParmPlus(readableText,"maxstr"));
		mob.baseCharStats().setStat(CharStats.STAT_MAX_WISDOM_ADJ,mob.baseCharStats().getStat(CharStats.STAT_MAX_WISDOM_ADJ)+CMParms.getParmPlus(readableText,"maxwis"));
		mob.baseCharStats().setStat(CharStats.STAT_MAX_CHARISMA_ADJ,mob.baseCharStats().getStat(CharStats.STAT_MAX_CHARISMA_ADJ)+CMParms.getParmPlus(readableText,"maxcha"));
		mob.baseCharStats().setStat(CharStats.STAT_MAX_CONSTITUTION_ADJ,mob.baseCharStats().getStat(CharStats.STAT_MAX_CONSTITUTION_ADJ)+CMParms.getParmPlus(readableText,"maxcon"));
		mob.baseCharStats().setStat(CharStats.STAT_MAX_DEXTERITY_ADJ,mob.baseCharStats().getStat(CharStats.STAT_MAX_DEXTERITY_ADJ)+CMParms.getParmPlus(readableText,"maxdex"));
		mob.baseCharStats().setStat(CharStats.STAT_MAX_INTELLIGENCE_ADJ,mob.baseCharStats().getStat(CharStats.STAT_MAX_INTELLIGENCE_ADJ)+CMParms.getParmPlus(readableText,"maxint"));

		mob.baseState().setHitPoints(mob.baseState().getHitPoints()+CMParms.getParmPlus(readableText,"hit"));
		mob.curState().setHunger(mob.curState().getHunger()+CMParms.getParmPlus(readableText,"hun"));
		mob.baseState().setMana(mob.baseState().getMana()+CMParms.getParmPlus(readableText,"man"));
		mob.baseState().setMovement(mob.baseState().getMovement()+CMParms.getParmPlus(readableText,"mov"));
		mob.curState().setThirst(mob.curState().getThirst()+CMParms.getParmPlus(readableText,"thi"));

		mob.setPractices(mob.getPractices()+CMParms.getParmPlus(readableText,"prac"));
		mob.setTrains(mob.getTrains()+CMParms.getParmPlus(readableText,"trai"));
		mob.setQuestPoint(mob.getQuestPoint()+CMParms.getParmPlus(readableText,"ques"));
		int newMoney=CMParms.getParmPlus(readableText,"coin");
		if(newMoney!=0) CMLib.beanCounter().setMoney(mob,CMLib.beanCounter().getMoney(mob)+newMoney);
		int exp=CMParms.getParmPlus(readableText,"expe");
		if(exp!=0) CMLib.combat().postExperience(mob,null,null,exp,false);
		mob.recoverCharStats();
		mob.recoverEnvStats();
		mob.recoverMaxState();
		if(redress)	mob.confirmWearability();
	}

	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_EAT:
				if((msg.sourceMessage()==null)&&(msg.othersMessage()==null))
				{
					EATME(mob);
					super.executeMsg(myHost,msg);
				}
				else
					msg.addTrailerMsg(CMClass.getMsg(msg.source(),msg.target(),msg.tool(),CMMsg.NO_EFFECT,null,msg.targetCode(),msg.targetMessage(),CMMsg.NO_EFFECT,null));
				break;
			default:
				super.executeMsg(myHost,msg);
				break;
			}
		}
		else
			super.executeMsg(myHost,msg);
	}
}
