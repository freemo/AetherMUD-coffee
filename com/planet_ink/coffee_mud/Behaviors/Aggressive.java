package com.planet_ink.coffee_mud.Behaviors;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;

import java.util.*;

public class Aggressive extends StdBehavior
{

	public Aggressive()
	{
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
	}
	public Behavior newInstance()
	{
		return new Aggressive();
	}

	public static void startFight(MOB monster, MOB mob, boolean fightMOBs)
	{
		if(((!mob.isMonster())||(fightMOBs))
		&&(monster.location()!=null)
		&&(canFreelyBehaveNormal(monster))
		&&(monster.location().isInhabitant(mob))
		&&(Sense.canBeSeenBy(mob,monster))
		&&((mob.charStats().getMyClass()==null)||(!(mob.charStats().getMyClass().ID().equals("Archon"))))
		&&(mob!=monster))
		{
			if(Sense.isHidden(monster))
			{
				Ability A=monster.fetchAbility("Thief_BackStab");
				if(A!=null)
				{
					A.setProfficiency(Dice.roll(1,50,(mob.baseEnvStats().level()-A.qualifyingLevel(mob))*15));
					A.invoke(monster,mob,false);
				}
			}
			ExternalPlay.postAttack(monster,mob,monster.fetchWieldedItem());
			monster.setVictim(mob);
		}
	}
	public static void pickAFight(MOB observer)
	{
		if(!canFreelyBehaveNormal(observer)) return;
		for(int i=0;i<observer.location().numInhabitants();i++)
		{
			MOB mob=observer.location().fetchInhabitant(i);
			if(mob!=observer)
			{
				startFight(observer,mob,false);
				if(observer.isInCombat()) break;
			}
		}
	}

	public static void tickAggressively(Environmental ticking, int tickID)
	{
		if(tickID!=Host.MOB_TICK) return;
		if(ticking==null) return;
		if(!(ticking instanceof MOB)) return;

		pickAFight((MOB)ticking);
	}

	public void tick(Environmental ticking, int tickID)
	{
		super.tick(ticking,tickID);
		if(tickID!=Host.MOB_TICK) return;
		tickAggressively(ticking,tickID);
	}
}
