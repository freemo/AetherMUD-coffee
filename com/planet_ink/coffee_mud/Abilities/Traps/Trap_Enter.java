package com.planet_ink.coffee_mud.Abilities.Traps;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import java.util.*;

public class Trap_Enter extends Trap_Trap
{
	public Trap_Enter()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		canTargetCode=0;
		canAffectCode=Ability.CAN_EXITS|Ability.CAN_ROOMS;
	}

	public Environmental newInstance()
	{
		return new Trap_Enter();
	}

	public boolean okAffect(Affect affect)
	{
		if(sprung) return super.okAffect(affect);
		if(!super.okAffect(affect))
			return false;

		   if((affect.amITarget(affected))
 			||((affect.tool()!=null)&&(affect.tool()==affected)))
		{
			if((affect.targetMinor()==Affect.TYP_ENTER)
			||(affect.targetMinor()==Affect.TYP_LEAVE)
			||(affect.targetMinor()==Affect.TYP_FLEE))
			{
				if(affect.targetMinor()==Affect.TYP_LEAVE)
					return true;
				else
				{
					spring(affect.source());
					return false;
				}
			}
		}
		return true;
	}
	public void affect(Affect affect)
	{
		super.affect(affect);
		if(sprung)
			return;

		if((affect.amITarget(affected))||((affect.tool()!=null)&&(affect.tool()==affected)))
		{
			if(affect.targetMinor()==Affect.TYP_LEAVE)
			{
				spring(affect.source());
			}
		}
	}
}
