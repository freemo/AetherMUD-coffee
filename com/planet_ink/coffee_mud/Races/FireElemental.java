package com.planet_ink.coffee_mud.Races;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
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
public class FireElemental extends StdRace
{
	public String ID(){	return "FireElemental"; }
	public String name(){ return "Fire Elemental"; }
	public int shortestMale(){return 64;}
	public int shortestFemale(){return 60;}
	public int heightVariance(){return 12;}
	public int lightestWeight(){return 400;}
	public int weightVariance(){return 100;}
	public long forbiddenWornBits(){return 0;}
	public String racialCategory(){return "Fire Elemental";}
	public boolean fertile(){return false;}
	public boolean uncharmable(){return true;}
	protected boolean destroyBodyAfterUse(){return true;}
	private String[]culturalAbilityNames={"Ignan"};
	private int[]culturalAbilityProfficiencies={100};
	public String[] culturalAbilityNames(){return culturalAbilityNames;}
	public int[] culturalAbilityProfficiencies(){return culturalAbilityProfficiencies;}

	protected static Vector resources=new Vector();
	public int availabilityCode(){return Area.THEME_FANTASY|Area.THEME_SKILLONLYMASK;}

	//                                an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
	public int[] bodyMask(){return parts;}

	private int[] agingChart={0,0,0,0,0,Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE};
	public int[] getAgingChart(){return agingChart;}
	
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.SAVE_POISON,affectableStats.getStat(CharStats.SAVE_POISON)+100);
		affectableStats.setStat(CharStats.SAVE_DISEASE,affectableStats.getStat(CharStats.SAVE_DISEASE)+100);
		affectableStats.setStat(CharStats.SAVE_FIRE,affectableStats.getStat(CharStats.SAVE_FIRE)+100);
	}
	public Weapon myNaturalWeapon()
	{
		if(naturalWeapon==null)
		{
			naturalWeapon=CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("an arm of flame");
			naturalWeapon.setWeaponType(Weapon.TYPE_BURNING);
		}
		return naturalWeapon;
	}

	public String healthText(MOB mob)
	{
		double pct=(Util.div(mob.curState().getHitPoints(),mob.maxState().getHitPoints()));

		if(pct<.10)
			return "^r" + mob.name() + "^r is almost put out!^N";
		else
		if(pct<.20)
			return "^r" + mob.name() + "^r is flickering alot and is almost smoked out.^N";
		else
		if(pct<.30)
			return "^r" + mob.name() + "^r is flickering alot and smoking massively.^N";
		else
		if(pct<.40)
			return "^y" + mob.name() + "^y is flickering alot and smoking a lot.^N";
		else
		if(pct<.50)
			return "^y" + mob.name() + "^y is flickering and smoking.^N";
		else
		if(pct<.60)
			return "^p" + mob.name() + "^p is flickering and smoking somewhat.^N";
		else
		if(pct<.70)
			return "^p" + mob.name() + "^p is showing large flickers.^N";
		else
		if(pct<.80)
			return "^g" + mob.name() + "^g is showing some flickers.^N";
		else
		if(pct<.90)
			return "^g" + mob.name() + "^g is showing small flickers.^N";
		else
		if(pct<.99)
			return "^g" + mob.name() + "^g is no longer in perfect condition.^N";
		else
			return "^c" + mob.name() + "^c is in perfect condition.^N";
	}
	public Vector myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
					("a pile of coal",EnvResource.RESOURCE_COAL));
			}
		}
		return resources;
	}
}
