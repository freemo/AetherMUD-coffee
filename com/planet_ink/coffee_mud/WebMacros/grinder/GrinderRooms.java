package com.planet_ink.coffee_mud.WebMacros.grinder;
import com.planet_ink.coffee_mud.WebMacros.RoomData;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
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
public class GrinderRooms
{
	public static void happilyAddMob(MOB M, Room R)
	{
		M.setStartRoom(R);
		M.setLocation(R);
		M.envStats().setRejuv(5000);
		M.recoverCharStats();
		M.recoverEnvStats();
		M.recoverMaxState();
		M.resetToMaxState();
		M.bringToLife(R,true);
		R.recoverRoomStats();
	}
	public static void happilyAddItem(Item I, Room R)
	{
		if(I.subjectToWearAndTear())
			I.setUsesRemaining(100);
		I.recoverEnvStats();
		R.addItem(I);
		R.recoverRoomStats();
	}

	public static String editRoom(ExternalHTTPRequests httpReq, Hashtable parms, Room R)
	{
		if(R==null) return "Old Room not defined!";
		boolean redoAllMyDamnRooms=false;
		Room oldR=R;

		// class!
		String className=httpReq.getRequestParameter("CLASSES");
		if((className==null)||(className.length()==0))
			return "Please select a class type for this room.";

		CMLib.utensils().resetRoom(R);

		if(!className.equalsIgnoreCase(CMClass.className(R)))
		{
			R=CMClass.getLocale(className);
			if(R==null)
				return "The class you chose does not exist.  Choose another.";
			for(int a=oldR.numEffects()-1;a>=0;a--)
			{
				Ability A=oldR.fetchEffect(a);
				if(A!=null)
				{
					A.unInvoke();
					oldR.delEffect(A);
				}
			}
			CMLib.threads().deleteTick(oldR,-1);
			R.setArea(oldR.getArea());
			R.setRoomID(oldR.roomID());
			for(int d=0;d<R.rawDoors().length;d++)
				R.rawDoors()[d]=oldR.rawDoors()[d];
			for(int d=0;d<R.rawExits().length;d++)
				R.rawExits()[d]=oldR.rawExits()[d];
			redoAllMyDamnRooms=true;
		}

		// name
		String name=httpReq.getRequestParameter("NAME");
		if((name==null)||(name.length()==0))
			return "Please enter a name for this room.";
		R.setDisplayText(name);


		// description
		String desc=httpReq.getRequestParameter("DESCRIPTION");
		if(desc==null)desc="";
		R.setDescription(desc);

		// image
		String img=httpReq.getRequestParameter("IMAGE");
		if(img==null)img="";
		R.setImage(img);

		if(R instanceof GridLocale)
		{
			String x=httpReq.getRequestParameter("XGRID");
			if(x==null)x="";
			((GridLocale)R).setXSize(Util.s_int(x));
			String y=httpReq.getRequestParameter("YGRID");
			if(y==null)y="";
			((GridLocale)R).setYSize(Util.s_int(y));
			((GridLocale)R).clearGrid(null);
		}

		String err=GrinderAreas.doAffectsNBehavs(R,httpReq,parms);
		if(err.length()>0) return err;

		// here's where you resolve items and mobs
		Vector allmobs=new Vector();
		int skip=0;
		while(oldR.numInhabitants()>(skip))
		{
			MOB M=oldR.fetchInhabitant(skip);
			if(M.isEligibleMonster())
			{
				if(!allmobs.contains(M))
					allmobs.addElement(M);
				oldR.delInhabitant(M);
			}
			else
			if(oldR!=R)
			{
				oldR.delInhabitant(M);
				R.bringMobHere(M,true);
			}
			else
				skip++;
		}
		Vector allitems=new Vector();
		while(oldR.numItems()>0)
		{
			Item I=oldR.fetchItem(0);
			if(!allitems.contains(I))
				allitems.addElement(I);
			oldR.delItem(I);
		}

		if(httpReq.isRequestParameter("MOB1"))
		{
			for(int i=1;;i++)
			{
				String MATCHING=httpReq.getRequestParameter("MOB"+i);
				if(MATCHING==null)
					break;
				else
				if(RoomData.isAllNum(MATCHING))
				{
					MOB M=RoomData.getMOBFromCode(allmobs,MATCHING);
					if(M!=null)	happilyAddMob(M,R);
					else
					{
						StringBuffer str=new StringBuffer("!!!No MOB?!!!!");
						str.append(" Got: "+MATCHING);
					}
				}
				else
				if(MATCHING.indexOf("@")>0)
				{
					for(int m=0;m<RoomData.mobs.size();m++)
					{
						MOB M2=(MOB)RoomData.mobs.elementAt(m);
						if(MATCHING.equals(""+M2))
						{
							happilyAddMob((MOB)M2.copyOf(),R);
							break;
						}
					}
				}
				else
				for(Enumeration m=CMClass.mobTypes();m.hasMoreElements();)
				{
					MOB M2=(MOB)m.nextElement();
					if((CMClass.className(M2).equals(MATCHING)))
					{
						happilyAddMob((MOB)M2.copyOf(),R);
						break;
					}
				}
			}
		}
		else
			return "No MOB Data!";


		if(httpReq.isRequestParameter("ITEM1"))
		{
			for(int i=1;;i++)
			{
				String MATCHING=httpReq.getRequestParameter("ITEM"+i);
				if(MATCHING==null)
					break;
				Item I2=RoomData.getItemFromAnywhere(allitems,MATCHING);
				if(I2!=null)
				{
					if(RoomData.isAllNum(MATCHING))
						happilyAddItem(I2,R);
					else
						happilyAddItem((Item)I2.copyOf(),R);
				}
			}
		}
		else
			return "No Item Data!";


		for(int i=0;i<allitems.size();i++)
		{
			Item I=(Item)allitems.elementAt(i);
			if(!R.isContent(I))
				I.destroy();
		}
		for(int i=0;i<R.numItems();i++)
		{
			Item I=R.fetchItem(i);
			if((I.container()!=null)&&(!R.isContent(I.container())))
				I.setContainer(null);
		}
		for(int m=0;m<allmobs.size();m++)
		{
			MOB M=(MOB)allmobs.elementAt(m);
			if(!R.isInhabitant(M))
				M.destroy();
		}

		if(redoAllMyDamnRooms)
		{
		    try
		    {
				for(Enumeration r=CMLib.map().rooms();r.hasMoreElements();)
				{
					Room R2=(Room)r.nextElement();
					for(int d=0;d<R2.rawDoors().length;d++)
						if(R2.rawDoors()[d]==oldR)
						{
							R2.rawDoors()[d]=R;
							if(R2 instanceof GridLocale)
								((GridLocale)R2).buildGrid();
						}
				}
		    }catch(NoSuchElementException e){}
		    try
		    {
				for(Enumeration e=CMLib.map().players();e.hasMoreElements();)
				{
					MOB M=(MOB)e.nextElement();
					if(M.getStartRoom()==oldR)
						M.setStartRoom(R);
					else
					if(M.location()==oldR)
						M.setLocation(R);
				}
		    }catch(NoSuchElementException e){}
		}
		R.getArea().fillInAreaRoom(R);
		CMLib.database().DBUpdateRoom(R);
		CMLib.database().DBUpdateMOBs(R);
		CMLib.database().DBUpdateItems(R);
		R.startItemRejuv();
        oldR.destroyRoom();
		return "";
	}



