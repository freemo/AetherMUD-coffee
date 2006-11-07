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


import java.io.IOException;
import java.util.*;

/* 
   Copyright 2000-2006 Bo Zimmerman

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
public class CoffeeUtensils extends StdLibrary implements CMMiscUtils
{
    public String ID(){return "CoffeeUtensils";}
	
	public String niceCommaList(Vector V, boolean andTOrF)
	{
		String id="";
		for(int v=0;v<V.size();v++)
		{
			String s=null;
			if(V.elementAt(v) instanceof Environmental)
				s=((Environmental)V.elementAt(v)).name();
			else
			if(V.elementAt(v) instanceof String)
				s=(String)V.elementAt(v);
			else
				continue;
			if(V.size()==1)
				id+=s;
			else
			if(v==(V.size()-1))
				id+=((andTOrF)?"and ":"or ")+s;
			else
				id+=s+", ";
		}
		return id;
	}
	
	public Law getTheLaw(Room R, MOB mob)
	{
        LegalBehavior B=getLegalBehavior(R);
	    if(B!=null)
	    {
			Area A2=getLegalObject(R.getArea());
            return B.legalInfo(A2);
	    }
	    return null;
	}
	
	public LegalBehavior getLegalBehavior(Area A)
	{
		if(A==null) return null;
		Vector V=CMLib.flags().flaggedBehaviors(A,Behavior.FLAG_LEGALBEHAVIOR);
		if(V.size()>0) return (LegalBehavior)V.firstElement();
        LegalBehavior B=null;
		for(Enumeration e=A.getParents();e.hasMoreElements();)
		{
		    B=getLegalBehavior((Area)e.nextElement());
		    if(B!=null) break;
		}
		return B;
	}
	public LegalBehavior getLegalBehavior(Room R)
	{
		if(R==null) return null;
		Vector V=CMLib.flags().flaggedBehaviors(R,Behavior.FLAG_LEGALBEHAVIOR);
		if(V.size()>0) return (LegalBehavior)V.firstElement();
		return getLegalBehavior(R.getArea());
	}
	public Area getLegalObject(Area A)
	{
		if(A==null) return null;
		Vector V=CMLib.flags().flaggedBehaviors(A,Behavior.FLAG_LEGALBEHAVIOR);
		if(V.size()>0) return A;
	    Area A2=null;
	    Area A3=null;
		for(Enumeration e=A.getParents();e.hasMoreElements();)
		{
		    A2=(Area)e.nextElement();
		    A3=getLegalObject(A2);
		    if(A3!=null) return A3;
		}
		return A3;
	}
	public Area getLegalObject(Room R)
	{
		if(R==null) return null;
		return getLegalObject(R.getArea());
	}

	public Vector getAllUniqueTitles(Enumeration e, String owner, boolean includeRentals)
	{
	    Vector V=new Vector();
	    HashSet roomsDone=new HashSet();
	    Room R=null;
	    for(;e.hasMoreElements();)
	    {
	        R=(Room)e.nextElement();
	        LandTitle T=getLandTitle(R);
	        if((T!=null)
	        &&(!V.contains(T))
	        &&(includeRentals||(!T.rentalProperty()))
            &&((owner==null)
                ||(owner.length()==0)
                ||(owner.equals("*")&&(T.landOwner().length()>0))
                ||(T.landOwner().equals(owner))))
	        {
	            Vector V2=T.getPropertyRooms();
	            boolean proceed=true;
	            for(int v=0;v<V2.size();v++)
	            {
	                Room R2=(Room)V2.elementAt(v);
	                if(!roomsDone.contains(R2))
	                    roomsDone.add(R2);
	                else
	                    proceed=false;
	            }
	            if(proceed)
	                V.addElement(T);
	                
	        }
	    }
	    return V;
	}
	
	public String getFormattedDate(Environmental E)
	{
	    String date=CMStrings.padRight("Unknown",11);
	    if(E!=null)
	    {
		    TimeClock C=(E instanceof Area)?((Area)E).getTimeObj():((CMLib.map().roomLocation(E)!=null)?CMLib.map().roomLocation(E).getArea().getTimeObj():null);
		    if(C!=null)
		        date=CMStrings.padRight(C.getDayOfMonth()+"-"+C.getMonth()+"-"+C.getYear(),11);
	    }
	    return date;
	}

	public void outfit(MOB mob, Vector items)
	{
		if((mob==null)||(items==null)||(items.size()==0))
			return;
		for(int i=0;i<items.size();i++)
		{
			Item I=(Item)items.elementAt(i);
			if(mob.fetchInventory("$"+I.name()+"$")==null)
			{
				I=(Item)I.copyOf();
				I.text();
				I.recoverEnvStats();
				mob.addInventory(I);
				if(I.whereCantWear(mob)<=0)
					I.wearIfPossible(mob);
				if(((I instanceof Armor)||(I instanceof Weapon))
				&&(I.amWearingAt(Item.IN_INVENTORY)))
					I.destroy();
			}
		}
	}

	public Trap makeADeprecatedTrap(Environmental unlockThis)
	{
		Trap theTrap=null;
		int roll=(int)Math.round(Math.random()*100.0);
		if(unlockThis instanceof Exit)
		{
			if(((Exit)unlockThis).hasADoor())
			{
				if(((Exit)unlockThis).hasALock())
				{
					if(roll<20)
						theTrap=(Trap)CMClass.getAbility("Trap_Open");
					else
					if(roll<80)
						theTrap=(Trap)CMClass.getAbility("Trap_Unlock");
					else
						theTrap=(Trap)CMClass.getAbility("Trap_Enter");
				}
				else
				{
					if(roll<50)
						theTrap=(Trap)CMClass.getAbility("Trap_Open");
					else
						theTrap=(Trap)CMClass.getAbility("Trap_Enter");
				}
			}
			else
				theTrap=(Trap)CMClass.getAbility("Trap_Enter");
		}
		else
		if(unlockThis instanceof Container)
		{
			if(((Container)unlockThis).hasALid())
			{
				if(((Container)unlockThis).hasALock())
				{
					if(roll<20)
						theTrap=(Trap)CMClass.getAbility("Trap_Open");
					else
					if(roll<80)
						theTrap=(Trap)CMClass.getAbility("Trap_Unlock");
					else
						theTrap=(Trap)CMClass.getAbility("Trap_Get");
				}
				else
				{
					if(roll<50)
						theTrap=(Trap)CMClass.getAbility("Trap_Open");
					else
						theTrap=(Trap)CMClass.getAbility("Trap_Get");
				}
			}
			else
				theTrap=(Trap)CMClass.getAbility("Trap_Get");
		}
		else
		if(unlockThis instanceof Item)
			theTrap=(Trap)CMClass.getAbility("Trap_Get");
		return theTrap;
	}


	public void setTrapped(Environmental myThang, boolean isTrapped)
	{
		Trap t=makeADeprecatedTrap(myThang);
		t.setReset(50);
		setTrapped(myThang,t,isTrapped);
	}
	public void setTrapped(Environmental myThang, Trap theTrap, boolean isTrapped)
	{
		for(int a=0;a<myThang.numEffects();a++)
		{
			Ability A=myThang.fetchEffect(a);
			if((A!=null)&&(A instanceof Trap))
				A.unInvoke();
		}

		if((isTrapped)&&(myThang.fetchEffect(theTrap.ID())==null))
			myThang.addEffect(theTrap);
	}

	public Trap fetchMyTrap(Environmental myThang)
	{
		if(myThang==null) return null;
        Ability A=null;
		for(int a=0;a<myThang.numEffects();a++)
		{
			A=myThang.fetchEffect(a);
			if((A!=null)&&(A instanceof  Trap))
				return (Trap)A;
		}
		return null;
	}
	public boolean reachableItem(MOB mob, Environmental E)
	{
		if((E==null)||(!(E instanceof Item)))
			return true;
        Item I=(Item)E;
		if((mob.isMine(I))
		||((mob.riding()!=null)&&((I==mob.riding())
								  ||(I.owner()==mob.riding())
								  ||(I.ultimateContainer()==mob.riding())))
        ||(I.owner()==null)
        ||((I.owner() instanceof Room)&&(!((Room)I.owner()).isContent(I))))
		   return true;
		return false;
	}

    public double memoryUse ( Environmental E, int number )
    {
		double s=-1.0;
		try
		{
            int n = number;
            Object[] objs = new Object[n] ;
            Environmental cl = E;
            Runtime rt = Runtime.getRuntime() ;
			long m0 =rt.totalMemory() - rt.freeMemory() ;
			System.gc() ;
			Thread.sleep( 500 ) ;
            for (int i = 0 ; i < n ; ++i) objs[i] =
                    E=(Environmental)cl.copyOf();
			System.gc() ;
			Thread.sleep( 1000 ) ;
			long m1 =rt.totalMemory() - rt.freeMemory() ;
            long dm = m1 - m0 ;
            s = (double)dm / (double)n ;
			if(s<0.0) return memoryUse(E,number);
		}
		catch(Exception e){return -1;}
		return s;
    }

	public void extinguish(MOB source, Environmental target, boolean mundane)
	{
		if(target instanceof Room)
		{
			Room R=(Room)target;
			for(int m=0;m<R.numInhabitants();m++)
			{
				MOB M=R.fetchInhabitant(m);
				if(M!=null) extinguish(source,M,mundane);
			}
			for(int i=0;i<R.numItems();i++)
			{
				Item I=R.fetchItem(i);
				if(I!=null) extinguish(source,I,mundane);
			}
			return;
		}
		for(int a=target.numEffects()-1;a>=0;a--)
		{
			Ability A=target.fetchEffect(a);
			if((A!=null)&&((!mundane)||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_PROPERTY)))
			{
				if((CMath.bset(A.flags(),Ability.FLAG_HEATING)&&(!mundane))
				||(CMath.bset(A.flags(),Ability.FLAG_FIREBASED))
				||((A.ID().equalsIgnoreCase("Spell_SummonElemental")&&A.text().toUpperCase().indexOf("FIRE")>=0)))
					A.unInvoke();
			}
		}
		if((target instanceof MOB)&&(!mundane))
		{
			MOB tmob=(MOB)target;
			if(tmob.charStats().getMyRace().ID().equals("FireElemental"))
				CMLib.combat().postDeath(source,(MOB)target,null);
			for(int i=0;i<tmob.inventorySize();i++)
			{
				Item I=tmob.fetchInventory(i);
				if(I!=null) extinguish(tmob,I,mundane);
			}
		}
		if((target instanceof Light)&&(((Light)target).isLit()))
		{
			((Light)target).tick(target,Tickable.TICKID_LIGHT_FLICKERS);
			((Light)target).light(false);
		}
	}

	public void roomAffectFully(CMMsg msg, Room room, int dirCode)
	{
		room.send(msg.source(),msg);
		if((msg.target()==null)||(!(msg.target() instanceof Exit)))
			return;
		if(dirCode<0)
		{
			for(int d=0;d<Directions.NUM_DIRECTIONS;d++)
				if(room.getExitInDir(d)==msg.target()){ dirCode=d; break;}
		}
		if(dirCode<0) return;
		Exit pair=room.getPairedExit(dirCode);
		if(pair!=null)
		{
			CMMsg altMsg=null;
			if((msg.targetCode()==CMMsg.MSG_OPEN)&&(pair.isLocked()))
			{
				altMsg=CMClass.getMsg(msg.source(),pair,msg.tool(),CMMsg.MSG_UNLOCK,null,CMMsg.MSG_UNLOCK,null,CMMsg.MSG_UNLOCK,null);
				pair.executeMsg(msg.source(),altMsg);
			}
			altMsg=CMClass.getMsg(msg.source(),pair,msg.tool(),msg.sourceCode(),null,msg.targetCode(),null,msg.othersCode(),null);
			pair.executeMsg(msg.source(),altMsg);
		}
	}

    
    public void recursiveDropMOB(MOB mob,
                                 Room room,
                                 Item thisContainer,
                                 boolean bodyFlag)
    {
        // caller is responsible for recovering any env
        // stat changes!
        
        if(CMLib.flags().isHidden(thisContainer))
            thisContainer.baseEnvStats().setDisposition(thisContainer.baseEnvStats().disposition()&((int)EnvStats.ALLMASK-EnvStats.IS_HIDDEN));
        mob.delInventory(thisContainer);
        thisContainer.unWear();
        if(!bodyFlag) bodyFlag=(thisContainer instanceof DeadBody);
        if(bodyFlag)
        {
            room.addItem(thisContainer);
            thisContainer.setExpirationDate(0);
        }
        else
            room.addItemRefuse(thisContainer,Item.REFUSE_PLAYER_DROP);
        thisContainer.recoverEnvStats();
        boolean nothingDone=true;
        do
        {
            nothingDone=true;
            for(int i=0;i<mob.inventorySize();i++)
            {
                Item thisItem=mob.fetchInventory(i);
                if((thisItem!=null)&&(thisItem.container()==thisContainer))
                {
                    recursiveDropMOB(mob,room,thisItem,bodyFlag);
                    nothingDone=false;
                    break;
                }
            }
        }while(!nothingDone);
    }
    
    
    public MOB getMobPossessingAnother(MOB mob)
    {
        if(mob==null) return null;
        Session S=null;
        MOB M=null;
        for(int s=0;s<CMLib.sessions().size();s++)
        {
            S=CMLib.sessions().elementAt(s);
            if(S!=null)
            {
                M=S.mob();
                if((M!=null)&&(M.soulMate()==mob))
                    return M;
            }
        }
        return null;
    }
    
	public LandTitle getLandTitle(Area area)
	{
		if(area==null) return null;
		for(int a=0;a<area.numEffects();a++)
		{
			Ability A=area.fetchEffect(a);
			if((A!=null)&&(A instanceof LandTitle))
				return (LandTitle)A;
		}
		return null;
	}
	public LandTitle getLandTitle(Room room)
	{
		if(room==null) return null;
		LandTitle title=getLandTitle(room.getArea());
		if(title!=null) return title;
        Ability A=null;
		for(int a=0;a<room.numEffects();a++)
		{
			A=room.fetchEffect(a);
			if((A!=null)&&(A instanceof LandTitle))
				return (LandTitle)A;
		}
		return null;
	}

	public boolean doesHavePriviledgesHere(MOB mob, Room room)
	{
		LandTitle title=getLandTitle(room);
		if(title==null) return false;
		if(title.landOwner()==null) return false;
		if(title.landOwner().length()==0) return false;
		if(title.landOwner().equals(mob.Name())) return true;
		if((title.landOwner().equals(mob.getLiegeID())&&(mob.isMarriedToLiege())))
			return true;
		if(title.landOwner().equals(mob.getClanID()))
			return true;
		if(mob.amFollowing()!=null) 
			return doesHavePriviledgesHere(mob.amFollowing(),room);
		return false;
	}
	
	public boolean doesOwnThisProperty(String name, Room room)
	{
		LandTitle title=getLandTitle(room);
		if(title==null) return false;
		if(title.landOwner()==null) return false;
		if(title.landOwner().length()==0) return false;
		if(title.landOwner().equals(name)) return true;
		return false;
	}
    
    public Ability getClericInfusion(Environmental room)
    {
        if(room==null) return null;
        Ability A=null;
        for(int e=room.numEffects()-1;e>=0;e--)
        {
            A=room.fetchEffect(e);
            if((A!=null)&&(A.ID().startsWith("Prayer_Infuse")))
                return A;
        }
        return null;
    }
    public Deity getClericInfused(Room room)
    {
        Ability A=getClericInfusion(room);
        if(A==null) return null;
        return CMLib.map().getDeity(A.text());
    }
	
	public boolean doesOwnThisProperty(MOB mob, Room room)
	{
		LandTitle title=getLandTitle(room);
		if(title==null) return false;
		if(title.landOwner()==null) return false;
		if(title.landOwner().length()==0) return false;
		if(title.landOwner().equals(mob.Name())) return true;
		if((title.landOwner().equals(mob.getLiegeID())&&(mob.isMarriedToLiege())))
		   return true;
		if(title.landOwner().equals(mob.getClanID()))
		{
			Clan C=CMLib.clans().getClan(mob.getClanID());
			if((C!=null)&&(C.allowedToDoThis(mob,Clan.FUNC_CLANPROPERTYOWNER)>=0))
				return true;
		}
		if(mob.amFollowing()!=null) 
			return doesOwnThisProperty(mob.amFollowing(),room);
		return false;
	}
	
	public boolean armorCheck(MOB mob, int allowedArmorLevel)
	{
		if(allowedArmorLevel==CharClass.ARMOR_ANY) return true;

		for(int i=0;i<mob.inventorySize();i++)
		{
			Item I=mob.fetchInventory(i);
			if(I==null) break;
			if((!I.amWearingAt(Item.IN_INVENTORY))
			&&((I instanceof Armor)||(I instanceof Shield)))
			{
				boolean ok=true;
				switch(I.material()&RawMaterial.MATERIAL_MASK)
				{
				case RawMaterial.MATERIAL_LEATHER:
					if((allowedArmorLevel==CharClass.ARMOR_CLOTH)
					||(allowedArmorLevel==CharClass.ARMOR_VEGAN)
					||(allowedArmorLevel==CharClass.ARMOR_OREONLY)
					||(allowedArmorLevel==CharClass.ARMOR_METALONLY))
						ok=false;
					break;
				case RawMaterial.MATERIAL_METAL:
				case RawMaterial.MATERIAL_MITHRIL:
					if((allowedArmorLevel==CharClass.ARMOR_CLOTH)
					||(allowedArmorLevel==CharClass.ARMOR_LEATHER)
					||(allowedArmorLevel==CharClass.ARMOR_NONMETAL))
						ok=false;
					break;
				case RawMaterial.MATERIAL_ENERGY:
					if((allowedArmorLevel==CharClass.ARMOR_METALONLY)
					||(allowedArmorLevel==CharClass.ARMOR_OREONLY)
					||(allowedArmorLevel==CharClass.ARMOR_VEGAN))
					   return false;
					break;
				case RawMaterial.MATERIAL_CLOTH:
					if((allowedArmorLevel==CharClass.ARMOR_METALONLY)
					||(allowedArmorLevel==CharClass.ARMOR_OREONLY)
					||((allowedArmorLevel==CharClass.ARMOR_VEGAN)
					   &&((I.material()==RawMaterial.RESOURCE_HIDE)
						  ||(I.material()==RawMaterial.RESOURCE_FUR)
						  ||(I.material()==RawMaterial.RESOURCE_FEATHERS)
						  ||(I.material()==RawMaterial.RESOURCE_WOOL))))
						ok=false;
					break;
				case RawMaterial.MATERIAL_PLASTIC:
				case RawMaterial.MATERIAL_WOODEN:
					if((allowedArmorLevel==CharClass.ARMOR_CLOTH)
					||(allowedArmorLevel==CharClass.ARMOR_OREONLY)
					||(allowedArmorLevel==CharClass.ARMOR_LEATHER)
					||(allowedArmorLevel==CharClass.ARMOR_METALONLY))
						ok=false;
					break;
				case RawMaterial.MATERIAL_ROCK:
				case RawMaterial.MATERIAL_GLASS:
					if((allowedArmorLevel==CharClass.ARMOR_CLOTH)
					||(allowedArmorLevel==CharClass.ARMOR_LEATHER)
					||(allowedArmorLevel==CharClass.ARMOR_METALONLY))
						ok=false;
					break;
				case RawMaterial.MATERIAL_FLESH:
					if((allowedArmorLevel==CharClass.ARMOR_METALONLY)
					||(allowedArmorLevel==CharClass.ARMOR_VEGAN)
					||(allowedArmorLevel==CharClass.ARMOR_CLOTH)
					||(allowedArmorLevel==CharClass.ARMOR_OREONLY))
						ok=false;
					break;
				default:
					if((allowedArmorLevel==CharClass.ARMOR_METALONLY)
					||(allowedArmorLevel==CharClass.ARMOR_OREONLY))
						ok=false;
					break;
				}
				if((!ok)&&((I.rawWornCode()&CharClass.ARMOR_WEARMASK)>0))
					return false;
			}
		}
		return true;
	}
    
    
    public String wornList(long wornCode)
    {
        StringBuffer buf=new StringBuffer("");
        for(int wornNum=0;wornNum<Item.WORN_DESCS.length-1;wornNum++)
        {
            if(CMath.isSet(wornCode,wornNum))
                buf.append(Item.WORN_DESCS[wornNum+1]+", ");
        }
        String buff=buf.toString();
        if(buff.endsWith(", ")) buff=buff.substring(0,buff.length()-2).trim();
        return buff;
    }
    
    public int getWornCode(String name)
    {
        int wornNum=0;
        name=name.toLowerCase().trim();
        for(;wornNum<Item.WORN_DESCS.length-1;wornNum++)
            if(Item.WORN_DESCS[wornNum].endsWith(name))
                return wornNum;
        return -1;
    }
}

