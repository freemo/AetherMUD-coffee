package com.planet_ink.coffee_mud.Abilities.Druid;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;


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

public class Chant_FindPlant extends Chant
{
	public String ID() { return "Chant_FindPlant"; }
	public String name(){ return "Find Plant";}
	public String displayText(){return "(Finding "+lookingFor+")";}
	public long flags(){return Ability.FLAG_TRACKING;}
	protected String lookingFor="plants";
	private Vector theTrail=null;
	private int nextDirection=-2;
	public int whatImLookingFor=-1;

	private int[] myMats={EnvResource.MATERIAL_VEGETATION,
						  EnvResource.MATERIAL_WOODEN};
	protected int[] okMaterials(){	return myMats;}
	private int[] myRscs={EnvResource.RESOURCE_COTTON,
						  EnvResource.RESOURCE_HEMP};
	protected int[] okResources(){	return myRscs;}

	private Vector allResources=null;
	protected Vector allOkResources()
	{
		if(allResources==null)
		{
			allResources=new Vector();
			if(okResources()!=null)
				for(int m=0;m<okResources().length;m++)
					if(!allResources.contains(new Integer(okResources()[m])))
					   allResources.addElement(new Integer(okResources()[m]));
			for(int i=0;i<EnvResource.RESOURCE_DESCS.length;i++)
			{
				int cd=EnvResource.RESOURCE_DATA[i][0];
				if(okMaterials()!=null)
					for(int m=0;m<okMaterials().length;m++)
						if((cd&EnvResource.MATERIAL_MASK)==okMaterials()[m])
							if(!allResources.contains(new Integer(cd)))
							   allResources.addElement(new Integer(cd));
			}
		}
		return allResources;
	}

	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if(tickID==MudHost.TICK_MOB)
		{
			if(nextDirection==-999)
				return true;

			if((theTrail==null)
			||(affected == null)
			||(!(affected instanceof MOB)))
				return false;

			MOB mob=(MOB)affected;

			if(nextDirection==999)
			{
				mob.tell(itsHere(mob,mob.location()));
				nextDirection=-2;
				unInvoke();
			}
			else
			if(nextDirection==-1)
			{
				if(itsHere(mob,mob.location()).length()==0)
					mob.tell("The trail fizzles out here.");
				nextDirection=-999;
				unInvoke();
			}
			else
			if(nextDirection>=0)
			{
				mob.tell("Your sense "+lookingFor+" "+Directions.getInDirectionName(nextDirection)+".");
				nextDirection=-2;
			}

		}
		return true;
	}

	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		super.executeMsg(myHost,msg);

		if((affected==null)||(!(affected instanceof MOB)))
			return;

		MOB mob=(MOB)affected;
		if((msg.amISource(mob))
		&&(msg.amITarget(mob.location()))
		&&(CMLib.flags().canBeSeenBy(mob.location(),mob))
		&&(msg.targetMinor()==CMMsg.TYP_LOOK))
			nextDirection=CMLib.tracking().trackNextDirectionFromHere(theTrail,mob.location(),false);
	}

	public String itsHere(MOB mob, Room R)
	{
		if(R==null) return "";
		Room room=R;
		if(room.myResource()==whatImLookingFor)
			return "There seems to be "+lookingFor+" around here.\n\r";
		return "";
	}
	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,"<S-NAME> <S-IS-ARE> already trying to "+name());
			return false;
		}
		Vector V=CMLib.flags().flaggedAffects(mob,Ability.FLAG_TRACKING);
		for(int v=0;v<V.size();v++)	((Ability)V.elementAt(v)).unInvoke();

        if((commands.size()==0)&&(text().length()>0))
            commands.addElement(text());
		if(commands.size()==0)
		{
			mob.tell("Find which "+lookingFor+"?  Use 'CHANT \""+name()+"\" LIST' for a list.");
			return false;
		}
		String s=CMParms.combine(commands,0);
		if(s.equalsIgnoreCase("LIST"))
		{
			StringBuffer msg=new StringBuffer("You may search for any of the following: ");
			for(int i=0;i<allOkResources().size();i++)
				msg.append(EnvResource.RESOURCE_DESCS[((Integer)allOkResources().elementAt(i)).intValue()&EnvResource.RESOURCE_MASK].toLowerCase()+", ");
			mob.tell(msg.substring(0,msg.length()-2));
			return false;
		}
		whatImLookingFor=-1;
		for(int i=0;i<allOkResources().size();i++)
		{
			int c=((Integer)allOkResources().elementAt(i)).intValue();
			String d=EnvResource.RESOURCE_DESCS[c&EnvResource.RESOURCE_MASK];
			if(d.equalsIgnoreCase(s))
			{
				lookingFor=d.toLowerCase();
				whatImLookingFor=c;
				break;
			}
		}
		if(whatImLookingFor<0)
		{
			mob.tell("'"+s+"' cannot be found with this chant.    Use 'CHANT \""+name()+"\" LIST' for a list.");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		String here=itsHere(target,target.location());
		if(here.length()>0)
		{
			target.tell(here);
			return true;
		}

		boolean success=profficiencyCheck(mob,0,auto);

		Vector rooms=new Vector();
		for(int i=0;i<1000;i++)
		{
			Room R=mob.location().getArea().getRandomProperRoom();
			if((itsHere(target,R).length()>0)&&(!rooms.contains(R)))
			{
				rooms.addElement(R);
				break;
			}
		}

		if(rooms.size()<=0)
		for(Enumeration r=mob.location().getArea().getProperMap();r.hasMoreElements();)
		{
			Room R=(Room)r.nextElement();
			if(itsHere(target,R).length()>0)
				rooms.addElement(R);
		}

		if(rooms.size()<=0)
		{
			for(int i=0;i<1000;i++)
			{
				Room R=CMLib.map().getRandomRoom();
				if(CMLib.flags().canAccess(mob,R))
				if((itsHere(target,R).length()>0)&&(!rooms.contains(R)))
				{
					rooms.addElement(R);
					break;
				}
			}
			if(rooms.size()<=0)
			{
			    try
			    {
					for(Enumeration r=CMLib.map().rooms();r.hasMoreElements();)
					{
						Room R=(Room)r.nextElement();
						if(CMLib.flags().canAccess(mob,R))
							if(itsHere(target,R).length()>0)
								rooms.addElement(R);
					}
			    }catch(NoSuchElementException e){}
			}
		}

		if(rooms.size()>0)
			theTrail=CMLib.tracking().findBastardTheBestWay(mob.location(),rooms,false,false,true,true,true,50);

		if((success)&&(theTrail!=null))
		{
			CMMsg msg=CMClass.getMsg(mob,target,this,affectType(auto),auto?"<T-NAME> begin(s) to "+name().toLowerCase()+"s!":"^S<S-NAME> chant(s) for "+lookingFor+".^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				Chant_FindPlant newOne=(Chant_FindPlant)this.copyOf();
				if(target.fetchEffect(newOne.ID())==null)
					target.addEffect(newOne);
				target.recoverEnvStats();
				newOne.nextDirection=CMLib.tracking().trackNextDirectionFromHere(newOne.theTrail,target.location(),false);
			}
		}
		else
			beneficialVisualFizzle(mob,null,"<S-NAME> chant(s), but gain(s) nothing from it.");

		return success;
	}
}
