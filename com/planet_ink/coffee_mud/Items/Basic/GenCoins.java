package com.planet_ink.coffee_mud.Items.Basic;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;

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
public class GenCoins extends GenItem implements Coins
{
	public String ID(){	return "GenCoins";}
	public int value(){	return envStats().ability();}
	protected String currency="";
	protected double denomination=1.0;
	
	public GenCoins()
	{
		super();
		myContainer=null;
		setMaterial(EnvResource.RESOURCE_GOLD);
		setNumberOfCoins(100);
		setCurrency("");
		setDenomination(BeanCounter.getLowestDenomination(""));
		setDescription("");
	}
	
	public String name()
	{
        return BeanCounter.getDenominationName(getCurrency(),getDenomination(),getNumberOfCoins());
	}
	public String displayText()
	{
        return BeanCounter.getDenominationName(getCurrency(),getDenomination(),getNumberOfCoins())+((getNumberOfCoins()==1)?" lies here.":"lie here.");
	}
	
	public void setDynamicMaterial()
	{
	    if((EnglishParser.containsString(name(),"note"))
	    ||(EnglishParser.containsString(name(),"bill"))
	    ||(EnglishParser.containsString(name(),"dollar")))
	        setMaterial(EnvResource.RESOURCE_PAPER);
	    else
		for(int i=0;i<EnvResource.RESOURCE_DESCS.length;i++)
		    if(EnglishParser.containsString(name(),EnvResource.RESOURCE_DESCS[i]))
		    {
		        setMaterial(EnvResource.RESOURCE_DATA[i][0]);
		        break;
		    }
		setDescription(BeanCounter.getConvertableDescription(getCurrency(),getDenomination()));
	}
	public long getNumberOfCoins(){return envStats().ability();}
	public void setNumberOfCoins(long number)
	{
	    if(number<Integer.MAX_VALUE)
		    baseEnvStats().setAbility((int)number); 
	    else
		    baseEnvStats().setAbility(Integer.MAX_VALUE); 
	    recoverEnvStats();
	}
	public double getDenomination(){return denomination;}
	public void setDenomination(double valuePerCoin){denomination=valuePerCoin; setDynamicMaterial();}
	public double getTotalValue(){return Util.mul(getDenomination(),getNumberOfCoins());}
	public String getCurrency(){return currency;}
	public void setCurrency(String named){currency=named; setDynamicMaterial();}

	public boolean isGeneric(){return true;}
	public void recoverEnvStats()
	{
		if(((material&EnvResource.MATERIAL_MASK)!=EnvResource.MATERIAL_CLOTH)
		&&((material&EnvResource.MATERIAL_MASK)!=EnvResource.MATERIAL_PAPER))
			baseEnvStats.setWeight((int)Math.round((new Integer(baseEnvStats().ability()).doubleValue()/100.0)));
		envStats=baseEnvStats.cloneStats();
		// import not to sup this, otherwise 'ability' makes it magical!
		for(int a=0;a<numEffects();a++)
		{
			Ability effect=fetchEffect(a);
			effect.affectEnvStats(this,envStats);
		}
	}

	public boolean putCoinsBack()
	{
		Coins alternative=null;
		if(owner() instanceof Room)
		{
			Room R=(Room)owner();
			for(int i=0;i<R.numItems();i++)
			{
				Item I=R.fetchItem(i);
				if((I!=null)
				   &&(I!=this)
				   &&(I instanceof Coins)
				   &&(((Coins)I).getDenomination()==getDenomination())
				   &&((Coins)I).getCurrency().equals(getCurrency())
				   &&(I.container()==container()))
				{
					alternative=(Coins)I;
					break;
				}
			}
		}
		else
		if(owner() instanceof MOB)
		{
			MOB M=(MOB)owner();
			if(container()==null)
			{
			    if((M.getMoney()+getNumberOfCoins())>Integer.MAX_VALUE)
			        M.setMoney(Integer.MAX_VALUE);
			    else
					M.setMoney((int)(M.getMoney()+getNumberOfCoins()));
				destroy();
				return true;
			}
			for(int i=0;i<M.inventorySize();i++)
			{
				Item I=M.fetchInventory(i);
				if((I!=null)
				   &&(I!=this)
				   &&(I instanceof Coins)
				   &&(((Coins)I).getDenomination()==getDenomination())
				   &&((Coins)I).getCurrency().equals(getCurrency())
				   &&(I.container()==container()))
				{
					alternative=(Coins)I;
					break;
				}
			}
		}
		if(alternative!=null)
		{
			alternative.setNumberOfCoins(alternative.getNumberOfCoins()+getNumberOfCoins());
			destroy();
			return true;
		}
		return false;
	}

	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((getCurrency().length()==0)&&(getDenomination()==0.0))
		switch(msg.targetMinor())
		{
		case CMMsg.TYP_REMOVE:
		case CMMsg.TYP_GET:
			if((msg.amITarget(this))||((msg.tool()==this)))
			{
				setContainer(null);
				msg.source().setMoney(msg.source().getMoney()+envStats().ability());
				unWear();
				destroy();
				if(!Util.bset(msg.targetCode(),CMMsg.MASK_OPTIMIZE))
					msg.source().location().recoverRoomStats();
			}
			break;
		default:
			break;
		}
	}
}
