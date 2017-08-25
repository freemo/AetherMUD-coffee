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
package com.syncleus.aethermud.game.Common;

import com.syncleus.aethermud.game.Common.interfaces.PlayerStats;
import com.syncleus.aethermud.game.Common.interfaces.Poll;
import com.syncleus.aethermud.game.Common.interfaces.Session;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMSecurity;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.collections.SVector;
import com.syncleus.aethermud.game.core.interfaces.CMObject;

import java.util.List;
import java.util.Vector;

/*
   Copyright 2005-2017 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
public class DefaultPoll implements Poll {
    public boolean loaded = false;
    protected String name = "POLL";
    protected String subject = "Poll Results Title";
    protected String description = "This is a Poll! Choose from the following:";
    protected String author = "noone";
    protected long expiration = 0;
    protected long bitmap = 0;
    protected String qualZapper = "";
    protected List<PollOption> options = new Vector<PollOption>();
    protected List<PollResult> results = new SVector<PollResult>();

    @Override
    public String ID() {
        return "DefaultPoll";
    }

    @Override
    public String name() {
        return ID();
    }

    @Override
    public CMObject newInstance() {
        try {
            return getClass().newInstance();
        } catch (final Exception e) {
            return new DefaultPoll();
        }
    }

    @Override
    public void initializeClass() {
    }

    @Override
    public int compareTo(CMObject o) {
        return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));
    }

    @Override
    public CMObject copyOf() {
        try {
            return (Poll) this.clone();
        } catch (final CloneNotSupportedException e) {
            return newInstance();
        }
    }

    @Override
    public boolean loaded() {
        return loaded;
    }

    @Override
    public void setLoaded(boolean truefalse) {
        loaded = truefalse;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String newname) {
        name = newname;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public void setSubject(String newsubject) {
        subject = newsubject;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String newdescription) {
        description = newdescription;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String newname) {
        author = newname;
    }

    @Override
    public long getFlags() {
        return bitmap;
    }

    @Override
    public void setFlags(long flag) {
        bitmap = flag;
    }

    @Override
    public String getQualZapper() {
        return qualZapper;
    }

    @Override
    public void setQualZapper(String newZap) {
        qualZapper = newZap;
    }

    @Override
    public long getExpiration() {
        return expiration;
    }

    @Override
    public void setExpiration(long time) {
        expiration = time;
    }

    @Override
    public List<PollOption> getOptions() {
        return options;
    }

    @Override
    public void setOptions(List<PollOption> V) {
        options = V;
    }

    @Override
    public List<PollResult> getResults() {
        return results;
    }

    @Override
    public void setResults(List<PollResult> V) {
        results = V;
    }

    @Override
    public String getOptionsXML() {
        if (options.size() == 0)
            return "<OPTIONS />";
        final StringBuffer str = new StringBuffer("<OPTIONS>");
        PollOption PO = null;
        for (int i = 0; i < options.size(); i++) {
            PO = options.get(i);
            str.append("<OPTION>");
            str.append(CMLib.xml().convertXMLtoTag("TEXT", CMLib.xml().parseOutAngleBrackets(PO.text)));
            str.append("</OPTION>");
        }
        str.append("</OPTIONS>");
        return str.toString();
    }

    @Override
    public String getResultsXML() {
        if (results.size() == 0)
            return "<RESULTS />";
        final StringBuffer str = new StringBuffer("<RESULTS>");
        PollResult PR = null;
        for (int i = 0; i < results.size(); i++) {
            PR = results.get(i);
            str.append("<RESULT>");
            str.append(CMLib.xml().convertXMLtoTag("USER", PR.user));
            str.append(CMLib.xml().convertXMLtoTag("IP", PR.ip));
            str.append(CMLib.xml().convertXMLtoTag("ANS", PR.answer));
            str.append("</RESULT>");
        }
        str.append("</RESULTS>");
        return str.toString();
    }

    @Override
    public PollResult getMyVote(MOB mob) {
        if (mob == null)
            return null;
        CMLib.polls().loadPollIfNecessary(this);
        PollResult R = null;
        final Session S = mob.session();
        for (int r = 0; r < results.size(); r++) {
            R = results.get(r);
            if ((mob.Name().equals(R.user)))
                return R;
            if (R.ip.length() > 0) {
                final String address = (S != null) ? S.getAddress() : "\n\r\t";
                final String accountName;
                final PlayerStats pstats = mob.playerStats();
                if ((pstats != null) && (pstats.getAccount() != null))
                    accountName = "\t" + pstats.getAccount().getAccountName();
                else
                    accountName = "\t\n";
                if ((R.ip.equals(address) || R.ip.startsWith(address + "\t") || R.ip.endsWith(accountName)))
                    return R;
            }
        }
        return null;
    }

    @Override
    public void addVoteResult(PollResult R) {
        CMLib.polls().loadPollIfNecessary(this);
        results.add(R);
        CMLib.polls().updatePollResults(this);
    }

    @Override
    public boolean mayIVote(MOB mob) {
        if (mob == null)
            return false;
        if (!CMath.bset(bitmap, FLAG_ACTIVE))
            return false;
        if (!CMLib.masking().maskCheck(qualZapper, mob, true))
            return false;
        if ((expiration > 0) && (System.currentTimeMillis() > expiration)) {
            bitmap = CMath.unsetb(bitmap, FLAG_ACTIVE);
            CMLib.polls().updatePoll(name, this);
            return false;
        }
        if (getMyVote(mob) != null)
            return false;
        return true;
    }

    @Override
    public boolean mayISeeResults(MOB mob) {
        if (mob == null)
            return false;
        if (!CMLib.masking().maskCheck(qualZapper, mob, true))
            return false;
        if (CMath.bset(bitmap, FLAG_HIDERESULTS) && (!CMSecurity.isAllowedAnywhere(mob, CMSecurity.SecFlag.POLLS)))
            return false;
        if (CMath.bset(bitmap, FLAG_PREVIEWRESULTS))
            return true;
        if ((expiration > 0) && (System.currentTimeMillis() < expiration))
            return false;
        if ((getMyVote(mob) == null) && (!CMath.bset(bitmap, FLAG_ABSTAIN)))
            return false;
        return true;
    }
}
