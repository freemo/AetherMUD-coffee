package com.planet_ink.coffee_mud.Locales;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;

import java.util.*;
public class StdRoom
	implements Room
{
	public String ID(){return "StdRoom";}
	protected String myID="room#";
	protected String name="the room";
	protected String displayText="Standard Room";
	protected String miscText="";
	protected String description="";
	protected Area myArea=null;
	protected EnvStats envStats=new DefaultEnvStats();
	protected EnvStats baseEnvStats=new DefaultEnvStats();
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

	// base move points and thirst points per round
	protected int baseThirst=1;
	protected int myResource=-1;
	protected long resourceFound=0;

	protected boolean skyedYet=false;
	public StdRoom()
	{
		baseEnvStats.setWeight(2);
		recoverEnvStats();
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
	public Environmental newInstance()
	{
		return new StdRoom();
	}
	public boolean isGeneric(){return false;}
	private void cloneFix(Room E)
	{
		baseEnvStats=E.baseEnvStats().cloneStats();
		envStats=E.envStats().cloneStats();

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
		for(int i=0;i<E.numAffects();i++)
		{
			Ability A=(Ability)E.fetchAffect(i);
			if((A!=null)&&(!A.canBeUninvoked()))
				addAffect((Ability)A.copyOf());
		}
		for(int i=0;i<E.numBehaviors();i++)
		{
			Behavior B=E.fetchBehavior(i);
			if(B!=null)
				addBehavior((Behavior)B.copyOf());
		}
	}
	public Environmental copyOf()
	{
		try
		{
			StdRoom R=(StdRoom)this.clone();
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
		return description;
	}
	public void setDescription(String newDescription)
	{
		description=newDescription;
	}
	public String text()
	{
		return Generic.getPropertiesStr(this,true);
	}
	public void setMiscText(String newMiscText)
	{
		miscText="";
		if(newMiscText.trim().length()>0)
			Generic.setPropertiesStr(this,newMiscText,true);
	}
	public void setRoomID(String newID)
	{
		myID=newID;
	}
	public Area getArea()
	{
		if(myArea==null) return (Area)CMClass.getAreaType("StdArea");
		return myArea;
	}
	public void setArea(Area newArea)
	{
		myArea=newArea;
	}

	public void giveASky(int depth)
	{
		if(skyedYet) return;
		if(depth>1000) return;
		skyedYet=true;
		if((rawDoors()[Directions.UP]==null)
		&&((domainType()&Room.INDOORS)==0)
		&&(domainType()!=Room.DOMAIN_OUTDOORS_UNDERWATER)
		&&(domainType()!=Room.DOMAIN_OUTDOORS_AIR)
		&&(CommonStrings.getIntVar(CommonStrings.SYSTEMI_SKYSIZE)>0))
		{
			Exit o=(Exit)CMClass.getExit("StdOpenDoorway");
			EndlessSky sky=new EndlessSky();
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
				if((thatSky!=null)&&(thatSky.roomID().length()==0)&&(thatSky instanceof EndlessSky))
				{
					sky.rawDoors()[d]=thatSky;
					Exit xo=rawExits()[d];
					if((xo==null)||(xo.hasADoor())) xo=o;
					sky.rawExits()[d]=o;
					thatSky.rawDoors()[Directions.getOpDirectionCode(d)]=sky;
					xo=thatRoom.rawExits()[Directions.getOpDirectionCode(d)];
					if((xo==null)||(xo.hasADoor())) xo=o;
					thatSky.rawExits()[Directions.getOpDirectionCode(d)]=xo;
					((GridLocale)thatSky).clearGrid();
				}
			}
			sky.clearGrid();
			CMMap.addRoom(sky);
		}
	}

	public void clearSky()
	{
		if(!skyedYet) return;
		Room room=rawDoors()[Directions.UP];
		if(room==null) return;
		if((room.roomID().length()==0)&&(room instanceof EndlessSky))
		{
			((EndlessSky)room).clearGrid();
			rawDoors()[Directions.UP]=null;
			rawExits()[Directions.UP]=null;
			room.rawDoors()[Directions.DOWN]=null;
			room.rawExits()[Directions.DOWN]=null;
			CMMap.delRoom(room);
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
			if(resourceFound<(System.currentTimeMillis()-(30*IQCalendar.MILI_MINUTE)))
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
				int theRoll=Dice.roll(1,totalChance,0);
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

	public boolean okAffect(Environmental myHost, Affect affect)
	{
		if(!getArea().okAffect(this,affect))
			return false;

		if(affect.amITarget(this))
		{
			MOB mob=(MOB)affect.source();
			switch(affect.targetMinor())
			{
			case Affect.TYP_LEAVE:
				if((!Sense.canMove(this))||(!getMobility()))
					return false;
				break;
			case Affect.TYP_FLEE:
			case Affect.TYP_ENTER:
				if((!Sense.canMove(this))||(!getMobility()))
					return false;
				if(!mob.isMonster())
					giveASky(0);
				break;
			case Affect.TYP_AREAAFFECT:
				// obsolete with the area objects
				break;
			case Affect.TYP_CAST_SPELL:
			case Affect.TYP_DELICATE_HANDS_ACT:
			case Affect.TYP_OK_ACTION:
			case Affect.TYP_JUSTICE:
			case Affect.TYP_OK_VISUAL:
				break;
			case Affect.TYP_SPEAK:
				break;
			default:
				if((Util.bset(affect.targetMajor(),Affect.MASK_HANDS))
				||(Util.bset(affect.targetMajor(),Affect.MASK_MOUTH)))
				{
					mob.tell("You can't do that here.");
					return false;
				}
				break;
			}
		}

		if(isInhabitant(affect.source()))
			if(!affect.source().okAffect(this,affect))
				return false;
		for(int i=0;i<numInhabitants();i++)
		{
			MOB inhab=fetchInhabitant(i);
			if((inhab!=null)
			&&(inhab!=affect.source())
			&&(!inhab.okAffect(this,affect)))
				return false;
		}
		for(int i=0;i<numItems();i++)
		{
			Item content=fetchItem(i);
			if((content!=null)&&(!content.okAffect(this,affect)))
				return false;
		}
		for(int i=0;i<numAffects();i++)
		{
			Ability A=fetchAffect(i);
			if((A!=null)&&(!A.okAffect(this,affect)))
				return false;
		}
		for(int b=0;b<numBehaviors();b++)
		{
			Behavior B=fetchBehavior(b);
			if((B!=null)&&(!B.okAffect(this,affect)))
				return false;
		}

		for(int i=0;i<Directions.NUM_DIRECTIONS;i++)
		{
			Exit thisExit=rawExits()[i];
			if(thisExit!=null)
				if(!thisExit.okAffect(this,affect))
					return false;
		}
		return true;
	}

	public void affect(Environmental myHost, Affect affect)
	{
		getArea().affect(this,affect);

		if(affect.amITarget(this))
		{
			MOB mob=(MOB)affect.source();
			switch(affect.targetMinor())
			{
			case Affect.TYP_LEAVE:
			{
				recoverRoomStats();
				break;
			}
			case Affect.TYP_FLEE:
			{
				recoverRoomStats();
				break;
			}
			case Affect.TYP_ENTER:
			case Affect.TYP_RECALL:
			{
				recoverRoomStats();
				break;
			}
			case Affect.TYP_EXAMINESOMETHING:
				look(mob,affect.sourceMessage()==null);
				break;
			case Affect.TYP_READSOMETHING:
				if(Sense.canBeSeenBy(this,mob))
					mob.tell("There is nothing written here.");
				else
					mob.tell("You can't see that!");
				break;
			case Affect.TYP_AREAAFFECT:
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
				content.affect(this,affect);
		}

		for(int d=0;d<Directions.NUM_DIRECTIONS;d++)
		{
			Exit thisExit=rawExits()[d];
			if(thisExit!=null)
				thisExit.affect(this,affect);
		}

		for(int b=0;b<numBehaviors();b++)
		{
			Behavior B=fetchBehavior(b);
			if(B!=null)
				B.affect(this,affect);
		}

		for(int a=0;a<numAffects();a++)
		{
			Ability A=fetchAffect(a);
			if(A!=null)
				A.affect(this,affect);
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

	public boolean tick(Tickable ticking, int tickID)
	{
		if(tickID==Host.ROOM_BEHAVIOR_TICK)
		{
			if(numBehaviors()==0) return false;
			for(int b=0;b<numBehaviors();b++)
			{
				Behavior B=fetchBehavior(b);
				if(B!=null) B.tick(ticking,tickID);
			}
		}
		else
		{
			int a=0;
			while(a<numAffects())
			{
				Ability A=fetchAffect(a);
				if(A!=null)
				{
					int s=numAffects();
					if(!A.tick(ticking,tickID))
						A.unInvoke();
					if(numAffects()==s)
						a++;
				}
				else
					a++;
			}
		}
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
		envStats=baseEnvStats.cloneStats();
		Area myArea=getArea();
		if(myArea!=null)
			myArea.affectEnvStats(this,envStats());
		for(int a=0;a<numAffects();a++)
		{
			Ability affect=fetchAffect(a);
			if(affect!=null)
				affect.affectEnvStats(this,envStats);
		}
		for(int i=0;i<numItems();i++)
		{
			Item item=fetchItem(i);
			if(item!=null)
				item.affectEnvStats(this,envStats);
		}
		for(int m=0;m<numInhabitants();m++)
		{
			MOB mob=fetchInhabitant(m);
			if(mob!=null)
				mob.affectEnvStats(this,envStats);
		}
	}
	public void recoverRoomStats()
	{
		recoverEnvStats();
		for(int m=0;m<numInhabitants();m++)
		{
			MOB mob=fetchInhabitant(m);
			if(mob!=null)
			{
				mob.recoverCharStats();
				mob.recoverEnvStats();
				mob.recoverMaxState();
			}
		}
		for(int d=0;d<exits.length;d++)
		{
			Exit X=exits[d];
			if(X!=null) X.recoverEnvStats();
		}
		for(int i=0;i<numItems();i++)
		{
			Item item=fetchItem(i);
			if(item!=null)
				item.recoverEnvStats();
		}
	}

	public void setBaseEnvStats(EnvStats newBaseEnvStats)
	{
		baseEnvStats=newBaseEnvStats.cloneStats();
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
	}
	public void affectCharStats(MOB affectedMob, CharStats affectableStats)
	{
		getArea().affectCharStats(affectedMob,affectableStats);
	}
	public void affectCharState(MOB affectedMob, CharState affectableMaxState)
	{
		getArea().affectCharState(affectedMob,affectableMaxState);
	}
	public int compareTo(Object o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}

	private void look(MOB mob, boolean careAboutBrief)
	{
		StringBuffer Say=new StringBuffer("");
		if(Util.bset(mob.getBitmap(),MOB.ATT_SYSOPMSGS))
		{
			if(myArea!=null)
				Say.append("^!Area  :^N("+myArea.Name()+")"+"\n\r");
			Say.append("^!Locale:^N("+ID()+")"+"\n\r");
			Say.append("^H("+CMMap.getExtendedRoomID(this)+")^N ");
		}
		if((Sense.canBeSeenBy(this,mob))
		   ||(Util.bset(mob.getBitmap(),MOB.ATT_SYSOPMSGS)))
		{
			Say.append("^O" + displayText()+Sense.colorCodes(this,mob)+"^L\n\r");
			if((!careAboutBrief)||(!Util.bset(mob.getBitmap(),MOB.ATT_BRIEF)))
			{
				Say.append("^L" + description());
				if((Util.bset(mob.getBitmap(),MOB.ATT_AUTOWEATHER))
				&&((domainType()&Room.INDOORS)==0))
					Say.append("\n\r\n\r"+getArea().weatherDescription(this));
				Say.append("^N\n\r\n\r");
			}
		}

		Vector viewItems=new Vector();
		for(int c=0;c<numItems();c++)
		{
			Item item=fetchItem(c);
			if((item!=null)&&(item.container()==null))
				viewItems.addElement(item);
		}
		Say.append(ExternalPlay.niceLister(mob,viewItems,false));

		for(int i=0;i<numInhabitants();i++)
		{
			MOB mob2=fetchInhabitant(i);
			if((mob2!=null)
			   &&(mob2!=mob)
			   &&((Sense.canBeSeenBy(mob2,mob)))
			   &&((mob2.displayText(mob).length()>0)
				  ||(Util.bset(mob.getBitmap(),MOB.ATT_SYSOPMSGS))))
			{
				if(Util.bset(mob.getBitmap(),MOB.ATT_SYSOPMSGS))
					Say.append("^H("+CMClass.className(mob2)+")^N ");

				Say.append("^M");
				if(mob2.displayText(mob).length()>0)
					Say.append(mob2.displayText(mob));
				else
					Say.append(mob2.name());
				Say.append(Sense.colorCodes(mob2,mob)+"^N\n\r");
			}
		}

		if(Say.length()==0)
			mob.tell("You can't see anything!");
		else
			mob.tell(Say.toString());
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
			if(mob.riding()!=null)
			{
				if((mob.riding().rideBasis()!=Rideable.RIDEABLE_SIT)
				&&(mob.riding().rideBasis()!=Rideable.RIDEABLE_TABLE)
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
		}
		oldRoom.recoverRoomStats();
		recoverRoomStats();
	}

	public void bringItemHere(Item item, int survivalCode)
	{
		if(item==null) return;

		if(item.owner()==null) return;
		Environmental o=item.owner();

		Vector V=new Vector();
		if(item instanceof Container)
			V=((Container)item).getContents();
		if(o instanceof MOB)((MOB)o).delInventory(item);
		if(o instanceof Room) ((Room)o).delItem(item);
		
		if(survivalCode<0)
			addItem(item);
		else
			addItemRefuse(item,survivalCode);
		for(int v=0;v<V.size();v++)
		{
			Item i2=(Item)V.elementAt(v);
			if(o instanceof MOB) ((MOB)o).delInventory(i2);
			if(o instanceof Room) ((Room)o).delItem(i2);
			addItem(i2);
		}
		item.setContainer(null);
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
		else
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
		if(nextRoom!=null)
		{
			if(nextRoom instanceof GridLocale)
			{
				Room realRoom=((GridLocale)nextRoom).getAltRoomFrom(this);
				if(realRoom!=null) return realRoom;
			}
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
		if(!Sense.canSee(mob))
		{
			mob.tell("You can't see anything!");
			return;
		}

		for(int i=0;i<Directions.NUM_DIRECTIONS;i++)
		{
			Exit exit=getExitInDir(i);
			Room room=getRoomInDir(i);

			String Dir=Directions.getDirectionName(i);
			StringBuffer Say=new StringBuffer("");
			if(exit!=null)
				Say=exit.viewableText(mob, room);
			if(Say.length()>0)
				mob.tell("^D" + Util.padRight(Dir,5)+":^.^N ^d"+Say+"^N^.");
		}
	}

	private void reallyReallySend(MOB source, Affect msg)
	{
		if(Log.debugChannelOn())
			Log.debugOut("StdRoom",((msg.source()!=null)?msg.source().ID():"null")+":"+msg.sourceCode()+":"+msg.sourceMessage()+"/"+((msg.target()!=null)?msg.target().ID():"null")+":"+msg.targetCode()+":"+msg.targetMessage()+"/"+((msg.tool()!=null)?msg.tool().ID():"null")+"/"+msg.othersCode()+":"+msg.othersMessage());
		Vector inhabs=(Vector)inhabitants.clone();
		for(int i=0;i<inhabs.size();i++)
		{
			MOB otherMOB=(MOB)inhabs.elementAt(i);
			if((otherMOB!=null)&&(otherMOB!=source))
				otherMOB.affect(otherMOB,msg);
		}
		affect(source,msg);
	}

	private void reallySend(MOB source, Affect msg)
	{
		reallyReallySend(source,msg);
		// now handle trailer msgs
		if(msg.trailerMsgs()!=null)
		{
			for(int i=0;i<msg.trailerMsgs().size();i++)
			{
				Affect affect=(Affect)msg.trailerMsgs().elementAt(i);
				if((affect!=msg)
				&&((affect.target()==null)
				   ||(!(affect.target() instanceof MOB))
				   ||(!((MOB)affect.target()).amDead()))
				&&(okAffect(source,affect)))
				{
					source.affect(source,affect);
					reallyReallySend(source,affect);
				}
			}
		}
	}

	public void send(MOB source, Affect msg)
	{
		source.affect(source,msg);
		reallySend(source,msg);
	}
	public void sendOthers(MOB source, Affect msg)
	{
		reallySend(source,msg);
	}

	public void showHappens(int allCode, String allMessage)
	{
		MOB everywhereMOB=CMClass.getMOB("StdMOB");
		everywhereMOB.setName("nobody");
		everywhereMOB.setLocation(this);
		FullMsg msg=new FullMsg(everywhereMOB,null,null,allCode,allCode,allCode,allMessage);
		send(everywhereMOB,msg);
	}
	public boolean show(MOB source,
					 Environmental target,
					 int allCode,
					 String allMessage)
	{
		FullMsg msg=new FullMsg(source,target,null,allCode,allCode,allCode,allMessage);
		if((!Util.bset(allCode,Affect.MASK_GENERAL))&&(!okAffect(source,msg)))
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
		FullMsg msg=new FullMsg(source,target,tool,allCode,allCode,allCode,allMessage);
		if((!Util.bset(allCode,Affect.MASK_GENERAL))&&(!okAffect(source,msg)))
			return false;
		send(source,msg);
		return true;
	}
	public boolean showOthers(MOB source,
						   Environmental target,
						   int allCode,
						   String allMessage)
	{
		FullMsg msg=new FullMsg(source,target,null,allCode,allCode,allCode,allMessage);
		if((!Util.bset(allCode,Affect.MASK_GENERAL))&&(!okAffect(source,msg)))
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
		FullMsg msg=new FullMsg(source,target,tool,allCode,allCode,allCode,allMessage);
		if((!Util.bset(allCode,Affect.MASK_GENERAL))&&(!okAffect(source,msg)))
			return false;
		reallySend(source,msg);
		return true;
	}
	public boolean showSource(MOB source,
						   Environmental target,
						   int allCode,
						   String allMessage)
	{
		FullMsg msg=new FullMsg(source,target,null,allCode,allCode,allCode,allMessage);
		if((!Util.bset(allCode,Affect.MASK_GENERAL))&&(!okAffect(source,msg)))
			return false;
		source.affect(source,msg);
		return true;
	}
	public boolean showSource(MOB source,
						   Environmental target,
						   Environmental tool,
						   int allCode,
						   String allMessage)
	{
		FullMsg msg=new FullMsg(source,target,tool,allCode,allCode,allCode,allMessage);
		if((!Util.bset(allCode,Affect.MASK_GENERAL))&&(!okAffect(source,msg)))
			return false;
		source.affect(source,msg);
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
		for(int a=numAffects()-1;a>=0;a--)
			fetchAffect(a).unInvoke();
		}catch(Exception e){}
		while(numAffects()>0)
			delAffect(fetchAffect(0));
		try{
		for(int a=numInhabitants()-1;a>=0;a--)
			fetchInhabitant(a).destroy();
		}catch(Exception e){}
		while(numInhabitants()>0)
			delInhabitant(fetchInhabitant(0));
		while(numBehaviors()>0)
			delBehavior(fetchBehavior(0));
		try{
		for(int a=numItems()-1;a>=0;a--)
			fetchItem(a).destroy();
		}catch(Exception e){}
		while(numItems()>0)
			delItem(fetchItem(0));
		if(this instanceof GridLocale)
			((GridLocale)this).clearGrid();
		clearSky();
		ExternalPlay.deleteTick(this,-1);
	}

	public MOB fetchInhabitant(String inhabitantID)
	{
		MOB mob=(MOB)CoffeeUtensils.fetchEnvironmental(inhabitants,inhabitantID,true);
		if(mob==null)
			mob=(MOB)CoffeeUtensils.fetchEnvironmental(inhabitants,inhabitantID, false);
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
				else
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

	public Item fetchItem(Item goodLocation, String itemID)
	{
		Item item=(Item)CoffeeUtensils.fetchAvailableItem(contents,itemID,goodLocation,Item.WORN_REQ_UNWORNONLY,true);
		if(item==null) item=(Item)CoffeeUtensils.fetchAvailableItem(contents,itemID,goodLocation,Item.WORN_REQ_UNWORNONLY,false);
		return item;
	}
	public void addItem(Item item)
	{
		item.setOwner(this);
		contents.addElement(item);
		item.recoverEnvStats();
	}
	public void addItemRefuse(Item item, int survivalTime)
	{
		addItem(item);
		item.setDispossessionTime(System.currentTimeMillis()+(survivalTime*IQCalendar.MILI_HOUR));
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
	public Environmental fetchFromRoomFavorItems(Item goodLocation, String thingName, int wornReqCode)
	{
		// def was Item.WORN_REQ_UNWORNONLY;
		Environmental found=null;
		if(found==null) found=CoffeeUtensils.fetchAvailableItem(contents,thingName,goodLocation,wornReqCode,true);
		if(found==null)	found=CoffeeUtensils.fetchEnvironmental(exits,thingName,true);
		if(found==null)	found=CoffeeUtensils.fetchEnvironmental(inhabitants,thingName,true);
		if(found==null) found=CoffeeUtensils.fetchAvailableItem(contents,thingName,goodLocation,wornReqCode,false);
		if(found==null)	found=CoffeeUtensils.fetchEnvironmental(exits,thingName,false);
		if(found==null)	found=CoffeeUtensils.fetchEnvironmental(inhabitants,thingName,false);

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
		if(found==null)	found=CoffeeUtensils.fetchEnvironmental(inhabitants,thingName,true);
		if(found==null)	found=CoffeeUtensils.fetchAvailableItem(contents,thingName,goodLocation,wornReqCode,true);
		if(found==null)	found=CoffeeUtensils.fetchEnvironmental(exits,thingName,true);
		if(found==null)	found=CoffeeUtensils.fetchEnvironmental(inhabitants,thingName,false);
		if(found==null) found=CoffeeUtensils.fetchAvailableItem(contents,thingName,goodLocation,wornReqCode,false);
		if(found==null)	found=CoffeeUtensils.fetchEnvironmental(exits,thingName,false);
		return found;
	}

	public Environmental fetchFromMOBRoomFavorsItems(MOB mob, Item goodLocation, String thingName, int wornReqCode)
	{
		Environmental found=null;
		if((mob!=null)&&(wornReqCode!=Item.WORN_REQ_WORNONLY))
			found=mob.fetchCarried(goodLocation, thingName);
		if(found==null)
		{
			found=fetchFromRoomFavorItems(goodLocation, thingName,wornReqCode);
			if((found!=null)&&(Sense.canBeSeenBy(found,mob)))
				return found;
			else
				found=null;
		}
		if((mob!=null)&&(found==null)&&(wornReqCode!=Item.WORN_REQ_UNWORNONLY))
			found=mob.fetchWornItem(thingName);
		return found;
	}

	public int pointsPerMove(MOB mob)
	{	return getArea().adjustMovement(envStats().weight(),mob,this);	}
	public int thirstPerRound(MOB mob)
	{
		switch(domainConditions())
		{
		case Room.CONDITION_HOT:
			return getArea().adjustWaterConsumption(baseThirst+1,mob,this);
		case Room.CONDITION_WET:
			return getArea().adjustWaterConsumption(baseThirst-1,mob,this);
		}
		return getArea().adjustWaterConsumption(baseThirst,mob,this);
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

	public void addAffect(Ability to)
	{
		if(to==null) return;
		if(affects==null) affects=new Vector();
		if(affects.contains(to)) return;
		affects.addElement(to);
		to.setAffectedOne(this);
	}
	public void addNonUninvokableAffect(Ability to)
	{
		if(to==null) return;
		if(affects==null) affects=new Vector();
		if(affects.contains(to)) return;
		to.makeNonUninvokable();
		to.makeLongLasting();
		affects.addElement(to);
		to.setAffectedOne(this);
	}
	public void delAffect(Ability to)
	{
		if(affects==null) return;
		int size=affects.size();
		affects.removeElement(to);
		if(affects.size()<size)
			to.setAffectedOne(null);
	}
	public int numAffects()
	{
		if(affects==null) return 0;
		return affects.size();
	}
	public Ability fetchAffect(int index)
	{
		if(affects==null) return null;
		try
		{
			return (Ability)affects.elementAt(index);
		}
		catch(java.lang.ArrayIndexOutOfBoundsException x){}
		return null;
	}
	public Ability fetchAffect(String ID)
	{
		if(affects==null) return null;
		for(int a=0;a<numAffects();a++)
		{
			Ability A=fetchAffect(a);
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
			ExternalPlay.startTickDown(this,Host.ROOM_BEHAVIOR_TICK,1);
		to.startBehavior(this);
		behaviors.addElement(to);
	}
	public void delBehavior(Behavior to)
	{
		if(behaviors==null) return;
		behaviors.removeElement(to);
		if(behaviors.size()==0)
			ExternalPlay.deleteTick(this,Host.ROOM_BEHAVIOR_TICK);
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
	private int getCodeNum(String code){
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
}
