package com.planet_ink.coffee_mud.common;
import com.planet_ink.coffee_mud.interfaces.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.Modifier;
import com.planet_ink.coffee_mud.utils.Log;
import com.planet_ink.coffee_mud.utils.INI;
import com.planet_ink.coffee_mud.utils.CoffeeUtensils;


public class CMClass extends ClassLoader
{
	private static Vector races=new Vector();
	private static Vector charClasses=new Vector();
	private static Vector MOBs=new Vector();
	private static Vector abilities=new Vector();
	private static Vector locales=new Vector();
	private static Vector exits=new Vector();
	private static Vector items=new Vector();
	private static Vector behaviors=new Vector();
	private static Vector weapons=new Vector();
	private static Vector armor=new Vector();
	private static Vector miscMagic=new Vector();
	private static Vector areaTypes=new Vector();
	private static Hashtable extraCmds=new Hashtable();
	private static final String[] names={
		"RACE","CHARCLASS","MOB","ABILITY","LOCALE","EXIT","ITEM","BEHAVIOR",
		"CLAN","WEAPON","ARMOR","MISCMAGIC","AREA","COMMAND"
		};
	private static final String[] ancestors={
		"com.planet_ink.coffee_mud.interfaces.Race",
		"com.planet_ink.coffee_mud.interfaces.CharClass",
		"com.planet_ink.coffee_mud.interfaces.MOB",
		"com.planet_ink.coffee_mud.interfaces.Ability",
		"com.planet_ink.coffee_mud.interfaces.Room",
		"com.planet_ink.coffee_mud.interfaces.Exit",
		"com.planet_ink.coffee_mud.interfaces.Item",
		"com.planet_ink.coffee_mud.interfaces.Behavior",
		"com.planet_ink.coffee_mud.interfaces.Clan",
		"com.planet_ink.coffee_mud.interfaces.Weapon",
		"com.planet_ink.coffee_mud.interfaces.Armor",
		"com.planet_ink.coffee_mud.interfaces.MiscMagic",
		"com.planet_ink.coffee_mud.interfaces.Area",
		"com.planet_ink.coffee_mud.interfaces.Command"
		};

	public static Enumeration races(){return races.elements();}
	public static Race randomRace(){return (Race)races.elementAt((int)Math.round(Math.floor(Math.random()*new Integer(races.size()).doubleValue())));}
	public static Enumeration charClasses(){return charClasses.elements();}
	public static CharClass randomCharClass(){return (CharClass)charClasses.elementAt((int)Math.round(Math.floor(Math.random()*new Integer(charClasses.size()).doubleValue())));}
	public static Enumeration mobTypes(){return MOBs.elements();}
	public static Enumeration locales(){return locales.elements();}
	public static Enumeration exits(){return exits.elements();}
	public static Enumeration behaviors(){return behaviors.elements();}
	public static Enumeration items(){return items.elements();}
	public static Enumeration weapons(){return weapons.elements();}
	public static Enumeration armor(){return armor.elements();}
	public static Enumeration miscMagic(){return miscMagic.elements();}
	public static Enumeration areaTypes(){return areaTypes.elements();}
	public static Enumeration extraCmds(){return extraCmds.elements();}
	public static Enumeration abilities(){return abilities.elements();}
	public static Ability randomAbility(){ return (Ability)abilities.elementAt((int)Math.round(Math.floor(Math.random()*new Integer(abilities.size()).doubleValue())));}
	
	public static Item getItem(String calledThis)
	{
		Item thisItem=(Item)getEnv(items,calledThis);
		if(thisItem==null)
			thisItem=(Item)getEnv(armor,calledThis);
		if(thisItem==null)
			thisItem=(Item)getEnv(weapons,calledThis);
		if(thisItem==null)
			thisItem=(Item)getEnv(miscMagic,calledThis);
		return thisItem;
	}
	public static Item sampleItem(){if(items.size()>0) return (Item)items.firstElement();	return null;}
	
