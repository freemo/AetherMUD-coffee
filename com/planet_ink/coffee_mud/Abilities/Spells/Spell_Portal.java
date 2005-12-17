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
public class Spell_Portal extends Spell
{
	public String ID() { return "Spell_Portal"; }
	public String name(){return "Portal";}
	protected int canTargetCode(){return 0;}
	public int classificationCode(){return Ability.SPELL|Ability.DOMAIN_EVOCATION;}
	public long flags(){return Ability.FLAG_TRANSPORTING;}
	protected int overrideMana(){return Integer.MAX_VALUE-90;}

	Room newRoom=null;
	Room oldRoom=null;

	public void unInvoke()
	{
		if(canBeUninvoked())
		{
			if(newRoom!=null)
			{
				newRoom.showHappens(CMMsg.MSG_OK_VISUAL,"The swirling portal closes.");
				newRoom.rawDoors()[Directions.GATE]=null;
				newRoom.rawExits()[Directions.GATE]=null;
			}
			if(oldRoom!=null)
			{
				oldRoom.showHappens(CMMsg.MSG_OK_VISUAL,"The swirling portal closes.");
				oldRoom.rawDoors()[Directions.GATE]=null;
				oldRoom.rawExits()[Directions.GATE]=null;
			}
		}
		super.unInvoke();
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{
		if((auto||mob.isMonster())&&((commands.size()<1)||(((String)commands.firstElement()).equals(mob.name()))))
		{
			commands.clear();
			commands.addElement(CMLib.map().getRandomRoom().displayText());
		}
		if(commands.size()<1)
		{
			mob.tell("Create a portal to where?");
			return false;
		}
		if((mob.location().getRoomInDir(Directions.GATE)!=null)
		||(mob.location().getExitInDir(Directions.GATE)!=null))
		{
			mob.tell("A portal cannot be created here.");
			return false;
		}
		String areaName=CMParms.combine(commands,0).trim().toUpperCase();
		int tries=0;
		while(((++tries)<10000))
		{
			Room room=CMLib.map().getRandomRoom();
			if((CMLib.flags().canAccess(mob,room))
			&&(CMLib.english().containsString(room.displayText(),areaName)))
			{
			   newRoom=room;
			   break;
			}
		}
		if(newRoom==null)
		{
		    try
		    {
				for(Enumeration r=CMLib.map().rooms();r.hasMoreElements();)
				{
					Room room=(Room)r.nextElement();
		
					if((CMLib.flags().canAccess(mob,room))
					&&(CMLib.english().containsString(room.displayText(),areaName)))
					{
					   newRoom=room;
					   break;
					}
				}
		    }catch(NoSuchElementException nse){}
		}

		if(newRoom==null)
		{
			mob.tell("You don't know of an place called '"+CMParms.combine(commands,0)+"'.");
			return false;
		}

		int profNeg=0;
		for(int i=0;i<newRoom.numInhabitants();i++)
		{
			MOB t=newRoom.fetchInhabitant(i);
			if(t!=null)
			{
				int adjustment=t.envStats().level()-mob.envStats().level();
				if(t.isMonster()) adjustment=adjustment*3;
				profNeg+=adjustment;
			}
		}
		profNeg+=newRoom.numItems()*20;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=profficiencyCheck(mob,-profNeg,auto);

		if((success)
		&&((newRoom.getRoomInDir(Directions.GATE)==null)
		&&(newRoom.getExitInDir(Directions.GATE)==null)))
		{
			CMMsg msg=CMClass.getMsg(mob,mob.location(),this,affectType(auto),"^S<S-NAME> evoke(s) a blinding, swirling portal here.^?");
			CMMsg msg2=CMClass.getMsg(mob,newRoom,this,affectType(auto),"A blinding, swirling portal appears here.");
			if((mob.location().okMessage(mob,msg))&&(newRoom.okMessage(mob,msg2)))
			{
				mob.location().send(mob,msg);
				newRoom.send(mob,msg2);
				Exit e=CMClass.getExit("GenExit");
				e.setDescription("A swirling portal to somewhere");
				e.setDisplayText("A swirling portal to somewhere");
				e.setDoorsNLocks(false,true,false,false,false,false);
				e.setExitParams("portal","close","open","closed.");
				e.setName("a swirling portal");
				Ability A1=CMClass.getAbility("Prop_RoomView");
				if(A1!=null){
					A1.setMiscText(CMLib.map().getExtendedRoomID(newRoom));
					e.addNonUninvokableEffect(A1);
				}
				Exit e2=(Exit)e.copyOf();
				Ability A2=CMClass.getAbility("Prop_RoomView");
				if(A2!=null){
					A2.setMiscText(CMLib.map().getExtendedRoomID(mob.location()));
					e2.addNonUninvokableEffect(A2);
				}
				mob.location().rawDoors()[Directions.GATE]=newRoom;
				newRoom.rawDoors()[Directions.GATE]=mob.location();
				mob.location().rawExits()[Directions.GATE]=e;
				newRoom.rawExits()[Directions.GATE]=e2;
				oldRoom=mob.location();
				beneficialAffect(mob,e,asLevel,5);
			}
		}
		else
			beneficialWordsFizzle(mob,null,"<S-NAME> attempt(s) to evoke a portal, but fizzle(s) the spell.");


		// return whether it worked
		return success;
	}
}
