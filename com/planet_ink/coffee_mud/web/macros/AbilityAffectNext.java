package com.planet_ink.coffee_mud.web.macros;
import java.util.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;


public class AbilityAffectNext extends StdWebMacro
{
	public String name(){return getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);}

	public String runMacro(ExternalHTTPRequests httpReq, String parm)
	{
		Hashtable parms=parseParms(parm);
		String last=(String)httpReq.getRequestParameters().get("ABILITY");
		if(parms.containsKey("RESET"))
		{	
			if(last!=null) httpReq.getRequestParameters().remove("ABILITY");
			return "";
		}
		String lastID="";
		for(Enumeration a=CMClass.abilities();a.hasMoreElements();)
		{
			Ability A=(Ability)a.nextElement();
			boolean okToShow=true;
			int classType=A.classificationCode()&Ability.ALL_CODES;
			if(CMAble.getQualifyingLevel("Archon",A.ID())>=0)
				continue;
			String ableType=(String)httpReq.getRequestParameters().get("ABILITYTYPE");
			if((ableType!=null)&&(ableType.length()>0))
				parms.put(ableType,ableType);
			boolean containsOne=false;
			for(int i=0;i<Ability.TYPE_DESCS.length;i++)
				if(parms.containsKey(Ability.TYPE_DESCS[i]))
				{ containsOne=true; break;}
			if(containsOne&&(!parms.containsKey(Ability.TYPE_DESCS[classType])))
				okToShow=false;
			if(parms.containsKey("NOT")) okToShow=!okToShow;
			if(okToShow)
			{
				if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!A.ID().equals(lastID))))
				{
					httpReq.getRequestParameters().put("ABILITY",A.ID());
					return "";
				}
				lastID=A.ID();
			}
		}
		httpReq.getRequestParameters().put("ABILITY","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		else
			return " @break@";
	}
}