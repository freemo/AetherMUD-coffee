package com.planet_ink.coffee_mud.Items;


import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class GenKey extends GenItem implements Key
{
	public String ID(){	return "GenKey";}
	public GenKey()
	{
		super();
		setName("a generic key thing");
		setDisplayText("a generic key thing sits here.");
		setDescription("");
		isReadable=false;
		setMaterial(EnvResource.RESOURCE_IRON);
	}

	public Environmental newInstance()
	{
		return new GenKey();
	}
	public boolean isGeneric(){return true;}

	public void setKey(String keyName){readableText=keyName;}
	public String getKey(){return readableText;}
}
