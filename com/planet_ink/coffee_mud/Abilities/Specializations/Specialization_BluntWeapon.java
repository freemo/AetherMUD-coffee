package com.planet_ink.coffee_mud.Abilities.Specializations;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;


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
public class Specialization_BluntWeapon extends Specialization_Weapon
{
	public String ID() { return "Specialization_BluntWeapon"; }
	public String name(){ return "Blunt Weapon Specialization";}
	public Specialization_BluntWeapon()
	{
		super();
		weaponType=Weapon.CLASS_BLUNT;
		secondWeaponType=Weapon.CLASS_STAFF;
	}

}
