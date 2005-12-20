package com.planet_ink.coffee_mud.Locales;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
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
public class StdRoom implements Room
{
	public String ID(){return "StdRoom";}
	protected String myID="";
	protected String name="the room";
	protected String displayText="Standard Room";
	protected String imageName=null;
	protected byte[] description=null;
	protected Area myArea=null;
	protected EnvStats envStats=(EnvStats)CMClass.getCommon("DefaultEnvStats");
	protected EnvStats baseEnvStats=(EnvStats)CMClass.getCommon("DefaultEnvStats");
	public Exit[] exits=new Exit[Directions.NUM_DIRECTIONS];
	public Room[] doors=new Room[Directions.NUM_DIRECTIONS];
	protected Vector affects=null;
	protected Vector behaviors=null;
	protected Vector contents=new Vector();
	protected Vector inhabitants=new Vector();
	protected int domainType=Room.DOMAIN_OUTDOORS_CITY;
	protected int domainCondition=Room.CONDITION_NORMAL;
	protected int maxRange=-1; // -1 = use indoor/outdoor algorithm
	protected boolean mobility=true;
	protected GridLocale gridParent=null;
	protected long tickStatus=Tickable.STATUS_NOT;

	// base move points and thirst points per round
	protected int baseThirst=1;
	protected int myResource=-1;
	protected long resourceFound=0;
    
	protected boolean skyedYet=false;
	public StdRoom()
	{
        super();
        CMClass.bumpCounter(CMClass.OBJECT_LOCALE);
		baseEnvStats.setWeight(2);
		recoverEnvStats();
	}
    protected void finalize(){CMClass.unbumpCounter(CMClass.OBJECT_LOCALE);}
	public CMObject newInstance()
	{
		try
        {
			return (Environmental)this.getClass().newInstance();
		}
		catch(Exception e)
		{
			Log.errOut(ID(),e);
		}
		return new StdRoom();
	}

