/*
 * @(#)AGBridgeRegistryClient.java
 * Created: 04-Sep-2006
 * Version: 2-0-alpha
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

package com.googlecode.onevre.bridges.lowbag.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcLiteHttpTransportFactory;


/**
 * Registers a bridge with the AGBridgeRegistry
 *
 * @author Andrew G D Rowley
 * @version 2-0-alpha
 */
public class AGBridgeRegistryClient extends Thread {

    // The default timeout value
    private static final int DEFAULT_TIMEOUT = 80000;

    // The timeout of the client
    private static final int TIMEOUT_MS = 120000;

    // The AG3 registry peers from the registry
    private Vector registries = new Vector();

    // The timeout before the next registry update is sent
    private long timeout = DEFAULT_TIMEOUT;

    // The name of the bridge
    private String name = null;

    // The host of the bridge
    private String host = null;

    // The port number of the bridge
    private int port = 0;

    // The minimum port number that will be used by the bridge
    private int minPort = 0;

    // The maximum port number that will be used by the bridge
    private int maxPort = 0;

    // The type of the bridge
    private String type = null;

    // True if the update client has been stopped
    private boolean done = false;

    /**
     * Creates an AGBridgeRegistryClient
     * @param registryUrl The url of the registry
     * @param name The name of the bridge
     * @param host The host of the bridge
     * @param port The port of the bridge
     * @param minPort The minimum client port number
     * @param maxPort The maximum client port number
     * @param type The type of the bridge
     * @throws IOException
     */
    public AGBridgeRegistryClient(String registryUrl, String name, String host,
            int port, int minPort, int maxPort, String type)
            throws IOException {
        System.err.println(registryUrl);
        URL url = new URL(registryUrl);
        URLConnection connection = url.openConnection();
        InputStream input = connection.getInputStream();
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(input));
        String data = "";
        String line = "";
        while ((line = reader.readLine()) != null) {
            data += line + "\n";
        }
        StringTokenizer tokens = new StringTokenizer(data);
        while (tokens.hasMoreTokens()) {
            registries.add(tokens.nextToken());
        }
        this.name = name;
        this.host = host;
        this.port = port;
        this.minPort = minPort;
        this.maxPort = maxPort;
        this.type = type;
    }

    /**
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while (!done) {
            boolean registered = false;
            for (int i = 0; (i < registries.size()) && !registered; i++) {
                String registry = (String) registries.get(i);
                try {
                    XmlRpcClientConfigImpl config =
                        new XmlRpcClientConfigImpl();
                    config.setServerURL(new URL("http://" + registry));
                    config.setEnabledForExtensions(true);
                    config.setConnectionTimeout(TIMEOUT_MS);
                    XmlRpcClient client = new XmlRpcClient();
                    client.setTransportFactory(
                            new XmlRpcLiteHttpTransportFactory(client));
                    client.setConfig(config);

                    Vector pingArgs = new Vector();
                    Vector registerArgs = new Vector();
                    Hashtable bridgeDescription = new Hashtable();
                    bridgeDescription.put("guid",
                            String.valueOf(System.currentTimeMillis()));
                    bridgeDescription.put("name", name);
                    bridgeDescription.put("host", host);
                    bridgeDescription.put("port", new Integer(port));
                    bridgeDescription.put("serverType", type);
                    bridgeDescription.put("description", "");
                    bridgeDescription.put("portMin", new Integer(minPort));
                    bridgeDescription.put("portMax", new Integer(maxPort));
                    registerArgs.add(bridgeDescription);
                    pingArgs.add(String.valueOf(System.currentTimeMillis()));
                    client.execute("Ping", pingArgs);
                    client.execute("RegisterBridge", registerArgs);
                    registered = true;
                    System.err.println("Registered on " + registry);
                } catch (Exception e) {
                    System.err.println("Error connecting to registry "
                            + registry);
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {

                // Do Nothing
            }
        }
    }

    /**
     * Closes the thread
     */
    public void close() {
        done = true;
    }
}
