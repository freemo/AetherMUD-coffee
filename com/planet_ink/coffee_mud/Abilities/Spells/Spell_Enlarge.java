package com.planet_ink.coffee_mud.Abilities.Spells;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Spell_Enlarge extends Spell
{

	private static final String addOnString=" of ENORMOUS SIZE!!!";

	public Spell_Enlarge()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		name="Enlarge Object";

		canBeUninvoked=true;
		isAutoinvoked=false;

		baseEnvStats().setLevel(2);

		baseEnvStats().setAbility(0);
		uses=Integer.MAX_VALUE;
		recoverEnvStats();
	}

	public Environmental newInstance()
	{
		return new Spell_Enlarge();
	}
	public int classificationCode()
	{
		return Ability.SPELL|Ability.SPELL_ALTERATION;
	}
	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		affectableStats.setWeight(affectableStats.weight()+9999);
	}

	public void unInvoke()
	{
		// undo the affects of this spell
		if(affected==null)
			return;
		if(!(affected instanceof Item))
			return;
		Item item=(Item)affected;
		if(item.name().endsWith(addOnString))
			item.setName(item.name().substring(0,item.name().length()-addOnString.length()).trim());
		int x=item.displayText().indexOf(addOnString);
		if(x>=0)
			item.setDisplayText(item.displayText().substring(0,x)+item.displayText().substring(x+addOnString.length()));
		item.recoverEnvStats();
		super.unInvoke();
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		Item target=getTarget(mob,mob.location(),givenTarget,commands);
		if(target==null) return false;

		if(mob.isMine(target))
		{
			mob.tell("You'd better put it down first.");
			return false;
		}
		if(target.fetchAffect(this.ID())!=null)
		{
			mob.tell(name()+" is already HUGE!");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=profficiencyCheck(0,auto);

		if(success)
		{
			FullMsg msg=new FullMsg(mob,target,this,affectType,auto?"":"<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>, chanting.");
			if(mob.location().okAffect(msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(mob,target,Affect.MSG_OK_ACTION,"<T-NAME> grow(s) to an enormous size!");
				beneficialAffect(mob,target,100);
				String lastWordIn=Util.lastWordIn(target.name());
				int x=target.displayText().toUpperCase().indexOf(lastWordIn.toUpperCase());
				if(x>=0)
					target.setDisplayText(target.displayText().substring(0,x+lastWordIn.length())+addOnString+target.displayText().substring(x+lastWordIn.length()));

				target.setName(target.name()+addOnString);
			}

		}
		else
			beneficialWordsFizzle(mob,target,"<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>, chanting, but nothing happens.");


		// return whether it worked
		return success;
	}
}