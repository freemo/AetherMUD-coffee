package com.planet_ink.coffee_mud.CharClasses;

import java.util.*;
import com.planet_ink.coffee_mud.utils.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;

/* 
   Copyright 2000-2004 Bo Zimmerman

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
public class Monk extends StdCharClass
{
	public String ID(){return "Monk";}
	public String name(){return "Monk";}
	public String baseClass(){return "Fighter";}
	public int getMaxHitPointsLevel(){return 24;}
	public int getBonusPracLevel(){return -1;}
	public int getBonusManaLevel(){return 4;}
	public int getBonusAttackLevel(){return 2;}
	public int getAttackAttribute(){return CharStats.STRENGTH;}
	public int getLevelsPerBonusDamage(){ return 1;}
	public int getPracsFirstLevel(){return 3;}
	public int getTrainsFirstLevel(){return 4;}
	public int getHPDivisor(){return 2;}
	public int getHPDice(){return 2;}
	public int getHPDie(){return 7;}
	public int getManaDivisor(){return 4;}
	public int getManaDice(){return 1;}
	public int getManaDie(){return 4;}
	public int allowedArmorLevel(){return CharClass.ARMOR_CLOTH;}
	private static boolean abilitiesLoaded=false;
	public boolean loaded(){return abilitiesLoaded;}
	public void setLoaded(boolean truefalse){abilitiesLoaded=truefalse;};

	public Monk()
	{
		super();
		maxStatAdj[CharStats.STRENGTH]=4;
		maxStatAdj[CharStats.DEXTERITY]=4;
		if(!loaded())
		{
			setLoaded(true);
			CMAble.addCharAbilityMapping(ID(),1,"Skill_Write",25,true);
			CMAble.addCharAbilityMapping(ID(),1,"Specialization_Axe",true);
			CMAble.addCharAbilityMapping(ID(),1,"Specialization_BluntWeapon",true);
			CMAble.addCharAbilityMapping(ID(),1,"Specialization_EdgedWeapon",true);
			CMAble.addCharAbilityMapping(ID(),1,"Specialization_FlailedWeapon",true);
			CMAble.addCharAbilityMapping(ID(),1,"Specialization_Hammer",true);
			CMAble.addCharAbilityMapping(ID(),1,"Specialization_Natural",true);
			CMAble.addCharAbilityMapping(ID(),1,"Specialization_Polearm",true);
			CMAble.addCharAbilityMapping(ID(),1,"Specialization_Ranged",true);
			CMAble.addCharAbilityMapping(ID(),1,"Specialization_Sword",true);
			CMAble.addCharAbilityMapping(ID(),1,"Skill_Recall",50,true);
			CMAble.addCharAbilityMapping(ID(),1,"Skill_Swim",false);

			CMAble.addCharAbilityMapping(ID(),1,"Fighter_Kick",true);
			CMAble.addCharAbilityMapping(ID(),1,"Fighter_MonkeyPunch",false);
			CMAble.addCharAbilityMapping(ID(),1,"Thief_Hide",false);
			
			CMAble.addCharAbilityMapping(ID(),2,"Skill_Climb",false);
			
			CMAble.addCharAbilityMapping(ID(),3,"Skill_Parry",true);
			CMAble.addCharAbilityMapping(ID(),3,"Skill_TwoWeaponFighting",false);
			
			CMAble.addCharAbilityMapping(ID(),4,"Skill_Dodge",false);
			
			CMAble.addCharAbilityMapping(ID(),5,"Fighter_Rescue",true);
			
			CMAble.addCharAbilityMapping(ID(),6,"Skill_Disarm",false);
			CMAble.addCharAbilityMapping(ID(),6,"Thief_Sneak",false);
			
			CMAble.addCharAbilityMapping(ID(),7,"Fighter_DeflectProjectile",true);
			CMAble.addCharAbilityMapping(ID(),7,"Fighter_KnifeHand",false);
			
			CMAble.addCharAbilityMapping(ID(),8,"Skill_Trip",false);
			CMAble.addCharAbilityMapping(ID(),8,"Fighter_AxKick",false);
			
			CMAble.addCharAbilityMapping(ID(),9,"Fighter_BackHand",true);
			CMAble.addCharAbilityMapping(ID(),9,"Fighter_BodyToss",false);
			
			CMAble.addCharAbilityMapping(ID(),10,"Fighter_BodyFlip",false);
			
			CMAble.addCharAbilityMapping(ID(),11,"Fighter_BlindFighting",true);
			
			CMAble.addCharAbilityMapping(ID(),12,"Fighter_CatchProjectile",false);
			CMAble.addCharAbilityMapping(ID(),12,"Fighter_FlyingKick",false);
			
			CMAble.addCharAbilityMapping(ID(),13,"Fighter_WeaponBreak",true);
			CMAble.addCharAbilityMapping(ID(),13,"Fighter_Pin",false);
			
/**/		CMAble.addCharAbilityMapping(ID(),14,"Skill_Dirt",false);
			CMAble.addCharAbilityMapping(ID(),14,"Thief_Detection",false);
			
			CMAble.addCharAbilityMapping(ID(),15,"Fighter_Sweep",true);
			CMAble.addCharAbilityMapping(ID(),15,"Fighter_Cartwheel",false);
			
			CMAble.addCharAbilityMapping(ID(),16,"Fighter_SideKick",false);
			CMAble.addCharAbilityMapping(ID(),16,"Fighter_BodyShield",false);
			
			CMAble.addCharAbilityMapping(ID(),17,"Fighter_CircleParry",true);
			CMAble.addCharAbilityMapping(ID(),17,"Fighter_KiStrike",false);
			
			CMAble.addCharAbilityMapping(ID(),18,"Skill_AttackHalf",false);
			
			CMAble.addCharAbilityMapping(ID(),19,"Fighter_Tumble",true);
			
			CMAble.addCharAbilityMapping(ID(),20,"Thief_Snatch",false);
			
			CMAble.addCharAbilityMapping(ID(),21,"Fighter_Endurance",true);
			CMAble.addCharAbilityMapping(ID(),21,"Fighter_Gouge",false);
			
			CMAble.addCharAbilityMapping(ID(),22,"Fighter_CircleTrip",false);
			
			CMAble.addCharAbilityMapping(ID(),23,"Thief_Listen",true);
			
			CMAble.addCharAbilityMapping(ID(),24,"Fighter_LightningStrike",false);
			
			CMAble.addCharAbilityMapping(ID(),25,"Fighter_ReturnProjectile",true);
			
			CMAble.addCharAbilityMapping(ID(),30,"Fighter_AtemiStrike",true);
		}
	}

	public boolean playerSelectable()
	{
		return true;
	}

	public String statQualifications(){return "Strength 9+, Dexterity 9+";}
	public boolean qualifiesForThisClass(MOB mob, boolean quiet)
	{
		if(mob.baseCharStats().getStat(CharStats.STRENGTH)<=8)
		{
			if(!quiet)
				mob.tell("You need at least a 9 Strength to become a Monk.");
			return false;
		}

		if(mob.baseCharStats().getStat(CharStats.DEXTERITY)<=8)
		{
			if(!quiet)
				mob.tell("You need at least a 9 Dexterity to become a Monk.");
			return false;
		}

		return super.qualifiesForThisClass(mob,quiet);
	}

	public void unLevel(MOB mob)
	{
		if(mob.envStats().level()<2)
			return;
		super.unLevel(mob);

		int dexStat=mob.charStats().getStat(CharStats.DEXTERITY);
		int maxDexStat=(CommonStrings.getIntVar(CommonStrings.SYSTEMI_BASEMAXSTAT)
					 +mob.charStats().getStat(CharStats.MAX_STRENGTH_ADJ+CharStats.DEXTERITY));
		if(dexStat>maxDexStat) dexStat=maxDexStat;
		int attArmor=((int)Math.round(Util.div(dexStat,9.0)))+1;
		attArmor=attArmor*-1;
		mob.baseEnvStats().setArmor(mob.baseEnvStats().armor()-attArmor);
		mob.envStats().setArmor(mob.envStats().armor()-attArmor);

		mob.recoverEnvStats();
		mob.recoverCharStats();
		mob.recoverMaxState();
	}

	public boolean anyWeapons(MOB mob)
	{
		for(int i=0;i<mob.inventorySize();i++)
		{
			Item I=mob.fetchInventory(i);
			if((I!=null)
			   &&((I.amWearingAt(Item.WIELD))
			      ||(I.amWearingAt(Item.HELD))))
				return true;
		}
		return false;
	}

	public String otherBonuses(){return "Receives (Dexterity/9)+1 bonus to defense every level.  Receives 2%/lvl unarmed attack bonus.  Receives bonus attack when unarmed.  Has Slow Fall ability.  Receives 2%/level trap avoidance.";}
	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		if(affected instanceof MOB)
		{
			if((!Sense.isSleeping(affected))&&(!Sense.isSitting(affected)))
			{
				MOB mob=(MOB)affected;
				int attArmor=(((int)Math.round(Util.div(mob.charStats().getStat(CharStats.DEXTERITY),9.0)))+1)*(mob.charStats().getClassLevel(this)-1);
				affectableStats.setArmor(affectableStats.armor()-attArmor);
			}
			if(!anyWeapons((MOB)affected))
			{
				affectableStats.setSpeed(affectableStats.speed()+1.0);
				affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+((MOB)affected).charStats().getClassLevel(this));
			}
			if(affected.fetchEffect("Falling")!=null)
				affectableStats.setWeight(0);
		}
	}
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB,affectableStats);
		affectableStats.setStat(CharStats.SAVE_MIND,
			affectableStats.getStat(CharStats.SAVE_MIND)
			+(affectableStats.getClassLevel(this)*2));
		affectableStats.setStat(CharStats.SAVE_TRAPS,
			affectableStats.getStat(CharStats.SAVE_TRAPS)
			+(affectableStats.getClassLevel(this)*2));
	}
	public void level(MOB mob)
	{
		super.level(mob);
		int dexStat=mob.charStats().getStat(CharStats.DEXTERITY);
		int maxDexStat=(CommonStrings.getIntVar(CommonStrings.SYSTEMI_BASEMAXSTAT)
					 +mob.charStats().getStat(CharStats.MAX_STRENGTH_ADJ+CharStats.DEXTERITY));
		if(dexStat>maxDexStat) dexStat=maxDexStat;
		int attArmor=((int)Math.round(Util.div(dexStat,9.0)))+1;
		mob.tell("^NYour stealthiness grants you a defensive bonus of ^H"+attArmor+"^?.^N");
	}

	public void grantAbilities(MOB mob, boolean isBorrowedClass)
	{
		super.grantAbilities(mob,isBorrowedClass);
		if(mob.playerStats()==null)
		{
			Vector V=CMAble.getUpToLevelListings(ID(),
												mob.charStats().getClassLevel(ID()),
												false,
												false);
			for(Enumeration a=V.elements();a.hasMoreElements();)
			{
				Ability A=CMClass.getAbility((String)a.nextElement());
				if((A!=null)
				&&((A.classificationCode()&Ability.ALL_CODES)!=Ability.COMMON_SKILL)
				&&(!CMAble.getDefaultGain(ID(),true,A.ID())))
					giveMobAbility(mob,A,CMAble.getDefaultProfficiency(ID(),true,A.ID()),CMAble.getDefaultParm(ID(),true,A.ID()),isBorrowedClass);
			}
		}
	}

	public Vector outfit()
	{
		return null;
	}
}
