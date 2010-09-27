/*
 * @(#)Preferences.java
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

package com.googlecode.onevre.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;


/**
 * Represents locally stored preferences
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Preferences {

    /**
     * The bridge registry preference
     */
    public static final String BRIDGE_REGISTRY =
        "bridgeRegistry";

    /**
     * The preference whether to order bridges by ping time
     */
    public static final String ORDER_BRIDGES_BY_PING =
        "orderBridgesByPing";

    /**
     * The preference for the delay between updates of bridge ping time
     */
    public static final String BRIDGE_PING_UPDATE_DELAY =
        "bridgePingUpdateDelay";

    /**
     * The preference for the default node configuration
     */
    public static final String DEFAULT_NODE_CONFIG =
        "defaultNodeConfig";

    // The only instance
    private static Preferences instance = null;

    private HashMap<String, String> preferences = new HashMap<String, String>();

    private Preferences() {
        preferences.put(BRIDGE_REGISTRY,
                "http://www.accessgrid.org/registry/peers.txt");
        preferences.put(ORDER_BRIDGES_BY_PING, "false");
        preferences.put(BRIDGE_PING_UPDATE_DELAY, "120");
        preferences.put(DEFAULT_NODE_CONFIG, "default");

        try {
            try {
                HashMap<String, HashMap<String, String>> prefsFile =
                    ConfigFile.read(getConfigDir()
                            + "/preferences");
                preferences.putAll(prefsFile.get("Preferences"));
            } catch (FileNotFoundException e) {
                // Do Nothing
            }
            save();
        } catch (IOException e) {
            // Do Nothing
        }
    }
    /**
     * Gets the base directory
     */
    protected String getBaseDir() {
        return System.getProperty("user.home") + OS.SLASH + "pag" + OS.SLASH;
    }

    /**
     * Gets the configuration file directory
     * @return The configuration file directory path
     */
    public String getConfigDir() {
        return getBaseDir() + "Config" + OS.SLASH;
    }

    /**
     * Gets the node configuration directory
     * @return The node configuration directory
     */
    public String getNodeConfigDir() {
        return getConfigDir() + "nodeConfig" + OS.SLASH;
    }

    /**
     * Gets the node services directory
     * @return The node services directory
     */
    public String getNodeServicesDir() {
        return getBaseDir() + "NodeServices" + OS.SLASH;
    }

    /**
     * Gets the local services directory
     * @return The local services directory
     */
    public String getLocalServicesDir() {
        return getBaseDir() + "local_services" + OS.SLASH;
    }


    /**
     * Gets the services directory
     * @return The services directory
     */
    public String getServicesDir() {
        return getBaseDir() + "Services" +  OS.SLASH;
    }

    /**
     * Gets the shared applications directory
     * @return The shared applications directory
     */
    public String getSharedApplicationsDir() {
        return getBaseDir() + "SharedApplications" +  OS.SLASH;
    }

    /**
     * Gets the log directory
     * @return The log directory
     */
    public String getLogDir() {
        return getBaseDir() + "Log" +  OS.SLASH;
    }


    /**
     * Loads preferences
     * @return The preferences
     */
    public static Preferences getInstance() {
        if (instance == null) {
            instance = new Preferences();
        }
        return instance;
    }

    /**
     * Saves the
     * @throws IOException
     */
    public void save() throws IOException {
        HashMap<String, HashMap<String, String>> prefsFile =
            new HashMap<String, HashMap<String, String>>();
        prefsFile.put("Preferences", preferences);
        ConfigFile.store(getConfigDir()
                    + "/preferences", prefsFile);
    }

    /**
     * Gets a value as an int
     * @param key The key to get the value of
     * @return The value as an int
     */
    public int getIntValue(String key) {
        return Integer.parseInt(preferences.get(key));
    }

    /**
     * Gets a value as a string
     * @param key The key to get the value of
     * @return The value as a string
     */
    public String getStringValue(String key) {
        return preferences.get(key);
    }

    /**
     * Gets a value as a boolean
     * @param key The key to get the value of
     * @return The value as a boolean
     */
    public boolean getBooleanValue(String key) {
        return Boolean.parseBoolean(preferences.get(key));
    }

    /**
     * Sets the value of a preference
     * @param key The key to set
     * @param value The value to set to
     * @throws IOException
     */
    public void setStringValue(String key, String value) throws IOException {
        preferences.put(key, value);
        save();
    }
}
