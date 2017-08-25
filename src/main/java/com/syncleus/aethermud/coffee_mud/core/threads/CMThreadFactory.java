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
package com.planet_ink.coffee_mud.core.threads;

import com.planet_ink.coffee_mud.core.collections.SLinkedList;

import java.util.Collection;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class CMThreadFactory implements ThreadFactory {
    private final AtomicInteger counter = new AtomicInteger();
    private final SLinkedList<Thread> active = new SLinkedList<Thread>();
    private final ThreadGroup threadGroup;
    private String serverName;

    public CMThreadFactory(String serverName) {
        this.serverName = serverName;
        this.threadGroup = Thread.currentThread().getThreadGroup();
    }

    public void setServerName(String newName) {
        this.serverName = newName;
    }

    @Override
    public Thread newThread(Runnable r) {
        final Thread t = new CMFactoryThread(threadGroup, r, serverName + "#" + counter.addAndGet(1));
        active.add(t);
        return t;
    }

    public Collection<Thread> getThreads() {
        return active;
    }
}
