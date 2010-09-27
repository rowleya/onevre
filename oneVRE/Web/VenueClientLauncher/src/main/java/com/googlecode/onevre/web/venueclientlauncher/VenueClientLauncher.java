/*
 * Copyright (c) 2008, University of Manchester All rights reserved.
 * See LICENCE in root directory of source code for details of the license.
 */

package com.googlecode.onevre.web.venueclientlauncher;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.net.URL;

import com.googlecode.onevre.utils.PrintOutStream;
import com.googlecode.onevre.utils.ServerClassLoader;
import com.googlecode.onevre.utils.ui.ProgressDialog;

/**
 * A class that launches the venue client
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class VenueClientLauncher {

    private VenueClientLauncher() {

        // Does Nothing
    }

    /**
     * Starting up the web server for interaction with the portlet
     * @param args port number for communication with the portlet
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        ProgressDialog progress = new ProgressDialog("PAG", false, true);
        progress.setMessage("Loading Venue Client");
        progress.setVisible(true);

        System.err.println("VenueClient class executed!");
        int port = 0;
        String clientProfile = null;
        String services = null;
        String applications = null;
        String pointOfReferenceUrl = null;
        String bridgeConnectors = null;
        String resourceDir = null;
        String url = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (args[i].startsWith("\"") && args[i].endsWith("\"")) {
                arg = arg.substring(1, arg.length() - 1);
            }
            System.err.println("Argument " + arg);
            if (arg.trim().startsWith("exitOnStart")) {
                System.exit(0);
            } else if (arg.trim().startsWith("port")) {
                String portstring = arg.trim();
                port = Integer.parseInt(portstring.substring(
                        portstring.indexOf("=") + 1));
            } else if (arg.trim().startsWith("clientProfileXml")) {
                String xml = arg.trim();
                xml = xml.substring(xml.indexOf("=") + 1);
                clientProfile = xml;
            } else if (arg.trim().startsWith("services")) {
                String servicesString = arg.trim();
                services = servicesString.substring(
                        servicesString.indexOf("=") + 1);
            } else if (arg.trim().startsWith("applications")) {
                String applicationsString = arg.trim();
                applications = applicationsString.substring(
                       applicationsString.indexOf("=") + 1);
            } else if (arg.trim().startsWith("bridgeConnectors")) {
                bridgeConnectors = arg.trim();
                bridgeConnectors = bridgeConnectors.substring(
                        bridgeConnectors.indexOf("=") + 1);
            } else if (arg.trim().startsWith("pointOfReferenceUrl")) {
                String urlString = arg.trim();
                pointOfReferenceUrl = urlString.substring(
                        urlString.indexOf("=") + 1);
            } else if (arg.trim().startsWith("resourceDir")) {
                resourceDir = arg.trim();
                resourceDir = resourceDir.substring(
                        resourceDir.indexOf("=") + 1);
            } else if (arg.trim().startsWith("url")) {
                url = arg.trim();
                url = url.substring(
                        url.indexOf("=") + 1);
            }
        }


        File resource = new File(resourceDir);
        resource.mkdirs();
        FileOutputStream log = new FileOutputStream(new File(resource,
                "VenueClient.log"));
        PrintOutStream out = new PrintOutStream(System.err, log);
        System.setErr(out);
        System.setOut(out);

        ServerClassLoader classLoader = new ServerClassLoader(
                VenueClientLauncher.class.getClassLoader(),
                new File(resourceDir), new URL(url));
        Class<?> venueClient = Class.forName("com.googlecode.onevre.web.venueclient.VenueClient", true, classLoader);
        Constructor< ? > constructor =
            venueClient.getConstructor(Integer.TYPE, String.class, String.class,
                String.class, String.class, String.class);
        constructor.newInstance(port, clientProfile,
                services, applications, pointOfReferenceUrl, bridgeConnectors);
        progress.setVisible(false);
    }

}
