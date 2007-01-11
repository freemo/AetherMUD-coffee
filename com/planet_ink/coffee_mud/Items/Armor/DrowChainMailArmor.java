package com.planet_ink.coffee_mud.Items.Armor;
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


/* 
   Copyright 2000-2007 Bo Zimmerman

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
public class DrowChainMailArmor extends StdArmor
{
	public String ID(){	return "DrowChainMailArmor";}
	public DrowChainMailArmor()
	{
		super();

		setName("a suit of dark chain mail armor");
		setDisplayText("a suit of chain mail armor made of dark material sits here.");
		setDescription("This suit includes a fairly solid looking hauberk with leggings and a coif, all constructed from a strong, dark metal.");
        secretIdentity="A suit of Drow Chain Mail Armor";
		properWornBitmap=Item.WORN_TORSO | Item.WORN_ARMS | Item.WORN_LEGS;
		wornLogicalAnd=true;
		baseEnvStats().setArmor(65);
		baseEnvStats().setWeight(60);
		baseEnvStats().setAbility(0);
		baseGoldValue=1500;
		recoverEnvStats();
		material=RawMaterial.RESOURCE_STEEL;
	}

}
