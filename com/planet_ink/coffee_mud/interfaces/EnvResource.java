package com.planet_ink.coffee_mud.interfaces;

public interface EnvResource extends Item
{
	// item materials
	public final static int MATERIAL_UNKNOWN=0;
	public final static int MATERIAL_CLOTH=1<<8;
	public final static int MATERIAL_LEATHER=2<<8;
	public final static int MATERIAL_METAL=3<<8;
	public final static int MATERIAL_MITHRIL=4<<8;
	public final static int MATERIAL_WOODEN=5<<8;
	public final static int MATERIAL_GLASS=6<<8;
	public final static int MATERIAL_VEGETATION=7<<8;
	public final static int MATERIAL_FLESH=8<<8;
	public final static int MATERIAL_PAPER=9<<8;
	public final static int MATERIAL_ROCK=10<<8;
	public final static int MATERIAL_LIQUID=11<<8;

	public final static int MATERIAL_MASK=
		MATERIAL_CLOTH|MATERIAL_LEATHER|MATERIAL_METAL|
		MATERIAL_MITHRIL|MATERIAL_WOODEN|MATERIAL_GLASS|
		MATERIAL_VEGETATION|MATERIAL_FLESH|MATERIAL_PAPER|
		MATERIAL_ROCK|MATERIAL_LIQUID;
	
	
	public final static String[] MATERIAL_DESCS={
	"UNKNOWN",
	"CLOTH",
	"LEATHER",
	"METAL",
	"MITHRIL",
	"WOODEN",
	"GLASS",
	"VEGETATION",
	"FLESH",
	"PAPER",
	"ROCK",
	"LIQUID"};
	
	public final static int RESOURCE_NOTHING=MATERIAL_UNKNOWN|0;
	public final static int RESOURCE_MEAT=MATERIAL_FLESH|1;
	public final static int RESOURCE_STEAK=MATERIAL_FLESH|2;
	public final static int RESOURCE_CHOPS=MATERIAL_FLESH|3;
	public final static int RESOURCE_ROAST=MATERIAL_FLESH|4;
	public final static int RESOURCE_RIBS=MATERIAL_FLESH|5;
	public final static int RESOURCE_FISH=MATERIAL_FLESH|6;
	public final static int RESOURCE_WHEAT=MATERIAL_VEGETATION|7;
	public final static int RESOURCE_CORN=MATERIAL_VEGETATION|8;
	public final static int RESOURCE_RICE=MATERIAL_VEGETATION|9;
	public final static int RESOURCE_CARROTS=MATERIAL_VEGETATION|10;
	public final static int RESOURCE_TOMATOES=MATERIAL_VEGETATION|11;
	public final static int RESOURCE_PEPPERS=MATERIAL_VEGETATION|12;
	public final static int RESOURCE_GREENS=MATERIAL_VEGETATION|13;
	public final static int RESOURCE_FRUIT=MATERIAL_VEGETATION|14;
	public final static int RESOURCE_APPLES=MATERIAL_VEGETATION|15;
	public final static int RESOURCE_BERRIES=MATERIAL_VEGETATION|16;
	public final static int RESOURCE_ORANGES=MATERIAL_VEGETATION|17;
	public final static int RESOURCE_LEMONS=MATERIAL_VEGETATION|18;
	public final static int RESOURCE_GRAPES=MATERIAL_VEGETATION|19;
	public final static int RESOURCE_OLIVES=MATERIAL_VEGETATION|20;
	public final static int RESOURCE_POTATOES=MATERIAL_VEGETATION|21;
	public final static int RESOURCE_CACTUS=MATERIAL_VEGETATION|22;
	public final static int RESOURCE_DATES=MATERIAL_VEGETATION|23;
	public final static int RESOURCE_SEAWEED=MATERIAL_VEGETATION|24;
	public final static int RESOURCE_STONE=MATERIAL_ROCK|25;
	public final static int RESOURCE_LIMESTONE=MATERIAL_ROCK|26;
	public final static int RESOURCE_FLINT=MATERIAL_ROCK|27;
	public final static int RESOURCE_GRANITE=MATERIAL_ROCK|28;
	public final static int RESOURCE_OBSIDIAN=MATERIAL_ROCK|29;
	public final static int RESOURCE_MARBLE=MATERIAL_ROCK|30;
	public final static int RESOURCE_SAND=MATERIAL_ROCK|31;
	public final static int RESOURCE_JADE=MATERIAL_ROCK|32;
	public final static int RESOURCE_IRON=MATERIAL_METAL|33;
	public final static int RESOURCE_LEAD=MATERIAL_METAL|34;
	public final static int RESOURCE_BRONZE=MATERIAL_METAL|35;
	public final static int RESOURCE_SILVER=MATERIAL_METAL|36;
	public final static int RESOURCE_GOLD=MATERIAL_METAL|37;
	public final static int RESOURCE_ZINC=MATERIAL_METAL|38;
	public final static int RESOURCE_COPPER=MATERIAL_METAL|39;
	public final static int RESOURCE_TIN=MATERIAL_METAL|40;
	public final static int RESOURCE_MITHRIL=MATERIAL_MITHRIL|41;
	public final static int RESOURCE_ADAMANTITE=MATERIAL_METAL|42;
	public final static int RESOURCE_STEEL=MATERIAL_METAL|43;
	public final static int RESOURCE_BRASS=MATERIAL_METAL|44;
	public final static int RESOURCE_WOOD=MATERIAL_WOODEN|45;
	public final static int RESOURCE_PINE=MATERIAL_WOODEN|46;
	public final static int RESOURCE_BALSA=MATERIAL_WOODEN|47;
	public final static int RESOURCE_OAK=MATERIAL_WOODEN|48;
	public final static int RESOURCE_MAPLE=MATERIAL_WOODEN|49;
	public final static int RESOURCE_REDWOOD=MATERIAL_WOODEN|50;
	public final static int RESOURCE_HICKORY=MATERIAL_WOODEN|51;
	public final static int RESOURCE_SCALES=MATERIAL_LEATHER|52;
	public final static int RESOURCE_FUR=MATERIAL_CLOTH|53;
	public final static int RESOURCE_LEATHER=MATERIAL_LEATHER|54;
	public final static int RESOURCE_SKIN=MATERIAL_CLOTH|55;
	public final static int RESOURCE_WOOL=MATERIAL_CLOTH|56;
	public final static int RESOURCE_FEATHERS=MATERIAL_CLOTH|57;
	public final static int RESOURCE_COTTON=MATERIAL_CLOTH|58;
	public final static int RESOURCE_HEMP=MATERIAL_CLOTH|59;
	public final static int RESOURCE_FRESHWATER=MATERIAL_LIQUID|60;
	public final static int RESOURCE_SALTWATER=MATERIAL_LIQUID|61;
	public final static int RESOURCE_DRINKABLE=MATERIAL_LIQUID|62;
	public final static int RESOURCE_GLASS=MATERIAL_GLASS|63;
	public final static int RESOURCE_PAPER=MATERIAL_PAPER|64;
	public final static int RESOURCE_CLAY=MATERIAL_GLASS|65;
	public final static int RESOURCE_CHINA=MATERIAL_GLASS|66;
	public final static int RESOURCE_DIAMOND=MATERIAL_ROCK|67;
	public final static int RESOURCE_CRYSTAL=MATERIAL_GLASS|68;
	public final static int RESOURCE_GEM=MATERIAL_ROCK|69;
	public final static int RESOURCE_PEARL=MATERIAL_ROCK|70;
	public final static int RESOURCE_PLATINUM=MATERIAL_METAL|71;
	
