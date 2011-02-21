/*
 * @(#)SystemConfigLinux.java
 * Created: 20 Sep 2007
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


package com.googlecode.onevre.platform;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * System configuration on a linux client machine
 * @author Anja Le Blanc
 * @version 1.0
 */

public class SystemConfigLinux extends SystemConfig {

    private Logger log = Logger.getLogger(SystemConfigLinux.class.getName());

    private static final String DIR = "/dev/";

    private static final String df = "df -P /";

    /**
     * determine the amount of free space available in the file system containing 'path'
     * <br/>
     * using: "/bin/bash df / | tail -n 1 | awk '{print $4}'";
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.platform.SystemConfig#getFileSystemFreeSpace(java.lang.String)}</dd></dl>
     * @param path a path from the file system
     * @return the amount of free space in the filesystem (in bytes)
     */
   public long getFileSystemFreeSpace(String path) {
        try {
            Process p = Runtime.getRuntime().exec(df);
            BufferedInputStream i = new BufferedInputStream(p.getInputStream());
            StringBuffer buffer = new StringBuffer();
            for (;;) {
                int c = i.read();
                if (c == -1) {
                    break;
                }
                buffer.append((char) c);
            }
            String output = buffer.toString();
            i.close();
            String[] split = output.split("\n");
            String[] numbers = split[split.length - 1].split(" +");
            long length = Long.parseLong(numbers[numbers.length - 3]);
            log.log(Level.INFO, "free disk space " + length);
            return length;
        } catch (IOException e) {
            log.log(Level.SEVERE, e.toString());
        }
        return 0;
    }

    /**
     * Gets the resources available
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.platform.SystemConfig#getResources()}</dd></dl>
     * @return The resources
     */
    public Hashtable<String, String[]> getResources() {
        File path = new File(DIR);

        Hashtable<String, String[]> resources = new Hashtable<String, String[]>();
        String[] ports = new String[]{"Camera"};
        File[] list = path.listFiles(new VideoFilter());
        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory()) {
                continue;
            }
            log.log(Level.INFO, "can write: " + list[i].canRead());
            if (list[i].canRead()) {
                resources.put(list[i].getName(), ports);
            }
        }
        return resources;
    }

    /**
     * Configures the firewall for an application
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.platform.SystemConfig#appFirewallConfig(java.lang.String, boolean)}</dd></dl>
     * @param path The application path
     * @param enableFlag True to enable the firewall, false otherwise
     */
    public void appFirewallConfig(String path, boolean enableFlag) {
        return;
    }

    private class VideoFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            String f = new File(name).getName();
            return f.matches("video[0-1]*");
        }

    }

}

