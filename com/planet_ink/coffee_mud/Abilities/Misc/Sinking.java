package com.planet_ink.coffee_mud.Abilities.Misc;
import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Sinking extends StdAbility
{
	public String ID() { return "Sinking"; }
	public String name(){ return "Sinking";}
	public String displayText(){ return "";}
	protected int canAffectCode(){return CAN_ITEMS|Ability.CAN_MOBS;}
	protected int canTargetCode(){return 0;}
	public Room room=null;
	public Environmental newInstance(){	return new Sinking();}

	private boolean reversed(){return profficiency()==100;}

	private boolean isWaterSurface(Room R)
	{
		if(R==null) return false;
		if((R.domainType()==Room.DOMAIN_INDOORS_WATERSURFACE)
		||(R.domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE))
			return true;
		return false;
	}
	private boolean isUnderWater(Room R)
	{
		if(R==null) return false;
		if((R.domainType()==Room.DOMAIN_INDOORS_UNDERWATER)
		||(R.domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER))
			return true;
		return false;
	}

	private boolean isAirRoom(Room R)
	{
		if(R==null) return false;
		if((R.domainType()==Room.DOMAIN_INDOORS_AIR)
		||(R.domainType()==Room.DOMAIN_OUTDOORS_AIR))
			return true;
		return false;
	}

	private boolean canSinkFrom(Room fromHere, int direction)
	{
		if((fromHere==null)||(direction<0)||(direction>=Directions.NUM_DIRECTIONS))
			return false;

		Room toHere=fromHere.getRoomInDir(direction);
		if((toHere==null)
		||(fromHere.getExitInDir(direction)==null)
		||(!fromHere.getExitInDir(direction).isOpen()))
			return false;
		if((!isWaterSurface(toHere))&&(!isUnderWater(toHere)))
			return false;
		return true;
	}

	private boolean stopSinking(MOB mob)
	{
		unInvoke();
		mob.delEffect(this);
		return false;
	}

	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		if(tickID!=Host.TICK_MOB)
			return true;

		if(affected==null)
			return false;

		int direction=Directions.DOWN;
		String addStr="down";
		if(reversed())
		{
			direction=Directions.UP;
			addStr="upwards";
		}
		if(affected instanceof MOB)
		{
			MOB mob=(MOB)affected;
			if(mob==null) return false;
			if(mob.location()==null) return false;

			if(isUnderWater(mob.location())
			||(Sense.isWaterWorthy(mob))
			||(Sense.isInFlight(mob))
			||(mob.envStats().weight()<1))
				return stopSinking(mob);
			else
			if(!canSinkFrom(mob.location(),direction))
				return stopSinking(mob);
			else
			{
				Ability A=mob.fetchAbility("Skill_Swim");
				if(((direction==Directions.DOWN)&&(A!=null))
				&&(A.profficiencyCheck(25,(A.profficiency()>=75))
				&&(mob.curState().getMovement()>0)))
				{
					if(mob.location().show(mob,null,CMMsg.MSG_NOISYMOVEMENT,"<S-NAME> tread(s) water."))
					{
						mob.curState().expendEnergy(mob,mob.maxState(),true);
						return true;
					}
				}
				mob.tell("\n\r\n\rYOU ARE SINKING "+addStr.toUpperCase()+"!!\n\r\n\r");
				ExternalPlay.move(mob,direction,false,false);
				if(!canSinkFrom(mob.location(),direction))
				{
					return stopSinking(mob);
				}
				else
				{
					return true;
				}
			}
		}
		else
		if(affected instanceof Item)
		{
			Item item=(Item)affected;
			if((room==null)
			   &&(item.owner()!=null)
			   &&(item.owner() instanceof Room))
				room=(Room)item.owner();

			if((room==null)
			||((room!=null)&&(!room.isContent(item)))
			||(!item.isGettable())
			||Sense.isInFlight(item.ultimateContainer())
			||(Sense.isWaterWorthy(item.ultimateContainer()))
			||(item.envStats().weight()<1))
			{
				unInvoke();
				return false;
			}
			else
			{
				Room nextRoom=room.getRoomInDir(direction);
				if(canSinkFrom(room,direction))
				{
					room.show(invoker,null,item,CMMsg.MSG_OK_ACTION,"<O-NAME> sinks "+addStr+".");
					Vector V=new Vector();
					recursiveRoomItems(V,item,room);
					for(int v=0;v<V.size();v++)
					{
						Item thisItem=(Item)V.elementAt(v);
						room.delItem(thisItem);
						nextRoom.addItemRefuse(thisItem,Item.REFUSE_PLAYER_DROP);
					}
					room=nextRoom;
					nextRoom.show(invoker,null,item,CMMsg.MSG_OK_ACTION,"<O-NAME> sinks in from "+(reversed()?"below":"above")+".");
					return true;
				}
				else
				{
					if(reversed())
						return true;
					unInvoke();
					return false;
				}
			}

		}

		return false;
	}

	public void recursiveRoomItems(Vector V, Item item, Room room)
	{
		V.addElement(item);
		for(int i=0;i<room.numItems();i++)
		{
			Item newItem=room.fetchItem(i);
			if((newItem!=null)&&(newItem.container()==item))
				recursiveRoomItems(V,newItem,room);
		}
	}

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		if((!Sense.isWaterWorthy(affected))
		&&(!Sense.isInFlight(affected))
		&&(affected.envStats().weight()>=1))
			affectableStats.setDisposition(affectableStats.disposition()|EnvStats.IS_FALLING);
	}
	public void setAffectedOne(Environmental being)
	{
		if(being instanceof Room)
			room=(Room)being;
		else
			super.setAffectedOne(being);
	}
	public boolean invoke(MOB mob, Vector commands, Environmental target, boolean auto)
	{
		if(!auto) return false;
		Environmental E=target;
		if(E==null) return false;
		if((E instanceof Item)&&(room==null)) return false;
		if(E.fetchEffect("Sinking")==null)
		{
			Sinking F=new Sinking();
			F.setProfficiency(profficiency());
			F.invoker=null;
			if(E instanceof MOB)
				F.invoker=(MOB)E;
			else
				F.invoker=CMClass.getMOB("StdMOB");
			E.addEffect(F);
			F.setBorrowed(E,true);
			F.makeLongLasting();
			if(!(E instanceof MOB))
				ExternalPlay.startTickDown(F,Host.TICK_MOB,1);
			E.recoverEnvStats();
		}
		return true;
	}
}
