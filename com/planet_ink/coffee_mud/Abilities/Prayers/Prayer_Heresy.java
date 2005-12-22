package com.planet_ink.coffee_mud.Abilities.Prayers;
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

public class Prayer_Heresy extends Prayer
{
	public String ID() { return "Prayer_Heresy"; }
	public String name(){return "Heresy";}
	protected int canTargetCode(){return Ability.CAN_MOBS;}
	protected int canAffectCode(){return 0;}
	public int quality(){ return MALICIOUS;}
	public long flags(){return Ability.FLAG_UNHOLY;}
	protected int overrideMana(){return 100;}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{
        LegalBehavior B=null;
		if(mob.location()!=null) B=CMLib.utensils().getLegalBehavior(mob.location());

		MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=profficiencyCheck(mob,0,auto);

		MOB oldVictim=mob.getVictim();
		if((success)&&(B!=null))
		{
			CMMsg msg=CMClass.getMsg(mob,target,this,affectType(auto),auto?"":"^S<S-NAME> accuse(s) <T-NAMESELF> of heresy"+againstTheGods(mob)+"!^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					MOB D=null;
					if(mob.getWorshipCharID().length()>0) D=CMLib.map().getDeity(mob.getWorshipCharID());
                    String crime="heresy against <T-NAME>";
                    String desc=null;
					if(D==null)
                    { 
                        D=target; 
                        crime="heresy against the gods";
                        desc="Angering the gods will bring doom upon us all!";
                    }
                    else
                        desc="Angering "+D.name()+" will bring doom upon us all!";
                    String crimeLocs="";
                    String crimeFlags="!witness";
					int low=CMLib.ableMapper().lowestQualifyingLevel(ID());
					int me=CMLib.ableMapper().qualifyingClassLevel(mob,this);
					int lvl=(me-low)/5;
					if(lvl<0) lvl=0;
					if(lvl>Law.ACTION_HIGHEST) lvl=Law.ACTION_HIGHEST;
                    String sentence=Law.ACTION_DESCS[lvl];
					B.addWarrant(CMLib.utensils().getLegalObject(mob.location()),target,D,crimeLocs,crimeFlags,crime,sentence,desc);
				}
			}

		}
		else
			beneficialWordsFizzle(mob,target,"<S-NAME> accuse(s) <T-NAMESELF> of heresy"+againstTheGods(mob)+", but nothing happens.");
		mob.setVictim(oldVictim);
		if(oldVictim==null) mob.makePeace();

		// return whether it worked
		return success;
	}
}
