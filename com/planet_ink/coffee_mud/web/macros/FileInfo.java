package com.planet_ink.coffee_mud.web.macros;
import java.util.*;
import java.io.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;


public class FileInfo extends StdWebMacro
{
	public String name()	{return this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);}
	public boolean isAdminMacro()	{return true;}

	public String runMacro(ExternalHTTPRequests httpReq, String parm)
	{
		Hashtable parms=parseParms(parm);
		String last=httpReq.getRequestParameter("PATH");
		if(last==null) last=""+File.separatorChar;
		String file=httpReq.getRequestParameter("FILE");
		if(file==null) file="";
		last+=file;
		if(last.length()>0)
		{
			try
			{
				File F=new File(last);
				if(parms.containsKey("ISDIRECTORY"))
					return ""+F.isDirectory();
				if(parms.containsKey("ISFILE"))
					return ""+F.isFile();
				if(parms.containsKey("NAME"))
					return ""+F.getName();
				if(parms.containsKey("DATA"))
				{
					StringBuffer buf=new StringBuffer("");
					FileReader FR=new FileReader(F);
					BufferedReader reader=new BufferedReader(FR);
					String line="";
					while((line!=null)&&(reader.ready()))
					{
						line=reader.readLine();
						if(line!=null)
							buf.append(line+"\n");
					}
					reader.close();
					FR.close();
					return buf.toString();
				}
			}
			catch(Exception e)
			{
				return "[error]";
			}
		}
		return "";
	}
}
