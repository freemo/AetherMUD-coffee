package com.planet_ink.coffee_mud.Races;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Grasshopper extends StdRace
{
	public String ID(){	return "Grasshopper"; }
	public String name(){ return "Grasshopper"; }
	public int shortestMale(){return 1;}
	public int shortestFemale(){return 1;}
	public int heightVariance(){return 0;}
	public int lightestWeight(){return 1;}
	public int weightVariance(){return 0;}
	public long forbiddenWornBits(){return Integer.MAX_VALUE-Item.ON_TORSO-Item.ON_EYES;}
	public String racialCategory(){return "Insect";}

	//                                an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={2 ,2 ,0 ,1 ,1 ,0 ,0 ,1 ,2 ,2 ,0 ,0 ,1 ,0 ,0 ,0 };
	public int[] bodyMask(){return parts;}

	protected static Vector resources=new Vector();
	public int availability(){return Race.AVAILABLE_MAGICONLY;}

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|EnvStats.IS_SNEAKING);
		affectableStats.setDisposition(affectableStats.disposition()|EnvStats.IS_GOLEM);
	}
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setPermaStat(CharStats.STRENGTH,3);
		affectableStats.setPermaStat(CharStats.DEXTERITY,3);
		affectableStats.setPermaStat(CharStats.INTELLIGENCE,1);
	}
	public String arriveStr()
	{
		return "hops in";
	}
	public String leaveStr()
	{
		return "hops";
	}
	public Weapon myNaturalWeapon()
	{
		if(naturalWeapon==null)
		{
			naturalWeapon=CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("a nasty maw");
			naturalWeapon.setWeaponType(Weapon.TYPE_NATURAL);
		}
		return naturalWeapon;
	}
	public Vector myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
				("a pair of "+name().toLowerCase()+" legs",EnvResource.RESOURCE_BONE));
			}
		}
		return resources;
	}
}