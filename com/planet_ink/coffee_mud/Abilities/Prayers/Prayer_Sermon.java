package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Prayer_Sermon extends Prayer
{
	public String ID() { return "Prayer_Sermon"; }
	public String name(){return "Sermon";}
	public String displayText(){return "(Sermon)";}
	public int quality(){return OK_OTHERS;};
	protected int canAffectCode(){return CAN_MOBS;}
	public Environmental newInstance(){	return new Prayer_Sermon();}
	public long flags(){return Ability.FLAG_CHARMING;}
	protected int overrideMana(){return Integer.MAX_VALUE;}

	private MOB charmer=null;
	private MOB getCharmer()
	{
		if(charmer!=null) return charmer;
		if((invoker!=null)&&(invoker!=affected))
			charmer=invoker;
		else
		if((text().length()>0)&&(affected instanceof MOB))
		{
			Room R=((MOB)affected).location();
			if(R!=null)
				charmer=R.fetchInhabitant(text());
		}
		if(charmer==null) return invoker;
		return charmer;
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
			ExternalPlay.follow(mob,null,false);
			ExternalPlay.standIfNecessary(mob);
			if((mob.isMonster())&&(!Sense.isMobile(mob)))
				CoffeeUtensils.wanderAway(mob,true,true);
		}
	}

	public boolean okAffect(Environmental myHost, Affect affect)
	{
		if((affected==null)||(!(affected instanceof MOB)))
			return true;

		MOB mob=(MOB)affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if((affect.amITarget(mob))
		&&(Util.bset(affect.targetCode(),Affect.MASK_MALICIOUS))
		&&(affect.amISource(mob.amFollowing())))
			unInvoke();
		else
		if((affect.amISource(mob))
		&&(Util.bset(affect.targetCode(),Affect.MASK_MALICIOUS))
		&&(affect.amITarget(mob.amFollowing())))
		{
			mob.tell("You admire "+mob.amFollowing().charStats().himher()+" too much.");
			return false;
		}
		else
		if((affect.amISource(mob))
		&&(!mob.isMonster())
		&&(affect.target() instanceof Room)
		&&(affect.targetMinor()==affect.TYP_LEAVE)
		&&(mob.amFollowing()!=null)
		&&(((Room)affect.target()).isInhabitant(mob.amFollowing())))
		{
			mob.tell("You are too enthralled to leave.");
			return false;
		}
		else
		if((affect.amISource(mob))
		&&(mob.amFollowing()!=null)
		&&(affect.sourceMinor()==Affect.TYP_NOFOLLOW))
		{
			mob.tell("You believe in "+mob.amFollowing().name()+" too much.");
			return false;
		}

		return super.okAffect(myHost,affect);
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		
		Hashtable h=new Hashtable();
		for(int i=0;i<mob.location().numInhabitants();i++)
		{
			MOB M=mob.location().fetchInhabitant(i);
			if((M!=null)&&(Sense.canBeSeenBy(M,mob))&&(M!=mob)
			&&(M.charStats().getStat(CharStats.INTELLIGENCE)>4))
				h.put(M,M);
		}
		if((h==null)||(h.size()==0))
		{
			mob.tell("There doesn't appear to be anyone here worth sermonizing to.");
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		
		boolean success=profficiencyCheck(-(h.size()*3),auto);

		if(success)
		{
			if(mob.location().show(mob,null,this,affectType(auto),auto?"":"^S<S-NAME> begin(s) sermonizing on the wonders of "+hisHerDiety(mob)+".^?"))
			for(Enumeration f=h.elements();f.hasMoreElements();)
			{
				MOB target=(MOB)f.nextElement();

				if((Sense.canBeHeardBy(mob,target))&&(mob.mayIFight(target)))
				{
					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					FullMsg msg=new FullMsg(mob,target,this,affectType(auto),null);
					if((mob.location().okAffect(mob,msg))&&(target.fetchAffect(this.ID())==null))
					{
						mob.location().send(mob,msg);
						success=maliciousAffect(mob,target,0,Affect.MSK_CAST_MALICIOUS_VERBAL|Affect.TYP_MIND|(auto?Affect.MASK_GENERAL:0));
						if(success)
						{
							if(target.getVictim()==mob)
								target.makePeace();
							target.location().show(target,null,Affect.MSG_OK_ACTION,"<S-NAME> begin(s) nodding and shouting praises to "+hisHerDiety(mob)+".");
							ExternalPlay.follow(target,mob,true);
						}
					}
				}
				else
					beneficialWordsFizzle(mob,target,"<T-NAME> seem(s) unmoved by the <S-YOUPOSS> sermon.");
			}
		}
		else
			return beneficialWordsFizzle(mob,null,"<S-NAME> forget(s) how <S-YOUPOSS> sermon to "+hisHerDiety(mob)+" goes.");


		// return whether it worked
		return success;
	}
}