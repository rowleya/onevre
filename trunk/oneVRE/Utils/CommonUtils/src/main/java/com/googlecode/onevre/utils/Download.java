/*
 * @(#)Download.java
 * Created: 27 May 2008
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

package com.googlecode.onevre.utils;

/**
 * Utility class to keep track of downloads
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Download {

    private Download() {
        // Does Nothing
    }

    private static Integer downloadSync = new Integer(0);

    private static int downloadCount = 0;


    /**
     * Indicates that a download is taking place
     */
    public static void startDownloading() {
        synchronized (downloadSync) {
            downloadCount += 1;
        }
    }

    /**
     * Indicates that a download has finished
     */
    public static void stopDownloading() {
        synchronized (downloadSync) {
            downloadCount -= 1;
            downloadSync.notifyAll();
        }
    }

    /**
     * Determines if anything is downloading
     * @return True if something is downloading
     */
    public static boolean isDownloading() {
        return downloadCount > 0;
    }

    /**
     * Waits until all the downloads have stopped
     */
    public static void waitForDownloadsToComplete() {
        synchronized (downloadSync) {
            while (downloadCount > 0) {
                try {
                    downloadSync.wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
        }
    }

}