	public static Command findExtraCommand(String word, boolean exactOnly)
	{
		Command C=(Command)extraCmds.get(word.trim().toUpperCase());
		if((exactOnly)||(C!=null)) return C;
		word=word.toUpperCase();
		for(Enumeration e=extraCmds.keys();e.hasMoreElements();)
		{
			String key=(String)e.nextElement();
			if(key.toUpperCase().startsWith(word))
				return (Command)extraCmds.get(key);
		}
		return null;
	}

	
	public static boolean delClass(Object O)
	{
		if(races.contains(O)){ races.removeElement(O); return true;}
		if(charClasses.contains(O)){ charClasses.removeElement(O); return true;}
		if(MOBs.contains(O)){ MOBs.removeElement(O); return true;}
		if(abilities.contains(O)){ abilities.removeElement(O); return true;}
		if(locales.contains(O)){ locales.removeElement(O); return true;}
		if(exits.contains(O)){ exits.removeElement(O); return true;}
		if(items.contains(O)){ items.removeElement(O); return true;}
		if(behaviors.contains(O)){ behaviors.removeElement(O); return true;}
		if(weapons.contains(O)){ weapons.removeElement(O); return true;}
		if(armor.contains(O)){ armor.removeElement(O); return true;}
		if(miscMagic.contains(O)){ miscMagic.removeElement(O); return true;}
		if(areaTypes.contains(O)){ areaTypes.removeElement(O); return true;}
		return false;
	}
	
	public static int classCode(String name)
	{
		for(int i=0;i<names.length;i++)
		{
			if(names[i].toUpperCase().startsWith(name.toUpperCase()))
				return i;
		}
		return -1;
	}
	public static boolean loadClass(String name, String path)
	{
		Object set=null;
		int code=classCode(name);
		switch(code)
		{
		case 0: set=races; break;
		case 1: set=charClasses; break;
		case 2: set=MOBs; break;
		case 3: set=abilities; break;
		case 4: set=locales; break;
		case 5: set=exits; break;
		case 6: set=items; break;
		case 7: set=behaviors; break;
		case 8: break;
		case 9: set=weapons; break;
		case 10: set=armor; break;
		case 11: set=miscMagic; break;
		case 12: set=areaTypes; break;
		case 13: set=extraCmds; break;
		}
		if(set==null) return false;
		
		loadListToObj(set,path,"",(path.indexOf(File.separatorChar)>0),ancestors[code]);
		switch(code)
		{
		case 0: races=new Vector(new TreeSet(races)); break;
		case 1: charClasses=new Vector(new TreeSet(charClasses)); break;
		case 2: MOBs=new Vector(new TreeSet(MOBs)); break;
		case 3: abilities=new Vector(new TreeSet(abilities)); break;
		case 4: locales=new Vector(new TreeSet(locales)); break;
		case 5: exits=new Vector(new TreeSet(exits)); break;
		case 6: items=new Vector(new TreeSet(items)); break;
		case 7: behaviors=new Vector(new TreeSet(behaviors)); break;
		case 8: break;
		case 9: weapons=new Vector(new TreeSet(weapons)); break;
		case 10: armor=new Vector(new TreeSet(armor)); break;
		case 11: miscMagic=new Vector(new TreeSet(miscMagic)); break;
		case 12: areaTypes=new Vector(new TreeSet(areaTypes)); break;
		}
		return true;
	}
	
	public static Object getClass(String calledThis)
	{
		Object thisItem=getGlobal(races,calledThis);
		if(thisItem==null) thisItem=getGlobal(charClasses,calledThis);
		if(thisItem==null) thisItem=getGlobal(MOBs,calledThis);
		if(thisItem==null) thisItem=getGlobal(abilities,calledThis);
		if(thisItem==null) thisItem=getGlobal(locales,calledThis);
		if(thisItem==null) thisItem=getGlobal(exits,calledThis);
		if(thisItem==null) thisItem=getGlobal(items,calledThis);
		if(thisItem==null) thisItem=getGlobal(behaviors,calledThis);
		if(thisItem==null) thisItem=getGlobal(weapons,calledThis);
		if(thisItem==null) thisItem=getGlobal(armor,calledThis);
		if(thisItem==null) thisItem=getGlobal(miscMagic,calledThis);
		if(thisItem==null) thisItem=getGlobal(areaTypes,calledThis);
		return thisItem;
	}
	
