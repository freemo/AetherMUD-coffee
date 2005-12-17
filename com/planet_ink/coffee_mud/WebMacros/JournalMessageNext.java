package com.planet_ink.coffee_mud.WebMacros;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
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
public class JournalMessageNext extends StdWebMacro
{
	public String name()	{return this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);}

	public static HashSet getProtectedJournals()
	{
	    Item I=null;
	    HashSet H=new HashSet();
	    for(Enumeration e=CMClass.items();e.hasMoreElements();)
	    {
	        I=(Item)e.nextElement();
	        if((I instanceof ArchonOnly)
	        &&(!I.isGeneric()))
	            H.add(I.Name().toUpperCase().trim());
	    }
	    return H;
	}
	public static boolean isProtectedJournal(String journal)
	{
	    if(getProtectedJournals().contains(journal.toUpperCase().trim()))
	        return true;
	    return false;
	}

	public String runMacro(ExternalHTTPRequests httpReq, String parm)
	{
		Hashtable parms=parseParms(parm);
		String journal=httpReq.getRequestParameter("JOURNAL");
		if(journal==null) return " @break@";
		if(isProtectedJournal(journal))
		{
			MOB M=CMLib.map().getLoadPlayer(Authenticate.getLogin(httpReq));
			if((M==null)||(!CMSecurity.isASysOp(M)))
			    return " @break@";
		}
		Vector info=(Vector)httpReq.getRequestObjects().get("JOURNAL: "+journal);
		if(info==null)
		{
			info=CMLib.database().DBReadJournal(journal);
			httpReq.getRequestObjects().put("JOURNAL: "+journal,info);
		}
		String last=httpReq.getRequestParameter("JOURNALMESSAGE");
		if(parms.containsKey("RESET"))
		{	
			if(last!=null) httpReq.removeRequestParameter("JOURNALMESSAGE");
			return "";
		}
        MOB M=CMLib.map().getLoadPlayer(Authenticate.getLogin(httpReq));
        boolean priviledged=CMSecurity.isAllowedAnywhere(M,"JOURNALS")&&(!parms.contains("NOPRIV"));
        while(true)
        {
            if(last==null) 
                last="0";
            else
    		if(CMath.s_int(last)>=info.size())
    		{
    			httpReq.addRequestParameters("JOURNALMESSAGE","");
    			if(parms.containsKey("EMPTYOK"))
    				return "<!--EMPTY-->";
    			return " @break@";
    		}
            else
                last=""+(CMath.s_int(last)+1);
            if(CMath.s_int(last)>=info.size())
            {
                httpReq.addRequestParameters("JOURNALMESSAGE","");
                if(parms.containsKey("EMPTYOK"))
                    return "<!--EMPTY-->";
                return " @break@";
            }
            String to=((String)((Vector)info.elementAt(CMath.s_int(last))).elementAt(3));
            if(to.equalsIgnoreCase("all")
            ||((M!=null)
                &&(priviledged||(to.equalsIgnoreCase(M.Name())))))
                break;
        }
		
		httpReq.addRequestParameters("JOURNALMESSAGE",last);
		return "";
	}
}
