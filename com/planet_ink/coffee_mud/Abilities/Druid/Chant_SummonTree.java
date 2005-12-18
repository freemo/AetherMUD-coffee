package com.planet_ink.coffee_mud.Abilities.Druid;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
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

public class Chant_SummonTree extends Chant_SummonPlants
{
	public String ID() { return "Chant_SummonTree"; }
	public String name(){ return "Summon Tree";}
	protected int canAffectCode(){return Ability.CAN_ITEMS;}
	protected int canTargetCode(){return 0;}
	protected int material=0;
	protected int oldMaterial=-1;

	public Item buildMyPlant(MOB mob, Room room)
	{
		int code=material&EnvResource.RESOURCE_MASK;
		Item newItem=CMClass.getStdItem("GenItem");
		String name=CMStrings.startWithAorAn(EnvResource.RESOURCE_DESCS[code].toLowerCase()+" tree");
		newItem.setName(name);
		newItem.setDisplayText(newItem.name()+" grows here.");
		newItem.setDescription("");
		newItem.baseEnvStats().setWeight(10000);
		CMLib.flags().setGettable(newItem,false);
		newItem.setMaterial(material);
		newItem.setSecretIdentity(mob.Name());
		newItem.setMiscText(newItem.text());
		room.addItem(newItem);
		newItem.setDispossessionTime(0);
		room.showHappens(CMMsg.MSG_OK_ACTION,"a tall, healthy "+EnvResource.RESOURCE_DESCS[code].toLowerCase()+" tree sprouts up.");
		room.recoverEnvStats();
		Chant_SummonTree newChant=new Chant_SummonTree();
		newChant.PlantsLocation=room;
		newChant.littlePlants=newItem;
		if(CMLib.utensils().doesOwnThisProperty(mob,room))
		{
			newChant.setInvoker(mob);
			newChant.setMiscText(mob.name());
			newItem.addNonUninvokableEffect(newChant);
		}
		else
			newChant.beneficialAffect(mob,newItem,0,(newChant.adjustedLevel(mob,0)*240)+450);
		room.recoverEnvStats();
		return newItem;
	}

	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if((PlantsLocation==null)||(littlePlants==null)) return false;
		if(PlantsLocation.myResource()!=littlePlants.material())
		{
			oldMaterial=PlantsLocation.myResource();
			PlantsLocation.setResource(littlePlants.material());
		}
		for(int i=0;i<PlantsLocation.numInhabitants();i++)
		{
			MOB M=PlantsLocation.fetchInhabitant(i);
			if(M.fetchEffect("Chopping")!=null)
			{
				unInvoke();
				break;
			}
		}
		return true;
	}

	public void unInvoke()
	{
		if((canBeUninvoked())&&(PlantsLocation!=null)&&(oldMaterial>=0))
			PlantsLocation.setResource(oldMaterial);
		super.unInvoke();
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{

		material=EnvResource.RESOURCE_OAK;
		if((mob.location().myResource()&EnvResource.MATERIAL_MASK)==EnvResource.MATERIAL_WOODEN)
			material=mob.location().myResource();
		else
		{
			Vector V=mob.location().resourceChoices();
			Vector V2=new Vector();
			if(V!=null)
			for(int v=0;v<V.size();v++)
			{
				if(((((Integer)V.elementAt(v)).intValue()&EnvResource.MATERIAL_MASK)==EnvResource.MATERIAL_WOODEN)
				&&((((Integer)V.elementAt(v)).intValue())!=EnvResource.RESOURCE_WOOD))
					V2.addElement(V.elementAt(v));
			}
			if(V2.size()>0)
				material=((Integer)V2.elementAt(CMLib.dice().roll(1,V2.size(),-1))).intValue();
		}

		return super.invoke(mob,commands,givenTarget,auto,asLevel);
	}
}
