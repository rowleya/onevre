/*
 * @(#)RegistryClient.java
 * Created: 13-Nov-2006
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

package com.googlecode.onevre.ag.agbridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcLiteHttpTransportFactory;

import com.googlecode.onevre.ag.types.BridgeDescription;


/**
 * An AG3 Bridge Registry Client
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class RegistryClient {

    private static final int TIMEOUT = 120000;

    // The AG3 registry peers from the registry
    private Vector<String> registries = new Vector<String>();

    /**
     * Creates a new RegistryClient
     * @param registryUrl The url of the registry
     * @throws IOException
     */
    public RegistryClient(String registryUrl) throws IOException {
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
        reader.close();
    }

    /**
     * Looks up a list of bridges
     * @return The list of bridge descriptions
     */
    @SuppressWarnings("unchecked")
    public Vector<BridgeDescription> lookupBridges() {
        Vector<BridgeDescription> bridges = new Vector<BridgeDescription>();
        boolean registryFound = false;
        BridgeDescription desc = null;
        for (int i = 0; (i < registries.size()) && !registryFound; i++) {
            String registry = registries.get(i);
            try {
                XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                config.setServerURL(new URL("http://" + registry));
                config.setEnabledForExtensions(true);
                config.setConnectionTimeout(TIMEOUT);
                XmlRpcClient client = new XmlRpcClient();
                client.setTransportFactory(new XmlRpcLiteHttpTransportFactory(
                        client));
                client.setConfig(config);
                Object[] bridgeList = (Object[]) client.execute("LookupBridge",
                        new Object[0]);
                for (int j = 0; j < bridgeList.length; j++) {
                    HashMap<Object, Object> bridge =
                        (HashMap<Object, Object>) bridgeList[j];
                    desc = new BridgeDescription();
                    desc.setGuid((String) bridge.get("guid"));
                    desc.setName((String) bridge.get("name"));
                    desc.setHost((String) bridge.get("host"));
                    desc.setPort(((Integer) bridge.get("port")).intValue());
                    desc.setServerType((String) bridge.get("serverType"));
                    desc.setDescription((String) bridge.get("description"));
                    desc.setPortMin(((Integer) bridge.get("portMin")).intValue());
                    desc.setPortMax(((Integer) bridge.get("portMax")).intValue());
                    bridges.add(desc);
                }
                registryFound = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bridges;
    }
}
