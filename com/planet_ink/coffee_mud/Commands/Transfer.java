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
public class Transfer extends At
{
	public Transfer(){}

	private String[] access={"TRANSFER"};
	public String[] getAccessWords(){return access;}
	public boolean execute(MOB mob, Vector commands)
		throws java.io.IOException
	{
		Room room=null;
		if(commands.size()<3)
		{
			mob.tell("Transfer whom where? Try all or a mob name, followerd by a Room ID, target player name, area name, or room text!");
			return false;
		}
		commands.removeElementAt(0);
		String mobname=(String)commands.elementAt(0);
		Room curRoom=mob.location();
		Vector V=new Vector();
		if(mobname.equalsIgnoreCase("all"))
		{
			for(int i=0;i<curRoom.numInhabitants();i++)
			{
				MOB M=curRoom.fetchInhabitant(i);
				if(M!=null)
					V.addElement(M);
			}
		}
		else
		{
			boolean allFlag=false;
			if(mobname.toUpperCase().startsWith("ALL "))
			{
				mobname=mobname.substring(4);
				allFlag=true;
			}
			if(!allFlag)
				for(int s=0;s<CMLib.sessions().size();s++)
				{
					Session S=CMLib.sessions().elementAt(s);
					MOB M=S.mob();
					if((M!=null)&&(M.Name().equalsIgnoreCase(mobname)))
						V.addElement(M);
				}
			if(V.size()==0)
				for(Enumeration r=mob.location().getArea().getProperMap();r.hasMoreElements();)
				{
					Room R=(Room)r.nextElement();
					MOB M=null;
					int num=1;
					while((num<=1)||(M!=null))
					{
						M=R.fetchInhabitant(mobname+"."+num);
						if((M!=null)&&(!V.contains(M)))
						   V.addElement(M);
						num++;
						if((!allFlag)&&(V.size()>0)) break;
					}
					if((!allFlag)&&(V.size()>0)) break;
				}
			if(V.size()==0)
			{
			    try
			    {
					for(Enumeration r=CMLib.map().rooms();r.hasMoreElements();)
					{
						Room R=(Room)r.nextElement();
						MOB M=null;
						int num=1;
						while((num<=1)||(M!=null))
						{
							M=R.fetchInhabitant(mobname+"."+num);
							if((M!=null)&&(!V.contains(M)))
								V.addElement(M);
							num++;
							if((!allFlag)&&(V.size()>0)) break;
						}
						if((!allFlag)&&(V.size()>0)) break;
					}
			    }catch(NoSuchElementException nse){}
			}
		}

		if(V.size()==0)
		{
			mob.tell("Transfer whom?  '"+mobname+"' is unknown to you.");
			return false;
		}

		StringBuffer cmd = new StringBuffer(CMParms.combine(commands,1));
		if(cmd.toString().equalsIgnoreCase("here")||cmd.toString().equalsIgnoreCase("."))
			room=mob.location();
		else
			room=findRoomLiberally(mob,cmd);

		if(room==null)
		{
			mob.tell("Transfer where? '"+cmd.toString()+"' is unknown.  Enter a Room ID, player name, area name, or room text!");
			return false;
		}
		for(int i=0;i<V.size();i++)
		{
			MOB M=(MOB)V.elementAt(i);
			if(!room.isInhabitant(M))
			{
				if((mob.playerStats().tranPoofOut().length()>0)&&(mob.location()!=null))
					M.location().show(mob,M,CMMsg.MSG_OK_VISUAL,mob.playerStats().tranPoofOut());
				room.bringMobHere(M,true);
				if(mob.playerStats().tranPoofIn().length()>0)
					room.showOthers(mob,M,CMMsg.MSG_OK_VISUAL,mob.playerStats().tranPoofIn());
				if(!M.isMonster())
					CMLib.commands().postLook(M,true);
			}
		}
		if(mob.playerStats().tranPoofOut().length()==0)
			mob.tell("Done.");
		return false;
	}
	public int ticksToExecute(){return 0;}
	public boolean canBeOrdered(){return true;}
	public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),"TRANSFER");}

	public int compareTo(Object o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}
}
