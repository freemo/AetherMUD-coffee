package com.planet_ink.coffee_mud.Races;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Chimp extends Monkey
{
	protected static Vector resources=new Vector();
	public Chimp()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		name="Chimp";
		// inches
		shortestMale=36;
		shortestFemale=34;
		heightVariance=8;
		// pounds
		lightestWeight=80;
		weightVariance=50;
	}
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.STRENGTH,15);
		affectableStats.setStat(CharStats.DEXTERITY,15);
		affectableStats.setStat(CharStats.INTELLIGENCE,1);
	}
	public Vector myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
				("a "+name.toLowerCase()+" hide",EnvResource.RESOURCE_FUR));
				resources.addElement(makeResource
				("some "+name.toLowerCase()+" toes",EnvResource.RESOURCE_HIDE));
				resources.addElement(makeResource
				("some "+name.toLowerCase()+" flesh",EnvResource.RESOURCE_MEAT));
				resources.addElement(makeResource
				("some "+name.toLowerCase()+" blood",EnvResource.RESOURCE_BLOOD));
				resources.addElement(makeResource
				("a pile of "+name.toLowerCase()+" bones",EnvResource.RESOURCE_BONE));
			}
		}
		return resources;
	}
}

