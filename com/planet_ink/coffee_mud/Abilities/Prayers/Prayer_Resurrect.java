package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Prayer_Resurrect extends Prayer
{
	public Prayer_Resurrect()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		name="Resurrect";
		displayText="(Resurrected)";

		baseEnvStats().setLevel(25);

		recoverEnvStats();
	}

	public Environmental newInstance()
	{
		return new Prayer_Resurrect();
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		Item body=this.getTarget(mob,mob.location(),givenTarget,commands);
		if(body==null) return false;
		if(!(body instanceof DeadBody))
		{
			mob.tell("You can't resurrect that.");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=profficiencyCheck(0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			FullMsg msg=new FullMsg(mob,body,this,affectType,auto?"<T-NAME> is resurrected!":"<S-NAME> resurrect(s) <T-NAMESELF>!");
			if(mob.location().okAffect(msg))
			{
				invoker=mob;
				mob.location().send(mob,msg);
				int x=0;
				if((body instanceof DeadBody)&&((x=body.name().toUpperCase().indexOf("BODY OF"))>=0))
				{
					String mobName=body.name().substring(x+7).trim();
					MOB rejuvedMOB=(MOB)CMMap.MOBs.get(mobName);
					if(rejuvedMOB!=null)
					{
						rejuvedMOB.tell(rejuvedMOB,null,"You are being resurrected.");
						if(rejuvedMOB.location()!=mob.location())
						{
							rejuvedMOB.location().delInhabitant(rejuvedMOB);
							rejuvedMOB.location().showOthers(rejuvedMOB,null,Affect.MSG_OK_VISUAL,"<S-NAME> disappears!");
							mob.location().addInhabitant(rejuvedMOB);
							rejuvedMOB.setLocation(mob.location());
						}
						int it=0;
						while(it<rejuvedMOB.location().numItems())
						{
							Item item=rejuvedMOB.location().fetchItem(it);
							if((item!=null)&&(item.location()==body))
							{
								FullMsg msg2=new FullMsg(rejuvedMOB,body,item,Affect.MSG_GET,null);
								rejuvedMOB.location().send(rejuvedMOB,msg2);
								FullMsg msg3=new FullMsg(rejuvedMOB,item,null,Affect.MSG_GET,null);
								rejuvedMOB.location().send(rejuvedMOB,msg3);
								it=0;
							}
							else
								it++;
						}
						body.destroyThis();
						rejuvedMOB.location().show(rejuvedMOB,null,Affect.MSG_NOISYMOVEMENT,"<S-NAME> get(s) up!");
						mob.location().recoverRoomStats();
					}
					else
						mob.location().show(mob,body,Affect.MSG_OK_VISUAL,"<T-NAME> twitch(es) for a moment, but the spirit is too far gone.");
				}
			}
		}
		else
			beneficialWordsFizzle(mob,body,auto?"":"<S-NAME> attempt(s) to resurrect <T-NAMESELF>, but nothing happens.");


		// return whether it worked
		return success;
	}
}
