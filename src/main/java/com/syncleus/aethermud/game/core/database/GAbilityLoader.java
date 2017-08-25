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
package com.planet_ink.game.core.database;

import com.planet_ink.game.Libraries.interfaces.DatabaseEngine;
import com.planet_ink.game.core.Log;

import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;


public class GAbilityLoader {
    protected DBConnector DB = null;

    public GAbilityLoader(DBConnector newDB) {
        DB = newDB;
    }

    public List<DatabaseEngine.AckRecord> DBReadAbilities() {
        DBConnection D = null;
        final Vector<DatabaseEngine.AckRecord> rows = new Vector<DatabaseEngine.AckRecord>();
        try {
            D = DB.DBFetch();
            final ResultSet R = D.query("SELECT * FROM CMGAAC");
            while (R.next()) {
                final String gaid = DBConnections.getRes(R, "CMGAID");
                final String gaat = DBConnections.getRes(R, "CMGAAT");
                String gaac = DBConnections.getRes(R, "CMGACL");
                final String finalGaac = (gaac == null) || (gaac.length() == 0) ? "GenAbility" : gaac;
                rows.addElement(new DatabaseEngine.AckRecord() {
                    @Override
                    public String ID() {
                        return gaid;
                    }

                    @Override
                    public String data() {
                        return gaat;
                    }

                    @Override
                    public String typeClass() {
                        return finalGaac;
                    }
                });
            }
        } catch (final Exception sqle) {
            Log.errOut("DataLoader", sqle);
        } finally {
            DB.DBDone(D);
        }
        // log comment
        return rows;
    }

    public void DBCreateAbility(String classID, String typeClass, String data) {
        DB.updateWithClobs(
            "INSERT INTO CMGAAC ("
                + "CMGAID, "
                + "CMGAAT, "
                + "CMGACL "
                + ") values ("
                + "'" + classID + "',"
                + "?,"
                + "'" + typeClass + "'"
                + ")",
            data + " ");
    }

    public void DBDeleteAbility(String classID) {
        DB.update("DELETE FROM CMGAAC WHERE CMGAID='" + classID + "'");
    }
}
