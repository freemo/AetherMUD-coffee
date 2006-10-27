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
import com.planet_ink.coffee_mud.Libraries.interfaces.ExpertiseLibrary;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;

/* 
   Copyright 2000-2006 Bo Zimmerman

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
public class Expertises extends StdCommand
{
	public Expertises(){}

	private String[] access={"EXPERTISES","EXPS"};
	public String[] getAccessWords(){return access;}
	public boolean execute(MOB mob, Vector commands)
		throws java.io.IOException
	{
		StringBuffer msg=new StringBuffer("");
		msg.append("\n\r^HYour expertises:^? \n\r");
		ExpertiseLibrary.ExpertiseDefinition def=null;
		int col=0;
        int colWidth=25;
		for(int e=0;e<mob.numExpertises();e++)
		{
			def=CMLib.expertises().getDefinition(mob.fetchExpertise(e));
            if(def==null)
                msg.append(CMStrings.padRight("?"+mob.fetchExpertise(e)+"^?",colWidth));
            else
            if((col>=2)&&(def.name.length()>=colWidth))
            {
                msg.append("\n\r");
                msg.append(CMStrings.padRightPreserve("^<HELP^>"+def.name+"^</HELP^>",colWidth));
                col=0;
            }
            else
            if(def.name.length()+1>=colWidth)
            {
    			msg.append(CMStrings.padRightPreserve("^<HELP^>"+def.name+"^</HELP^>",colWidth));
                int spaces=(colWidth*2)-def.name.length();
                for(int i=0;i<spaces;i++) msg.append(" ");
                col++;
            }
            else
                msg.append(CMStrings.padRight("^<HELP^>"+def.name+"^</HELP^>",colWidth));
			if((++col)>=3)
			{
				msg.append("\n\r");
				col=0;
			}
		}
		msg.append("\n\r");
		if(!mob.isMonster())
			mob.session().wraplessPrintln(msg.toString());
		return false;
	}
	
	public boolean canBeOrdered(){return true;}

	
}
