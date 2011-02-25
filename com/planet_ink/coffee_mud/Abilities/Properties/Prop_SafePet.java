package com.planet_ink.coffee_mud.Abilities.Properties;
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
   Copyright 2000-2011 Bo Zimmerman

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
public class Prop_SafePet extends Property
{
	public String ID() { return "Prop_SafePet"; }
	public String name(){ return "Unattackable Pets";}
	protected int canAffectCode(){return Ability.CAN_MOBS;}
	boolean disabled=false;

	public String accountForYourself()
	{ return "Unattackable";	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(affected instanceof MOB)
		{
			if(CMath.bset(msg.targetCode(),CMMsg.MASK_MALICIOUS))
			{
		        if((msg.amITarget(affected))
		        &&(!disabled))
				{
		            if(!CMath.bset(msg.sourceCode(),CMMsg.MASK_ALWAYS))
		    			msg.source().tell("Ah, leave "+affected.name()+" alone.");
		            ((MOB)affected).makePeace();
					return false;
				}
				else
				if(msg.amISource((MOB)affected))
					disabled=true;
			}
			else
			if(!((MOB)affected).isInCombat())
				disabled=false;
		}
		else
		if(CMath.bset(msg.targetCode(),CMMsg.MASK_MALICIOUS) && msg.amITarget(affected))
		{
            if(!CMath.bset(msg.sourceCode(),CMMsg.MASK_ALWAYS))
    			msg.source().tell("Ah, leave "+affected.name()+" alone.");
			return false;
		}
		return super.okMessage(myHost,msg);
	}
}
