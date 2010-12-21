/*
 * @(#)BridgeClientCreator.java
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

package com.googlecode.onevre.ag.agbridge;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import com.googlecode.onevre.ag.types.BridgeDescription;
import com.googlecode.onevre.ag.types.service.AGBridgeConnectorDescription;
import com.googlecode.onevre.utils.Preferences;
import com.googlecode.onevre.utils.ServerClassLoader;


/**
 * Used to create bridge clients from bridge descriptions
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class BridgeClientCreator {

/*
    private static URL url = null;

    private static HashMap<URL, ClassLoader> classLoaders =
        new HashMap<URL, ClassLoader>();
*/

    private static Vector<String> prefixes = new Vector<String>();
    static {
        prefixes.add("com.googlecode.onevre.ag.agbridge");
    }

    private static HashMap<String, AGBridgeConnectorDescription> bridgeConnectors =
        new HashMap<String, AGBridgeConnectorDescription>();

    private BridgeClientCreator() {
        // Does Nothing
    }
/*
    private static ClassLoader getClassLoader(URL url) throws IOException {

        if (url != null) {
            if (classLoaders.containsKey(url)) {
                return classLoaders.get(url);
            }
            Vector<URL> urls = new Vector<URL>();
            HttpURLConnection connection = (HttpURLConnection)
                url.openConnection();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                try {
                    URL jar = new URL(line);
                    urls.add(jar);
                } catch (MalformedURLException e) {
                    // Do Nothing
                }
                line = reader.readLine();
            }
            ClassLoader loader = new AllPermissionsClassLoader(
                    urls.toArray(new URL[0]),
                    BridgeClientCreator.class.getClassLoader());
            classLoaders.put(url, loader);
            return loader;
        }
        return BridgeClientCreator.class.getClassLoader();
    }
*/
    /**
     * Sets the url of the bridges on the server
     * @param url The url of the bridges
     */
    public static void setBridgeConnectors(HashMap<String, AGBridgeConnectorDescription> bridgeConnectors) {
        BridgeClientCreator.bridgeConnectors = bridgeConnectors;
    }

    public static void addBridgeConnector(String type, AGBridgeConnectorDescription connector) {
        BridgeClientCreator.bridgeConnectors.put(type, connector);
    }

    public static void addPrefix(String prefix) {
        BridgeClientCreator.prefixes.add(prefix);
    }

    /**
     * Gets the url of the server
     * @return The url or null if none
     */
    /*
    public static String getServer() {
        return url.toString();
    }
     */

    /**
     * Creates a bridge client from a bridge description
     * @param bridge The bridge description
     * @param url The url to load the bridge implementations from
     * @return The bridge client
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public static BridgeClient create(BridgeDescription bridge)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, IOException {

        BridgeClient client = null;
        AGBridgeConnectorDescription bridgeDescription = bridgeConnectors.get(bridge.getServerType());

        if (bridgeDescription != null) {
            String className = bridgeDescription.getBridgeClass();
            String resourceDir = Preferences.getInstance().getLocalServicesDir()
            + bridgeDescription.getName();
            ServerClassLoader classLoader = new ServerClassLoader(BridgeClientCreator.class.getClassLoader(),
                     new File(resourceDir), new URL(bridgeDescription.getLaunchUrl()));
            Class<?> clientClass = Class.forName(className, true, classLoader);
            client = (BridgeClient) clientClass.newInstance();
            client.init(InetAddress.getByName(bridge.getHost()), bridge.getPort());
            return client;
        }
        String postfix = "." + bridge.getServerType().toLowerCase()
            + ".BridgeClientImpl";
        for (String prefix : prefixes) {
            try {
                String className = prefix + postfix;
                Class<?> clientClass = Class.forName(className);
                client = (BridgeClient) clientClass.newInstance();
                client.init(InetAddress.getByName(bridge.getHost()), bridge.getPort());
                return client;
            } catch (Exception e) {
                // Do Nothing
            }
        }
        throw new ClassNotFoundException("No connector found for bridge type " + bridge.getServerType());
    }

/*
    private static class AllPermissionsClassLoader extends URLClassLoader {

        private static HashMap<URL, Boolean> checked =
            new HashMap<URL, Boolean>();

        private AllPermissionsClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        private boolean verifyCertificate(X509Certificate cert) {
            try {
                String keypass = "";
                String keystorename = System.getProperty(
                        "deployment.user.security.trusted.certs");
                if (keystorename == null) {
                    throw new IOException("No trusted certs keystore");
                }

                KeyStore keystore = KeyStore.getInstance("JKS", "SUN");
                File file = new File(keystorename);
                if (!file.exists()) {
                    keystore.load(null, keypass.toCharArray());
                } else {
                    keystore.load(new FileInputStream(keystorename),
                        keypass.toCharArray());
                }
                boolean isInStore = false;
                Enumeration<String> aliases = keystore.aliases();
                while (aliases.hasMoreElements() && !isInStore) {
                    String alias = aliases.nextElement();
                    Certificate certificate =
                        keystore.getCertificate(alias);
                    if (certificate != null) {
                        if (certificate.equals(cert)) {
                            isInStore = true;
                        }
                    }
                }
                if (!isInStore) {
                    int result = JOptionPane.showConfirmDialog(null,
                            "Do you want to trust the bridge implementation "
                                + "signed by\n"
                                + cert.getSubjectX500Principal().getName(),
                            "Trust source?",
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        keystore.setEntry("deploymentusercert-"
                                    + System.currentTimeMillis(),
                                new KeyStore.TrustedCertificateEntry(cert),
                                null);
                        FileOutputStream output =
                            new FileOutputStream(keystorename);
                        keystore.store(output, keypass.toCharArray());
                        output.close();
                        return true;
                    }
                    return false;
                }
                return true;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return false;
        }

        protected PermissionCollection getPermissions(CodeSource codesource) {
            boolean isAcceptable = false;
            if (!checked.containsKey(codesource.getLocation())) {
                Certificate[] certs = codesource.getCertificates();
                if (certs == null || certs.length == 0) {
                    JOptionPane.showMessageDialog(null,
                            "The bridge implementation at "
                                + codesource.getLocation() + " is not signed!",
                            "Security Error",
                            JOptionPane.ERROR_MESSAGE);
                    isAcceptable = false;
                } else {
                    isAcceptable = true;
                    for (int i = 0; (i < certs.length) && isAcceptable; i++) {
                        if (!verifyCertificate(
                                (X509Certificate) certs[i])) {
                            isAcceptable = false;
                        }
                    }
                }
                checked.put(codesource.getLocation(), isAcceptable);
            } else {
                isAcceptable = checked.get(codesource.getLocation());
            }

            Permissions permissions = new Permissions();
            if (isAcceptable) {
                permissions.add(new AllPermission());
                return permissions;
            }
            throw new SecurityException("Access denied to "
                    + codesource.getLocation());
        }
    }
    */
}
