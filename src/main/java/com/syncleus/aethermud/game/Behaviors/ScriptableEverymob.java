/**
 * Copyright 2017 Syncleus, Inc.
 * with portions copyright 2004-2017 Bo Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.planet_ink.game.Behaviors;

import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.Behaviors.interfaces.Behavior;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.ScriptingEngine;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.collections.DVector;
import com.planet_ink.game.core.collections.XVector;
import com.planet_ink.game.core.exceptions.ScriptParseException;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.PhysicalAgent;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.Enumeration;
import java.util.List;


@SuppressWarnings({"unchecked", "rawtypes"})
public class ScriptableEverymob extends StdBehavior implements ScriptingEngine {
    private boolean started = false;
    private Scriptable sampleB = null;

    @Override
    public String ID() {
        return "ScriptableEverymob";
    }

    @Override
    protected int canImproveCode() {
        return Behavior.CAN_ROOMS | Behavior.CAN_AREAS;
    }

    @Override
    public String accountForYourself() {
        return "complex triggered behaving";
    }

    private void giveUpTheScript(Area metroA, MOB M) {
        if ((M == null)
            || (!M.isMonster())
            || (M.getStartRoom() == null)
            || (metroA == null)
            || (!metroA.inMyMetroArea(M.getStartRoom().getArea()))
            || (M.fetchBehavior("Scriptable") != null))
            return;
        final Scriptable S = new Scriptable();
        S.setParms(getParms());
        S.setSavable(false);
        M.addBehavior(S);
        S.setSavable(false);
        sampleB = S;
    }

    private Area determineArea(Environmental forMe) {
        if (forMe instanceof Room)
            return ((Room) forMe).getArea();
        else if (forMe instanceof Area)
            return (Area) forMe;
        return null;
    }

    private Enumeration determineRooms(Environmental forMe) {
        if (forMe instanceof Room)
            return new XVector(forMe).elements();
        else if (forMe instanceof Area)
            return ((Area) forMe).getMetroMap();
        return null;
    }

    private void giveEveryoneTheScript(Environmental forMe) {
        if ((CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
            && (!started)) {
            started = true;
            final Enumeration rooms = determineRooms(forMe);
            final Area A = determineArea(forMe);
            if ((A != null) && (rooms != null)) {
                Room R = null;
                for (; rooms.hasMoreElements(); ) {
                    R = (Room) rooms.nextElement();
                    for (int m = 0; m < R.numInhabitants(); m++)
                        giveUpTheScript(A, R.fetchInhabitant(m));
                }
            }
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((!started) && (ticking instanceof Environmental))
            giveEveryoneTheScript((Environmental) ticking);
        return super.tick(ticking, tickID);
    }

    @Override
    public void startBehavior(PhysicalAgent forMe) {
        giveEveryoneTheScript(forMe);
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        if ((msg.target() instanceof Room)
            && (msg.targetMinor() == CMMsg.TYP_LOOK))
            giveUpTheScript(determineArea(host), msg.source());
        super.executeMsg(host, msg);
    }

    @Override
    public String defaultQuestName() {
        return (sampleB == null) ? "" : sampleB.defaultQuestName();
    }

    @Override
    public void dequeResponses() {
        if (sampleB != null)
            sampleB.dequeResponses();
    }

    @Override
    public List<String> externalFiles() {
        return (sampleB == null) ? null : sampleB.externalFiles();
    }

    @Override
    public boolean endQuest(PhysicalAgent hostObj, MOB mob, String quest) {
        return (sampleB == null) ? false : sampleB.endQuest(hostObj, mob, quest);
    }

    @Override
    public boolean eval(PhysicalAgent scripted, MOB source,
                        Environmental target, MOB monster, Item primaryItem,
                        Item secondaryItem, String msg, Object[] tmp, String[][] eval,
                        int startEval) {
        return (sampleB == null) ? false : sampleB.eval(scripted, source, target, monster, primaryItem, secondaryItem, msg, tmp, eval, startEval);
    }

    @Override
    public String execute(PhysicalAgent scripted, MOB source,
                          Environmental target, MOB monster, Item primaryItem,
                          Item secondaryItem, DVector script, String msg, Object[] tmp) {
        return (sampleB == null) ? "" : sampleB.execute(scripted, source, target, monster, primaryItem, secondaryItem, script, msg, tmp);
    }

    @Override
    public String getLocalVarXML() {
        return (sampleB == null) ? "" : sampleB.getLocalVarXML();
    }

    @Override
    public void setLocalVarXML(String xml) {
        if (sampleB != null)
            sampleB.setLocalVarXML(xml);
    }

    @Override
    public MOB getMakeMOB(Tickable ticking) {
        return (sampleB == null) ? null : sampleB.getMakeMOB(ticking);
    }

    @Override
    public String getScript() {
        return (sampleB == null) ? "" : sampleB.getScript();
    }

    @Override
    public void setScript(String newParms) {
        if (sampleB != null)
            sampleB.setScript(newParms);
    }

    @Override
    public String getScriptResourceKey() {
        return (sampleB == null) ? "" : sampleB.getScriptResourceKey();
    }

    @Override
    public String getVar(String context, String variable) {
        return (sampleB == null) ? "" : sampleB.getVar(context, variable);
    }

    @Override
    public String getVarScope() {
        return (sampleB == null) ? "" : sampleB.getVarScope();
    }

    @Override
    public void setVarScope(String scope) {
        if (sampleB != null)
            sampleB.setVarScope(scope);
    }

    @Override
    public boolean isVar(String context, String variable) {
        return (sampleB == null) ? false : sampleB.isVar(context, variable);
    }

    @Override
    public String[] parseEval(String evaluable) throws ScriptParseException {
        return (sampleB == null) ? new String[0] : sampleB.parseEval(evaluable);
    }

    @Override
    public void setVar(String context, String variable, String value) {
        if (sampleB != null)
            sampleB.setVar(context, variable, value);
    }

    @Override
    public String varify(MOB source, Environmental target,
                         PhysicalAgent scripted, MOB monster, Item primaryItem,
                         Item secondaryItem, String msg, Object[] tmp, String varifyable) {
        return (sampleB == null) ? "" : sampleB.varify(source, target, scripted, monster, primaryItem, secondaryItem, msg, tmp, varifyable);
    }

    @Override
    public String functify(PhysicalAgent scripted, MOB source, Environmental target, MOB monster, Item primaryItem,
                           Item secondaryItem, String msg, Object[] tmp, String evaluable) {
        return (sampleB == null) ? "" : sampleB.functify(scripted, source, target, monster, primaryItem, secondaryItem, msg, tmp, evaluable);
    }
}
