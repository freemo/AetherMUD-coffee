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
package com.syncleus.aethermud.game.Abilities.Thief;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.CharClasses.interfaces.CharClass;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;


public class Thief_ContractHit extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Contract Hit");
    private static final String[] triggerStrings = I(new String[]{"CONTRACTHIT"});
    protected boolean done = false;
    protected boolean readyToHit = false;
    protected boolean hitting = false;
    protected Vector<MOB> hitmen = new Vector<MOB>();

    @Override
    public String ID() {
        return "Thief_ContractHit";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public boolean disregardsArmorCheck(MOB mob) {
        return true;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_CRIMINAL;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (affected instanceof MOB)
            if (msg.amISource((MOB) affected)
                && (msg.sourceMinor() == CMMsg.TYP_DEATH)) {
                done = true;
                unInvoke();
            }
        super.executeMsg(myHost, msg);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((affected != null) && (affected instanceof MOB) && (invoker() != null)) {
            if (super.tickDown == 1) {
                makeLongLasting();
                readyToHit = true;
            }
            final MOB mob = (MOB) affected;
            if (readyToHit && (!hitting)
                && (mob.location() != null)
                && (mob.location().domainType() == Room.DOMAIN_OUTDOORS_CITY)) {

                hitting = true;
                final int num = CMLib.dice().roll(1, 3, 3);
                int level = mob.phyStats().level();
                if (level > (invoker.phyStats().level() + (2 * getXLEVELLevel(invoker))))
                    level = (invoker.phyStats().level() + (2 * getXLEVELLevel(invoker)));
                CharClass C = CMClass.getCharClass("StdCharClass");
                if (C == null)
                    C = mob.charStats().getCurrentClass();
                for (int i = 0; i < num; i++) {
                    final MOB M = CMClass.getMOB("Assassin");
                    M.basePhyStats().setLevel(level);
                    M.recoverPhyStats();
                    M.basePhyStats().setArmor(CMLib.leveler().getLevelMOBArmor(M));
                    M.basePhyStats().setAttackAdjustment(CMLib.leveler().getLevelAttack(M));
                    M.basePhyStats().setDamage(CMLib.leveler().getLevelMOBDamage(M));
                    M.basePhyStats().setRejuv(PhyStats.NO_REJUV);
                    M.baseState().setMana(CMLib.leveler().getLevelMana(M));
                    M.baseState().setMovement(CMLib.leveler().getLevelMove(M));
                    M.baseState().setHitPoints(CMLib.dice().rollHP(level, M.basePhyStats().level()));
                    final Behavior B = CMClass.getBehavior("Thiefness");
                    B.setParms("Assassin");
                    M.addBehavior(B);
                    M.recoverPhyStats();
                    M.recoverCharStats();
                    M.recoverMaxState();
                    M.text(); // this establishes his current state.
                    hitmen.addElement(M);
                    M.bringToLife(mob.location(), true);
                    M.setStartRoom(null);
                    M.setVictim(mob);
                    mob.setVictim(M);
                    Ability A = M.fetchAbility("Thief_Hide");
                    if (A != null)
                        A.invoke(M, M, true, 0);
                    A = M.fetchAbility("Thief_BackStab");
                    if (A != null)
                        A.invoke(M, mob, false, 0);
                }
            } else if (hitting) {
                boolean anyLeft = false;
                for (int i = 0; i < hitmen.size(); i++) {
                    final MOB M = hitmen.elementAt(i);
                    if ((!M.amDead())
                        && (M.location() != null)
                        && (CMLib.flags().isInTheGame(M, false))
                        && (CMLib.flags().isAliveAwakeMobileUnbound(M, true))) {
                        anyLeft = true;
                        M.isInCombat();
                        if ((((M.getVictim() != mob))
                            || (!M.location().isInhabitant(mob)))
                            && (M.fetchEffect("Thief_Assassinate") == null)) {
                            M.setVictim(null);
                            final Ability A = M.fetchAbility("Thief_Assassinate");
                            A.setProficiency(100);
                            A.invoke(M, mob, false, 0);
                        }
                    }
                }
                if (!anyLeft)
                    unInvoke();
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void unInvoke() {
        MOB M = invoker();
        final MOB M2 = (MOB) affected;
        super.unInvoke();
        if ((M != null) && (M2 != null) && (((done) || (M2.amDead())))) {
            if (M.location() != null) {
                M.location().showHappens(CMMsg.MSG_OK_VISUAL, L("Someone steps out of the shadows and whispers something to @x1.", M.name()));
                M.tell(L("'It is done.'"));
            }
        }
        for (int i = 0; i < hitmen.size(); i++) {
            M = hitmen.elementAt(i);
            M.destroy();
        }
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (!(target instanceof MOB))
                return Ability.QUALITY_INDIFFERENT;
            if (mob.location().domainType() != Room.DOMAIN_OUTDOORS_CITY)
                return Ability.QUALITY_INDIFFERENT;
            if (mob.isInCombat())
                return Ability.QUALITY_INDIFFERENT;
            if (target == mob)
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (commands.size() < 1) {
            mob.tell(L("Who would you like to put a hit out on?"));
            return false;
        }
        if (mob.location() == null)
            return false;
        if (mob.location().domainType() != Room.DOMAIN_OUTDOORS_CITY) {
            mob.tell(L("You need to be on the streets to put out a hit."));
            return false;
        }
        if (mob.isInCombat()) {
            mob.tell(L("You are too busy to get that done right now."));
            return false;
        }

        List<MOB> V = new Vector<MOB>();
        try {
            V = CMLib.map().findInhabitants(CMLib.map().rooms(), mob, CMParms.combine(commands, 0), 10);
        } catch (final NoSuchElementException nse) {
        }
        MOB target = null;
        if (V.size() > 0)
            target = V.get(CMLib.dice().roll(1, V.size(), -1));
        if (target == null) {
            mob.tell(L("You've never heard of '@x1'.", CMParms.combine(commands, 0)));
            return false;
        }
        if (target == mob) {
            mob.tell(L("You cannot hit yourself!"));
            return false;
        }
        if (!mob.mayIFight(target)) {
            mob.tell(L("You are not allowed to put out a hit on @x1.", target.name(mob)));
            return false;
        }

        int level = target.phyStats().level();
        if (level > (mob.phyStats().level() + (2 * getXLEVELLevel(mob))))
            level = (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));
        final double goldRequired = 100.0 * level;
        final String localCurrency = CMLib.moneyCounter().getCurrency(mob.location());
        if (CMLib.moneyCounter().getTotalAbsoluteValue(mob, localCurrency) < goldRequired) {
            final String costWords = CMLib.moneyCounter().nameCurrencyShort(localCurrency, goldRequired);
            mob.tell(L("You'll need at least @x1 to put a hit out on @x2.", costWords, target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        int levelDiff = target.phyStats().level() - (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));
        if (levelDiff < 0)
            levelDiff = 0;
        levelDiff *= 10;
        final boolean success = proficiencyCheck(mob, -levelDiff, auto);

        final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MASK_ALWAYS | CMMsg.MSG_THIEF_ACT, CMMsg.MSG_THIEF_ACT, CMMsg.MSG_THIEF_ACT, L("<S-NAME> whisper(s) to a dark figure stepping out of the shadows.  The person nods and slips away."));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);

            CMLib.moneyCounter().subtractMoney(mob, localCurrency, goldRequired);
            if (success)
                maliciousAffect(mob, target, asLevel, target.phyStats().level() + 10, 0);
        }
        return success;
    }

}
