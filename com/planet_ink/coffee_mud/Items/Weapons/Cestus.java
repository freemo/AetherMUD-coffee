package com.planet_ink.coffee_mud.Items.Weapons;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;

/* 
   Copyright 2000-2004 Bo Zimmerman

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
public class Cestus extends StdWeapon
{
	public String ID(){	return "Cestus";}
	public Cestus()
	{
		super();

		setName("a mean looking cestus");
		setDisplayText("a cestus is on the gound.");
		setDescription("It\\`s a glove covered in long spikes and blades.");
		baseEnvStats().setAbility(0);
		baseEnvStats().setLevel(0);
		baseEnvStats.setWeight(2);
		baseEnvStats().setAttackAdjustment(0);
		baseEnvStats().setDamage(4);
		material=EnvResource.RESOURCE_LEATHER;
		baseGoldValue=5;
		recoverEnvStats();
		weaponType=Weapon.TYPE_PIERCING;
		weaponClassification=Weapon.CLASS_EDGED;

	}



}
