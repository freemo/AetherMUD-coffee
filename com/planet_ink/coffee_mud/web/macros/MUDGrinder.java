package com.planet_ink.coffee_mud.web.macros;
import java.util.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.web.macros.grinder.*;
import com.planet_ink.coffee_mud.utils.*;


public class MUDGrinder extends StdWebMacro
{
	public String name()	{return this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);}
	public boolean isAdminMacro()	{return true;}

	public String runMacro(ExternalHTTPRequests httpReq, String parm)
	{
		Hashtable parms=parseParms(parm);
		if(parms!=null)
		if(parms.containsKey("AREAMAP"))
		{
			String AREA=(String)httpReq.getRequestParameters().get("AREA");
			if(AREA==null) return "";
			if(AREA.length()==0) return "";
			Area A=CMMap.getArea(AREA);
			if(A==null) return "";
			GrinderMap map=new GrinderMap(A);
			map.rePlaceRooms();
			return map.getHTMLTable(httpReq).toString();
		}
		else
		if(parms.containsKey("AREALIST"))
		{
			MOB mob=Authenticate.getAuthenticatedMOB(Authenticate.getLogin(httpReq));
			if(mob==null) return "@break@";
			Area pickedA=getLoggedArea(httpReq,mob);
			return GrinderAreas.getAreaList(pickedA,mob);
		}
		else
		if(parms.containsKey("DELAREA"))
		{
			MOB mob=Authenticate.getAuthenticatedMOB(Authenticate.getLogin(httpReq));
			if(mob==null) return "@break@";
			Area A=getLoggedArea(httpReq,mob);
			if(A==null) return "@break@";
			ExternalPlay.obliterateArea(A.name());
			return "The area "+A.name()+" has been successfully deleted.";
		}
		else
		if(parms.containsKey("ADDAREA"))
		{
			String AREA=(String)httpReq.getRequestParameters().get("AREA");
			if(AREA==null) return "false";
			if(AREA.length()==0) return "false";
			Area A=CMMap.getArea(AREA);
			if(A==null)
				A=ExternalPlay.DBCreateArea(AREA,"StdArea");
			else
				return "false";
			return "true";
		}
		else
		if(parms.containsKey("EDITAREA"))
		{
			String AREA=(String)httpReq.getRequestParameters().get("AREA");
			if(AREA==null) return "";
			if(AREA.length()==0) return "";
			Area A=CMMap.getArea(AREA);
			if(A==null) return "";
			String error=GrinderAreas.modifyArea(httpReq,parms);
			AREA=(String)httpReq.getRequestParameters().get("AREA");
			if(error==null) error="";
			httpReq.getRequestParameters().put("ERRMSG",error);
			httpReq.resetRequestEncodedParameters();
		}
		else
		if(parms.containsKey("DELEXIT"))
		{
			MOB mob=Authenticate.getAuthenticatedMOB(Authenticate.getLogin(httpReq));
			if(mob==null) return "@break@";
			Room R=CMMap.getRoom((String)httpReq.getRequestParameters().get("ROOM"));
			if(R==null) return "@break@";
			int dir=Directions.getGoodDirectionCode((String)httpReq.getRequestParameters().get("LINK"));
			if(dir<0) return "@break@";
			String errMsg=GrinderExits.delExit(R,dir);
			httpReq.getRequestParameters().put("ERRMSG",errMsg);
			httpReq.resetRequestEncodedParameters();
		}
		else
		if(parms.containsKey("EDITEXIT"))
		{
			MOB mob=Authenticate.getAuthenticatedMOB(Authenticate.getLogin(httpReq));
			if(mob==null) return "@break@";
			Room R=CMMap.getRoom((String)httpReq.getRequestParameters().get("ROOM"));
			if(R==null) return "@break@";
			int dir=Directions.getGoodDirectionCode((String)httpReq.getRequestParameters().get("LINK"));
			if(dir<0) return "@break@";
			String errMsg=GrinderExits.editExit(R,dir,httpReq,parms);
			httpReq.getRequestParameters().put("ERRMSG",errMsg);
			httpReq.resetRequestEncodedParameters();
		}
		else
		if(parms.containsKey("QUICKFIND"))
		{
			String find=(String)httpReq.getRequestParameters().get("QUICKFIND");
			if(find==null) return "@break@";
			String AREA=(String)httpReq.getRequestParameters().get("AREA");
			if(AREA==null) return "";
			if(AREA.length()==0) return "";
			Area A=CMMap.getArea(AREA);
			if(A==null) return "";
			for(int a=0;a<A.getMyMap().size();a++)
			{
				Room R=(Room)A.getMyMap().elementAt(a);
				if(R.ID().toUpperCase().endsWith(find.toUpperCase()))
				{
					httpReq.getRequestParameters().put("ROOM",R.ID());
					httpReq.resetRequestEncodedParameters();
					return "";
				}
			}
			for(int a=0;a<A.getMyMap().size();a++)
			{
				Room R=(Room)A.getMyMap().elementAt(a);
				if(R.displayText().toUpperCase().indexOf(find.toUpperCase())>=0)
				{
					httpReq.getRequestParameters().put("ROOM",R.ID());
					httpReq.resetRequestEncodedParameters();
					return "";
				}
			}
			for(int a=0;a<A.getMyMap().size();a++)
			{
				Room R=(Room)A.getMyMap().elementAt(a);
				if(R.description().toUpperCase().indexOf(find.toUpperCase())>=0)
				{
					httpReq.getRequestParameters().put("ROOM",R.ID());
					httpReq.resetRequestEncodedParameters();
					return "";
				}
			}
			return "";
		}
		else
		if(parms.containsKey("LINKEXIT"))
		{
			MOB mob=Authenticate.getAuthenticatedMOB(Authenticate.getLogin(httpReq));
			if(mob==null) return "@break@";
			Room R=CMMap.getRoom((String)httpReq.getRequestParameters().get("ROOM"));
			if(R==null) return "@break@";
			int dir=Directions.getGoodDirectionCode((String)httpReq.getRequestParameters().get("LINK"));
			if(dir<0) return "@break@";
			Room R2=CMMap.getRoom((String)httpReq.getRequestParameters().get("OLDROOM"));
			if(R2==null) return "@break@";
			int dir2=Directions.getGoodDirectionCode((String)httpReq.getRequestParameters().get("OLDLINK"));
			if(dir2<0) return "@break@";
			String errMsg=GrinderExits.linkRooms(R,R2,dir,dir2);
			httpReq.getRequestParameters().put("ERRMSG",errMsg);
			httpReq.resetRequestEncodedParameters();
		}
		else
		if(parms.containsKey("EDITROOM"))
		{
			MOB mob=Authenticate.getAuthenticatedMOB(Authenticate.getLogin(httpReq));
			if(mob==null) return "@break@";
			Room R=CMMap.getRoom((String)httpReq.getRequestParameters().get("ROOM"));
			if(R==null) return "@break@";
			String errMsg=GrinderRooms.editRoom(httpReq,parms,R);
			httpReq.getRequestParameters().put("ERRMSG",errMsg);
			httpReq.resetRequestEncodedParameters();
		}
		else
		if(parms.containsKey("DELROOM"))
		{
			MOB mob=Authenticate.getAuthenticatedMOB(Authenticate.getLogin(httpReq));
			if(mob==null) return "@break@";
			Room R=CMMap.getRoom((String)httpReq.getRequestParameters().get("ROOM"));
			if(R==null) return "@break@";
			for(int d=0;d<R.rawDoors().length;d++)
				if(R.rawDoors()[d]!=null)
				{
					httpReq.getRequestParameters().put("ROOM",R.rawDoors()[d].ID());
					httpReq.getRequestParameters().put("LINK","");
					break;
				}
			String errMsg=GrinderRooms.delRoom(R);
			httpReq.getRequestParameters().put("ERRMSG",errMsg);
			httpReq.resetRequestEncodedParameters();
		}
		else
		if(parms.containsKey("ADDROOM"))
		{
			MOB mob=Authenticate.getAuthenticatedMOB(Authenticate.getLogin(httpReq));
			if(mob==null) return "@break@";
			Room R=CMMap.getRoom((String)httpReq.getRequestParameters().get("ROOM"));
			if(R==null) return "@break@";
			int dir=Directions.getGoodDirectionCode((String)httpReq.getRequestParameters().get("LINK"));
			if(dir<0) return "@break@";
			String errMsg=GrinderRooms.createRoom(R,dir);
			httpReq.getRequestParameters().put("ERRMSG",errMsg);
			R=R.rawDoors()[dir];
			if(R!=null)
				httpReq.getRequestParameters().put("ROOM",R.ID());
			httpReq.getRequestParameters().put("LINK","");
			httpReq.resetRequestEncodedParameters();
		}
		return "";
	}
	
	private Area getLoggedArea(ExternalHTTPRequests httpReq, MOB mob)
	{
		String AREA=(String)httpReq.getRequestParameters().get("AREA");
		if(AREA==null) return null;
		if(AREA.length()==0) return null;
		Area A=CMMap.getArea(AREA);
		if(A==null) return null;
		if(mob.isASysOp(null)||A.amISubOp(mob.name()))
			return A;
		return null;
	}
}
