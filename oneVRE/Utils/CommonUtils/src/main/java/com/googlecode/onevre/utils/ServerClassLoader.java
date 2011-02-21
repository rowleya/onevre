/*
 * Copyright (c) 2008, University of Manchester All rights reserved.
 * See LICENCE in root directory of source code for details of the license.
 */

package com.googlecode.onevre.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.KeyStore;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.SecureClassLoader;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.onevre.security.AcceptAllHostnameVerifier;
import com.googlecode.onevre.security.AcceptAllTrustManager;
import com.googlecode.onevre.web.common.Defaults;




/**
 * A Class loader for loading classes from the server
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ServerClassLoader extends SecureClassLoader {

    private Log log = LogFactory.getLog(this.getClass());

    private static final int BUFFER_SIZE = 4096;

    private static final String INDEX = "index.dat";

    private static final long CACHE_TIMEOUT = 60000;

    private static final String LIB_DIR = "native";

    private static final HashMap<URL, Boolean> CHECKED =
        new HashMap<URL, Boolean>();

    private File localCacheDirectory = null;

    private File localLibDirectory = null;

    private URL remoteServer = null;

    private HashMap<URL, File> cachedJars = new HashMap<URL, File>();

    private HashMap<String, URL> cachedFiles = new HashMap<String, URL>();

    /**
     * Creates a new ServerClassLoader
     * @param parent The parent class loader
     * @param localCacheDirectory The directory to cache files to
     * @param remoteServer The URL of the remote server
     */
    public ServerClassLoader(ClassLoader parent, File localCacheDirectory,
            URL remoteServer) {
        super(parent);
        this.localCacheDirectory = localCacheDirectory;
        this.localLibDirectory = new File(localCacheDirectory, LIB_DIR);
        File versionFile = new File(localCacheDirectory, "Version");
        boolean versionCorrect = false;
        if (!localCacheDirectory.exists()) {
            localCacheDirectory.mkdirs();
        } else {
            if (versionFile.exists()) {
                try {
                    BufferedReader reader = new BufferedReader(
                            new FileReader(versionFile));
                    String version = reader.readLine();
                    reader.close();
                    versionCorrect = Defaults.PAG_VERSION.equals(version);
                    log.info(version + " == " + Defaults.PAG_VERSION + " = " + versionCorrect);
                } catch (IOException e) {
                    // Do Nothing
                }
            }
            try {
                FileInputStream input = new FileInputStream(
                        new File(localCacheDirectory, INDEX));
                DataInputStream cacheFile = new DataInputStream(input);
                FileChannel channel = input.getChannel();
                while (channel.position() < channel.size()) {
                    URL url = new URL(cacheFile.readUTF());
                    String file = cacheFile.readUTF();
                    if (versionCorrect
                            && url.getHost().equals(remoteServer.getHost())
                            && (url.getPort() == remoteServer.getPort())) {
                        File jar = new File(localCacheDirectory, file);
                        if (jar.exists()) {
                            indexJar(url, jar);
                            CHECKED.put(url, true);
                        }
                    }
                }
                input.close();
            } catch (FileNotFoundException e) {
                // Do Nothing - cache will be recreated later

            } catch (IOException e) {
                // Do Nothing - as above

            }
        }
        localLibDirectory.mkdirs();
        try {
            PrintWriter writer = new PrintWriter(versionFile);
            writer.println(Defaults.PAG_VERSION);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.remoteServer = remoteServer;
    }

    private void addSslConnection(URLConnection connection) {
        if (connection instanceof HttpsURLConnection) {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[]{new AcceptAllTrustManager()}, new SecureRandom());
                ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
                ((HttpsURLConnection) connection).setHostnameVerifier(new AcceptAllHostnameVerifier());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *
     * @see java.lang.ClassLoader#findClass(java.lang.String)
     */
    protected Class <?> findClass(String name) throws ClassNotFoundException {

        // Attempt to find the class in a cached jar file
        String pathName = name.replaceAll("\\.", "/") + ".class";
        try {
            URL url = getResourceURL(pathName);
            if (url != null) {
                Class <?> loadedClass = defineClassFromJar(name, url,
                        cachedJars.get(url), pathName);
                return loadedClass;
            }
            throw new ClassNotFoundException("Could not find class " + name);
        } catch (IOException e) {
            throw new ClassNotFoundException("Error finding class " + name, e);
        }
    }

    /**
     *
     * @see java.lang.ClassLoader#findResource(java.lang.String)
     */
    protected URL findResource(String name) {
        try {
            URL url = getResourceURL(name);
            if (url != null) {
                File jar = cachedJars.get(url);
                try {
                    String resource = jar.toURI().toURL().toString();
                    return new URL("jar:" + resource + "!/" + name);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @see java.lang.ClassLoader#findLibrary(java.lang.String)
     */
    protected String findLibrary(String libname) {
        try {
            String name = System.mapLibraryName(libname + "-" + System.getProperty("os.arch"));
            URL url = getResourceURL(name);
            log.info("Loading " + name + " from " + url);
            if (url != null) {
                File jar = cachedJars.get(url);
                JarFile jarFile = new JarFile(jar);
                JarEntry entry = jarFile.getJarEntry(name);
                File library = new File(localLibDirectory, name);
                if (!library.exists()) {
                    InputStream input = jarFile.getInputStream(entry);
                    FileOutputStream output = new FileOutputStream(library);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int totalBytes = 0;
                    while (totalBytes < entry.getSize()) {
                        int bytesRead = input.read(buffer);
                        if (bytesRead < 0) {
                            throw new IOException("Jar Entry too short!");
                        }
                        output.write(buffer, 0, bytesRead);
                        totalBytes += bytesRead;
                    }
                    output.close();
                    input.close();
                    jarFile.close();
                }
                return library.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @see java.security.SecureClassLoader#getPermissions(
     *     java.security.CodeSource)
     */
    protected PermissionCollection getPermissions(CodeSource codesource) {
        boolean isAcceptable = false;
        if (!CHECKED.containsKey(codesource.getLocation())) {
            Certificate[] certs = codesource.getCertificates();
            if (certs == null || certs.length == 0) {
                JOptionPane.showMessageDialog(null,
                        "The jar at " + codesource.getLocation()
                            + " is not signed!",
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
            CHECKED.put(codesource.getLocation(), isAcceptable);
        } else {
            isAcceptable = CHECKED.get(codesource.getLocation());
        }

        Permissions permissions = new Permissions();
        if (isAcceptable) {
            permissions.add(new AllPermission());
            return permissions;
        }
        throw new SecurityException("Access denied to "
                + codesource.getLocation());
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

    private URL getResourceURL(String name) throws IOException {
        URL url = cachedFiles.get(name);

        // If the cached jar is not found, find the class in a remote jar file
        if (url == null) {
            return getRemoteResource(name);
        }

        // If the cached jar is found, check if it is updated remotely
        File jar = cachedJars.get(url);
        if (System.currentTimeMillis() - jar.lastModified()
                > CACHE_TIMEOUT) {

            HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();
            addSslConnection(connection);
            connection.setRequestMethod("HEAD");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                long time = connection.getHeaderFieldDate("Last-Modified",
                        System.currentTimeMillis());

                // If the remote jar has been updated,
                // redownload the jar and load it
                if (jar.lastModified() < time) {
                    downloadJar(url);
                } else {
                    jar.setLastModified(System.currentTimeMillis());
                }
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                return getRemoteResource(name);
            } else {
                throw new IOException("Connection Error: "
                    + connection.getResponseCode() + " "
                    + connection.getResponseMessage());
            }
        }

        return url;
    }

    private URL getRemoteResource(String name) throws IOException {
        String query = "resourceName=" + name;
        String urlQuery = remoteServer.getQuery();
        if ((urlQuery == null) || urlQuery.equals("")) {
            query = "?" + query;
        } else {
            query = "&" + query;
        }
        URL findUrl = new URL(remoteServer.toString() + query);
        URLConnection connection = findUrl.openConnection();
        addSslConnection(connection);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String line = reader.readLine();
        String jarUrl = null;
        while (line != null) {
            if (!line.trim().equals("")) {
                jarUrl = line;
            }
            line = reader.readLine();
        }

        // If the class is not found in a remote jar, throw an error
        if (jarUrl == null) {
            return null;
        }

        // If the class is found in a remote jar,
        // download the jar and load the class
        URL url = new URL(jarUrl);
        File jar = downloadJar(url);

        // Add the jar to the cache
        appendIndex(url, jar);
        indexJar(url, jar);

        return url;
    }

    private File downloadJar(URL url) throws IOException {
        String filename = url.getFile();
        int lastSlashIndex = filename.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            filename = filename.substring(lastSlashIndex + 1);
        }
        File outputJar = new File(localCacheDirectory, filename);
        FileOutputStream output = new FileOutputStream(outputJar);
        URLConnection connection = url.openConnection();
        addSslConnection(connection);
        InputStream input = connection.getInputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = input.read(buffer);
        while (bytesRead != -1) {
            output.write(buffer, 0, bytesRead);
            bytesRead = input.read(buffer);
        }
        input.close();
        output.close();
        CHECKED.remove(url);
        return outputJar;
    }

    private void appendIndex(URL url, File jar) throws IOException {
        FileOutputStream output = new FileOutputStream(
                new File(localCacheDirectory, INDEX), true);
        DataOutputStream cacheFile = new DataOutputStream(output);
        cacheFile.writeUTF(url.toString());
        cacheFile.writeUTF(jar.getName());
        output.close();
    }

    private void indexJar(URL url, File jar) throws IOException {
        JarFile jarFile = new JarFile(jar);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            cachedFiles.put(entry.getName(), url);
        }
        jarFile.close();
        cachedJars.put(url, jar);
    }

    private Class <?> defineClassFromJar(String name, URL url, File jar,
            String pathName) throws IOException {
        JarFile jarFile = new JarFile(jar);
        JarEntry entry = jarFile.getJarEntry(pathName);
        InputStream input = jarFile.getInputStream(entry);
        byte[] classData = new byte[(int) entry.getSize()];
        int totalBytes = 0;
        while (totalBytes < classData.length) {
            int bytesRead = input.read(classData, totalBytes,
                    classData.length - totalBytes);
            if (bytesRead == -1) {
                throw new IOException("Jar Entry too short!");
            }
            totalBytes += bytesRead;
        }
        Class <?> loadedClass = defineClass(name, classData, 0,
                classData.length,
                new CodeSource(url, entry.getCertificates()));
        input.close();
        jarFile.close();
        return loadedClass;
    }


}
