package com.planet_ink.coffee_mud.Behaviors;

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
public class Vagrant extends StdBehavior
{
	public String ID(){return "Vagrant";}
	protected int canImproveCode(){return Behavior.CAN_MOBS;}
	private int sleepForTicks=0;
	private int wakeForTicks=0;


	public boolean okMessage(Environmental oking, CMMsg msg)
	{
		if((oking==null)||(!(oking instanceof MOB)))
		   return super.okMessage(oking,msg);
		MOB mob=(MOB)oking;
		if(msg.amITarget(mob)
		   &&(((msg.sourceCode()&CMMsg.MASK_MOVE)>0)||((msg.sourceCode()&CMMsg.MASK_HANDS)>0)))
		{
			if(!msg.amISource(mob))
				sleepForTicks=0;
			else
			if(sleepForTicks>0)
			{
				mob.envStats().setDisposition(mob.envStats().disposition()|EnvStats.IS_SLEEPING);
				return false;
			}
		}
		return true;
	}

	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);

		if(tickID!=MudHost.TICK_MOB) return true;
		MOB mob=(MOB)ticking;
		if((wakeForTicks<=0)&&(sleepForTicks<=0))
		{
			if((Dice.rollPercentage()>50)||(mob.isInCombat()))
			{
				CommonMsgs.stand(mob,true);
				wakeForTicks=Dice.roll(1,30,0);
			}
			else
			{
				if(Sense.aliveAwakeMobile(mob,true))
					mob.location().show(mob,mob.location(),CMMsg.MSG_SLEEP,"<S-NAME> curl(s) on the ground and go(es) to sleep.");
				if(Sense.isSleeping(mob))
					sleepForTicks=Dice.roll(1,10,0);
			}
		}
		else
		if(wakeForTicks>0)
			wakeForTicks--;
		else
		if(sleepForTicks>0)
			sleepForTicks--;
		return true;
	}
}
