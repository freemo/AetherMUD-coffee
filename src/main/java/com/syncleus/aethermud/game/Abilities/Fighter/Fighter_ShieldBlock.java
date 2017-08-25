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
package com.syncleus.aethermud.game.Abilities.Fighter;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Shield;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.Enumeration;


public class Fighter_ShieldBlock extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Shield Block");
    public int hits = 0;
    protected volatile int amountOfShieldArmor = -1;

    @Override
    public String ID() {
        return "Fighter_ShieldBlock";
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
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_SHIELDUSE;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        if (msg.amITarget(mob)
            && (amountOfShieldArmor > 0)
            && (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK)
            && (CMLib.flags().isAliveAwakeMobileUnbound(mob, true))
            && (msg.tool() instanceof Weapon)
            && (proficiencyCheck(null, mob.charStats().getStat(CharStats.STAT_DEXTERITY) - 90 + (2 * getXLEVELLevel(mob)), false))
            && (msg.source().getVictim() == mob)) {
            final CMMsg msg2 = CMClass.getMsg(msg.source(), mob, mob.fetchHeldItem(), CMMsg.MSG_QUIETMOVEMENT, L("<T-NAME> block(s) <S-YOUPOSS> attack with <O-NAME>!"));
            if (mob.location().okMessage(mob, msg2)) {
                mob.location().send(mob, msg2);
                helpProficiency(mob, 0);
                return false;
            }
        }
        return true;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);

        if (!(affected instanceof MOB))
            return;

        final MOB mob = (MOB) affected;

        if (msg.amISource(mob) && (msg.target() instanceof Shield))
            amountOfShieldArmor = -1;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((amountOfShieldArmor < 0) && (tickID == Tickable.TICKID_MOB) && (ticking instanceof MOB)) {
            amountOfShieldArmor = 0;
            for (final Enumeration<Item> i = ((MOB) ticking).items(); i.hasMoreElements(); ) {
                final Item I = i.nextElement();
                if ((I instanceof Shield)
                    && (I.amWearingAt(Wearable.WORN_HELD) || I.amWearingAt(Wearable.WORN_WIELD))
                    && (I.owner() == ticking)
                    && (I.container() == null))
                    amountOfShieldArmor += I.phyStats().armor();
            }
            ((MOB) ticking).recoverPhyStats();
        }
        return true;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats stats) {
        super.affectPhyStats(affected, stats);
        if ((affected instanceof MOB) && (amountOfShieldArmor > 0)) {
            stats.setArmor(stats.armor() - (int) Math.round(CMath.mul(amountOfShieldArmor, CMath.mul(getXLEVELLevel((MOB) affected), 0.5))));
        }
    }

    @Override
    public boolean autoInvocation(MOB mob, boolean force) {
        amountOfShieldArmor = -1;
        return super.autoInvocation(mob, force);
    }
}
