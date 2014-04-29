package com.planet_ink.coffee_mud.WebMacros;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;
import com.planet_ink.miniweb.interfaces.*;

import java.util.*;

/*
   Copyright 2000-2014 Bo Zimmerman

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
@SuppressWarnings({"unchecked","rawtypes"})
public class AccountData extends StdWebMacro
{
	@Override public String name() { return "AccountData"; }
	@Override public boolean isAdminMacro()	{return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		java.util.Map<String,String> parms=parseParms(parm);
		String last=httpReq.getUrlParameter("ACCOUNT");
		if(last==null) return "";
		if(last.length()>0)
		{
			PlayerAccount A = CMLib.players().getLoadAccount(last);
			if(A==null) return "";
			if(parms.containsKey("NAME")||parms.containsKey("ACCOUNT"))
				return clearWebMacros(A.getAccountName());
			if(parms.containsKey("CLASS"))
				return clearWebMacros(A.ID());
			if(parms.containsKey("LASTIP"))
				return ""+A.getLastIP();
			if(parms.containsKey("LASTDATETIME"))
				return ""+CMLib.time().date2String(A.getLastDateTime());
			if(parms.containsKey("EMAIL"))
				return ""+A.getEmail();
			if(parms.containsKey("NOTES"))
				return ""+A.getNotes();
			if(parms.containsKey("ACCTEXPIRATION"))
			{
				if(A.isSet(PlayerAccount.FLAG_NOEXPIRE))
					return "Never";
				return ""+CMLib.time().date2String(A.getAccountExpiration());
			}
			for(String flag : PlayerAccount.FLAG_DESCS)
				if(parms.containsKey("IS"+flag))
					return ""+A.isSet(flag);
			if(parms.containsKey("FLAGS"))
			{
				String old=httpReq.getUrlParameter("FLAGS");
				List<String> set=null;
				if(old==null)
				{
					String matList=A.getStat("FLAG");
					set=CMParms.parseCommas(matList,true);
				}
				else
				{
					String id="";
					set=new Vector();
					for(int i=0;httpReq.isUrlParameter("FLAG"+id);id=""+(++i))
						set.add(httpReq.getUrlParameter("FLAG"+id));
				}
				StringBuffer str=new StringBuffer("");
				for(int i=0;i<PlayerAccount.FLAG_DESCS.length;i++)
				{
					str.append("<OPTION VALUE=\""+PlayerAccount.FLAG_DESCS[i]+"\"");
					if(set.contains(PlayerAccount.FLAG_DESCS[i])) str.append(" SELECTED");
					str.append(">"+CMStrings.capitalizeAndLower(PlayerAccount.FLAG_DESCS[i]));
				}
				str.append(", ");
			}
			if(parms.containsKey("IGNORE"))
				return ""+CMParms.toStringList(A.getIgnored());
		}
		return "";
	}
}
