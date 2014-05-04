package com.planet_ink.coffee_mud.Abilities.Druid;
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
public class Chant_Tether extends Chant
{
	@Override public String ID() { return "Chant_Tether"; }
	@Override public String name(){ return "Tether";}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PRESERVING;}
	@Override public String displayText(){ return "(Tether)";}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	public Room tetheredTo=null;
	public Room lastRoom=null;


	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if((affected!=null)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			if((lastRoom!=mob.location())
			&&(lastRoom!=null))
				tetheredTo=lastRoom;
			lastRoom=mob.location();

			if(msg.amISource(mob)
			&&(msg.target()==null)
			&&(msg.tool()==null)
			&&(msg.sourceMinor()==CMMsg.TYP_DEATH)
			&&(mob.curState().getHitPoints()>0))
			{
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,_("<S-NAME> <S-IS-ARE> pulled back by the tether!"));
				if((tetheredTo!=null)&&(tetheredTo!=mob.location()))
					tetheredTo.bringMobHere(mob,false);
				return false;
			}
		}
		return super.okMessage(host,msg);
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			mob.tell(_("Your tether has left you."));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			if((lastRoom!=mob.location())
			&&(lastRoom!=null))
				tetheredTo=lastRoom;
			lastRoom=mob.location();
			if(mob.fetchEffect("Falling")!=null)
			{
				mob.tell(_("The tether keeps you from falling!"));
				mob.delEffect(mob.fetchEffect("Falling"));
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(target,null,null,_("<S-NAME> <S-IS-ARE> already tethered."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?_("<T-NAME> become(s) magically tethered!"):_("^S<S-NAME> chant(s) about a magical tether!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				lastRoom=mob.location();
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,null,_("<S-NAME> chant(s) about a magical tether, but the magic fades."));


		// return whether it worked
		return success;
	}
}
