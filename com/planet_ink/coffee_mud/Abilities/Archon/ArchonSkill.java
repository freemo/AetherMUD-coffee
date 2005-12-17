package com.planet_ink.coffee_mud.Abilities.Archon;
import com.planet_ink.coffee_mud.Abilities.StdAbility;
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
   Copyright 2000-2005 Bo Zimmerman

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

public class ArchonSkill extends StdAbility
{
	public String ID() { return "ArchonSkill"; }
	public String name(){ return "an Archon Skill";}
	public String displayText(){return "(in the realms of greatest power)";}
	public boolean putInCommandlist(){return false;}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return 0;}

	public int classificationCode()
	{ return Ability.SKILL;	}

	public MOB getTargetAnywhere(MOB mob, Vector commands, Environmental givenTarget, boolean playerOnly)
	{ return getTargetAnywhere(mob,commands,givenTarget,false,false,playerOnly);	}

	public MOB getTargetAnywhere(MOB mob, Vector commands, Environmental givenTarget, boolean quiet, boolean alreadyAffOk, boolean playerOnly)
	{
		MOB target=super.getTarget(mob,commands,givenTarget,true,alreadyAffOk);
		if(target!=null) return target;
		
		String targetName=CMParms.combine(commands,0);
		if((givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		else
		if((targetName.length()==0)&&(mob.isInCombat())&&(quality()==Ability.MALICIOUS)&&(mob.getVictim()!=null))
		   target=mob.getVictim();
		else
		if((targetName.length()==0)&&(quality()!=Ability.MALICIOUS))
			target=mob;
		else
		if(targetName.equalsIgnoreCase("self")||targetName.equalsIgnoreCase("me"))
		   target=mob;
		else
		if(targetName.length()>0)
		{
		    try
		    {
				for(Enumeration e=CMLib.map().rooms();e.hasMoreElements();)
				{
					Room R=(Room)e.nextElement();
					if(R!=mob.location())
						target=R.fetchInhabitant(targetName);
				}
		    }
		    catch(NoSuchElementException e){}
		}

		if((target==null)||((playerOnly)&&(target.isMonster())))
		{
			if(CMLib.database().DBUserSearch(null,targetName))
				target=CMLib.map().getLoadPlayer(targetName);
		}
		
		if((target!=null)&&((!playerOnly)||(!target.isMonster())))
			targetName=target.name();

		
		if(((target==null)||((playerOnly)&&(target.isMonster())))
		||((givenTarget==null)&&(!CMLib.flags().canBeSeenBy(target,mob))&&((!CMLib.flags().canBeHeardBy(target,mob))||(!target.isInCombat()))))
		{
			if(!quiet)
			{
				if(targetName.trim().length()==0)
					mob.tell("You don't know of anyone called '"+targetName+"'.");
				else
					mob.tell("You don't know of anyone called '"+targetName+"' here.");
			}
			return null;
		}

		if((!alreadyAffOk)&&(!isAutoInvoked())&&(target.fetchEffect(this.ID())!=null))
		{
			if((givenTarget==null)&&(!quiet))
			{
				if(target==mob)
					mob.tell("You are already affected by "+name()+".");
				else
					mob.tell(target,null,null,"<S-NAME> is already affected by "+name()+".");
			}
			return null;
		}
		return target;
	}

}
