package com.planet_ink.coffee_mud.Items;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class StdLimb extends StdItem
{
	public String ID(){	return "StdLimb";}

	public StdLimb()
	{
		super();
		setName("a false limb");
		baseEnvStats.setWeight(1);
		setDisplayText("a false limb is here.");
		setDescription("Looks like a false limb.");
		properWornBitmap=Item.HELD;
		baseGoldValue=10;
		material=EnvResource.RESOURCE_MEAT;
		recoverEnvStats();
	}

	private int partNum()
	{
		for(int i=0;i<Race.BODYPARTSTR.length;i++)
			if(this.name.toUpperCase().endsWith(Race.BODYPARTSTR[i]))
				return i;
			else
			if(this.secretIdentity.toUpperCase().endsWith(Race.BODYPARTSTR[i]))
				return i;
		return -1;
	}
	
	private long wearPlace()
	{ 
		int num=partNum(); 
		if(num<0) return 0; 
		return Race.BODY_WEARVECTOR[num];
	}
	
	
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(amWearingAt(Item.FLOATING_NEARBY))	
			setRawWornCode(wearPlace());
		
		if((!amWearingAt(Item.INVENTORY))
		&&(!amWearingAt(Item.HELD))
		&&(!amWearingAt(Item.FLOATING_NEARBY))
		&&(!amWearingAt(Item.WIELD)))
		{
			int num=partNum();
			int parts=envStats().ability();
			if((affectableStats.getBodyPart(num)+parts)<=affectableStats.getMyRace().bodyMask()[num])
				affectableStats.alterBodypart(num,parts);
			else
				setRawWornCode(0);
		}
	}
	
	public boolean okMessage(Environmental myHost, CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		if(msg.amITarget(this))
		{
			MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_WEAR:
			case CMMsg.TYP_HOLD:
			case CMMsg.TYP_WIELD:
				mob.tell("You can't just wear that.");
				return false;
			default:
				break;
			}
		}
		return true;
	}
}
