package com.planet_ink.coffee_mud.Abilities.Druid;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Chant_Earthquake extends Chant
{
	public String ID() { return "Chant_Earthquake"; }
	public String name(){ return "Earthquake";}
	public String displayText(){return "(Earthquake)";}
	public int quality(){return Ability.MALICIOUS;}
	public int maxRange(){return 3;}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return 0;}
	public Environmental newInstance(){	return new Chant_Earthquake();}

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		// when this spell is on a MOBs Affected list,
		// it should consistantly put the mob into
		// a sleeping state, so that nothing they do
		// can get them out of it.
		affectableStats.setDisposition(affectableStats.disposition()|EnvStats.IS_SITTING);
	}


	private MOB lastMOB=null;
	public boolean okAffect(Affect msg)
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof MOB)))
			return super.okAffect(msg);
		MOB mob=(MOB)affected;
		if((msg.amISource(mob))
		&&(msg.sourceMinor()==Affect.TYP_STAND)
		&&(mob.location()!=null))
		{
			if(mob!=lastMOB)
			{
				lastMOB=mob;
				mob.location().show(mob,null,Affect.MSG_NOISYMOVEMENT,"<S-NAME> attempt(s) to stand up, and falls back down!");
			}
			else
				lastMOB=null;
			return false;
		}
		return super.okAffect(msg);
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
			if(mob.location()!=null)
			{
				FullMsg msg=new FullMsg(mob,null,Affect.MSG_NOISYMOVEMENT,"<S-NAME> regain(s) <S-HIS-HER> feet as the ground stops shaking.");
				if(mob.location().okAffect(msg))
				{
					mob.location().send(mob,msg);
					ExternalPlay.standIfNecessary(mob);
				}
			}
			else
				mob.tell("The movement under your feet stops.");
		}
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		Hashtable h=ExternalPlay.properTargets(this,mob,auto);
		if(h==null)
		{
			mob.tell("There doesn't appear to be anyone here worth shaking up.");
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=profficiencyCheck(0,auto);

		if(success)
		{

			mob.location().show(mob,null,affectType(auto),auto?"":"^S<S-NAME> chant(s) thunderously.^?");
			for(Enumeration f=h.elements();f.hasMoreElements();)
			{
				MOB target=(MOB)f.nextElement();

				// it worked, so build a copy of this ability,
				// and add it to the affects list of the
				// affected MOB.  Then tell everyone else
				// what happened.
				FullMsg msg=new FullMsg(mob,target,this,affectType(auto),null);
				if(Sense.isFlying(target))
					mob.location().show(target,null,Affect.MSG_OK_VISUAL,"<S-NAME> seem(s) unaffected.");
				else
				if((mob.location().okAffect(msg))&&(target.fetchAffect(this.ID())==null))
				{
					mob.location().send(mob,msg);
					if(!msg.wasModified())
					{
						success=maliciousAffect(mob,target,10,-1);
						if(success)
						{
							if(target.location()==mob.location())
								ExternalPlay.postDamage(mob,target,this,20,Affect.MASK_GENERAL|Affect.TYP_CAST_SPELL,-1,"The ground underneath <T-NAME> shakes as <T-NAME> fall(s) to the ground!!");
						}
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,null,"<S-NAME> attempt(s) to invoke a thunderous spell, but the spell fizzles.");


		// return whether it worked
		return success;
	}
}