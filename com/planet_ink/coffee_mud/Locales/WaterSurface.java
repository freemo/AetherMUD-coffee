package com.planet_ink.coffee_mud.Locales;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.utils.*;
import com.planet_ink.coffee_mud.common.*;
import java.util.*;

public class WaterSurface extends StdRoom implements Drink
{
	public String ID(){return "WaterSurface";}
	public WaterSurface()
	{
		super();
		name="the water";
		baseEnvStats.setWeight(2);
		recoverEnvStats();
		domainType=Room.DOMAIN_OUTDOORS_WATERSURFACE;
		domainCondition=Room.CONDITION_WET;
	}
	public Environmental newInstance()
	{
		return new WaterSurface();
	}

	public void giveASky(int depth)
	{
		if(skyedYet) return;
		if(depth>1000) return;
		super.giveASky(depth+1);
		skyedYet=true;
		if((rawDoors()[Directions.DOWN]==null)
		&&((domainType()&Room.INDOORS)==0)
		&&(domainType()!=Room.DOMAIN_OUTDOORS_UNDERWATER)
		&&(domainType()!=Room.DOMAIN_OUTDOORS_AIR))
		{
			Exit o=(Exit)CMClass.getExit("StdOpenDoorway");
			UnderWaterGrid sea=new UnderWaterGrid();
			sea.setArea(getArea());
			sea.setRoomID("");
			rawDoors()[Directions.DOWN]=sea;
			rawExits()[Directions.DOWN]=o;
			sea.rawDoors()[Directions.UP]=this;
			sea.rawExits()[Directions.UP]=o;
			for(int d=0;d<4;d++)
			{
				Room thatRoom=rawDoors()[d];
				Room thatSea=null;
				if((thatRoom!=null)&&(rawExits()[d]!=null))
				{
					thatRoom.giveASky(depth+1);
					thatSea=thatRoom.rawDoors()[Directions.DOWN];
				}
				if((thatSea!=null)&&(thatSea.roomID().length()==0)&&(thatSea instanceof UnderWaterGrid))
				{
					sea.rawDoors()[d]=thatSea;
					sea.rawExits()[d]=rawExits()[d];
					thatSea.rawDoors()[Directions.getOpDirectionCode(d)]=sea;
					Exit xo=thatRoom.rawExits()[Directions.getOpDirectionCode(d)];
					if((xo==null)||(xo.hasADoor())) xo=o;
					thatSea.rawExits()[Directions.getOpDirectionCode(d)]=xo;
					((GridLocale)thatSea).clearGrid();
				}
			}
			sea.clearGrid();
			CMMap.addRoom(sea);
		}
	}

	public void clearSky()
	{
		if(!skyedYet) return;
		super.clearSky();
		Room room=rawDoors()[Directions.DOWN];
		if(room==null) return;
		if((room.roomID().length()==0)&&(room instanceof UnderWaterGrid))
		{
			((UnderWaterGrid)room).clearGrid();
			rawDoors()[Directions.UP]=null;
			rawExits()[Directions.UP]=null;
			room.rawDoors()[Directions.DOWN]=null;
			room.rawExits()[Directions.DOWN]=null;
			CMMap.delRoom(room);
			skyedYet=false;
		}
	}

	public boolean okAffect(Environmental myHost, Affect affect)
	{
		switch(WaterSurface.isOkWaterSurfaceAffect(this,affect))
		{
		case -1: return false;
		case 1: return true;
		}
		return super.okAffect(myHost,affect);
	}
	public static int isOkWaterSurfaceAffect(Room room, Affect affect)
	{
		if(Sense.isSleeping(room))
			return 0;

		if(((affect.targetMinor()==Affect.TYP_LEAVE)
			||(affect.targetMinor()==Affect.TYP_ENTER)
			||(affect.targetMinor()==Affect.TYP_FLEE))
		   &&(affect.amITarget(room))
		   &&(!Sense.isFalling(affect.source()))
		   &&(!Sense.isInFlight(affect.source()))
		   &&(!Sense.isWaterWorthy(affect.source())))
		{
			MOB mob=affect.source();
			boolean hasBoat=false;
			for(int i=0;i<mob.inventorySize();i++)
			{
				Item I=mob.fetchInventory(i);
				if((I!=null)&&(I instanceof Rideable)&&(((Rideable)I).rideBasis()==Rideable.RIDEABLE_WATER))
				{	hasBoat=true; break;}
			}
			if((!Sense.isSwimming(mob))&&(!hasBoat)&&(!Sense.isInFlight(mob)))
			{
				mob.tell("You need to swim or ride a boat that way.");
				return -1;
			}
			else
			if(Sense.isSwimming(mob))
				if(mob.envStats().weight()>Math.round(Util.mul(mob.maxCarry(),0.50)))
				{
					mob.tell("You are too encumbered to swim.");
					return -1;
				}
		}
		else
		if(((affect.sourceMinor()==Affect.TYP_SIT)||(affect.sourceMinor()==Affect.TYP_SLEEP))
		&&((affect.source().riding()==null)||(!Sense.isSwimming(affect.source().riding()))))
		{
			affect.source().tell("You cannot rest here.");
			return -1;
		}
		else
		if(affect.amITarget(room)
		&&(affect.targetMinor()==Affect.TYP_DRINK)
		&&(room instanceof Drink))
		{
			if(((Drink)room).liquidType()==EnvResource.RESOURCE_SALTWATER)
			{
				affect.source().tell("You don't want to be drinking saltwater.");
				return -1;
			}
			return 1;
		}
		return 0;
	}

	public void affect(Environmental myHost, Affect affect)
	{
		super.affect(myHost,affect);
		UnderWater.sinkAffects(this,affect);
	}
	public int thirstQuenched(){return 1000;}
	public int liquidHeld(){return Integer.MAX_VALUE-1000;}
	public int liquidRemaining(){return Integer.MAX_VALUE-1000;}
	public int liquidType(){return EnvResource.RESOURCE_FRESHWATER;}
	public void setLiquidType(int newLiquidType){}
	public void setThirstQuenched(int amount){}
	public void setLiquidHeld(int amount){}
	public void setLiquidRemaining(int amount){}
	public boolean containsDrink(){return true;}
	public Vector resourceChoices(){return UnderWater.roomResources;}
}