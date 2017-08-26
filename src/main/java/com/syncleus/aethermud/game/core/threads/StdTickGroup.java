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
package com.syncleus.aethermud.game.core.threads;

import com.syncleus.aethermud.game.Abilities.interfaces.ItemTicker;
import com.syncleus.aethermud.game.Libraries.interfaces.ThreadEngine;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.STreeSet;
import com.syncleus.aethermud.game.core.interfaces.TickClient;
import com.syncleus.aethermud.game.core.interfaces.Tickable;
import com.syncleus.aethermud.game.core.interfaces.TickableGroup;

import java.util.Iterator;
import java.util.LinkedList;


public class StdTickGroup implements TickableGroup, Cloneable {
    private static volatile int tickObjReference = 0;
    private final ThreadEngine myEngine;
    private final long tickTime;
    private final int tickObjectCounter;
    private final String name;
    private final String threadGroupName;
    private final boolean solitaryTicker;
    private final STreeSet<TickClient> tickers = new STreeSet<TickClient>();
    private volatile long nextTickTime;
    private volatile long lastStart = 0;
    private volatile long lastStop = 0;
    private volatile long milliTotal = 0;
    private volatile int tickTotal = 0;
    private volatile TickClient lastClient = null;
    private volatile Thread currentThread = null;

    public StdTickGroup(long sleep, String threadGroupName, boolean isSolitary) {
        name = "Tick." + (tickObjReference + 1);
        tickObjectCounter = tickObjReference++;
        tickTime = sleep;
        nextTickTime = System.currentTimeMillis() + tickTime;
        myEngine = null;
        solitaryTicker = isSolitary;
        this.threadGroupName = threadGroupName;
    }

    public StdTickGroup(String a_name, long sleep, String threadGroupName, boolean isSolitary) {
        name = "Tick." + a_name + "." + (tickObjReference + 1);
        tickObjectCounter = tickObjReference++;
        tickTime = sleep;
        nextTickTime = System.currentTimeMillis() + tickTime;
        myEngine = null;
        solitaryTicker = isSolitary;
        this.threadGroupName = threadGroupName;
    }

    public StdTickGroup(ThreadEngine theEngine, long sleep, String threadGroupName, boolean isSolitary) {
        name = "Tick." + (tickObjReference + 1);
        tickObjectCounter = tickObjReference++;
        myEngine = theEngine;
        tickTime = sleep;
        nextTickTime = System.currentTimeMillis() + tickTime;
        solitaryTicker = isSolitary;
        this.threadGroupName = threadGroupName;
    }

    public StdTickGroup copyOf() {
        try {
            final StdTickGroup T = (StdTickGroup) this.clone();
            T.tickers.clear();
            T.tickers.addAll(tickers);
            return T;
        } catch (final Exception e) {
        }
        return this;
    }

    @Override
    public long getStartTime() {
        return lastStart;
    }

    @Override
    public int getGroupID() {
        return threadGroupName.charAt(0);
    }

    @Override
    public TickClient fetchTickerByIndex(int i) {
        int x = 0;
        for (final TickClient C : tickers) {
            if (i == (x++))
                return C;
        }
        return null;
    }

    @Override
    public Thread getCurrentThread() {
        return currentThread;
    }

    @Override
    public String getThreadGroupName() {
        return threadGroupName;
    }

    @Override
    public long getLastStartTime() {
        return lastStart;
    }

    @Override
    public long getLastStopTime() {
        return lastStop;
    }

    @Override
    public long getMilliTotal() {
        return milliTotal;
    }

    @Override
    public long getTickTotal() {
        return tickTotal;
    }

    @Override
    public boolean isSolitaryTicker() {
        return solitaryTicker;
    }

    @Override
    public boolean isAwake() {
        return currentThread != null;
    }

    @Override
    public Iterator<TickClient> tickers() {
        return tickers.iterator();
    }

    @Override
    public int numTickers() {
        return tickers.size();
    }

    @Override
    public Iterator<TickClient> getTickSet(final Tickable T, final int tickID) {
        final LinkedList<TickClient> subSet = new LinkedList<TickClient>();
        if (tickID < 0)
            subSet.addAll(tickers.subSet(new StdTickClient(T, 0, 0), true, new StdTickClient(T, 0, Integer.MAX_VALUE), true));
        else
            subSet.addAll(tickers.subSet(new StdTickClient(T, 0, tickID), true, new StdTickClient(T, 0, tickID), true));
        return subSet.iterator();
    }

