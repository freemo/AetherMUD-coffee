package com.planet_ink.coffee_mud.Items.Armor;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;

public class LeatherGauntlets extends StdArmor
{
	public String ID(){	return "LeatherGauntlets";}
	public LeatherGauntlets()
	{
		super();

		setName("Leather Gauntlets");
		setDisplayText("a pair of leather gauntlets.");
		setDescription("They look like they're made of doeskin.");
		properWornBitmap=Item.ON_HANDS | Item.ON_LEFT_WRIST | Item.ON_RIGHT_WRIST;
		wornLogicalAnd=true;
		baseEnvStats().setArmor(1);
		baseEnvStats().setWeight(5);
		baseEnvStats().setAbility(0);
		baseGoldValue=5;
		recoverEnvStats();
		material=EnvResource.RESOURCE_LEATHER;
	}

}
