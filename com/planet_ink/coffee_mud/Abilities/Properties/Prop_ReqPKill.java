package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Prop_ReqPKill extends Property
{
	public Prop_ReqPKill()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		name="Playerkill ONLY Zone";
		canAffectCode=Ability.CAN_ROOMS|Ability.CAN_AREAS|Ability.CAN_EXITS;
	}

	public Environmental newInstance()
	{
		Prop_ReqPKill newOne=new Prop_ReqPKill();
		newOne.setMiscText(text());
		return newOne;
	}

	public boolean okAffect(Affect affect)
	{
		if((affected!=null)
		   &&(affect.target()!=null)
		   &&(affect.target() instanceof Room)
		   &&(affect.targetMinor()==Affect.TYP_ENTER)
		   &&((affect.amITarget(affected))||(affect.tool()==affected)||(affected instanceof Area)))
		{
			if((!affect.source().isMonster())
			   &&((affect.source().getBitmap()&MOB.ATT_PLAYERKILL)==0))
			{
				affect.source().tell("You must have your playerkill flag set to enter here.");
				return false;
			}
		}
		if((!affect.source().isMonster())&&((affect.source().getBitmap()&MOB.ATT_PLAYERKILL)==0))
			affect.source().setBitmap(affect.source().getBitmap()|MOB.ATT_PLAYERKILL);
		return super.okAffect(affect);
	}
}
