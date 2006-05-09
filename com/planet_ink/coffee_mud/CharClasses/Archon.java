package com.planet_ink.coffee_mud.CharClasses;
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
   Copyright 2000-2006 Bo Zimmerman

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
public class Archon extends StdCharClass
{
	public String ID(){return "Archon";}
	public String name(){return "Archon";}
	public String baseClass(){return ID();}
	private static boolean abilitiesLoaded=false;
	public boolean loaded(){return abilitiesLoaded;}
	public void setLoaded(boolean truefalse){abilitiesLoaded=truefalse;};
	public boolean leveless(){return true;}

	public Archon()
	{
		super();
		for(int i=0;i<CharStats.NUM_BASE_STATS;i++)
			maxStatAdj[i]=7;
		if(!loaded())
		{
			setLoaded(true);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"AnimalTaming",false);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"AnimalTrading",false);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"AnimalTraining",false);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Domesticating",false);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"InstrumentMaking",false);
			CMLib.ableMapper().addCharAbilityMapping(ID(),20,"PlantLore",false);
			CMLib.ableMapper().addCharAbilityMapping(ID(),10,"Scrapping",false);
			
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Common",100,"",true,true);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Resistance",100,"",true,true);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Archon_Multiwatch",100,"",true,true);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Archon_Wrath",100,"",true,true);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Archon_Hush",100,"",true,true);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Archon_Banish",100,"",true,true);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Archon_Metacraft",100,"",true,true);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Amputation",100,"",true,true);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Chant_AlterTime",true);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Chant_MoveSky",true);
			
			// temporarily here until we find a place for them
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Enslave",false);
			CMLib.ableMapper().addCharAbilityMapping(ID(),1,"SlaveTrading",false);
		}
	}

	public int availabilityCode(){return 0;}

	public String statQualifications(){return "Must be granted by another Archon.";}
	public boolean qualifiesForThisClass(MOB mob, boolean quiet)
	{
		if(!quiet)
			mob.tell("This class cannot be learned.");
		return false;
	}
	public Vector outfit(MOB myChar)
	{
		if(outfitChoices==null)
		{
			outfitChoices=new Vector();
			Weapon w=CMClass.getWeapon("ArchonStaff");
			outfitChoices.addElement(w);
		}
		return outfitChoices;
	}

	public void startCharacter(MOB mob, boolean isBorrowedClass, boolean verifyOnly)

	{
		// archons ALWAYS use borrowed abilities
		super.startCharacter(mob, true, verifyOnly);
		if(verifyOnly)
			grantAbilities(mob,true);
	}

	public void grantAbilities(MOB mob, boolean isBorrowedClass)
	{
        boolean allowed=CMSecurity.isAllowedEverywhere(mob,"ALLSKILLS");
        if((!allowed)&&(mob.playerStats()!=null)&&(!mob.playerStats().getSecurityGroups().contains("ALLSKILLS"))) 
            mob.playerStats().getSecurityGroups().addElement("ALLSKILLS");
        super.grantAbilities(mob,isBorrowedClass);
        if((!allowed)&&(mob.playerStats()!=null)&&(mob.playerStats().getSecurityGroups().contains("ALLSKILLS"))) 
            mob.playerStats().getSecurityGroups().removeElement("ALLSKILLS");
	}
}