	public final static int RESOURCE_MASK=255;
	
	public final static String[] RESOURCE_DESCS={
	"NOTHING", "MEAT", "STEAK", "CHOPS", "ROAST", "RIBS", "FISH",
	"WHEAT", "CORN", "RICE", "CARROTS", "TOMATOES", "PEPPERS", "GREENS",
	"FRUIT", "APPLES", "BERRIES", "ORANGES", "LEMONS", "GRAPES", "OLIVES",
	"POTATOES", "CACTUS", "DATES", "SEAWEED", "STONE", "LIMESTONE",
	"FLINT", "GRANITE", "OBSIDIAN", "MARBLE", "SAND", "JADE", "IRON",
	"LEAD", "BRONZE", "SILVER", "GOLD", "ZINC", "COPPER", "TIN", "MITHRIL",
	"ADAMANTITE", "STEEL", "BRASS", "WOOD", "PINE", "BALSA", "OAK", "MAPLE",
	"REDWOOD", "HICKORY", "SCALES", "FUR", "LEATHER", "SKIN", "WOOL",
	"FEATHERS", "COTTON", "HEMP","WATER","SALT WATER","GLASS","PAPER",
	"CLAY","DIAMOND","CHINA","CRYSTAL","GEM", "PEARL", "PLATINUM"
	};
	public final static int[] RESOURCES_ALL={
	RESOURCE_NOTHING, RESOURCE_MEAT, RESOURCE_STEAK, RESOURCE_CHOPS, 
	RESOURCE_ROAST, RESOURCE_RIBS, RESOURCE_FISH, RESOURCE_WHEAT, 
	RESOURCE_CORN, RESOURCE_RICE, RESOURCE_CARROTS, RESOURCE_TOMATOES, 
	RESOURCE_PEPPERS, RESOURCE_GREENS, RESOURCE_FRUIT, RESOURCE_APPLES, 
	RESOURCE_BERRIES, RESOURCE_ORANGES, RESOURCE_LEMONS, RESOURCE_GRAPES, 
	RESOURCE_OLIVES, RESOURCE_POTATOES, RESOURCE_CACTUS, RESOURCE_DATES, 
	RESOURCE_SEAWEED, RESOURCE_STONE, RESOURCE_LIMESTONE, RESOURCE_FLINT, 
	RESOURCE_GRANITE, RESOURCE_OBSIDIAN, RESOURCE_MARBLE, RESOURCE_SAND, 
	RESOURCE_JADE, RESOURCE_IRON, RESOURCE_LEAD, RESOURCE_BRONZE, 
	RESOURCE_SILVER, RESOURCE_GOLD, RESOURCE_ZINC, RESOURCE_COPPER, 
	RESOURCE_TIN, RESOURCE_MITHRIL, RESOURCE_ADAMANTITE, RESOURCE_STEEL, 
	RESOURCE_BRASS, RESOURCE_WOOD, RESOURCE_PINE, RESOURCE_BALSA, 
	RESOURCE_OAK, RESOURCE_MAPLE, RESOURCE_REDWOOD, RESOURCE_HICKORY, 
	RESOURCE_SCALES, RESOURCE_FUR, RESOURCE_LEATHER, RESOURCE_SKIN, 
	RESOURCE_WOOL, RESOURCE_FEATHERS, RESOURCE_COTTON, RESOURCE_HEMP, 
	RESOURCE_FRESHWATER, RESOURCE_SALTWATER, RESOURCE_GLASS, RESOURCE_PAPER,
	RESOURCE_CLAY, RESOURCE_DIAMOND, RESOURCE_CHINA, RESOURCE_CRYSTAL, 
	RESOURCE_GEM, RESOURCE_PEARL, RESOURCE_PLATINUM
	};
}
