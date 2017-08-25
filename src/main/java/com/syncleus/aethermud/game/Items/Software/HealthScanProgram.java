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
package com.syncleus.aethermud.game.Items.Software;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Abilities.interfaces.DiseaseAffect;
import com.syncleus.aethermud.game.Abilities.interfaces.HealthCondition;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.CagedAnimal;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.*;
import com.syncleus.aethermud.game.core.interfaces.PhysicalAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class HealthScanProgram extends GenSoftware {
    protected WeakReference<MOB> lastMOBChecked = null;

    public HealthScanProgram() {
        super();
        setName("a healthscan minidisk");
        setDisplayText("a minidisk sits here.");
        setDescription("Healthscan software, for small computer/scanners, will diagnose anomalies in organic life.");
        super.setCurrentScreenDisplay("HEALTHSCAN [TARGET] : Check health of the living target.\n\r");
        basePhyStats().setWeight(1); // the higher the weight, the wider the scan
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "HealthScanProgram";
    }

    @Override
    public String getParentMenu() {
        return "";
    }

    @Override
    public String getInternalName() {
        return "";
    }

    public boolean isAlive(MOB M) {
        // there you have it, the definition of "life" -- is biological, and can reproduce
        return ((M != null) && (!CMLib.flags().isGolem(M)) && (M.charStats().getMyRace().canBreedWith(M.charStats().getMyRace())));
    }

    public CMMsg getScanMsg(Room R) {
        return CMClass.getMsg(CMLib.map().getFactoryMOB(R), null, this, CMMsg.MASK_CNTRLMSG | CMMsg.MSG_LOOK, null); // cntrlmsg is important
    }

    public String getScanMsg(MOB viewerM, MOB M) {
        final Room R = CMLib.map().roomLocation(M);
        if (R == null)
            return "";
        final StringBuilder str = new StringBuilder("");
        final char gender = (char) M.charStats().getStat(CharStats.STAT_GENDER);
        final String genderName = (gender == 'M') ? "male" : (gender == 'F') ? "female" : "neuter";
        str.append(M.name(viewerM) + " is a " + genderName + " " + M.charStats().getMyRace().name() + ".\n\r");
        final String age = CMLib.flags().getAge(M);
        if (!CMSecurity.isDisabled(CMSecurity.DisFlag.ALL_AGEING))
            str.append(L("Biological age: @x1.\n\r", age));
        str.append("Health: " + CMath.toPct(M.curState().getHitPoints() / M.maxState().getHitPoints())
            + "  " + CMStrings.removeColors(M.healthText(viewerM)) + "\n\r");
        final List<Ability> diseases = CMLib.flags().domainAffects(M, Ability.ACODE_DISEASE);
        for (final Ability A : diseases) {
            int[] spreadBits = new int[0];
            if (A instanceof DiseaseAffect) {
                str.append(CMath.appendNumAppendage(((DiseaseAffect) A).difficultyLevel())).append(" level");
                spreadBits = CMath.getAllBitsSet(((DiseaseAffect) A).spreadBitmap());
            }
            str.append(L("@x1 has been detected", A.name()));
            if (spreadBits.length > 0) {
                str.append(L(", which is spread by: "));
                final List<String> spreadList = new ArrayList<String>();
                for (final int i : spreadBits)
                    spreadList.add(DiseaseAffect.SPREAD_DESCS[i]);
                str.append(CMLib.english().toEnglishStringList(spreadList.toArray(new String[0])));
            }
            str.append(".\n\r");
        }
        int found = 0;
        for (int a = 0; a < M.numAllEffects(); a++) {
            final Ability A = M.fetchEffect(a);
            if ((A instanceof HealthCondition) && (!(A instanceof DiseaseAffect))) // diseases handled above
            {
                found++;
                final String desc = ((HealthCondition) A).getHealthConditionDesc();
                if (desc.length() > 0)
                    str.append(desc).append("\n\r");
            }
        }
        if (found == 0) {
            if (CMLib.flags().isSleeping(M))
                str.append(M.name(viewerM) + " is sleeping.\n\r");
        }
        if (str.length() == 0)
            return "No life signs detected.";
        return str.toString().toLowerCase();
    }

    @Override
    public boolean isActivationString(String word) {
        return "healthscan".startsWith(CMLib.english().getFirstWord(word.toLowerCase()));
    }

    @Override
    public boolean isDeActivationString(String word) {
        return false;
    }

    @Override
    public boolean isCommandString(String word, boolean isActive) {
        return "healthscan".startsWith(CMLib.english().getFirstWord(word.toLowerCase()));
    }

    @Override
    public String getActivationMenu() {
        return super.getActivationMenu();
    }

    @Override
    public boolean checkActivate(MOB mob, String message) {
        return checkTyping(mob, message);
    }

    @Override
    public boolean checkDeactivate(MOB mob, String message) {
        return super.checkDeactivate(mob, message);
    }

    protected MOB getTarget(MOB mob, String name) {
        if (name.equalsIgnoreCase("self"))
            return mob;
        final Room R = mob.location();
        if (R == null)
            return null;
        MOB M = R.fetchInhabitant(name);
        if (M == null) {
            final PhysicalAgent I = R.fetchFromMOBRoomFavorsItems(mob, null, name, Wearable.FILTER_ANY);
            if (I instanceof CagedAnimal) {
                M = ((CagedAnimal) I).unCageMe();
                if (M != null) {
                    Ability ageA = M.fetchEffect("Age");
                    if (ageA == null) {
                        ageA = I.fetchEffect("Age");
                        if (ageA != null)
                            M.addNonUninvokableEffect(ageA);
                    }
                }
            }
        }
        return M;
    }

    @Override
    public boolean checkTyping(MOB mob, String message) {
        if (!super.checkTyping(mob, message))
            return false;
        final List<String> parts = CMParms.parse(message);
        if (parts.size() == 0) {
            super.addScreenMessage("Failure: HEALTHSCAN target unspecified.");
            return false;
        }
        final String name = CMParms.combine(parts, 1);
        MOB M = getTarget(mob, name);
        final Room R = (M != null) ? M.location() : null;
        if (R != null) {
            final CMMsg lookCheck = this.getScanMsg(R);
            lookCheck.setTarget(M);
            if (!R.okMessage(lookCheck.source(), lookCheck))
                M = null;
        }
        if ((M == null) || (!isAlive(M))) {
            super.addScreenMessage("Failure: HEALTHSCAN cannot track target \"" + name + "\"");
            return false;
        }
        lastMOBChecked = new WeakReference<MOB>(M);
        return true;
    }

    @Override
    public boolean checkPowerCurrent(int value) {
        return super.checkPowerCurrent(value);
    }

    @Override
    public void onActivate(MOB mob, String message) {
        onTyping(mob, message);
    }

    @Override
    public void onDeactivate(MOB mob, String message) {
        super.onDeactivate(mob, message);
    }

    @Override
    public void onTyping(MOB mob, String message) {
        super.onTyping(mob, message);
        MOB M = null;
        if (lastMOBChecked != null)
            M = lastMOBChecked.get();
        if (M != null) {
            final String scan = getScanMsg(mob, M);
            if (scan.length() > 0)
                super.addScreenMessage(scan);
        }
    }

    @Override
    public void onPowerCurrent(int value) {
        super.onPowerCurrent(value);
    }
}