	public static Environmental getUnknown(String calledThis)
	{
		Environmental thisItem=getEnv(items,calledThis);
		if(thisItem==null)
			thisItem=getEnv(armor,calledThis);
		if(thisItem==null)
			thisItem=getEnv(weapons,calledThis);
		if(thisItem==null)
			thisItem=getEnv(miscMagic,calledThis);
		if(thisItem==null)
			thisItem=getEnv(MOBs,calledThis);
		if(thisItem==null)
			thisItem=getEnv(abilities,calledThis);
		
		if((thisItem==null)&&(charClasses.size()>0)&&(calledThis.length()>0))
			Log.sysOut("CMClass","Unknown Unknown '"+calledThis+"'.");
		
		return thisItem;
	}
	
	public static Weapon getWeapon(String calledThis)
	{
		return (Weapon)getEnv(weapons,calledThis);
	}
	public static Item getMiscMagic(String calledThis)
	{
		return (Item)getEnv(miscMagic,calledThis);
	}
	public static Armor getArmor(String calledThis)
	{
		return (Armor)getEnv(armor,calledThis);
	}
	public static Item getStdItem(String calledThis)
	{
		return (Item)getEnv(items,calledThis);
	}

	public static CharClass getCharClass(String calledThis)
	{
		CharClass thisItem= (CharClass)getGlobal(charClasses,calledThis);
		if((thisItem==null)&&(charClasses.size()>0))
		{
			for(int i=0;i<charClasses.size();i++)
			{
				CharClass C=(CharClass)charClasses.elementAt(i);
				if(C.name().equalsIgnoreCase(calledThis))
					return C;
			}
		}
		return thisItem;
	}
	public static Race getRace(String calledThis)
	{
		Race thisItem= (Race)getGlobal(races,calledThis);
		if((thisItem==null)&&(races.size()>0))
		{
			for(int i=0;i<races.size();i++)
				if(((Race)(races.elementAt(i))).name().equalsIgnoreCase(calledThis))
					return (Race)races.elementAt(i);
		}
		return thisItem;
	}
	public static Behavior getBehavior(String calledThis)
	{
		Behavior B=(Behavior)getGlobal(behaviors,calledThis);
		if(B!=null) 
			B=B.newInstance();
		return B;
	}
	public static Room getLocale(String calledThis)
	{
		return (Room)getEnv(locales,calledThis);
	}
	public static Area getAreaType(String calledThis)
	{
		return (Area)getEnv(areaTypes,calledThis);
	}
	public static Exit getExit(String calledThis)
	{
		return (Exit)getEnv(exits,calledThis);
	}
	public static MOB getMOB(String calledThis)
	{
		return (MOB)getEnv(MOBs,calledThis);
	}
	
	
	public static Object getGlobal(Vector list, String ID)
	{
		if(list.size()==0) return null;
		int start=0;
		int end=list.size()-1;
		while(start<=end)
		{
			int mid=(end+start)/2;
			int comp=classID(list.elementAt(mid)).compareToIgnoreCase(ID);
			if(comp==0) 
				return list.elementAt(mid);
			else
			if(comp>0) 
				end=mid-1;
			else
				start=mid+1;
			
		}
		return null;
	}
	public static Ability getAbility(String calledThis)
	{
		Ability A=(Ability)getGlobal(abilities,calledThis);
		if(A!=null)
			A=(Ability)A.newInstance();
		return A;
	}
	public static Ability findAbility(String calledThis)
	{
		Ability A=(Ability)getGlobal(abilities,calledThis);
		if(A==null)
			A=(Ability)CoffeeUtensils.fetchEnvironmental(abilities,calledThis,true);
		if(A==null)
			A=(Ability)CoffeeUtensils.fetchEnvironmental(abilities,calledThis,false);
		if(A!=null)A=(Ability)A.newInstance();
		return A;
	}
	public static Ability findAbility(String calledThis, CharStats charStats)
	{
		Ability A=(Ability)getGlobal(abilities,calledThis);
		if(A==null)
		{
			Vector As=new Vector();
			for(Enumeration e=abilities();e.hasMoreElements();)
			{
				A=(Ability)e.nextElement();
				for(int c=0;c<charStats.numClasses();c++)
				{
					CharClass C=charStats.getMyClass(c);
					if(CMAble.getQualifyingLevel(C.ID(),A.ID())>=0)
					{	As.addElement(A); break;}
				}
			}
			A=(Ability)CoffeeUtensils.fetchEnvironmental(As,calledThis,true);
			if(A==null)
				A=(Ability)CoffeeUtensils.fetchEnvironmental(As,calledThis,false);
		}
		if(A!=null)A=(Ability)A.newInstance();
		return A;
	}

