package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Prop_NarrowLedge extends Property
{
	public String ID() { return "Prop_NarrowLedge"; }
	public String name(){ return "The Narrow Ledge";}
	protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_EXITS;}
	public Environmental newInstance(){	return new Prop_NarrowLedge();}
	
	protected int check=16;
	protected String name="the narrow ledge";
	protected Vector mobsToKill=new Vector();
	
	public String accountForYourself()
	{ return "Very narrow";	}

	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		check=getParmVal(newText,"check",16);
		name=getParmStr(newText,"name","the narrow ledge");
	}
	
	public boolean tick(int tickID)
	{
		if(tickID==Host.SPELL_AFFECT)
		{
			synchronized(mobsToKill)
			{
				ExternalPlay.deleteTick(this,Host.SPELL_AFFECT);
				Vector V=((Vector)mobsToKill.clone());
				mobsToKill.clear();
				for(int v=0;v<V.size();v++)
				{
					MOB mob=(MOB)V.elementAt(v);
					if(mob.location()!=null)
					{
						if((affected instanceof Room)&&(mob.location()!=affected))
							continue;
						
						if((affected instanceof Room)
						&&((((Room)affected).domainType()==Room.DOMAIN_INDOORS_AIR)
						   ||(((Room)affected).domainType()==Room.DOMAIN_OUTDOORS_AIR))
						&&(((Room)affected).getRoomInDir(Directions.DOWN)!=null)
						&&(((Room)affected).getExitInDir(Directions.DOWN)!=null)
						&&(((Room)affected).getExitInDir(Directions.DOWN).isOpen()))
							mob.location().show(mob,null,Affect.MSG_OK_ACTION,"<S-NAME> fall(s) off "+name+"!!");
						else
						{
							mob.location().show(mob,null,Affect.MSG_OK_ACTION,"<S-NAME> fall(s) off "+name+" to <S-HIS-HER> death!!");
							mob.location().show(mob,null,Affect.MSG_DEATH,null);
						}
					}
				}
			}
		}
		return true;
	}
	public void affect(Affect msg)
	{
		if((msg.targetMinor()==Affect.TYP_ENTER)
		&&((msg.amITarget(affected))||(msg.tool()==affected))
		&&(!Sense.isFalling(msg.source())))
		{
			MOB mob=msg.source();
			if((!Sense.isInFlight(mob))
			&&(Dice.roll(1,check,-mob.charStats().getStat(CharStats.DEXTERITY))>0))
			{
				synchronized(mobsToKill)
				{
					if(!mobsToKill.contains(mob))
					{
						mobsToKill.addElement(mob);
						Ability falling=CMClass.getAbility("Falling");
						falling.setProfficiency(0);
						falling.setAffectedOne(msg.target());
						falling.invoke(null,null,mob,true);
						ExternalPlay.startTickDown(this,Host.SPELL_AFFECT,1);
					}
				}
			}
		}
		super.affect(msg);
	}
	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		// always disable flying restrictions!
		affectableStats.setDisposition(affectableStats.disposition()|EnvStats.IS_SLEEPING);
	}
}
