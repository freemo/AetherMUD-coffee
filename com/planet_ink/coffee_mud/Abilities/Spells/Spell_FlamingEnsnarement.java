package com.planet_ink.coffee_mud.Abilities.Spells;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Spell_FlamingEnsnarement extends Spell
{
	public String ID() { return "Spell_FlamingEnsnarement"; }
	public String name(){return "Flaming Ensnarement";}
	public String displayText(){return "(Ensnared in Fire)";}
	public int maxRange(){return 5;}
	public int minRange(){return 1;}
	public int quality(){return MALICIOUS;};
	protected int canAffectCode(){return CAN_MOBS;}
	public Environmental newInstance(){	return new Spell_FlamingEnsnarement();}
	public int classificationCode(){ return Ability.SPELL|Ability.DOMAIN_CONJURATION;}
	public long flags(){return Ability.FLAG_BINDING|Ability.FLAG_BURNING|Ability.FLAG_HEATING;}

	public int amountRemaining=0;
	public boolean okMessage(Environmental myHost, CMMsg msg)
	{
		if((affected==null)||(!(affected instanceof MOB)))
			return true;

		MOB mob=(MOB)affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if(msg.amISource(mob))
		{
			switch(msg.sourceMinor())
			{
			case CMMsg.TYP_ENTER:
			case CMMsg.TYP_ADVANCE:
			case CMMsg.TYP_LEAVE:
			case CMMsg.TYP_FLEE:
				if(mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,"<S-NAME> struggle(s) against the flaming ensnarement."))
				{
					amountRemaining-=mob.envStats().level();
					if(amountRemaining<0)
						unInvoke();
				}
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		affectableStats.setDisposition((int)(affectableStats.disposition()&(EnvStats.ALLMASK-EnvStats.IS_FLYING)));
	}
	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
			mob.location().show(mob,null,CMMsg.MSG_NOISYMOVEMENT,"<S-NAME> manage(s) to break <S-HIS-HER> way free of the burning ensnarement.");
	}

	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Host.TICK_MOB)
		&&(affected!=null)
		&&(affected instanceof MOB))
		{
			MOB vic=(MOB)affected;
			if((!vic.amDead())&&(vic.location()!=null))
				ExternalPlay.postDamage(invoker,vic,this,Dice.roll(2,4,0),CMMsg.TYP_FIRE,-1,"<T-NAME> get(s) singed from <T-HIS-HER> flaming ensnarement!");
		}
		return super.tick(ticking,tickID);
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		Hashtable h=properTargets(mob,givenTarget,auto);
		if(h==null)
		{
			mob.tell("There doesn't appear to be anyone here worth ensnaring.");
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
			if(mob.location().show(mob,null,this,affectType(auto),auto?"":"^S<S-NAME> speak(s) and wave(s) <S-HIS-HER> fingers at the ground.^?"))
			for(Enumeration f=h.elements();f.hasMoreElements();)
			{
				MOB target=(MOB)f.nextElement();

				// it worked, so build a copy of this ability,
				// and add it to the affects list of the
				// affected MOB.  Then tell everyone else
				// what happened.
				FullMsg msg=new FullMsg(mob,target,this,affectType(auto),null);
				if((mob.location().okMessage(mob,msg))&&(target.fetchEffect(this.ID())==null))
				{
					mob.location().send(mob,msg);
					if(!msg.wasModified())
					{
						amountRemaining=60;
						if(target.location()==mob.location())
						{
							success=maliciousAffect(mob,target,0,-1);
							target.location().show(target,null,CMMsg.MSG_OK_ACTION,"<S-NAME> become(s) ensnared in the flaming tendrils erupting from the ground, and is unable to move <S-HIS-HER> feet!");
						}
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,null,"<S-NAME> speak(s) and wave(s) <S-HIS-HER> fingers, but the spell fizzles.");


		// return whether it worked
		return success;
	}
}