	public static Environmental getEnv(Vector fromThese, String calledThis)
	{
		Environmental E=(Environmental)getGlobal(fromThese,calledThis);
		if(E!=null) return E.newInstance();
		return E;
	}

	public static Object getGlobal(Hashtable fromThese, String calledThis)
	{
		Object o=fromThese.get(calledThis);
		if(o==null)
		{
			for(Enumeration e=fromThese.elements();e.hasMoreElements();)
			{
				o=e.nextElement();
				if(classID(o).equalsIgnoreCase(calledThis))
					return o;
			}
			return null;
		}
		return o;
	}

	public static boolean loadClasses(INI page)
	{
		String prefix="com"+File.separatorChar+"planet_ink"+File.separatorChar+"coffee_mud"+File.separatorChar;

		races=loadVectorListToObj(prefix+"Races"+File.separatorChar,page.getStr("RACES"),"com.planet_ink.coffee_mud.interfaces.Race");
		Log.sysOut("MUD","Races loaded      : "+races.size());
		if(races.size()==0) return false;

		charClasses=loadVectorListToObj(prefix+"CharClasses"+File.separatorChar,page.getStr("CHARCLASSES"),"com.planet_ink.coffee_mud.interfaces.CharClass");
		Log.sysOut("MUD","Classes loaded    : "+charClasses.size());
		if(charClasses.size()==0) return false;

		MOBs=loadVectorListToObj(prefix+"MOBS"+File.separatorChar,page.getStr("MOBS"),"com.planet_ink.coffee_mud.interfaces.MOB");
		Log.sysOut("MUD","MOB Types loaded  : "+MOBs.size());
		if(MOBs.size()==0) return false;

		exits=loadVectorListToObj(prefix+"Exits"+File.separatorChar,page.getStr("EXITS"),"com.planet_ink.coffee_mud.interfaces.Exit");
		Log.sysOut("MUD","Exit Types loaded : "+exits.size());
		if(exits.size()==0) return false;

		areaTypes=loadVectorListToObj(prefix+"Areas"+File.separatorChar,page.getStr("AREAS"),"com.planet_ink.coffee_mud.interfaces.Area");
		Log.sysOut("MUD","Area Types loaded : "+areaTypes.size());
		if(areaTypes.size()==0) return false;

		locales=loadVectorListToObj(prefix+"Locales"+File.separatorChar,page.getStr("LOCALES"),"com.planet_ink.coffee_mud.interfaces.Room");
		Log.sysOut("MUD","Locales loaded    : "+locales.size());
		if(locales.size()==0) return false;

		abilities=loadVectorListToObj(prefix+"Abilities"+File.separatorChar,page.getStr("ABILITIES"),"com.planet_ink.coffee_mud.interfaces.Ability");
		if(abilities.size()==0) return false;

		if((page.getStr("ABILITIES")!=null)
		&&(page.getStr("ABILITIES").toUpperCase().indexOf("%DEFAULT%")>=0))
		{
			Vector tempV;
			int size=0;
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Fighter"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			size=tempV.size();
			addV(tempV,abilities);
			
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Ranger"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			size+=tempV.size();
			addV(tempV,abilities);
			
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Paladin"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			size+=tempV.size();
			addV(tempV,abilities);
			
			size+=tempV.size();
			Log.sysOut("MUD","Fighter Skills    : "+size);
			addV(tempV,abilities);
		
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Druid"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			Log.sysOut("MUD","Chants loaded     : "+tempV.size());
			addV(tempV,abilities);
		
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Languages"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			Log.sysOut("MUD","Languages loaded  : "+tempV.size());
			addV(tempV,abilities);
		
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Properties"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			size=tempV.size();
			addV(tempV,abilities);
			
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Diseases"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			size+=tempV.size();
			addV(tempV,abilities);
			
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Poisons"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			size+=tempV.size();
			addV(tempV,abilities);
			
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Misc"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			size+=tempV.size();
			Log.sysOut("MUD","Properties loaded : "+size);
			addV(tempV,abilities);
		
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Prayers"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			Log.sysOut("MUD","Prayers loaded    : "+tempV.size());
			addV(tempV,abilities);
		
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Archon"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			size+=tempV.size();
			addV(tempV,abilities);
			
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Skills"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			size=tempV.size();
			addV(tempV,abilities);
			
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Thief"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			size+=tempV.size();
			addV(tempV,abilities);
			
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Common"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			size+=tempV.size();
			addV(tempV,abilities);
			
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Specializations"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			size+=tempV.size();
			addV(tempV,abilities);
			Log.sysOut("MUD","Skills loaded     : "+size);
		
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Songs"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			Log.sysOut("MUD","Songs loaded      : "+tempV.size());
			addV(tempV,abilities);
		
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Spells"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			Log.sysOut("MUD","Spells loaded     : "+tempV.size());
			addV(tempV,abilities);
		
			tempV=loadVectorListToObj(prefix+"Abilities"+File.separatorChar+"Traps"+File.separatorChar,"%DEFAULT%","com.planet_ink.coffee_mud.interfaces.Ability");
			Log.sysOut("MUD","Traps loaded      : "+tempV.size());
			addV(tempV,abilities);
			abilities=new Vector(new TreeSet(abilities));
		}

		items=loadVectorListToObj(prefix+"Items"+File.separatorChar,page.getStr("ITEMS"),"com.planet_ink.coffee_mud.interfaces.Item");
		Log.sysOut("MUD","Items loaded      : "+items.size());

		weapons=loadVectorListToObj(prefix+"Items"+File.separatorChar+"Weapons"+File.separatorChar,page.getStr("WEAPONS"),"com.planet_ink.coffee_mud.interfaces.Weapon");
		Log.sysOut("MUD","Weapons loaded    : "+weapons.size());

		armor=loadVectorListToObj(prefix+"Items"+File.separatorChar+"Armor"+File.separatorChar,page.getStr("ARMOR"),"com.planet_ink.coffee_mud.interfaces.Armor");
		Log.sysOut("MUD","Armor loaded      : "+armor.size());

		miscMagic=loadVectorListToObj(prefix+"Items"+File.separatorChar+"MiscMagic"+File.separatorChar,page.getStr("MISCMAGIC"),"com.planet_ink.coffee_mud.interfaces.MiscMagic");
		Log.sysOut("MUD","Magic Items loaded: "+miscMagic.size());
		
		if((items.size()+weapons.size()+armor.size()+miscMagic.size())==0) 
			return false;
		
		behaviors=loadVectorListToObj(prefix+"Behaviors"+File.separatorChar,page.getStr("BEHAVIORS"),"com.planet_ink.coffee_mud.interfaces.Behavior");
		Log.sysOut("MUD","Behaviors loaded  : "+behaviors.size());
		if(behaviors.size()==0) return false;

		Vector cmds=loadVectorListToObj(prefix+"Commands"+File.separatorChar+"extra"+File.separatorChar,page.getStr("COMMANDS"),"com.planet_ink.coffee_mud.interfaces.Command");
		if(cmds.size()>1)
		{
			Log.sysOut("MUD","XCommands loaded  : "+cmds.size());
			for(int c=0;c<cmds.size();c++)
			{
				Command C=(Command)cmds.elementAt(c);
				Vector wordList=C.getAccessWords();
				for(int w=0;w<wordList.size();w++)
				{
					String word=(String)wordList.elementAt(w);
					extraCmds.put(word.trim().toUpperCase(),C);
				}
			}
		}
		
		// misc startup stuff
		for(int c=0;c<charClasses.size();c++)
		{
			CharClass C=(CharClass)charClasses.elementAt(c);
			C.copyOf();
		}
		for(int r=0;r<races.size();r++)
		{
			Race R=(Race)races.elementAt(r);
			R.copyOf();
		}
		Vector genRaces=ExternalPlay.DBReadRaces();
		if(genRaces.size()>0)
		{
			int loaded=0;
			for(int r=0;r<genRaces.size();r++)
			{
				Race GR=((Race)getRace("GenRace").copyOf());
				GR.setRacialParms((String)((Vector)genRaces.elementAt(r)).elementAt(1));
				if(!GR.ID().equals("GenRace"))
				{
					addRace(GR);
					loaded++;
				}
			}
			if(loaded>0)
				Log.sysOut("MUD","GenRaces loaded   : "+loaded);
		}
		return true;
	}

