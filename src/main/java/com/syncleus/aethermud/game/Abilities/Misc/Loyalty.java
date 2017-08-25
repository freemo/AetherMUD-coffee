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
package com.syncleus.aethermud.game.Abilities.Misc;

import com.syncleus.aethermud.game.Abilities.StdAbility;
import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB.Attrib;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.lang.ref.WeakReference;


public class Loyalty extends StdAbility {
    protected final static int INTERVAL = (2 * 60) / 4000;
    private final static String localizedName = CMLib.lang().L("Loyalty");
    private final static String localizedStaticDisplay = "(Loyal to @x1)";
    protected String loyaltyName = "";
    protected boolean teleport = false;
    protected volatile boolean watchForMaster = true;
    protected int checkDown = INTERVAL;
    protected WeakReference<MOB> loyaltyPlayer = null;

    @Override
    public String ID() {
        return "Loyalty";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return CMLib.lang().L(localizedStaticDisplay, loyaltyName);
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
    public int classificationCode() {
        return Ability.ACODE_PROPERTY;
    }

    @Override
    public void setMiscText(String newMiscText) {
        super.setMiscText(newMiscText);
        loyaltyName = CMParms.getParmStr(newMiscText, "NAME", "");
        teleport = CMParms.getParmBool(newMiscText, "TELEPORT", false);
    }

    protected MOB getPlayer() {
        if ((loyaltyPlayer != null)
            && (loyaltyPlayer.get() != null)
            && (!loyaltyPlayer.get().amDestroyed()))
            return loyaltyPlayer.get();
        final MOB player = CMLib.players().getLoadPlayer(loyaltyName);
        if (player == null) {
            loyaltyName = "";
            if (affected != null)
                affected.delEffect(this);
        } else
            loyaltyPlayer = new WeakReference<MOB>(player);
        return player;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((ticking instanceof MOB)
            && (tickID == Tickable.TICKID_MOB)
            && (loyaltyName.length() > 0)) {
            final MOB M = (MOB) ticking;
            final MOB player = getPlayer();
            if (--checkDown <= 0) {
                checkDown = INTERVAL;
                watchForMaster = false;
                if (CMLib.flags().canFreelyBehaveNormal(M) && (player != null)) {
                    watchForMaster = true;
                    if (CMLib.flags().isInTheGame(player, true) && (player.location() != null) && (!M.isAttributeSet(Attrib.AUTOGUARD)) && teleport)
                        CMLib.tracking().wanderCheckedFromTo(M, player.location(), true);
                }
            }
            if ((watchForMaster) && (player != null) && (player.location() == M.location()) && (M.amFollowing() == null)) {
                CMLib.commands().postFollow(M, player, false);
                if (M.amFollowing() == player)
                    watchForMaster = false;
            }
        }
        return super.tick(ticking, tickID);
    }
}
