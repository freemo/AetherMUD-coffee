package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;


public class Song_Strength extends Song
{
	public String ID() { return "Song_Strength"; }
	public String name(){ return "Strength";}
	public String displayText(){ return "(Song of Strength)";}
	public int quality(){ return BENEFICIAL_OTHERS;}
	public Environmental newInstance(){	return new Song_Strength();}
	
	private int amount=0;

	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(invoker==null) return;
		if(affected==invoker)
			affectableStats.setStat(CharStats.STRENGTH,(int)Math.round(affectableStats.getStat(CharStats.STRENGTH)-amount));
		else
			affectableStats.setStat(CharStats.STRENGTH,(int)Math.round(affectableStats.getStat(CharStats.STRENGTH)+amount));
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		amount=Util.s_int(Util.combine(commands,0));

		if(amount<=0)
		{
			mob.tell(mob,null,"Sing about how much strength?");
			return false;
		}

		if(amount>=mob.charStats().getStat(CharStats.STRENGTH))
		{
			mob.tell(mob,null,"You can't sing away that much strength.");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;
		return true;
	}
}
