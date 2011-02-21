/*
 * @(#)JavaVersion.java
 * Created: 13 Oct 2007
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

package com.googlecode.onevre.web.version;

import java.applet.Applet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An applet for getting the current java version
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class JavaVersion extends Applet {

    private static final long serialVersionUID = 1L;

    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "^(\\d+)\\.(\\d+)\\.(\\d+)(_(\\d+))?(-(.*))?");

    private static final Matcher MATCHER = VERSION_PATTERN.matcher(
            System.getProperty("java.version"));

    /**
     * Gets the current java version
     *
     * @return The current java version
     */
    public String getVersion() {
        return System.getProperty("java.version");
    }

    /**
     * Gets the current java vendor
     *
     * @return The current java vendor
     */
    public String getVendor() {
        return System.getProperty("java.vendor");
    }

    /**
     * Tests if the vendor contains the given string
     * @param test The string to test for
     * @return True if the string is contained
     */
    public boolean vendorContains(String test) {
        return getVendor().toLowerCase().indexOf(test) != -1;
    }

    /**
     * Checks that the version of java is at least the given version
     * @param minVersion The minimum version to check for
     * @return True if the version is at least the minimum version
     */
    public boolean versionIsAtLeast(String minVersion) {
        Matcher matcher = VERSION_PATTERN.matcher(minVersion);
        boolean minMatches = matcher.matches();
        boolean matches = MATCHER.matches();
        if (minMatches && matches) {
            System.err.println(minVersion + " matches version pattern");
            int minMajorVersion = Integer.parseInt(matcher.group(1));
            int majorVersion = Integer.parseInt(MATCHER.group(1));
            System.err.println("Comparing major versions " + majorVersion
                    + " > " + minMajorVersion);
            if (majorVersion > minMajorVersion) {
                return true;
            }

            if (majorVersion == minMajorVersion) {
                int minMinorVersion = Integer.parseInt(matcher.group(2));
                int minorVersion = Integer.parseInt(MATCHER.group(2));
                System.err.println("Comparing minor versions " + minorVersion
                        + " > " + minMinorVersion);
                if (minorVersion > minMinorVersion) {
                    return true;
                }

                if (minorVersion == minMinorVersion) {
                    int minMaintenanceVersion = Integer.parseInt(
                            matcher.group(3));
                    int maintenanceVersion = Integer.parseInt(MATCHER.group(3));
                    System.err.println("Comparing maintenance versions "
                            + maintenanceVersion + " > "
                            + minMaintenanceVersion);
                    if (maintenanceVersion > minMaintenanceVersion) {
                        return true;
                    }

                    if (maintenanceVersion == minMaintenanceVersion) {
                        int minUpdateVersion = 0;
                        if (matcher.group(5) != null) {
                            minUpdateVersion = Integer.parseInt(
                                    matcher.group(5));
                        }
                        int updateVersion = 0;
                        if (MATCHER.group(5) != null) {
                            updateVersion = Integer.parseInt(MATCHER.group(5));
                        }
                        System.err.println("Comparing update versions "
                                + updateVersion + " > " + minUpdateVersion);
                        if (updateVersion >= minUpdateVersion) {
                            return true;
                        }
                    }
                }
            }
        }

        if (!minMatches) {
            System.err.println("Version " + minVersion + " does not match!");
        }
        if (!matches) {
            System.err.println("Version " + getVersion() + " does not match!");
        }
        return false;
    }
}
