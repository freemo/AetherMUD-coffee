package com.planet_ink.coffee_mud.Items;


import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class GenLantern extends GenLightSource
{
	public String ID(){	return "GenLantern";}
	public GenLantern()
	{
		super();
		name="a hooded lantern";
		displayText="a hooded lantern sits here.";
		description="";

		baseEnvStats().setWeight(5);
		setDuration(400);
		destroyedWhenBurnedOut=false;
		goesOutInTheRain=false;
		baseGoldValue=60;
		setMaterial(EnvResource.RESOURCE_STEEL);
		recoverEnvStats();
	}
	public Environmental newInstance()
	{
		return new GenLantern();
	}
	public boolean isGeneric(){return true;}

	public boolean okAffect(Environmental myHost, Affect affect)
	{
		if(affect.amITarget(this))
		{
			MOB mob=affect.source();
			switch(affect.targetMinor())
			{
				case Affect.TYP_FILL:
					if(mob.isMine(this))
					{
						if(getDuration()>0)
						{
							mob.tell(name()+" still has some oil left in it.");
							return false;
						}
						if((affect.tool()!=null)&&(affect.tool() instanceof Drink))
						{
							if(((Drink)affect.tool()).liquidType()!=EnvResource.RESOURCE_LAMPOIL)
							{
								mob.tell("You can only fill "+name()+" with lamp oil!");
								return false;
							}
							Drink thePuddle=(Drink)affect.tool();
							if(thePuddle.liquidRemaining()<1)
							{
								mob.tell(thePuddle.name()+" is empty.");
								return false;
							}
							return true;
						}
						else
						{
							mob.tell("You can't fill "+name()+" from that.");
							return false;
						}
					}
					else
					{
						mob.tell("You don't have that.");
						return false;
					}
				default:
					break;
			}
		}
		return super.okAffect(myHost,affect);
	}

	public void affect(Environmental myHost, Affect affect)
	{
		if(affect.amITarget(this))
		{
			switch(affect.targetMinor())
			{
			case Affect.TYP_FILL:
				if((affect.tool()!=null)&&(affect.tool() instanceof Drink))
				{
					Drink thePuddle=(Drink)affect.tool();
					int amountToTake=1;
					if(amountToTake>thePuddle.liquidRemaining())
						amountToTake=0;
					thePuddle.setLiquidRemaining(thePuddle.liquidRemaining()-amountToTake);
					lit=false;
					setDuration(200);
					description="The lantern still looks like it has some oil in it.";
				}
				break;
			default:
				break;
			}
		}
		super.affect(myHost,affect);
	}
}
