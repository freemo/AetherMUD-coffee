package com.planet_ink.coffee_mud.Abilities.Spells;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Spell_Blink extends Spell
{
	public Spell_Blink()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		name="Blink";

		// what the affected mob sees when they
		// bring up their affected list.
		displayText="(Blink spell)";


		quality=Ability.BENEFICIAL_OTHERS;
		canBeUninvoked=true;
		isAutoinvoked=false;

		baseEnvStats().setLevel(12);

		uses=Integer.MAX_VALUE;
		recoverEnvStats();
	}

	public Environmental newInstance()
	{
		return new Spell_Blink();
	}
	public int classificationCode()
	{
		return Ability.SPELL|Ability.DOMAIN_CONJURATION;
	}

	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;
		super.unInvoke();

		mob.tell("You stop blinking.");
	}
	
	public boolean tick(int tickID)
	{
		if((tickID==Host.MOB_TICK)&&(affected!=null)&&(affected instanceof MOB))
		{
			MOB mob=(MOB)affected;
			int roll=Dice.roll(1,8,0);
			if(mob.isInCombat())
			{
				int move=0;
				switch(roll)
				{
				case 1: move=-2; break;
				case 2: move=-1; break;
				case 7: move=1; break;
				case 8: move=2; break;
				default: move=0;
				}
				if(move==0)
					mob.location().show(mob,null,Affect.MSG_OK_VISUAL,"<S-NAME> vanish(es) and reappear(s) again.");
				else
				{
					int rangeTo=mob.rangeToTarget();
					rangeTo+=move;
					if((move==0)||(rangeTo<0)||(rangeTo>mob.location().maxRange()))
						move=0;
					else
					{
						mob.setAtRange(rangeTo);
						if(mob.getVictim().getVictim()==mob)
							mob.getVictim().setAtRange(rangeTo);
					}
					switch(move)
					{
					case 0:
						mob.location().show(mob,null,Affect.MSG_OK_VISUAL,"<S-NAME> vanish(es) and reappear(s) again.");
						break;
					case 1:
						mob.location().show(mob,null,Affect.MSG_OK_VISUAL,"<S-NAME> vanish(es) and reappear(s) a bit further from "+mob.getVictim().name()+".");
						break;
					case 2:
						mob.location().show(mob,null,Affect.MSG_OK_VISUAL,"<S-NAME> vanish(es) and reappear(s) much further from "+mob.getVictim().name()+".");
						break;
					case -1:
						mob.location().show(mob,null,Affect.MSG_OK_VISUAL,"<S-NAME> vanish(es) and reappear(s) a bit closer to "+mob.getVictim().name()+".");
						break;
					case -2:
						mob.location().show(mob,null,Affect.MSG_OK_VISUAL,"<S-NAME> vanish(es) and reappear(s) much closer to "+mob.getVictim().name()+".");
						break;
					}
				}
					
			}
			else
			if((roll>2)&&(roll<7))
				mob.location().show(mob,null,Affect.MSG_OK_VISUAL,"<S-NAME> vanish(es) and reappear(s) a few feet away.");
			else
				mob.location().show(mob,null,Affect.MSG_OK_VISUAL,"<S-NAME> vanish(es) and reappear(s) again.");
		}
		return super.tick(tickID);
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
			FullMsg msg=new FullMsg(mob,target,this,affectType,auto?"<S-NAME> begin(s) to blink!":"<S-NAME> chant(s) at <T-NAMESELF>.");
			if(mob.location().okAffect(msg))
			{
				mob.location().send(mob,msg);
				if(target.location()==mob.location())
					if(mob.envStats().level()>5)
						success=beneficialAffect(mob,target,mob.envStats().level()-4);
					else
						success=beneficialAffect(mob,target,mob.envStats().level());
			}
		}
		else
			return beneficialWordsFizzle(mob,target,"<S-NAME> chant(s) to <T-NAMESELF>, but the spell fizzles.");

		// return whether it worked
		return success;
	}
}
