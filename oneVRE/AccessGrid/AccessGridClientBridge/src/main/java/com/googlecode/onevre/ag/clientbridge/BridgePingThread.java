/*
 * @(#)BridgePingThread.java
 * Created: 29 Aug 2007
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.googlecode.onevre.ag.clientbridge;

import java.io.IOException;

import com.googlecode.onevre.ag.agbridge.BridgeClient;
import com.googlecode.onevre.ag.agbridge.BridgeClientCreator;
import com.googlecode.onevre.ag.types.BridgeDescription;

/**
 * A Thread that pings bridges periodically
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class BridgePingThread extends Thread {

    private static final long TIMEOUT = 2000;

    // The clients
    private BridgeClient[] bridgeClients = null;

    // The bridges to ping
    private BridgeDescription[] bridges = null;

    // The delay between pings
    private int pingDelay = 0;

    // True when the thread has done
    private boolean done = false;

    /**
     * Creates a new BridgePingThread
     * @param bridges The bridges to ping
     * @param pingDelay The delay between pings
     */
    public BridgePingThread(BridgeDescription[] bridges, int pingDelay) {
        this.bridges = bridges;
        this.pingDelay = pingDelay * 1000;
        this.bridgeClients = new BridgeClient[bridges.length];
        for (int i = 0; i < bridges.length; i++) {
            try {
                bridgeClients[i] = BridgeClientCreator.create(bridges[i]);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                bridgeClients[i] = null;
            }
        }
    }

    /**
     * @see java.lang.Thread#run()
     */
    public synchronized void run() {
        for (int i = 0; i < bridges.length; i++) {
            PingThread t = new PingThread(i);
            t.start();
        }
    }

    /**
     * Closes the thread
     */
    public void close() {
        done = true;
        synchronized (this) {
            notifyAll();
		}
    }

    private class PingThread extends Thread {

        private Integer pingSync = new Integer(0);

        private int i = 0;

        private boolean pinged = false;

        private PingThread(int i) {
            this.i = i;
        }

        /**
         * @see java.lang.Thread#run()
         */
        public void run() {
            while (!done) {
                if (bridgeClients[i] != null) {
                    pinged = false;
                    Thread ping = new Thread() {
                        public void run() {
                            synchronized (pingSync) {
                                if (!pinged) {
                                    try {
                                        pingSync.wait(TIMEOUT);
                                    } catch (InterruptedException e) {
                                        // Do Nothing
                                    }
                                }
                            }
                            if (!pinged) {
                                bridges[i].setPingTime(
                                        BridgeDescription.UNREACHABLE);
                                bridges[i].setPinged(true);
                            }
                        }
                    };
                    ping.start();
                    synchronized (pingSync) {
                        long startTime = System.currentTimeMillis();
                        try {
                            bridgeClients[i].ping();
                            bridges[i].setPingTime((int)
                                    (System.currentTimeMillis()
                                        - startTime));
                            bridges[i].setPinged(true);
                            pinged = true;
                            pingSync.notifyAll();
                        } catch (IOException e) {
                            bridges[i].setPingTime(
                                    BridgeDescription.UNREACHABLE);
                            bridges[i].setPinged(true);
                        }
                    }
                }
                try {
                    Thread.sleep(pingDelay);
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
        }
    }
}