	public static void addRace(Race GR)
	{
		for(int i=0;i<races.size();i++)
		{
			Race R=(Race)races.elementAt(i);
			if(R.ID().compareToIgnoreCase(GR.ID())>=0)
			{
				races.insertElementAt(GR,i);
				return;
			}
		}
		races.addElement(GR);
	}
	public static void delRace(Race R)
	{
		races.removeElement(R);
	}
	
	
	public static int addV(Vector addMe, Vector toMe)
	{
		for(int v=0;v<addMe.size();v++)
			toMe.addElement(addMe.elementAt(v));
		return addMe.size();
	}
	public static int addH(Hashtable addMe, Hashtable toMe)
	{
		for(Enumeration e=addMe.keys();e.hasMoreElements();)
		{
			Object key=(String)e.nextElement();
			Object O=addMe.get(key);
			if(!toMe.containsKey(key))
				toMe.put(key,O);
		}
		return addMe.size();
	}

	public static void unload()
	{
		races=new Vector();
		charClasses=new Vector();
		MOBs=new Vector();
		abilities=new Vector();
		locales=new Vector();
		exits=new Vector();
		items=new Vector();
		behaviors=new Vector();
		weapons=new Vector();
		armor=new Vector();
		miscMagic=new Vector();
		areaTypes=new Vector();
	}

