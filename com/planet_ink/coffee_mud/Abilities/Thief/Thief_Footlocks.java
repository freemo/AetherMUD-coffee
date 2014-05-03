package com.planet_ink.coffee_mud.Abilities.Thief;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
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
   Copyright 2000-2014 Bo Zimmerman

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
@SuppressWarnings("rawtypes")
public class Thief_Footlocks extends ThiefSkill
{
	@Override public String ID() { return "Thief_Footlocks"; }
	@Override public String name(){ return "Footlocks";}
	@Override public String displayText(){ return "(Footlocked)";}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_BINDING;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	private static final String[] triggerStrings = {"FOOTLOCK"};
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT;}
	public int code=0;
	public Item footlock=null;

	@Override public int abilityCode(){return code;}
	@Override public void setAbilityCode(int newCode){code=newCode;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected==null)||(!(affected instanceof MOB))||(invoker==null))
			return true;

		final MOB mob=(MOB)affected;
		if(msg.amISource(mob)
		&&(CMLib.dice().rollPercentage()>(mob.charStats().getStat(CharStats.STAT_DEXTERITY)-(getXLEVELLevel(mob)*3)))
		&&((msg.sourceMinor()==CMMsg.TYP_ADVANCE)||(msg.sourceMinor()==CMMsg.TYP_RETREAT)||(msg.sourceMinor()==CMMsg.TYP_FLEE)))
		{
			mob.location().show(mob,null,CMMsg.MSG_NOISYMOVEMENT,_("<S-NAME> stumble(s) in the footlocks."));
			return false;
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
		{
			if(!mob.amDead())
			{
				if((mob.location()!=null)&&(!mob.amDead()))
					mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,_("<S-NAME> break(s) free of the footlocks."));
			}
			if(footlock!=null)
			{
				footlock.destroy();
				footlock=null;
			}
		}
		super.unInvoke();
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if((mob!=null)&&(target!=null))
		{
			if(CMLib.flags().isSleeping(mob))
				return Ability.QUALITY_INDIFFERENT;
			if(!CMLib.flags().aliveAwakeMobileUnbound(mob,false))
				return Ability.QUALITY_INDIFFERENT;
			final Item cloth=CMLib.materials().findMostOfMaterial(mob,RawMaterial.MATERIAL_CLOTH);
			if((cloth==null)||CMLib.materials().findNumberOfResource(mob,cloth.material())<1)
				return Ability.QUALITY_INDIFFERENT;
			final Item wood=CMLib.materials().findMostOfMaterial(mob,RawMaterial.MATERIAL_WOODEN);
			if((wood==null)||CMLib.materials().findNumberOfResource(mob,wood.material())<2)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(CMLib.flags().isSleeping(mob))
		{
			mob.tell(_("You need to wake up!"));
			return false;
		}
		if(!CMLib.flags().aliveAwakeMobileUnbound(mob,false))
			return false;
		Item cloth=null;
		Item wood=null;
		if(!auto)
		{
			cloth=CMLib.materials().findMostOfMaterial(mob,RawMaterial.MATERIAL_CLOTH);
			if((cloth==null)||CMLib.materials().findNumberOfResource(mob,cloth.material())<1)
			{
				mob.tell(_("You need a pound of cloth to use this skill."));
				return false;
			}
			wood=CMLib.materials().findMostOfMaterial(mob,RawMaterial.MATERIAL_WOODEN);
			if((wood==null)||CMLib.materials().findNumberOfResource(mob,wood.material())<2)
			{
				mob.tell(_("You need two pounds of wood to use this skill."));
				return false;
			}
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int levelDiff=target.phyStats().level()-(mob.phyStats().level()+(2*getXLEVELLevel(mob))+abilityCode());
		if(levelDiff>0)
			levelDiff=levelDiff*5;
		else
			levelDiff=0;

		if(cloth!=null) CMLib.materials().destroyResourcesValue(mob,1,cloth.material(),-1,null);
		if(wood!=null) CMLib.materials().destroyResourcesValue(mob,2,wood.material(),-1,null);

		final boolean success=proficiencyCheck(mob,-levelDiff,auto);
		if(success)
		{
			final Item foots=CMClass.getItem("GenItem");
			foots.setRawWornCode(Wearable.WORN_FEET);
			foots.setName("a pair of footlock blocks");
			foots.setDisplayText("whats left of some footlocks");
			CMLib.flags().setRemovable(foots,false);
			CMLib.flags().setDroppable(foots,false);
			foots.setMaterial((wood!=null)?wood.material():RawMaterial.RESOURCE_WOOD);
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_MALICIOUS|CMMsg.MSG_THIEF_ACT,auto?"<T-NAME> can't seem to move <T-HIS-HER> feet!":"<S-NAME> throw(s) a pair of roped blocks at <T-YOUPOSS> feet!");
			final CMMsg msg2=CMClass.getMsg(mob,target,foots,CMMsg.MSG_THROW,null);
			if((mob.location().okMessage(mob,msg))&&(mob.location().okMessage(mob,msg2)))
			{
				mob.location().send(mob,msg);
				mob.location().send(mob,msg2);
				maliciousAffect(mob,target,asLevel,20+(getXLEVELLevel(mob)*3),-1);
				final Ability A=target.fetchEffect(ID());
				if((A!=null)&&(msg.value()<=0))
				{
					target.addItem(foots);
					foots.wearAt(Wearable.WORN_FEET);
					if(target.location()!=null) target.location().recoverRoomStats();
				}
			}
		}
		else
			return beneficialVisualFizzle(mob,target,"<S-NAME> throw(s) a pair of footlock blocks at <T-YOUPOSS> feet and miss(es).");
		return success;
	}
}
