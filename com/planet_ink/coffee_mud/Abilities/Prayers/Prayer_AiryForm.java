package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Prayer_AiryForm extends Prayer
{
	public String ID() { return "Prayer_AiryForm"; }
	public String name(){ return "Airyform";}
	public String displayText(){ return "(Airyform)";}
	protected int canAffectCode(){return CAN_MOBS;}
	protected int canTargetCode(){return 0;}
	public int quality(){ return BENEFICIAL_SELF;}
	public int holyQuality(){ return HOLY_NEUTRAL;}
	public Environmental newInstance(){	return new Prayer_AiryForm();}


	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			mob.tell("You return to normal form.");
	}

	public boolean okAffect(Environmental myHost, Affect affect)
	{
		if(!super.okAffect(myHost,affect))
			return false;

		if((affected==null)||(!(affected instanceof MOB)))
			return true;

		MOB mob=(MOB)affected;
		if((affect.amITarget(mob))
		   &&(Util.bset(affect.targetCode(),Affect.MASK_HURT))
		   &&(affect.tool()!=null)
		   &&(affect.tool() instanceof Item))
		{
			int recovery=(int)Math.round(Util.div((affect.targetCode()-Affect.MASK_HURT),2.0));
			affect.modify(affect.source(),affect.target(),affect.tool(),affect.sourceCode(),affect.sourceMessage(),affect.targetCode()-recovery,affect.targetMessage(),affect.othersCode(),affect.othersMessage());
		}
		return true;
	}
	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;

		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=profficiencyCheck(0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			FullMsg msg=new FullMsg(mob,target,this,affectType(auto),auto?"":"^S<S-NAME> "+prayWord(mob)+" that <T-NAME> be given an airy form.^?");
			if(mob.location().okAffect(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(target,null,Affect.MSG_OK_VISUAL,"<S-NAME> shimmer(s).");
				beneficialAffect(mob,target,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,"<S-NAME> "+prayWord(mob)+" for a new form, but <S-HIS-HER> plea is not answered.");


		// return whether it worked
		return success;
	}
}
