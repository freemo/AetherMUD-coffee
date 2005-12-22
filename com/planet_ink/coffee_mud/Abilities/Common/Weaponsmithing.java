package com.planet_ink.coffee_mud.Abilities.Common;
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

public class Weaponsmithing extends CraftingSkill
{
	public String ID() { return "Weaponsmithing"; }
	public String name(){ return "Weaponsmithing";}
	private static final String[] triggerStrings = {"WEAPONSMITH","WEAPONSMITHING"};
	public String[] triggerStrings(){return triggerStrings;}
    public String supportedResourceString(){return "METAL|MITHRIL";}

	protected static final int RCP_FINALNAME=0;
	protected static final int RCP_LEVEL=1;
	protected static final int RCP_TICKS=2;
	protected static final int RCP_WOOD=3;
	protected static final int RCP_VALUE=4;
	protected static final int RCP_CLASSTYPE=5;
	protected static final int RCP_WEAPONCLASS=6;
	protected static final int RCP_WEAPONTYPE=7;
	protected static final int RCP_ARMORDMG=8;
	protected static final int RCP_ATTACK=9;
	protected static final int RCP_HANDS=10;
	protected static final int RCP_MAXRANGE=11;
	protected static final int RCP_EXTRAREQ=12;
	protected static final int RCP_SPELL=13;

