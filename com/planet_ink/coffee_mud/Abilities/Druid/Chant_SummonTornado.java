package com.planet_ink.coffee_mud.Abilities.Druid;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
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

public class Chant_SummonTornado extends Chant
{
	public String ID() { return "Chant_SummonTornado"; }
	public String name(){return renderedMundane?"tornado":"Summon Tornado";}
	public String displayText(){return "(Inside a Tornado)";}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return 0;}
	public int quality(){return Ability.MALICIOUS;}
	public long flags(){return Ability.FLAG_MOVING|Ability.FLAG_WEATHERAFFECTING;}

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|EnvStats.IS_FLYING);
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{
		if(((mob.location().domainType()&Room.INDOORS)>0)&&(!auto))
		{
			mob.tell("You must be outdoors for this chant to work.");
			return false;
		}
		if((mob.location().getArea().getClimateObj().weatherType(mob.location())!=Climate.WEATHER_THUNDERSTORM)
		&&(mob.location().getArea().getClimateObj().weatherType(mob.location())!=Climate.WEATHER_WINDY)
        &&(!auto))
		{
			mob.tell("This chant requires a thunderstorm!");
			return false;
		}

		Environmental target = mob.location();

		if(target.fetchEffect(this.ID())!=null)
		{
		    mob.tell(mob,null,null,"A tornado is already here!");
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		boolean success=profficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.

			FullMsg msg = new FullMsg(mob, null, this, affectType(auto), (auto?"A":"^S<S-NAME> chant(s) to the sky and a")+" tornado touches down!^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				Vector stuff=new Vector();
				for(int i=0;i<mob.location().numItems();i++)
				{
					Item I=mob.location().fetchItem(i);
					if((I!=null)&&(I.container()==null)&&(Sense.isGettable(I)))
						stuff.addElement(I);
				}
				HashSet H=properTargets(mob,givenTarget,true);
				if(H!=null)
				for(Iterator e=H.iterator();e.hasNext();)
					stuff.addElement(e.next());
				Vector availableRooms=new Vector();
				availableRooms.addElement(mob.location());
				for(int d=0;d<Directions.NUM_DIRECTIONS;d++)
				{
					Room R=mob.location().getRoomInDir(d);
					Exit E=mob.location().getExitInDir(d);
					if((R!=null)&&(E!=null)&&(E.isOpen())
					&&((R.domainType()&Room.INDOORS)==0))
						availableRooms.addElement(R);
				}
				if(stuff.size()==0)
					mob.location().showHappens(CMMsg.MSG_OK_ACTION,"The tornado dissipates harmlessly.");
				else
				while(stuff.size()>0)
				{
					Object O=stuff.elementAt(Dice.roll(1,stuff.size(),-1));
					stuff.removeElement(O);
					Room R=(Room)availableRooms.elementAt(Dice.roll(1,availableRooms.size(),-1));
					if(O instanceof Item)
					{
						Item I=(Item)O;
						if(R==mob.location())
							mob.location().show(mob,null,I,CMMsg.MSG_OK_ACTION,"The tornado picks up <O-NAME> and whisks it around.");
						else
						{
							mob.location().show(mob,null,I,CMMsg.MSG_OK_ACTION,"The tornado picks up <O-NAME> and whisks it away.");
							R.bringItemHere(I,-1);
						}
						if(I.subjectToWearAndTear())
						{
							switch(I.material()&EnvResource.MATERIAL_MASK)
							{
							case EnvResource.MATERIAL_PRECIOUS:
							case EnvResource.MATERIAL_ROCK:
							case EnvResource.MATERIAL_MITHRIL:
								I.setUsesRemaining(I.usesRemaining()-1);
								break;
							case EnvResource.MATERIAL_LIQUID:
							case EnvResource.MATERIAL_UNKNOWN:
								break;
							case EnvResource.MATERIAL_GLASS:
								I.setUsesRemaining(I.usesRemaining()-75);
								break;
							case EnvResource.MATERIAL_CLOTH:
							case EnvResource.MATERIAL_FLESH:
							case EnvResource.MATERIAL_LEATHER:
							case EnvResource.MATERIAL_PAPER:
							case EnvResource.MATERIAL_VEGETATION:
							case EnvResource.MATERIAL_WOODEN:
							case EnvResource.MATERIAL_PLASTIC:
								I.setUsesRemaining(I.usesRemaining()-50);
								break;
							case EnvResource.MATERIAL_METAL:
								I.setUsesRemaining(I.usesRemaining()-20);
								break;
							case EnvResource.MATERIAL_ENERGY:
								break;
							}
							if(I.usesRemaining()<=0)
							{
								mob.location().showHappens(CMMsg.MSG_OK_VISUAL,I.name()+" is destroyed!");
								I.destroy();
							}
						}
					}
					else
					if(O instanceof MOB)
					{
						MOB M=(MOB)O;
						msg=new FullMsg(M,mob.location(),null,CMMsg.MSG_LEAVE|CMMsg.MASK_GENERAL,CMMsg.MSG_LEAVE,CMMsg.NO_EFFECT,null);
						FullMsg msg2=new FullMsg(mob,M,this,affectType(auto),null);
						FullMsg msg3=new FullMsg(mob,M,this,verbalCastMask(auto)|CMMsg.TYP_JUSTICE,null);
						if((mob.location().okMessage(M,msg))
						&&(mob.location().okMessage(mob,msg2))
						&&(mob.location().okMessage(mob,msg3)))
						{
							mob.location().send(mob,msg2);
							mob.location().send(mob,msg3);
							if(R==mob.location())
								mob.location().show(M,null,null,CMMsg.MSG_OK_ACTION,"The tornado picks <S-NAME> up and whisks <S-HIM-HER> around.");
							else
							{
								mob.location().show(M,null,null,CMMsg.MSG_OK_ACTION,"The tornado picks <S-NAME> up and whisks <S-HIM-HER> away.");
								R.bringMobHere(M,false);
							}
							int maxDie=(int)Math.round(Util.div(adjustedLevel(mob,asLevel),2.0));
							int damage = Dice.roll(maxDie,7,1);
							if((msg.value()>0)||(msg2.value()>0))
								damage = (int)Math.round(Util.div(damage,2.0));
							MUDFight.postDamage(mob,M,this,damage,CMMsg.MASK_GENERAL|CMMsg.TYP_WEAPONATTACK,Weapon.TYPE_BASHING,"The tornado <DAMAGE> <T-NAME>!");
							//if(R!=mob.location()) M.tell("Wait a minute! Where are you?");
						}
					}
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,null,"<S-NAME> chant(s) into the sky, but nothing happens.");

		// return whether it worked
		return success;
	}
}
