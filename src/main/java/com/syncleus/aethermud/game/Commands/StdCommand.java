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
package com.syncleus.aethermud.game.Commands;

import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Commands.interfaces.Command;
import com.syncleus.aethermud.game.Items.interfaces.BoardableShip;
import com.syncleus.aethermud.game.Items.interfaces.Coins;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.Log;
import com.syncleus.aethermud.game.core.collections.Filterer;
import com.syncleus.aethermud.game.core.interfaces.CMObject;
import com.syncleus.aethermud.game.core.interfaces.Combatant;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.List;
import java.util.Vector;


@SuppressWarnings({"unchecked", "rawtypes"})
public class StdCommand implements Command {
    protected final static Filterer<Environmental> noCoinFilter = new Filterer<Environmental>() {
        @Override
        public boolean passesFilter(Environmental obj) {
            return !(obj instanceof Coins);
        }
    };
    protected final String ID;
    private final String[] access = null;

    public StdCommand() {
        final String id = this.getClass().getName();
        final int x = id.lastIndexOf('.');
        if (x >= 0)
            ID = id.substring(x + 1);
        else
            ID = id;
    }

    public static String[] I(final String[] str) {
        for (int i = 0; i < str.length; i++)
            str[i] = CMLib.lang().commandWordTranslation(str[i]);
        return str;
    }

    @Override
    public String ID() {
        return ID;
    }

    @Override
    public String name() {
        return ID();
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public void initializeClass() {
    }

    public String L(final String str, final String... xs) {
        return CMLib.lang().fullSessionTranslation(str, xs);
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        // accepts the mob executing, and a list of Strings as a parm.
        // the return value is arbitrary, though false is conventional.
        return false;
    }

    @Override
    public boolean preExecute(MOB mob, List<String> commands, int metaFlags, int secondsElapsed, double actionsRemaining)
        throws java.io.IOException {
        return true;
    }

    @Override
    public Object executeInternal(MOB mob, int metaFlags, Object... args) throws java.io.IOException {
        // fake it!
        final Vector<String> commands = new Vector<String>();
        commands.add(getAccessWords()[0]);
        for (final Object o : args)
            commands.add(o.toString());
        return Boolean.valueOf(execute(mob, commands, metaFlags));
    }

    public boolean checkArguments(Class[][] fmt, Object... args) {
        for (final Class[] element : fmt) {
            final Class[] ff = element;
            if (ff.length == args.length) {
                boolean check = true;
                for (int i = 0; i < ff.length; i++) {
                    if ((args[i] != null)
                        && (ff[i] != null)
                        && (!ff[i].isAssignableFrom(args[i].getClass()))) {
                        check = false;
                        break;
                    }
                }
                if (check)
                    return true;
            }
        }
        final StringBuilder str = new StringBuilder("");
        str.append(L("Illegal arguments. Sent: "));
        for (final Object o : args) {
            if (o == null)
                str.append(L("null "));
            else
                str.append(o.getClass().getSimpleName()).append(" ");
        }
        str.append(L(". Correct: "));
        for (final Class[] element : fmt) {
            for (final Class c : element)
                str.append(c.getSimpleName()).append(" ");
        }
        Log.errOut(ID(), str.toString());
        return false;
    }

    @Override
    public double actionsCost(final MOB mob, final List<String> cmds) {
        return CMProps.getCommandActionCost(ID(), 0.0);
    }

    @Override
    public double combatActionsCost(MOB mob, List<String> cmds) {
        return CMProps.getCommandCombatActionCost(ID(), 0.0);
    }

    @Override
    public double checkedActionsCost(final MOB mob, final List<String> cmds) {
        if (mob != null) {
            if (mob.isInCombat())
                return combatActionsCost(mob, cmds);
            final Room R = mob.location();
            if (R != null) {
                final Area A = R.getArea();
                if (A instanceof BoardableShip) {
                    final BoardableShip ship = (BoardableShip) A;
                    if ((ship.getShipItem() instanceof Combatant)
                        && (((Combatant) ship.getShipItem()).isInCombat()))
                        return combatActionsCost(mob, cmds);
                }
            }
        }
        return actionsCost(mob, cmds);
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

    @Override
    public boolean securityCheck(MOB mob) {
        return true;
    }

    @Override
    public CMObject newInstance() {
        return this;
    }

    @Override
    public CMObject copyOf() {
        try {
            final Object O = this.clone();
            return (CMObject) O;
        } catch (final CloneNotSupportedException e) {
            return this;
        }
    }

    @Override
    public int compareTo(CMObject o) {
        return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));
    }
}
