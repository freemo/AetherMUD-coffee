package com.planet_ink.coffee_mud.Abilities.Druid;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Chant_SummonFire extends Chant
{
	public String ID() { return "Chant_SummonFire"; }
	public String name(){ return "Summon Fire";}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return 0;}
	private Room FireLocation=null;
	private Item littleFire=null;
	public Environmental newInstance(){	return new Chant_SummonFire();}
	public long flags(){return Ability.FLAG_HEATING|Ability.FLAG_BURNING;}

	public void unInvoke()
	{
		if(FireLocation==null)
			return;
		if(littleFire==null)
			return;
		if(canBeUninvoked())
			FireLocation.showHappens(CMMsg.MSG_OK_VISUAL,"The little magical fire goes out.");
		super.unInvoke();
		if(canBeUninvoked())
		{
			Item fire=littleFire; // protects against uninvoke loops!
			littleFire=null;
			fire.destroy();
			FireLocation.recoverRoomStats();
			FireLocation=null;
		}
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		if((((mob.location().domainType()&Room.INDOORS)>0))&&(!auto))
		{
			mob.tell("You must be outdoors for this chant to work.");
			return false;
		}
		if(((mob.location().domainType()==Room.DOMAIN_OUTDOORS_CITY)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_AIR)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE))
		   &&(!auto))
		{
			mob.tell("This magic will not work here.");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		// now see if it worked
		boolean success=profficiencyCheck(0,auto);
		if(success)
		{
			FullMsg msg=new FullMsg(mob,null,this,affectType(auto),auto?"":"^S<S-NAME> chant(s) for fire.^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				Item I=CMClass.getItem("GenItem");
				I.baseEnvStats().setWeight(50);
				I.setName("a magical campfire");
				I.setDisplayText("A roaring magical campfire has been built here.");
				I.setDescription("It consists of magically burning flames, consuming no fuel.");
				I.recoverEnvStats();
				I.setMaterial(EnvResource.RESOURCE_NOTHING);
				I.setMiscText(I.text());
				Ability B=CMClass.getAbility("Burning");
				I.addNonUninvokableEffect(B);

				mob.location().addItem(I);
				mob.location().showHappens(CMMsg.MSG_OK_ACTION,"Suddenly, a little magical campfire begins burning here.");
				FireLocation=mob.location();
				littleFire=I;
				beneficialAffect(mob,I,0);
				mob.location().recoverEnvStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,null,"<S-NAME> chant(s) for fire, but nothing happens.");

		// return whether it worked
		return success;
	}
}