package com.planet_ink.coffee_mud.CharClasses;

import java.util.*;
import com.planet_ink.coffee_mud.utils.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;

public class Archon extends StdCharClass
{
	public Archon()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		for(int i=0;i<=5;i++)
			maxStat[i]=25;
		name=myID;
	}

	public boolean playerSelectable()
	{
		return false;
	}

	public boolean qualifiesForThisClass(MOB mob)
	{
		return false;
	}

	public void logon(MOB mob)
	{
		super.logon(mob);
		for(int a=0;a<CMClass.abilities.size();a++)
		{
			Ability A=(Ability)CMClass.abilities.elementAt(a);
			if(A.qualifyingLevel(mob)>0)
			{
				A=(Ability)A.copyOf();
				A.setProfficiency(100);
				A.setBorrowed(mob,true);
				giveMobAbility(mob,A,true);
			}
		}
	}

	public void outfit(MOB mob)
	{
		Weapon w=(Weapon)CMClass.getWeapon("ArchonStaff");
		if(mob.fetchInventory(w.ID())==null)
		{
			mob.addInventory(w);
			if(!mob.amWearingSomethingHere(Item.WIELD))
				w.wearAt(Item.WIELD);
		}
	}
	public void newCharacter(MOB mob, boolean isBorrowedClass)
	{
		super.newCharacter(mob, isBorrowedClass);
		logon(mob);
	}

	public void level(MOB mob)
	{
		mob.tell("^HYou leveled... not that it matters.^N");
	}
}
