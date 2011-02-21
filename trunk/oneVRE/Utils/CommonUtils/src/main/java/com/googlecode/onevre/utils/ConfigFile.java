/*
 * @(#)ConfigFile.java
 * Created: 28 Aug 2007
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Reads and writes config files
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ConfigFile {

    // Hides Constructor
    private ConfigFile() {

        // Does Nothing
    }

    /**
     * Reads a config file
     *
     * @param input The input stream to read
     * @return A map of config section name -> (map of key -> value)
     * @throws IOException
     */
    public static HashMap<String, HashMap<String, String>> read(
            InputStream input) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        HashMap<String, HashMap<String, String>> filetree =
            new HashMap<String, HashMap<String, String>>();
        String line;
        String key;
        line = in.readLine();
        while (line != null) {
            if (line.trim().startsWith("[") && line.trim().endsWith("]")) {
                key = line.trim().substring(1, line.trim().length() - 1);
                HashMap<String, String> value = filetree.get(key.trim());
                if (value == null) {
                    value = new HashMap<String, String>();
                }
                line = in.readLine();
                while ((line != null)
                        && !line.trim().startsWith("[")) {
                    if ((line.trim().length() > 0)
                            && !line.trim().startsWith("#")) {
                        String[] parameter = line.split("[:=]", 2);
                        value.put(parameter[0].trim(), parameter[1].trim());
                    }
                    line = in.readLine();
                }
                filetree.put(key.trim(), value);
            } else {
                //something wrong read next line
                line = in.readLine();
            }
        }
        return filetree;
    }

    /**
     * Reads a config file
     *
     * @param filename The name of the file to read
     * @return A map of config section name -> (map of key -> value)
     * @throws IOException
     */
    public static HashMap<String, HashMap<String, String>> read(String filename)
            throws IOException {
        return read(new FileInputStream(filename));
    }

    /**
     * Writes a configuration file
     * @param filename The name of the file
     * @param values The values to write
     * @throws IOException
     */
    public static void store(String filename,
            HashMap<String, HashMap<String, String>> values)
            throws IOException {
        store(filename, "=", values);
    }

    /**
     * Writes a configuration file
     * @param filename The name of the file
     * @param valueDelim delimiter to be used in the datafile for the name/value pair
     * @param values The values to write
     * @throws IOException
     */
    public static void store(String filename, String valueDelim,
            HashMap<String, HashMap<String, String>> values)
            throws IOException {
        File file = new File(filename);
        file.getParentFile().mkdirs();
        PrintWriter out = new PrintWriter(new FileWriter(filename));
        Iterator<String> iter = values.keySet().iterator();
        while (iter.hasNext()) {
            String section = iter.next();
            HashMap<String, String> valueSet = values.get(section);
            Iterator<String> iterator = valueSet.keySet().iterator();
            out.println("[" + section + "]");
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = valueSet.get(key);
                out.println(key + " " + valueDelim + " " + value);
            }
        }
        out.close();
    }

    /**
     * Returns a parameter of a specific section if it is set. Otherwise the default parameter is returned
     * @param config the configuration HashMap
     * @param section the section in which the parmeter is found
     * @param parameter the parameter name
     * @param defaultValue The default value if the parameter is not provided
     * @return the value of the parameter or the default value if it's unset
     */
    public static String getParameter(HashMap<String, HashMap<String, String>> config,
            String section, String parameter, String defaultValue) {
        HashMap<String, String> cfgSection = config.get(section);
        if (cfgSection != null) {
            String cfgParameter = cfgSection.get(parameter);
            if (cfgParameter != null) {
                return cfgParameter;
            }
        }
        return defaultValue;
    }
}
