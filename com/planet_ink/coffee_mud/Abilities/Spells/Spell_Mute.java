package com.planet_ink.coffee_mud.Abilities.Spells;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Spell_Mute extends Spell
{
	public String ID() { return "Spell_Mute"; }
	public String name(){return "Mute";}
	public String displayText(){return "(Mute)";}
	public int quality(){return MALICIOUS;};
	protected int canAffectCode(){return CAN_MOBS;}
	public Environmental newInstance(){	return new Spell_Mute();}
	public int classificationCode(){ return Ability.SPELL|Ability.DOMAIN_TRANSMUTATION;}


	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()|EnvStats.CAN_NOT_SPEAK);
	}


	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;
		super.unInvoke();

		if(canBeUninvoked())
			mob.tell("Your ability to speak returns.");
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
			FullMsg msg=new FullMsg(mob,target,this,affectType(auto),auto?"":"^S<S-NAME> wave(s) <S-HIS-HER> hands at <T-NAMESELF>.^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(!msg.wasModified())
				{
					success=maliciousAffect(mob,target,0,-1);
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,"<S-NAME> become(s) mute!");
				}
			}
		}
		else
			return maliciousFizzle(mob,target,"<S-NAME> wave(s) <S-HIS-HER> hands at <T-NAMESELF>, but the spell fizzles.");

		// return whether it worked
		return success;
	}
}
