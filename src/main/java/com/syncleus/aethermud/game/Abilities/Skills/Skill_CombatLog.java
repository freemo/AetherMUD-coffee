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
package com.syncleus.aethermud.game.Abilities.Skills;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.Faction;
import com.syncleus.aethermud.game.Common.interfaces.Session;
import com.syncleus.aethermud.game.Common.interfaces.TimeClock;
import com.syncleus.aethermud.game.Items.interfaces.Coins;
import com.syncleus.aethermud.game.Items.interfaces.DeadBody;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.*;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.*;


public class Skill_CombatLog extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Combat Log");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Combat Logging: ");
    private static final String[] triggerStrings = I(new String[]{"COMBATLOG"});
    protected Map<CombatStat, long[]> stats = new Hashtable<CombatStat, long[]>();
    protected Map<Faction, long[]> factionChanges = new Hashtable<Faction, long[]>();
    protected MOB loggingM = null;
    protected long secondsPerTick = (CMProps.getTickMillis() / 1000);
    protected volatile boolean wasInCombat = false;
    protected volatile int numCombatants = 0;
    protected volatile int numManaLastTick = 0;
    protected volatile int numMovesLastTick = 0;

    @Override
    public String ID() {
        return "Skill_CombatLog";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        if (loggingM != null)
            return localizedStaticDisplay + loggingM.name() + ")";
        else
            return "";
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_COMBATLORE;
    }

    @Override
    public int maxRange() {
        return adjustedMaxInvokerRange(20);
    }

    @Override
    public int usageType() {
        return USAGE_MANA;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        return true;
    }

    protected String getReport(final Session session, final MOB mob, final int level) {
        StringBuilder rep = new StringBuilder("");
        rep.append(L("Combat statistics for @x1 \n\r", mob.name()));
        if (stats.size() == CombatStat.values().length) {
            final Map<CombatStat, long[]> stats = new Hashtable<CombatStat, long[]>();
            stats.putAll(this.stats);
            final TimeClock clock = CMLib.time().localClock(mob);
            final String totalLogTime = clock.deriveEllapsedTimeString(stats.get(CombatStat.NUM_SECONDS_TOTAL)[0] * 1000);
            rep.append(CMStrings.padRight(L("Total Log Time: @x1\n\r", totalLogTime), 38));
            final String totalCombatTime = clock.deriveEllapsedTimeString(stats.get(CombatStat.NUM_SECONDS_COMBAT)[0] * 1000);
            rep.append(L("Total Combat Time: @x1\n\r", totalCombatTime));

            int colWidth = CMLib.lister().fixColWidth(26, session);
            rep.append("\n\r");

            rep.append(CMStrings.padRight(L("Enemies defeated: @x1", "" + stats.get(CombatStat.NUM_ENEMIES_KILLED)[0]), colWidth));
            if (level >= 5) {
                double npc = Math.round(stats.get(CombatStat.NUM_ENEMIES_KILLED)[0] / stats.get(CombatStat.NUM_COMBATS)[0]);
                rep.append(L("Enemies per Combat: @x1", "" + npc));
                rep.append("\n\r");
            }
            rep.append("\n\r");

            if (level >= 4) {
                rep.append(CMStrings.padRight(L("Gold gained: @x1", "" + stats.get(CombatStat.NUM_GOLD_LOOTED)[0]), colWidth));
                if (level >= 9) {
                    double age = Math.round(stats.get(CombatStat.NUM_GOLD_LOOTED)[0] / stats.get(CombatStat.NUM_ENEMIES_KILLED)[0]);
                    rep.append(CMStrings.padRight(L("Gold per enemy: @x1", "" + age), colWidth));
                    if (level >= 10) {
                        double agh = Math.round(stats.get(CombatStat.NUM_GOLD_LOOTED)[0] / ((stats.get(CombatStat.NUM_SECONDS_TOTAL)[0] * 1000) / CMProps.getMillisPerMudHour()));
                        rep.append(L("Gold per game-hour: @x1", "" + agh));
                    }
                }
                rep.append("\n\r");
            }
            if (level >= 4) {
                rep.append(CMStrings.padRight(L("Exp. gained: @x1", "" + stats.get(CombatStat.NUM_XP_GAINED)[0]), colWidth));
                if (level >= 9) {
                    double age = Math.round(stats.get(CombatStat.NUM_XP_GAINED)[0] / stats.get(CombatStat.NUM_ENEMIES_KILLED)[0]);
                    rep.append(CMStrings.padRight(L("Exp. per enemy: @x1", "" + age), colWidth));
                    if (level >= 10) {
                        double agh = Math.round(stats.get(CombatStat.NUM_XP_GAINED)[0] / ((stats.get(CombatStat.NUM_SECONDS_TOTAL)[0] * 1000) / CMProps.getMillisPerMudHour()));
                        rep.append(L("Exp. per game-hour: @x1", "" + agh));
                    }
                }
                rep.append("\n\r");
            }

            rep.append(CMStrings.padRight(L("Damage done: @x1", "" + stats.get(CombatStat.NUM_DAMAGE_DONE)[0]), colWidth));
            if (level >= 2) {
                double adc = Math.round(stats.get(CombatStat.NUM_DAMAGE_DONE)[0] / stats.get(CombatStat.NUM_COMBATS)[0]);
                rep.append(CMStrings.padRight(L("Damage per combat: @x1", "" + adc), colWidth));
                if (level >= 6) {
                    double ads = Math.round(stats.get(CombatStat.NUM_DAMAGE_DONE)[0] / stats.get(CombatStat.NUM_SECONDS_TOTAL)[0]);
                    rep.append(L("Damage per second: @x1", "" + ads));
                }
            }
            rep.append("\n\r");

            rep.append(CMStrings.padRight(L("Healing done: @x1", "" + stats.get(CombatStat.NUM_HEALING_DONE)[0]), colWidth));
            if (level >= 2) {
                double adc = Math.round(stats.get(CombatStat.NUM_HEALING_DONE)[0] / stats.get(CombatStat.NUM_COMBATS)[0]);
                rep.append(CMStrings.padRight(L("Healing per combat: @x1", "" + adc), colWidth));
                if (level >= 6) {
                    double ads = Math.round(stats.get(CombatStat.NUM_HEALING_DONE)[0] / stats.get(CombatStat.NUM_SECONDS_TOTAL)[0]);
                    rep.append(L("Healing per second: @x1", "" + ads));
                }
            }
            rep.append("\n\r");

            rep.append(CMStrings.padRight(L("Damage taken: @x1", "" + stats.get(CombatStat.NUM_DAMAGE_TAKEN)[0]), colWidth));
            if (level >= 2) {
                double adc = Math.round(stats.get(CombatStat.NUM_DAMAGE_TAKEN)[0] / stats.get(CombatStat.NUM_COMBATS)[0]);
                rep.append(CMStrings.padRight(L("Dmg taken/combat: @x1", "" + adc), colWidth));
                if (level >= 6) {
                    double ads = Math.round(stats.get(CombatStat.NUM_DAMAGE_TAKEN)[0] / stats.get(CombatStat.NUM_SECONDS_TOTAL)[0]);
                    rep.append(L("Dmg taken/second: @x1", "" + ads));
                }
            }
            rep.append("\n\r");

            if (level >= 1) {
                rep.append(CMStrings.padRight(L("Mana used: @x1", "" + stats.get(CombatStat.NUM_MANA_USED)[0]), colWidth));
                if (level >= 3) {
                    double muc = Math.round(stats.get(CombatStat.NUM_MANA_USED_COMBAT)[0] / stats.get(CombatStat.NUM_COMBATS)[0]);
                    rep.append(CMStrings.padRight(L("Mana used/combat: @x1", "" + muc), colWidth));
                    if (level >= 7) {
                        double mus = Math.round(stats.get(CombatStat.NUM_MANA_USED_COMBAT)[0] / stats.get(CombatStat.NUM_SECONDS_TOTAL)[0]);
                        rep.append(L("Mana used/second: @x1", "" + mus));
                    }
                }
                rep.append("\n\r");
            }

            if (level >= 1) {
                rep.append(CMStrings.padRight(L("Moves used: @x1", "" + stats.get(CombatStat.NUM_MOVEMENT_USED)[0]), colWidth));
                if (level >= 3) {
                    double muc = Math.round(stats.get(CombatStat.NUM_MOVEMENT_USED_COMBAT)[0] / stats.get(CombatStat.NUM_COMBATS)[0]);
                    rep.append(CMStrings.padRight(L("Moves used/combat: @x1", "" + muc), colWidth));
                    if (level >= 7) {
                        double mus = Math.round(stats.get(CombatStat.NUM_MOVEMENT_USED_COMBAT)[0] / stats.get(CombatStat.NUM_SECONDS_TOTAL)[0]);
                        rep.append(L("Moves used/second: @x1", "" + mus));
                    }
                }
                rep.append("\n\r");
            }

            if (level >= 5) {
                rep.append("\n\r");
                for (final Faction F : factionChanges.keySet()) {
                    final long[] val = factionChanges.get(F);
                    if (val[0] > 0)
                        rep.append(L("@x1 gained: @x2\n\r", F.name(), "" + val[0]));
                    if (val[1] < 0)
                        rep.append(L("@x1 lost: @x2\n\r", F.name(), "" + val[1]));
                }
            }
            rep.append("\n\r");
        }
        return rep.toString();
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if ((msg.sourceMinor() == CMMsg.TYP_DEATH)
            && (msg.tool() == affected)
            && (msg.tool() instanceof MOB)) {
            if (stats.size() == CombatStat.values().length)
                stats.get(CombatStat.NUM_ENEMIES_KILLED)[0]++;
        }
        if (msg.source() == affected) {
            switch (msg.sourceMinor()) {
                case CMMsg.TYP_EXPCHANGE:
                    if (stats.size() == CombatStat.values().length)
                        stats.get(CombatStat.NUM_XP_GAINED)[0] += msg.value();
                    break;
                case CMMsg.TYP_GET:
                    if ((msg.tool() instanceof Coins)
                        && (msg.target() instanceof DeadBody)) {
                        if (stats.size() == CombatStat.values().length)
                            stats.get(CombatStat.NUM_GOLD_LOOTED)[0] += Math.round(((Coins) msg.tool()).getTotalValue());
                    }
                    break;
                case CMMsg.TYP_DAMAGE:
                    if (msg.target() instanceof MOB) {
                        if (stats.size() == CombatStat.values().length)
                            stats.get(CombatStat.NUM_DAMAGE_DONE)[0] += msg.value();
                    }
                    break;
                case CMMsg.TYP_HEALING:
                    if (msg.target() instanceof MOB) {
                        if (stats.size() == CombatStat.values().length)
                            stats.get(CombatStat.NUM_HEALING_DONE)[0] += msg.value();
                    }
                    break;
                case CMMsg.TYP_FACTIONCHANGE:
                    if (msg.othersMessage() != null) {
                        if ((msg.value() < Integer.MAX_VALUE) && (msg.value() > Integer.MIN_VALUE)) {
                            final Faction F = CMLib.factions().getFaction(msg.othersMessage());
                            if (F != null) {
                                if (!factionChanges.containsKey(F))
                                    factionChanges.put(F, new long[]{0, 0});
                                if (msg.value() > 0)
                                    factionChanges.get(F)[0] += msg.value();
                                else if (msg.value() < 0)
                                    factionChanges.get(F)[1] -= msg.value();
                            }
                        }
                    }
                    break;
            }
        } else if ((msg.target() == affected)
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)) {
            if (stats.size() == CombatStat.values().length)
                stats.get(CombatStat.NUM_DAMAGE_TAKEN)[0] += msg.value();
        }
    }

    @Override
    public boolean tick(final Tickable ticking, int tickID) {
        if (stats.size() == 0) {
            for (CombatStat stat : CombatStat.values())
                stats.put(stat, new long[]{0});
            secondsPerTick = (CMProps.getTickMillis() / 1000);
        }
        stats.get(CombatStat.NUM_SECONDS_TOTAL)[0] += secondsPerTick;
        final Physical affected = this.affected;
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            int manaChange = 0;
            if (mob.curState().getMana() < this.numManaLastTick)
                manaChange = (this.numManaLastTick - mob.curState().getMana());
            int moveChange = 0;
            if (mob.curState().getMovement() < this.numMovesLastTick)
                moveChange = (this.numMovesLastTick - mob.curState().getMovement());

            stats.get(CombatStat.NUM_MANA_USED)[0] += manaChange;
            stats.get(CombatStat.NUM_MOVEMENT_USED)[0] += moveChange;
            if (mob.isInCombat()) {
                stats.get(CombatStat.NUM_MANA_USED_COMBAT)[0] += manaChange;
                stats.get(CombatStat.NUM_MOVEMENT_USED_COMBAT)[0] += moveChange;

                stats.get(CombatStat.NUM_SECONDS_COMBAT)[0] += secondsPerTick;

                if (!this.wasInCombat) {
                    this.wasInCombat = true;
                    stats.get(CombatStat.NUM_COMBATS)[0]++;
                    this.numCombatants = 0;
                }
                final Room R = mob.location();
                if (R != null) {
                    int numCounted = 0;
                    for (Enumeration<MOB> r = R.inhabitants(); r.hasMoreElements(); ) {
                        final MOB M = r.nextElement();
                        if ((M != mob)
                            && (M.getVictim() == mob))
                            numCounted++;
                    }
                    if (numCounted > this.numCombatants)
                        this.numCombatants = numCounted;
                }
                stats.get(CombatStat.NUM_COMBAT_ROUNDS)[0]++;
            } else {
                if (this.wasInCombat) {
                    this.wasInCombat = false;
                    stats.get(CombatStat.NUM_ENEMIES_FOUGHT)[0] += this.numCombatants;
                }
            }
            this.numManaLastTick = mob.curState().getMana();
            this.numMovesLastTick = mob.curState().getMana();
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void unInvoke() {
        super.unInvoke();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final boolean logging = (this.loggingM != null) && (this.loggingM.fetchEffect(ID()) != null);

        if (commands.size() == 0) {
            if (!logging)
                mob.tell(L("Combat log whom?"));
            else
                mob.tell(L("Do what? STOP to stop logging. REPORT to announce your current log.  WRITE <target> to write it down."));
            return false;
        }
        final Room R = mob.location();
        if (R == null)
            return false;
        final MOB target;
        if (!logging) {
            target = this.getTarget(mob, commands, givenTarget);
            if (target == null)
                return false;
        } else {
            String cmd = commands.get(0).toUpperCase().trim();
            if ("STOP".startsWith(cmd)) {
                if (this.loggingM != null) {
                    final String name = this.loggingM.name(mob);
                    final Ability A = this.loggingM.fetchEffect(ID());
                    if (A != null)
                        A.unInvoke();
                    this.loggingM = null;
                    mob.tell(L("You stop combat logging on @x1.", name));
                }
                return true;
            } else if ("REPORT".startsWith(cmd)) {
                CMLib.commands().postSay(mob, this.getReport(mob.session(), loggingM, super.getXLEVELLevel(mob)));
                return true;
            } else if ("WRITE".startsWith(cmd)) {
                Skill_Write write = (Skill_Write) mob.fetchAbility("Skill_Write");
                if (write == null) {
                    mob.tell(L("You don't know how to write!"));
                    return false;
                }
                if (commands.size() < 2) {
                    mob.tell(L("Write the report on what?"));
                    return false;
                }
                String onWhat = CMParms.combine(commands, 1);
                List<String> writeParms = new ArrayList<String>();
                writeParms.add(onWhat);
                writeParms.add(this.getReport(null, loggingM, super.getXLEVELLevel(mob)));
                return write.invoke(mob, writeParms, null, auto, asLevel);
            } else {
                mob.tell(L("'@x1' is an unknown command while logging.  Try STOP, REPORT, or WRITE <target>."));
                return false;
            }
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MASK_EYES | CMMsg.TYP_OK_VISUAL | (auto ? CMMsg.MASK_ALWAYS : 0), L("<S-NAME> start(s) watching <T-YOUPOSS> combat maneuvers."));
            if (R.okMessage(mob, msg)) {
                R.send(mob, msg);
                final Skill_CombatLog log = (Skill_CombatLog) super.beneficialAffect(mob, target, asLevel, Integer.MAX_VALUE / 2);
                if (log != null) {
                    log.makeLongLasting();
                }
            }
        } else
            return beneficialVisualFizzle(mob, target, L("<S-NAME> attempt(s) to start combat logging on <T-NAMESELF>, but fail(s)."));
        return success;
    }

    protected enum CombatStat {
        NUM_COMBATS,
        NUM_COMBAT_ROUNDS,
        NUM_ENEMIES_FOUGHT,
        NUM_ENEMIES_KILLED,
        NUM_XP_GAINED,
        NUM_GOLD_LOOTED,
        NUM_SECONDS_TOTAL,
        NUM_SECONDS_COMBAT,
        NUM_MANA_USED,
        NUM_MANA_USED_COMBAT,
        NUM_MOVEMENT_USED,
        NUM_MOVEMENT_USED_COMBAT,
        NUM_DAMAGE_DONE,
        NUM_DAMAGE_TAKEN,
        NUM_HEALING_DONE,
    }
}
