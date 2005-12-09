package com.planet_ink.coffee_mud.utils;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;

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
public class BeanCounter
{
    protected static Hashtable currencies=new Hashtable();
    protected static Vector allCurrencyNames=new Vector();
    protected static Hashtable allCurrencyDenominationNames=new Hashtable();
    
	protected static final String defaultCurrencyDefinition=
	    "=1 gold coin(s);100 golden note(s);10000 whole note(s);1000000 Archon note(s)";
	protected static final String goldStandard=
	    "GOLD=0.01 copper piece(s) (cp);0.1 silver piece(s) (sp);1.0 gold piece(s) (gp);5.0 platinum piece(s) (pp)";
	protected static final String copperStandard=
	    "COPPER=1 copper bit(s) (cc);10 silver bit(s) (sc);100 gold bit(s) (gc);500 platinum bit(s) (pc)";
    
	private BeanCounter()
	{};
	
	public static void unloadCurrencySet(String currency)
	{
	    String code=currency.toUpperCase().trim();
	    int x=code.indexOf("=");
	    if(x>=0) code=code.substring(0,x).trim();
	    if((code.length()>0)&&(currencies.containsKey(code)))
	    {
	        allCurrencyNames.removeElement(code);
            currencies.remove(code);
            allCurrencyDenominationNames.remove(code);
	    }
	}
	
	public static DVector createCurrencySet(String currency)
	{
	    int x=currency.indexOf("=");
	    if(x<0) return null;
	    String code=currency.substring(0,x).trim().toUpperCase();
	    if(currencies.containsKey(code)) 
	        return (DVector)currencies.get(code);
        currency=currency.substring(x+1).trim();
        Vector V=Util.parseSemicolons(currency,true);
        DVector DV=new DVector(3);
        String s=null;
        String num=null;
        double d=0.0;
        Vector currencyNames=new Vector();
        for(int v=0;v<V.size();v++)
        {
            s=(String)V.elementAt(v);
            x=s.indexOf(" ");
            if(x<0) continue;
            num=s.substring(0,x).trim();
            if(Util.isDouble(num))
                d=Util.s_double(num);
            else
            if(Util.isInteger(num))
                d=new Integer(Util.s_int(num)).doubleValue();
            else
                continue;
            s=s.substring(x+1).trim();
            String shortName="";
            if(s.endsWith(")"))
            {
                x=s.lastIndexOf(" ");
                if((x>0)&&(x<s.length()-1)&&(s.charAt(x+1)=='('))
                {
                    shortName=s.substring(x+2,s.length()-1).trim();
                    s=s.substring(0,x).trim();
                }
            }
            if((s.length()>0)&&(d>0.0))
            {
                int insertAt=-1;
                for(int i=0;i<DV.size();i++)
                    if(((Double)DV.elementAt(i,1)).doubleValue()>d)
                    { insertAt=i; break;}
                if((insertAt<0)||(insertAt>=DV.size()))
	                DV.addElement(new Double(d),s,shortName);
                else
                    DV.insertElementAt(insertAt,new Double(d),s,shortName);
                currencyNames.addElement(s);
                if(shortName.length()>0)
                    currencyNames.add(shortName);
            }
        }
        currencies.put(code,DV);
        allCurrencyNames.addElement(code);
        allCurrencyDenominationNames.put(code,currencyNames);
        return DV;
	}
	
	public static DVector getCurrencySet(String currency)
	{
	    if((currency==null)||(currencies==null)) return null; 
	    if((currency.length()==0)&&(!currencies.containsKey("")))
	    {
	        createCurrencySet(defaultCurrencyDefinition);
	        createCurrencySet(goldStandard);
	        createCurrencySet(copperStandard);
	    }
	    String code=currency.toUpperCase().trim();
	    int x=code.indexOf("=");
	    if(x<0)
	    {
	        if(currencies.containsKey(code))
	            return (DVector)currencies.get(code);
	        return null;
	    }
        code=code.substring(0,x).trim();
        if(currencies.containsKey(code))
            return (DVector)currencies.get(code);
        return createCurrencySet(currency);
	}
	
