package com.planet_ink.coffee_mud.Items.Basic;
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
   Copyright 2000-2007 Bo Zimmerman

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
public class StdItem implements Item
{
	public String ID(){	return "StdItem";}

	protected String 	name="an ordinary item";
	protected String	displayText="a nondescript item sits here doing nothing.";
	protected byte[] 	description=null;
	protected Item 		myContainer=null;
	protected int 		myUses=Integer.MAX_VALUE;
	protected long 		myWornCode=Item.IN_INVENTORY;
	protected String 	miscText="";
	protected String    imageName=null;
	protected String	secretIdentity=null;
	protected boolean	wornLogicalAnd=false;
	protected long 		properWornBitmap=Item.WORN_HELD;
	protected int		baseGoldValue=0;
	protected int		material=RawMaterial.RESOURCE_COTTON;
	protected Environmental owner=null;
	protected long dispossessionTime=0;
	protected long tickStatus=Tickable.STATUS_NOT;
	//protected String databaseID="";

	protected Vector affects=null;
	protected Vector behaviors=null;

	protected EnvStats envStats=(EnvStats)CMClass.getCommon("DefaultEnvStats");
	protected EnvStats baseEnvStats=(EnvStats)CMClass.getCommon("DefaultEnvStats");

	protected boolean destroyed=false;

	public StdItem()
	{
        super();
        CMClass.bumpCounter(this,CMClass.OBJECT_ITEM);
		baseEnvStats().setWeight(1);
		baseEnvStats().setArmor(0);
	}
    protected boolean abilityImbuesMagic(){return true;}
    protected void finalize(){CMClass.unbumpCounter(this,CMClass.OBJECT_ITEM);}
    public void initializeClass(){}
    public boolean isGeneric(){return false;}
	public String Name(){ return name;}
	public void setName(String newName){name=newName;}
	public String name()
	{
		if(envStats().newName()!=null) return envStats().newName();
		return Name();
	}
    public String image()
    {
        if(imageName==null) 
            imageName=CMProps.getDefaultMXPImage(this);
        return imageName;
    }
    public String rawImage()
    {
        if(imageName==null) 
            return "";
        return imageName;
    }
    public void setImage(String newImage)
    {
        if((newImage==null)||(newImage.trim().length()==0))
            imageName=null;
        else
            imageName=newImage;
    }
	
	public EnvStats envStats()
	{
		return envStats;
	}

	public EnvStats baseEnvStats()
	{
		return baseEnvStats;
	}

	public void recoverEnvStats()
	{
		baseEnvStats.copyInto(envStats);
		for(int a=0;a<numEffects();a++)
		{
			Ability A=fetchEffect(a);
			if(A!=null)
				A.affectEnvStats(this,envStats);
		}
		if(((envStats().ability()>0)&&abilityImbuesMagic())||(this instanceof MiscMagic))
			envStats().setDisposition(envStats().disposition()|EnvStats.IS_BONUS);
		if((owner()!=null)
		&&(owner() instanceof MOB)
		&&(CMLib.flags().isHidden(this)))
		   envStats().setDisposition((int)(envStats().disposition()&(EnvStats.ALLMASK-EnvStats.IS_HIDDEN)));
	}

	public void setBaseEnvStats(EnvStats newBaseEnvStats)
	{
		baseEnvStats=(EnvStats)newBaseEnvStats.copyOf();
	}
	public CMObject newInstance()
	{
		try
        {
			return (CMObject)this.getClass().newInstance();
		}
		catch(Exception e)
		{
			Log.errOut(ID(),e);
		}
		return new StdItem();
	}
	public boolean subjectToWearAndTear(){return false;}
	protected void cloneFix(Item E)
	{
		destroyed=false;
		baseEnvStats=(EnvStats)E.baseEnvStats().copyOf();
		envStats=(EnvStats)E.envStats().copyOf();

		affects=null;
		behaviors=null;
		for(int b=0;b<E.numBehaviors();b++)
		{
			Behavior B=E.fetchBehavior(b);
			if(B!=null)	addBehavior((Behavior)B.copyOf());
		}

		for(int a=0;a<E.numEffects();a++)
		{
			Ability A=E.fetchEffect(a);
			if((A!=null)&&(!A.canBeUninvoked())&&(!A.ID().equals("ItemRejuv")))
				addEffect((Ability)A.copyOf());
		}

	}
	public CMObject copyOf()
	{
		try
		{
			StdItem E=(StdItem)this.clone();
            CMClass.bumpCounter(E,CMClass.OBJECT_ITEM);
			E.cloneFix(this);
			return E;

		}
		catch(CloneNotSupportedException e)
		{
			return this.newInstance();
		}
	}

	protected Rideable riding=null;
	public Rideable riding(){return riding;}
	public void setRiding(Rideable ride)
	{
		if((ride!=null)&&(riding()!=null)&&(riding()==ride)&&(riding().amRiding(this)))
			return;
		if((riding()!=null)&&(riding().amRiding(this)))
			riding().delRider(this);
		riding=ride;
		if((riding()!=null)&&(!riding().amRiding(this)))
			riding().addRider(this);
	}