	public static Hashtable loadHashListToObj(String filePath, String auxPath, String ancester)
	{
		Hashtable h=new Hashtable();
		int x=auxPath.indexOf(";");
		while(x>=0)
		{
			String path=auxPath.substring(0,x).trim();
			auxPath=auxPath.substring(x+1).trim();
			loadObjectListToObj(h,filePath,path,ancester);
			x=auxPath.indexOf(";");
		}
		loadObjectListToObj(h,filePath,auxPath,ancester);
		return h;
	}
	
	public static void loadObjectListToObj(Object o, String filePath, String path, String ancester)
	{
		if(path.length()>0)
		{
			if(path.equalsIgnoreCase("%default%"))
				loadListToObj(o,filePath,"",false, ancester);
			else
				loadListToObj(o,path,"",true, ancester);
		}
	}
	
	public static Vector loadVectorListToObj(String filePath, String auxPath, String ancester)
	{
		Vector v=new Vector();
		int x=auxPath.indexOf(";");
		while(x>=0)
		{
			String path=auxPath.substring(0,x).trim();
			auxPath=auxPath.substring(x+1).trim();
			loadObjectListToObj(v,filePath,path,ancester);
			x=auxPath.indexOf(";");
		}
		loadObjectListToObj(v,filePath,auxPath,ancester);
		return new Vector(new TreeSet(v));
	}
	public static boolean loadListToObj(Object toThis, String filePath, String packageName, boolean aux, String ancestor)
	{
		StringBuffer objPathBuf=new StringBuffer(filePath);
		String objPath=objPathBuf.toString();
		
		Class ancestorCl=null;
		if (ancestor != null && ancestor.length() != 0)
		{
			try
			{
				ancestorCl = Class.forName(ancestor);
			}
			catch (ClassNotFoundException e)
			{
				Log.sysOut("CMClass","WARNING: Couldn't load ancestor class: "+ancestor);
				ancestorCl = null;
			}
		}
		
		int x=0;
		if(!aux)
		while((x=objPath.indexOf(File.separatorChar))>=0)
		{
			objPathBuf.setCharAt(x,'.');
			objPath=objPathBuf.toString();
		}
		File directory=new File(filePath);
		CMClass loader=new CMClass();
		Vector fileList=new Vector();
		if((directory.canRead())&&(directory.isDirectory()))
		{
			String[] list=directory.list();
			for(int l=0;l<list.length;l++)
				fileList.addElement(list[l]);
		}
		else
		if(directory.canRead()&&(directory.isFile()))
		{
			String fileName=filePath;
			int e=fileName.lastIndexOf(File.separatorChar);
			if(e>0)
			{
				fileName=fileName.substring(e+1);
				filePath=filePath.substring(0,e);
			}
			if(objPath.toUpperCase().endsWith(".CLASS"))
			{
				objPath=objPath.substring(0,objPath.length()-6);
				e=objPath.lastIndexOf(File.separatorChar);
				if(e>0)	objPath=objPath.substring(0,e+1);
			}
			fileList.addElement(fileName);
		}
		else
		if(!aux)
		{
			fileList.addElement(objPath);
			objPath="";
		}
		for(int l=0;l<fileList.size();l++)
		{
			String item=(String)fileList.elementAt(l);
			if((item!=null)&&(item.length()>0))
			{
				if(item.toUpperCase().endsWith(".CLASS")&&(item.indexOf("$")<0))
				{
					String itemName=item.substring(0,item.length()-6);
					try
					{
						Object O=null;
						if(aux)
						{
							File f=new File(objPath+item);
							byte[] data=new byte[(int)f.length()];
							new FileInputStream(f).read(data,0,(int)f.length());
							try
							{
								Class C=loader.defineClass(packageName+itemName, data, 0, data.length);
								loader.resolveClass(C);
								if (!checkClass(C,ancestorCl))
									O=null;
								else
									O=(Object)C.newInstance();
							}
							catch(Exception e)
							{
								Log.errOut("CMClass",e.getMessage());
							}
						}
						else
						{
							Class C=Class.forName(objPath+itemName);
							if (!checkClass(C,ancestorCl))
								O=null;
							else
								O=(Object)C.newInstance();
						}
						if(O!=null)
						{
							if(toThis instanceof Hashtable)
								((Hashtable)toThis).put(itemName.trim(),O);
							else
							if(toThis instanceof Vector)
								((Vector)toThis).addElement(O);
						}
					}
					catch(Exception e)
					{
						Log.errOut("CMCLASS",e);
						if((!item.endsWith("Child"))&&(!item.endsWith("Room")))
							Log.sysOut("CMClass","Couldn't load: "+objPath+item);
					}
				}
			}
		}
		return true;
	}

