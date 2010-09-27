/*
 * @(#)NonJavaService.java
 * Created: 13 May 2008
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

package com.googlecode.onevre.web.nonJavaService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.googlecode.onevre.utils.ConfigFile;
import com.googlecode.onevre.utils.Preferences;



/**
 * A service that does not use Java
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class AGService {

    private static final int BUFFER_SIZE = 4096;

    private AGService() {
        // Does Nothing
    }

    // Extracts the service to the local node services directory
    private static File extractResources() throws IOException {
        HashMap<String, HashMap<String, String>> svcFile =
            ConfigFile.read(AGService.class.getResourceAsStream(
                "/service.svc"));
        HashMap<String, String> serviceDescription = svcFile.get(
            "ServiceDescription");
        File resourcesDirectory = new File(
            Preferences.getInstance().getLocalServicesDir()
            + serviceDescription.get("name"));
        InputStream res =  AGService.class.getResourceAsStream(
                "/resources.zip");
        if (res != null) {
            File dir = resourcesDirectory;
            dir.mkdirs();
            ZipInputStream input = new ZipInputStream(res);
            ZipEntry entry = input.getNextEntry();
            while (entry != null) {
                if (entry.isDirectory()) {
                    File newDir = new File(dir, entry.getName());
                    newDir.mkdirs();
                } else {
                    BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(new File(dir, entry.getName())));
                    BufferedInputStream in = new BufferedInputStream(input);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead = in.read(buffer);
                    while (bytesRead != -1) {
                        out.write(buffer, 0, bytesRead);
                        bytesRead = in.read(buffer);
                    }
                    out.close();
                }
                entry = input.getNextEntry();
            }
        }
        return resourcesDirectory;
    }

    /**
     * Executes the external service
     * @param args The arguments
     */
    public static void main(String[] args) {
        String port = "";
        String serviceMgrUrl = null;
        String token = null;
        boolean test = false;
        boolean secure = false;
        String execute = "";

        System.err.println("Reading arguments");
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--exitOnStart")) {
                System.err.println("Exiting");
                System.exit(0);
            } else if (args[i].equals("--port") || args[i].equals("-p")) {
                port = args[i + 1];
                i++;
            } else if (args[i].equals("--serviceManagerUri")
                    || args.equals("-s")) {
                serviceMgrUrl = args[i + 1];
                i++;
            } else if (args[i].equals("--token") || args[i].equals("-t")) {
                token = args[i + 1];
                i++;
            } else if (args[i].equals("--test")) {
                test = true;
            } else if (args[i].equals("--secure")) {
                secure = true;
            } else if (args[i].equals("--execute")) {
                execute = args[i + 1];
                i++;
            } else {
                System.err.println("Unknown argument " + args[i]);
            }
        }

        try {
            File dir = extractResources();
            Vector<String> commands = new Vector<String>();
            commands.add(execute);
            commands.add("--port");
            commands.add(port);
            commands.add("--serviceManagerUrl");
            commands.add(serviceMgrUrl);
            commands.add("--token");
            commands.add(token);
            if (test) {
                commands.add("--test");
            }
            if (secure) {
                commands.add("--secure");
            }
            String[] command = commands.toArray(new String[0]);
            Runtime.getRuntime().exec(command, null, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