	public Environmental owner(){return owner;}
	public void setOwner(Environmental E)
	{
		owner=E;
		if((E!=null)&&(!(E instanceof Room)))
			setExpirationDate(0);
		recoverEnvStats();
	}
	public long expirationDate()
	{
		return dispossessionTime;
	}
	public void setDatabaseID(String id){}//databaseID=id;}
	
	public String databaseID(){return "";}//databaseID;}
	
	public void setExpirationDate(long time)
	{
		dispossessionTime=time;
	}
	public boolean amDestroyed()
	{
		return destroyed;
	}

	public boolean amWearingAt(long wornCode)
	{
		if((myWornCode+wornCode)==0)
			return true;
		else
		if(wornCode==0)
			return false;
		return (myWornCode & wornCode)==wornCode;
	}
	public boolean fitsOn(long wornCode)
	{
		if(wornCode<=0)	return true;
		return ((properWornBitmap & wornCode)==wornCode);
	}
    public void wearEvenIfImpossible(MOB mob)
    {
        for(int i=0;i<WORN_ORDER.length;i++)
        {
            if(fitsOn(WORN_ORDER[i]))
            {
                wearAt(WORN_ORDER[i]);
                break;
            }
        }
    }
	public void wearIfPossible(MOB mob)
	{
		for(int i=0;i<WORN_ORDER.length;i++)
		{
			if((fitsOn(WORN_ORDER[i]))
			&&(canWear(mob,WORN_ORDER[i])))
			{
				wearAt(WORN_ORDER[i]);
				break;
			}
		}
	}
	public void wearAt(long wornCode)
	{
		if(wornCode==Item.IN_INVENTORY)
		{
			unWear();
			return;
		}
		if(wornLogicalAnd)
			setRawWornCode(properWornBitmap);
		else
			setRawWornCode(wornCode);
		recoverEnvStats();
	}

	public long rawProperLocationBitmap()
	{ return properWornBitmap;}
	public boolean rawLogicalAnd()
	{ return wornLogicalAnd;}
	public void setRawProperLocationBitmap(long newValue)
	{
		properWornBitmap=newValue;
	}
	public void setRawLogicalAnd(boolean newAnd)
	{
		wornLogicalAnd=newAnd;
	}
	public boolean compareProperLocations(Item toThis)
	{
		if(toThis.rawLogicalAnd()!=wornLogicalAnd)
			return false;
		if((toThis.rawProperLocationBitmap()|Item.WORN_HELD)==(properWornBitmap|Item.WORN_HELD))
			return true;
		return false;
	}

	public long whereCantWear(MOB mob)
	{
		long couldHaveBeenWornAt=-1;
		if(properWornBitmap==0)
			return couldHaveBeenWornAt;
		short layer=0;
		short layerAtt=0;
		if(this instanceof Armor)
		{
			layer=((Armor)this).getClothingLayer();
			layerAtt=((Armor)this).getLayerAttributes();
		}

		if(!wornLogicalAnd)
		{
			for(int i=1;i<WORN_CODES.length;i++)
			{
				if(fitsOn(WORN_CODES[i]))
				{
					couldHaveBeenWornAt=WORN_CODES[i];
					if(mob.freeWearPositions(WORN_CODES[i],layer,layerAtt)>0)
						return 0;
				}
			}
			return couldHaveBeenWornAt;
		}
		for(int i=1;i<WORN_CODES.length;i++)
		{
			if((fitsOn(WORN_CODES[i]))
			&&(mob.freeWearPositions(WORN_CODES[i],layer,layerAtt)==0))
				return WORN_CODES[i];
		}
		return 0;
	}

	public boolean canWear(MOB mob, long where)
	{
		if(where==0) return (whereCantWear(mob)==0);
		return mob.freeWearPositions(where,(short)0,(short)0)>0;
	}

	public long rawWornCode()
	{
		return myWornCode;
	}
	public void setRawWornCode(long newValue)
	{
		myWornCode=newValue;
	}

	public void unWear()
	{
		setRawWornCode(Item.IN_INVENTORY);
		recoverEnvStats();
	}


	public int material()
	{
		return material;
	}

	public void setMaterial(int newValue)
	{
		material=newValue;
	}

	public int value()
	{
		return baseGoldValue()+(10*envStats().ability());
	}
	public int baseGoldValue(){return baseGoldValue;}
	public void setBaseValue(int newValue)
	{
		baseGoldValue=newValue;
	}

