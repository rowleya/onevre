package com.googlecode.onevre.ag.types.server;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;


import com.googlecode.onevre.ag.common.interfaces.AGSharedApplication;
import com.googlecode.onevre.ag.common.interfaces.SharedApplication;
import com.googlecode.onevre.ag.interfaces.VenueClientInterface;
import com.googlecode.onevre.ag.types.EventDescription;
import com.googlecode.onevre.ag.types.application.AGSharedApplicationDescription;
import com.googlecode.onevre.ag.types.application.ApplicationDescription;
import com.googlecode.onevre.ag.types.application.SharedAppState;
import com.googlecode.onevre.types.soap.annotation.SoapParameter;
import com.googlecode.onevre.types.soap.exceptions.SoapException;
import com.googlecode.onevre.types.soap.interfaces.SoapServable;
import com.googlecode.onevre.utils.Download;
import com.googlecode.onevre.utils.Utils;

/**
 * An AGSharedApplicationManager
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class AGSharedApplicationManager extends SoapServable {

    // Time to wait for
    private static final int WAIT_TIME = 1000;

    // Time to delay after starting service
    private static final int DELAY_TIME = 10000;

    // The OS string for Mac
    private static final String MAC_OS_STRING = "mac";

    // The OS string for Linux
    private static final String LINUX_OS_STRING = "linux";

    // The OS substring for windows
    private static final String WINDOWS_OS_STRING = "windows";

    // The windows executable
    private static final String WINDOWS_EXEC = "\\bin\\javaws.exe";

    // The linux executable
    private static final String LINUX_EXEC = "/bin/javaws";

    // The mac executable
    private static final String MAC_EXEC = LINUX_EXEC;

    private String uri;
    private HashMap<String, AGSharedApplicationDescription>
        allApplications =
            new HashMap<String, AGSharedApplicationDescription>();
    private HashMap<String, Vector <String>> runningApplications =
        new HashMap<String, Vector <String>>();

    private VenueClientInterface venueClient = null;
    //local return values
    private Integer registerSync = new Integer(0);
    private HashMap<String, String> registeringApplications =
        new HashMap<String, String>();

    /*  NOT implemented Methods
    static {
        RESULT_NAMES.put("addService", "serviceDescription");
        RESULT_TYPES.put("addService", null);
        RESULT_NAMES.put("addServiceByName", "serviceDescription");
        RESULT_TYPES.put("addServiceByName", null);
        RESULT_NAMES.put("getDescription", "description");
        RESULT_TYPES.put("getDescription", null);
        RESULT_NAMES.put("getNodeServiceUrl", "nodeServiceUrl");
        RESULT_TYPES.put("getNodeServiceUrl", STRING_TYPE);
        RESULT_NAMES.put("getServicePackageDescriptions",
                "servicePackageDescription");
        RESULT_TYPES.put("getServicePackageDescriptions", null);
        RESULT_NAMES.put("getServices", "serviceDescription");
        RESULT_TYPES.put("getServices", null);
        RESULT_NAMES.put("getResource", "resources");
        RESULT_TYPES.put("getResource", null);
        RESULT_NAMES.put("getVersion", "version");
        RESULT_TYPES.put("getVersion", STRING_TYPE);
        RESULT_NAMES.put("isValid", "isValid");
        RESULT_TYPES.put("isValid", INTEGER_TYPE);
    }
*/

    /**
     * Creates a new AGSharedApplicationManager
     * @param allApplications
     * @param venueClient
     */
    public AGSharedApplicationManager(HashMap<String,
            AGSharedApplicationDescription> allApplications,
            VenueClientInterface venueClient) {
        this.venueClient = venueClient;
        this.allApplications = allApplications;
    }

    /**
     * Sets the uri of this AGSharedApplicationManager
     * @param uri The uri of the SharedApplicationManager
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * gets the application descriptions of the available applications
     * @return The list of available SharedApplications
     */
    public AGSharedApplicationDescription[] getApplications() {
        return allApplications.values().toArray(
                new AGSharedApplicationDescription[0]);
    }

    /**
     * registering a shared application with the SharedApplicationManager
     * @param token Token through which a SharedApplication can be accessed
     * @param url URL of the application
     */
    public void registerApplication(@SoapParameter("token") String token, @SoapParameter("url") String url) {
        System.err.println("Token " + token + " registered for Application " + url);
        if (registeringApplications.get(token) != null) {
            registeringApplications.remove(token);
            registeringApplications.put(token, url);
            synchronized (registerSync) {
                registerSync.notifyAll();
            }
        }
    }

    /**
     * downloads data to be used by the Application
     * @param venueFilename name of the file in the venue
     * @param applicationUrl URL of the application
     */
    public void downloadData(String venueFilename,String applicationUrl){
        venueClient.downloadData(venueFilename,applicationUrl);
    }

    /**
     * downloads, extracts and executes a shared application on the client machine through Java WebStart
     * @param applicationPackage
     * @param applicationId
     * @param namespace
     * @param dataDownloadUrl
     * @param sessionId
     * @return URL the Application is reachable
     * @throws IOException
     */
    private String executeApplication(
            AGSharedApplicationDescription applicationPackage,
            String applicationId, String namespace, String dataDownloadUrl,
            String sessionId)
            throws IOException {
        String port = Integer.toString(Utils.searchPort(1, 2, true));
        String token = UUID.randomUUID().toString();
        URL startjnlp = new URL(applicationPackage.getUri()
                + "&port=" + port
                + "&applicationManagerUri=" + URLEncoder.encode(uri, "UTF-8")
                + "&token=" + token
                + "&sessionId=" + URLEncoder.encode(sessionId, "UTF-8")
                + "&namespace=" + URLEncoder.encode(namespace, "UTF-8")
                + "&dataUri=" + URLEncoder.encode(dataDownloadUrl, "UTF-8"));
        String os = System.getProperty("os.name").toLowerCase();
        String exec = "";
        String launch = "";
        String launch2 = "";
        String prefix = "";
        String javaHome = System.getProperty("java.home");
        System.err.println(os);

        if (os.indexOf(WINDOWS_OS_STRING) != -1) {
            exec = WINDOWS_EXEC;
        } else if (os.indexOf(LINUX_OS_STRING) != -1) {
            exec = LINUX_EXEC;
        } else if (os.indexOf(MAC_OS_STRING) != -1) {
            exec = MAC_EXEC;
        }
        launch += prefix + javaHome + exec;
        launch2 += prefix + javaHome + exec;
        launch += " -Xnosplash -wait";
        launch2 += " -Xnosplash";
        launch += " " + startjnlp + "&exitOnStart=1";
        launch2 += " " + startjnlp;
        System.err.println("Downloading Application: " + launch);
        Download.startDownloading();
        Process p = Runtime.getRuntime().exec(launch);
        Download.stopDownloading();
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            // Do Nothing
        }
        System.err.println("Launching Application " + launch2);
        Runtime.getRuntime().exec(launch2);

        /*
         * wait for the application to register with me
         */
        registeringApplications.put(token, "");
        long startTime = System.currentTimeMillis();
        synchronized (registerSync) {
            while ((System.currentTimeMillis() < (startTime + DELAY_TIME))
                    && (registeringApplications.get(token).equals(""))) {
                try {
                    registerSync.wait(WAIT_TIME);
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
        }
        String applicationUrl = registeringApplications.get(token);
        registeringApplications.remove(token);
        if (applicationUrl.equals("")) {
            throw new IOException("Application failed to become reachable");
        }
        Vector<String> urls = runningApplications.get(applicationId);
        if (urls == null) {
            urls = new Vector<String>();
        }
        urls.add(applicationUrl);
        runningApplications.put(applicationId, urls);
        return applicationUrl;
    }

    /**
     * Start a shared application
     * @param applicationDescription application to start
     * @param appState initial application state
     * @param namespace
     * @param dataDownloadUrl URL to download data from
     * @param sessionId
     * @return URL of the running application
     * @throws IOException
     */
    public String startApplication(
            ApplicationDescription applicationDescription,
            SharedAppState appState, String namespace,
            String dataDownloadUrl,String sessionId) throws IOException{
        String applicationUrl = null;
        String mimeType = applicationDescription.getMimeType();
        System.out.println("MimeType:" + mimeType);
        if (allApplications.containsKey(mimeType)) {
            AGSharedApplicationDescription applicationPackage =
                allApplications.get(mimeType);
            try {
                System.out.println("Application:" + applicationPackage.getName());
                applicationUrl = executeApplication(applicationPackage,
                        applicationDescription.getId(), namespace,
                        dataDownloadUrl, sessionId);
                try {
                    AGSharedApplication sha = new AGSharedApplication(
                            applicationUrl);
                    sha.setState(appState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IOException("No Application registered for Mime-Type: "
                    + mimeType);
        }
        return applicationUrl;
    }

    /**
     * distributes an event to a set of Applications that match an application decription
     * @param applicationDescription description of applications an event applies to
     * @param eventDescription Event to distribute
     */
    public void distributeEvent(ApplicationDescription applicationDescription,
            EventDescription eventDescription){
        System.err.println("Application: " + applicationDescription.getName() + " Event: " + eventDescription.getEventType());
        Vector<String> urls = runningApplications.get(
                applicationDescription.getId());
        if (urls != null) {
            for (int i = 0; i < urls.size(); i++){
                AGSharedApplication sha;
                try {
                    sha = new AGSharedApplication(urls.get(i));
                    sha.handleEvent(eventDescription);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Stops the applications
     * @throws IOException
     * @throws SoapException
     */
    public void stopApplications() throws IOException, SoapException {
        Iterator<String> iter = runningApplications.keySet().iterator();
        while (iter.hasNext()) {
            String id = iter.next();
            Vector<String> urls = runningApplications.get(id);
            for (int i = 0; (urls != null) && (i < urls.size()); i++) {
                SharedApplication app = new SharedApplication(urls.get(i));
                app.shutdown();
            }
        }
    }


}
