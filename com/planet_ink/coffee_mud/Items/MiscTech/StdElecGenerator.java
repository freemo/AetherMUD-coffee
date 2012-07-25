package com.planet_ink.coffee_mud.Items.MiscTech;
import com.planet_ink.coffee_mud.Items.Basic.StdContainer;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;

/* 
   Copyright 2000-2012 Bo Zimmerman

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
public class StdElecGenerator extends StdElecContainer implements Electronics.PowerGenerator
{
	public String ID(){	return "StdElecGenerator";}
	public StdElecGenerator()
	{
		super();
		setName("a generator");
		setDisplayText("a generator sits here.");
		setDescription("If you put the right fuel in it, I'll bet it makes power.");

		material=RawMaterial.RESOURCE_STEEL;
		setPowerCapacity(1000);
		setPowerRemaining(0);
		baseGoldValue=0;
		recoverPhyStats();
	}
	
	protected int   generatedAmtPerTick = 1;
	protected int[] generatedFuelTypes = new int[]{RawMaterial.RESOURCE_DEUTERIUM};
	
	@Override
	public long containTypes(){return Container.CONTAIN_RAWMATERIALS;}
	@Override
	public void setContainTypes(long containTypes){containType=CONTAIN_RAWMATERIALS;}
	@Override
    public int[] getConsumedFuelTypes() { return generatedFuelTypes; }
	@Override
    public void setConsumedFuelType(int[] resources) { 
		generatedFuelTypes = resources;
    }
	@Override
    public int getGeneratedAmountPerTick() { return generatedAmtPerTick; }
	@Override
    public void setGenerationAmountPerTick(int amt) 
	{
		generatedAmtPerTick=amt;
    }
	@Override
	public boolean canContain(Environmental E)
	{
		if(!super.canContain(E)) return false;
		if(E instanceof RawMaterial)
			return CMParms.contains(this.getConsumedFuelTypes(), ((RawMaterial)E).material());
		return false;
	}
	
	public void destroy()
	{
		CMLib.threads().deleteTick(this,Tickable.TICKID_ELEC_GENERATOR);
	}
	
	public void setOwner(ItemPossessor newOwner)
	{
		super.setOwner(newOwner);
		if(newOwner instanceof Room)
		{
			if(!CMLib.threads().isTicking(this, Tickable.TICKID_ELEC_GENERATOR))
    			CMLib.threads().startTickDown(this, Tickable.TICKID_ELEC_GENERATOR, 1);
		}
	}
	
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking, tickID))
			return false;
		tickStatus=Tickable.STATUS_START;
		if((tickID==Tickable.TICKID_ELEC_GENERATOR) && (activated()))
		{
			//TODO: behave like a generator
		}
		tickStatus=Tickable.STATUS_NOT;
		return !amDestroyed();
	}
}
