package com.planet_ink.coffee_mud.Abilities.Common;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;
import java.io.File;

public class Carpentry extends CraftingSkill
{
	public String ID() { return "Carpentry"; }
	public String name(){ return "Carpentry";}
	private static final String[] triggerStrings = {"CARVE","CARPENTRY"};
	public String[] triggerStrings(){return triggerStrings;}

	private static final int RCP_FINALNAME=0;
	private static final int RCP_LEVEL=1;
	private static final int RCP_TICKS=2;
	private static final int RCP_WOOD=3;
	private static final int RCP_VALUE=4;
	private static final int RCP_CLASSTYPE=5;
	private static final int RCP_MISCTYPE=6;
	private static final int RCP_CAPACITY=7;
	private static final int RCP_ARMORDMG=8;
	private static final int RCP_CONTAINMASK=9;
	private static final int RCP_SPELL=10;


	private Item building=null;
	private Item key=null;
	private boolean mending=false;
	private boolean refitting=false;
	private boolean messedUp=false;
	private static boolean mapped=false;
	public Carpentry()
	{
		super();
		if(!mapped){mapped=true;
					CMAble.addCharAbilityMapping("All",1,ID(),false);}
	}
	public Environmental newInstance(){return new Carpentry();}

	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB)&&(tickID==MudHost.TICK_MOB))
		{
			if(building==null)
				unInvoke();
		}
		return super.tick(ticking,tickID);
	}

	protected Vector loadRecipes()
	{
		Vector V=(Vector)Resources.getResource("CARPENTRY RECIPES");
		if(V==null)
		{
			StringBuffer str=Resources.getFile("resources"+File.separatorChar+"skills"+File.separatorChar+"carpentry.txt");
			V=loadList(str);
			if(V.size()==0)
				Log.errOut("Carpentry","Recipes not found!");
			Resources.submitResource("CARPENTRY RECIPES",V);
		}
		return V;
	}

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
						if(refitting)
							commonEmote(mob,"<S-NAME> mess(es) up refitting "+building.name()+".");
						else
							commonEmote(mob,"<S-NAME> mess(es) up carving "+building.name()+".");
					}
					else
					{
						if(mending)
							building.setUsesRemaining(100);
						else
						if(refitting)
						{
							building.baseEnvStats().setHeight(0);
							building.recoverEnvStats();
						}
						else
						{
							mob.location().addItemRefuse(building,Item.REFUSE_PLAYER_DROP);
							if(key!=null)
							{
								mob.location().addItemRefuse(key,Item.REFUSE_PLAYER_DROP);
								key.setContainer(building);
							}
						}
					}
				}
				building=null;
				key=null;
				mending=false;
			}
		}
		super.unInvoke();
	}

	protected boolean canMend(MOB mob, Environmental E, boolean quiet)
	{
		if(!super.canMend(mob,E,quiet)) return false;
		Item IE=(Item)E;
		if((IE.material()&EnvResource.MATERIAL_MASK)!=EnvResource.MATERIAL_WOODEN)
		{
			if(!quiet)
				commonTell(mob,"That's not made of wood.  That can't be mended.");
			return false;
		}
		return true;
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		int autoGenerate=0;
		if((auto)&&(givenTarget==this)&&(commands.size()>0)&&(commands.firstElement() instanceof Integer))
		{	
			autoGenerate=((Integer)commands.firstElement()).intValue(); 
			commands.removeElementAt(0);
			givenTarget=null;
		}
		randomRecipeFix(mob,loadRecipes(),commands,autoGenerate);
		if(commands.size()==0)
		{
			commonTell(mob,"Carve what? Enter \"carve list\" for a list, \"carve refit <item>\" to resize shoes or armor, \"carve scan\", or \"carve mend <item>\".");
			return false;
		}
		Vector recipes=loadRecipes();
		String str=(String)commands.elementAt(0);
		String startStr=null;
		int completion=4;
		boolean bundle=false;
		if(str.equalsIgnoreCase("list"))
		{
			StringBuffer buf=new StringBuffer("Item <S-NAME> <S-IS-ARE> skilled at carving:\n\r");
			int toggler=1;
			int toggleTop=3;
			for(int r=0;r<toggleTop;r++)
				buf.append(Util.padRight("Item",17)+" "+Util.padRight("Wood",5)+" ");
			buf.append("\n\r");
			for(int r=0;r<recipes.size();r++)
			{
				Vector V=(Vector)recipes.elementAt(r);
				if(V.size()>0)
				{
					String item=replacePercent((String)V.elementAt(RCP_FINALNAME),"");
					int level=Util.s_int((String)V.elementAt(RCP_LEVEL));
					int wood=Util.s_int((String)V.elementAt(RCP_WOOD));
					if(level<=mob.envStats().level())
					{
						buf.append(Util.padRight(item,17)+" "+Util.padRight(""+wood,5)+((toggler!=toggleTop)?" ":"\n\r"));
						if(++toggler>toggleTop) toggler=1;
					}
				}
			}
			if(toggler!=1) buf.append("\n\r");
			commonTell(mob,buf.toString());
			return true;
		}
		if(str.equalsIgnoreCase("scan"))
			return publicScan(mob,commands);
		else
		if(str.equalsIgnoreCase("mend"))
		{
			building=null;
			mending=false;
			key=null;
			messedUp=false;
			Vector newCommands=Util.parse(Util.combine(commands,1));
			building=getTarget(mob,mob.location(),givenTarget,newCommands,Item.WORN_REQ_UNWORNONLY);
			if(!canMend(mob, building,false)) return false;
			mending=true;
			if(!super.invoke(mob,commands,givenTarget,auto))
				return false;
			startStr="<S-NAME> start(s) mending "+building.name()+".";
			displayText="You are mending "+building.name();
			verb="mending "+building.name();
		}
		else
		if(str.equalsIgnoreCase("refit"))
		{
			building=null;
			mending=false;
			refitting=false;
			messedUp=false;
			Vector newCommands=Util.parse(Util.combine(commands,1));
			building=getTarget(mob,mob.location(),givenTarget,newCommands,Item.WORN_REQ_UNWORNONLY);
			if(building==null) return false;
			if((building.material()&EnvResource.MATERIAL_MASK)!=EnvResource.MATERIAL_WOODEN)
			{
				commonTell(mob,"That's not made of wood.  That can't be refitted.");
				return false;
			}
			if(!(building instanceof Armor))
		    {
				commonTell(mob,"You don't know how to refit that sort of thing.");
				return false;
			}
			if(((Item)building).envStats().height()==0)
			{
				commonTell(mob,building.name()+" is already the right size.");
				return false;
			}
			refitting=true;
			if(!super.invoke(mob,commands,givenTarget,auto))
				return false;
			startStr="<S-NAME> start(s) refitting "+building.name()+".";
			displayText="You are refitting "+building.name();
			verb="refitting "+building.name();
		}
		else
		{
			building=null;
			mending=false;
			key=null;
			messedUp=false;
			int amount=-1;
			if((commands.size()>1)&&(Util.isNumber((String)commands.lastElement())))
			{
				amount=Util.s_int((String)commands.lastElement());
				commands.removeElementAt(commands.size()-1);
			}
			String recipeName=Util.combine(commands,0);
			Vector foundRecipe=null;
			Vector matches=matchingRecipeNames(recipes,recipeName);
			for(int r=0;r<matches.size();r++)
			{
				Vector V=(Vector)matches.elementAt(r);
				if(V.size()>0)
				{
					int level=Util.s_int((String)V.elementAt(RCP_LEVEL));
					if(level<=mob.envStats().level())
					{
						foundRecipe=V;
						break;
					}
				}
			}
			if(foundRecipe==null)
			{
				commonTell(mob,"You don't know how to carve a '"+recipeName+"'.  Try \"carve list\" for a list.");
				return false;
			}
			
			int woodRequired=Util.s_int((String)foundRecipe.elementAt(RCP_WOOD));
			if(amount>woodRequired) woodRequired=amount;
			String misctype=(String)foundRecipe.elementAt(RCP_MISCTYPE);
			int[] pm={EnvResource.MATERIAL_WOODEN};
			int[][] data=fetchFoundResourceData(mob,
												woodRequired,"wood",pm,
												0,null,null,
												misctype.equalsIgnoreCase("BUNDLE"),
												autoGenerate);
			if(data==null) return false;
			woodRequired=data[0][FOUND_AMT];
			if(!super.invoke(mob,commands,givenTarget,auto))
				return false;
			int lostValue=destroyResources(mob.location(),woodRequired,data[0][FOUND_CODE],0,null,autoGenerate);
			building=CMClass.getItem((String)foundRecipe.elementAt(RCP_CLASSTYPE));
			if(building==null)
			{
				commonTell(mob,"There's no such thing as a "+foundRecipe.elementAt(RCP_CLASSTYPE)+"!!!");
				return false;
			}
			completion=Util.s_int((String)foundRecipe.elementAt(this.RCP_TICKS))-((mob.envStats().level()-Util.s_int((String)foundRecipe.elementAt(RCP_LEVEL)))*2);
			String itemName=replacePercent((String)foundRecipe.elementAt(RCP_FINALNAME),EnvResource.RESOURCE_DESCS[(data[0][FOUND_CODE]&EnvResource.RESOURCE_MASK)]).toLowerCase();
			if(misctype.equalsIgnoreCase("BUNDLE")) 
				itemName="a "+woodRequired+"# "+itemName;
			else
				itemName=Util.startWithAorAn(itemName);
			building.setName(itemName);
			startStr="<S-NAME> start(s) carving "+building.name()+".";
			displayText="You are carving "+building.name();
			verb="carving "+building.name();
			building.setDisplayText(itemName+" is here");
			building.setDescription(itemName+". ");
			building.baseEnvStats().setWeight(woodRequired);
			building.setBaseValue(Util.s_int((String)foundRecipe.elementAt(RCP_VALUE)));
			building.setMaterial(data[0][FOUND_CODE]);
			int hardness=EnvResource.RESOURCE_DATA[data[0][FOUND_CODE]&EnvResource.RESOURCE_MASK][EnvResource.DATA_STRENGTH]-3;
			building.baseEnvStats().setLevel(Util.s_int((String)foundRecipe.elementAt(RCP_LEVEL))+(hardness));
			if(building.baseEnvStats().level()<1) building.baseEnvStats().setLevel(1);
			building.setSecretIdentity("This is the work of "+mob.Name()+".");
			int capacity=Util.s_int((String)foundRecipe.elementAt(RCP_CAPACITY));
			int canContain=Util.s_int((String)foundRecipe.elementAt(RCP_CONTAINMASK));
			int armordmg=Util.s_int((String)foundRecipe.elementAt(RCP_ARMORDMG));
			bundle=misctype.equalsIgnoreCase("BUNDLE");
			if(bundle) building.setBaseValue(lostValue);
			String spell=(foundRecipe.size()>RCP_SPELL)?((String)foundRecipe.elementAt(RCP_SPELL)).trim():"";
			if(spell.length()>0)
			{
				String parm="";
				if(spell.indexOf(";")>0)
				{
					parm=spell.substring(spell.indexOf(";")+1);
					spell=spell.substring(0,spell.indexOf(";"));
				}
				Ability A=CMClass.getAbility(spell);
				A.setMiscText(parm);
				if(A!=null)	building.addNonUninvokableEffect(A);
			}
			key=null;
			if((building instanceof Container)
			&&(!(building instanceof Armor)))
			{
				if(capacity>0)
				{
					((Container)building).setCapacity(capacity+woodRequired);
					((Container)building).setContainTypes(canContain);
				}
				if(misctype.equalsIgnoreCase("LID"))
					((Container)building).setLidsNLocks(true,false,false,false);
				else
				if(misctype.equalsIgnoreCase("LOCK"))
				{
					((Container)building).setLidsNLocks(true,false,true,false);
					((Container)building).setKeyName(new Double(Math.random()).toString());
					key=CMClass.getItem("GenKey");
					((Key)key).setKey(((Container)building).keyName());
					key.setName("a key");
					key.setDisplayText("a small key sits here");
					key.setDescription("looks like a key to "+building.name());
					key.recoverEnvStats();
					key.text();
				}
			}
			if(building instanceof Drink)
			{
				((Drink)building).setLiquidHeld(capacity*50);
				((Drink)building).setThirstQuenched(250);
				if((capacity*50)<250)
					((Drink)building).setThirstQuenched(capacity*50);
				((Drink)building).setLiquidRemaining(0);
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
			if(building instanceof Weapon)
			{
				((Weapon)building).setWeaponType(Weapon.TYPE_BASHING);
				((Weapon)building).setWeaponClassification(Weapon.CLASS_BLUNT);
				for(int cl=0;cl<Weapon.classifictionDescription.length;cl++)
				{
					if(misctype.equalsIgnoreCase(Weapon.classifictionDescription[cl]))
						((Weapon)building).setWeaponClassification(cl);
				}
				switch(((Weapon)building).weaponClassification())
				{
				case Weapon.CLASS_AXE:
				case Weapon.CLASS_FLAILED:
					((Weapon)building).setWeaponType(Weapon.TYPE_SLASHING);
					break;
				case Weapon.CLASS_SWORD:
				case Weapon.CLASS_DAGGER:
				case Weapon.CLASS_EDGED:
				case Weapon.CLASS_POLEARM:
				case Weapon.CLASS_RANGED:
					((Weapon)building).setWeaponType(Weapon.TYPE_PIERCING);
					break;
				case Weapon.CLASS_NATURAL:
				case Weapon.CLASS_THROWN:
				case Weapon.CLASS_BLUNT:
				case Weapon.CLASS_HAMMER:
				case Weapon.CLASS_STAFF:
					((Weapon)building).setWeaponType(Weapon.TYPE_BASHING);
					break;
				}
				building.baseEnvStats().setAttackAdjustment((abilityCode()+(hardness*5)-1));
				building.baseEnvStats().setDamage(armordmg+hardness);
				((Weapon)building).setRawProperLocationBitmap(Item.WIELD|Item.HELD);
				((Weapon)building).setRawLogicalAnd((capacity>1));
			}
			if(building instanceof Armor)
			{
				double hardBonus=0.0;
				((Armor)building).setRawProperLocationBitmap(0);
				for(int wo=1;wo<Item.wornLocation.length;wo++)
				{
					String WO=Item.wornLocation[wo].toUpperCase();
					if(misctype.equalsIgnoreCase(WO))
					{
						hardBonus+=Item.wornWeights[wo];
						((Armor)building).setRawProperLocationBitmap(Util.pow(2,wo-1));
						((Armor)building).setRawLogicalAnd(false);
					}
					else
					if((misctype.toUpperCase().indexOf(WO+"||")>=0)
					||(misctype.toUpperCase().endsWith("||"+WO)))
					{
						if(hardBonus==0.0)
							hardBonus+=Item.wornWeights[wo];
						((Armor)building).setRawProperLocationBitmap(building.rawProperLocationBitmap()|Util.pow(2,wo-1));
						((Armor)building).setRawLogicalAnd(false);
					}
					else
					if((misctype.toUpperCase().indexOf(WO+"&&")>=0)
					||(misctype.toUpperCase().endsWith("&&"+WO)))
					{
						hardBonus+=Item.wornWeights[wo];
						((Armor)building).setRawProperLocationBitmap(building.rawProperLocationBitmap()|Util.pow(2,wo-1));
						((Armor)building).setRawLogicalAnd(true);
					}
				}
				int hardPoints=(int)Math.round(Util.mul(hardBonus,hardness));
				((Armor)building).baseEnvStats().setArmor(armordmg+hardPoints+(abilityCode()-1));
			}
			if(building instanceof Light)
			{
				((Light)building).setDuration(capacity);
				if((building instanceof Container)
				&&(!misctype.equals("SMOKE")))
				{
					((Light)building).setDuration(200);
					((Container)building).setCapacity(0);
				}
			}
			building.recoverEnvStats();
			building.text();
			building.recoverEnvStats();
		}


		messedUp=!profficiencyCheck(mob,0,auto);
		if(completion<4) completion=4;

		if(bundle)
		{
			messedUp=false;
			completion=1;
			verb="bundling "+EnvResource.RESOURCE_DESCS[building.material()&EnvResource.RESOURCE_MASK].toLowerCase();
			startStr="<S-NAME> start(s) "+verb+".";
			displayText="You are "+verb;
		}

		if(autoGenerate>0)
		{
			if(key!=null) commands.addElement(key);
			commands.addElement(building);
			return true;
		}

		FullMsg msg=new FullMsg(mob,null,CMMsg.MSG_NOISYMOVEMENT,startStr);
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,mob,completion);
		}
		return true;
	}
}