    @Override
    public Iterator<TickClient> getLocalItems(int itemTypes, Room R) {
        LinkedList<TickClient> localItems = null;
        for (TickClient C : tickers) {
            switch (itemTypes) {
                case 0:
                    if (C.getClientObject() instanceof MOB) {
                        if (((MOB) C.getClientObject()).getStartRoom() == R) {
                            if (localItems == null)
                                localItems = new LinkedList<TickClient>();
                            localItems.add(C);
                        }
                    } else if ((C.getClientObject() instanceof ItemTicker)
                        && ((((ItemTicker) C.getClientObject()).properLocation() == R))) {
                        if (localItems == null)
                            localItems = new LinkedList<TickClient>();
                        localItems.add(C);
                    }
                    break;
                case 1:
                    if ((C.getClientObject() instanceof ItemTicker)
                        && ((((ItemTicker) C.getClientObject()).properLocation() == R))) {
                        if (localItems == null)
                            localItems = new LinkedList<TickClient>();
                        localItems.add(C);
                    }
                    break;
                case 2:
                    if ((C.getClientObject() instanceof MOB)
                        && (((MOB) C.getClientObject()).getStartRoom() == R)) {
                        if (localItems == null)
                            localItems = new LinkedList<TickClient>();
                        localItems.add(C);
                    }
                    break;
            }
        }
        if (localItems == null)
            return null;
        return localItems.iterator();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getTickInterval() {
        return tickTime;
    }

    @Override
    public long getNextTickTime() {
        return nextTickTime;
    }

    @Override
    public boolean contains(final Tickable T, final int tickID) {
        if (tickID >= 0)
            return tickers.contains(new StdTickClient(T, 0, tickID));
        return tickers.subSet(new StdTickClient(T, 0, -1), true, new StdTickClient(T, 0, Integer.MAX_VALUE), true).size() > 0;
    }

    public int getCounter() {
        return tickObjectCounter;
    }

    @Override
    public void delTicker(TickClient C) {
        tickers.remove(C);
    }

    @Override
    public void addTicker(TickClient C) {
        if (!tickers.contains(C))
            tickers.add(C);
    }

    @Override
    public TickClient getLastTicked() {
        return lastClient;
    }

    @Override
    public String getStatus() {
        final TickClient lastTicked = getLastTicked();
        if (!isAwake())
            return "Sleeping";
        if ((lastTicked == null) || (lastTicked.getClientObject() == null))
            return "Shutdown";
        final Tickable ticker = lastTicked.getClientObject();
        return ticker.ID() + ": " + ticker.name() + ": " + ((myEngine != null) ? myEngine.getTickStatusSummary(ticker) : "null");
    }

    @Override
    public void shutdown() {
        tickers.clear();
        if (CMLib.threads() instanceof ServiceEngine)
            ((ServiceEngine) CMLib.threads()).delTickGroup(this);
    }

    @Override
    public void run() {

        nextTickTime = System.currentTimeMillis() + tickTime;
        //final String oldThreadName=Thread.currentThread().getName();
        try {
            currentThread = Thread.currentThread();
            lastStart = System.currentTimeMillis();
            lastClient = null;
            final boolean allSuspended = CMLib.threads().isAllSuspended();
            if ((CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
                && (!allSuspended)) {
                for (final Iterator<TickClient> i = tickers(); i.hasNext(); ) {
                    final TickClient client = i.next();
                    lastClient = client;
                    //if(client.getCurrentTickDown()<=1) currentThread.setName(oldThreadName+":"+getName()+":"+client.getName());
                    if (client.tickTicker(false)) {
                        delTicker(client); // cant do i.remove, its an streeset
                    }
                }
            }
        } finally {
            lastStop = System.currentTimeMillis();
            milliTotal += (lastStop - lastStart);
            tickTotal++;
            currentThread = null;
            //Thread.currentThread().setName(oldThreadName);
        }
        if (tickers.size() == 0) {
            if (CMLib.threads() instanceof ServiceEngine)
                ((ServiceEngine) CMLib.threads()).delTickGroup(this);
        }
    }

    @Override
    public long activeTimeMillis() {
        if (this.isAwake())
            return System.currentTimeMillis() - this.lastStart;
        return 0;
    }
}