	public String roomID()
	{
		return myID	;
	}
	public String Name(){ return name;}
	public void setName(String newName){name=newName;}
	public String name()
	{
		if(envStats().newName()!=null) return envStats().newName();
		return name;
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
    
	
	public boolean isGeneric(){return false;}
	protected void cloneFix(Room E)
	{
		baseEnvStats=(EnvStats)E.baseEnvStats().copyOf();
		envStats=(EnvStats)E.envStats().copyOf();

		contents=new Vector();
		inhabitants=new Vector();
		affects=null;
		behaviors=null;
		exits=new Exit[Directions.NUM_DIRECTIONS];
		doors=new Room[Directions.NUM_DIRECTIONS];
		for(int d=0;d<Directions.NUM_DIRECTIONS;d++)
		{
			if(E.rawExits()[d]!=null)
				exits[d]=(Exit)E.rawExits()[d].copyOf();
			if(E.rawDoors()[d]!=null)
				doors[d]=E.rawDoors()[d];
		}
		for(int i=0;i<E.numItems();i++)
		{
			Item I2=E.fetchItem(i);
			if(I2!=null)
			{
				Item I=(Item)I2.copyOf();
				I.setOwner(this);
				contents.addElement(I);
			}
		}
		for(int i=0;i<numItems();i++)
		{
			Item I2=fetchItem(i);
			if((I2!=null)
			&&(I2.container()!=null)
			&&(!isContent(I2.container())))
				for(int ii=0;ii<E.numItems();ii++)
					if((E.fetchItem(ii)==I2.container())&&(ii<numItems()))
					{I2.setContainer(fetchItem(ii)); break;}
		}
		for(int m=0;m<E.numInhabitants();m++)
		{
			MOB M2=E.fetchInhabitant(m);
			if((M2!=null)&&(M2.isEligibleMonster()))
			{
				MOB M=(MOB)M2.copyOf();
				if(M.getStartRoom()==E)
					M.setStartRoom(this);
				M.setLocation(this);
				inhabitants.addElement(M);
			}
		}
		for(int i=0;i<E.numEffects();i++)
		{
			Ability A=E.fetchEffect(i);
			if((A!=null)&&(!A.canBeUninvoked()))
				addEffect((Ability)A.copyOf());
		}
		for(int i=0;i<E.numBehaviors();i++)
		{
			Behavior B=E.fetchBehavior(i);
			if(B!=null)
				addBehavior((Behavior)B.copyOf());
		}
	}
	public CMObject copyOf()
	{
		try
		{
			StdRoom R=(StdRoom)this.clone();
            CMClass.bumpCounter(CMClass.OBJECT_LOCALE);
			R.cloneFix(this);
			return R;

		}
		catch(CloneNotSupportedException e)
		{
			return this.newInstance();
		}
	}
	public int domainType()
	{
		return domainType;
	}

	public int domainConditions()
	{
		return domainCondition;
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
		if(CMProps.getBoolVar(CMProps.SYSTEMB_ROOMDNOCACHE)
		&&((description==null)||(description.length==0))
		&&(roomID().trim().length()>0))
		{
			String txt=CMLib.database().DBReadRoomDesc(roomID());
			if(txt==null)
			{
				Log.errOut("Unable to recover description for "+roomID()+".");
				return "";
			}
			return txt;
		}
		else
		if((description==null)||(description.length==0))
			return "";
		else
		if(CMProps.getBoolVar(CMProps.SYSTEMB_ROOMDCOMPRESS))
			return CMLib.encoder().decompressString(description);
		else
			return new String(description);
	}
	public void setDescription(String newDescription)
	{
		if(newDescription.length()==0)
			description=null;
		else
		if(CMProps.getBoolVar(CMProps.SYSTEMB_ROOMDCOMPRESS))
			description=CMLib.encoder().compressString(newDescription);
		else
			description=newDescription.getBytes();
	}
	public String text()
	{
		return CMLib.coffeeMaker().getPropertiesStr(this,true);
	}
	public void setMiscText(String newMiscText)
	{
		if(newMiscText.trim().length()>0)
			CMLib.coffeeMaker().setPropertiesStr(this,newMiscText,true);
	}
	public void setRoomID(String newID)
	{
        if((myID!=null)&&(!myID.equals(newID)))
        {
            myID=newID;
            if(myArea!=null)
            { 
                // force the re-sort
                myArea.delRoom(this);
                myArea.addRoom(this);
            }
        }
        else
            myID=newID;
	}
	public Area getArea()
	{
		if(myArea==null) return CMClass.anyOldArea();
		return myArea;
	}
	public void setArea(Area newArea)
	{
        if(newArea!=myArea)
        {
            if(myArea!=null) myArea.delRoom(this);
            myArea=newArea;
            if(myArea!=null) myArea.addRoom(this);
        }
	}

	public void setGridParent(GridLocale room){gridParent=room;}
	public GridLocale getGridParent(){return gridParent;}
	
	public void giveASky(int depth)
	{
		if(skyedYet) return;
		if(depth>1000) return;
		skyedYet=true;
		if((rawDoors()[Directions.UP]==null)
		&&((domainType()&Room.INDOORS)==0)
		&&(domainType()!=Room.DOMAIN_OUTDOORS_UNDERWATER)
		&&(domainType()!=Room.DOMAIN_OUTDOORS_AIR)
		&&(CMProps.getIntVar(CMProps.SYSTEMI_SKYSIZE)>0))
		{
			Exit o=CMClass.getExit("StdOpenDoorway");
			GridLocale sky=(GridLocale)CMClass.getLocale("EndlessThinSky");
			sky.setArea(getArea());
			sky.setRoomID("");
			rawDoors()[Directions.UP]=sky;
			rawExits()[Directions.UP]=o;
			sky.rawDoors()[Directions.DOWN]=this;
			sky.rawExits()[Directions.DOWN]=o;
			for(int d=0;d<4;d++)
			{
				Room thatRoom=rawDoors()[d];
				Room thatSky=null;
				if((thatRoom!=null)&&(rawExits()[d]!=null))
				{
					thatRoom.giveASky(depth+1);
					thatSky=thatRoom.rawDoors()[Directions.UP];
				}
				if((thatSky!=null)&&(thatSky.roomID().length()==0)
				&&((thatSky instanceof EndlessThinSky)||(thatSky instanceof EndlessSky)))
				{
					sky.rawDoors()[d]=thatSky;
					Exit xo=rawExits()[d];
					if((xo==null)||(xo.hasADoor())) xo=o;
					sky.rawExits()[d]=o;
					thatSky.rawDoors()[Directions.getOpDirectionCode(d)]=sky;
					xo=thatRoom.rawExits()[Directions.getOpDirectionCode(d)];
					if((xo==null)||(xo.hasADoor())) xo=o;
					thatSky.rawExits()[Directions.getOpDirectionCode(d)]=xo;
					((GridLocale)thatSky).clearGrid(null);
				}
			}
			sky.clearGrid(null);
		}
	}

	public void clearSky()
	{
		if(!skyedYet) return;
		Room skyGridRoom=rawDoors()[Directions.UP];
		if(skyGridRoom==null) return;
		if((skyGridRoom.roomID().length()==0)
		&&((skyGridRoom instanceof EndlessSky)||(skyGridRoom instanceof EndlessThinSky)))
		{
			((GridLocale)skyGridRoom).clearGrid(null);
			rawDoors()[Directions.UP]=null;
			rawExits()[Directions.UP]=null;
            skyGridRoom.rawDoors()[Directions.DOWN]=null;
            skyGridRoom.rawExits()[Directions.DOWN]=null;
            skyGridRoom.destroyRoom();
			skyedYet=false;
		}
	}

	public Vector resourceChoices(){return null;}
	public void setResource(int resourceCode)
	{
		myResource=resourceCode;
		resourceFound=0;
		if(resourceCode>=0)
			resourceFound=System.currentTimeMillis();
	}

	public int myResource()
	{
		if(resourceFound!=0)
		{
			if(resourceFound<(System.currentTimeMillis()-(30*TimeManager.MILI_MINUTE)))
				setResource(-1);
		}
		if(myResource<0)
		{
			if(resourceChoices()==null)
				setResource(-1);
			else
			{
				int totalChance=0;
				for(int i=0;i<resourceChoices().size();i++)
				{
					int resource=((Integer)resourceChoices().elementAt(i)).intValue();
					int chance=EnvResource.RESOURCE_DATA[resource&EnvResource.RESOURCE_MASK][2];
					totalChance+=chance;
				}
				setResource(-1);
				int theRoll=CMLib.dice().roll(1,totalChance,0);
				totalChance=0;
				for(int i=0;i<resourceChoices().size();i++)
				{
					int resource=((Integer)resourceChoices().elementAt(i)).intValue();
					int chance=EnvResource.RESOURCE_DATA[resource&EnvResource.RESOURCE_MASK][2];
					totalChance+=chance;
					if(theRoll<=totalChance)
					{
						setResource(resource);
						break;
					}
				}
			}
		}
		return myResource;
	}

	public void toggleMobility(boolean onoff){mobility=onoff;}
	public boolean getMobility(){return mobility;}
	
	protected Vector herbTwistChart(){return null;}

	public boolean okMessage(Environmental myHost, CMMsg msg)
	{
		if(!getArea().okMessage(this,msg))
			return false;

		if(msg.amITarget(this))
		{
			MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_LEAVE:
				if((!CMLib.flags().allowsMovement(this))||(!getMobility()))
					return false;
				break;
			case CMMsg.TYP_FLEE:
			case CMMsg.TYP_ENTER:
				if((!CMLib.flags().allowsMovement(this))||(!getMobility()))
					return false;
				if(!mob.isMonster())
					giveASky(0);
				break;
			case CMMsg.TYP_AREAAFFECT:
				// obsolete with the area objects
				break;
			case CMMsg.TYP_CAST_SPELL:
			case CMMsg.TYP_DELICATE_HANDS_ACT:
			case CMMsg.TYP_OK_ACTION:
			case CMMsg.TYP_JUSTICE:
			case CMMsg.TYP_OK_VISUAL:
			case CMMsg.TYP_SNIFF:
				break;
            case CMMsg.TYP_LIST:
            case CMMsg.TYP_BUY:
            case CMMsg.TYP_SELL:
            case CMMsg.TYP_VIEW:
            case CMMsg.TYP_VALUE:
                if(CMLib.coffeeShops().getShopKeeper(this)==null)
                {
                    mob.tell("You can't shop here.");
                    return false;
                }
                break;
			case CMMsg.TYP_SPEAK:
				break;
			default:
				if(((CMath.bset(msg.targetMajor(),CMMsg.MASK_HANDS))||(CMath.bset(msg.targetMajor(),CMMsg.MASK_MOUTH)))
                &&(msg.targetMinor()!=CMMsg.TYP_THROW))
				{
					mob.tell("You can't do that here.");
					return false;
				}
				break;
			}
		}

		if(isInhabitant(msg.source()))
			if(!msg.source().okMessage(this,msg))
				return false;
			
		for(int i=0;i<numInhabitants();i++)
		{
			MOB inhab=fetchInhabitant(i);
			if((inhab!=null)
			&&(inhab!=msg.source())
			&&(!inhab.okMessage(this,msg)))
				return false;
		}
		for(int i=0;i<numItems();i++)
		{
			Item content=fetchItem(i);
			if((content!=null)&&(!content.okMessage(this,msg)))
				return false;
		}
		for(int i=0;i<numEffects();i++)
		{
			Ability A=fetchEffect(i);
			if((A!=null)&&(!A.okMessage(this,msg)))
				return false;
		}
		for(int b=0;b<numBehaviors();b++)
		{
			Behavior B=fetchBehavior(b);
			if((B!=null)&&(!B.okMessage(this,msg)))
				return false;
		}

		for(int i=0;i<Directions.NUM_DIRECTIONS;i++)
		{
			Exit thisExit=rawExits()[i];
			if(thisExit!=null)
				if(!thisExit.okMessage(this,msg))
					return false;
		}
		return true;
	}

	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		getArea().executeMsg(this,msg);

