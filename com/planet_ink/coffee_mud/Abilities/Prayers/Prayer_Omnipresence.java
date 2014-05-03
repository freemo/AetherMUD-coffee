package com.planet_ink.coffee_mud.Abilities.Prayers;
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
import com.planet_ink.coffee_mud.Libraries.interfaces.TrackingLibrary;
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
public class Prayer_Omnipresence extends Prayer
{
	@Override public String ID() { return "Prayer_Omnipresence"; }
	@Override public String name(){return "Omnipresence";}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){ return Ability.ACODE_PRAYER|Ability.DOMAIN_COMMUNING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		final boolean success=proficiencyCheck(mob,0,auto);
		if(!success)
			this.beneficialVisualFizzle(mob,null,"<S-NAME> "+prayWord(mob)+", but the is unanswered.");
		else
		{
			final CMMsg msg=CMClass.getMsg(mob,null,null,verbalCastCode(mob,null,auto),"^S<S-NAME> "+prayWord(mob)+" for the power of omnipresence.^?");
			int numLayers=super.getXLEVELLevel(mob) + 1;
			if(CMLib.ableMapper().qualifyingLevel(mob, this)>1)
				numLayers += ((super.adjustedLevel(mob, 0) / CMLib.ableMapper().qualifyingLevel(mob, this)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Room thatRoom=mob.location();
				final TrackingLibrary.TrackingFlags flags = new TrackingLibrary.TrackingFlags()
														.plus(TrackingLibrary.TrackingFlag.NOAIR)
														.plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS)
														.plus(TrackingLibrary.TrackingFlag.NOWATER);
				mob.tell(_("Your mind is filled with visions as your presence expands...."));
				final List<Room> list = CMLib.tracking().getRadiantRooms(thatRoom, flags, numLayers);
				for(final Room R : list)
				{
					if(CMLib.flags().canAccess(mob, R))
					{
						final CMMsg msg2=CMClass.getMsg(mob,R,CMMsg.MSG_LOOK,null);
						R.executeMsg(mob,msg2);
					}
				}
			}
		}

		return success;
	}
}
