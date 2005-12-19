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
   Copyright 2000-2005 Bo Zimmerman

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
public class Spell_Summon extends Spell
{
	public String ID() { return "Spell_Summon"; }
	public String name(){return "Summon";}
	protected int canTargetCode(){return 0;}
	public int classificationCode(){return Ability.SPELL|Ability.DOMAIN_CONJURATION;}
	public long flags(){return Ability.FLAG_TRANSPORTING|Ability.FLAG_SUMMONING;}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{

		String areaName=CMParms.combine(commands,0).trim().toUpperCase();
		if((commands.size()<1)&&(!auto))
		{
			mob.tell("Summon whom?");
			return false;
		}
		else
		if(auto)
		{
			for(int i=0;i<2000;i++)
			{
				Room R=CMLib.map().getRandomRoom();
				if((CMLib.flags().canAccess(mob,R))&&(R.numInhabitants()>0))
				{	areaName=R.fetchInhabitant(CMLib.dice().roll(1,R.numInhabitants(),-1)).Name().toUpperCase(); break;}
			}
		}

		if((mob.location().fetchInhabitant(areaName)!=null)&&(!auto))
		{
			mob.tell("Better look around first.");
			return false;
		}

		Room oldRoom=null;
		MOB target=null;
		try
		{
			for(Enumeration r=CMLib.map().rooms();r.hasMoreElements();)
			{
				Room room=(Room)r.nextElement();
				if(CMLib.flags().canAccess(mob,room))
				{
					target=room.fetchInhabitant(areaName);
					if(target!=null)
					{
						oldRoom=room;
						break;
					}
				}
			}
	    }catch(NoSuchElementException nse){}

		if(oldRoom==null)
		{
			mob.tell("You can't seem to fixate on '"+CMParms.combine(commands,0)+"', perhaps they don't exist?");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int adjustment=(target.envStats().level()-mob.envStats().level())*3;
		boolean success=profficiencyCheck(mob,-adjustment,auto);
		
		if(success&&(!auto)&&(!mob.mayIFight(target))&&(!mob.getGroupMembers(new HashSet()).contains(target)))
		{
			mob.tell(target.name()+" is a player, so you must be group members, or your playerkill flags must be on for this to work.");
			success=false;
		}

		if(success)
		{
			CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_MOVE|affectType(auto),auto?"":"^S<S-NAME> summon(s) <T-NAME> in a mighty cry!^?");
			if((mob.location().okMessage(mob,msg))&&(oldRoom.okMessage(mob,msg)))
			{
				mob.location().send(mob,msg);

				MOB follower=target;
				Room newRoom=mob.location();
				CMMsg enterMsg=CMClass.getMsg(follower,newRoom,this,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,("<S-NAME> appear(s) in a burst of light.")+CMProps.msp("appear.wav",10));
				CMMsg leaveMsg=CMClass.getMsg(follower,oldRoom,this,CMMsg.MSG_LEAVE|CMMsg.MASK_MAGIC,"<S-NAME> disappear(s) in a great summoning swirl created by "+mob.name()+".");
				if(oldRoom.okMessage(follower,leaveMsg))
				{
					if(newRoom.okMessage(follower,enterMsg))
					{
						follower.makePeace();
						oldRoom.send(follower,leaveMsg);
						newRoom.bringMobHere(follower,false);
						newRoom.send(follower,enterMsg);
						follower.tell("\n\r\n\r");
						CMLib.commands().postLook(follower,true);
					}
					else
						mob.tell("Some powerful magic stifles the spell.");
				}
				else
					mob.tell("Some powerful magic stifles the spell.");
			}

		}
		else
			beneficialWordsFizzle(mob,null,"<S-NAME> attempt(s) to summon '"+areaName+"', but fail(s).");


		// return whether it worked
		return success;
	}
}
