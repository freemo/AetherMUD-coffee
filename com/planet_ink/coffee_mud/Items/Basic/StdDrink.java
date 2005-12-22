package com.planet_ink.coffee_mud.Items.Basic;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;


import java.util.*;

/* 
   Copyright 2000-2006 Bo Zimmerman

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
public class StdDrink extends StdContainer implements Drink,Item
{
	public String ID(){	return "StdDrink";}
	protected int amountOfThirstQuenched=250;
	protected int amountOfLiquidHeld=2000;
	protected int amountOfLiquidRemaining=2000;
	protected boolean disappearsAfterDrinking=false;
	protected int liquidType=EnvResource.RESOURCE_FRESHWATER;

	public StdDrink()
	{
		super();
		setName("a cup");
		baseEnvStats.setWeight(10);
		capacity=0;
		containType=Container.CONTAIN_LIQUID;
		setDisplayText("a cup sits here.");
		setDescription("A small wooden cup with a lid.");
		baseGoldValue=5;
		material=EnvResource.RESOURCE_LEATHER;
		recoverEnvStats();
	}



	public int thirstQuenched(){return amountOfThirstQuenched;}
	public int liquidHeld(){return amountOfLiquidHeld;}
	public int liquidRemaining(){return amountOfLiquidRemaining;}
    public boolean disappearsAfterDrinking(){return disappearsAfterDrinking;}
	public int liquidType(){
		if((material()&EnvResource.MATERIAL_MASK)==EnvResource.MATERIAL_LIQUID)
			return material();
		return liquidType;
	}
	public void setLiquidType(int newLiquidType){liquidType=newLiquidType;}

	public void setThirstQuenched(int amount){amountOfThirstQuenched=amount;}
	public void setLiquidHeld(int amount){amountOfLiquidHeld=amount;}
	public void setLiquidRemaining(int amount){amountOfLiquidRemaining=amount;}

	public boolean containsDrink()
	{
		if((liquidRemaining()<1)
		||
		 ((!CMLib.flags().isGettable(this))
		&&(owner()!=null)
		&&(owner() instanceof Room)
		&&(((Room)owner()).getArea()!=null)
		&&(((Room)owner()).getArea().getClimateObj().weatherType((Room)owner())==Climate.WEATHER_DROUGHT)))
			return false;
		return true;
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
			case CMMsg.TYP_DRINK:
				if((mob.isMine(this))||(envStats().weight()>1000)||(!CMLib.flags().isGettable(this)))
				{
					if(!containsDrink())
					{
						mob.tell(name()+" is empty.");
						return false;
					}
					if((liquidType()==EnvResource.RESOURCE_SALTWATER)
					||(liquidType()==EnvResource.RESOURCE_LAMPOIL))
					{
						mob.tell("You don't want to be drinking "+EnvResource.RESOURCE_DESCS[liquidType()&EnvResource.RESOURCE_MASK].toLowerCase()+".");
						return false;
					}
					return true;
				}
				mob.tell("You don't have that.");
				return false;
			case CMMsg.TYP_FILL:
				if((liquidRemaining()>=amountOfLiquidHeld)
				&&(liquidHeld()<500000))
				{
					mob.tell(name()+" is full.");
					return false;
				}
                if((msg.tool() instanceof Container)
                &&((!(msg.tool() instanceof Drink))||(!((Drink)msg.tool()).containsDrink()))
                &&(msg.tool()!=msg.target()))
                {
                    Vector V=((Container)msg.tool()).getContents();
                    Item I=null;
                    for(int i=0;i<V.size();i++)
                    {
                        I=(Item)V.elementAt(i);
                        if(I instanceof Drink)
                            break;
                    }
                    if(I instanceof Drink)
                        msg.modify(msg.source(),msg.target(),I,
                                msg.sourceCode(),msg.sourceMessage(),
                                msg.targetCode(),msg.targetMessage(),
                                msg.othersCode(),msg.othersMessage());
                    else
                    {
                        msg.source().tell(msg.tool().name()+" has nothing you can fill this with.");
                        return false;
                    }
                }
				if((msg.tool()!=null)
				&&(msg.tool()!=msg.target())
				&&(msg.tool() instanceof Drink))
				{
					Drink thePuddle=(Drink)msg.tool();
					if(!thePuddle.containsDrink())
					{
						mob.tell(thePuddle.name()+" is empty.");
						return false;
					}
					if((liquidRemaining()>0)&&(liquidType()!=thePuddle.liquidType()))
					{
						mob.tell("There is still some "+EnvResource.RESOURCE_DESCS[liquidType()&EnvResource.RESOURCE_MASK].toLowerCase()
								 +" left in "+name()+".  You must empty it before you can fill it with "
								 +EnvResource.RESOURCE_DESCS[thePuddle.liquidType()&EnvResource.RESOURCE_MASK].toLowerCase()+".");
						return false;

					}
					return true;
				}
				mob.tell("You can't fill "+name()+" from that.");
				return false;
			default:
				break;
			}
		}
		return true;
	}
    
    public int amountTakenToFillMe(Drink theSource)
    {
        int amountToTake=amountOfLiquidHeld-amountOfLiquidRemaining;
        if(amountOfLiquidHeld>=500000)
            amountToTake=theSource.liquidRemaining();
        if(amountToTake>theSource.liquidRemaining())
            amountToTake=theSource.liquidRemaining();
        return amountToTake;
    }

	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(msg.amITarget(this))
		{
			MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_DRINK:
				amountOfLiquidRemaining-=amountOfThirstQuenched;
				boolean thirsty=mob.curState().getThirst()<=0;
				boolean full=!mob.curState().adjThirst(amountOfThirstQuenched,mob.maxState().maxThirst(mob.baseWeight()));
				if(thirsty)
					mob.tell("You are no longer thirsty.");
				else
				if(full)
					mob.tell("You have drunk all you can.");
				if(disappearsAfterDrinking)
					destroy();
				break;
			case CMMsg.TYP_FILL:
				if((msg.tool()!=null)&&(msg.tool() instanceof Drink))
				{
					Drink thePuddle=(Drink)msg.tool();
					int amountToTake=amountTakenToFillMe(thePuddle);
					thePuddle.setLiquidRemaining(thePuddle.liquidRemaining()-amountToTake);
					if(amountOfLiquidRemaining<=0)
						setLiquidType(thePuddle.liquidType());
					if((amountOfLiquidRemaining+amountToTake)<=Integer.MAX_VALUE)
						amountOfLiquidRemaining+=amountToTake;
					if(amountOfLiquidRemaining>amountOfLiquidHeld)
						amountOfLiquidRemaining=amountOfLiquidHeld;
					if((thePuddle.liquidRemaining()<=0)
                    &&(thePuddle instanceof Item)
					&&((thePuddle.disappearsAfterDrinking())||(thePuddle instanceof EnvResource)))
						((Item)thePuddle).destroy();
                    if((amountOfLiquidRemaining<=0)
                    &&((disappearsAfterDrinking)||(this instanceof EnvResource)))
                        destroy();
				}
				break;
			default:
				break;
			}
		}
	}
}
