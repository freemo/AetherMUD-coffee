package com.planet_ink.coffee_mud.web.macros;
import java.util.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;


public class ClassRaceNext extends StdWebMacro
{
	public String name(){return this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);}

	public String runMacro(ExternalHTTPRequests httpReq, String parm)
	{
		Hashtable parms=parseParms(parm);
		String cclass=(String)httpReq.getRequestParameters().get("CLASS");
		if(cclass.length()==0) return " @break@";
		CharClass C=CMClass.getCharClass(cclass);
		String last=(String)httpReq.getRequestParameters().get("RACE");
		if(parms.containsKey("RESET"))
		{	
			if(last!=null) httpReq.getRequestParameters().remove("RACE");
			return "";
		}
		String lastID="";
		MOB mob=CMClass.getMOB("StdMOB");
		for(int i=0;i<CharStats.NUM_BASE_STATS;i++)
			mob.baseCharStats().setStat(i,25);
		mob.recoverCharStats();
		mob.recoverEnvStats();
		mob.recoverMaxState();
		for(int r=0;r<CMClass.races.size();r++)
		{
			Race R=(Race)CMClass.races.elementAt(r);
			mob.baseCharStats().setMyRace(R);
			mob.recoverCharStats();
			if(((R.playerSelectable())||(parms.containsKey("ALL")))
			   &&(C.qualifiesForThisClass(mob)))
			{
				if((last==null)||((last.length()>0)&&(last.equals(lastID))))
				{
					httpReq.getRequestParameters().put("RACE",R.ID());
					return "";
				}
				lastID=R.ID();
			}
		}
		httpReq.getRequestParameters().put("RACE","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		else
			return " @break@";
	}
}