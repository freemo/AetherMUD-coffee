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

public class Torturesmithing extends CraftingSkill implements ItemCraftor
{
	public String ID() { return "Torturesmithing"; }
	public String name(){ return "Torturesmithing";}
	private static final String[] triggerStrings = {"TORTURESMITH","TORTURESMITHING"};
	public String[] triggerStrings(){return triggerStrings;}
    public String supportedResourceString(){return "METAL|MITHRIL|CLOTH";}

	protected static final int RCP_FINALNAME=0;
	protected static final int RCP_LEVEL=1;
	protected static final int RCP_TICKS=2;
	protected static final int RCP_WOOD=3;
	protected static final int RCP_VALUE=4;
	protected static final int RCP_CLASSTYPE=5;
	protected static final int RCP_MISCTYPE=6;
	protected static final int RCP_CAPACITY=7;
	protected static final int RCP_ARMORDMG=8;
	protected static final int RCP_MATERIAL=9;
	protected static final int RCP_SPELL=10;

	protected Item building=null;
	protected boolean messedUp=false;

    protected Vector loadRecipes(){return super.loadRecipes("torturesmith.txt");}

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
						commonTell(mob,"You've ruined "+building.name()+"!");
					else
						mob.location().addItemRefuse(building,Item.REFUSE_PLAYER_DROP);
				}
				building=null;
			}
		}
		super.unInvoke();
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
			commonTell(mob,"Make what? Enter \""+triggerStrings[0].toLowerCase()+" list\" for a list.");
			return false;
		}
		Vector recipes=addRecipes(mob,loadRecipes());
		String str=(String)commands.elementAt(0);
		String startStr=null;
        bundling=false;
		int completion=4;
		if(str.equalsIgnoreCase("list"))
		{
			StringBuffer buf=new StringBuffer(CMStrings.padRight("Item",16)+" Lvl Material required\n\r");
			for(int r=0;r<recipes.size();r++)
			{
				Vector V=(Vector)recipes.elementAt(r);
				if(V.size()>0)
				{
					String item=replacePercent((String)V.elementAt(RCP_FINALNAME),"");
					int level=CMath.s_int((String)V.elementAt(RCP_LEVEL));
					String mat=(String)V.elementAt(RCP_MATERIAL);
					int wood=CMath.s_int((String)V.elementAt(RCP_WOOD));
					if(level<=mob.envStats().level())
						buf.append(CMStrings.padRight(item,16)+" "+CMStrings.padRight(""+level,3)+" "+wood+" "+mat.toLowerCase()+"\n\r");
				}
			}
			commonTell(mob,buf.toString());
			return true;
		}

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
                if((autoGenerate>0)||(level<=mob.envStats().level()))
				{
					foundRecipe=V;
					break;
				}
			}
		}
		if(foundRecipe==null)
		{
			commonTell(mob,"You don't know how to make a '"+recipeName+"'.  Try \""+triggerStrings[0].toLowerCase()+" list\" for a list.");
			return false;
		}
		int woodRequired=CMath.s_int((String)foundRecipe.elementAt(RCP_WOOD));
		if(amount>woodRequired) woodRequired=amount;
		String misctype=(String)foundRecipe.elementAt(RCP_MISCTYPE);
		String materialtype=(String)foundRecipe.elementAt(RCP_MATERIAL);
		int[] pm=null;
		if(materialtype.equalsIgnoreCase("wood"))
		{
		    pm=new int[1];
			pm[0]=RawMaterial.MATERIAL_WOODEN;
		}
		else
		if(materialtype.equalsIgnoreCase("metal"))
		{
		    pm=new int[2];
			pm[0]=RawMaterial.MATERIAL_METAL;
			pm[1]=RawMaterial.MATERIAL_MITHRIL;
		}
		else
		if(materialtype.equalsIgnoreCase("cloth"))
		{
		    pm=new int[1];
			pm[0]=RawMaterial.MATERIAL_CLOTH;
		}
        bundling=misctype.equalsIgnoreCase("BUNDLE");
		int[][] data=fetchFoundResourceData(mob,
											woodRequired,"wood or cloth",pm,
											0,null,null,
                                            bundling,
											autoGenerate);
		if(data==null) return false;
		woodRequired=data[0][FOUND_AMT];
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		int lostValue=destroyResources(mob.location(),data[0][FOUND_AMT],data[0][FOUND_CODE],0,null,autoGenerate);
		building=CMClass.getItem((String)foundRecipe.elementAt(RCP_CLASSTYPE));
		if(building==null)
		{
			commonTell(mob,"There's no such thing as a "+foundRecipe.elementAt(RCP_CLASSTYPE)+"!!!");
			return false;
		}
		completion=CMath.s_int((String)foundRecipe.elementAt(RCP_TICKS))-((mob.envStats().level()-CMath.s_int((String)foundRecipe.elementAt(RCP_LEVEL)))*2);
		String itemName=replacePercent((String)foundRecipe.elementAt(RCP_FINALNAME),RawMaterial.RESOURCE_DESCS[(data[0][FOUND_CODE]&RawMaterial.RESOURCE_MASK)]).toLowerCase();
		if(bundling)
			itemName="a "+woodRequired+"# "+itemName;
		else
			itemName=CMStrings.startWithAorAn(itemName);
		building.setName(itemName);
		startStr="<S-NAME> start(s) making "+building.name()+".";
		displayText="You are making "+building.name();
		verb="making "+building.name();
        playSound="hammer.wav";
		building.setDisplayText(itemName+" lies here");
		building.setDescription(itemName+". ");
		building.baseEnvStats().setWeight(woodRequired);
		building.setBaseValue(CMath.s_int((String)foundRecipe.elementAt(RCP_VALUE))+(woodRequired*(RawMaterial.RESOURCE_DATA[data[0][FOUND_CODE]&RawMaterial.RESOURCE_MASK][RawMaterial.DATA_VALUE])));
		building.setMaterial(data[0][FOUND_CODE]);
		building.baseEnvStats().setLevel(CMath.s_int((String)foundRecipe.elementAt(RCP_LEVEL)));
		building.setSecretIdentity("This is the work of "+mob.Name()+".");
		int capacity=CMath.s_int((String)foundRecipe.elementAt(RCP_CAPACITY));
		int armordmg=CMath.s_int((String)foundRecipe.elementAt(RCP_ARMORDMG));
		int hardness=RawMaterial.RESOURCE_DATA[data[0][FOUND_CODE]&RawMaterial.RESOURCE_MASK][RawMaterial.DATA_STRENGTH]-3;
		String spell=(foundRecipe.size()>RCP_SPELL)?((String)foundRecipe.elementAt(RCP_SPELL)).trim():"";
		addSpells(building,spell);
		if(building instanceof Container)
		{
			((Container)building).setCapacity(capacity+woodRequired);
			if(misctype.equalsIgnoreCase("LID"))
				((Container)building).setLidsNLocks(true,false,false,false);
			else
			if(misctype.equalsIgnoreCase("LOCK"))
			{
				((Container)building).setLidsNLocks(true,false,true,false);
				((Container)building).setKeyName(new Double(Math.random()).toString());
			}
			else
				((Container)building).setContainTypes(CMath.s_long(misctype));
		}
		if(building instanceof Rideable)
		{
			if(misctype.equalsIgnoreCase("CHAIR"))
				((Rideable)building).setRideBasis(Rideable.RIDEABLE_SIT);
			else
			if(misctype.equalsIgnoreCase("TABLE"))
				((Rideable)building).setRideBasis(Rideable.RIDEABLE_TABLE);
			else
			if(misctype.equalsIgnoreCase("LADDER"))
				((Rideable)building).setRideBasis(Rideable.RIDEABLE_LADDER);
			else
			if(misctype.equalsIgnoreCase("BED"))
				((Rideable)building).setRideBasis(Rideable.RIDEABLE_SLEEP);
		}
		if(building instanceof Armor)
		{
			double hardBonus=0.0;
			((Armor)building).setRawProperLocationBitmap(0);
			for(int wo=1;wo<Item.WORN_DESCS.length;wo++)
			{
			    ((Armor)building).baseEnvStats().setSensesMask(EnvStats.SENSE_ITEMNOREMOVE);
				String WO=Item.WORN_DESCS[wo].toUpperCase();
				if(misctype.equalsIgnoreCase(WO))
				{
					hardBonus+=Item.WORN_WEIGHTS[wo];
					((Armor)building).setRawProperLocationBitmap(CMath.pow(2,wo-1));
					((Armor)building).setRawLogicalAnd(false);
				}
				else
				if((misctype.toUpperCase().indexOf(WO+"||")>=0)
				||(misctype.toUpperCase().endsWith("||"+WO)))
				{
					if(hardBonus==0.0)
						hardBonus+=Item.WORN_WEIGHTS[wo];
					((Armor)building).setRawProperLocationBitmap(building.rawProperLocationBitmap()|CMath.pow(2,wo-1));
					((Armor)building).setRawLogicalAnd(false);
				}
				else
				if((misctype.toUpperCase().indexOf(WO+"&&")>=0)
				||(misctype.toUpperCase().endsWith("&&"+WO)))
				{
					hardBonus+=Item.WORN_WEIGHTS[wo];
					((Armor)building).setRawProperLocationBitmap(building.rawProperLocationBitmap()|CMath.pow(2,wo-1));
					((Armor)building).setRawLogicalAnd(true);
				}
			}
			int hardPoints=(int)Math.round(CMath.mul(hardBonus,hardness));
			((Armor)building).baseEnvStats().setArmor(armordmg+hardPoints+(abilityCode()-1));
		}
		if(building instanceof Drink)
		{
			if(CMLib.flags().isGettable(building))
			{
				((Drink)building).setLiquidHeld(capacity*50);
				((Drink)building).setThirstQuenched(250);
				if((capacity*50)<250)
					((Drink)building).setThirstQuenched(capacity*50);
				((Drink)building).setLiquidRemaining(0);
			}
		}
		if(bundling) building.setBaseValue(lostValue);
		building.recoverEnvStats();
		building.text();
		building.recoverEnvStats();


		messedUp=!profficiencyCheck(mob,0,auto);
		if(completion<4) completion=4;

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
		return true;
	}
}
