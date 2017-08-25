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
package com.syncleus.aethermud.game.Abilities.Properties;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class Prop_RestrictSkills extends Property {
    protected Set<Integer> onlyRoomDomains = new TreeSet<Integer>();
    protected Set<Integer> neverRoomDomains = new TreeSet<Integer>();
    protected Set<String> skills = new TreeSet<String>();
    protected String message = L("You can't do that here.");

    @Override
    public String ID() {
        return "Prop_RestrictSkills";
    }

    @Override
    public String name() {
        return "Specific Skill Neutralizing";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS | Ability.CAN_AREAS | Ability.CAN_MOBS | Ability.CAN_ITEMS;
    }

    @Override
    public void setMiscText(String newMiscText) {
        onlyRoomDomains.clear();
        neverRoomDomains.clear();
        skills.clear();
        super.setMiscText(newMiscText);
        this.message = CMParms.getParmStr(newMiscText, "MESSAGE", "You can't do that here.");
        String domains = CMParms.getParmStr(newMiscText, "ONLYROOMS", "");
        List<String> domainList = CMParms.parseCommas(domains, true);
        for (String domain : domainList) {
            int x = CMParms.indexOf(Room.DOMAIN_INDOORS_DESCS, domain.toUpperCase().trim());
            if (x >= 0)
                onlyRoomDomains.add(Integer.valueOf(Room.INDOORS + x));
            else {
                x = CMParms.indexOf(Room.DOMAIN_OUTDOOR_DESCS, domain.toUpperCase().trim());
                if (x >= 0)
                    onlyRoomDomains.add(Integer.valueOf(x));
            }
        }
        domains = CMParms.getParmStr(newMiscText, "NEVERROOMS", "");
        domainList = CMParms.parseCommas(domains, true);
        for (String domain : domainList) {
            int x = CMParms.indexOf(Room.DOMAIN_INDOORS_DESCS, domain.toUpperCase().trim());
            if (x >= 0)
                neverRoomDomains.add(Integer.valueOf(Room.INDOORS + x));
            else {
                x = CMParms.indexOf(Room.DOMAIN_OUTDOOR_DESCS, domain.toUpperCase().trim());
                if (x >= 0)
                    neverRoomDomains.add(Integer.valueOf(x));
            }
        }

        String skillStr = CMParms.getParmStr(newMiscText, "SKILLS", "");
        List<String> skillList = CMParms.parseCommas(skillStr, true);
        for (String skill : skillList) {
            final Ability A = CMClass.getAbility(skill);
            if (A != null) {
                skills.add(A.ID());
            }
        }

    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if ((myHost instanceof MOB) && (msg.source() != myHost))
            return true;

        if ((myHost instanceof Item) && (((Item) myHost).owner() != msg.source()))
            return true;

        if ((msg.tool() instanceof Ability)
            && (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
            && (skills.contains(msg.tool().ID()))) {
            Room roomS = null;
            Room roomD = null;
            if ((msg.target() instanceof MOB) && (((MOB) msg.target()).location() != null))
                roomD = ((MOB) msg.target()).location();
            else if (msg.source().location() != null)
                roomS = msg.source().location();
            else if (msg.target() instanceof Room)
                roomD = (Room) msg.target();

            if ((roomS != null) && (roomD != null) && (roomS == roomD))
                roomD = null;

            final Room room = msg.source().location();
            if (room != null) {
                if ((onlyRoomDomains.size() > 0) && (onlyRoomDomains.contains(Integer.valueOf(room.domainType()))))
                    return true;
                if ((neverRoomDomains.size() > 0) && (!neverRoomDomains.contains(Integer.valueOf(room.domainType()))))
                    return true;
                if (!msg.source().isMonster())
                    msg.source().tell(L(message));
                return false;
            }
        }
        return true;
    }
}
