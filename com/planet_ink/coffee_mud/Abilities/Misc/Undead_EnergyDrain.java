package com.planet_ink.coffee_mud.Abilities.Misc;
import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Undead_EnergyDrain extends StdAbility
{
	public String ID() { return "Undead_EnergyDrain"; }
	public String name(){ return "Energy Drain";}
	public String displayText(){ return "(Drained of Energy)";}
	protected int canAffectCode(){return CAN_MOBS;}
	protected int canTargetCode(){return CAN_MOBS;}
	public int quality(){return Ability.MALICIOUS;}
	public boolean putInCommandlist(){return false;}
	private static final String[] triggerStrings = {"DRAINENERGY"};
	public String[] triggerStrings(){return triggerStrings;}
	public Environmental newInstance(){	return new Undead_EnergyDrain();}
	public int classificationCode(){return Ability.SKILL;}
	public int levelsDown=1;

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		if(affected==null) return;
		if(levelsDown<0) return;
		int attacklevel=affectableStats.attackAdjustment()/affectableStats.level();
		affectableStats.setLevel(affectableStats.level()-levelsDown);
		if(affectableStats.level()<=0)
		{
			levelsDown=-1;
			ExternalPlay.postDeath(invoker(),(MOB)affected,null);
		}
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()-(attacklevel*levelsDown));
	}

	public void affectCharState(MOB affected, CharState affectableState)
	{
		super.affectCharState(affected,affectableState);
		if(affected==null) return;
		int hplevel=affectableState.getHitPoints()/affected.baseEnvStats().level();
		affectableState.setHitPoints(affectableState.getHitPoints()-(hplevel*levelsDown));
		int manalevel=affectableState.getMana()/affected.baseEnvStats().level();
		affectableState.setMana(affectableState.getMana()-(manalevel*levelsDown));
		int movelevel=affectableState.getMovement()/affected.baseEnvStats().level();
		affectableState.setMovement(affectableState.getMovement()-(movelevel*levelsDown));
	}

	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(affected==null) return;
		affectableStats.setClassLevel(affectableStats.getCurrentClass(),baseEnvStats().level()-levelsDown-affectableStats.combinedSubLevels());
	}

	public void unInvoke()
	{
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
			mob.tell("The energy drain is lifted.");
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		MOB target=null;
		Ability reAffect=null;
		if(mob.isInCombat())
		{
			if(mob.rangeToTarget()>0)
			{
				mob.tell("You are too far away to touch!");
				return false;
			}
			MOB victim=mob.getVictim();
				reAffect=victim.fetchEffect("Undead_WeakEnergyDrain");
			if(reAffect==null)
				reAffect=victim.fetchEffect("Undead_EnergyDrain");
			if(reAffect!=null) target=victim;
		}
		if(target==null)
			target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=profficiencyCheck(0,auto);

		String str=null;
		if(success)
		{
			str=auto?"":"^S<S-NAME> extend(s) an energy draining hand to <T-NAMESELF>!^?";
			FullMsg msg=new FullMsg(mob,target,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_UNDEAD|(auto?CMMsg.MASK_GENERAL:0),str);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(!msg.wasModified())
				{
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,"<S-NAME> <S-IS-ARE> drained!");
					if(reAffect!=null)
					{
						if(reAffect instanceof Undead_EnergyDrain)
							((Undead_EnergyDrain)reAffect).levelsDown++;
						((StdAbility)reAffect).setTickDownRemaining(((StdAbility)reAffect).getTickDownRemaining()+mob.envStats().level());
						mob.recoverEnvStats();
						mob.recoverCharStats();
						mob.recoverMaxState();
					}
					else
						success=maliciousAffect(mob,target,0,-1);
				}
			}
		}
		else
			return maliciousFizzle(mob,target,"<S-NAME> attempt(s) to drain <T-NAMESELF>, but fail(s).");

		return success;
	}
}
