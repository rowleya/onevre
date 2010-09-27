/*
 * @(#)ClientUpdateThread.java
 * Created: 22-Sep-2006
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

package com.googlecode.onevre.ag.agclient;

import com.googlecode.onevre.ag.types.server.Venue;

/**
 * Keeps the client connected to the venue
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ClientUpdateThread extends Thread {

    // The default time between updates
    private static final float DEFAULT_LIFETIME = 10.0f;

    // The value to convert ms to seconds
    private static final int MS_TO_SECS = 1000;

    // The venue to update
    private Venue venue = null;

    // The connection to update
    private String connectionId = null;

    // The timeout before the next update
    private long timeout = 0;

    // True if the thread has finished
    private boolean done = false;

    /**
     * Creates a new ClientUpdateThread
     * @param venue The venue to update
     * @param connectionId The id of the connection
     */
    public ClientUpdateThread(Venue venue, String connectionId) {
        this.venue = venue;
        this.connectionId = connectionId;
        done = false;
        doUpdate();
        start();
    }

    // Performs the actual update call
    private synchronized void doUpdate() {
        if (!done) {
//            System.err.println("Updating Client");
            try {
                timeout = (long) (venue.updateLifetime(connectionId,
                        DEFAULT_LIFETIME) * MS_TO_SECS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        done = false;
        while (!done && (venue != null)) {
            try {
                sleep(timeout);
            } catch (InterruptedException e) {

                // Do Nothing
            }
            doUpdate();
        }
    }

    /**
     * Stops the updates
     *
     */
    public synchronized void close() {
        done = true;
        interrupt();
    }
}
