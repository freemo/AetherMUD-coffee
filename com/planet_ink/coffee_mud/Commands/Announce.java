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
* <p>Portions Copyright (c) 2003 Jeremy Vyska</p>
* <p>Portions Copyright (c) 2004 Bo Zimmerman</p>

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
public class Announce extends StdCommand
{
	public Announce(){}

	private String[] access={"ANNOUNCE","ANNOUNCETO","ANNOUNCEMSG"};
	public String[] getAccessWords(){return access;}

	public void sendAnnounce(String announcement, Session S)
	{
	  	StringBuffer Message=new StringBuffer("");
        if((S.mob()!=null)&&(S.mob().playerStats()!=null)&&(S.mob().playerStats().announceMessage().length()>0))
            Message.append(S.mob().playerStats().announceMessage()+" '"+announcement+"'.^.^N");
        else
        {
    	  	int alignType=2;
            if (CMLib.flags().isEvil(S.mob()))
                alignType = 0;
            else
            if (CMLib.flags().isGood(S.mob()))
                alignType = 1;
    	  	switch(alignType)
    	  	{
    	  	  case 0:
    	  	    Message.append("^rA terrifying voice bellows out of Hell '"+announcement+"'.^N");
    	  	    break;
    	  	  case 1:
    	  	    Message.append("^wAn awe-inspiring voice thunders down from Heaven '"+announcement+"'.^N");
    	  	    break;
    	  	  case 2:
    	  	    Message.append("^pA powerful voice rings out '"+announcement+"'.^N");
    	  	    break;
    	  	}
        }
	  	S.stdPrintln(Message.toString());
	}

	public boolean execute(MOB mob, Vector commands)
		throws java.io.IOException
	{
        
        String cmd=((String)commands.firstElement()).toUpperCase();
        if((!cmd.equalsIgnoreCase("ANNOUNCEMSG"))
        &&(!cmd.equalsIgnoreCase("ANNOUNCETO"))
        &&(!cmd.equalsIgnoreCase("ANNOUNCE")))
        {
            boolean cmdm="ANNOUNCEMSG".toUpperCase().startsWith(cmd);
            boolean cmdt="ANNOUNCETO".toUpperCase().startsWith(cmd);
            boolean cmd1="ANNOUNCE".toUpperCase().startsWith(cmd);
            if(cmdm&&(!cmdt)&&(!cmd1))
                cmd="ANNOUNCEMSG";
            else
            if(cmdt&&(!cmdm)&&(!cmd1))
                cmd="ANNOUNCETO";
            else
            if(cmd1&&(!cmdm)&&(!cmdt))
                cmd="ANNOUNCE";
        }
		if(commands.size()>1)
		{
            if(cmd.equalsIgnoreCase("ANNOUNCEMSG"))
            {
                String s=CMParms.combine(commands,1);
                if(mob.playerStats()!=null)
                    mob.playerStats().setAnnounceMessage(s);
                mob.tell("Your announce message has been changed.");
            }
            else
            if((!cmd.equalsIgnoreCase("ANNOUNCETO"))
            ||(((String)commands.elementAt(1)).toUpperCase().equals("ALL")))
			{
                String text=null;
                if(cmd.equalsIgnoreCase("ANNOUNCETO"))
                    text=CMParms.combine(commands,2);
                else
                    text=CMParms.combine(commands,1);
                    
				for(int s=0;s<CMLib.sessions().size();s++)
				{
					Session S=CMLib.sessions().elementAt(s);
					if((S.mob()!=null)
					&&(S.mob().location()!=null)
					&&(CMSecurity.isAllowed(mob,S.mob().location(),"ANNOUNCE")))
						sendAnnounce(text,S);
				}
			}
			else
			{
				boolean found=false;
				for(int s=0;s<CMLib.sessions().size();s++)
				{
					Session S=CMLib.sessions().elementAt(s);
					if((S.mob()!=null)
					&&(S.mob().location()!=null)
					&&(CMSecurity.isAllowed(mob,S.mob().location(),"ANNOUNCE"))
					&&(CMLib.english().containsString(S.mob().name(),(String)commands.elementAt(1))))
					{
						sendAnnounce(CMParms.combine(commands,2),S);
						found=true;
						break;
					}
				}
				if(!found)
					mob.tell("You can't find anyone by that name.");
			}
		}
		else
			mob.tell("Usage ANNOUNCETO [ALL|(USER NAME)] (MESSAGE)\n\rANNOUNCE (MESSAGE)\n\rANNOUNCEMSG (NEW ANNOUNCE PREFIX)\n\r");
		return false;
	}
	
	public boolean canBeOrdered(){return true;}
	public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),"ANNOUNCE");}

	
}