	public static Vector getAllCurrencies()
	{ return allCurrencyNames;}
	
	public static Vector getDenominationNameSet(String currency)
	{ 
	    if(allCurrencyDenominationNames.containsKey(currency))
	        return (Vector)allCurrencyDenominationNames.get(currency);
        return new Vector();
	}
	
	public static double lowestAbbreviatedDenomination(String currency)
	{
	    DVector DV=getCurrencySet(currency);
	    if(DV!=null)
	    {
	        for(int i=0;i<DV.size();i++)
	            if(((String)DV.elementAt(i,3)).length()>0)
	                return ((Double)DV.elementAt(i,1)).doubleValue();
	        return getLowestDenomination(currency);
	    }
	    return 1.0;
	}
	
	public static double lowestAbbreviatedDenomination(String currency, double absoluteAmount)
	{
	    DVector DV=getCurrencySet(currency);
	    if(DV!=null)
	    {
	        double absoluteLowest=lowestAbbreviatedDenomination(currency);
	        double lowestDenom=Double.MAX_VALUE;
	        double diff=0.0;
	        double denom=0.0;
	        long num=0;
	        for(int i=DV.size()-1;i>=0;i--)
	            if(((String)DV.elementAt(i,3)).length()>0)
	            {
	                denom=((Double)DV.elementAt(i,1)).doubleValue();
	                if(denom<absoluteAmount)
	                {
	                    num=Math.round(Math.floor(absoluteAmount/denom));
		                diff=Math.abs(absoluteAmount-Util.mul(denom,num));
		                if(((diff/absoluteAmount)<0.05)&&(num>=10))
		                {
		                    lowestDenom=denom;
		                    break;
		                }
	                }
	            }
	        if(lowestDenom==Double.MAX_VALUE) lowestDenom=absoluteLowest;
	        return lowestDenom;
	    }
        return 1.0;
	}
	
	public static double abbreviatedRePrice(MOB shopkeeper, double absoluteAmount)
	{  return abbreviatedRePrice(getCurrency(shopkeeper),absoluteAmount);}
	public static double abbreviatedRePrice(String currency, double absoluteAmount)
	{   
	    double lowDenom=lowestAbbreviatedDenomination(currency,absoluteAmount);
	    long lowAmt=Math.round(absoluteAmount/lowDenom);
	    return Util.mul(lowDenom,lowAmt);
	}
	public static String abbreviatedPrice(MOB shopkeeper, double absoluteAmount)
	{ return abbreviatedPrice(getCurrency(shopkeeper),absoluteAmount);}
	public static String abbreviatedPrice(String currency, double absoluteAmount)
	{
	    double lowDenom=lowestAbbreviatedDenomination(currency,absoluteAmount);
	    long lowAmt=Math.round(absoluteAmount/lowDenom);
	    String denominationShortCode=getDenominationShortCode(currency,lowDenom);
	    if(denominationShortCode.length()==0)
		    return ""+lowAmt;
	    return lowAmt+denominationShortCode;
	}
	
	public static String getDenominationShortCode(String currency, double denomination)
	{
	    DVector DV=getCurrencySet(currency);
	    if(DV==null) return "";
	    for(int d=0;d<DV.size();d++)
	        if(((Double)DV.elementAt(d,1)).doubleValue()==denomination)
	            return (String)DV.elementAt(d,3);
	    return "";
	}
	
	public static double getLowestDenomination(String currency)
	{
	    DVector DV=getCurrencySet(currency);
	    if(DV==null) return 1.0;
	    return ((Double)DV.elementAt(0,1)).doubleValue();
	}

	public static String getDenominationName(String currency)
	{ return getDenominationName(currency,getLowestDenomination(currency));}
	
	public static String getDenominationName(String currency, 
	        								 double denomination, 
	        								 long number)
	{
	    String s=getDenominationName(currency,denomination);
	    if(s.toUpperCase().endsWith("(S)"))
	    {
	        if(number>1)
	            return number+" "+s.substring(0,s.length()-3)+s.charAt(s.length()-2);
            return number+" "+s.substring(0,s.length()-3);
	    }
        return number+" "+s;
	}

