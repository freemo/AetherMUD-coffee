package com.planet_ink.coffee_mud.Abilities.Spells;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import com.planet_ink.coffee_mud.Abilities.Spells.interfaces.*;
import java.util.*;

public class Spell_FaerieFire extends Spell
	implements EvocationDevotion
{

	public Spell_FaerieFire()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		name="Faerie Fire";

		// what the affected mob sees when they
		// bring up their affected list.
		displayText="(Faerie Fire)";


		canBeUninvoked=true;
		isAutoinvoked=false;
		quality=Ability.MALICIOUS;

		baseEnvStats().setLevel(5);

		uses=Integer.MAX_VALUE;
		recoverEnvStats();
	}

	public Environmental newInstance()
	{
		return new Spell_FaerieFire();
	}


	public void unInvoke()
	{
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;
		mob.location().show(mob, null, Affect.MSG_OK_VISUAL, "The faerie fire around <S-NAME> fades.");
		super.unInvoke();
	}

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);

		if((affectableStats.disposition()&Sense.IS_INVISIBLE)==Sense.IS_INVISIBLE)
			affectableStats.setDisposition(affectableStats.disposition()-Sense.IS_INVISIBLE);
		affectableStats.setDisposition(affectableStats.disposition()|Sense.IS_LIGHT);
		affectableStats.setDisposition(affectableStats.disposition()|Sense.IS_BONUS);
		affectableStats.setArmor(affectableStats.armor()+10);
	}


	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		MOB target = getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		boolean success=profficiencyCheck(0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.

			FullMsg msg = new FullMsg(mob, target, this, affectType,(auto?"A ":"<S-NAME> chant(s) and gesture(s) and a ")+"twinkling fire envelopes <T-NAME>.");
			if(mob.location().okAffect(msg))
			{
				mob.location().send(mob,msg);
				maliciousAffect(mob,target,0,-1);
			}
		}
		else
			return beneficialWordsFizzle(mob,null,"<S-NAME> chant(s) for a faerie fire, but the spell fizzles.");

		// return whether it worked
		return success;
	}
}
