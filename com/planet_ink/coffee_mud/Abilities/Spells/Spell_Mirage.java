package com.planet_ink.coffee_mud.Abilities.Spells;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Spell_Mirage extends Spell
{
	public String ID() { return "Spell_Mirage"; }
	public String name(){return "Mirage";}
	public String displayText(){return "(Mirage spell)";}
	protected int canAffectCode(){return CAN_ROOMS;}
	protected int canTargetCode(){return CAN_ROOMS;}
	public Environmental newInstance(){	return new Spell_Mirage();}
	public int classificationCode(){ return Ability.SPELL|Ability.DOMAIN_ILLUSION;}

	Room newRoom=null;

	public void unInvoke()
	{
		// undo the affects of this spell
		if(affected==null)
			return;
		if(!(affected instanceof Room))
			return;
		Room room=(Room)affected;
		if(canBeUninvoked())
			room.showHappens(Affect.MSG_OK_VISUAL, "The appearance of this place changes...");
		super.unInvoke();
	}

	private Room room()
	{
		if(newRoom==null)
		{
			newRoom=CMMap.getRoom(text());
			if(newRoom==null)
			{
				if(!(affected instanceof Room))
					return null;
				newRoom=((Room)affected).getArea().getRandomRoom();
			}
		}
		return newRoom;
	}
	
	
	public boolean okAffect(Environmental myHost, Affect affect)
	{
		if((affected!=null)
		&&(affected instanceof Room)
		&&(affect.amITarget(affected))
		&&(room().fetchAffect(ID())==null)
		&&(affect.targetMinor()==Affect.TYP_EXAMINESOMETHING))
		{
			Affect msg=new FullMsg(affect.source(),room(),affect.tool(),
						  affect.sourceCode(),affect.sourceMessage(),
						  affect.targetCode(),affect.targetMessage(),
						  affect.othersCode(),affect.othersMessage());
			if(room().okAffect(affect.source(),msg))
			{
				room().affect(affect.source(),msg);
				return false;
			}
		}
		return super.okAffect(myHost,affect);
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		if(mob.location().getArea().mapSize()<2)
		{
			mob.tell("This area is too small to cast this spell.");
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		Environmental target = mob.location();
		boolean success=profficiencyCheck(0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.

			FullMsg msg = new FullMsg(mob, target, this, affectType(auto), auto?"":"^S<S-NAME> speak(s) and gesture(s) dramatically!^?");
			if(mob.location().okAffect(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().showHappens(Affect.MSG_OK_VISUAL,"The appearance of this place changes...");
				if((ExternalPlay.doesOwnThisProperty(mob,mob.location()))
				||((mob.amFollowing()!=null)&&(ExternalPlay.doesOwnThisProperty(mob.amFollowing(),mob.location()))))
				{
					Ability A=(Ability)copyOf();
					A.setInvoker(mob);
					newRoom=((Room)affected).getArea().getRandomRoom();
					if((newRoom!=null)&&(newRoom.roomID().length()>0)&&(!(newRoom instanceof GridLocale)))
					{
						A.setMiscText(CMMap.getExtendedRoomID(newRoom));
						mob.location().addNonUninvokableAffect(A);
					}
				}
				else
					beneficialAffect(mob,mob.location(),0);
			}
		}
		else
			return beneficialWordsFizzle(mob,null,"<S-NAME> speak(s) and gesture(s) dramatically, but the spell fizzles.");

		// return whether it worked
		return success;
	}
}
