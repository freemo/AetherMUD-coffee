package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Prayer_MassMobility extends Prayer
{
	public String ID() { return "Prayer_MassMobility"; }
	public String name(){ return "Mass Mobility";}
	public int quality(){ return BENEFICIAL_OTHERS;}
	public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY;}
	public String displayText(){ return "(Mass Mobility)";}
	public Environmental newInstance(){	return new Prayer_MassMobility();}



	public boolean okAffect(Environmental myHost, Affect affect)
	{
		if((affected==null)||(!(affected instanceof MOB)))
			return true;

		MOB mob=(MOB)affected;
		if((affect.amITarget(mob))
		&&(Util.bset(affect.targetCode(),Affect.MASK_MALICIOUS))
		&&(affect.targetMinor()==Affect.TYP_CAST_SPELL)
		&&(affect.tool()!=null)
		&&(affect.tool() instanceof Ability)
		&&(!mob.amDead()))
		{
			Ability A=(Ability)affect.tool();
			MOB newMOB=(MOB)CMClass.getMOB("StdMOB");
			FullMsg msg=new FullMsg(newMOB,null,null,Affect.MSG_SIT,null);
			newMOB.recoverEnvStats();
			try
			{
				A.affectEnvStats(newMOB,newMOB.envStats());
				if((!Sense.aliveAwakeMobile(newMOB,true))
				   ||(Util.bset(A.flags(),Ability.FLAG_BINDING))
				   ||(!A.okAffect(newMOB,msg)))
				{
					mob.location().show(mob,affect.source(),null,Affect.MSG_OK_VISUAL,"The aura around <S-NAME> repels the "+A.name()+" from <T-NAME>.");
					return false;
				}
			}
			catch(Exception e)
			{}
		}
		return true;
	}


	public void affectCharStats(MOB affectedMOB, CharStats affectedStats)
	{
		super.affectCharStats(affectedMOB,affectedStats);
		affectedStats.setStat(CharStats.SAVE_PARALYSIS,affectedStats.getStat(CharStats.SAVE_PARALYSIS)+100);
	}

	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			mob.tell("The aura of mobility around you fades.");
	}


	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=profficiencyCheck(0,auto);

		Room room=mob.location();
		int affectType=Affect.MSG_CAST_VERBAL_SPELL;
		if(auto) affectType=affectType|Affect.MASK_GENERAL;
		if((success)&&(room!=null))
		{
			FullMsg msg=new FullMsg(mob,null,this,affectType,auto?"":"^S<S-NAME> "+prayWord(mob)+" for an aura of mobility!^?");
			if(mob.location().okAffect(mob,msg))
			{
				mob.location().send(mob,msg);
				for(int i=0;i<room.numInhabitants();i++)
				{
					MOB target=room.fetchInhabitant(i);
					if(target==null) break;

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					msg=new FullMsg(mob,target,this,affectType,"Mobility is invoked upon <T-NAME>.");
					if(mob.location().okAffect(mob,msg))
					{
						mob.location().send(mob,msg);
						beneficialAffect(mob,target,0);
					}
				}
			}
		}
		else
		{
			beneficialWordsFizzle(mob,null,"<S-NAME> "+prayWord(mob)+", but nothing happens.");
			return false;
		}
		return success;
	}
}