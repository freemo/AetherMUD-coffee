package com.planet_ink.coffee_mud.Abilities.Spells;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Spell_FeelTheVoid extends Spell
{
	public String ID() { return "Spell_FeelTheVoid"; }
	public String name(){return "Feel The Void";}
	public String displayText(){return "(In a Void)";}
	public int quality(){return MALICIOUS;};
	protected int canAffectCode(){return CAN_MOBS;}
	public Environmental newInstance(){	return new Spell_FeelTheVoid();}
	public int classificationCode(){	return Ability.SPELL|Ability.DOMAIN_ILLUSION;}
	private final static int mask=
			EnvStats.CAN_NOT_TASTE|EnvStats.CAN_NOT_SMELL|EnvStats.CAN_NOT_SEE
		    |EnvStats.CAN_NOT_HEAR;
	private final static int mask2=Integer.MAX_VALUE
			-EnvStats.CAN_SEE_BONUS
		    -EnvStats.CAN_SEE_DARK
		    -EnvStats.CAN_SEE_EVIL
		    -EnvStats.CAN_SEE_GOOD
		    -EnvStats.CAN_SEE_HIDDEN
		    -EnvStats.CAN_SEE_INFRARED
		    -EnvStats.CAN_SEE_INVISIBLE
		    -EnvStats.CAN_SEE_METAL
		    -EnvStats.CAN_SEE_SNEAKERS
		    -EnvStats.CAN_SEE_VICTIM;

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		affectableStats.setSensesMask(mask&mask2);
	}


	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;
		super.unInvoke();

		if(canBeUninvoked())
			mob.tell("You are no longer in the void.");
	}



	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=profficiencyCheck(0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			FullMsg msg=new FullMsg(mob,target,this,affectType(auto),auto?"":"^S<S-NAME> invoke(s) the void at <T-NAMESELF>.^?");
			if(mob.location().okAffect(mob,msg))
			{
				mob.location().send(mob,msg);
				if(!msg.wasModified())
				{
					if(target.location()==mob.location())
					{
						target.location().show(target,null,Affect.MSG_OK_ACTION,"<S-NAME> stand(s) dazed and quiet!");
						success=maliciousAffect(mob,target,0,-1);
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,target,"<S-NAME> invoke(s) at <T-NAMESELF>, but the spell fizzles.");

		// return whether it worked
		return success;
	}
}
