package com.planet_ink.coffee_mud.web.macros;
import java.util.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;


public class AbilityNext extends StdWebMacro
{
	public String name(){return this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);}

	public String runMacro(ExternalHTTPRequests httpReq, String parm)
	{
		Hashtable parms=parseParms(parm);
		String last=httpReq.getRequestParameter("ABILITY");
		if(parms.containsKey("RESET"))
		{	
			if(last!=null) httpReq.removeRequestParameter("ABILITY");
			return "";
		}
		String ableType=httpReq.getRequestParameter("ABILITYTYPE");
		if((ableType!=null)&&(ableType.length()>0))
			parms.put(ableType,ableType);
		String domainType=httpReq.getRequestParameter("DOMAIN");
		if((domainType!=null)&&(domainType.length()>0))
			parms.put("DOMAIN",domainType);
		
		String lastID="";
		for(Enumeration a=CMClass.abilities();a.hasMoreElements();)
		{
			Ability A=(Ability)a.nextElement();
			boolean okToShow=true;
			int classType=A.classificationCode()&Ability.ALL_CODES;
			String className=httpReq.getRequestParameter("CLASS");
			
			if((className!=null)&&(className.length()>0))
			{
				int level=CMAble.getQualifyingLevel(className,A.ID());
				if(level<0)
					okToShow=false;
				else
				if(CMAble.getSecretSkill(className,A.ID()))
					okToShow=false;
				else
				{
					String levelName=httpReq.getRequestParameter("LEVEL");
					if((levelName!=null)&&(levelName.length()>0)&&(Util.s_int(levelName)!=level))
						okToShow=false;
				}
			}
			else
			{
				int level=CMAble.getQualifyingLevel("Archon",A.ID());
				if(level<0)
					okToShow=false;
				else
				if(CMAble.getAllSecretSkill(A.ID()))
					okToShow=false;
				else
				{
					String levelName=httpReq.getRequestParameter("LEVEL");
					if((levelName!=null)&&(levelName.length()>0)&&(Util.s_int(levelName)!=level))
						okToShow=false;
				}
			}
			if(okToShow)
			{
				if(parms.containsKey("DOMAIN")&&(classType==Ability.SPELL))
				{
					String domain=(String)parms.get("DOMAIN");
					if(!domain.equalsIgnoreCase(Ability.DOMAIN_DESCS[(A.classificationCode()&Ability.ALL_DOMAINS)>>5]))
					   okToShow=false;
				}
				else
				{
					boolean containsOne=false;
					for(int i=0;i<Ability.TYPE_DESCS.length;i++)
						if(parms.containsKey(Ability.TYPE_DESCS[i]))
						{ containsOne=true; break;}
					if(containsOne&&(!parms.containsKey(Ability.TYPE_DESCS[classType])))
						okToShow=false;
				}
			}
			if(parms.containsKey("NOT")) okToShow=!okToShow;
			if(okToShow)
			{
				if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!A.ID().equals(lastID))))
				{
					httpReq.addRequestParameters("ABILITY",A.ID());
					return "";
				}
				lastID=A.ID();
			}
		}
		httpReq.addRequestParameters("ABILITY","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		else
			return " @break@";
	}
}