package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Prayer_CureBlindness extends Prayer
{
	public String ID() { return "Prayer_CureBlindness"; }
	public String name(){ return "Cure Blindness";}
	public int quality(){ return OK_OTHERS;}
	public long flags(){return Ability.FLAG_HOLY;}
	public Environmental newInstance(){	return new Prayer_CureBlindness();}

	public static Vector returnOffensiveAffects(MOB caster, Environmental fromMe)
	{
		MOB newMOB=(MOB)CMClass.getMOB("StdMOB");
		MOB newerMOB=(MOB)CMClass.getMOB("StdMOB");
		Vector offenders=new Vector();

		FullMsg msg=new FullMsg(newMOB,newerMOB,null,CMMsg.MSG_EXAMINESOMETHING,null);
		for(int a=0;a<fromMe.numEffects();a++)
		{
			Ability A=fromMe.fetchEffect(a);
			if(A!=null)
			{
				newMOB.recoverEnvStats();
				A.affectEnvStats(newMOB,newMOB.envStats());
				if((!Sense.canSee(newMOB))
				   ||(!A.okMessage(newMOB,msg)))
				if((A.invoker()==null)
				   ||((A.invoker()!=null)
					  &&(A.invoker().envStats().level()<=caster.envStats().level()+1)))
						offenders.addElement(A);
			}
		}
		return offenders;
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=profficiencyCheck(0,auto);
		Vector offensiveAffects=returnOffensiveAffects(mob,target);

		if((success)&&(offensiveAffects.size()>0))
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			FullMsg msg=new FullMsg(mob,target,this,affectType(auto),auto?"A visible glow surrounds <T-NAME>.":"^S<S-NAME> "+prayWord(mob)+" for <T-NAMESELF> to see the light.^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				for(int a=offensiveAffects.size()-1;a>=0;a--)
					((Ability)offensiveAffects.elementAt(a)).unInvoke();
			}
		}
		else
			beneficialWordsFizzle(mob,target,auto?"":"<S-NAME> "+prayWord(mob)+" for <T-NAMESELF>, but nothing happens.");

		// return whether it worked
		return success;
	}
}
