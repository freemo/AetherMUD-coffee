package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Prayer_Disenchant extends Prayer
{
	public String ID() { return "Prayer_Disenchant"; }
	public String name(){ return "Disenchant";}
	public int quality(){ return INDIFFERENT;}
	public int holyQuality(){ return HOLY_NEUTRAL;}
	protected int canTargetCode(){return Ability.CAN_ITEMS;}
	public Environmental newInstance(){	return new Prayer_Disenchant();}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		Item target=getTarget(mob,mob.location(),givenTarget,commands,Item.WORN_REQ_ANY);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=profficiencyCheck(5-((mob.envStats().level()-target.envStats().level())*5),auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			FullMsg msg=new FullMsg(mob,target,this,affectType(auto),auto?"<T-NAME> appear(s) neutralized!":"^S<S-NAME> invoke(s) <S-HIS-HER> god's power to neutralize <T-NAMESELF>.^?");
			if(mob.location().okAffect(msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,0);
				target.baseEnvStats().setAbility(0);
				Vector affects=new Vector();
				for(int a=target.numAffects()-1;a>=0;a--)
				{
					Ability A=target.fetchAffect(a);
					if(A!=null)
						affects.addElement(A);
				}
				for(int a=0;a<affects.size();a++)
				{
					Ability A=(Ability)affects.elementAt(a);
					A.unInvoke();
					target.delAffect(A);
				}
				target.recoverEnvStats();
				mob.location().recoverRoomStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,target,"<S-NAME> pray(s) over <T-NAMESELF> for neutrality, but nothing happens.");
		// return whether it worked
		return success;
	}
}