	public String readableText(){return miscText;}
	public void setReadableText(String text){miscText=text;}

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		if(affected instanceof Room)
		{
			if((CMLib.flags().isLightSource(this))&&(CMLib.flags().isInDark(affected)))
				affectableStats.setDisposition(affectableStats.disposition()-EnvStats.IS_DARK);
		}
		else
		{
			if(CMLib.flags().isLightSource(this))
			{
				if(rawWornCode()!=Item.IN_INVENTORY)
					affectableStats.setDisposition(affectableStats.disposition()|EnvStats.IS_LIGHTSOURCE);
				if(CMLib.flags().isInDark(affected))
					affectableStats.setDisposition(affectableStats.disposition()-EnvStats.IS_DARK);
			}
			if((amWearingAt(Item.WORN_MOUTH))&&(affected instanceof MOB))
			{
				if(!(this instanceof Light))
					affectableStats.setSensesMask(affectableStats.sensesMask()|EnvStats.CAN_NOT_SPEAK);
				affectableStats.setSensesMask(affectableStats.sensesMask()|EnvStats.CAN_NOT_TASTE);
			}
			if((!amWearingAt(Item.WORN_FLOATING_NEARBY))
			&&((!(affected instanceof MOB))||(((MOB)affected).riding()!=this)))
				affectableStats.setWeight(affectableStats.weight()+envStats().weight());
		}
        int num=numEffects();
		for(int a=0;a<num;a++)
		{
			Ability A=fetchEffect(a);
			if((A!=null)&&(A.bubbleAffect()))
			   A.affectEnvStats(affected,affectableStats);
		}
	}
	public void affectCharStats(MOB affectedMob, CharStats affectableStats)
	{
        int num=numEffects();
		for(int a=0;a<num;a++)
		{
			Ability A=fetchEffect(a);
			if((A!=null)&&(A.bubbleAffect()))
			   A.affectCharStats(affectedMob,affectableStats);
		}
	}
	public void affectCharState(MOB affectedMob, CharState affectableMaxState)
	{
        int num=numEffects();
		for(int a=0;a<num;a++)
		{
			Ability A=fetchEffect(a);
			if((A!=null)&&(A.bubbleAffect()))
			   A.affectCharState(affectedMob,affectableMaxState);
		}
	}
	public void setMiscText(String newText)
	{
		miscText=newText;
	}
	public String text()
	{
		return miscText;
	}
	public String miscTextFormat(){return CMParms.FORMAT_UNDEFINED;}

	public int compareTo(Object o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}

	public long getTickStatus(){return tickStatus;}
	public boolean tick(Tickable ticking, int tickID)
	{
		if(destroyed) 
			return false;
		tickStatus=Tickable.STATUS_START;
		if(tickID==Tickable.TICKID_ITEM_BEHAVIOR)
		{
            int num=numBehaviors();
			if(num==0) return false;
            Behavior B=null;
			for(int b=0;b<num;b++)
			{
				tickStatus=Tickable.STATUS_BEHAVIOR+b;
				B=fetchBehavior(b);
				if(B!=null)
					B.tick(ticking,tickID);
			}
		}
		else
		if(tickID!=Tickable.TICKID_CLANITEM)
		{
            Vector aff=cloneEffects();
            if(aff!=null)
            {
                Ability A=null;
                for(int a=0;a<aff.size();a++)
    			{
    				A=(Ability)aff.elementAt(a);
    				tickStatus=Tickable.STATUS_AFFECT+a;
    				if(!A.tick(ticking,tickID))
    					A.unInvoke();
    			}
            }
		}
		tickStatus=Tickable.STATUS_NOT;
		return true;
	}

	public Item ultimateContainer()
	{
		if(container()==null) return this;
		return container().ultimateContainer();
	}
	public Item container()
	{
		return myContainer;
	}
	public String rawSecretIdentity(){return ((secretIdentity==null)?"":secretIdentity);}
	public String secretIdentity()
	{
		if((secretIdentity!=null)&&(secretIdentity.length()>0))
			return secretIdentity+"\n\rLevel: "+envStats().level()+tackOns();
		return description()+"\n\rLevel: "+envStats().level()+tackOns();
	}

	public void setSecretIdentity(String newIdentity)
	{
		if((newIdentity==null)
		||(newIdentity.trim().equalsIgnoreCase(description()))
		||(newIdentity.length()==0))
			secretIdentity=null;
		else
			secretIdentity=newIdentity;
	}

	public String displayText()
	{
		return displayText;
	}
	public void setDisplayText(String newDisplayText)
	{
		displayText=newDisplayText;
	}
	public String description()
	{
		if((description==null)||(description.length==0))
			return "";
		else
		if(CMProps.getBoolVar(CMProps.SYSTEMB_ITEMDCOMPRESS))
			return CMLib.encoder().decompressString(description);
		else
			return CMStrings.bytesToStr(description);
	}
	public void setDescription(String newDescription)
	{
		if(newDescription.length()==0)
			description=null;
		else
		if(CMProps.getBoolVar(CMProps.SYSTEMB_ITEMDCOMPRESS))
			description=CMLib.encoder().compressString(newDescription);
		else
			description=CMStrings.strToBytes(newDescription);
	}
	public void setContainer(Item newContainer)
	{
		myContainer=newContainer;
	}
    public int numberOfItems()
    {
        if(!(this instanceof Container))
            return 1;
        return ((Container)this).getContents().size()+1;
    }
	public int usesRemaining()
	{
		return myUses;
	}
	public void setUsesRemaining(int newUses)
	{
		myUses=newUses;
	}

	public boolean savable(){return CMLib.flags().isSavable(this);}

	protected boolean canWearComplete(MOB mob)
	{
		if(!canWear(mob,0))
		{
			long cantWearAt=whereCantWear(mob);
			Item alreadyWearing=mob.fetchFirstWornItem(cantWearAt);
			if(alreadyWearing!=null)
			{
				if((cantWearAt!=Item.WORN_HELD)&&(cantWearAt!=Item.WORN_WIELD))
				{
					if(!CMLib.commands().postRemove(mob,alreadyWearing,false))
					{
						mob.tell("You are already wearing "+alreadyWearing.name()+" on your "+CMLib.flags().wornLocation(cantWearAt)+".");
						return false;
					}
					alreadyWearing=mob.fetchFirstWornItem(cantWearAt);
					if((alreadyWearing!=null)&&(!canWear(mob,0)))
					{
						mob.tell("You are already wearing "+alreadyWearing.name()+" on your "+CMLib.flags().wornLocation(cantWearAt)+".");
						return false;
					}
				}
				else
				{
					if(cantWearAt==Item.WORN_HELD)
						mob.tell("You are already holding "+alreadyWearing.name()+".");
					else
					if(cantWearAt==Item.WORN_WIELD)
						mob.tell("You are already wielding "+alreadyWearing.name()+".");
					else
						mob.tell("You are already wearing "+alreadyWearing.name()+" on your "+CMLib.flags().wornLocation(cantWearAt)+".");
					return false;
				}
			}
			else
			{
				mob.tell("You don't have anywhere you can wear that.");
				return false;
			}
		}
		return true;
	}
	
	protected boolean alreadyWornMsg(MOB mob, Item thisItem)
	{
		if(!thisItem.amWearingAt(Item.IN_INVENTORY))
		{
			if(thisItem.amWearingAt(Item.WORN_WIELD))
				mob.tell(thisItem.name()+" is already being wielded.");
			else
			if(thisItem.amWearingAt(Item.WORN_HELD))
				mob.tell(thisItem.name()+" is already being held.");
			else
			if(thisItem.amWearingAt(Item.WORN_FLOATING_NEARBY))
				mob.tell(thisItem.name()+" is floating nearby.");
			else
				mob.tell(thisItem.name()+"is already being worn.");
			return false;
		}
		return true;
	}

	public boolean okMessage(Environmental myHost, CMMsg msg)
	{
		// the order that these things are checked in should
		// be holy, and etched in stone.
        int num=numBehaviors();
		for(int b=0;b<num;b++)
		{
			Behavior B=fetchBehavior(b);
			if((B!=null)&&(!B.okMessage(this,msg)))
				return false;
		}
        num=numEffects();
		for(int a=0;a<num;a++)
		{
			Ability A=fetchEffect(a);
			if((A!=null)&&(!A.okMessage(this,msg)))
				return false;
		}

		MOB mob=msg.source();
		
		if((msg.tool()==this)
		&&(msg.sourceMinor()==CMMsg.TYP_THROW)
		&&(mob!=null)
		&&(mob.isMine(this)))
		{
			if(envStats().weight()>(mob.maxCarry()/5))
			{
				mob.tell(name()+" is too heavy to throw.");
				return false;
			}
			if(!CMLib.flags().isDroppable(this))
			{
				mob.tell("You can't seem to let go of "+name()+".");
				return false;
			}
			return true;
		}
		else
		if(!msg.amITarget(this))
			return true;
		else
		if(msg.targetCode()==CMMsg.NO_EFFECT)
			return true;
		else
		if((CMath.bset(msg.targetCode(),CMMsg.MASK_MAGIC))
		&&(!CMLib.flags().isGettable(this))
		&&((displayText().length()==0)
		   ||((msg.tool()!=null)
			&&(msg.tool() instanceof Ability)
			&&(((Ability)msg.tool()).abstractQuality()==Ability.QUALITY_MALICIOUS))))
		{
			mob.tell("Please don't do that.");
			return false;
		}
		else
		switch(msg.targetMinor())
		{
		case CMMsg.TYP_EXPIRE:
		case CMMsg.TYP_LOOK:
        case CMMsg.TYP_EXAMINE:
		case CMMsg.TYP_READ:
		case CMMsg.TYP_QUIETMOVEMENT:
		case CMMsg.TYP_NOISYMOVEMENT:
		case CMMsg.TYP_HANDS:
		case CMMsg.TYP_SPEAK:
		case CMMsg.TYP_OK_ACTION:
		case CMMsg.TYP_OK_VISUAL:
		case CMMsg.TYP_DEATH:
		case CMMsg.TYP_NOISE:
		case CMMsg.TYP_EMOTE:
		case CMMsg.TYP_SNIFF:
			return true;
		case CMMsg.TYP_SIT:
		case CMMsg.TYP_SLEEP:
		case CMMsg.TYP_MOUNT:
		case CMMsg.TYP_DISMOUNT:
		case CMMsg.TYP_ENTER:
			if(this instanceof Rideable)
				return true;
			break;
        case CMMsg.TYP_LIST:
            if(CMLib.coffeeShops().getShopKeeper(this)!=null)
                return true;
            break;
		case CMMsg.TYP_RELOAD:
			if((this instanceof Weapon)
			&&(((Weapon)this).requiresAmmunition()))
				return true;
			break;
		case CMMsg.TYP_HOLD:
			if((!fitsOn(Item.WORN_HELD))||(properWornBitmap==0))
			{
				StringBuffer str=new StringBuffer("You can't hold "+name()+".");
				if(fitsOn(Item.WORN_WIELD))
					str.append("Try WIELDing it.");
				else
				if(properWornBitmap>0)
					str.append("Try WEARing it.");
				mob.tell(str.toString());
				return false;
			}
			if(!alreadyWornMsg(msg.source(),this))
				return false;
			if(envStats().level()>mob.envStats().level())
			{
				mob.tell("That looks too advanced for you.");
				return false;
			}
			if((!rawLogicalAnd())||(properWornBitmap==0))
			{
				if(!canWear(mob,Item.WORN_HELD))
				{
					Item alreadyWearing=mob.fetchFirstWornItem(Item.WORN_HELD);
					if(alreadyWearing!=null)
					{
						if((!CMLib.commands().postRemove(mob,alreadyWearing,false))
						||(!canWear(mob,Item.WORN_HELD)))
						{
							mob.tell("Your hands are full.");
							return false;
						}
					}
					else
					{
						mob.tell("You need hands to hold things.");
						return false;
					}
				}
				return true;
			}
			return canWearComplete(mob);
		case CMMsg.TYP_WEAR:
			if(properWornBitmap==0)
			{
				mob.tell("You can't wear "+name()+".");
				return false;
			}
			if(!alreadyWornMsg(msg.source(),this))
				return false;
			if(envStats().level()>mob.envStats().level())
			{
				mob.tell("That looks too advanced for you.");
				return false;
			}
			return canWearComplete(mob);
		case CMMsg.TYP_WIELD:
			if((!fitsOn(Item.WORN_WIELD))||(properWornBitmap==0))
			{
				mob.tell("You can't wield "+name()+" as a weapon.");
				return false;
			}
			if(!alreadyWornMsg(msg.source(),this))
				return false;
			if(envStats().level()>mob.envStats().level())
			{
				mob.tell("That looks too advanced for you.");
				return false;
			}
			if((!rawLogicalAnd())||(properWornBitmap==0))
			{
				if(!canWear(mob,Item.WORN_WIELD))
				{
					Item alreadyWearing=mob.fetchFirstWornItem(Item.WORN_WIELD);
					if(alreadyWearing!=null)
					{
						if(!CMLib.commands().postRemove(mob,alreadyWearing,false))
						{
							mob.tell("You are already wielding "+alreadyWearing.name()+".");
							return false;
						}
					}
					else
					{
						mob.tell("You need hands to wield things.");
						return false;
					}
				}
			}
			return canWearComplete(mob);
		case CMMsg.TYP_PUSH:
		case CMMsg.TYP_PULL:
		    if(msg.source().isMine(this))
		    {
		        mob.tell("You'll need to put that down first.");
		        return false;
		    }
			if(!CMLib.flags().isGettable(this))
			{
				mob.tell("You can't move "+name()+".");
				return false;
			}
		    return true;
		case CMMsg.TYP_GET:
			if((msg.tool()==null)||(msg.tool() instanceof MOB))
			{
				if((!CMLib.flags().canBeSeenBy(this,mob))
				&&((msg.sourceMajor()&CMMsg.MASK_ALWAYS)==0)
				&&(amWearingAt(Item.IN_INVENTORY)))
				{
					mob.tell("You can't see that.");
					return false;
				}
				if((mob.envStats().level()<envStats().level()-(10+(mob.envStats().level()/5)))
				&&(!(mob instanceof ShopKeeper)))
				{
					mob.tell(name()+" is too powerful to endure possessing it.");
					return false;
				}
				if((envStats().weight()>(mob.maxCarry()-mob.envStats().weight()))&&(!mob.isMine(this)))
				{
					mob.tell(name()+" is too heavy.");
					return false;
				}
                if((numberOfItems()>(mob.maxItems()-mob.inventorySize()))&&(!mob.isMine(this)))
                {
                    mob.tell("You can't carry that many items.");
                    return false;
                }
				if(!CMLib.flags().isGettable(this))
				{
					mob.tell("You can't get "+name()+".");
					return false;
				}
				if((this instanceof Rideable)&&(((Rideable)this).numRiders()>0))
				{
					if((mob.riding()!=null)&&(mob.riding()==this))
						mob.tell("You are "+((Rideable)this).stateString(mob)+" "+name()+"!");
					else
						mob.tell("Someone is "+((Rideable)this).stateString(mob)+" "+name()+"!");
					return false;
				}
				return true;
			}
			if(this instanceof Container)
				return true;
			switch(msg.sourceMinor())
			{
			case CMMsg.TYP_BUY:
			case CMMsg.TYP_BID:
			case CMMsg.TYP_GET:
			case CMMsg.TYP_GENERAL:
			case CMMsg.TYP_REMOVE:
			case CMMsg.TYP_SELL:
			case CMMsg.TYP_VALUE:
			case CMMsg.TYP_VIEW:
			case CMMsg.TYP_GIVE:
				return true;
			}
			break;
		case CMMsg.TYP_REMOVE:
			if((msg.tool()==null)||(msg.tool() instanceof MOB))
			{
				if((!CMLib.flags().canBeSeenBy(this,mob))
				   &&((msg.sourceMajor()&CMMsg.MASK_ALWAYS)==0)
				   &&(amWearingAt(Item.IN_INVENTORY)))
				{
					mob.tell("You can't see that.");
					return false;
				}
				if((!amWearingAt(Item.IN_INVENTORY))&&(!CMLib.flags().isRemovable(this)))
				{
					if(amWearingAt(Item.WORN_WIELD)||amWearingAt(Item.WORN_HELD))
					{
						mob.tell("You can't seem to let go of "+name()+".");
						return false;
					}
					mob.tell("You can't seem to remove "+name()+".");
					return false;
				}
				Item I=null;
				short layer=(this instanceof Armor)?((Armor)this).getClothingLayer():0;
				short thislayer=0;
				if(rawWornCode()>0)
				for(int i=0;i<mob.inventorySize();i++)
				{
					I=mob.fetchInventory(i);
					if((I!=null)&&(I!=this)&&((I.rawWornCode()&rawWornCode())>0))
					{
						thislayer=(I instanceof Armor)?((Armor)I).getClothingLayer():0;
						if(thislayer>layer)
						{
							mob.tell(mob,I,null,"You must remove <T-NAME> first.");
							return false;
						}
					}
				}
				return true;
			}
			if(this instanceof Container)
				return true;
			switch(msg.sourceMinor())
			{
			case CMMsg.TYP_BUY:
			case CMMsg.TYP_BID:
			case CMMsg.TYP_GET:
			case CMMsg.TYP_GENERAL:
			case CMMsg.TYP_REMOVE:
			case CMMsg.TYP_SELL:
			case CMMsg.TYP_VALUE:
			case CMMsg.TYP_VIEW:
			case CMMsg.TYP_GIVE:
				return true;
			}
			break;
		case CMMsg.TYP_DROP:
			if(!mob.isMine(this))
			{
				mob.tell("You don't have that.");
				return false;
			}
			if(!CMLib.flags().isDroppable(this))
			{
				mob.tell("You can't seem to let go of "+name()+".");
				return false;
			}
			return true;
		case CMMsg.TYP_BUY:
		case CMMsg.TYP_BID:
		case CMMsg.TYP_SELL:
		case CMMsg.TYP_VALUE:
		case CMMsg.TYP_VIEW:
			return true;
		case CMMsg.TYP_OPEN:
		case CMMsg.TYP_CLOSE:
		case CMMsg.TYP_LOCK:
		case CMMsg.TYP_PUT:
		case CMMsg.TYP_UNLOCK:
			if(this instanceof Container)
				return true;
			break;
		case CMMsg.TYP_DELICATE_HANDS_ACT:
		case CMMsg.TYP_JUSTICE:
		case CMMsg.TYP_WAND_USE:
		case CMMsg.TYP_FIRE: // lighting
		case CMMsg.TYP_WATER: // rust
		case CMMsg.TYP_CAST_SPELL:
		case CMMsg.TYP_POISON: // for use poison
			return true;
		case CMMsg.TYP_FILL:
			if(this instanceof Drink)
				return true;
			if(this instanceof Lantern)
				return true;
			break;
		case CMMsg.TYP_EAT:
			if(this instanceof Food)
				return true;
			break;
		case CMMsg.TYP_DRINK:
			if(this instanceof Drink)
				return true;
			break;
		case CMMsg.TYP_WRITE:
			if((CMLib.flags().isReadable(this))&&(!(this instanceof Scroll)))
			{
				if(msg.targetMessage().trim().length()==0)
				{
					mob.tell("Write what on "+name()+"?");
					return false;
				}
				return true;
			}
			mob.tell("You can't write on "+name()+".");
			return false;
		default:
			break;
		}
		mob.tell(mob,this,null,"You can't do that to <T-NAMESELF>.");
		return false;
	}

    protected Vector cloneEffects(){return (Vector)(((affects==null)||(affects.size()==0))?null:affects.clone());}
    
	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		// the order that these things are checked in should
		// be holy, and etched in stone.
		for(int b=0;b<numBehaviors();b++)
		{
			Behavior B=fetchBehavior(b);
			if(B!=null)
				B.executeMsg(this,msg);
		}

		for(int a=0;a<numEffects();a++)
		{
			Ability A=fetchEffect(a);
			if(A!=null)
				A.executeMsg(this,msg);
		}

		MOB mob=msg.source();
		if((msg.tool()==this)
		&&(msg.sourceMinor()==CMMsg.TYP_THROW)
		&&(mob!=null)
		&&(msg.target()!=null))
		{
			Room R=CMLib.map().roomLocation(msg.target());
			if(mob.isMine(this))
			{
				mob.delInventory(this);
				if(!R.isContent(this))
					R.addItemRefuse(this,Item.REFUSE_PLAYER_DROP);
				if(!CMath.bset(msg.sourceCode(),CMMsg.MASK_OPTIMIZE))
				{
					R.recoverRoomStats();
					if(mob.location()!=R)
						mob.location().recoverRoomStats();
				}
			}
			unWear();
			setContainer(null);
		}
		else
		if(!msg.amITarget(this))
			return;
		else
		switch(msg.targetMinor())
		{
		case CMMsg.TYP_SNIFF:
            CMLib.commands().handleBeingSniffed(msg);
			break;
		case CMMsg.TYP_LOOK:
        case CMMsg.TYP_EXAMINE: CMLib.commands().handleBeingLookedAt(msg); break;
		case CMMsg.TYP_READ: CMLib.commands().handleBeingRead(msg); break;
		case CMMsg.TYP_HOLD: CMLib.commands().handleBeingHeld(msg); break;
		case CMMsg.TYP_WEAR: CMLib.commands().handleBeingWorn(msg); break;
		case CMMsg.TYP_WIELD: CMLib.commands().handleBeingWielded(msg); break;
		case CMMsg.TYP_GET: CMLib.commands().handleBeingGetted(msg); break;
		case CMMsg.TYP_REMOVE: CMLib.commands().handleBeingRemoved(msg);  break;
		case CMMsg.TYP_DROP: CMLib.commands().handleBeingDropped(msg); break;
		case CMMsg.TYP_WRITE:
			if(CMLib.flags().isReadable(this))
				setReadableText((readableText()+" "+msg.targetMessage()).trim());
			break;
		case CMMsg.TYP_EXPIRE:
		case CMMsg.TYP_DEATH:
			destroy();
			break;
		default:
			break;
		}
	}

    public int recursiveWeight()
    {
        int weight=envStats().weight();
        if(this instanceof Container)
        {
            if(owner()==null) return weight;
            if(owner() instanceof MOB)
            {
                MOB M=(MOB)owner();
                for(int i=0;i<M.inventorySize();i++)
                {
                    Item thisItem=M.fetchInventory(i);
                    if((thisItem!=null)&&(thisItem.container()==this))
                        weight+=thisItem.recursiveWeight();
                }
            }
            else
            if(owner() instanceof Room)
            {
                Room R=(Room)owner();
                for(int i=0;i<R.numItems();i++)
                {
                    Item thisItem=R.fetchItem(i);
                    if((thisItem!=null)&&(thisItem.container()==this))
                        weight+=thisItem.recursiveWeight();
                }
            }
        }
        return weight;
    }
    
	public void stopTicking(){destroyed=true;}
	public void destroy()
	{
		myContainer=null;
    	if((baseEnvStats()!=null)
    	&&(CMath.bset(baseEnvStats().disposition(),EnvStats.IS_CATALOGED)))
    	{
    		int index=CMLib.map().getCatalogItemIndex(Name());
    		if(index>=0) CMLib.map().getCatalogItemUsage(index)[0]--;
    	}
		for(int a=numEffects()-1;a>=0;a--)
		{
			Ability aff=fetchEffect(a);
			if((aff!=null)&&(!(aff.ID().equals("ItemRejuv"))))
            {
				aff.unInvoke();
                aff.delEffect(aff);
            }
		}
		for(int b=numBehaviors()-1;b>=0;b--)
			delBehavior(fetchBehavior(b));
		
		riding=null;
		destroyed=true;

		if(owner!=null)
		{
			if (owner instanceof Room)
			{
				Room thisRoom=(Room)owner;
				for(int r=thisRoom.numItems()-1;r>=0;r--)
				{
					Item thisItem = thisRoom.fetchItem(r);
					if((thisItem!=null)
					&&(!thisItem.amDestroyed())
					&&(thisItem.container()!=null)
					&&(thisItem.container()==this))
						thisItem.destroy();
				}
				thisRoom.delItem(this);
			}
			else
			if (owner instanceof MOB)
			{
				MOB mob=(MOB)owner;
				for(int r=mob.inventorySize()-1;r>=0;r--)
				{
					Item thisItem = mob.fetchInventory(r);
					if((thisItem!=null)
					&&(!thisItem.amDestroyed())
					&&(thisItem.container()!=null)
					&&(thisItem.container()==this))
						thisItem.destroy();
				}
				mob.delInventory(this);
			}
		}
        myContainer=null;
        imageName=null;
        secretIdentity=null;
        owner=null;
        affects=null;
        behaviors=null;
	}

	public void removeFromOwnerContainer()
	{
		myContainer=null;

		if(owner==null) return;

		if (owner instanceof Room)
		{
			Room thisRoom=(Room)owner;
			for(int r=thisRoom.numItems()-1;r>=0;r--)
			{
				Item thisItem = thisRoom.fetchItem(r);
				if((thisItem!=null)
				&&(thisItem.container()!=null)
				&&(thisItem.container()==this))
					thisItem.removeFromOwnerContainer();
			}
			thisRoom.delItem(this);
		}
		else
		if (owner instanceof MOB)
		{
			MOB mob=(MOB)owner;
			for(int r=mob.inventorySize()-1;r>=0;r--)
			{
				Item thisItem = mob.fetchInventory(r);
				if((thisItem!=null)
				&&(thisItem.container()!=null)
				&&(thisItem.container()==this))
					thisItem.removeFromOwnerContainer();
			}
			mob.delInventory(this);
		}
		recoverEnvStats();
	}

	public void addNonUninvokableEffect(Ability to)
	{
		if(to==null) return;
		if(fetchEffect(to.ID())!=null) return;
		if(affects==null) affects=new Vector();
		to.makeNonUninvokable();
		to.makeLongLasting();
		affects.addElement(to);
		to.setAffectedOne(this);
	}
	public void addEffect(Ability to)
	{
		if(to==null) return;
		if(fetchEffect(to.ID())!=null) return;
		if(affects==null) affects=new Vector();
		affects.addElement(to);
		to.setAffectedOne(this);
	}
	public void delEffect(Ability to)
	{
		if(affects==null) return;
		int size=affects.size();
		affects.removeElement(to);
		if(affects.size()<size)
			to.setAffectedOne(null);
	}
	public int numEffects()
	{
		if(affects==null) return 0;
		return affects.size();
	}
	public Ability fetchEffect(int index)
	{
		if(affects==null) return null;
		try
		{
			return (Ability)affects.elementAt(index);
		}
		catch(java.lang.ArrayIndexOutOfBoundsException x){}
		return null;
	}
	public Ability fetchEffect(String ID)
	{
		if(affects==null) return null;
		for(int a=0;a<numEffects();a++)
		{
			Ability A=fetchEffect(a);
			if((A!=null)&&(A.ID().equals(ID)))
				return A;
		}
		return null;
	}

	/** Manipulation of Behavior objects, which includes
	 * movement, speech, spellcasting, etc, etc.*/
	public void addBehavior(Behavior to)
	{
		if(to==null) return;
		if(behaviors==null) behaviors=new Vector();
		for(int b=0;b<numBehaviors();b++)
		{
			Behavior B=fetchBehavior(b);
			if(B.ID().equals(to.ID()))
				return;
		}

		// first one! so start ticking...
		if(behaviors.size()==0)
			CMLib.threads().startTickDown(this,Tickable.TICKID_ITEM_BEHAVIOR,1);
		to.startBehavior(this);
		behaviors.addElement(to);
	}
	public void delBehavior(Behavior to)
	{
		if(behaviors==null) return;
		behaviors.removeElement(to);
		if(behaviors.size()==0)
			CMLib.threads().deleteTick(this,Tickable.TICKID_ITEM_BEHAVIOR);
	}
	public int numBehaviors()
	{
		if(behaviors==null) return 0;
		return behaviors.size();
	}
	public Behavior fetchBehavior(int index)
	{
		if(behaviors==null) return null;
		try
		{
			return (Behavior)behaviors.elementAt(index);
		}
		catch(java.lang.ArrayIndexOutOfBoundsException x){}
		return null;
	}
	public Behavior fetchBehavior(String ID)
	{
		if(behaviors==null) return null;
		for(int b=0;b<numBehaviors();b++)
		{
			Behavior B=fetchBehavior(b);
			if((B!=null)&&(B.ID().equalsIgnoreCase(ID)))
				return B;
		}
		return null;
	}
	protected String tackOns()
	{
		String identity="";
		if(numEffects()>0)
			identity+="\n\rHas the following magical properties: ";
		for(int a=0;a<numEffects();a++)
		{
			Ability A=fetchEffect(a);
			if((A!=null)&&(A.accountForYourself().length()>0))
				identity+="\n\r"+A.accountForYourself();
		}
		return identity;
	}

	public int maxRange(){return 0;}
	public int minRange(){return 0;}
	protected static String[] CODES={"CLASS","USES","LEVEL","ABILITY","TEXT"};
	public String getStat(String code){
		switch(getCodeNum(code))
		{
		case 0: return ID();
		case 1: return ""+usesRemaining();
		case 2: return ""+baseEnvStats().ability();
		case 3: return ""+baseEnvStats().level();
		case 4: return text();
		}
		return "";
	}
	public void setStat(String code, String val)
	{
		switch(getCodeNum(code))
		{
		case 0: return;
		case 1: setUsesRemaining(CMath.s_int(val)); break;
		case 2: baseEnvStats().setLevel(CMath.s_int(val)); break;
		case 3: baseEnvStats().setAbility(CMath.s_int(val)); break;
		case 4: setMiscText(val); break;
		}
	}
	public int getSaveStatIndex(){return getStatCodes().length;}
	public String[] getStatCodes(){return CODES;}
	protected int getCodeNum(String code){
		for(int i=0;i<CODES.length;i++)
			if(code.equalsIgnoreCase(CODES[i])) return i;
		return -1;
	}
	public boolean sameAs(Environmental E)
	{
		if(!(E instanceof StdItem)) return false;
		for(int i=0;i<CODES.length;i++)
			if(!E.getStat(CODES[i]).equals(getStat(CODES[i])))
				return false;
		return true;
	}
}
