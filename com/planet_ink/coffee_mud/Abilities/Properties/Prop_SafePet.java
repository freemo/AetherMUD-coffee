package com.planet_ink.coffee_mud.Abilities.Properties;
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
public class Prop_SafePet extends Property
{
	boolean disabled=false;
	public String ID() { return "Prop_SafePet"; }
	public String name(){ return "Unattackable Pets";}
	protected int canAffectCode(){return Ability.CAN_MOBS;}

	public String accountForYourself()
	{ return "Unattackable";	}

	public boolean okMessage(Environmental myHost, CMMsg msg)
	{
		if((CMath.bset(msg.targetCode(),CMMsg.MASK_MALICIOUS)
        &&(msg.target()==affected)
        &&(affected instanceof MOB)
        &&(!disabled)))
		{
            if(!CMath.bset(msg.sourceCode(),CMMsg.MASK_GENERAL))
    			msg.source().tell("Ah, leave "+affected.name()+" alone.");
            ((MOB)affected).makePeace();
			return false;
		}
		else
		if((affected!=null)&&(affected instanceof MOB)&&(CMath.bset(msg.targetCode(),CMMsg.MASK_MALICIOUS))&&(msg.amISource((MOB)affected)))
			disabled=true;
		return super.okMessage(myHost,msg);
	}
}
