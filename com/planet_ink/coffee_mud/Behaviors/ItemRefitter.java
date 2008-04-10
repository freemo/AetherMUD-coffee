package com.planet_ink.coffee_mud.Behaviors;
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
public class ItemRefitter extends StdBehavior
{
	public String ID(){return "ItemRefitter";}

	protected int cost(Item item)
	{
		int cost=item.envStats().level()*100;
		if(CMLib.flags().isABonusItems(item))
			cost+=(item.envStats().level()*100);
		return cost;
	}

	public boolean okMessage(Environmental affecting, CMMsg msg)
	{
		if(!super.okMessage(affecting,msg))
			return false;
		MOB source=msg.source();
		if(!canFreelyBehaveNormal(affecting))
			return true;
		MOB observer=(MOB)affecting;
		if((source!=observer)
		&&(msg.amITarget(observer))
		&&(msg.targetMinor()==CMMsg.TYP_GIVE)
		&&(!CMSecurity.isAllowed(source,source.location(),"CMDROOMS"))
		&&(msg.tool()!=null)
		&&(!(msg.tool() instanceof Coins))
		&&(msg.tool() instanceof Item))
		{
			Item tool=(Item)msg.tool();
			int cost=cost(tool);
			if(!(tool instanceof Armor))
			{
				CMLib.commands().postSay(observer,source,"I'm sorry, I can't refit that.",true,false);
				return false;
			}

			if(tool.baseEnvStats().height()==0)
			{
				CMLib.commands().postSay(observer,source,"This already looks your size!",true,false);
				return false;
			}
			if(CMLib.beanCounter().getTotalAbsoluteShopKeepersValue(msg.source(),observer)<new Integer(cost).doubleValue())
			{
			    String costStr=CMLib.beanCounter().nameCurrencyShort(observer,new Integer(cost).doubleValue());
				CMLib.commands().postSay(observer,source,"You'll need "+costStr+" for me to refit that.",true,false);
				return false;
			}
			return true;
		}
		return true;
	}

	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		super.executeMsg(affecting,msg);
		MOB source=msg.source();
		if(!canFreelyBehaveNormal(affecting))
			return;
		MOB observer=(MOB)affecting;

		if((source!=observer)
		&&(msg.amITarget(observer))
		&&(msg.targetMinor()==CMMsg.TYP_GIVE)
		&&(!CMSecurity.isAllowed(source,source.location(),"CMDROOMS"))
		&&(msg.tool()!=null)
		&&(!(msg.tool() instanceof Coins))
		&&(msg.tool() instanceof Armor))
		{
			int cost=cost((Item)msg.tool());
			CMLib.beanCounter().subtractMoney(source,CMLib.beanCounter().getCurrency(observer),new Integer(cost).doubleValue());
			String costStr=CMLib.beanCounter().nameCurrencyLong(observer,new Integer(cost).doubleValue());
			source.recoverEnvStats();
			((Item)msg.tool()).baseEnvStats().setHeight(0);
			((Item)msg.tool()).recoverEnvStats();

			CMMsg newMsg=CMClass.getMsg(observer,source,msg.tool(),CMMsg.MSG_GIVE,"<S-NAME> give(s) <O-NAME> to <T-NAMESELF> and charges <T-NAMESELF> "+costStr+".");
			msg.addTrailerMsg(newMsg);
			newMsg=CMClass.getMsg(observer,source,null,CMMsg.MSG_SPEAK,"^T<S-NAME> say(s) 'There she is, a perfect fit!  Thanks for your business' to <T-NAMESELF>.^?");
			msg.addTrailerMsg(newMsg);
			newMsg=CMClass.getMsg(observer,msg.tool(),null,CMMsg.MSG_DROP,null);
			msg.addTrailerMsg(newMsg);
		}
	}
}
