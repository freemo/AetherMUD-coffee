package com.planet_ink.coffee_mud.web.macros;
import java.util.*;
import java.io.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;


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
public class FileMgr extends StdWebMacro
{
	public String name()	{return this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);}
	public boolean isAdminMacro()	{return true;}

	public String runMacro(ExternalHTTPRequests httpReq, String parm)
	{
		Hashtable parms=parseParms(parm);
		String path=httpReq.getRequestParameter("PATH");
		if(path==null) path="";
		String file=httpReq.getRequestParameter("FILE");
		if(file==null) file="";
		if(!path.endsWith("/"))
		{
			path+="/";
			httpReq.addRequestParameters("PATH",path);
		}
		if(path.indexOf("..")>=0)
			return "[path security error]";
		if(file.indexOf("..")>=0)
			return "[file security error]";
		if(file.indexOf(File.separatorChar)>=0)
			return "[file security error]";
		if(file.indexOf("/")>=0)
			return "[file security error]";
		String last=FileNext.fixFileName(httpReq,path);
		if(last.length()==0)
			return "[path security error]";
		last+=file;
		if(last.length()>0)
		{
			try
			{
				File F=new File(last);
				if(parms.containsKey("DELETE"))
				{
					if(F.delete())
						return "File `"+last+"` was deleted.";
					else
						return "File `"+last+"` was NOT deleted. Perhaps it`s read-only?";
				}
				else
				if(parms.containsKey("CREATE"))
				{
					FileWriter FW=new FileWriter(F,false);
					String s=httpReq.getRequestParameter("RAWTEXT");
					if(s==null) return "File `"+last+"` not updated -- no buffer!";
					FW.write(s);
					FW.flush();
					FW.close();
					return "File `"+last+"` updated.";
				}
				else
				if(parms.containsKey("CREATEDIR"))
				{
					if(F.exists())
						return "File exists! Directory not created!";
					if(!F.mkdir())
						return "Error creating directory!";
					return "Created directory.";
				}
				else
				if(parms.containsKey("APPEND"))
				{
					FileWriter FW=new FileWriter(F,true);
					String s=httpReq.getRequestParameter("RAWTEXT");
					if(s==null) return "File `"+last+"` not appended -- no buffer!";
					FW.write(s);
					FW.flush();
					FW.close();
					return "File `"+last+"` appended.";
				}
			}
			catch(Exception e)
			{
				return "[an error occurred performing the last operation]";
			}
		}
		return "";
	}
}
