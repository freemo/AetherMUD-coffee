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
package com.planet_ink.coffee_mud.Commands;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMFile;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import java.util.List;


public class JRun extends StdCommand {
    private final String[] access = I(new String[]{"JRUN"});

    public JRun() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        if (commands.size() < 2) {
            mob.tell(L("jrun filename1 parm1 parm2 ..."));
            return false;
        }
        commands.remove(0);

        final String fn = commands.get(0);
        final StringBuffer ft = new CMFile(fn, mob, CMFile.FLAG_LOGERRORS).text();
        if ((ft == null) || (ft.length() == 0)) {
            mob.tell(L("File '@x1' could not be found.", fn));
            return false;
        }
        commands.remove(0);
        final Context cx = Context.enter();
        try {
            final JScriptWindow scope = new JScriptWindow(mob, commands);
            cx.initStandardObjects(scope);
            scope.defineFunctionProperties(JScriptWindow.functions,
                JScriptWindow.class,
                ScriptableObject.DONTENUM);
            cx.evaluateString(scope, ft.toString(), "<cmd>", 1, null);
        } catch (final Exception e) {
            mob.tell(L("JavaScript error: @x1", e.getMessage()));
        }
        Context.exit();
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return false;
    }

    @Override
    public boolean securityCheck(MOB mob) {
        return CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.JSCRIPTS);
    }

    @Override
    public int compareTo(CMObject o) {
        return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));
    }

    protected static class JScriptWindow extends ScriptableObject {
        static final long serialVersionUID = 45;
        public static String[] functions = {"mob", "numParms", "getParm", "getParms", "toJavaString"};
        MOB s = null;
        List<String> v = null;

        public JScriptWindow(MOB executor, List<String> parms) {
            s = executor;
            v = parms;
        }

        @Override
        public String getClassName() {
            return "JScriptWindow";
        }

        public MOB mob() {
            return s;
        }

        public int numParms() {
            return (v == null) ? 0 : v.size();
        }

        public String getParm(int i) {
            if (v == null)
                return "";
            if ((i < 0) || (i >= v.size()))
                return "";
            return v.get(i);
        }

        public String getParms() {
            return (v == null) ? "" : CMParms.combineQuoted(v, 0);
        }

        public String toJavaString(Object O) {
            return Context.toString(O);
        }
    }

}
