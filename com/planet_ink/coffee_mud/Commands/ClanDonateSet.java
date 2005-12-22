package com.planet_ink.coffee_mud.Commands;
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
public class ClanDonateSet extends BaseClanner
{
	public ClanDonateSet(){}

	private String[] access={getScr("ClanDonateSet","cmd")};
	public String[] getAccessWords(){return access;}
	public boolean execute(MOB mob, Vector commands)
		throws java.io.IOException
	{
		boolean skipChecks=mob.Name().equals(mob.getClanID());
		commands.setElementAt("clandonateset",0);

		Room R=mob.location();
		if(skipChecks)
			R=CMLib.map().getRoom(CMParms.combine(commands,1));
		else
		{
			commands.clear();
			commands.addElement("clandonateset");
			commands.addElement(CMLib.map().getExtendedRoomID(R));
		}

		if((mob.getClanID()==null)||(mob.getClanID().equalsIgnoreCase("")))
		{
			mob.tell(getScr("ClanDonateSet","evenmember"));
			return false;
		}
		Clan C=CMLib.clans().getClan(mob.getClanID());
		if(C==null)
		{
			mob.tell(getScr("ClanDonateSet","nolonger",mob.getClanID()));
			return false;
		}
		if(C.getStatus()>Clan.CLANSTATUS_ACTIVE)
		{
			mob.tell(getScr("ClanDonateSet","donroom",C.typeName()));
			return false;
		}
		if(skipChecks||goForward(mob,C,commands,Clan.FUNC_CLANDONATESET,false))
		{
			if(!CMLib.utensils().doesOwnThisProperty(C.clanID(),R))
			{
				mob.tell(getScr("ClanDonateSet","donotownroom",C.typeName()));
				return false;
			}
			if(skipChecks||goForward(mob,C,commands,Clan.FUNC_CLANDONATESET,true))
			{
				C.setDonation(CMLib.map().getExtendedRoomID(R));
				C.update();
				mob.tell(getScr("ClanDonateSet","donationset",C.typeName(),C.clanID(),R.roomTitle()));
				clanAnnounce(mob,getScr("ClanDonateSet","donationset",C.typeName(),C.clanID(),R.roomTitle()));
				return true;
			}
		}
		else
		{
			mob.tell(getScr("ClanDonateSet","notrightpos",C.typeName()));
			return false;
		}
		return false;
	}
	
	public boolean canBeOrdered(){return false;}

	
}
