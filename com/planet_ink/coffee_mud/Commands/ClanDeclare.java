package com.planet_ink.coffee_mud.Commands;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class ClanDeclare extends BaseClanner
{
	public ClanDeclare(){}

	private String[] access={"CLANDECLARE"};
	public String[] getAccessWords(){return access;}
	public boolean execute(MOB mob, Vector commands)
		throws java.io.IOException
	{
		boolean skipChecks=mob.Name().equals(mob.getClanID());

		commands.setElementAt("clandeclare",0);
		if(commands.size()<3)
		{
			mob.tell("You must specify the clans name, and a new relationship.");
			return false;
		}
		String rel=((String)commands.lastElement()).toUpperCase();
		commands.removeElementAt(commands.size()-1);
		String clan=Util.combine(commands,1).toUpperCase();
		commands.addElement(rel);
		StringBuffer msg=new StringBuffer("");
		Clan C=null;
		if((clan.length()>0)&&(rel.length()>0))
		{
			if((mob.getClanID()==null)||(mob.getClanID().equalsIgnoreCase("")))
			{
				msg.append("You aren't even a member of a clan.");
			}
			else
			{
				C=Clans.getClan(mob.getClanID());
				if(C==null)
				{
					mob.tell("There is no longer a clan called "+mob.getClanID()+".");
					return false;
				}
				if(skipChecks||goForward(mob,C,commands,Clan.FUNC_CLANDECLARE,false))
				{
					int newRole=-1;
					for(int i=0;i<Clan.REL_DESCS.length;i++)
						if(rel.equalsIgnoreCase(Clan.REL_DESCS[i]))
							newRole=i;
					if(newRole<0)
					{
						mob.tell("'"+rel+"' is not a valid relationship. Try WAR, HOSTILE, NEUTRAL, FRIENDLY, or ALLY.");
						return false;
					}
					Clan C2=Clans.getClan(clan);
					if(C2==null)
					{
						mob.tell(clan+" isn't valid clan.");
						return false;
					}
					if(C2==C)
					{
						mob.tell("You can't do that.");
						return false;
					}
					if(C.getClanRelations(C2.ID())==newRole)
					{
						mob.tell("You are already in that state with "+C2.ID()+".");
						return false;

					}
					long last=C.getLastRelationChange(C2.ID());
					if(last>(MudHost.TICKS_PER_MUDDAY*MudHost.TICK_TIME))
					{
						last=last+(MudHost.TICKS_PER_MUDDAY*MudHost.TICK_TIME);
						if(System.currentTimeMillis()<last)
						{
							mob.tell("You must wait at least 1 mud day between relation changes.");
							return false;
						}
					}
					if(skipChecks||goForward(mob,C,commands,Clan.FUNC_CLANDECLARE,true))
					{
						clanAnnounce(mob,"Your "+C.typeName()+" has declared "+Util.capitalize(Clan.REL_STATES[newRole].toLowerCase())+" "+C2.name()+".");
						C.setClanRelations(C2.ID(),newRole);
						C.update();
						return false;
					}
				}
				else
				{
					msg.append("You aren't in the right position to declare relationships with your "+C.typeName()+".");
				}
			}
		}
		else
		{
			mob.tell("You must specify the clans name, and a new relationship.");
			return false;
		}
		mob.tell(msg.toString());
		return false;
	}
	public int ticksToExecute(){return 0;}
	public boolean canBeOrdered(){return false;}

	public int compareTo(Object o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}
}
