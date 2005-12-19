package com.planet_ink.coffee_mud.Items.Basic;
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
public class StdInnKey extends StdKey implements InnKey
{
	public String ID(){	return "StdInnKey";}
	public ShopKeeper myShopkeeper=null;

	public StdInnKey()
	{
		super();
		setName("a metal key");
		setDisplayText("a small metal key sits here.");
		setDescription("It says it goes to room 1.");

		material=EnvResource.RESOURCE_STEEL;
		baseGoldValue=10;
		recoverEnvStats();
	}


	public boolean tick(Tickable ticking, int tickID)
	{
		if(tickID==MudHost.TICK_ITEM_BOUNCEBACK)
		{
			this.destroyed=false;
			this.setContainer(null);
			if((owner()!=null)&&(owner()==myShopkeeper))
				return false;
			if(owner()!=null)
				removeFromOwnerContainer();
			if(myShopkeeper!=null)
			{
				myShopkeeper.getShop().addStoreInventory(this,myShopkeeper); // makes a copy
				destroy();
			}
			return false;
		}
		return true;
	}

	public void hangOnRack(ShopKeeper sk)
	{
		if(myShopkeeper==null)
		{
			myShopkeeper=sk;
			int y=sk.getShop().numberInStock(this);
			setName("key to room "+(y+1));
			setDescription("The key goes to room "+(y+1)+", but will expire soon, so you better use it quickly! Give the key to your innkeeper, "+sk.name()+", when you leave.");
			setMiscText("INN"+(y+1));
		}
	}

	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(((msg.targetMinor()==CMMsg.TYP_GIVE)
		||(msg.targetMinor()==CMMsg.TYP_SELL))
		&&(myShopkeeper!=null)
		&&(msg.target()==myShopkeeper)
		&&(msg.tool()==this))
		{
			CMLib.threads().deleteTick(this,MudHost.TICK_ITEM_BOUNCEBACK);
			myShopkeeper.getShop().addStoreInventory(this,myShopkeeper); //makes a copy
			destroy();
		}
	}
	public boolean okMessage(Environmental myHost, CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if(((msg.targetMinor()==CMMsg.TYP_GIVE)
		||(msg.targetMinor()==CMMsg.TYP_SELL))
		&&(msg.target() instanceof ShopKeeper)
		&&(myShopkeeper!=null)
		&&(msg.target()!=myShopkeeper)
		&&(msg.tool()==this))
		{
			CMLib.commands().postSay((MOB)msg.target(),msg.source(),"I'm not interested.",false,false);
			return false;
		}
		else
		if((msg.sourceMinor()==CMMsg.TYP_GET)
		&&(myShopkeeper!=null)
		&&(msg.tool()==myShopkeeper)
		&&(msg.target()==this))
			CMLib.threads().startTickDown(this,MudHost.TICK_ITEM_BOUNCEBACK,CMProps.getIntVar(CMProps.SYSTEMI_TICKSPERMUDDAY));
		return true;
	}
}