	public static String className(Object O)
	{
		if(O==null) return "";
		String name=O.getClass().getName();
		int lastDot=name.lastIndexOf(".");
		if(lastDot>=0)
			return name.substring(lastDot+1);
		else
			return name;
	}
	
	private static boolean checkClass(Class cl, Class ancestorCl)
	{
		if (cl == null)
			return false;

		// if it's a primitive or an interface, we're not interested
		if (cl.isPrimitive() || cl.isInterface())
			return false;
		
		// if it's abstract or not public, we're not interested either
		// the Class class lacks shortcut methods for these, 
		// so we use java.lang.reflect.Modifier
		if ( Modifier.isAbstract( cl.getModifiers()) || !Modifier.isPublic( cl.getModifiers()) )
			return false;
		
		// if no ancestor was specified, we're done
		if (ancestorCl == null)
			return true;
		
		return (ancestorCl.isAssignableFrom(cl)) ;
	}

	public static String classID(Object e)
	{
		if(e!=null)
		{
			if(e instanceof Environmental)
				return ((Environmental)e).ID();
			else
			if(e instanceof Race)
				return ((Race)e).ID();
			else
			if(e instanceof CharClass)
				return ((CharClass)e).ID();
			else
			if(e instanceof Behavior)
				return ((Behavior)e).ID();
			else
			if(e instanceof WebMacro)
				return ((WebMacro)e).ID();
			else
				return className(e);
		}
		return "";
	}
}