	public static double getBestDenomination(String currency, double absoluteValue)
	{
		DVector DV=getCurrencySet(currency);
		double denom=0.0;
		if(DV!=null)
		{
			double low=getLowestDenomination(currency);
			for(int d=DV.size()-1;d>=0;d--)
			{
			    denom=((Double)DV.elementAt(d,1)).doubleValue();
			    if((denom<=absoluteValue)
			    &&(absoluteValue-(Math.floor(absoluteValue/denom)*denom)<low))
			        return denom;
			}
		}
		return denom;
	}
	public static Vector getBestDenominations(String currency, double absoluteValue)
	{
		DVector DV=getCurrencySet(currency);
		Vector V=new Vector();
		if(DV!=null)
		for(int d=DV.size()-1;d>=0;d--)
		{
		    double denom=((Double)DV.elementAt(d,1)).doubleValue();
		    if(denom<=absoluteValue)
		    {
		        long number=Math.round(Math.floor(absoluteValue/denom));
		        if(number>0)
		        {
		            V.addElement(new Double(denom));
		            absoluteValue-=Util.mul(denom,number);
		        }
		    }
		}
		return V;
	}
	public static String getConvertableDescription(String currency, double denomination)
	{
	    double low=getLowestDenomination(currency);
	    if(low==denomination) return "";
	    return "Equal to "+getDenominationName(currency,low,Math.round(Math.floor(denomination/low)))+".";
	}
	
	public static String getDenominationName(String currency, double denomination)
	{
	    DVector DV=getCurrencySet(currency);
	    if((DV==null)||(DV.size()==0)) DV=getCurrencySet("");
	    if((DV==null)||(DV.size()==0)) return "unknown!";
	    int closestX=DV.indexOf(new Double(denomination));
	    if(closestX<0)
		    for(int i=0;i<DV.size();i++)
		        if((((Double)DV.elementAt(i,1)).doubleValue()<=denomination)
		        &&((closestX<0)||(((Double)DV.elementAt(i,1)).doubleValue()>=((Double)DV.elementAt(closestX,1)).doubleValue())))
		            closestX=i;
	    if(closestX<0)
	        return "unknown";
        return (String)DV.elementAt(closestX,2);
	}

	public static String nameCurrencyShort(MOB mob, double absoluteValue)
	{   return nameCurrencyShort(getCurrency(mob),absoluteValue);}
	public static String nameCurrencyShort(MOB mob, int absoluteValue)
	{   return nameCurrencyShort(getCurrency(mob),new Integer(absoluteValue).doubleValue());}
	public static String nameCurrencyShort(String currency, double absoluteValue)
	{
		double denom=getBestDenomination(currency,absoluteValue);
		if(denom>0.0)
		    return getDenominationName(currency,denom,Math.round(Math.floor(absoluteValue/denom)));
	    return getDenominationName(currency,denom,Math.round(Math.floor(absoluteValue)));
	}
	public static String nameCurrencyLong(MOB mob, double absoluteValue)
	{   return nameCurrencyLong(getCurrency(mob),absoluteValue);}
	public static String nameCurrencyLong(MOB mob, int absoluteValue)
	{   return nameCurrencyLong(getCurrency(mob),new Integer(absoluteValue).doubleValue());}
	public static String nameCurrencyLong(String currency, double absoluteValue)
	{
	    StringBuffer str=new StringBuffer("");
		Vector V=getBestDenominations(currency,absoluteValue);
		for(int d=0;d<V.size();d++)
		{
		    double denom=((Double)V.elementAt(d)).doubleValue();
	        long number=Math.round(Math.floor(absoluteValue/denom));
	        String name=getDenominationName(currency,denom,number);
            absoluteValue-=Util.mul(denom,number);
            if(str.length()>0) str.append(", ");
            str.append(name);
		}
		return str.toString();
	}
	
