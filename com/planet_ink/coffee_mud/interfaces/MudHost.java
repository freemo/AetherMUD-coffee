package com.planet_ink.coffee_mud.interfaces;
import java.util.Properties;

public interface MudHost
{
	public final static long TICK_TIME=4000;
	public final static long TIME_SAVETHREAD_SLEEP=60*60000; // 30 minutes, right now.
	public final static long TIME_MILIS_PER_MUDHOUR=10*60000; // 10 minutes, right now.
	public final static long TIME_UTILTHREAD_SLEEP=TIME_MILIS_PER_MUDHOUR;
	public final static long TICKS_PER_RLMIN=(int)Math.round(60000.0/new Long(TICK_TIME).doubleValue());

	public final static int TICKMASK_SOLITARY=65536;
	public final static int TICK_MOB=0;
	public final static int TICK_ITEM_BEHAVIOR=1;
	public final static int TICK_EXIT_REOPEN=2;
	public final static int TICK_DEADBODY_DECAY=3;
	public final static int TICK_LIGHT_FLICKERS=4;
	public final static int TICK_TRAP_RESET=5;
	public final static int TICK_TRAP_DESTRUCTION=6;
	public final static int TICK_ITEM_BOUNCEBACK=7;
	public final static int TICK_ROOM_BEHAVIOR=8;
	public final static int TICK_AREA=9;
	public final static int TICK_ROOM_ITEM_REJUV=10;
	public final static int TICK_EXIT_BEHAVIOR=11;
	public final static int TICK_SPELL_AFFECT=12;
	public final static int TICK_QUEST=13;
	public final static int TICK_CLAN=14;
	public final static int TICK_CLANITEM=15;
	public final static int TICK_EMAIL=TICKMASK_SOLITARY|16;
	public final static int TICK_READYTOSTOP=17;

	public final static int MAX_TICK_CLIENTS=32;

	public String getHost();
	public int getPort();
	public void shutdown(Session S, boolean keepItDown, String externalCommand);
}
