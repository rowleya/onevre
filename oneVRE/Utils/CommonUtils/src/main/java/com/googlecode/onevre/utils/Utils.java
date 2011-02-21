/*
 * @(#)PagUtils.java
 * Created: 25 May 2007
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.security.cert.CertPath;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

import com.googlecode.onevre.security.AcceptAllHostnameVerifier;
import com.googlecode.onevre.security.AcceptAllTrustManager;
import com.googlecode.onevre.security.AliasKeyManager;


/**
 * Utility Class for Portal Access Grid
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class Utils {

    private static Log log = LogFactory.getLog(Utils.class);

    /** get an even port number */
    public static final int PAG_PORT_EVEN = 2;

    /** get an odd port number */
    public static final int PAG_PORT_ODD = 1;

    /** get an any port number */
    public static final int PAG_PORT_RANDOM = 0;

    private static int minPort = 49152;

    private static int maxPort = 65535;

    // The slash charchter on windows
    private static final String WINDOWS_SLASH = "\\";

    private static final Boolean syncConnectionID = true;

    // The quote character
    private static final String QUOTE = "\"";

    // The OS string for Mac
    private static final String MAC_OS_STRING = "mac";

    // The OS string for Linux
    private static final String LINUX_OS_STRING = "linux";

    // The OS substring for windows
    private static final String WINDOWS_OS_STRING = "windows";

    // The windows executable
    private static final String WINDOWS_EXEC = WINDOWS_SLASH + "bin"
                                             + WINDOWS_SLASH + "javaws.exe";

    // The linux executable
    private static final String LINUX_EXEC = "/bin/javaws";

    // The mac executable
    private static final String MAC_EXEC = "/usr/bin/javaws";

    private static Vector<Integer> assignedPorts = new Vector<Integer>();

    private static InetAddress localhost = null;

    static {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {

                try {
                    Enumeration<NetworkInterface> interfaces =
                        NetworkInterface.getNetworkInterfaces();
                    NetworkInterface loopback = null;
                    NetworkInterface siteLocal = null;
                    InetAddress lh = null; // check if we can find an IPv4 address rather than an IPv6 one
                    while (interfaces.hasMoreElements() && (localhost == null)) {
                        NetworkInterface intf = interfaces.nextElement();
                        if (intf.getDisplayName().startsWith(
                                "Microsoft Loopback Adaptor") && (loopback == null)) {
                            loopback = intf;
                        } else if (!intf.getDisplayName().equals(
                                    "Microsoft TV/Video Connection")) {
                            Enumeration<InetAddress> ifaddrs = intf.getInetAddresses();
                            while (ifaddrs.hasMoreElements() && (localhost == null)) {
                                InetAddress addr = ifaddrs.nextElement();
                                if (!addr.isLoopbackAddress()
                                        && !addr.isSiteLocalAddress()) {
                                    System.err.println("Using interface " + intf.getDisplayName() + " as localhost");
                                    lh = addr;
                                    if (lh.getAddress().length == 4){
                                        localhost = lh;
                                    }
                                } else if (addr.isSiteLocalAddress()
                                        && (siteLocal == null)) {
                                    siteLocal = intf;
                                }
                            }
                            if (localhost == null) {
                                localhost = lh;
                            }
                        }
                    }
                    if ((localhost == null) && (siteLocal != null)) {
                        Enumeration<InetAddress> ifaddrs = siteLocal.getInetAddresses();
                        while (ifaddrs.hasMoreElements() && (localhost == null)) {
                            InetAddress addr = ifaddrs.nextElement();
                            if (!addr.getHostAddress().equals("127.0.0.1")) {
                                log.info("Using site local interface " + siteLocal.getDisplayName() + " as localhost");
                                localhost = addr;
                            }
                        }
                    }
                    if ((localhost == null) && (loopback != null)) {
                        Enumeration<InetAddress> ifaddrs = loopback.getInetAddresses();
                        while (ifaddrs.hasMoreElements() && (localhost == null)) {
                            InetAddress addr = ifaddrs.nextElement();
                            if (!addr.getHostAddress().equals("127.0.0.1")) {
                                log.info("Using loopback interface " + loopback.getDisplayName() + " as localhost");
                                localhost = addr;
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                if (localhost == null) {
                    try {
                        localhost = InetAddress.getByName("127.0.0.1");
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage());
                    }
                }
                return null;
            }
        });
        System.out.println("LOCALHOST: " + localhost.getHostAddress());
    }

    private Utils() {
        // Does Nothing
    }

    /**
     * Sets the localhost IP address to use
     * @param address The address to use
     */
    public static void setLocalHost(InetAddress address) {
        localhost = address;
    }

    /**
     * Gets the set localhost address
     * @return The localhost address
     */
    public static InetAddress getLocalHost() {
        return localhost;
    }


    /**
     * Gets the set localhost address as URL string (ipv6 addresses are enclosed in [])
     * @return The localhost address
     */

    public static String getLocalHostAddress() {
        byte[] addr = localhost.getAddress();

        String localHostAddress = localhost.getHostAddress().replaceAll("%10", "");
        if (addr.length == 16) {
            // ipv6 address
            localHostAddress = "[" + localHostAddress + "]";
        }
        return localHostAddress;
    }

    /**
     *
     * Searches for a number of successive free ports in the range between minPort and maxPort
     *
     * @param number Number of Ports requested
     * @param even first Port Number should be even (2) , odd (1) or random (0)
     * @param stream search for TCP (true) or UDP (false) Socket
     * @return free port number to be used (if multiple numbers were requested this
     * is the first of number successive numbers
     * @throws SocketException
     */
    public static int searchPort(int number, int even, boolean stream) throws SocketException {
        boolean found = false;
        int offset = 0;
        int port = minPort;
        while (!found) {
            switch (even) {
                case 2:
                    port = minPort + 2 * offset;
                    break;
                case 1:
                    port = minPort + 2 * offset + 1;
                    break;
                default:
                    port = minPort + offset;
            }
            for (int i = 0; i < number; i++) {
                if ((port + i) > maxPort) {
                    throw new SocketException("No port in Range found");
                } else if (!assignedPorts.contains(new Integer(port + i))) {
                    System.err.println((port + i) + " is not assigned");
                    try {
                        if (stream) {
                            ServerSocket test = new ServerSocket(port + i);
                            found = true;
                            test.close();
                        } else {
                            DatagramSocket test = new DatagramSocket(port + i);
                            found = true;
                            test.close();
                        }
                    } catch (SocketException e) {
                        found = false;
                        offset++;
                        break;
                    } catch (UnknownHostException e) {
                        // this should not happen
                        e.printStackTrace();
                    } catch (IOException e) {
                        found = false;
                        offset++;
                        break;
                    }
                } else {
                    found = false;
                    offset++;
                    break;
                }
            }
        }
        for (int i = 0; i < number; i++) {
            System.err.println((port + i) + " assigned");
            assignedPorts.add(new Integer(port + i));
        }
        return port;
    }

    /**
     * Assigns a port so that it can't be assigned by the searchPort method
     * @param port The port to assign
     */
    public static void assignPort(int port) {
        assignedPorts.add(new Integer(port));
    }

    /**
     * Converts escaped XML input text to be sent through XML-RPC replacing & into &_
     *
     * @param string Input string
     * @return String with all & symbols escaped
     */
    public static String escapeXmlRpcValue(String string) {
        return StringEscapeUtils.escapeXml(string.replaceAll("&", "&_"));
    }

    /**
     * Reverse conversions of escapeXmlRpcValue replacing &_ into &
     *
     * @param string Input string
     * @return String with all &_ symbols unescaped
     */
    public static String unescapeXmlRpcValue(String string) {
        return StringEscapeUtils.unescapeXml(string).replaceAll("&_", "&");
    }

    /**
     * Checks the certificates of a set of jars
     * @param jars The jars to check
     * @param files The files to remove from
     * @param uploadDir The directory of the upload
     * @param signer The signer to check
     * @return The set of files with the jars removed
     * @throws Exception
     */
    public static Vector<String> checkJars(String[] jars, Vector<String> files,
            File uploadDir, CertPath signer) throws Exception {
        for (int i = 0; i < jars.length; i++) {
            if (!jars[i].equals("")) {
                File jar = new File(uploadDir, jars[i]);
                if (!jar.exists()) {
                    throw new Exception("Jar " + jars[i]
                        + " referred to in service.svc "
                        + "does not exist");
                }
                Utils.checkJarCertificate(jar, signer);
                files.remove(jars[i]);
            }
        }
        return files;
    }

    /**
     * Checks the certificate of a jar file
     * @param jarFile The jar file
     * @param signer The certificate to check
     * @throws Exception
     */
    public static void checkJarCertificate(File jarFile, CertPath signer)
            throws Exception {
        JarFile jar = new JarFile(jarFile);
        Enumeration<JarEntry> e = jar.entries();
        while (e.hasMoreElements()) {
            JarEntry entry = e.nextElement();
            if (!entry.getName().startsWith("META-INF")
                    && !entry.isDirectory()) {
                readEntry(jar, entry);
                CodeSigner[] signers = entry.getCodeSigners();
                if ((signers == null) || signers.length != 1) {
                    System.err.println(entry.getName() + " signed with "
                            + Arrays.toString(signers));
                    jar.close();
                    throw new Exception(
                            "Jars must be signed by one certificate");
                }
                if (!signers[0].getSignerCertPath().equals(signer)) {
                    jar.close();
                    throw new Exception(
                            "Jars must be signed by only one certificate");
                }
            }
        }
        jar.close();
    }

    /**
     * Reads an entry from a jar file
     * @param jar The jar file
     * @param entry The entry
     * @throws Exception
     */
    public static void readEntry(JarFile jar, JarEntry entry) throws Exception {
        InputStream input = jar.getInputStream(entry);
        long size = entry.getSize();
        byte[] data = null;
        if (size != -1) {
            data = new byte[(int) size];
        } else {
            data = new byte[8196];
        }
        int bytesRead = input.read(data);
        while (bytesRead != -1) {
            bytesRead = input.read(data);
        }
    }

    /**
     * @return a unique Id to
     */
    public static String generateID() {
        String connectionId = null;
        synchronized (syncConnectionID) {
            connectionId = String.valueOf(System.currentTimeMillis()
                    + String.valueOf((long) (Math.random() * 100000000)));
        }
        return connectionId;
    }
    /**
     * Gets a subsection of a configuration file
     * @param details The section to read from
     * @param section The subsection name
     * @param allowBlank True if the item can be blank
     * @return The section
     * @throws Exception
     */
    public static String getSection(HashMap<String, String> details,
            String section, boolean allowBlank) throws Exception {
        String value = details.get(section);
        if (value == null) {
            throw new Exception("ServiceDescription does not contain "
                    + section);
        }
        if (!allowBlank && value.trim().equals("")) {
            throw new Exception(section
                    + " must not be blank in ServiceDescription");
        }
        return value;
    }

    /**
     * Adds an all accepting SSL context to a URL connection
     * @param connection the connection to add the SSL context to
     *
     */
    public static void addSslConnection(final URLConnection connection) {
        addSslConnection(connection, null);
    }

    public static void addSslConnection(final URLConnection connection, final GSSCredential credential) {

        if (connection instanceof HttpsURLConnection) {
            AccessController.doPrivileged(
                new PrivilegedAction<Void>() {
                    public Void run() {
                        KeyManager[] km = null;
                        if (credential != null) {
                            GlobusGSSCredentialImpl globusCredential = (GlobusGSSCredentialImpl) credential;
                            try {
                                log.info("credential :" + globusCredential.getName().toString());
                            } catch (GSSException e1) {
                                log.info("globusCredential.getName() failed");
                            }
                            KeyStore ks = null;
                            try {
                                ks = KeyStore.getInstance("JKS");
                                try {
                                    ks.load(null, new String("").toCharArray());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ks.setEntry("cert", new KeyStore.PrivateKeyEntry(globusCredential.getPrivateKey(),
                                        globusCredential.getCertificateChain()),
                                        new KeyStore.PasswordProtection(new String("").toCharArray()));
                            } catch (KeyStoreException e) {
                                e.printStackTrace();
                            }
                            km = new KeyManager[]{new AliasKeyManager(ks, "cert", "")};
                        } else {
                            log.info("credential not provided");
                        }

                        String truststore = "/usr/local/liferay/tomcat5.5/webapps/Venues/WEB-INF/venueTrustStore";
                        if (truststore != null) {
                            log.debug("found configured truststore: " + truststore);
      //                      truststore = expandPathname(truststore);
                            log.debug("final truststore: " + truststore);
                            String truststorepw = "gridcert";
                            try {
                                log.info("loading truststore " + truststore);
                                KeyStore tsKS = KeyStore.getInstance("JKS");
                                char[] pw = null;
                                if (truststorepw != null) {
                                    pw = truststorepw.toCharArray();
                                }
                                tsKS.load(new FileInputStream(truststore), pw);
                                TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                                        TrustManagerFactory.getDefaultAlgorithm());
                                tmf.init(tsKS);
                                log.info("loading truststore " + truststore);
                                SSLContext context = SSLContext.getInstance("TLS");
                                if (credential != null) {
                                    context.init(km, tmf.getTrustManagers(), new SecureRandom());
                                } else {
                                    context.init(km, new TrustManager[]{new AcceptAllTrustManager()},
                                            new SecureRandom());
                                }
                                ((HttpsURLConnection) connection).setSSLSocketFactory(context.getSocketFactory());
                                ((HttpsURLConnection) connection).setHostnameVerifier(new AcceptAllHostnameVerifier());
                            } catch (KeyStoreException e) {
                                log.error("unable to create truststore", e);
                            } catch (NoSuchAlgorithmException e) {
                                log.error("configuration problem, unable to check integrity of truststore", e);
                            } catch (CertificateException e) {
                                log.error("configuration problem, unable to load at least "
                                        + "one certificate from truststore", e);
                            } catch (FileNotFoundException e) {
                                log.error("configuration problem, unable load truststore, file not found", e);
                            } catch (IOException e) {
                                log.error("configuration problem, unable to load truststore", e);
                            } catch (KeyManagementException e) {
                                log.error("configuration problem, unable to initialise truststore", e);
                            }
                        }




        /*                try {
                            SSLContext sslContext = SSLContext.getInstance("TLS");
                            sslContext.init(km, new TrustManager[]{new AcceptAllTrustManager()}, new SecureRandom());
                            sslContext.init(km, tmf.getTrustManagers(), new SecureRandom());
                            ((HttpsURLConnection)connection).setSSLSocketFactory(sslContext.getSocketFactory());
                            ((HttpsURLConnection)connection).setHostnameVerifier(new AcceptAllHostnameVerifier());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                        return null;
                    }
                }
            );
        }

    }

    /**
     * Sets the defaut SSL connection to an all accepting SSL context
     */
    public static void setDefaultSslConnection() {
        AccessController.doPrivileged(
            new PrivilegedAction<Void>() {
                public Void run() {
                    try {
                        SSLContext sslContext = SSLContext.getInstance("TLS");
                        sslContext.init(null, new TrustManager[]{new AcceptAllTrustManager()}, new SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                        HttpsURLConnection.setDefaultHostnameVerifier(new AcceptAllHostnameVerifier());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }
        );
   }

    /**
     * Starts a web start application
     * @param startjnlp The url of the JNLP to start the application
     * @throws IOException
     */
    public static void execWebStart(URL startjnlp) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String launch = "";
        String launch2 = "";
        String javaHome = System.getProperty("java.home");

        if (os.indexOf(WINDOWS_OS_STRING) != -1) {
            launch += QUOTE + javaHome + WINDOWS_EXEC + QUOTE;
            launch2 += QUOTE + javaHome + WINDOWS_EXEC + QUOTE;
        } else if (os.indexOf(LINUX_OS_STRING) != -1) {
            launch += javaHome + LINUX_EXEC;
            launch2 += javaHome + LINUX_EXEC;
        } else if (os.indexOf(MAC_OS_STRING) != -1) {
            launch += MAC_EXEC;
            launch2 += MAC_EXEC;
        }

        launch += " -Xnosplash -wait";
        launch2 += " -Xnosplash";
        launch += " " + startjnlp + "&exitOnStart=1";
        launch2 += " " + startjnlp;
        System.err.println("Downloading Web Start App: " + launch);
        Process p = Runtime.getRuntime().exec(launch);
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            // Do Nothing
        }
        System.err.println("Launching Web Start App: " + launch2);
        Runtime.getRuntime().exec(launch2);
    }

    public static String readPlainFile(String filename) throws IOException {
        String contents = "";
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String text = null;
        // repeat until all lines is read
        while ((text = reader.readLine()) != null) {
            contents += text;
        }
        return contents;
    }



}