		if(msg.amITarget(this))
		{
			MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_LEAVE:
			{
				if(!CMath.bset(msg.targetCode(),CMMsg.MASK_OPTIMIZE))
					recoverRoomStats();
				break;
			}
			case CMMsg.TYP_FLEE:
			{
				if(!CMath.bset(msg.targetCode(),CMMsg.MASK_OPTIMIZE))
					recoverRoomStats();
				break;
			}
			case CMMsg.TYP_ENTER:
			case CMMsg.TYP_RECALL:
			{
				if(!CMath.bset(msg.targetCode(),CMMsg.MASK_OPTIMIZE))
					recoverRoomStats();
                if(msg.source().playerStats()!=null)
                    msg.source().playerStats().addRoomVisit(this);
				break;
			}
			case CMMsg.TYP_LOOK:
            case CMMsg.TYP_EXAMINE:
                CMLib.commands().handleBeingLookedAt(msg);
				break;
			case CMMsg.TYP_SNIFF:
                CMLib.commands().handleBeingSniffed(msg);
    			break;
			case CMMsg.TYP_READ:
				if(CMLib.flags().canBeSeenBy(this,mob))
					mob.tell("There is nothing written here.");
				else
					mob.tell("You can't see that!");
				break;
			case CMMsg.TYP_AREAAFFECT:
				// obsolete with the area objects
				break;
			default:
				break;
			}
		}

		for(int i=0;i<numItems();i++)
		{
			Item content=fetchItem(i);
			if(content!=null)
				content.executeMsg(this,msg);
		}

		for(int d=0;d<Directions.NUM_DIRECTIONS;d++)
		{
			Exit thisExit=rawExits()[d];
			if(thisExit!=null)
				thisExit.executeMsg(this,msg);
		}

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

	}

	public void startItemRejuv()
	{
		for(int c=0;c<numItems();c++)
		{
			Item item=fetchItem(c);
			if((item!=null)&&(item.container()==null))
			{
				ItemTicker I=(ItemTicker)CMClass.getAbility("ItemRejuv");
				I.unloadIfNecessary(item);
				if((item.envStats().rejuv()<Integer.MAX_VALUE)&&(item.envStats().rejuv()>0))
					I.loadMeUp(item,this);
			}
		}
	}
    
	public long getTickStatus(){return tickStatus;}
	public boolean tick(Tickable ticking, int tickID)
	{
		tickStatus=Tickable.STATUS_START;
		if(tickID==MudHost.TICK_ROOM_BEHAVIOR)
		{
			if(numBehaviors()==0) return false;
			for(int b=0;b<numBehaviors();b++)
			{
				tickStatus=Tickable.STATUS_BEHAVIOR+b;
				Behavior B=fetchBehavior(b);
				if(B!=null) B.tick(ticking,tickID);
			}
		}
		else
		{
			int a=0;
			while(a<numEffects())
			{
				Ability A=fetchEffect(a);
				if(A!=null)
				{
					int s=numEffects();
					tickStatus=Tickable.STATUS_AFFECT+a;
					if(!A.tick(ticking,tickID))
						A.unInvoke();
					if(numEffects()==s)
						a++;
				}
				else
					a++;
			}
		}
		tickStatus=Tickable.STATUS_NOT;
		return true;
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
		envStats=(EnvStats)baseEnvStats.copyOf();
		Area myArea=getArea();
		if(myArea!=null)
			myArea.affectEnvStats(this,envStats());

		for(int a=0;a<numEffects();a++)
		{
			Ability A=fetchEffect(a);
			if(A!=null) A.affectEnvStats(this,envStats);
		}
		for(int i=0;i<numItems();i++)
		{
			Item I=fetchItem(i);
			if(I!=null) I.affectEnvStats(this,envStats);
		}
		for(int m=0;m<numInhabitants();m++)
		{
			MOB M=fetchInhabitant(m);
			if(M!=null) M.affectEnvStats(this,envStats);
		}
	}
	public void recoverRoomStats()
	{
		recoverEnvStats();
		for(int m=0;m<numInhabitants();m++)
		{
			MOB M=fetchInhabitant(m);
			if(M!=null)
			{
				M.recoverCharStats();
				M.recoverEnvStats();
				M.recoverMaxState();
			}
		}
		for(int d=0;d<exits.length;d++)
		{
			Exit X=exits[d];
			if(X!=null) X.recoverEnvStats();
		}
		for(int i=0;i<numItems();i++)
		{
			Item I=fetchItem(i);
			if(I!=null) I.recoverEnvStats();
		}
	}

	public void setBaseEnvStats(EnvStats newBaseEnvStats)
	{
		baseEnvStats=(EnvStats)newBaseEnvStats.copyOf();
	}

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		getArea().affectEnvStats(affected,affectableStats);
		if(envStats().sensesMask()>0)
			affectableStats.setSensesMask(affectableStats.sensesMask()|envStats().sensesMask());
		int disposition=envStats().disposition()
			&((Integer.MAX_VALUE-(EnvStats.IS_DARK|EnvStats.IS_LIGHTSOURCE|EnvStats.IS_SLEEPING|EnvStats.IS_HIDDEN)));
		if(disposition>0)
			affectableStats.setDisposition(affectableStats.disposition()|disposition);
		for(int a=0;a<numEffects();a++)
		{
			Ability A=fetchEffect(a);
			if((A!=null)&&(A.bubbleAffect()))
			   A.affectEnvStats(affected,affectableStats);
		}
	}
	public void affectCharStats(MOB affectedMob, CharStats affectableStats)
	{
		getArea().affectCharStats(affectedMob,affectableStats);
		for(int a=0;a<numEffects();a++)
		{
			Ability A=fetchEffect(a);
			if((A!=null)&&(A.bubbleAffect()))
			   A.affectCharStats(affectedMob,affectableStats);
		}
	}
	public void affectCharState(MOB affectedMob, CharState affectableMaxState)
	{
		getArea().affectCharState(affectedMob,affectableMaxState);
		for(int a=0;a<numEffects();a++)
		{
			Ability A=fetchEffect(a);
			if((A!=null)&&(A.bubbleAffect()))
			   A.affectCharState(affectedMob,affectableMaxState);
		}
	}
	public int compareTo(Object o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}

	protected String parseVariesCodes(String text)
	{
		StringBuffer buf=new StringBuffer("");
		int x=text.indexOf("<");
		String elseStr=null;
		while(x>=0)
		{
			buf.append(text.substring(0,x));
			text=text.substring(x);
			boolean found=false;
			for(int i=0;i<variationCodes.length;i++)
				if(text.startsWith("<"+variationCodes[i][0]+">"))
				{
					found=true;
					int y=text.indexOf("</"+variationCodes[i][0]+">");
					String dispute=null;
					if(y>0)
					{
						dispute=text.substring(variationCodes[i][0].length()+2,y);
						text=text.substring(y+variationCodes[i][0].length()+3);
					}
					else
					{
						dispute=text.substring(variationCodes[i][0].length()+2);
						text="";
					}
					int num=CMath.s_int(variationCodes[i][1].substring(1));
					switch(variationCodes[i][1].charAt(0))
					{
					case 'D': elseStr=parseVariesCodes(dispute); break;
					case 'W':
						if(getArea().getClimateObj().weatherType(null)==num)
							buf.append(parseVariesCodes(dispute));
						break;
					case 'C':
						if(getArea().getTimeObj().getTODCode()==num)
							buf.append(parseVariesCodes(dispute));
						break;
					case 'S':
						if(getArea().getTimeObj().getSeasonCode()==num)
							buf.append(parseVariesCodes(dispute));
						break;
					}
					break;
				}
			if(!found)
				x=text.indexOf("<",1);
			else
				x=text.indexOf("<");
		}
		if((buf.length()==0)&&(elseStr!=null))
		{
			buf.append(text);
			buf.append(elseStr);
		}
		else
			buf.append(text);
		return buf.toString();
	}

	protected String parseVaries(String text)
	{
		if(text.startsWith("<VARIES>"))
		{
			int x=text.indexOf("</VARIES>");
			if(x>=0)
				return parseVariesCodes(text.substring(8,x))+text.substring(x+9);
			return parseVariesCodes(text.substring(8));
		}
		return text;
	}

	public String roomTitle(){
		return parseVaries(displayText());
	}
	public String roomDescription(){
		return parseVaries(description());
	}


	public void bringMobHere(MOB mob, boolean andFollowers)
	{
		if(mob==null) return;

		Room oldRoom=mob.location();
		if(oldRoom!=null)
			oldRoom.delInhabitant(mob);
		addInhabitant(mob);
		mob.setLocation(this);

		if((andFollowers)&&(oldRoom!=null))
		{
			for(int f=0;f<mob.numFollowers();f++)
			{
				MOB fol=mob.fetchFollower(f);
				if((fol!=null)&&(fol.location()==oldRoom))
					bringMobHere(fol,true);
			}
		}
		if(mob.riding()!=null)
		{
			if((mob.riding().rideBasis()!=Rideable.RIDEABLE_SIT)
			&&(mob.riding().rideBasis()!=Rideable.RIDEABLE_TABLE)
			&&(mob.riding().rideBasis()!=Rideable.RIDEABLE_ENTERIN)
			&&(mob.riding().rideBasis()!=Rideable.RIDEABLE_SLEEP)
			&&(mob.riding().rideBasis()!=Rideable.RIDEABLE_LADDER))
			{
				if(mob.riding() instanceof MOB)
					bringMobHere((MOB)mob.riding(),andFollowers);
				else
				if(mob.riding() instanceof Item)
					bringItemHere((Item)mob.riding(),-1);
			}
			else
				mob.setRiding(null);
		}
		if(oldRoom!=null)
			oldRoom.recoverRoomStats();
		recoverRoomStats();
	}

	public void bringItemHere(Item item, double survivalRLHours)
	{
		if(item==null) return;

		if(item.owner()==null) return;
		Environmental o=item.owner();
		
		Vector V=new Vector();
		if(item instanceof Container)
			V=((Container)item).getContents();
		if(o instanceof MOB)((MOB)o).delInventory(item);
		if(o instanceof Room) ((Room)o).delItem(item);

		if(survivalRLHours<=0.0)
			addItem(item);
		else
			addItemRefuse(item,survivalRLHours);
		for(int v=0;v<V.size();v++)
		{
			Item i2=(Item)V.elementAt(v);
			if(o instanceof MOB) ((MOB)o).delInventory(i2);
			if(o instanceof Room) ((Room)o).delItem(i2);
			addItem(i2);
		}
		item.setContainer(null);
		
		if(item.riding()!=null)
		{
			if((item.riding().rideBasis()!=Rideable.RIDEABLE_SIT)
			&&(item.riding().rideBasis()!=Rideable.RIDEABLE_TABLE)
			&&(item.riding().rideBasis()!=Rideable.RIDEABLE_ENTERIN)
			&&(item.riding().rideBasis()!=Rideable.RIDEABLE_SLEEP)
			&&(item.riding().rideBasis()!=Rideable.RIDEABLE_LADDER))
			{
				if(item.riding() instanceof MOB)
					bringMobHere((MOB)item.riding(),true);
				else
				if(item.riding() instanceof Item)
					bringItemHere((Item)item.riding(),-1);
			}
			else
				item.setRiding(null);
		}
		
		if(o instanceof Room)
			((Room)o).recoverRoomStats();
		else
		if(o instanceof MOB)
		{
			((MOB)o).recoverCharStats();
			((MOB)o).recoverEnvStats();
			((MOB)o).recoverMaxState();
		}
		recoverRoomStats();
	}
	public Exit getReverseExit(int direction)
	{
		if((direction<0)||(direction>=Directions.NUM_DIRECTIONS))
			return null;
		Room opRoom=getRoomInDir(direction);
		if(opRoom!=null)
			return opRoom.getExitInDir(Directions.getOpDirectionCode(direction));
		return null;
	}
	public Exit getPairedExit(int direction)
	{
		Exit opExit=getReverseExit(direction);
		Exit myExit=getExitInDir(direction);
		if((myExit==null)||(opExit==null))
			return null;
		if(myExit.hasADoor()!=opExit.hasADoor())
			return null;
		return opExit;
	}

	public Room getRoomInDir(int direction)
	{
		if((direction<0)||(direction>=Directions.NUM_DIRECTIONS))
			return null;
		Room nextRoom=rawDoors()[direction];
		if((nextRoom==null)
        &&(gridParent instanceof StdThinGrid))
        {
            GridLocale GP=getGridParent();
			int x=GP.getChildX(this);
			int y=GP.getChildY(this);
			if((x>=0)&&(x<GP.xSize())&&(y>=0)&&(y<GP.ySize()))
			    ((StdThinGrid)GP).fillExitsOfGridRoom(this,x,y);
			nextRoom=rawDoors()[direction];
        }
		    
		if(nextRoom instanceof GridLocale)
		{
			Room realRoom=((GridLocale)nextRoom).getAltRoomFrom(this,direction);
			if(realRoom!=null) return realRoom;
		}
		return nextRoom;
	}
	public Exit getExitInDir(int direction)
	{
		if((direction<0)||(direction>=Directions.NUM_DIRECTIONS))
			return null;
		return rawExits()[direction];
	}

	public void listExits(MOB mob)
	{
		if(!CMLib.flags().canSee(mob))
		{
			mob.tell("You can't see anything!");
			return;
		}

		mob.tell("^DObvious exits:^.^N");
        String Dir=null;
		for(int i=0;i<Directions.NUM_DIRECTIONS;i++)
		{
			Exit exit=getExitInDir(i);
			Room room=getRoomInDir(i);
			StringBuffer Say=new StringBuffer("");
			if(exit!=null)
				Say=exit.viewableText(mob, room);
			else
			if((room!=null)&&(CMath.bset(mob.getBitmap(),MOB.ATT_SYSOPMSGS)))
				Say.append(room.roomID()+" via NULL");
			if(Say.length()>0)
            {
                Dir=CMStrings.padRightPreserve(Directions.getDirectionName(i),5);
                if((mob!=null)
                &&(mob.playerStats()!=null)
                &&(room!=null)
                &&(!mob.playerStats().hasVisited(room)))
    				mob.tell("^U^<EX^>" + Dir+"^</EX^>:^.^N ^u"+Say+"^.^N");
                else
                    mob.tell("^D^<EX^>" + Dir+"^</EX^>:^.^N ^d"+Say+"^.^N");
            }
		}
		Item I=null;
		for(int i=0;i<numItems();i++)
		{
		    I=fetchItem(i);
		    if((I instanceof Exit)&&(((Exit)I).doorName().length()>0))
		    {
				StringBuffer Say=((Exit)I).viewableText(mob, this);
				if(Say.length()>5)
					mob.tell("^D^<MEX^>" + ((Exit)I).doorName()+"^</MEX^>:^.^N ^d"+Say+"^.^N");
				else
				if(Say.length()>0)
					mob.tell("^D^<MEX^>" + CMStrings.padRight(((Exit)I).doorName(),5)+"^</MEX^>:^.^N ^d"+Say+"^.^N");
		    }
		}
	}
	public void listShortExits(MOB mob)
	{
		if(!CMLib.flags().canSee(mob)) return;
		StringBuffer buf=new StringBuffer("^D[Exits: ");
		for(int i=0;i<Directions.NUM_DIRECTIONS;i++)
		{
			Exit exit=getExitInDir(i);
			if((exit!=null)&&(exit.viewableText(mob, getRoomInDir(i)).length()>0))
				buf.append("^<EX^>"+Directions.getDirectionName(i)+"^</EX^> ");
		}
		Item I=null;
		for(int i=0;i<numItems();i++)
		{
		    I=fetchItem(i);
		    if((I instanceof Exit)&&(((Exit)I).viewableText(mob, this).length()>0))
		        buf.append("^<MEX^>"+((Exit)I).doorName()+"^</MEX^> ");
		}
		mob.tell(buf.toString().trim()+"]^.^N");
	}

    protected void reallyReallySend(MOB source, CMMsg msg)
	{
		if((Log.debugChannelOn())&&(CMSecurity.isDebugging("MESSAGES")))
			Log.debugOut("StdRoom",((msg.source()!=null)?msg.source().ID():"null")+":"+msg.sourceCode()+":"+msg.sourceMessage()+"/"+((msg.target()!=null)?msg.target().ID():"null")+":"+msg.targetCode()+":"+msg.targetMessage()+"/"+((msg.tool()!=null)?msg.tool().ID():"null")+"/"+msg.othersCode()+":"+msg.othersMessage());
		Vector inhabs=(Vector)inhabitants.clone();
		for(int i=0;i<inhabs.size();i++)
		{
			MOB otherMOB=(MOB)inhabs.elementAt(i);
			if((otherMOB!=null)&&(otherMOB!=source))
				otherMOB.executeMsg(otherMOB,msg);
		}
		executeMsg(source,msg);
	}

    protected void reallySend(MOB source, CMMsg msg)
	{
		reallyReallySend(source,msg);
		// now handle trailer msgs
		if(msg.trailerMsgs()!=null)
		{
			for(int i=0;i<msg.trailerMsgs().size();i++)
			{
				CMMsg msg2=(CMMsg)msg.trailerMsgs().elementAt(i);
				if((msg!=msg2)
				&&((msg2.target()==null)
				   ||(!(msg2.target() instanceof MOB))
				   ||(!((MOB)msg2.target()).amDead()))
				&&(okMessage(source,msg2)))
				{
					source.executeMsg(source,msg2);
					reallyReallySend(source,msg2);
				}
			}
		}
	}

	public void send(MOB source, CMMsg msg)
	{
		source.executeMsg(source,msg);
		reallySend(source,msg);
	}
	public void sendOthers(MOB source, CMMsg msg)
	{
		reallySend(source,msg);
	}

	public void showHappens(int allCode, String allMessage)
	{
		MOB everywhereMOB=CMLib.map().god(this);
		CMMsg msg=CMClass.getMsg(everywhereMOB,null,null,allCode,allCode,allCode,allMessage);
		sendOthers(everywhereMOB,msg);
        everywhereMOB.destroy();
	}
	public void showHappens(int allCode, Environmental like, String allMessage)
	{
		MOB everywhereMOB=CMClass.getMOB("StdMOB");
		everywhereMOB.setName(like.name());
		everywhereMOB.setBaseEnvStats(like.envStats());
		everywhereMOB.setLocation(this);
		everywhereMOB.recoverEnvStats();
		CMMsg msg=CMClass.getMsg(everywhereMOB,null,null,allCode,allCode,allCode,allMessage);
		send(everywhereMOB,msg);
        everywhereMOB.destroy();
	}
	public boolean show(MOB source,
					 Environmental target,
					 int allCode,
					 String allMessage)
	{
		CMMsg msg=CMClass.getMsg(source,target,null,allCode,allCode,allCode,allMessage);
		if((!CMath.bset(allCode,CMMsg.MASK_GENERAL))&&(!okMessage(source,msg)))
			return false;
		send(source,msg);
		return true;
	}
	public boolean show(MOB source,
					 Environmental target,
					 Environmental tool,
					 int allCode,
					 String allMessage)
	{
		CMMsg msg=CMClass.getMsg(source,target,tool,allCode,allCode,allCode,allMessage);
		if((!CMath.bset(allCode,CMMsg.MASK_GENERAL))&&(!okMessage(source,msg)))
			return false;
		send(source,msg);
		return true;
	}
	public boolean showOthers(MOB source,
						   Environmental target,
						   int allCode,
						   String allMessage)
	{
		CMMsg msg=CMClass.getMsg(source,target,null,allCode,allCode,allCode,allMessage);
		if((!CMath.bset(allCode,CMMsg.MASK_GENERAL))&&(!okMessage(source,msg)))
			return false;
		reallySend(source,msg);
		return true;
	}
	public boolean showOthers(MOB source,
						   Environmental target,
						   Environmental tool,
						   int allCode,
						   String allMessage)
	{
		CMMsg msg=CMClass.getMsg(source,target,tool,allCode,allCode,allCode,allMessage);
		if((!CMath.bset(allCode,CMMsg.MASK_GENERAL))&&(!okMessage(source,msg)))
			return false;
		reallySend(source,msg);
		return true;
	}
	public boolean showSource(MOB source,
						   Environmental target,
						   int allCode,
						   String allMessage)
	{
		CMMsg msg=CMClass.getMsg(source,target,null,allCode,allCode,allCode,allMessage);
		if((!CMath.bset(allCode,CMMsg.MASK_GENERAL))&&(!okMessage(source,msg)))
			return false;
		source.executeMsg(source,msg);
		return true;
	}
	public boolean showSource(MOB source,
						   Environmental target,
						   Environmental tool,
						   int allCode,
						   String allMessage)
	{
		CMMsg msg=CMClass.getMsg(source,target,tool,allCode,allCode,allCode,allMessage);
		if((!CMath.bset(allCode,CMMsg.MASK_GENERAL))&&(!okMessage(source,msg)))
			return false;
		source.executeMsg(source,msg);
		return true;
	}

	public Exit[] rawExits()
	{
		return exits;
	}
	public Room[] rawDoors()
	{
		return doors;
	}

	public void destroyRoom()
	{
		try{
		for(int a=numEffects()-1;a>=0;a--)
			fetchEffect(a).unInvoke();
		}catch(Exception e){}
		while(numEffects()>0)
			delEffect(fetchEffect(0));
		try{
		for(int a=numInhabitants()-1;a>=0;a--)
			fetchInhabitant(a).destroy();
		}catch(Exception e){}
		while(numInhabitants()>0)
			delInhabitant(fetchInhabitant(0));
		while(numBehaviors()>0)
			delBehavior(fetchBehavior(0));
		try{
            while(numItems()>0)
                fetchItem(0).destroy();
		}catch(Exception e){}
		while(numItems()>0)
			delItem(fetchItem(0));
		if(this instanceof GridLocale)
			((GridLocale)this).clearGrid(null);
		clearSky();
		CMLib.threads().deleteTick(this,-1);
        imageName=null;
        setArea(null);
        baseEnvStats=(EnvStats)CMClass.getCommon("DefaultEnvStats");
        envStats=baseEnvStats;
        exits=new Exit[Directions.NUM_DIRECTIONS];
        doors=new Room[Directions.NUM_DIRECTIONS];
        affects=null;
        behaviors=null;
        contents=new Vector();
        inhabitants=new Vector();
        gridParent=null;
	}

	public MOB fetchInhabitant(String inhabitantID)
	{
		MOB mob=(MOB)CMLib.english().fetchEnvironmental(inhabitants,inhabitantID,true);
		if(mob==null)
			mob=(MOB)CMLib.english().fetchEnvironmental(inhabitants,inhabitantID, false);
		return mob;
	}
	public void addInhabitant(MOB mob)
	{
		inhabitants.addElement(mob);
	}
	public int numInhabitants()
	{
		return inhabitants.size();
	}
	public int numPCInhabitants()
	{
		int numUsers=0;
		for(int i=0;i<numInhabitants();i++)
		{
			MOB inhab=fetchInhabitant(i);
			if((inhab!=null)
			&&(!inhab.isMonster()))
				numUsers++;
		}
		return numUsers;
	}
	public MOB fetchPCInhabitant(int which)
	{
		int numUsers=0;
		for(int i=0;i<numInhabitants();i++)
		{
			MOB inhab=fetchInhabitant(i);
			if((inhab!=null)
			&&(!inhab.isMonster()))
			{
				if(numUsers==which)
					return inhab;
				numUsers++;
			}
		}
		return null;
	}
	public boolean isInhabitant(MOB mob)
	{
		return inhabitants.contains(mob);
	}
	public MOB fetchInhabitant(int i)
	{
		try
		{
			return (MOB)inhabitants.elementAt(i);
		}
		catch(java.lang.ArrayIndexOutOfBoundsException x){}
		return null;
	}
	public void delInhabitant(MOB mob)
	{
		inhabitants.removeElement(mob);
	}

	public Item fetchAnyItem(String itemID)
	{
		Item item=(Item)CMLib.english().fetchEnvironmental(contents,itemID,true);
		if(item==null) item=(Item)CMLib.english().fetchEnvironmental(contents,itemID,false);
		return item;
	}
	public Item fetchItem(Item goodLocation, String itemID)
	{
		Item item=CMLib.english().fetchAvailableItem(contents,itemID,goodLocation,Item.WORN_REQ_UNWORNONLY,true);
		if(item==null) item=CMLib.english().fetchAvailableItem(contents,itemID,goodLocation,Item.WORN_REQ_UNWORNONLY,false);
		return item;
	}
	public void addItem(Item item)
	{
		item.setOwner(this);
		contents.addElement(item);
		item.recoverEnvStats();
	}
	public void addItemRefuse(Item item, double survivalRLHours)
	{
		addItem(item);
        if(survivalRLHours<=0)
            item.setDispossessionTime(0);
        else
    		item.setDispossessionTime(System.currentTimeMillis()+Math.round(CMath.mul(survivalRLHours,TimeManager.MILI_HOUR)));
	}
	public void delItem(Item item)
	{
		contents.removeElement(item);
		item.recoverEnvStats();
	}
	public int numItems()
	{
		return contents.size();
	}
	public boolean isContent(Item item)
	{
		return contents.contains(item);
	}
	public Item fetchItem(int i)
	{
		try
		{
			return (Item)contents.elementAt(i);
		}
		catch(java.lang.ArrayIndexOutOfBoundsException x){}
		return null;
	}
	public Environmental fetchFromMOBRoomItemExit(MOB mob, Item goodLocation, String thingName, int wornReqCode)
	{
		Environmental found=null;
		boolean mineOnly=(mob!=null)&&(thingName.toUpperCase().trim().startsWith("MY "));
		if(mineOnly) thingName=thingName.trim().substring(3).trim();
		if((mob!=null)&&(wornReqCode!=Item.WORN_REQ_WORNONLY))
			found=mob.fetchCarried(goodLocation, thingName);
		if((found==null)&&(!mineOnly))
		{
			if(found==null)	found=CMLib.english().fetchEnvironmental(exits,thingName,true);
			if(found==null) found=CMLib.english().fetchAvailableItem(contents,thingName,goodLocation,wornReqCode,true);
			if(found==null)	found=CMLib.english().fetchEnvironmental(exits,thingName,false);
			if(found==null) found=CMLib.english().fetchAvailableItem(contents,thingName,goodLocation,wornReqCode,false);
			if((found!=null)&&(CMLib.flags().canBeSeenBy(found,mob)))
				return found;
			while((found!=null)&&(!CMLib.flags().canBeSeenBy(found,mob)))
			{
				String newThingName=CMLib.english().bumpDotNumber(thingName);
				if(!newThingName.equals(thingName))
				{
					thingName=newThingName;
					found=fetchFromRoomFavorItems(goodLocation, thingName,wornReqCode);
				}
				else
					found=null;
			}
		}

		if((found!=null) // the smurfy well/gate exception
		&&(found instanceof Item)
		&&(goodLocation==null)
		&&(found.displayText().length()==0)
		&&(thingName.indexOf(".")<0))
		{
			Environmental visibleItem=null;
			visibleItem=CMLib.english().fetchEnvironmental(exits,thingName,false);
			if(visibleItem==null)
				visibleItem=fetchFromMOBRoomItemExit(null,null,thingName+".2",wornReqCode);
			if(visibleItem!=null)
				found=visibleItem;
		}
		if((mob!=null)&&(found==null)&&(wornReqCode!=Item.WORN_REQ_UNWORNONLY))
			found=mob.fetchWornItem(thingName);
		return found;
	}
	public Environmental fetchFromRoomFavorItems(Item goodLocation, String thingName, int wornReqCode)
	{
		// def was Item.WORN_REQ_UNWORNONLY;
		Environmental found=null;
		Vector V=(Vector)contents.clone();
		for(int e=0;e<exits.length;e++)
		    if(exits[e]!=null)V.addElement(exits[e]);
		V.addAll(inhabitants);
		found=CMLib.english().fetchAvailable(V,thingName,goodLocation,wornReqCode,true);
		if(found==null) found=CMLib.english().fetchAvailable(V,thingName,goodLocation,wornReqCode,false);

		if((found!=null) // the smurfy well exception
		&&(found instanceof Item)
		&&(goodLocation==null)
		&&(found.displayText().length()==0)
		&&(thingName.indexOf(".")<0))
		{
			Environmental visibleItem=fetchFromRoomFavorItems(null,thingName+".2",wornReqCode);
			if(visibleItem!=null)
				found=visibleItem;
		}
		return found;
	}

	public Environmental fetchFromRoomFavorMOBs(Item goodLocation, String thingName, int wornReqCode)
	{
		// def was Item.WORN_REQ_UNWORNONLY;
		Environmental found=null;
		Vector V=(Vector)inhabitants.clone();
		V.addAll(contents);
		for(int e=0;e<exits.length;e++)
		    if(exits[e]!=null)V.addElement(exits[e]);
		if(found==null)	found=CMLib.english().fetchAvailable(V,thingName,goodLocation,wornReqCode,true);
		if(found==null) found=CMLib.english().fetchAvailable(V,thingName,goodLocation,wornReqCode,false);
		return found;
	}

	public Environmental fetchFromMOBRoomFavorsItems(MOB mob, Item goodLocation, String thingName, int wornReqCode)
	{
		Environmental found=null;
		boolean mineOnly=(mob!=null)&&(thingName.toUpperCase().trim().startsWith("MY "));
		if(mineOnly) thingName=thingName.trim().substring(3).trim();
		if((mob!=null)&&(wornReqCode!=Item.WORN_REQ_WORNONLY))
			found=mob.fetchCarried(goodLocation, thingName);
		if((found==null)&&(!mineOnly))
		{
				found=fetchFromRoomFavorItems(goodLocation, thingName,wornReqCode);
			if((found!=null)&&(CMLib.flags().canBeSeenBy(found,mob)))
				return found;
			while((found!=null)&&(!CMLib.flags().canBeSeenBy(found,mob)))
			{
				String newThingName=CMLib.english().bumpDotNumber(thingName);
				if(!newThingName.equals(thingName))
				{
					thingName=newThingName;
					found=fetchFromRoomFavorItems(goodLocation, thingName,wornReqCode);
				}
				else
					found=null;
			}
		}
		if((mob!=null)&&(found==null)&&(wornReqCode!=Item.WORN_REQ_UNWORNONLY))
			found=mob.fetchWornItem(thingName);
        if(found==null)
        for(int d=0;d<exits.length;d++)
            if((exits[d]!=null)&&(thingName.equalsIgnoreCase(Directions.getDirectionName(d))))
                return getExitInDir(d);
		return found;
	}

	public int pointsPerMove(MOB mob)
	{	return getArea().getClimateObj().adjustMovement(envStats().weight(),mob,this);	}
	public int thirstPerRound(MOB mob)
	{
		switch(domainConditions())
		{
		case Room.CONDITION_HOT:
			return getArea().getClimateObj().adjustWaterConsumption(baseThirst+1,mob,this);
		case Room.CONDITION_WET:
			return getArea().getClimateObj().adjustWaterConsumption(baseThirst-1,mob,this);
		}
		return getArea().getClimateObj().adjustWaterConsumption(baseThirst,mob,this);
	}
	public int minRange(){return Integer.MIN_VALUE;}
	public int maxRange()
	{
		if(maxRange>=0)
			return maxRange;
		else
		if((domainType&Room.INDOORS)>0)
			return 1;
		else
			return 10;
	}

	public void addEffect(Ability to)
	{
		if(to==null) return;
		if(affects==null) affects=new Vector();
		if(affects.contains(to)) return;
		affects.addElement(to);
		to.setAffectedOne(this);
	}
	public void addNonUninvokableEffect(Ability to)
	{
		if(to==null) return;
		if(affects==null) affects=new Vector();
		if(affects.contains(to)) return;
		to.makeNonUninvokable();
		to.makeLongLasting();
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
			if((B!=null)&&(B.ID().equals(to.ID())))
			   return;
		}
		if(behaviors.size()==0)
			CMLib.threads().startTickDown(this,MudHost.TICK_ROOM_BEHAVIOR,1);
		to.startBehavior(this);
		behaviors.addElement(to);
	}
	public void delBehavior(Behavior to)
	{
		if(behaviors==null) return;
		behaviors.removeElement(to);
		if(behaviors.size()==0)
			CMLib.threads().deleteTick(this,MudHost.TICK_ROOM_BEHAVIOR);
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
    
	private static final String[] CODES={"CLASS","DISPLAY","DESCRIPTION","TEXT"};
	public String[] getStatCodes(){return CODES;}
	protected int getCodeNum(String code){
		for(int i=0;i<CODES.length;i++)
			if(code.equalsIgnoreCase(CODES[i])) return i;
		return -1;
	}
	public String getStat(String code){
		switch(getCodeNum(code))
		{
		case 0: return CMClass.className(this);
		case 1: return displayText();
		case 2: return description();
		case 3: return text();
		}
		return "";
	}
	public void setStat(String code, String val)
	{
		switch(getCodeNum(code))
		{
		case 0: return;
		case 1: setDisplayText(val); break;
		case 2: setDescription(val); break;
		case 3: setMiscText(val); break;
		}
	}
	public boolean sameAs(Environmental E)
	{
		if(!(E instanceof StdRoom)) return false;
		for(int i=0;i<CODES.length;i++)
			if(!E.getStat(CODES[i]).equals(getStat(CODES[i])))
				return false;
		return true;
	}
	public boolean isSameRoom(Object O)
	{
	    if(O==this) return true;
	    if(O instanceof Room)
	    {
	        if(CMLib.map().getExtendedRoomID(this).equals(CMLib.map().getExtendedRoomID((Room)O)))
	            return true;
	    }
	    return false;
	}
}
