package com.planet_ink.coffee_mud.WebMacros;
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
public class TimsItemTable extends StdWebMacro
{
	public String name(){return this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);}
	public boolean isAdminMacro()	{return true;}

	
	public String runMacro(ExternalHTTPRequests httpReq, String parm)
	{
		long endTime=System.currentTimeMillis()+(1000*60*10);
		int min=CMath.s_int((httpReq.getRequestParameter("MIN")));
		if(min>0)
			endTime=System.currentTimeMillis()+(1000*60*((long)min));
		
		StringBuffer str=new StringBuffer("<TABLE WIDTH=100% BORDER=1>");
		str.append("<TR><TD>Name</TD><TD>LVL</TD><TD>TVLV</TD><TD>DIFF</TD><TD>DIFF%</TD><TD>ARM</TD><TD>ATT</TD><TD>DAM</TD><TD>ADJ</TD><TD>CAST</TD><TD>RESIST</TD></TR>");
		Vector onesDone=new Vector();
		for(Enumeration e=CMLib.map().areas();e.hasMoreElements();)
		{
			Area A=(Area)e.nextElement();
			for(Enumeration r=A.getProperMap();r.hasMoreElements();)
			{
				Room R=(Room)r.nextElement();
				if((endTime>0)&&(System.currentTimeMillis()>endTime))
					break;
				for(int i=0;i<R.numItems();i++)
				{
					Item I=R.fetchItem(i);
					if((endTime>0)&&(System.currentTimeMillis()>endTime))
						break;
					if(!doneBefore(onesDone,I)) str.append(addRow(I));
				}
				if((endTime>0)&&(System.currentTimeMillis()>endTime))
					break;
				for(int m=0;m<R.numInhabitants();m++)
				{
					if((endTime>0)&&(System.currentTimeMillis()>endTime))
						break;
					MOB M=R.fetchInhabitant(m);
					if(M==null) continue;
					for(int i=0;i<M.inventorySize();i++)
					{
						Item I=M.fetchInventory(i);
						if((endTime>0)&&(System.currentTimeMillis()>endTime))
							break;
						if(!doneBefore(onesDone,I)) str.append(addRow(I));
					}
					if((endTime>0)&&(System.currentTimeMillis()>endTime))
						break;
					if(!(M instanceof ShopKeeper)) continue;
					ShopKeeper S=(ShopKeeper)M;
					Vector V2=S.getShop().getStoreInventory();
					for(int v=0;v<V2.size();v++)
					{
						if((endTime>0)&&(System.currentTimeMillis()>endTime))
							break;
						if((V2.elementAt(v) instanceof Item)
						&&(!doneBefore(onesDone,(Item)V2.elementAt(v))))
							str.append(addRow((Item)V2.elementAt(v)));
					}
				}
			}
		}
        return clearWebMacros(str)+"</TABLE>";
	}
	
	public boolean doneBefore(Vector V, Item I)
	{
		if(I==null) return true;
		if((!(I instanceof Armor))&&(!(I instanceof Weapon)))
			return true;
		if(I.displayText().length()==0)
			return true;
		for(int i=0;i<V.size();i++)
			if(I.sameAs((Environmental)V.elementAt(i)))
				return true;
		V.addElement(I);
		return false;
	}
	
	public String addRow(Item I)
	{
		StringBuffer row=new StringBuffer("");
		int lvl=I.envStats().level();
		Ability ADJ=I.fetchEffect("Prop_WearAdjuster");
		if(ADJ==null) ADJ=I.fetchEffect("Prop_HaveAdjuster");
		if(ADJ==null) ADJ=I.fetchEffect("Prop_RideAdjuster");
		Ability RES=I.fetchEffect("Prop_WearResister");
		if(RES==null) RES=I.fetchEffect("Prop_HaveResister");
		Ability CAST=I.fetchEffect("Prop_WearSpellCast");
		int castMul=1;
		if(CAST==null) CAST=I.fetchEffect("Prop_UseSpellCast");
		if(CAST==null) CAST=I.fetchEffect("Prop_UseSpellCast2");
		if(CAST==null) CAST=I.fetchEffect("Prop_HaveSpellCast");
		if(CAST==null){ CAST=I.fetchEffect("Prop_FightSpellCast"); castMul=-1;}
		row.append("<TR>");
		row.append("<TD>"+I.name()+"</TD>");
		row.append("<TD>"+lvl+"</TD>");
		int tlvl=timsLevelCalculator(I,ADJ,RES,CAST,castMul);
		row.append("<TD>"+tlvl+"</TD>");
		int diff=tlvl-lvl; if(diff<0) diff=diff*-1;
		row.append("<TD>"+diff+"</TD>");
		int pct=0;
		if((lvl<0)&&(tlvl>=0)) pct=(int)Math.round(CMath.div(tlvl+(lvl*-1),1)*100.0);
		else
		if((tlvl<=0)&&(lvl>0)) pct=(int)Math.round(CMath.div((tlvl-lvl),-1)*100.0);
		else
		if((tlvl<0)&&(lvl==0)) pct=(int)Math.round(CMath.div(tlvl,-1)*100.0);
		else
		if(lvl==0) pct=(int)Math.round(CMath.div(tlvl,1)*100.0);
		else
			pct=(int)Math.round(CMath.div(tlvl,lvl)*100.0);
		row.append("<TD>"+pct+"%</TD>");
		
		if(!(I instanceof Weapon))
			row.append("<TD>"+I.baseEnvStats().armor()+"</TD><TD>&nbsp;</TD><TD>&nbsp;</TD>");
		else
		{
			row.append("<TD>&nbsp;</TD><TD>"+I.baseEnvStats().attackAdjustment()+"</TD>");
			row.append("<TD>"+I.baseEnvStats().damage()+"</TD>");
		}
		if(ADJ!=null) row.append("<TD>"+ADJ.text()+"</TD>");
		else row.append("<TD>&nbsp;</TD>");
		if(CAST!=null) row.append("<TD>"+CAST.text()+"</TD>");
		else row.append("<TD>&nbsp;</TD>");
		if(RES!=null) row.append("<TD>"+RES.text()+"</TD>");
		else row.append("<TD>&nbsp;</TD>");
		row.append("</TR>");
		return row.toString();
	}
	
	public static int timsLevelCalculator(Item I,
										  Ability ADJ,
										  Ability RES,
										  Ability CAST,
										  int castMul)
	{
		int level=0;
		Item savedI=(Item)I.copyOf();
		savedI.recoverEnvStats();
		I=(Item)I.copyOf();
		I.recoverEnvStats();
		int otherDam=0;
		int otherAtt=0;
		int otherArm=0;
		if(ADJ!=null)
		{
			otherArm=CMParms.getParmPlus(ADJ.text(),"arm")*-1;
			otherAtt=CMParms.getParmPlus(ADJ.text(),"att");
			otherDam=CMParms.getParmPlus(ADJ.text(),"dam");
		}
		int curArmor=savedI.baseEnvStats().armor()+otherArm;
		double curAttack=new Integer(savedI.baseEnvStats().attackAdjustment()+otherAtt).doubleValue();
		double curDamage=new Integer(savedI.baseEnvStats().damage()+otherDam).doubleValue();
		if(I instanceof Weapon)
		{
			double weight=new Integer(I.baseEnvStats().weight()).doubleValue();
			if(weight<1.0) weight=1.0;
			double range=new Integer(savedI.maxRange()).doubleValue();
			level=(int)Math.round(Math.floor((2.0*curDamage/(2.0*(I.rawLogicalAnd()?2.0:1.0)+1.0)+(curAttack-weight)/5.0+range)*(range/weight+2.0)))+1;
		}
		else
		{
			long worndata=savedI.rawProperLocationBitmap();
			double weightpts=0;
			for(int i=0;i<Item.WORN_WEIGHTS.length-1;i++)
			{
				if(CMath.isSet(worndata,i))
				{
					weightpts+=Item.WORN_WEIGHTS[i+1];
					if(!I.rawLogicalAnd()) break;
				}
			}
			int[] leatherPoints={ 0, 0, 1, 5,10,16,23,31,40,49,58,67,76,85,94};
			int[] clothPoints=  { 0, 3, 7,12,18,25,33,42,52,62,72,82,92,102};
			int[] metalPoints=  { 0, 0, 0, 0, 1, 3, 5, 8,12,17,23,30,38,46,54,62,70,78,86,94};
			int materialCode=savedI.material()&RawMaterial.MATERIAL_MASK;
			int[] useArray=null;
			switch(materialCode)
			{
			case RawMaterial.MATERIAL_METAL:
			case RawMaterial.MATERIAL_MITHRIL:
			case RawMaterial.MATERIAL_PRECIOUS:
			case RawMaterial.MATERIAL_ENERGY:
				useArray=metalPoints;
				break;
			case RawMaterial.MATERIAL_PLASTIC:
			case RawMaterial.MATERIAL_LEATHER:
			case RawMaterial.MATERIAL_GLASS:
			case RawMaterial.MATERIAL_ROCK:
			case RawMaterial.MATERIAL_WOODEN:
				useArray=leatherPoints;
				break;
			default:
				useArray=clothPoints;
				break;
			}
			int which=(int)Math.round(CMath.div(curArmor,weightpts)+1);
			if(which<0) which=0;
			if(which>=useArray.length)
				which=useArray.length-1;
			level=useArray[which];
		}
		level+=I.baseEnvStats().ability()*5;
		if(CAST!=null)
		{
			String ID=CAST.ID().toUpperCase();
			Vector theSpells=new Vector();
			String names=CAST.text();
			int del=names.indexOf(";");
			while(del>=0)
			{
				String thisOne=names.substring(0,del);
				Ability A=CMClass.getAbility(thisOne);
				if(A!=null)	theSpells.addElement(A);
				names=names.substring(del+1);
				del=names.indexOf(";");
			}
			Ability A=CMClass.getAbility(names);
			if(A!=null) theSpells.addElement(A);
			for(int v=0;v<theSpells.size();v++)
			{
				A=(Ability)theSpells.elementAt(v);
				int mul=1;
				if(A.quality()==Ability.MALICIOUS) mul=-1;
				if(ID.indexOf("HAVE")>=0)
					level+=(mul*CMLib.ableMapper().lowestQualifyingLevel(A.ID()));
				else
					level+=(mul*CMLib.ableMapper().lowestQualifyingLevel(A.ID())/2);
			}
		}
		if(ADJ!=null)
		{
			String newText=ADJ.text();
			int ab=CMParms.getParmPlus(newText,"abi");
			int arm=CMParms.getParmPlus(newText,"arm")*-1;
			int att=CMParms.getParmPlus(newText,"att");
			int dam=CMParms.getParmPlus(newText,"dam");
			if(savedI instanceof Weapon)
				level+=(arm*2);
			else
			if(savedI instanceof Armor)
			{
				level+=(att/2);
				level+=(dam*3);
			}
			level+=ab*5;
			
			
			int dis=CMParms.getParmPlus(newText,"dis");
			if(dis!=0) level+=5;
			int sen=CMParms.getParmPlus(newText,"sen");
			if(sen!=0) level+=5;
			level+=(int)Math.round(5.0*CMParms.getParmDoublePlus(newText,"spe"));
			for(int i=0;i<CharStats.NUM_BASE_STATS;i++)
			{
				int stat=CMParms.getParmPlus(newText,CharStats.STAT_DESCS[i].substring(0,3).toLowerCase());
				int max=CMParms.getParmPlus(newText,("max"+(CharStats.STAT_DESCS[i].substring(0,3).toLowerCase())));
				level+=(stat*5);
				level+=(max*5);
			}

			int hit=CMParms.getParmPlus(newText,"hit");
			int man=CMParms.getParmPlus(newText,"man");
			int mv=CMParms.getParmPlus(newText,"mov");
			level+=(hit/5);
			level+=(man/5);
			level+=(mv/5);
		}
		
		return level;
	}
	
}
