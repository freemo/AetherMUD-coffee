package com.planet_ink.coffee_mud.Abilities.Fighter;
import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Fighter_Warcry extends StdAbility
{
	public String ID() { return "Fighter_Warcry"; }
	public String name(){ return "War Cry";}
	public String displayText(){return "(War Cry)";}
	public int quality(){ return BENEFICIAL_OTHERS;}
	public Environmental newInstance(){	return new Fighter_Warcry();}
	private static final String[] triggerStrings = {"WARCRY"};
	public String[] triggerStrings(){return triggerStrings;}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return Ability.CAN_MOBS;}
	public int classificationCode(){return Ability.SKILL;}

	private int timesTicking=0;

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		if(invoker==null) return;
		affectableStats.setDamage(affectableStats.damage()+1+(int)Math.round(Util.div(affectableStats.damage(),4.0)));
	}

	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((affected==null)||(invoker==null)||(!(affected instanceof MOB)))
			return false;
		if((!((MOB)affected).isInCombat())&&(++timesTicking>5))
			unInvoke();
		return true;
	}

	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			mob.tell("You calm down a bit.");
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=profficiencyCheck(0,auto);
		if(success)
		{
			FullMsg msg=new FullMsg(mob,null,this,CMMsg.MSG_SPEAK,auto?"":"^S<S-NAME> scream(s) a mighty WAR CRY!!^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				Hashtable h=properTargets(mob,givenTarget,auto);
				if(h==null) return false;
				for(Enumeration e=h.elements();e.hasMoreElements();)
				{
					MOB target=(MOB)e.nextElement();
					target.location().show(target,null,CMMsg.MSG_OK_VISUAL,"<S-NAME> get(s) enraged!");
					timesTicking=0;
					beneficialAffect(mob,target,0);
				}
			}
		}
		else
			beneficialWordsFizzle(mob,null,auto?"":"<S-NAME> mumble(s) a weak war cry.");

		// return whether it worked
		return success;
	}
}