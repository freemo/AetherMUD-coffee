package com.planet_ink.coffee_mud.Items;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;

public class NeedleChest extends LargeChest
{
	public String ID(){	return "NeedleChest";}
	public NeedleChest()
	{
		super();
		Trap t=(Trap)CMClass.getAbility("Trap_OpenNeedle");
		if(t!=null) CoffeeUtensils.setTrapped(this,t,true);
		isLocked=false;
		setMaterial(EnvResource.RESOURCE_OAK);
	}

	public Environmental newInstance()
	{
		return new NeedleChest();
	}
}
