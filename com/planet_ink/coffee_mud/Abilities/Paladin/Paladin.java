package com.planet_ink.coffee_mud.Abilities.Paladin;
import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Paladin extends StdAbility
{
	public String ID() { return "Paladin"; }
	public String name(){ return "Paladin's Amazement";}
	public String displayText(){return "";}
	public int quality(){return Ability.OK_OTHERS;}
	public boolean isAutoInvoked(){return true;}
	public boolean canBeUninvoked(){return false;}
	protected int canAffectCode(){return Ability.CAN_MOBS;}
	protected int canTargetCode(){return 0;}
	protected Vector paladinsGroup=null;
	public Environmental newInstance(){	return new Paladin();}
	public int classificationCode(){ return Ability.SKILL;}

	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if((affected==null)||(!(affected instanceof MOB)))
			return false;
		if(invoker==null) invoker=(MOB)affected;
		if(invoker.getAlignment()<650)
			return false;
		if(paladinsGroup!=null)
		{
			HashSet H=((MOB)affected).getGroupMembers(new HashSet());
			for(Iterator e=H.iterator();e.hasNext();)
			{
				MOB mob=(MOB)e.next();
				if(!paladinsGroup.contains(mob))
					paladinsGroup.addElement(mob);
			}
			for(int i=paladinsGroup.size()-1;i>=0;i--)
			{
				try
				{
					MOB mob=(MOB)paladinsGroup.elementAt(i);
					if((!H.contains(mob))
					||(mob.location()!=invoker.location()))
						paladinsGroup.removeElement(mob);
				}
				catch(java.lang.ArrayIndexOutOfBoundsException e)
				{
				}
			}
		}
		if(Dice.rollPercentage()==1) 
			helpProfficiency(invoker);
		return true;
	}
	
	public boolean autoInvocation(MOB mob)
	{
		if(mob.charStats().getCurrentClass().ID().equals("Archon"))
			return false;
		return super.autoInvocation(mob);
	}
}