	public static Coins makeBestCurrency(MOB mob, 
										 double absoluteValue, 
										 Environmental owner,
										 Item container)
	{ return makeBestCurrency(getCurrency(mob),absoluteValue,owner,container);}
	public static Coins makeBestCurrency(String currency, 
	        							double absoluteValue, 
	        							Environmental owner,
	        							Item container)
	{
	    Coins C=makeBestCurrency(currency,absoluteValue);
	    if(C!=null)
	    {
		    if(owner instanceof Room)
		        ((Room)owner).addItem(C);
		    if(owner instanceof MOB)
		        ((MOB)owner).addInventory(C);
		    C.setContainer(container);
		    C.recoverEnvStats();
	    }
	    return C;
	}
	public static Coins makeBestCurrency(MOB mob, double absoluteValue)
	{ return makeBestCurrency(getCurrency(mob),absoluteValue);}
	public static Coins makeCurrency(String currency, double denomination, long numberOfCoins)
	{
	    if(numberOfCoins>0)
	    {
		    Coins C=(Coins)CMClass.getItem("StdCoins");
		    C.setCurrency(currency);
		    C.setDenomination(denomination);
		    C.setNumberOfCoins(numberOfCoins);
		    C.recoverEnvStats();
		    return C;
	    }
	    return null;
	}
	public static Coins makeBestCurrency(String currency, double absoluteValue)
	{
	    double denom=getBestDenomination(currency,absoluteValue);
	    if(denom==0.0) return null;
	    long number=Math.round(Math.floor(absoluteValue/denom));
	    if(number>0)
		    return makeCurrency(currency,denom,number);
	    return null;
	}

	public static Vector makeAllCurrency(String currency, double absoluteValue)
	{
	    Vector V=new Vector();
	    Vector DV=getBestDenominations(currency,absoluteValue);
		for(int d=0;d<DV.size();d++)
		{
		    double denom=((Double)DV.elementAt(d)).doubleValue();
		    long number=Math.round(Math.floor(absoluteValue/denom));
		    if(number>0)
		    {
			    Coins C=makeCurrency(currency,denom,number);
			    if(C!=null)
			    {
				    absoluteValue-=C.getTotalValue();
		            V.addElement(C);
			    }
		    }
		}
		return V;
	}
	
	public static void addMoney(MOB customer, int absoluteValue)
	{  addMoney(customer,BeanCounter.getCurrency(customer),new Integer(absoluteValue).doubleValue());}
	public static void addMoney(MOB customer, double absoluteValue)
	{  addMoney(customer,BeanCounter.getCurrency(customer),absoluteValue);}
	public static void addMoney(MOB customer, String currency,int absoluteValue)
	{  addMoney(customer,currency,new Integer(absoluteValue).doubleValue());}
	public static void addMoney(MOB mob, String currency, double absoluteValue)
	{
	    if(mob==null) return;
		Vector V=makeAllCurrency(currency,absoluteValue);
	    for(int i=0;i<V.size();i++)
	    {
	        Coins C=(Coins)V.elementAt(i);
	        mob.addInventory(C);
	        C.putCoinsBack();
		}
		mob.recoverEnvStats();
	}
	
    public static void giveSomeoneMoney(MOB recipient, double absoluteValue)
    {  giveSomeoneMoney(recipient,recipient,getCurrency(recipient),absoluteValue); }
    public static void giveSomeoneMoney(MOB recipient, String currency, double absoluteValue)
    {  giveSomeoneMoney(recipient,recipient,currency,absoluteValue); }
	public static void giveSomeoneMoney(MOB banker, MOB customer, double absoluteValue)
	{  giveSomeoneMoney(banker,customer,getCurrency(banker),absoluteValue); }
	public static void giveSomeoneMoney(MOB banker, MOB customer, String currency, double absoluteValue)
	{
		if(banker==null) banker=customer;
		if(banker==customer) 
		{
		    addMoney(customer,currency,absoluteValue);
		    return;
		}
        
		Vector V=makeAllCurrency(currency,absoluteValue);
	    for(int i=0;i<V.size();i++)
	    {
	        Coins C=(Coins)V.elementAt(i);
	        banker.addInventory(C);
			FullMsg newMsg=new FullMsg(banker,customer,C,CMMsg.MSG_GIVE,"<S-NAME> give(s) "+C.Name()+" to <T-NAMESELF>.");
			if(banker.location().okMessage(banker,newMsg))
			{
				banker.location().send(banker,newMsg);
				C.putCoinsBack();
		    }
			else
				CommonMsgs.drop(banker,C,true,false);
	    }
		banker.recoverEnvStats();
		customer.recoverEnvStats();
	}

