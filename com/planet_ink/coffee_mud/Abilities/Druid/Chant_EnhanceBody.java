package com.planet_ink.coffee_mud.Abilities.Druid;


import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Chant_EnhanceBody extends Chant
{
	public String ID() { return "Chant_EnhanceBody"; }
	public String name(){ return "Enhance Body";}
	public String displayText(){return "(Enhanced Body)";}
	public int quality(){return Ability.BENEFICIAL_SELF;}
	public Environmental newInstance(){	return new Chant_EnhanceBody();}

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		if((affected instanceof MOB)
		&&(((MOB)affected).fetchWieldedItem()==null))
		{
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() + (5*(affected.envStats().level()/10)));
			affectableStats.setDamage(affectableStats.damage() + 1+(affected.envStats().level()/10));
		}
	}

	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;
		super.unInvoke();
		if(canBeUninvoked())
			mob.tell("Your body doesn't feel quite so enhanced.");
	}


	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,"<S-NAME> <S-IS-ARE> already enhanced.");
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=profficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			FullMsg msg=new FullMsg(mob,null,this,affectType(auto),auto?"<T-NAME> go(es) feral!":"^S<S-NAME> chant(s) to <S-NAMESELF> and <S-HIS-HER> body become(s) enhanced!^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,mob,0);
				target.location().recoverRoomStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,null,"<S-NAME> chant(s) to <S-NAMESELF>, but nothing happens");

		// return whether it worked
		return success;
	}
}
