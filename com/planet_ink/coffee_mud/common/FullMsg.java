package com.planet_ink.coffee_mud.common;
import com.planet_ink.coffee_mud.interfaces.*;
import java.util.Vector;

public class FullMsg implements Affect
{
	private int targetCode=0;
	private int sourceCode=0;
	private int othersCode=0;
	private String targetMsg=null;
	private String othersMsg=null;
	private String sourceMsg=null;
	private MOB myAgent=null;
	private Environmental myTarget=null;
	private Environmental myTool=null;
	private boolean modified=false;
	private Vector trailMsgs=null;

	public FullMsg(MOB source,
				   Environmental target,
				   int newAllCode,
				   String allMessage)
	{
		myAgent=source;
		myTarget=target;
		myTool=null;
		sourceMsg=allMessage;
		targetMsg=allMessage;
		targetCode=newAllCode;
		sourceCode=newAllCode;
		othersCode=newAllCode;
		othersMsg=allMessage;
	}
	public FullMsg(MOB source,
				   Environmental target,
				   Environmental tool,
				   int newAllCode,
				   String allMessage)
	{
		myAgent=source;
		myTarget=target;
		myTool=tool;
		sourceMsg=allMessage;
		targetMsg=allMessage;
		targetCode=newAllCode;
		sourceCode=newAllCode;
		othersCode=newAllCode;
		othersMsg=allMessage;
	}
	public FullMsg(MOB source,
				   Environmental target,
				   Environmental tool,
				   int newSourceCode,
				   String sourceMessage,
				   int newTargetCode,
				   String targetMessage,
				   int newOthersCode,
				   String othersMessage)
	{
		myAgent=source;
		myTarget=target;
		myTool=tool;
		sourceMsg=sourceMessage;
		targetMsg=targetMessage;
		targetCode=newTargetCode;
		sourceCode=newSourceCode;
		othersCode=newOthersCode;
		othersMsg=othersMessage;
	}

	public Affect copyOf()
	{
		return new FullMsg(source(),target(),tool(),sourceCode(),sourceMessage(),targetCode(),targetMessage(),othersCode(),othersMessage());
	}

	public boolean wasModified()
	{
		return modified;
	}
	public void tagModified(boolean newStatus)
	{
		modified=newStatus;
	}
	public Vector trailerMsgs()
	{	return trailMsgs;}
	public void addTrailerMsg(Affect msg)
	{
		if(trailMsgs==null) trailMsgs=new Vector();
		trailMsgs.addElement(msg);
	}

	public void modify(MOB source,
						Environmental target,
						Environmental tool,
						int newSourceCode,
						String sourceMessage,
						int newTargetCode,
						String targetMessage,
						int newOthersCode,
						String othersMessage)
	{
		myAgent=source;
		myTarget=target;
		myTool=tool;
		sourceMsg=sourceMessage;
		targetMsg=targetMessage;
		targetCode=newTargetCode;
		sourceCode=newSourceCode;
		othersCode=newOthersCode;
		othersMsg=othersMessage;
	}
	public FullMsg(MOB source,
				   Environmental target,
				   Environmental tool,
				   int newSourceCode,
				   int newTargetCode,
				   int newOthersCode,
				   String Message)
	{
		myAgent=source;
		myTarget=target;
		myTool=tool;
		targetMsg=Message;
		sourceMsg=Message;
		targetCode=newTargetCode;
		sourceCode=newSourceCode;
		othersCode=newOthersCode;
		othersMsg=Message;
	}
	public MOB source()
	{
		return myAgent;
	}
	public void setTarget(Environmental target)
	{	myTarget=target;}
	public Environmental target()
	{
		return myTarget;
	}
	public Environmental tool()
	{
		return myTool;
	}
	public int targetMajor()
	{
		return targetCode&Affect.MAJOR_MASK;
	}
	public int targetMinor()
	{
		return targetCode&Affect.MINOR_MASK;
	}
	public int targetCode()
	{
		return targetCode;
	}
	public String targetMessage()
	{
		return targetMsg;
	}

	public int sourceCode()
	{
		return sourceCode;
	}
	public int sourceMajor()
	{
		return sourceCode&Affect.MAJOR_MASK;
	}
	public int sourceMinor()
	{
		return sourceCode&Affect.MINOR_MASK;
	}
	public String sourceMessage()
	{
		return sourceMsg;
	}


	public int othersMajor()
	{
		return othersCode&Affect.MAJOR_MASK;
	}
	public int othersMinor()
	{
		return othersCode&Affect.MINOR_MASK;
	}
	public int othersCode()
	{
		return othersCode;
	}
	public String othersMessage()
	{
		return othersMsg;
	}
	public boolean amITarget(Environmental thisOne)
	{
		if((target()!=null)
		   &&(target()==thisOne))
			return true;
		return false;

	}
	public boolean amISource(MOB thisOne)
	{
		if((source()!=null)
		   &&(source()==thisOne))
			return true;
		return false;

	}
}