	public static String delRoom(Room R)
	{
		CMLib.utensils().obliterateRoom(R);
		return "";
	}

	public static Room createLonelyRoom(Area A, Room linkTo, int dir, boolean copyThisOne)
	{
		Room newRoom=null;
		if((copyThisOne)&&(linkTo!=null))
		{
			CMLib.utensils().resetRoom(linkTo);
			newRoom=(Room)linkTo.copyOf();
			for(int d=0;d<Directions.NUM_DIRECTIONS;d++)
			{
				newRoom.rawDoors()[d]=null;
				newRoom.rawExits()[d]=null;
			}
		}
		else
		{
			newRoom=CMClass.getLocale("StdRoom");
			newRoom.setDisplayText("Title of "+newRoom.roomID());
			newRoom.setDescription("Description of "+newRoom.roomID());
		}
		newRoom.setRoomID(CMLib.map().getOpenRoomID(A.Name()));
		newRoom.setArea(A);
		if(linkTo!=null)
		{
			newRoom.rawDoors()[Directions.getOpDirectionCode(dir)]=linkTo;
			newRoom.rawExits()[Directions.getOpDirectionCode(dir)]=CMClass.getExit("StdOpenDoorway");
		}
		CMLib.database().DBCreateRoom(newRoom,CMClass.className(newRoom));
		CMLib.database().DBUpdateExits(newRoom);
		if(newRoom.numInhabitants()>0)
			CMLib.database().DBUpdateMOBs(newRoom);
		if(newRoom.numItems()>0)
			CMLib.database().DBUpdateItems(newRoom);
		newRoom.getArea().fillInAreaRoom(newRoom);
		return newRoom;
	}

	public static String createRoom(Room R, int dir, boolean copyThisOne)
	{
		R.clearSky();
		if(R instanceof GridLocale)
			((GridLocale)R).clearGrid(null);
		Room newRoom=createLonelyRoom(R.getArea(),R,dir,copyThisOne);
		R.rawDoors()[dir]=newRoom;
		if(R.rawExits()[dir]==null)
			R.rawExits()[dir]=CMClass.getExit("StdOpenDoorway");
		CMLib.database().DBUpdateExits(R);
		R.getArea().fillInAreaRoom(R);
		return "";
	}
}
