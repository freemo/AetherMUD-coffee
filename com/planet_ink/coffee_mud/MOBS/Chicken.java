package com.planet_ink.coffee_mud.MOBS;

import java.util.*;
import com.planet_ink.coffee_mud.utils.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
public class Chicken extends StdMOB
{
	public String ID(){return "Chicken";}
	public Chicken()
	{
		super();
		Username="a chicken";
		setDescription("a fat, short winged bird");
		setDisplayText("A chicken is here, not flying.");
		setAlignment(500);
		setMoney(0);
		setWimpHitPoint(0);

		baseEnvStats().setDamage(1);
		baseEnvStats().setSpeed(1.0);
		baseEnvStats().setAbility(0);
		baseEnvStats().setLevel(1);
		baseEnvStats().setArmor(90);
		baseCharStats().setMyRace(CMClass.getRace("Chicken"));
		baseCharStats().getMyRace().startRacing(this,false);

		baseState.setHitPoints(Dice.roll(baseEnvStats().level(),20,baseEnvStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverEnvStats();
		recoverCharStats();
	}
	public Environmental newInstance()
	{
		return new Chicken();
	}
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if(tickID==Host.TICK_MOB)
		{
			if(Dice.rollPercentage()>95)
			{
				Item I=CMClass.getItem("GenFoodResource");
				I.setName("an egg");
				I.setDisplayText("an egg has been left here.");
				I.setMaterial(EnvResource.RESOURCE_EGGS);
				I.setDescription("It looks like a chicken egg!");
				I.baseEnvStats().setWeight(1);
				addInventory((Item)I.copyOf());
			}
			if((inventorySize()>8)&&(location()!=null))
			{
				Item I=fetchInventory(Dice.roll(1,inventorySize(),-1));
				if(I.name().equals("an egg"))
				{
					location().show(this,null,CMMsg.MSG_NOISYMOVEMENT,"<S-NAME> lay(s) an egg.");
					I.removeFromOwnerContainer();
					location().addItemRefuse(I,Item.REFUSE_RESOURCE);
					location().recoverRoomStats();
				}
			}
		}
		return true;
	}
}
