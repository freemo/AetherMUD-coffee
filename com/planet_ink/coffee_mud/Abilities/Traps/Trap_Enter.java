package com.planet_ink.coffee_mud.Abilities.Traps;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;

/*
   Copyright 2001-2017 Bo Zimmerman

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
public class Trap_Enter extends Trap_Trap
{
	@Override
	public String ID()
	{
		return "Trap_Enter";
	}

	private final static String	localizedName	= CMLib.lang().L("Entry Trap");

	@Override
	public String name()
	{
		return localizedName;
	}

	@Override
	protected int canAffectCode()
	{
		return Ability.CAN_EXITS | Ability.CAN_ROOMS;
	}

	@Override
	protected int canTargetCode()
	{
		return 0;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(sprung)
			return super.okMessage(myHost,msg);
		if(!super.okMessage(myHost,msg))
			return false;

		if((msg.amITarget(affected))
 		||((msg.tool()!=null)&&(msg.tool()==affected)))
		{
			if((msg.targetMinor()==CMMsg.TYP_ENTER)
			||(msg.targetMinor()==CMMsg.TYP_LEAVE)
			||(msg.targetMinor()==CMMsg.TYP_FLEE))
			{
				if(msg.targetMinor()==CMMsg.TYP_LEAVE)
					return true;
				spring(msg.source());
				return false;
			}
		}
		return true;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(sprung)
			return;

		if((msg.amITarget(affected))||((msg.tool()!=null)&&(msg.tool()==affected)))
		{
			if(msg.targetMinor()==CMMsg.TYP_LEAVE)
			{
				spring(msg.source());
			}
		}
	}
}
