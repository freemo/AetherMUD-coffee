package com.planet_ink.coffee_mud.Abilities.Fighter;
import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Fighter_LightningStrike extends StdAbility
{
	public String ID() { return "Fighter_LightningStrike"; }
	public String name(){ return "Lightning Strike";}
	public String displayText(){return "(Exhausted)";}
	private static final String[] triggerStrings = {"LIGHTNINGSTRIKE","LSTRIKE"};
	public int quality(){return Ability.MALICIOUS;}
	public String[] triggerStrings(){return triggerStrings;}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return Ability.CAN_MOBS;}
	public Environmental newInstance(){	return new Fighter_LightningStrike();	}
	public int classificationCode(){ return Ability.SKILL;}

	public boolean okAffect(Affect affect)
	{
		if((affected==null)||(!(affected instanceof MOB)))
			return true;

		MOB mob=(MOB)affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if((affect.amISource(mob))&&(!Util.bset(affect.sourceMajor(),Affect.MASK_GENERAL)))
		{
			if((Util.bset(affect.sourceMajor(),Affect.MASK_EYES))
			||(Util.bset(affect.sourceMajor(),Affect.MASK_HANDS))
			||(Util.bset(affect.sourceMajor(),Affect.MASK_MOUTH))
			||(Util.bset(affect.sourceMajor(),Affect.MASK_MOVE)))
			{
				if(affect.sourceMessage()!=null)
					mob.tell(mob,null,"You are way too drowsy.");
				return false;
			}
		}
		return super.okAffect(affect);
	}

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		// when this spell is on a MOBs Affected list,
		// it should consistantly put the mob into
		// a sleeping state, so that nothing they do
		// can get them out of it.
		affectableStats.setDisposition(affectableStats.disposition()|EnvStats.IS_SLEEPING);
	}

	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
		{
			if(!mob.amDead())
			{
				if(mob.location()!=null)
					mob.location().show(mob,null,Affect.MSG_OK_ACTION,"<S-NAME> seem(s) less drowsy.");
				else
					mob.tell("You feel less drowsy.");
				ExternalPlay.standIfNecessary(mob);
			}
		}
	}

	public boolean anyWeapons(MOB mob)
	{
		for(int i=0;i<mob.inventorySize();i++)
		{
			Item I=mob.fetchInventory(i);
			if((I!=null)
			   &&((I.amWearingAt(Item.WIELD))
			      ||(I.amWearingAt(Item.HELD))))
				return true;
		}
		return false;
	}


	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(mob.isInCombat()&&(mob.rangeToTarget()>0))
		{
			mob.tell("You are too far away from your target to strike!");
			return false;
		}
		if((!auto)&&(mob.charStats().getStat(CharStats.DEXTERITY)<18))
		{
			mob.tell("You need at least an 18 dexterity to do that.");
			return false;
		}

		if((!auto)&&(anyWeapons(mob)))
		{
			mob.tell("You must be unarmed to perform the strike.");
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		int levelDiff=target.envStats().level()-adjustedLevel(mob);
		if(levelDiff>0)
			levelDiff=levelDiff*5;
		else
			levelDiff=0;
		// now see if it worked
		boolean success=profficiencyCheck((-levelDiff),auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			FullMsg msg=new FullMsg(mob,target,this,Affect.MSK_MALICIOUS_MOVE|Affect.TYP_JUSTICE|(auto?Affect.MASK_GENERAL:0),auto?"":"^F<S-NAME> unleash(es) a flurry of lightning strikes against <T-NAMESELF>!^?");
			if(mob.location().okAffect(msg))
			{
				mob.location().send(mob,msg);
				for(int i=0;i<CMAble.qualifyingClassLevel(mob,this);i++)
					if(!target.amDead())
						ExternalPlay.postAttack(mob,target,null);
				mob.location().show(mob,null,Affect.MSG_OK_VISUAL,"<S-NAME> collapse(s) in exhaustion.");
				success=maliciousAffect(mob,mob,7,-1);
			}
		}
		else
			return maliciousFizzle(mob,target,"<S-NAME> attempt(s) to flurry <T-NAMESELF> with lighting strikes, but fail(s).");

		// return whether it worked
		return success;
	}
}
