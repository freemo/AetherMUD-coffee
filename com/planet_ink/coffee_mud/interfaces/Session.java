package com.planet_ink.coffee_mud.interfaces;
import java.util.*;
import java.io.IOException;
/* 
   Copyright 2000-2004 Bo Zimmerman

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
public interface Session
{
	public String[] clookup();
	
	public void onlyPrint(String msg, int pageBreak);
	public void onlyPrint(String msg);
	
	public void rawPrintln(String msg);
	public void rawPrintln(String msg, int pageBreak);
	public void rawPrint(String msg);
	public void rawPrint(String msg, int pageBreak);
	
	public void stdPrint(String msg);
	public void stdPrint(Environmental Source,
						 Environmental Target,
						 Environmental Tool,
						 String msg);
	public void stdPrintln(String msg);
	public void stdPrintln(Environmental Source,
						   Environmental Target,
						   Environmental Tool,
						   String msg);
	
	public void print(String msg);
	public void print(Environmental Source,
					  Environmental Target,
					  Environmental Tool,
					  String msg);
	public void println(String msg);
	public void println(Environmental Source,
						Environmental Target,
						Environmental Tool,
						String msg);
	
	public void setPromptFlag(boolean truefalse);
	
	
	public void unfilteredPrintln(String msg);
	public void unfilteredPrint(String msg);
	
	public void colorOnlyPrintln(String msg, int pageBreak);
	public void colorOnlyPrint(String msg, int pageBreak);
	public void colorOnlyPrintln(String msg);
	public void colorOnlyPrint(String msg);
	
	public String prompt(String Message, String Default)
		throws IOException;
	public String prompt(String Message, String Default, long maxTime)
		throws IOException;
	public String prompt(String Message)
		throws IOException;
	public String prompt(String Message, long maxTime)
		throws IOException;
	public boolean confirm(String Message, String Default)
	throws IOException;
	public boolean confirm(String Message, String Default, long maxTime)
	throws IOException;
	public String choose(String Message, String Choices, String Default)
	throws IOException;
	public String choose(String Message, String Choices, String Default, long maxTime)
	throws IOException;
	
	public void startBeingSnoopedBy(Session S);
	public void stopBeingSnoopedBy(Session S);
	public boolean amBeingSnoopedBy(Session S);
	
	public void cmdExit(MOB mob, Vector commands)
		throws Exception;
	public void logoff();
	public boolean killFlag();
	public void setKillFlag(boolean truefalse);
	
	public boolean afkFlag();
	public void setAfkFlag(boolean truefalse);
	
	public String blockingIn()
		throws IOException;
	public String readlineContinue()
		throws IOException;
	
	public Vector previousCMD();
	
	public MOB mob();
	public void setMob(MOB newmob);
	
	public void setTermID(int tid);
	public int getTermID();
	public String makeEscape(int c);
	public int getColor(char c);
	public int currentColor();
	
	public String getAddress();
	public int getStatus();
	public long getTotalMillis();
	public long getTotalTicks();
	public long getIdleMillis();
	
	public static final int TERM_ANSI=1;
	public static final int TERM_MSP=2;
	public static final int TERM_MSX=4;
	public static final int TERM_XML=8;
	public static final int TERM_MCP=16;
	
	public static final int STATUS_OK=0;
	public static final int STATUS_LOGIN=1;
	public static final int STATUS_LOGIN1=2;
	public static final int STATUS_LOGIN2=3;
	public static final int STATUS_LOGOUT=4;
	public static final int STATUS_LOGOUT1=5;
	public static final int STATUS_LOGOUT2=6;
	public static final int STATUS_LOGOUT3=7;
	public static final int STATUS_LOGOUT4=8;
	public static final int STATUS_LOGOUT5=9;
	public static final int STATUS_LOGOUT6=10;
	public static final int STATUS_LOGOUT7=11;
	public static final int STATUS_LOGOUT8=12;
	public static final int STATUS_LOGOUT9=13;
	public static final int STATUS_LOGOUT10=14;
	public static final int STATUS_LOGOUT11=15;
	public static final int STATUS_LOGOUT12=16;
	public static final int STATUS_LOGOUTFINAL=17;
	public static final String[] statusStr={"OPEN","LOGIN-S","LOGIN-1","LOGIN-2",
											"LOGOUT-S","LOGOUT-1","LOGOUT-2","LOGOUT-3",
											"LOGOUT-4","LOGOUT-5","LOGOUT-6","LOGOUT-7",
											"LOGOUT-8","LOGOUT-9","LOGOUT-10","LOGOUT-11",
											"LOGOUT-12","CLOSED"};
}
