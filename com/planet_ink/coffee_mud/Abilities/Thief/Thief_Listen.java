package com.planet_ink.coffee_mud.Abilities.Thief;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Thief_Listen extends ThiefSkill
{

	public Thief_Listen()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		name="Listen";
		displayText="(in a dark realm of thievery)";
		miscText="";

		triggerStrings.addElement("LISTEN");

		canBeUninvoked=true;
		isAutoinvoked=false;

		baseEnvStats().setLevel(11);

		addQualifyingClass("Thief",11);
		recoverEnvStats();
	}

	public Environmental newInstance()
	{
		return new Thief_Listen();
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		int dirCode=Directions.getGoodDirectionCode(Util.combine(commands,0));
		if(!Sense.canHear(mob))
		{
			mob.tell("You don't hear anything.");
			return false;
		}

		Room room=null;
		if(dirCode<0)
			room=mob.location();
		else
		{
			if((mob.location().doors()[dirCode]==null)||(mob.location().exits()[dirCode]==null))
			{
				mob.tell("Listen which direction?");
				return false;
			}
			room=mob.location().doors()[dirCode];
			if((room.domainType()&128)==Room.OUTDOORS)
			{
				mob.tell("You can only listen indoors.");
				return false;
			}
		}

		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=false;
		FullMsg msg=new FullMsg(mob,null,null,auto?Affect.MSG_OK_ACTION:(Affect.MSG_DELICATE_HANDS_ACT|Affect.ACT_EARS),Affect.MSG_OK_VISUAL,Affect.MSG_OK_VISUAL,"<S-NAME> listen(s)"+((dirCode<0)?"":" "+Directions.getDirectionName(dirCode))+".");
		if(mob.location().okAffect(msg))
		{
			mob.location().send(mob,msg);
			success=profficiencyCheck(0,auto);
			int numberHeard=0;
			for(int i=0;i<room.numInhabitants();i++)
			{
				MOB inhab=room.fetchInhabitant(i);
				if((inhab!=null)&&(!Sense.isSneaking(inhab))&&(!Sense.isHidden(inhab))&&(inhab!=mob))
					numberHeard++;
			}
			if((success)&&(numberHeard>0))
			{
				if(profficiency()>75)
					mob.tell("You definitely hear the movement of "+numberHeard+" creature(s).");
				else
					mob.tell("You definitely hear something.");
			}
			else
				mob.tell("You don't hear anything.");
		}
		return success;
	}

}