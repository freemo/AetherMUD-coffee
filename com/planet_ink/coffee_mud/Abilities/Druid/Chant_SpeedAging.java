package com.planet_ink.coffee_mud.Abilities.Druid;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Chant_SpeedAging extends Chant
{
	public String ID() { return "Chant_SpeedAging"; }
	public String name(){ return "Speed Aging";}
	protected int canAffectCode(){return 0;}
	public int quality(){return Ability.OK_OTHERS;}
	protected int overrideMana(){return Integer.MAX_VALUE;}
	public Environmental newInstance(){	return new Chant_SpeedAging();}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		Environmental target=getAnyTarget(mob,commands,givenTarget,Item.WORN_REQ_ANY);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;
		
		boolean success=profficiencyCheck(0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			FullMsg msg=new FullMsg(mob,target,this,affectType(auto),auto?"":"^S<S-NAME> chant(s) to <T-NAMESELF>.^?");
			if(mob.location().okAffect(mob,msg))
			{
				mob.location().send(mob,msg);
				Ability A=target.fetchAffect("Age");
				if((!(target instanceof MOB))&&(A==null))
				{
					if(target instanceof Food)
					{
						mob.tell(target.name()+" rots away!");
						((Item)target).destroy();
					}
					else
					if(target instanceof Item)
					{
						switch(((Item)target).material()&EnvResource.MATERIAL_MASK)
						{
							case EnvResource.MATERIAL_CLOTH:
							case EnvResource.MATERIAL_FLESH:
							case EnvResource.MATERIAL_LEATHER:
							case EnvResource.MATERIAL_PAPER:
							case EnvResource.MATERIAL_VEGETATION:
							case EnvResource.MATERIAL_WOODEN:
							{
								mob.location().showHappens(Affect.MSG_OK_VISUAL,target.name()+" rots away!");
								((Item)target).destroy();
								break;
							}
						default:
							mob.location().showHappens(Affect.MSG_OK_VISUAL,target.name()+" ages, but nothing happens to it.");
							break;
						}
					}
					else
						mob.location().showHappens(Affect.MSG_OK_VISUAL,target.name()+" ages, but nothing happens to it.");
					success=false;
				}
				else
				if((A==null)||(A.displayText().length()==0))
				{
					MOB M=(MOB)target;
					mob.location().show(M,null,Affect.MSG_OK_VISUAL,"<S-NAME> age(s) a bit.");
					M.setAgeHours(M.getAgeHours()+(M.getAgeHours()/10));
				}
				else
				{
					long start=Util.s_long(A.text());
					long age=System.currentTimeMillis()-start;
					age=age+(age/10);
					if(age<(Host.TICKS_PER_MUDDAY*Host.TICK_TIME))
						age=(Host.TICKS_PER_MUDDAY*Host.TICK_TIME);
					A.setMiscText(""+(start-age));
					if(target instanceof MOB)
						mob.location().show((MOB)target,null,Affect.MSG_OK_VISUAL,"<S-NAME> age(s) a bit.");
					else
						mob.location().showHappens(Affect.MSG_OK_VISUAL,target.name()+" ages a bit.");
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,target,"<S-NAME> chant(s) to <T-NAMESELF>, but the magic fades.");


		// return whether it worked
		return success;
	}
}