	public static void bankLedger(String bankName, String owner, String explanation)
	{
		Vector V=CMClass.DBEngine().DBReadData(owner,"LEDGER-"+bankName,"LEDGER-"+bankName+"/"+owner);
		if((V!=null)&&(V.size()>0))
		{
			Vector D=(Vector)V.firstElement();
			String last=(String)D.elementAt(3);
			if(last.length()>4096)
			{
			    int x=last.indexOf(";|;",1024);
			    if(x>=0) last=last.substring(x+3);
			}
			CMClass.DBEngine().DBDeleteData(owner,(String)D.elementAt(1),(String)D.elementAt(2));
			CMClass.DBEngine().DBCreateData(owner,(String)D.elementAt(1),(String)D.elementAt(2),last+explanation+";|;");
		}
		else
			CMClass.DBEngine().DBCreateData(owner,"LEDGER-"+bankName,"LEDGER-"+bankName+"/"+owner,explanation+";|;");
	}
	
	public static boolean modifyBankGold(String bankName, 
	        							 String owner,
	        							 String explanation,
	        							 String currency,
	        							 double absoluteAmount)
	{
		Vector V=CMClass.DBEngine().DBReadAllPlayerData(owner);
		for(int v=0;v<V.size();v++)
		{
			Vector D=(Vector)V.elementAt(v);
			String last=(String)D.elementAt(3);
			if(last.startsWith("COINS;"))
			{
				if((bankName==null)||(bankName.length()==0)||(bankName.equals(D.elementAt(1))))
				{
					Coins C=(Coins)CMClass.getItem("StdCoins");
					CoffeeMaker.setPropertiesStr(C,last.substring(6),true);
					if((C.getDenomination()==0.0)&&(C.getNumberOfCoins()>0)) 
					    C.setDenomination(1.0);
					C.recoverEnvStats();
					double value=C.getTotalValue();
					if((absoluteAmount>0.0)||(value>=(-absoluteAmount)))
					{
					    C=BeanCounter.makeBestCurrency(currency,value+absoluteAmount);
						CMClass.DBEngine().DBDeleteData(owner,(String)D.elementAt(1),(String)D.elementAt(2));
						if(C!=null)
							CMClass.DBEngine().DBCreateData(owner,(String)D.elementAt(1),""+C+Math.random(),"COINS;"+CoffeeMaker.getPropertiesStr(C,true));
						bankLedger(bankName,owner,explanation);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean modifyThisAreaBankGold(Area A, 
	        									 HashSet triedBanks, 
	        									 String owner,
	        									 String explanation,
	        									 String currency,
	        									 double absoluteAmount)
	{
		Room R=null;
		for(Enumeration e=A.getMetroMap();e.hasMoreElements();)
		{
			R=(Room)e.nextElement();
			for(int m=0;m<R.numInhabitants();m++)
			{
				MOB M=R.fetchInhabitant(m);
				if((M instanceof Banker)&&(!triedBanks.contains(((Banker)M).bankChain())))
				{
					if(modifyBankGold(((Banker)M).bankChain(),owner,explanation,currency,absoluteAmount))
						return true;
					triedBanks.add(((Banker)M).bankChain());
				}
			}
		}
		return false;
	}
	
	public static boolean modifyLocalBankGold(Area A, 
	        								  String owner,
	        								  String explanation,
	        								  String currency,
	        								  double absoluteAmount)
	{
		HashSet triedBanks=new HashSet();
		if(modifyThisAreaBankGold(A,triedBanks,owner,explanation,currency,absoluteAmount))
			return true;
		for(Enumeration e=A.getParents();e.hasMoreElements();)
		{
			Area A2=(Area)e.nextElement();
			if(modifyThisAreaBankGold(A2,triedBanks,owner,explanation,currency,absoluteAmount))
				return true;
		}
		return modifyBankGold(null,owner,explanation,currency,absoluteAmount);
	}

	public static void subtractMoneyGiveChange(MOB banker, MOB mob, int absoluteAmount)
	{ subtractMoneyGiveChange(banker,mob,new Integer(absoluteAmount).doubleValue());}
	public static void subtractMoneyGiveChange(MOB banker, MOB mob, double absoluteAmount)
	{ subtractMoneyGiveChange(banker, mob,(banker!=null)?getCurrency(banker):getCurrency(mob),absoluteAmount);}
	public static void subtractMoneyGiveChange(MOB banker, MOB mob, String currency, double absoluteAmount)
	{
		if(mob==null) return;
		double myMoney=getTotalAbsoluteValue(mob,currency);
		Vector V=getStandardCurrency(mob,currency);
		for(int v=0;v<V.size();v++)
		    ((Item)V.elementAt(v)).destroy();
		if(myMoney>=absoluteAmount)
		    myMoney-=absoluteAmount;
		else
		    myMoney=0.0;
		if(myMoney>0.0)
			giveSomeoneMoney(banker,mob,currency,myMoney);
	}
	
	public static void setMoney(MOB mob, double absoluteAmount)
	{ 
	    clearZeroMoney(mob,null);
	    BeanCounter.addMoney(mob,getCurrency(mob),absoluteAmount);
	}
	public static void setMoney(MOB mob, String currency, double absoluteAmount)
	{
	    clearZeroMoney(mob,currency);
	    BeanCounter.addMoney(mob,currency,absoluteAmount);
	}
	
	public static void subtractMoney(MOB mob, double absoluteAmount)
	{ subtractMoney(mob,getCurrency(mob),absoluteAmount);}
	public static void subtractMoney(MOB mob, String currency, double absoluteAmount)
	{
		if(mob==null) return;
		double myMoney=getTotalAbsoluteValue(mob,currency);
		Vector V=getStandardCurrency(mob,currency);
		for(int v=0;v<V.size();v++)
		    ((Item)V.elementAt(v)).destroy();
		if(myMoney>=absoluteAmount)
		    myMoney-=absoluteAmount;
		else
		    myMoney=0.0;
		if(myMoney>0.0)
			addMoney(mob,currency,myMoney);
	}
	
	public static int getMoney(MOB mob)
	{
	    if(mob==null) return 0;
	    long money=mob.getMoney();
	    if(money>0) return mob.getMoney();
	    Vector V=getStandardCurrency(mob,null);
	    for(int i=0;i<V.size();i++)
	        money+=Math.round(((Coins)V.elementAt(i)).getTotalValue());
	    if(money>Integer.MAX_VALUE) return Integer.MAX_VALUE;
	    return (int)money;
	}
	
	public static void setMoney(MOB mob, int amount)
	{
	    if(mob==null) return;
	    clearZeroMoney(mob,null);
	    mob.setMoney(amount);
	}
	
	public static void clearZeroMoney(MOB mob, String currency)
	{
	    if(mob==null) return;
	    mob.setMoney(0);
	    clearInventoryMoney(mob,currency);
	}

	public static void clearInventoryMoney(MOB mob, String currency)
	{
	    if(mob==null) return;
	    Vector clear=null;
	    Item I=null;
	    for(int i=0;i<mob.inventorySize();i++)
	    {
	        I=mob.fetchInventory(i);
	        if(I instanceof Coins)
	        {
	            if(clear==null) clear=new Vector();
	            if(currency==null)
		            clear.addElement(I);
	            else
	            if(((Coins)I).getCurrency().equalsIgnoreCase(currency))
	                clear.addElement(I);
	        }
	    }
	    if(clear!=null)
	        for(int i=0;i<clear.size();i++)
	            ((Item)clear.elementAt(i)).destroy();
	}
	
	public static void subtractMoney(MOB mob, double denomination, double absoluteAmount)
	{ subtractMoney(mob,getCurrency(mob),denomination,absoluteAmount);}
	public static void subtractMoney(MOB mob, String currency, double denomination, double absoluteAmount)
	{
		if(mob==null) return;
		Vector V=getStandardCurrency(mob,currency);
		Coins C=null;
		for(int v=0;v<V.size();v++)
		{
		    C=(Coins)V.elementAt(v);
		    if(C.getDenomination()==denomination)
		    {
		        if(C.getTotalValue()>absoluteAmount)
		        {
		            C.setNumberOfCoins(Math.round(Math.floor((C.getTotalValue()-absoluteAmount)/denomination)));
		            C.text();
		            break;
		        }
	            absoluteAmount-=C.getTotalValue();
	            C.destroy();
		    }
		}
	}

	public static Vector getStandardCurrency(MOB mob, String currency)
	{
	    Vector V=new Vector();
		if(mob==null) return V;
		if((currency==null)||((currency.equals(getCurrency(mob)))&&(mob.getMoney()>0)))
		{
		    addMoney(mob,getCurrency(mob),new Integer(mob.getMoney()).doubleValue());
		    mob.setMoney(0);
		}
		for(int i=0;i<mob.inventorySize();i++)
		{
			Item I=mob.fetchInventory(i);
			if((I!=null)
			&&(I instanceof Coins)
			&&((currency==null)||((Coins)I).getCurrency().equalsIgnoreCase(currency))
			&&(I.container()==null))
				V.addElement(I);
		}
		return V;
	}
	
	public static long getNumberOfCoins(MOB mob, String currency, double denomination)
	{
	    Vector V=getStandardCurrency(mob,currency);
	    long gold=0;
	    for(int v=0;v<V.size();v++)
	        if(((Coins)V.elementAt(v)).getDenomination()==denomination)
	            gold+=((Coins)V.elementAt(v)).getNumberOfCoins();
	    return gold;
	}

	public static String getCurrency(Environmental E)
	{
	    if(E instanceof MOB)
	    {
		    if(((MOB)E).getStartRoom()!=null)
		        return getCurrency(((MOB)E).getStartRoom());
		    else
		    if(((MOB)E).location()!=null)
			    return getCurrency(((MOB)E).location());
	    }
	    else
	    if(E instanceof Room)
	        return getCurrency(((Room)E).getArea());
        else
        if(E instanceof Coins)
            return ((Coins)E).getCurrency();
	    else
	    if(E instanceof Area)
	    {
	        String s=((Area)E).getCurrency();
	        if(s.length()==0)
		        for(int p=0;p<((Area)E).getNumParents();p++)
		        {
		            s=getCurrency(((Area)E).getParent(p));
			        if(s.length()>0) 
			            break;
		        }
	        int x=s.indexOf("=");
	        if(x<0) return s.toUpperCase().trim();
	        return s.substring(0,x).toUpperCase().trim();
	    }
	    return "";
	}
	
	public static double getTotalAbsoluteValue(MOB mob, String currency)
	{
		double money=0.0;
	    Vector V=getStandardCurrency(mob,currency);
	    for(int v=0;v<V.size();v++)
			money+=((Coins)V.elementAt(v)).getTotalValue();
		return money;
	}
	
	public static double getTotalAbsoluteNativeValue(MOB mob)
	{
		double money=0.0;
	    Vector V=getStandardCurrency(mob,getCurrency(mob));
	    for(int v=0;v<V.size();v++)
			money+=((Coins)V.elementAt(v)).getTotalValue();
		return money;
	}
	public static double getTotalAbsoluteShopKeepersValue(MOB mob, MOB shopkeeper)
	{
		double money=0.0;
	    Vector V=getStandardCurrency(mob,getCurrency(shopkeeper));
	    for(int v=0;v<V.size();v++)
			money+=((Coins)V.elementAt(v)).getTotalValue();
		return money;
	}
    
    public static double getTotalAbsoluteValueAllCurrencies(MOB mob)
    { return BeanCounter.getTotalAbsoluteValue(mob,null);}
}
