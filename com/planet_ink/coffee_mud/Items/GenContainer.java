package com.planet_ink.coffee_mud.Items;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class GenContainer extends StdContainer
{
	public String ID(){	return "GenContainer";}
	private String readableText = "";
	public GenContainer()
	{
		super();
		name="a generic container";
		baseEnvStats.setWeight(2);
		displayText="a generic container sits here.";
		description="";
		baseGoldValue=5;
		capacity=50;
		recoverEnvStats();
	}

	public String readableText(){return readableText;}
	public void setReadableText(String text){readableText=text;}
	public String keyName()
	{
		return readableText;
	}
	public void setKeyName(String newKeyName)
	{
		readableText=newKeyName;
	}
	public Environmental newInstance()
	{
		return new GenContainer();
	}
	public boolean isGeneric(){return true;}

	public String text()
	{
		return Generic.getPropertiesStr(this,false);
	}

	public void setMiscText(String newText)
	{
		miscText="";
		Generic.setPropertiesStr(this,newText,false);
		recoverEnvStats();
	}
	private static String[] MYCODES={"HASLOCK","HASLID","CAPACITY","CONTAINTYPES"};
	public String getStat(String code)
	{
		if(Generic.getGenItemCodeNum(code)>=0)
			return Generic.getGenItemStat(this,code);
		else
		switch(getCodeNum(code))
		{
		case 0: return ""+hasALock();
		case 1: return ""+hasALid();
		case 2: return ""+capacity();
		case 3: return ""+containTypes();
		}
		return "";
	}
	public void setStat(String code, String val)
	{ 
		if(Generic.getGenItemCodeNum(code)>=0)
			Generic.setGenItemStat(this,code,val);
		else
		switch(getCodeNum(code))
		{
		case 0: setLidsNLocks(hasALid(),isOpen(),Util.s_bool(val),false); break;
		case 1: setLidsNLocks(Util.s_bool(val),isOpen(),hasALock(),false); break;
		case 2: setCapacity(Util.s_int(val)); break;
		case 3: setContainTypes(Util.s_long(val)); break;
		}
	}
	protected int getCodeNum(String code){
		for(int i=0;i<MYCODES.length;i++)
			if(code.equalsIgnoreCase(MYCODES[i])) return i;
		return -1;
	}
	private static String[] codes=null;
	public String[] getStatCodes()
	{
		if(codes!=null) return codes;
		String[] superCodes=Generic.GENITEMCODES;
		codes=new String[superCodes.length+MYCODES.length];
		int i=0;
		for(;i<=superCodes.length;i++)
			codes[i]=superCodes[i];
		for(int x=0;x<MYCODES.length;i++,x++)
			codes[i]=MYCODES[x];
		return codes;
	}
	public boolean sameAs(Environmental E)
	{
		if(!(E instanceof GenContainer)) return false;
		String[] codes=getStatCodes();
		for(int i=0;i<codes.length;i++)
			if(!E.getStat(codes[i]).equals(getStat(codes[i])))
				return false;
		return true;
	}
}