	protected Item building=null;
	protected Item fire=null;
	protected boolean mending=false;
	protected boolean messedUp=false;

	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB)&&(tickID==MudHost.TICK_MOB))
		{
			MOB mob=(MOB)affected;
			if((building==null)
			||(fire==null)
			||(!CMLib.flags().isOnFire(fire))
			||(!mob.location().isContent(fire))
			||(mob.isMine(fire)))
			{
				messedUp=true;
				unInvoke();
			}
		}
		return super.tick(ticking,tickID);
	}

    protected Vector loadRecipes(){return super.loadRecipes("weaponsmith.txt");}

	public void unInvoke()
	{
		if(canBeUninvoked())
		{
			if((affected!=null)&&(affected instanceof MOB))
			{
				MOB mob=(MOB)affected;
				if((building!=null)&&(!aborted))
				{
					if(messedUp)
					{
						if(mending)
							commonEmote(mob,"<S-NAME> mess(es) up mending "+building.name()+".");
						else
							commonEmote(mob,"<S-NAME> mess(es) up smithing "+building.name()+".");
					}
					else
					{
						if(mending)
							building.setUsesRemaining(100);
						else
							mob.location().addItemRefuse(building,Item.REFUSE_PLAYER_DROP);
					}
				}
				building=null;
				mending=false;
			}
		}
		super.unInvoke();
	}


	protected int specClass(String weaponClass)
	{
		for(int i=0;i<Weapon.classifictionDescription.length;i++)
		{
			if(Weapon.classifictionDescription[i].equalsIgnoreCase(weaponClass))
				return i;
		}
		return -1;
	}
	protected int specType(String weaponType)
	{
		for(int i=0;i<Weapon.typeDescription.length;i++)
		{
			if(Weapon.typeDescription[i].equalsIgnoreCase(weaponType))
				return i;
		}
		return -1;
	}
	protected boolean canDo(String weaponClass, MOB mob)
	{
		if((mob.isMonster())&&(!CMLib.flags().isAnimalIntelligence(mob)))
			return true;

		String specialization="";
		switch(specClass(weaponClass))
		{
		case Weapon.CLASS_AXE: specialization="Specialization_Axe"; break;
		case Weapon.CLASS_STAFF:
		case Weapon.CLASS_HAMMER:
		case Weapon.CLASS_BLUNT: specialization="Specialization_BluntWeapon"; break;
		case Weapon.CLASS_DAGGER:
		case Weapon.CLASS_EDGED: specialization="Specialization_EdgedWeapon"; break;
		case Weapon.CLASS_FLAILED: specialization="Specialization_FlailedWeapon"; break;
		case Weapon.CLASS_POLEARM: specialization="Specialization_Polearm"; break;
		case Weapon.CLASS_SWORD: specialization="Specialization_Sword"; break;
		case Weapon.CLASS_THROWN:
		case Weapon.CLASS_RANGED: specialization="Specialization_Ranged"; break;
		default: return false;
		}
		if(mob.fetchAbility(specialization)==null) return false;
		return true;
	}

	public boolean canBeLearnedBy(MOB teacher, MOB student)
	{
		if(!super.canBeLearnedBy(teacher,student))
			return false;
		if(student==null) return true;
		if((student.fetchAbility("Specialization_Axe")==null)
		&&(student.fetchAbility("Specialization_BluntWeapon")==null)
		&&(student.fetchAbility("Specialization_EdgedWeapon")==null)
		&&(student.fetchAbility("Specialization_FlailedWeapon")==null)
		&&(student.fetchAbility("Specialization_Polearm")==null)
		&&(student.fetchAbility("Specialization_Hammer")==null)
		&&(student.fetchAbility("Specialization_Sword")==null)
		&&(student.fetchAbility("Specialization_Ranged")==null))
		{
			teacher.tell(student.name()+" has not yet specialized in any weapons.");
			student.tell("You need to specialize in a weapon type to learn "+name()+".");
			return false;
		}
		if(student.fetchAbility("Blacksmithing")==null)
		{
			teacher.tell(student.name()+" has not yet learned blacksmithing.");
			student.tell("You need to learn blacksmithing before you can learn "+name()+".");
			return false;
		}

		return true;
	}

	protected boolean canMend(MOB mob, Environmental E, boolean quiet)
	{
		if(!super.canMend(mob,E,quiet)) return false;
		Item IE=(Item)E;
		if((!(IE instanceof Weapon))
		||(((((Weapon)IE).material()&EnvResource.MATERIAL_MASK)!=EnvResource.MATERIAL_METAL)
			&&((((Weapon)IE).material()&EnvResource.MATERIAL_MASK)!=EnvResource.MATERIAL_MITHRIL)))
		{
			if(!quiet)
				commonTell(mob,"You don't know how to mend that sort of thing.");
			return false;
		}
		return true;
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{
		int autoGenerate=0;
		if((auto)&&(givenTarget==this)&&(commands.size()>0)&&(commands.firstElement() instanceof Integer))
		{
			autoGenerate=((Integer)commands.firstElement()).intValue();
			commands.removeElementAt(0);
			givenTarget=null;
		}
		randomRecipeFix(mob,addRecipes(mob,loadRecipes()),commands,autoGenerate);
		if(commands.size()==0)
		{
			commonTell(mob,"Make what? Enter \"weaponsmith list\" for a list, \"weaponsmith scan\", or \"weaponsmith mend <item>\".");
			return false;
		}
        if((!auto)
        &&(commands.size()>0)
        &&(((String)commands.firstElement()).equalsIgnoreCase("bundle")))
        {
            bundling=true;
            if(super.invoke(mob,commands,givenTarget,auto,asLevel))
                return super.bundle(mob,commands);
            return false;
        }
		Vector recipes=addRecipes(mob,loadRecipes());
		String str=(String)commands.elementAt(0);
        bundling=false;
		String startStr=null;
		int completion=4;
		if(str.equalsIgnoreCase("list"))
		{
			StringBuffer buf=new StringBuffer("Weapons <S-NAME> <S-IS-ARE> skilled at making:\n\r");
			int toggler=1;
			int toggleTop=3;
			for(int r=0;r<toggleTop;r++)
				buf.append(CMStrings.padRight("Item",17)+" Lvl "+CMStrings.padRight("Amt",3)+((r<(toggleTop-1)?" ":"")));
			buf.append("\n\r");
			for(int r=0;r<recipes.size();r++)
			{
				Vector V=(Vector)recipes.elementAt(r);
				if(V.size()>0)
				{
					String item=replacePercent((String)V.elementAt(RCP_FINALNAME),"");
					int level=CMath.s_int((String)V.elementAt(RCP_LEVEL));
					int wood=CMath.s_int((String)V.elementAt(RCP_WOOD));
					if((autoGenerate>0)||((level<=mob.envStats().level())
                                        &&((canDo((String)V.elementAt(RCP_WEAPONCLASS),mob)))))
					{
						buf.append(CMStrings.padRight(item,17)+" "+CMStrings.padRight(""+level,3)+" "+CMStrings.padRight(""+wood,3)+((toggler!=toggleTop)?" ":"\n\r"));
						if(++toggler>toggleTop) toggler=1;
					}
				}
			}
			if(toggler!=1) buf.append("\n\r");
			commonEmote(mob,buf.toString());
			return true;
		}
		if(str.equalsIgnoreCase("scan"))
			return publicScan(mob,commands);
		else
		if(str.equalsIgnoreCase("mend"))
		{
			fire=getRequiredFire(mob,autoGenerate);
			if(fire==null) return false;
			building=null;
			mending=false;
			messedUp=false;
			Vector newCommands=CMParms.parse(CMParms.combine(commands,1));
			building=getTarget(mob,mob.location(),givenTarget,newCommands,Item.WORN_REQ_UNWORNONLY);
			if(!canMend(mob,building,false)) return false;
			mending=true;
			if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
				return false;
			startStr="<S-NAME> start(s) mending "+building.name()+".";
			displayText="You are mending "+building.name();
			verb="mending "+building.name();
		}
		else
		{
			mending=false;
			fire=getRequiredFire(mob,autoGenerate);
			if(fire==null) return false;
			building=null;
			messedUp=false;
			int amount=-1;
			if((commands.size()>1)&&(CMath.isNumber((String)commands.lastElement())))
			{
				amount=CMath.s_int((String)commands.lastElement());
				commands.removeElementAt(commands.size()-1);
			}
			String recipeName=CMParms.combine(commands,0);
			Vector foundRecipe=null;
			Vector matches=matchingRecipeNames(recipes,recipeName,true);
			for(int r=0;r<matches.size();r++)
			{
				Vector V=(Vector)matches.elementAt(r);
				if(V.size()>0)
				{
					int level=CMath.s_int((String)V.elementAt(RCP_LEVEL));
					if((autoGenerate>0)||((level<=mob.envStats().level())
                                        &&(canDo((String)V.elementAt(RCP_WEAPONCLASS),mob))))
					{
						foundRecipe=V;
						break;
					}
				}
			}
			if(foundRecipe==null)
			{
				commonTell(mob,"You don't know how to make a '"+recipeName+"'.  Try 'list' instead.");
				return false;
			}
			int woodRequired=CMath.s_int((String)foundRecipe.elementAt(RCP_WOOD));
			if(amount>woodRequired) woodRequired=amount;
			String otherRequired=(String)foundRecipe.elementAt(RCP_EXTRAREQ);
			int[] pm={EnvResource.MATERIAL_METAL,EnvResource.MATERIAL_MITHRIL};
            String spell=(foundRecipe.size()>RCP_SPELL)?((String)foundRecipe.elementAt(RCP_SPELL)).trim():"";
            bundling=spell.equalsIgnoreCase("BUNDLE");
			int[][] data=fetchFoundResourceData(mob,
												woodRequired,"metal",pm,
												otherRequired.length()>0?1:0,otherRequired,null,
												false,
												autoGenerate);
			if(data==null) return false;
			woodRequired=data[0][FOUND_AMT];

			if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
				return false;
			int lostValue=destroyResources(mob.location(),woodRequired,data[0][FOUND_CODE],data[1][FOUND_CODE],null,autoGenerate);
			building=CMClass.getItem((String)foundRecipe.elementAt(RCP_CLASSTYPE));
			if(building==null)
			{
				commonTell(mob,"There's no such thing as a "+foundRecipe.elementAt(RCP_CLASSTYPE)+"!!!");
				return false;
			}
			completion=CMath.s_int((String)foundRecipe.elementAt(RCP_TICKS))-((mob.envStats().level()-CMath.s_int((String)foundRecipe.elementAt(RCP_LEVEL)))*2);
			String itemName=replacePercent((String)foundRecipe.elementAt(RCP_FINALNAME),EnvResource.RESOURCE_DESCS[(data[0][FOUND_CODE]&EnvResource.RESOURCE_MASK)]).toLowerCase();
			itemName=CMStrings.startWithAorAn(itemName);
			building.setName(itemName);
			startStr="<S-NAME> start(s) smithing "+building.name()+".";
			displayText="You are smithing "+building.name();
			verb="smithing "+building.name();
            playSound="tinktinktink2.wav";
			int hardness=EnvResource.RESOURCE_DATA[data[0][FOUND_CODE]&EnvResource.RESOURCE_MASK][EnvResource.DATA_STRENGTH]-6;
			building.setDisplayText(itemName+" is here");
			building.setDescription(itemName+". ");
			building.baseEnvStats().setWeight(woodRequired);
			building.setBaseValue((CMath.s_int((String)foundRecipe.elementAt(RCP_VALUE))/4)+(woodRequired*(EnvResource.RESOURCE_DATA[data[0][FOUND_CODE]&EnvResource.RESOURCE_MASK][EnvResource.DATA_VALUE])));
			building.setMaterial(data[0][FOUND_CODE]);
			building.baseEnvStats().setLevel(CMath.s_int((String)foundRecipe.elementAt(RCP_LEVEL))+(hardness*3));
			building.setSecretIdentity("This is the work of "+mob.Name()+".");
			if(bundling) building.setBaseValue(lostValue);
			addSpells(building,spell);
			if(building instanceof Weapon)
			{
				Weapon w=(Weapon)building;
				w.setWeaponClassification(specClass((String)foundRecipe.elementAt(RCP_WEAPONCLASS)));
				w.setWeaponType(specType((String)foundRecipe.elementAt(RCP_WEAPONTYPE)));
				w.setRanges(w.minRange(),CMath.s_int((String)foundRecipe.elementAt(RCP_MAXRANGE)));
			}
			if(CMath.s_int((String)foundRecipe.elementAt(RCP_HANDS))==2)
				building.setRawLogicalAnd(true);
			building.baseEnvStats().setAttackAdjustment(CMath.s_int((String)foundRecipe.elementAt(RCP_ATTACK))+(hardness*5)+(abilityCode()-1));
			building.baseEnvStats().setDamage(CMath.s_int((String)foundRecipe.elementAt(RCP_ARMORDMG))+hardness);

			building.recoverEnvStats();
			building.text();
			building.recoverEnvStats();
		}

		messedUp=!profficiencyCheck(mob,0,auto);
		if(completion<6) completion=6;

		if(bundling)
		{
			messedUp=false;
			completion=1;
			verb="bundling "+EnvResource.RESOURCE_DESCS[building.material()&EnvResource.RESOURCE_MASK].toLowerCase();
			startStr="<S-NAME> start(s) "+verb+".";
			displayText="You are "+verb;
		}

		if(autoGenerate>0)
		{
			commands.addElement(building);
			return true;
		}

		CMMsg msg=CMClass.getMsg(mob,building,this,CMMsg.MSG_NOISYMOVEMENT,startStr);
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			building=(Item)msg.target();
			beneficialAffect(mob,mob,asLevel,completion);
		}
		else
		if(bundling)
		{
			messedUp=false;
			aborted=false;
			unInvoke();
		}
		return true;
	}
}
