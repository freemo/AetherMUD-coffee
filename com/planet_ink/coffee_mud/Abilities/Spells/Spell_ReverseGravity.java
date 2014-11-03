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
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;


import java.util.*;

/*
   Copyright 2002-2014 Bo Zimmerman

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
@SuppressWarnings({"unchecked","rawtypes"})
public class Spell_ReverseGravity extends Spell
{
	@Override public String ID() { return "Spell_ReverseGravity"; }
	private final static String localizedName = CMLib.lang().L("Reverse Gravity");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Gravity is Reversed)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	protected Vector childrenAffects=new Vector();
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_ALTERATION;}
	@Override public long flags(){return Ability.FLAG_MOVING;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		else
		if((affected!=null)&&(affected instanceof Room)&&(invoker!=null))
		{
			final Room room=(Room)affected;
			for(int i=0;i<room.numInhabitants();i++)
			{
				final MOB inhab=room.fetchInhabitant(i);
				if(!CMLib.flags().isInFlight(inhab))
				{
					inhab.makePeace();
					Ability A=CMClass.getAbility("Falling");
					A.setAffectedOne(null);
					A.setProficiency(100);
					A.invoke(null,null,inhab,true,0);
					A=inhab.fetchEffect("Falling");
					if(A!=null)
						childrenAffects.addElement(A);
				}
			}
			for(int i=0;i<room.numItems();i++)
			{
				final Item inhab=room.getItem(i);
				if((inhab!=null)
				&&(inhab.container()==null)
				&&(!CMLib.flags().isInFlight(inhab.ultimateContainer(null))))
				{
					Ability A=CMClass.getAbility("Falling");
					A.setAffectedOne(room);
					A.setProficiency(100);
					A.invoke(null,null,inhab,true,0);
					A=inhab.fetchEffect("Falling");
					if(A!=null)
						childrenAffects.addElement(A);
				}
			}
		}
		return true;
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(affected==null)
			return;
		if(canBeUninvoked())
		{
			if(affected instanceof Room)
			{
				final Room room=(Room)affected;
				room.showHappens(CMMsg.MSG_OK_VISUAL, L("Gravity returns to normal..."));
				if(invoker!=null)
				{
					final Ability me=invoker.fetchEffect(ID());
					if(me!=null) me.setProficiency(0);
				}
			}
			else
			if(affected instanceof MOB)
			{
				final MOB mob=(MOB)affected;
				if(mob.location()!=null)
				{
					mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("Gravity returns to normal.."));
					final Ability me=mob.location().fetchEffect(ID());
					if(me!=null) me.setProficiency(0);
				}
			}
			while(childrenAffects.size()>0)
			{
				final Ability A=(Ability)childrenAffects.elementAt(0);
				A.setProficiency(0);
				childrenAffects.removeElement(A);
			}
		}
		super.unInvoke();
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.location().fetchEffect(this.ID())!=null)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Physical target = mob.location();

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(mob,null,null,L("Gravity has already been reversed here!"));
			return false;
		}


		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.

			final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob,target,auto), L((auto?"G":"^S<S-NAME> speak(s) and wave(s) and g")+"ravity begins to reverse!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				childrenAffects=new Vector();
				mob.location().send(mob,msg);
				beneficialAffect(mob,mob.location(),asLevel,4+(super.getXLEVELLevel(mob)/3));
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> speak(s) in reverse, but the spell fizzles."));

		// return whether it worked
		return success;
	}
}
