package com.planet_ink.coffee_mud.Races;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class StdRace implements Race
{
	public String ID(){	return "StdRace"; }
	public String name(){ return "StdRace"; }
	protected int practicesAtFirstLevel(){return 0;}
	protected int trainsAtFirstLevel(){return 0;}
	public int shortestMale(){return 24;}
	public int shortestFemale(){return 24;}
	public int heightVariance(){return 5;}
	public int lightestWeight(){return 60;}
	public int weightVariance(){return 10;}
	public long forbiddenWornBits(){return 0;}
	public String racialCategory(){return "Unknown";}
	public boolean isGeneric(){return false;}

	//                                an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
	public int[] bodyMask(){return parts;}

	private static final Vector empty=new Vector();
	protected Weapon naturalWeapon=null;
	protected Vector naturalWeaponChoices=null;
	protected Hashtable racialAbilityMap=null;
	public String[] racialAbilityNames(){return null;}
	public int[] racialAbilityLevels(){return null;}
	public int[] racialAbilityProfficiencies(){return null;}
	public boolean[] racialAbilityQuals(){return null;}
	public String[] culturalAbilityNames(){return null;}
	public int[] culturalAbilityProfficiencies(){return null;}

	public boolean playerSelectable(){return false;}

	public boolean fertile(){return true;}

	public Race copyOf()
	{
		try
		{
			StdRace E=(StdRace)this.clone();
			return E;

		}
		catch(CloneNotSupportedException e)
		{
			return this;
		}
	}

	public Race healthBuddy(){return this;}

	/** some general statistics about such an item
	 * see class "EnvStats" for more information. */
	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{

	}
	public void affectCharStats(MOB affectedMob, CharStats affectableStats)
	{

	}
	public void affectCharState(MOB affectedMob, CharState affectableMaxState)
	{

	}
	public boolean okMessage(Environmental myHost, CMMsg msg)
	{
		return true;
	}

	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		// the sex rules
		if(!(myHost instanceof MOB)) return;

		MOB myChar=(MOB)myHost;
		if((msg.amITarget(myChar))
		&&(fertile())
		&&(msg.tool()!=null)
		&&(msg.tool().ID().equals("Social"))
		&&(myChar.charStats().getStat(CharStats.GENDER)==((int)'F'))
		&&(msg.source().charStats().getStat(CharStats.GENDER)==((int)'M'))
		&&(msg.tool().Name().equals("MATE <T-NAME>")
			||msg.tool().Name().equals("SEX <T-NAME>"))
		&&(Dice.rollPercentage()<10)
		&&((ID().equals("Human"))
		   ||(msg.source().charStats().getMyRace().ID().equals("Human"))
		   ||(msg.source().charStats().getMyRace().ID().equals(ID())))
		&&(msg.source().charStats().getMyRace().fertile())
		&&(myChar.location()==msg.source().location())
		&&(myChar.numWearingHere(Item.ON_LEGS)>0)
		&&(msg.source().numWearingHere(Item.ON_LEGS)>0)
		&&(myChar.numWearingHere(Item.ON_WAIST)>0)
		&&(msg.source().numWearingHere(Item.ON_WAIST)>0))
		{
			Ability A=CMClass.getAbility("Pregnancy");
			if((A!=null)
			&&(myChar.fetchAbility(A.ID())==null)
			&&(myChar.fetchEffect(A.ID())==null))
				A.invoke(msg.source(),myChar,true);
		}
	}
	public void wearOutfit(MOB mob, Armor s1, Armor s2, Armor p1)
	{
		if((s1!=null)&&(mob.fetchInventory(s1.ID())==null))
		{
			mob.addInventory(s1);
			if(mob.freeWearPositions(Item.ON_TORSO)>0)
				s1.wearAt(Item.ON_TORSO);
		}
		if((p1!=null)&&(mob.fetchInventory(p1.ID())==null))
		{
			mob.addInventory(p1);
			if(mob.freeWearPositions(Item.ON_LEGS)>0)
				p1.wearAt(Item.ON_LEGS);
		}
		if((s2!=null)&&(mob.fetchInventory(s2.ID())==null))
		{
			mob.addInventory(s2);
			if(mob.freeWearPositions(Item.ON_FEET)>0)
				s2.wearAt(Item.ON_FEET);
		}
	}
	public String arriveStr()
	{
		return "arrives";
	}
	public String leaveStr()
	{
		return "leaves";
	}
	public void outfit(MOB mob)
	{
	}
	public void level(MOB mob)
	{
	}
	public long getTickStatus(){return Tickable.STATUS_NOT;}
	public boolean tick(Tickable myChar, int tickID){return true;}
	public void startRacing(MOB mob, boolean verifyOnly)
	{
		if(!verifyOnly)
		{
			if(mob.baseEnvStats().level()<=1)
			{
				mob.setPractices(mob.getPractices()+practicesAtFirstLevel());
				mob.setTrains(mob.getTrains()+trainsAtFirstLevel());
			}
			setHeightWeight(mob.baseEnvStats(),(char)mob.baseCharStats().getStat(CharStats.GENDER));

			if((culturalAbilityNames()!=null)&&(culturalAbilityProfficiencies()!=null)
			   &&(culturalAbilityNames().length==culturalAbilityProfficiencies().length))
			for(int a=0;a<culturalAbilityNames().length;a++)
			{
				Ability A=CMClass.getAbility(culturalAbilityNames()[a]);
				if(A!=null)
				{
					A.setProfficiency(culturalAbilityProfficiencies()[a]);
					mob.addAbility(A);
					A.autoInvocation(mob);
					if((mob.isMonster())&&((A.classificationCode()&Ability.ALL_CODES)==Ability.LANGUAGE))
						A.invoke(mob,mob,false);
				}
			}
		}
	}
	public Weapon myNaturalWeapon()
	{
		if(naturalWeapon==null)
			naturalWeapon=(Weapon)CMClass.getWeapon("Natural");
		return naturalWeapon;
	}

	public String healthText(MOB mob)
	{
		return CommonStrings.standardMobCondition(mob);
	}

	public Weapon funHumanoidWeapon()
	{
		if(naturalWeaponChoices==null)
		{
			naturalWeaponChoices=new Vector();
			for(int i=1;i<11;i++)
			{
				naturalWeapon=CMClass.getWeapon("StdWeapon");
				switch(i)
				{
					case 1:
					case 2:
					case 3:
					naturalWeapon.setName("a quick punch");
					naturalWeapon.setWeaponType(Weapon.TYPE_BASHING);
					break;
					case 4:
					naturalWeapon.setName("fingernails and teeth");
					naturalWeapon.setWeaponType(Weapon.TYPE_PIERCING);
					break;
					case 5:
					naturalWeapon.setName("an elbow");
					naturalWeapon.setWeaponType(Weapon.TYPE_NATURAL);
					break;
					case 6:
					naturalWeapon.setName("a backhand");
					naturalWeapon.setWeaponType(Weapon.TYPE_BASHING);
					break;
					case 7:
					naturalWeapon.setName("a strong jab");
					naturalWeapon.setWeaponType(Weapon.TYPE_BASHING);
					break;
					case 8:
					naturalWeapon.setName("a stinging punch");
					naturalWeapon.setWeaponType(Weapon.TYPE_BASHING);
					break;
					case 9:
					naturalWeapon.setName("a knee");
					naturalWeapon.setWeaponType(Weapon.TYPE_BASHING);
					break;
					case 10:
					naturalWeapon.setName("a head butt");
					naturalWeapon.setWeaponType(Weapon.TYPE_BASHING);
					break;
				}
				naturalWeaponChoices.addElement(naturalWeapon);
			}
		}
		return (Weapon)naturalWeaponChoices.elementAt(Dice.roll(1,naturalWeaponChoices.size(),-1));
	}
	public Vector myResources(){return new Vector();}
	public void setHeightWeight(EnvStats stats, char gender)
	{
		int weightModifier=0;
		if(weightVariance()>0)
			weightModifier=Dice.roll(1,weightVariance(),0);
		stats.setWeight(lightestWeight()+weightModifier);
		int heightModifier=0;
		if(heightVariance()>0)
			heightModifier=Dice.roll(1,heightVariance(),0);
		if (gender == 'M')
			stats.setHeight(shortestMale()+heightModifier);
 		else
			stats.setHeight(shortestFemale()+heightModifier);
	}

	public int getMaxWeight()
	{
		return lightestWeight()+weightVariance();
	}

	public int compareTo(Object o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}

	protected Item makeResource(String name, int type)
	{
		Item I=null;
		if(((type&EnvResource.MATERIAL_MASK)==EnvResource.MATERIAL_FLESH)
		||((type&EnvResource.MATERIAL_MASK)==EnvResource.MATERIAL_VEGETATION))
			I=CMClass.getItem("GenFoodResource");
		else
		if((type&EnvResource.MATERIAL_MASK)==EnvResource.MATERIAL_LIQUID)
			I=CMClass.getItem("GenLiquidResource");
		else
			I=CMClass.getItem("GenResource");
		I.setName(name);
		I.setDisplayText(name+" has been left here.");
		I.setDescription("It looks like "+name());
		I.setMaterial(type);
		I.setBaseValue(EnvResource.RESOURCE_DATA[type&EnvResource.RESOURCE_MASK][1]);
		I.baseEnvStats().setWeight(1);
		I.recoverEnvStats();
		return I;
	}

	public void reRoll(MOB mob, CharStats C)
	{
		int avg=0;
		int max=CommonStrings.getIntVar(CommonStrings.SYSTEMI_MAXSTAT);
		if(max<(4*6)) max=4*6;
		max--;
		while(avg!=max)
		{
			int tries=0;
			max++;
			while((avg!=max)&&((++tries)<1000))
			{
				C.setStat(C.STRENGTH,3+(int)Math.floor(Math.random()*16.0));
				C.setStat(C.INTELLIGENCE,3+(int)Math.floor(Math.random()*16.0));
				C.setStat(C.DEXTERITY,3+(int)Math.floor(Math.random()*16.0));
				C.setStat(C.WISDOM,3+(int)Math.floor(Math.random()*16.0));
				C.setStat(C.CONSTITUTION,3+(int)Math.floor(Math.random()*16.0));
				C.setStat(C.CHARISMA,3+(int)Math.floor(Math.random()*16.0));
				avg=(C.getStat(C.STRENGTH)
					 +C.getStat(C.INTELLIGENCE)
					 +C.getStat(C.DEXTERITY)
					 +C.getStat(C.WISDOM)
					 +C.getStat(C.CONSTITUTION)
					 +C.getStat(C.CHARISMA));
			}
		}
	}

	public DeadBody getCorpse(MOB mob, Room room)
	{
		if(room==null) room=mob.location();

		DeadBody Body=(DeadBody)CMClass.getItem("Corpse");
		Body.setCharStats(mob.baseCharStats().cloneCharStats());
		Body.baseEnvStats().setLevel(mob.baseEnvStats().level());
		Body.baseEnvStats().setWeight(mob.baseEnvStats().weight());
		Body.baseEnvStats().setAbility(mob.isMonster()?0:11);
		if(!mob.isMonster())
			Body.baseEnvStats().setDisposition(Body.baseEnvStats().disposition()|EnvStats.IS_BONUS);
		if(!mob.isMonster())
			Body.baseEnvStats().setRejuv(Body.baseEnvStats().rejuv()*10);
		Body.setName("the body of "+mob.name());
		Body.setSecretIdentity(mob.name()+"/"+mob.description());
		Body.setDisplayText("the body of "+mob.name()+" lies here.");
		if(room!=null)
			room.addItem(Body);
		Body.recoverEnvStats();
		for(int i=0;i<mob.numEffects();i++)
		{
			Ability A=mob.fetchEffect(i);
			if((A!=null)&&(A instanceof DiseaseAffect))
			{
				if((Util.bset(((DiseaseAffect)A).abilityCode(),DiseaseAffect.SPREAD_CONSUMPTION))
				||(Util.bset(((DiseaseAffect)A).abilityCode(),DiseaseAffect.SPREAD_CONTACT)))
					Body.addNonUninvokableEffect((Ability)A.copyOf());
			}
		}

		Vector items=new Vector();
		for(int i=0;i<mob.inventorySize();)
		{
			Item thisItem=mob.fetchInventory(i);
			if((thisItem!=null)&&(thisItem.savable()))
			{
				if(mob.isMonster())
				{
					Item newItem=(Item)thisItem.copyOf();
					newItem.setContainer(null);
					newItem.setDispossessionTime(System.currentTimeMillis()+(Item.REFUSE_MONSTER_EQ*IQCalendar.MILI_HOUR));
					newItem.recoverEnvStats();
					thisItem=newItem;
					i++;
				}
				else
					mob.delInventory(thisItem);
				thisItem.unWear();
				if(thisItem.container()==null)
					thisItem.setContainer(Body);
				if(room!=null)
					room.addItem(thisItem);
				items.addElement(thisItem);
			}
			else
			if(thisItem!=null)
				mob.delInventory(thisItem);
			else
				i++;
		}
		if(mob.getMoney()>0)
		{
			Item C=(Item)CMClass.getItem("StdCoins");
			C.baseEnvStats().setAbility(mob.getMoney());
			C.recoverEnvStats();
			C.setContainer(Body);
			if(room!=null)
				room.addItemRefuse(C,Item.REFUSE_MONSTER_EQ);
			mob.setMoney(0);
		}
		return Body;
	}

	public Vector racialAbilities(MOB mob)
	{
		if((racialAbilityMap==null)
		&&(racialAbilityNames()!=null)
		&&(racialAbilityLevels()!=null)
		&&(racialAbilityProfficiencies()!=null)
		&&(racialAbilityQuals()!=null))
		{
			racialAbilityMap=new Hashtable();
			for(int i=0;i<racialAbilityNames().length;i++)
			{
				CMAble.addCharAbilityMapping(ID(),
											 racialAbilityLevels()[i],
											 racialAbilityNames()[i],
											 racialAbilityProfficiencies()[i],
											 "",
											 !racialAbilityQuals()[i],
											 false);
			}
		}
		if(racialAbilityMap==null) return empty;
		Integer level=null;
		if(mob!=null)
			level=new Integer(mob.envStats().level());
		else
			level=new Integer(Integer.MAX_VALUE);
		if(racialAbilityMap.containsKey(level))
			return (Vector)racialAbilityMap.get(level);
		Vector V=CMAble.getUpToLevelListings(ID(),level.intValue(),true,(mob!=null));
		Vector finalV=new Vector();
		for(int v=0;v<V.size();v++)
		{
			Ability A=CMClass.getAbility((String)V.elementAt(v));
			if(A!=null)
			{
				A.setProfficiency(CMAble.getDefaultProfficiency(ID(),A.ID()));
				A.setBorrowed(mob,true);
				A.setMiscText(CMAble.getDefaultParm(ID(),A.ID()));
				finalV.addElement(A);
			}
		}
		racialAbilityMap.put(level,finalV);
		return finalV;
	}

	public String racialParms(){ return "";}
	public void setRacialParms(String parms){}
	protected static String[] CODES={"CLASS","PARMS"};
	public String getStat(String code){
		switch(getCodeNum(code))
		{
		case 0: return ID();
		case 1: return ""+racialParms();
		}
		return "";
	}
	public void setStat(String code, String val)
	{
		switch(getCodeNum(code))
		{
		case 0: return;
		case 1: setRacialParms(val); break;
		}
	}
	public String[] getStatCodes(){return CODES;}
	protected int getCodeNum(String code){
		for(int i=0;i<CODES.length;i++)
			if(code.equalsIgnoreCase(CODES[i])) return i;
		return -1;
	}
	public boolean sameAs(Race E)
	{
		if(!(E instanceof StdRace)) return false;
		for(int i=0;i<CODES.length;i++)
			if(!E.getStat(CODES[i]).equals(getStat(CODES[i])))
				return false;
		return true;
	}
}
