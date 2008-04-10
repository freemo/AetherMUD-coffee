package com.planet_ink.coffee_mud.Abilities.Spells;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;



import java.util.*;

/*
   Copyright 2000-2008 Bo Zimmerman

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
public class Spell_SummonEnemy extends Spell
{
	public String ID() { return "Spell_SummonEnemy"; }
	public String name(){return "Summon Enemy";}
	public String displayText(){return "(Enemy Summoning)";}
	protected int canTargetCode(){return 0;}
	public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}
	public long flags(){return Ability.FLAG_TRANSPORTING|Ability.FLAG_SUMMONING;}
	protected int overrideMana(){return Integer.MAX_VALUE;}
	public int enchantQuality(){return Ability.QUALITY_INDIFFERENT;}

	public void unInvoke()
	{
		MOB mob=(MOB)affected;
		super.unInvoke();
		if((canBeUninvoked())&&(mob!=null))
		{
			if(mob.amDead()) mob.setLocation(null);
			mob.destroy();
		}
	}

	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected!=null)
		&&(affected instanceof MOB)
		&&(msg.amISource((MOB)affected)||msg.amISource(((MOB)affected).amFollowing()))
		&&(msg.sourceMinor()==CMMsg.TYP_QUIT))
		{
			unInvoke();
			if(msg.source().playerStats()!=null) msg.source().playerStats().setLastUpdated(0);
		}
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			invoker=mob;
			CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":"^S<S-NAME> conjur(s) the dark shadow of a living creature...^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				MOB target = determineMonster(mob, mob.envStats().level());
				if(target!=null)
				{
					beneficialAffect(mob,target,asLevel,0);
					target.setVictim(mob);
				}
				else
					mob.tell("Your equal could not be summoned.");
			}
		}
		else
			return beneficialWordsFizzle(mob,null,"<S-NAME> conjur(s), but nothing happens.");

		// return whether it worked
		return success;
	}
	public MOB determineMonster(MOB caster, int level)
	{
		if(caster==null) return null;
		if(caster.location()==null) return null;
		if(caster.location().getArea()==null) return null;
		MOB monster=null;
		int tries=10000000;
		while((monster==null)&&((--tries)>0))
		{
			Room room=CMLib.map().getRandomRoom();
			if((room!=null)&&CMLib.flags().canAccess(caster,room)&&(room.numInhabitants()>0))
			{
				MOB mob=room.fetchInhabitant(CMLib.dice().roll(1,room.numInhabitants(),-1));
				if((mob!=null)
				&&(!(mob instanceof Deity))
				&&(mob.envStats().level()>=level)
				&&(mob.charStats()!=null)
				&&(mob.charStats().getMyRace()!=null)
				&&(CMProps.isTheme(mob.charStats().getMyRace().availabilityCode()))
				&&(CMath.bset(mob.charStats().getMyRace().availabilityCode(),Area.THEME_SKILLONLYMASK))
                &&( (CMLib.flags().isGood(caster)&&CMLib.flags().isEvil(mob)) || (CMLib.flags().isEvil(caster)&&CMLib.flags().isGood(mob))))
        			monster=mob;
			}
		}
		if(monster==null) return null;
		monster=(MOB)monster.copyOf();
		monster.baseEnvStats().setRejuv(Integer.MAX_VALUE);
		monster.recoverCharStats();
		monster.recoverEnvStats();
		monster.recoverMaxState();
		monster.resetToMaxState();
		monster.text();
		monster.bringToLife(caster.location(),true);
		CMLib.beanCounter().clearZeroMoney(monster,null);
		monster.location().showOthers(monster,null,CMMsg.MSG_OK_ACTION,"<S-NAME> appears!");
		caster.location().recoverRoomStats();
		monster.setStartRoom(null);
		return(monster);
	}
}
