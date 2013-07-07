package com.planet_ink.coffee_mud.Items.Software;
import com.planet_ink.coffee_mud.Items.Basic.StdItem;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
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

import java.net.*;
import java.io.*;
import java.util.*;

/* 
   Copyright 2000-2013 Bo Zimmerman

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
public class StdTelnetProgram extends StdProgram
{
	public String ID(){	return "StdTelnetProgram";}
	
	protected Socket sock = null;
	protected BufferedReader reader=null;
	protected BufferedWriter writer=null;
	
	public StdTelnetProgram()
	{
		super();
		setName("a telnet disk");
		setDisplayText("a small computer disk with sits here.");
		setDescription("It appears to be a telnet computer program.");

		material=RawMaterial.RESOURCE_STEEL;
		baseGoldValue=1000;
		recoverPhyStats();
	}
	
	public String getParentMenu() { return ""; }
	
	public String getInternalName() { return "TELNET";}
	
	public boolean isActivationString(String word, boolean activeState)
	{
		if(!activeState)
		{
			word=word.toUpperCase();
			return (word.startsWith("TELNET ")||word.equals("TELNET"));
		}
		else
		{
			return true;
		}
	}

	public String getActivationMenu()
	{
		return "TELNET <HOST> <PORT>: Telnet Network Software";
	}

	protected void shutdown()
	{
		currentScreen="";
		synchronized(this)
		{
			try
			{
				try
				{
					if(sock!=null)
					{
						sock.shutdownInput();
						sock.shutdownOutput();
					}
					if(reader!=null)
						reader.close();
					if(writer!=null)
						writer.close();
				}
				catch(Exception e) {}
				finally
				{
					sock.close();
				}
			}
			catch(Exception e) {}
			finally
			{
				sock=null;
				reader=null;
				writer=null;
			}
		}
	}
	
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_ACTIVATE:
			{
				List<String> parsed=CMParms.parse(msg.targetMessage());
				if(parsed.size()!=3)
				{
					msg.source().tell("Incorrect usage, try: TELNET <HOSTNAME> <PORT>");
					return false;
				}
				try
				{
					shutdown();
					synchronized(this)
					{
						sock=new Socket(parsed.get(1),CMath.s_int(parsed.get(2)));
						sock.setSoTimeout(1);
						reader=new BufferedReader(new InputStreamReader(sock.getInputStream()));
						writer=new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
					}
					currentScreen="Connected to "+parsed.get(1)+" at port "+parsed.get(2);
					fillWithData();
					return true;
				}
				catch(Exception e)
				{
					msg.source().tell("Telnet software failure: "+e.getMessage());
					return false;
				}
			}
			case CMMsg.TYP_DEACTIVATE:
				shutdown();
				break;
			case CMMsg.TYP_WRITE:
				if(sock==null)
				{
					msg.source().tell("Software failure.");
					super.forceUpMenu();
					super.forceNewMenuRead();
				}
				else
					return true;
				break;
			case CMMsg.TYP_POWERCURRENT:
				return true;
			}
		}
		return super.okMessage(host,msg);
	}
	
	public void fillWithData()
	{
		try
		{
			synchronized(this)
			{
				if(reader!=null)
				{
					String msg=reader.readLine();
					while(msg!=null)
					{
						super.addScreenMessage(msg);
						msg=reader.readLine().replace('\n', '\0').replace('\r', '\0');
					}
				}
			}
		}
		catch(java.net.SocketTimeoutException se)
		{
			
		}
		catch(Exception e)
		{
			super.addScreenMessage("*** Telnet disconnected: "+e.getMessage()+" ***");
			super.forceNewMessageScan();
			shutdown();
			super.forceUpMenu();
			super.forceNewMenuRead();
		}
	}
	
	public void executeMsg(Environmental host, CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_ACTIVATE:
				break;
			case CMMsg.TYP_DEACTIVATE:
				break;
			case CMMsg.TYP_WRITE:
			{
				synchronized(this)
				{
					if(writer!=null)
					{
						try 
						{
							writer.write(msg.targetMessage()+"\n\r");
						} 
						catch (IOException e)
						{
							super.addScreenMessage("*** Telnet disconnected: "+e.getMessage()+" ***");
							super.forceNewMessageScan();
							shutdown();
							super.forceUpMenu();
							super.forceNewMenuRead();
						}
					}
				}
				break;
			}
			case CMMsg.TYP_POWERCURRENT:
				if(msg.value()>0)
					fillWithData();
				break;
			}
		}
		super.executeMsg(host, msg);
	}
}
