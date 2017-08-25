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
package com.syncleus.aethermud.game.Abilities.Songs;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.collections.STreeMap;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Skill_Imitation extends BardSkill {
    private final static String localizedName = CMLib.lang().L("Imitate");
    private static final String[] triggerStrings = I(new String[]{"IMITATE"});
    public String lastID = "";
    public STreeMap<String, String> immitations = new STreeMap<String, String>();
    public String[] lastOnes = new String[2];

    @Override
    public String ID() {
        return "Skill_Imitation";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_DECEPTIVE;
    }

    public int craftType() {
        return Ability.ACODE_SPELL;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        if ((myHost == null) || (!(myHost instanceof MOB)))
            return;
        final MOB mob = (MOB) myHost;
        if (msg.tool() != null) {
            if ((msg.amISource(mob))
                && ((msg.tool().ID().equals("Skill_Spellcraft"))
                || (msg.tool().ID().equals("Skill_Songcraft"))
                || (msg.tool().ID().equals("Skill_Chantcraft"))
                || (msg.tool().ID().equals("Skill_Prayercraft")))
                && (msg.tool().text().equals(lastOnes[0]))
                && (msg.tool().text().length() > 0)
                && (!immitations.containsKey(msg.tool().text()))) {
                final Ability A = CMClass.getAbility(msg.tool().text());
                if (A != null)
                    immitations.put(A.name(), lastOnes[1]);
            } else if ((msg.tool() instanceof Ability)
                && (!msg.amISource(mob))
                && (msg.othersMessage() != null)) {
                lastOnes[0] = msg.tool().ID();
                lastOnes[1] = msg.othersMessage();
            }
        }

    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        Environmental target = null;
        if (commands.size() > 1) {
            target = mob.location().fetchFromRoomFavorMOBs(null, commands.get(commands.size() - 1));
            if (target == null)
                target = mob.findItem(null, commands.get(commands.size() - 1));
            if (target != null)
                commands.remove(commands.size() - 1);
        }
        final String cmd = (commands.size() > 0) ? CMParms.combine(commands, 0).toUpperCase() : "";
        final StringBuffer str = new StringBuffer("");
        String found = null;
        for (final String key : immitations.keySet()) {
            if ((cmd.length() > 0) && (key.toUpperCase().startsWith(cmd)))
                found = key;
            str.append(key + " ");
        }
        if ((cmd.length() == 0) || (found == null)) {
            if (found != null)
                mob.tell(L("'@x1' is not something you know how to imitate.", cmd));
            mob.tell(L("Spells/Skills you may imitate: @x1.", str.toString()));
            return true;
        }
        if (target == null)
            target = mob.getVictim();
        if (target == null)
            target = mob;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_NOISYMOVEMENT | CMMsg.MASK_DELICATE | (auto ? CMMsg.MASK_ALWAYS : 0), immitations.get(found));
            if (mob.location().okMessage(mob, msg))
                mob.location().send(mob, msg);
        } else
            return beneficialVisualFizzle(mob, null, L("<S-NAME> attempt(s) to imitate @x1, but fail(s).", found));

        return success;
    }
}
