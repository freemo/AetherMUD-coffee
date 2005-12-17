package com.planet_ink.coffee_mud.Libraries;
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
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;


import java.util.*;
import java.io.IOException;

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
public class CommonMsgs extends StdLibrary implements CommonCommands
{
    public String ID(){return "CommonMsgs";}

	public boolean doStandardCommand(MOB mob, String command, Vector parms)
	{
		try
		{
			Command C=CMClass.getCommand(command);
			if(C!=null)
				return C.execute(mob,parms);
		}
		catch(IOException e)
		{
			Log.errOut("CommonMsgs",e);
		}
		return false;
	}

	public StringBuffer getScore(MOB mob)
	{
		Vector V=new Vector();
		doStandardCommand(mob,"Score",V);
		if((V.size()==1)&&(V.firstElement() instanceof StringBuffer))
			return (StringBuffer)V.firstElement();
		return new StringBuffer("");
	}
	public StringBuffer getEquipment(MOB viewer, MOB mob)
	{
		Vector V=new Vector();
		V.addElement(viewer);
		doStandardCommand(mob,"Equipment",V);
		if((V.size()>1)&&(V.elementAt(1) instanceof StringBuffer))
			return (StringBuffer)V.elementAt(1);
		return new StringBuffer("");
	}
	public StringBuffer getInventory(MOB viewer, MOB mob)
	{
		Vector V=new Vector();
		V.addElement(viewer);
		doStandardCommand(mob,"Inventory",V);
		if((V.size()>1)&&(V.elementAt(1) instanceof StringBuffer))
			return (StringBuffer)V.elementAt(1);
		return new StringBuffer("");
	}
	
	public void channel(MOB mob, 
							   String channelName, 
							   String message, 
							   boolean systemMsg)
	{
		doStandardCommand(mob,"Channel",
						  CMParms.makeVector(new Boolean(systemMsg),channelName,message));
	}
	
	public void channel(String channelName, 
							   String clanID, 
							   String message, 
							   boolean systemMsg)
	{
        MOB talker=CMClass.getMOB("StdMOB");
		talker.setName("^?");
		talker.setLocation(CMLib.map().getRandomRoom());
		talker.baseEnvStats().setDisposition(EnvStats.IS_GOLEM);
        talker.envStats().setDisposition(EnvStats.IS_GOLEM);
		talker.setClanID(clanID);
		channel(talker,channelName,message,systemMsg);
        talker.destroy();
	}

	public boolean drop(MOB mob, Environmental dropThis, boolean quiet, boolean optimized)
	{
		return doStandardCommand(mob,"Drop",CMParms.makeVector(dropThis,new Boolean(quiet),new Boolean(optimized)));
	}
	public boolean get(MOB mob, Item container, Item getThis, boolean quiet)
	{
		if(container==null)
			return doStandardCommand(mob,"Get",CMParms.makeVector(getThis,new Boolean(quiet)));
		return doStandardCommand(mob,"Get",CMParms.makeVector(getThis,container,new Boolean(quiet)));
	}
	
	public boolean remove(MOB mob, Item item, boolean quiet)
	{
		if(quiet)
			return doStandardCommand(mob,"Remove",CMParms.makeVector("REMOVE",item,"QUIETLY"));
		return doStandardCommand(mob,"Remove",CMParms.makeVector("REMOVE",item));
	}
	
	public void look(MOB mob, boolean quiet)
	{
		if(quiet)
			doStandardCommand(mob,"Look",CMParms.makeVector("LOOK","UNOBTRUSIVELY"));
		else
			doStandardCommand(mob,"Look",CMParms.makeVector("LOOK"));
	}

	public void flee(MOB mob, String whereTo)
	{
		doStandardCommand(mob,"Flee",CMParms.makeVector("FLEE",whereTo));
	}

	public void sheath(MOB mob, boolean ifPossible)
	{
		if(ifPossible)
			doStandardCommand(mob,"Sheath",CMParms.makeVector("SHEATH","IFPOSSIBLE"));
		else
			doStandardCommand(mob,"Sheath",CMParms.makeVector("SHEATH"));
	}
	
	public void draw(MOB mob, boolean doHold, boolean ifNecessary)
	{
		if(ifNecessary)
		{
			if(doHold)
				doStandardCommand(mob,"Draw",CMParms.makeVector("DRAW","HELD","IFNECESSARY"));
			else
				doStandardCommand(mob,"Draw",CMParms.makeVector("DRAW","IFNECESSARY"));
		}
		else
			doStandardCommand(mob,"Draw",CMParms.makeVector("DRAW"));
	}
	
