package com.planet_ink.coffee_mud.Races;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class MindFlayer extends Humanoid
{
	public String ID(){	return "MindFlayer"; }
	public String name(){ return "MindFlayer"; }
	protected static Vector resources=new Vector();
	public boolean playerSelectable(){return false;}
	public String racialCategory(){return "Illithid";}
	private String[]culturalAbilityNames={"Spell_MindFog","Spell_Charm"};
	private int[]culturalAbilityProfficiencies={100,50};
	public String[] culturalAbilityNames(){return culturalAbilityNames;}
	public int[] culturalAbilityProfficiencies(){return culturalAbilityProfficiencies;}
    private String[]racialAbilityNames={"Spell_DetectSentience","Spell_CombatPrecognition"};
	private int[]racialAbilityLevels={10,30};
	private int[]racialAbilityProfficiencies={50,30};
	private boolean[]racialAbilityQuals={true,false};
	public String[] racialAbilityNames(){return racialAbilityNames;}
	public int[] racialAbilityLevels(){return racialAbilityLevels;}
	public int[] racialAbilityProfficiencies(){return racialAbilityProfficiencies;}
	public boolean[] racialAbilityQuals(){return racialAbilityQuals;}

	//                                an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={0 ,2 ,2 ,1 ,1 ,2 ,2 ,1 ,2 ,2 ,1 ,0 ,1 ,1 ,0 ,0 };
	public int[] bodyMask(){return parts;}
	
	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()|EnvStats.CAN_SEE_DARK);
	}
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.SAVE_MAGIC,affectableStats.getStat(CharStats.SAVE_MAGIC)+50);
		affectableStats.setStat(CharStats.SAVE_MIND,affectableStats.getStat(CharStats.SAVE_MIND)+100);
		affectableStats.setStat(CharStats.STRENGTH,affectableStats.getStat(CharStats.STRENGTH)-5);
		affectableStats.setStat(CharStats.CONSTITUTION,affectableStats.getStat(CharStats.CONSTITUTION)-5);
		affectableStats.setStat(CharStats.INTELLIGENCE,affectableStats.getStat(CharStats.INTELLIGENCE)+5);
	}
	public Vector myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources=super.myResources();
				resources.addElement(makeResource
				("a "+name().toLowerCase()+" tenticle",EnvResource.RESOURCE_MEAT));
			}
		}
		return resources;
	}
}
