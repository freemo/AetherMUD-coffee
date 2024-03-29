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
package com.syncleus.aethermud.game.Behaviors;

import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.ScriptingEngine;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.DVector;
import com.syncleus.aethermud.game.core.exceptions.ScriptParseException;
import com.syncleus.aethermud.game.core.interfaces.CMObject;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.PhysicalAgent;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Scriptable extends StdBehavior implements ScriptingEngine {
    protected ScriptingEngine engine = null;

    @Override
    public String ID() {
        return "Scriptable";
    }

    @Override
    protected int canImproveCode() {
        return Behavior.CAN_MOBS | Behavior.CAN_ITEMS | Behavior.CAN_ROOMS;
    }

    protected ScriptingEngine engine() {
        if (engine == null)
            engine = (ScriptingEngine) CMClass.getCommon("DefaultScriptingEngine");
        return engine;
    }

    @Override
    public String accountForYourself() {
        return "complex triggered behaving";
    }

    @Override
    public int getTickStatus() {
        final Tickable T = engine();
        if (T != null)
            return T.getTickStatus();
        return Tickable.STATUS_NOT;
    }

    @Override
    public void registerDefaultQuest(String questName) {
        engine().registerDefaultQuest(questName);
    }

    @Override
    public MOB getMakeMOB(Tickable ticking) {
        return engine().getMakeMOB(ticking);
    }

    @Override
    public boolean endQuest(PhysicalAgent hostObj, MOB mob, String quest) {
        engine().endQuest(hostObj, mob, quest);
        return false;
    }

    @Override
    public CMObject copyOf() {
        try {
            final Scriptable B = (Scriptable) this.clone();
            if (B.engine != null)
                B.engine = (ScriptingEngine) engine.copyOf();
            return B;
        } catch (final CloneNotSupportedException e) {
            return new Scriptable();
        }
    }

    @Override
    public List<String> externalFiles() {
        return engine().externalFiles();
    }

    @Override
    public String getScriptResourceKey() {
        return engine().getScriptResourceKey();
    }

    @Override
    public String getParms() {
        return engine().getScript();
    }

    @Override
    public void setParms(String newParms) {
        engine().setScript(newParms);
        super.setParms("");
    }

    @Override
    public String[] parseEval(String evaluable) throws ScriptParseException {
        return engine().parseEval(evaluable);
    }

    @Override
    public String getVar(String context, String variable) {
        return engine().getVar(context, variable);
    }

    @Override
    public boolean isVar(String context, String variable) {
        return engine().isVar(context, variable);
    }

    @Override
    public void setVar(String context, String variable, String value) {
        engine().setVar(context, variable, value);
    }

    @Override
    public String defaultQuestName() {
        return engine().defaultQuestName();
    }

    @Override
    public String getVarScope() {
        return engine().getVarScope();
    }

    @Override
    public void setVarScope(String scope) {
        engine().setVarScope(scope);
    }

    @Override
    public String getLocalVarXML() {
        return engine().getLocalVarXML();
    }

    @Override
    public void setLocalVarXML(String xml) {
        if (engine().getVarScope().length() > 0)
            engine().setLocalVarXML(xml);
    }

    @Override
    public boolean eval(PhysicalAgent scripted,
                        MOB source,
                        Environmental target,
                        MOB monster,
                        Item primaryItem,
                        Item secondaryItem,
                        String msg,
                        Object[] tmp,
                        String[][] eval,
                        int startEval) {
        return engine().eval(scripted, source, target, monster, primaryItem, secondaryItem, msg, tmp, eval, startEval);
    }

    @Override
    public String getScript() {
        return engine().getScript();
    }

    @Override
    public void setScript(String newParms) {
        engine().setScript(newParms);
    }

    @Override
    public String execute(PhysicalAgent scripted,
                          MOB source,
                          Environmental target,
                          MOB monster,
                          Item primaryItem,
                          Item secondaryItem,
                          DVector script,
                          String msg,
                          Object[] tmp) {
        return engine().execute(scripted, source, target, monster, primaryItem, secondaryItem, script, msg, tmp);
    }

    @Override
    public void executeMsg(Environmental affecting, CMMsg msg) {
        super.executeMsg(affecting, msg);
        engine().executeMsg(affecting, msg);
    }

    @Override
    public boolean okMessage(Environmental affecting, CMMsg msg) {
        if (!super.okMessage(affecting, msg))
            return false;
        return engine().okMessage(affecting, msg);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        super.tick(ticking, tickID);
        if (!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
            return false;
        return engine().tick(ticking, tickID);
    }

    @Override
    public void dequeResponses() {
        engine().dequeResponses();
    }

    @Override
    public String varify(MOB source, Environmental target,
                         PhysicalAgent scripted, MOB monster, Item primaryItem,
                         Item secondaryItem, String msg, Object[] tmp, String varifyable) {
        return engine().varify(source, target, scripted, monster, primaryItem, secondaryItem, msg, tmp, varifyable);
    }

    @Override
    public String functify(PhysicalAgent scripted, MOB source, Environmental target, MOB monster, Item primaryItem,
                           Item secondaryItem, String msg, Object[] tmp, String evaluable) {
        return engine().functify(scripted, source, target, monster, primaryItem, secondaryItem, msg, tmp, evaluable);
    }
}
