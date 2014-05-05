package com.planet_ink.coffee_mud.Abilities.Fighter;
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
public class Fighter_SizeOpponent extends FighterSkill
{
	@Override public String ID() { return "Fighter_SizeOpponent"; }
	private final static String localizedName = CMLib.lang()._("Opponent Knowledge");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =_i(new String[] {"SIZEUP","OPPONENT"});
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_COMBATLORE;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_LOOK|(auto?CMMsg.MASK_ALWAYS:0),_("<S-NAME> size(s) up <T-NAMESELF> with <S-HIS-HER> eyes."));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final StringBuffer buf=new StringBuffer(_("@x1 looks to have @x2 out of @x3 hit points.\n\r",target.name(mob),""+target.curState().getHitPoints(),""+target.maxState().getHitPoints()));
				buf.append(_("@x1 looks like @x2 is @x3 and is @x4.",target.charStats().HeShe(),target.charStats().heshe(),CMStrings.removeColors(CMLib.combat().fightingProwessStr(target)),CMStrings.removeColors(CMLib.combat().armorStr(target))));
				mob.tell(buf.toString());
			}
		}
		else
			return beneficialVisualFizzle(mob,target,_("<S-NAME> size(s) up <T-NAMESELF> with <S-HIS-HER> eyes, but look(s) confused."));

		// return whether it worked
		return success;
	}
}
