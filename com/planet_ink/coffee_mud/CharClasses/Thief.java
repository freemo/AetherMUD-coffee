package com.planet_ink.coffee_mud.CharClasses;

import java.util.*;
import com.planet_ink.coffee_mud.utils.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;

public class Thief extends StdCharClass
{
	public Thief()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		maxHitPointsPerLevel=16;
		maxStat[CharStats.DEXTERITY]=25;
		bonusPracLevel=1;
		manaMultiplier=12;
		attackAttribute=CharStats.DEXTERITY;
		bonusAttackLevel=1;
		damageBonusPerLevel=0;
		name=myID;
	}

	public boolean playerSelectable()
	{
		return true;
	}

	public boolean qualifiesForThisClass(MOB mob)
	{
		if(mob.baseCharStats().getDexterity()>8)
			return true;
		return false;
	}

	public void outfit(MOB mob)
	{
		Weapon w=(Weapon)CMClass.getWeapon("Shortsword");
		if(mob.fetchInventory(w.ID())==null)
		{
			mob.addInventory(w);
			if(!mob.amWearingSomethingHere(Item.WIELD))
				w.wearAt(Item.WIELD);
		}
	}
	public void newCharacter(MOB mob, boolean isBorrowedClass)
	{
		for(int a=0;a<CMClass.abilities.size();a++)
		{
			Ability A=(Ability)CMClass.abilities.elementAt(a);
			if((A.qualifyingLevel(mob)>0)&&(A.classificationCode()==Ability.THIEF_SKILL))
				giveMobAbility(mob,A, isBorrowedClass);
		}
		giveMobAbility(mob,CMClass.getAbility("Skill_Climb"), isBorrowedClass);
		if(!mob.isMonster())
			outfit(mob);
		super.newCharacter(mob, isBorrowedClass);
	}

	public boolean okAffect(MOB myChar, Affect affect)
	{
		if(affect.amISource(myChar)&&(!myChar.isMonster()))
		{
			if(affect.sourceMinor()==Affect.TYP_DELICATE_HANDS_ACT)
			{
				for(int i=0;i<myChar.inventorySize();i++)
				{
					Item I=myChar.fetchInventory(i);
					if((I.amWearingAt(Item.ON_TORSO))
					 ||(I.amWearingAt(Item.HELD)&&(I instanceof Shield))
					 ||(I.amWearingAt(Item.ON_LEGS))
					 ||(I.amWearingAt(Item.ON_ARMS))
					 ||(I.amWearingAt(Item.ON_WAIST))
					 ||(I.amWearingAt(Item.ON_HEAD)))
						if((I instanceof Armor)&&(((Armor)I).material()!=Armor.CLOTH)&&(((Armor)I).material()!=Armor.LEATHER))
							if(Dice.rollPercentage()>(myChar.charStats().getDexterity()*4))
							{
								myChar.location().show(myChar,null,Affect.MSG_OK_ACTION,"<S-NAME> fumble(s) in <S-HIS-HER> maneuver!");
								return false;
							}
				}
			}
			else
			if(affect.sourceMinor()==Affect.TYP_WEAPONATTACK)
			{
				Item I=myChar.fetchWieldedItem();
				if((I!=null)&&(I instanceof Weapon))
				{
					int classification=((Weapon)I).weaponClassification();
					if(!((classification==Weapon.CLASS_SWORD)
					||(classification==Weapon.CLASS_RANGED)
					||(classification==Weapon.CLASS_NATURAL)
					||(classification==Weapon.CLASS_DAGGER))
					   )
						if(Dice.rollPercentage()>(myChar.charStats().getDexterity()*4))
						{
							myChar.location().show(myChar,null,Affect.MSG_OK_ACTION,"<S-NAME> fumble(s) horribly with "+I.name()+".");
							return false;
						}
				}
			}
		}
		return super.okAffect(myChar,affect);
	}

	public void unLevel(MOB mob)
	{
		if(mob.envStats().level()<2)
			return;
		super.unLevel(mob);

		int attArmor=((int)Math.round(Util.div(mob.charStats().getDexterity(),9.0)))+1;
		attArmor=attArmor*-1;
		mob.baseEnvStats().setArmor(mob.baseEnvStats().armor()-attArmor);
		mob.envStats().setArmor(mob.envStats().armor()-attArmor);

		mob.recoverEnvStats();
		mob.recoverCharStats();
		mob.recoverMaxState();
	}

	public void level(MOB mob)
	{
		super.level(mob);
		int attArmor=((int)Math.round(Util.div(mob.charStats().getDexterity(),9.0)))+1;
		mob.baseEnvStats().setArmor(mob.baseEnvStats().armor()-attArmor);
		mob.envStats().setArmor(mob.envStats().armor()-attArmor);
		mob.tell("^BYour stealthiness grants you a defensive bonus of ^H"+attArmor+"^?.^N");
	}
}