	public void stand(MOB mob, boolean ifNecessary)
	{
		if(ifNecessary)
			doStandardCommand(mob,"Stand",CMParms.makeVector("STAND","IFNECESSARY"));
		else
			doStandardCommand(mob,"Stand",CMParms.makeVector("STAND"));
	}

	public void follow(MOB follower, MOB leader, boolean quiet)
	{
		if(leader!=null)
		{
			if(quiet)
				doStandardCommand(follower,"Follow",CMParms.makeVector("FOLLOW",leader,"UNOBTRUSIVELY"));
			else
				doStandardCommand(follower,"Follow",CMParms.makeVector("FOLLOW",leader));
		}
		else
		{
			if(quiet)
				doStandardCommand(follower,"Follow",CMParms.makeVector("FOLLOW","SELF","UNOBTRUSIVELY"));
			else
				doStandardCommand(follower,"Follow",CMParms.makeVector("FOLLOW","SELF"));
		}
	}

	public void say(MOB mob,
						   MOB target,
						   String text,
						   boolean isPrivate,
						   boolean tellFlag)
	{
		Room location=mob.location();
		text=CMProps.applyINIFilter(text,CMProps.SYSTEM_SAYFILTER);
		if(target!=null)
			location=target.location();
		if(location==null) return;
		if((isPrivate)&&(target!=null))
		{
			if(tellFlag)
			{
				String targetName=target.name();
				if(targetName.indexOf("@")>=0)
				{
					String mudName=targetName.substring(targetName.indexOf("@")+1);
					targetName=targetName.substring(0,targetName.indexOf("@"));
					if((!(CMLib.intermud().i3online()))&&(!(CMLib.intermud().imc2online())))
						mob.tell("Intermud is unavailable.");
					else
						CMLib.intermud().i3tell(mob,targetName,mudName,text);
				}
				else
				{
					boolean ignore=((target.playerStats()!=null)&&(target.playerStats().getIgnored().contains(mob.Name())));
					CMMsg msg=CMClass.getMsg(mob,target,null,CMMsg.MSG_TELL,"^t^<TELL \""+target.name()+"\"^>You tell "+target.name()+" '"+text+"'^</TELL^>^?^.",CMMsg.MSG_TELL,"^t^<TELL \""+mob.Name()+"\"^>"+mob.Name()+" tell(s) you '"+text+"'^</TELL^>^?^.",CMMsg.NO_EFFECT,null);
					if((mob.location().okMessage(mob,msg))
					&&((ignore)||(target.okMessage(target,msg))))
					{
						mob.executeMsg(mob,msg);
						if((mob!=target)&&(!ignore))
						{
							target.executeMsg(target,msg);
							if(msg.trailerMsgs()!=null)
							{
								for(int i=0;i<msg.trailerMsgs().size();i++)
								{
									CMMsg msg2=(CMMsg)msg.trailerMsgs().elementAt(i);
									if((msg!=msg2)&&(target.okMessage(target,msg2)))
										target.executeMsg(target,msg2);
								}
								msg.trailerMsgs().clear();
							}
							if(mob.playerStats()!=null)
							{
								mob.playerStats().setReplyTo(target);
								mob.playerStats().addTellStack(msg.sourceMessage());
							}
							if(target.playerStats()!=null)
							{
								target.playerStats().setReplyTo(mob);
								target.playerStats().addTellStack(msg.targetMessage());
							}
						}
					}
				}
			}
			else
			{
				CMMsg msg=CMClass.getMsg(mob,target,null,CMMsg.MSG_SPEAK,"^T^<SAY \""+((target==null)?mob.name():target.name())+"\"^><S-NAME> say(s) '"+text+"'"+((target==null)?"^</SAY^>":" to <T-NAMESELF>.^</SAY^>^?"),CMMsg.MSG_SPEAK,"^T^<SAY \""+mob.name()+"\"^><S-NAME> say(s) '"+text+"'"+((target==null)?"^</SAY^>":" to <T-NAMESELF>.^</SAY^>^?"),CMMsg.NO_EFFECT,null);
				if(location.okMessage(mob,msg))
					location.send(mob,msg);
			}
		}
		else
		if(!isPrivate)
		{
		    String str="<S-NAME> say(s) '"+text+"'"+((target==null)?"^</SAY^>":" to <T-NAMESELF>.^</SAY^>^?");
			CMMsg msg=CMClass.getMsg(mob,target,null,CMMsg.MSG_SPEAK,"^T^<SAY \""+((target==null)?mob.name():target.name())+"\"^>"+str,"^T^<SAY \""+mob.name()+"\"^>"+str,"^T^<SAY \""+mob.name()+"\"^>"+str);
			if(location.okMessage(mob,msg))
				location.send(mob,msg);
		}
	}
    
}
