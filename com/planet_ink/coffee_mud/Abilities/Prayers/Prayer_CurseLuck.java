package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

/* 
   Copyright 2000-2004 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

public class Prayer_CurseLuck extends Prayer
{
	public String ID() { return "Prayer_CurseLuck"; }
	public String name(){return "Curse Luck";}
	public String displayText(){return "(Cursed Luck)";}
	public int quality(){return MALICIOUS;};
	public long flags(){return Ability.FLAG_UNHOLY|Ability.FLAG_CURSE;}
	protected int canAffectCode(){return CAN_MOBS;}
	private HashSet permProts=new HashSet();
	private int prots=4;
	boolean notAgain=false;

	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
			mob.tell("Your cursed luck fades.");
	}

	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		affectableStats.setStat(CharStats.SAVE_WATER,-100);
		affectableStats.setStat(CharStats.SAVE_UNDEAD,-100);
		affectableStats.setStat(CharStats.SAVE_TRAPS,-100);
		affectableStats.setStat(CharStats.SAVE_POISON,-100);
		affectableStats.setStat(CharStats.SAVE_PARALYSIS,-100);
		affectableStats.setStat(CharStats.SAVE_MIND,-100);
		affectableStats.setStat(CharStats.SAVE_MAGIC,-100);
		affectableStats.setStat(CharStats.SAVE_JUSTICE,-100);
		affectableStats.setStat(CharStats.SAVE_GENERAL,-100);
		affectableStats.setStat(CharStats.SAVE_GAS,-100);
		affectableStats.setStat(CharStats.SAVE_FIRE,-100);
		affectableStats.setStat(CharStats.SAVE_ELECTRIC,-100);
		affectableStats.setStat(CharStats.SAVE_DISEASE,-100);
		affectableStats.setStat(CharStats.SAVE_COLD,-100);
		affectableStats.setStat(CharStats.SAVE_ACID,-100);
	}

	public boolean okMessage(Environmental host, CMMsg msg)
	{

		if((msg.target()==affected)
		&&(affected instanceof MOB)
		&&((msg.tool()==null)||(!permProts.contains(msg.tool())))
		&&(prots>0)
		&&(msg.source().location()!=null))
		{
			boolean proceed=false;
			int sm=msg.sourceMinor();
			int tm=msg.targetMinor();
			for(int i=0;i<CharStats.affectTypeMap.length;i++)
				if((CharStats.affectTypeMap[i]>=0)
				&&((sm==CharStats.affectTypeMap[i])||(tm==CharStats.affectTypeMap[i])))
					proceed=true;
			if((msg.tool() instanceof Trap)||(proceed))
			{
				if(msg.tool()!=null)
					permProts.add(msg.tool());
				prots--;
				msg.source().location().show((MOB)msg.target(),msg.source(),this,CMMsg.MSG_OK_VISUAL,"<S-YOUPOSS> cursed luck trips!");
				if(prots==0)
					unInvoke();
			}
		}
		return super.okMessage(host,msg);
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

		boolean success=profficiencyCheck(mob,-target.envStats().level(),auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			FullMsg msg=new FullMsg(mob,target,this,affectType(auto),auto?"<T-NAME> feel(s) <T-HIS-HER> luck become cursed!":"^S<S-NAME> "+prayForWord(mob)+" to curse the luck of <T-NAMESELF>!^?");
			FullMsg msg2=new FullMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_MIND|(auto?CMMsg.MASK_GENERAL:0),null);
			if((mob.location().okMessage(mob,msg))&&(mob.location().okMessage(mob,msg2)))
			{
				mob.location().send(mob,msg);
				mob.location().send(mob,msg2);
				if((msg.value()<=0)&&(msg2.value()<=0))
					success=maliciousAffect(mob,target,0,-1);
			}
		}
		else
			return maliciousFizzle(mob,target,"<S-NAME> "+prayForWord(mob)+" to curse the luck of <T-NAMESELF>, but nothing happens.");

		// return whether it worked
		return success;
	}
}
