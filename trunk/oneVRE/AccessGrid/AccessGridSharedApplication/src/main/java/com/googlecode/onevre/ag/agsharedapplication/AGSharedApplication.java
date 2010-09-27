/*
 * @(#)SharedApplication.java
 * Created: 22-Mar-2008
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

package com.googlecode.onevre.ag.agsharedapplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;


import com.googlecode.onevre.ag.common.interfaces.SharedApplicationManager;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.DataDescription;
import com.googlecode.onevre.ag.types.EventDescription;
import com.googlecode.onevre.ag.types.application.AppDataDescription;
import com.googlecode.onevre.ag.types.application.AppParticipantDescription;
import com.googlecode.onevre.ag.types.application.ApplicationCmdDescription;
import com.googlecode.onevre.ag.types.application.SharedAppState;
import com.googlecode.onevre.platform.SystemConfig;
import com.googlecode.onevre.protocols.soap.common.SoapDeserializer;
import com.googlecode.onevre.protocols.soap.soapserver.SoapServer;
import com.googlecode.onevre.types.soap.annotation.SoapParameter;
import com.googlecode.onevre.types.soap.interfaces.SoapServable;
import com.googlecode.onevre.utils.ConfigFile;
import com.googlecode.onevre.utils.Preferences;


/**
 * A shared application
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public abstract class AGSharedApplication extends SoapServable{

    private static final int BUFFER_SIZE = 4096;
    // The maximum random number
    private static final int MAX_RANDOM = 100000;

    static {
        SoapDeserializer.mapType(EventDescription.class);
        SoapDeserializer.mapType(AppParticipantDescription.class);
        SoapDeserializer.mapType(ApplicationCmdDescription.class);
        SoapDeserializer.mapType(AppDataDescription.class);
        SoapDeserializer.mapType(SharedAppState.class);
        SoapDeserializer.mapType(ClientProfile.class);
        SoapDeserializer.mapType(DataDescription.class);
    }

    private HashMap<String, HashMap<String, String>> appFile = null;

    // The url of the service manager
    private String sharedApplicationManagerUrl = null;
    private String url = null;
    // The local resources directory
    private File resourcesDirectory = null;

    private String namespace=null;
    private String sessionId=null;
    private String dataDownloadUrl=null;

    // The shutdown hook
    private DoShutdown ds = null;

    private SoapServer server = null;

    /**
     * Creates a new shared application
     */
    public AGSharedApplication() {
        try {
            appFile = ConfigFile.read(getClass().getResourceAsStream(
                    "/shared.app"));
            HashMap<String, String> applicationDescription = appFile.get(
            "SharedApplication");
            resourcesDirectory = new File(
                Preferences.getInstance().getSharedApplicationsDir()
                + applicationDescription.get("name"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // The name of the service (the class name by default)
    private String name = this.getClass().getSimpleName();
    /**
     * Gets the name of the service
     * @return the name
     */
    public String getName() {
        return name;
    }

    protected File getResourcesDirectory() {
        return resourcesDirectory;
    }

    // Extracts the service to the local node services directory
    protected void extractResources() throws IOException {
        InputStream res = getClass().getResourceAsStream("/resources.zip");
        if (res != null) {
            File dir = getResourcesDirectory();
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
    }

    /**
     * Sets the url of the shared application manager of the application
     * @param sharedApplicationManagerUrl The url of the shared application manager
     */
    public void setSharedApplicationManagerUrl(String sharedApplicationManagerUrl) {
        this.sharedApplicationManagerUrl = sharedApplicationManagerUrl;
    }

    /**
     * Gets the url of the shared application manager of the application
     * @return The url of the shared application manager
     */
    public String getSharedApplicationManagerUrl() {
        return sharedApplicationManagerUrl;
    }

    /**
     * Downloads a data file to the shared application to the pag local file store
     * @param venueFilename
     * @return the local filename where the application can access the file
     */
    public String downloadData(String venueFilename){

        String localFileName=venueFilename;
        BufferedOutputStream out = null;

        InputStream  in = null;
        try {
            File tempFile = File.createTempFile( "SharedApplication", venueFilename, new File(SystemConfig.getInstance().getTempDir()));
            tempFile.deleteOnExit();
            out = new BufferedOutputStream(
                    new FileOutputStream(tempFile));

            String data="?"+URLEncoder.encode("namespace", "UTF-8") + "=" + URLEncoder.encode(namespace, "UTF-8");
            data += "&" + URLEncoder.encode("file", "UTF-8") + "=" + URLEncoder.encode(venueFilename, "UTF-8");
            data += "&" + URLEncoder.encode("selection", "UTF-8") + "=" + URLEncoder.encode("filename", "UTF-8");

            System.out.println("Download URL: "+dataDownloadUrl+data);
            System.out.println("SessId: "+sessionId);

            GetMethod get = new GetMethod(dataDownloadUrl+data);
            get.addRequestHeader("Cookie","JSESSIONID=" + sessionId);
            HttpClient httpClient = new HttpClient();
            int status = httpClient.executeMethod(get);
            System.err.println("Upload Status: " + status);
            if (status==200) {
                in=get.getResponseBodyAsStream();


                byte[] buffer = new byte[BUFFER_SIZE];
                int numRead;
                long numWritten = 0;
                while ((numRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, numRead);
                    numWritten += numRead;
                }
                localFileName=tempFile.getCanonicalPath();
                System.out.println(localFileName + "\t" + numWritten);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
                // do nothing - there is nothing we can do
            }
        }
        return localFileName;
    }

    /**
     * Sets the application state
     * @param appState
     */
    public abstract void setState(@SoapParameter("appState") SharedAppState appState);

    /**
     * handles events locally
     * @param event
     */
    public abstract void handleEvent(@SoapParameter("event") EventDescription event);

    /**
     * Shuts down the shared application
     */
    public void shutdown() {
        Runtime.getRuntime().removeShutdownHook(ds);
        stop();
        server.end();
        System.exit(0);
    }

    /**
     * Runs the shared application and registers it with the shared application manager
     * @param args command line arguments of the application<br>
     * <dl><dd>valid arguments:</dd>
     *    <dl>
     *    <dt>--exitOnStart</dt><dd>exits the application immediately</dd>
     *    <dt>--port | -p  &lt;port&gt;</dt><dd>the port number on which the SOAP server of the shared application listens</dd>
     *    <dt>--applicationManagerUri | -a &lt;uri&gt;</dt><dd>the uri of the shared application manager</dd>
     *    <dt>--token | -t &lt;token&gt;</dt><dd>the token to register the shared application with the shared application manager</dd>
     *    <dt>--sessionId | -s &lt;session id&gt;</dt><dd>the session id of the PAG session</dd>
     *    <dt>--namespace | -n &lt;namespace&gt;</dt><dd>the PAG namespace</dd>
     *    <dt>--dataUri | -a &lt;uri&gt;</dt><dd>the uri of the shared application data</dd>
     *    <dt>--secure</dt><dd>the SOAP server of the shared application uses an ssl connection to communicate</dd>
     *    <dt>--test</dt><dd>the shared application is run in test mode</dd>
     *    </dl>
     * </dl>
     */
    public void run(String [] args){
        System.err.println("SharedApplication run called: " + Arrays.toString(args));
        int port = 0;
        String applicationMgrUrl = null;
        String token = null;
        String dataUrl = null;
        String ns=null;
        String sessId=null;
        boolean test = false;
        boolean secure = false;

        System.err.println("Reading arguments");
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--exitOnStart")) {
                System.err.println("Exiting");
                System.exit(0);
            } else if (args[i].equals("--port") || args[i].equals("-p")) {
                port = Integer.parseInt(args[i + 1]);
                i++;
            } else if (args[i].equals("--applicationManagerUri")
                    || args.equals("-a")) {
                applicationMgrUrl = args[i + 1];
                i++;
            } else if (args[i].equals("--token") || args[i].equals("-t")) {
                token = args[i + 1];
                i++;
            } else if (args[i].equals("--sessionId") || args[i].equals("-s")) {
                sessId = args[i + 1];
                i++;
            } else if (args[i].equals("--namespace") || args[i].equals("-n")) {
                ns = args[i + 1];
                i++;
           } else if (args[i].equals("--dataUri") || args[i].equals("-d")) {
                dataUrl = args[i + 1];
                i++;
            } else if (args[i].equals("--test")) {
                test = true;
            } else if (args[i].equals("--secure")) {
                secure = true;
            } else {
                System.err.println("Unknown argument " + args[i]);
            }
        }

        try {
            extractResources();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (dataUrl!=null){
            dataDownloadUrl=dataUrl;
        }
        if (ns!=null){
            namespace=ns;
        }
        if (sessId!=null){
            sessionId=sessId;
        }
        if (test) {
            // testing hook - do nothing
        }

        try {

            // Start the server
            String servicePath = "/SharedApplications/" + getName() + "."
                + System.currentTimeMillis() + (Math.random() * MAX_RANDOM);
            server = new SoapServer(port, secure);
            server.registerObject(servicePath, this);
            server.start();

            // Set Parameters
            url = server.findURLForObject(this);
            System.err.println("Starting application " + url);
            setSharedApplicationManagerUrl(applicationMgrUrl);

            // Register
            if (sharedApplicationManagerUrl != null) {
                System.err.println("Registering with shared application manager; url="
                        + sharedApplicationManagerUrl);
                SharedApplicationManager sharedApplicationManager =
                    new SharedApplicationManager(sharedApplicationManagerUrl);
                try {
                    sharedApplicationManager.registerApplication(token, url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Shared Application Manager does not exist for Appliction "
                        + url);
            }

            // Add the shutdown processor
            try {
                ds = new DoShutdown(this);
                Runtime.getRuntime().addShutdownHook(ds);
            } catch (Exception e) {
                // Do Nothing
            }
        } catch (Exception e) {
            System.err.println("Server could not start");
            e.printStackTrace();
        }
    }

    /**
     * Stops the shared application
     *
     */
    public abstract void stop();

    // Used to detect when the server should be stopped
    private class DoShutdown extends Thread {

        // The shared application
        private AGSharedApplication application = null;

        /**
         * Creates a shutdown handler for the application
         * @param application the shared application
         */
        public DoShutdown(AGSharedApplication application) {
            this.application = application;
        }

        /**
         * @see java.lang.Thread#run()
         */
        public void run() {
            // Stop the server
            application.shutdown();
        }
    }

}
