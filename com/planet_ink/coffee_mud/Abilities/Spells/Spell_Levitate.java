package com.planet_ink.coffee_mud.Abilities.Spells;
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
public class Spell_Levitate extends Spell
{
	@Override public String ID() { return "Spell_Levitate"; }
	@Override public String name(){return "Levitate";}
	@Override public String displayText(){return "(Levitated)";}
	@Override public int maxRange(){return adjustedMaxInvokerRange(5);}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS|CAN_ITEMS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;}
	@Override public long flags(){return Ability.FLAG_MOVING;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if(msg.amISource(mob))
		{
			if((msg.sourceMinor()==CMMsg.TYP_ADVANCE)
			||(msg.sourceMinor()==CMMsg.TYP_RETREAT)
			||(msg.sourceMinor()==CMMsg.TYP_LEAVE)
			||(msg.sourceMinor()==CMMsg.TYP_ENTER)
			||(msg.sourceMinor()==CMMsg.TYP_RETREAT))
			{
				mob.tell(_("You can't seem to go anywhere!"));
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_FLYING);
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
		{
			super.unInvoke();
			return;
		}
		final MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
		{
			mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,"<S-NAME> float(s) back down.");
			CMLib.commands().postStand(mob,true);
		}
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if((mob.isMonster())&&(mob.isInCombat()))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_UNWORNONLY);
		if(target==null) return false;
		if(target instanceof Item)
		{
			if(mob.isMine(target))
			{
				mob.tell(_("You'd better set it down first!"));
				return false;
			}
		}
		else
		if(target instanceof MOB)
		{
		}
		else
		{
			mob.tell("You can't levitate "+target.name(mob)+"!");
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,somanticCastCode(mob,target,auto),auto?"":"^S<S-NAME> wave(s) <S-HIS-HER> arms and cast(s) a spell.^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					success=maliciousAffect(mob,target,asLevel,5+super.getXLEVELLevel(mob),-1);
					if(target instanceof MOB)
						((MOB)target).location().show((MOB)target,null,CMMsg.MSG_OK_ACTION,"<S-NAME> float(s) straight up!");
					else
						mob.location().showHappens(CMMsg.MSG_OK_ACTION,target.name()+" float(s) straight up!");
				}
			}
		}
		else
			return maliciousFizzle(mob,null,"<S-NAME> incant(s), but the spell fizzles.");
		// return whether it worked
		return success;
	}
}
