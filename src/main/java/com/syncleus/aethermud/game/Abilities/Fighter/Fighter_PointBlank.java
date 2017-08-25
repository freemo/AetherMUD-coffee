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
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.AmmunitionWeapon;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Vector;


public class Fighter_PointBlank extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Point Blank Shot");
    public int checkDown = 4;
    protected List<Weapon> qualifiedWeapons = new Vector<Weapon>();

    @Override
    public String ID() {
        return "Fighter_PointBlank";
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
        return Ability.QUALITY_INDIFFERENT;
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_MARTIALLORE;
    }

    @Override
    protected void cloneFix(Ability E) {
        super.cloneFix(E);
        qualifiedWeapons = new XVector<Weapon>(((Fighter_PointBlank) E).qualifiedWeapons);
    }

    @Override
    public void setMiscText(String newText) {
        super.setMiscText(newText);
        qualifiedWeapons = new Vector<Weapon>();
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        super.executeMsg(host, msg);
        if (affected instanceof Weapon) {
            final Weapon targetW = (Weapon) affected;
            if ((targetW != null)
                && (targetW.amWearingAt(Wearable.IN_INVENTORY))) {
                qualifiedWeapons.remove(targetW);
                targetW.delEffect(targetW.fetchEffect(ID()));
                targetW.recoverPhyStats();
            }
        } else if ((msg.source() == affected)
            && (msg.target() instanceof AmmunitionWeapon)) {
            final AmmunitionWeapon W = (AmmunitionWeapon) msg.target();
            if ((W.weaponClassification() == Weapon.CLASS_RANGED)
                && (W.ammunitionType().length() > 0)) {
                if (((msg.targetMinor() == CMMsg.TYP_WEAR)
                    || (msg.targetMinor() == CMMsg.TYP_WIELD)
                    || (msg.targetMinor() == CMMsg.TYP_HOLD))
                    && (!qualifiedWeapons.contains(W))
                    && ((msg.source().fetchAbility(ID()) == null) || proficiencyCheck(null, 0, false))) {
                    qualifiedWeapons.add(W);
                    final Ability A = (Ability) this.copyOf();
                    A.setInvoker(invoker());
                    A.setSavable(false);
                    W.addEffect(A);
                    W.recoverPhyStats();
                } else if (((msg.targetMinor() == CMMsg.TYP_REMOVE)
                    || (msg.targetMinor() == CMMsg.TYP_DROP))
                    && (qualifiedWeapons.contains(msg.target()))) {
                    qualifiedWeapons.remove(msg.target());
                    W.delEffect(W.fetchEffect(ID()));
                    W.recoverPhyStats();
                }
            }
        }
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (affected instanceof Item)
            affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.SENSE_ITEMNOMINRANGE);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if (--checkDown <= 0) {
            checkDown = 5;
            final Item w = mob.fetchWieldedItem();
            if ((w != null)
                && (w instanceof AmmunitionWeapon)
                && (((Weapon) w).weaponClassification() == Weapon.CLASS_RANGED)
                && (((AmmunitionWeapon) w).ammunitionType().length() > 0)
                && ((mob.fetchAbility(ID()) == null) || proficiencyCheck(null, 0, false))) {
                if ((CMLib.dice().rollPercentage() < 5) && (mob.isInCombat()) && (mob.rangeToTarget() == 0))
                    helpProficiency(mob, 0);
                if (w.fetchEffect(ID()) == null) {
                    if (!qualifiedWeapons.contains(w))
                        qualifiedWeapons.add((Weapon) w);
                    final Ability A = (Ability) this.copyOf();
                    A.setSavable(false);
                    A.setInvoker(invoker());
                    w.addEffect(A);
                    w.recoverPhyStats();
                }
            }
            for (int i = qualifiedWeapons.size() - 1; i >= 0; i--) {
                final Item I = qualifiedWeapons.get(i);
                if ((I.amWearingAt(Wearable.IN_INVENTORY))
                    || (I.owner() != affected)) {
                    qualifiedWeapons.remove(I);
                    I.delEffect(I.fetchEffect(ID()));
                    I.recoverPhyStats();
                }
            }
        }
        return true;
    }
}
