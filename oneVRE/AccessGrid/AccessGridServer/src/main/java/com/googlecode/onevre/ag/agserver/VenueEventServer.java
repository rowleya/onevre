
package com.googlecode.onevre.ag.agserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.onevre.ag.agclient.venue.Group;
import com.googlecode.onevre.ag.agclient.venue.GroupClient;
import com.googlecode.onevre.ag.types.EventDescription;
import com.googlecode.onevre.protocols.soap.common.SoapSerializer;
import com.googlecode.onevre.utils.ConfigFile;
import com.googlecode.onevre.utils.Utils;


public class VenueEventServer extends Thread {

    private Log log = LogFactory.getLog(this.getClass());

    private HashMap<String, Group> groups = new HashMap<String, Group>();

    private SSLServerSocket sslsocket = null;

    private String eventLocation = Utils.getLocalHostAddress();

    private int eventPort = Integer.valueOf(VenueServerDefaults.eventPort);

    private boolean done = false;


    public VenueEventServer(HashMap<String, HashMap<String, String>> serverConfig) {
        try {
            String eventHost = ConfigFile.getParameter(serverConfig,
                    VenueServerConfigParameters.VENUE_SERVER_EVENTSERVER_SECTION,
                    VenueServerConfigParameters.EVENTSERVER_HOST,
                    Utils.getLocalHost().getHostName());
            eventPort = Integer.valueOf(ConfigFile.getParameter(serverConfig,
                    VenueServerConfigParameters.VENUE_SERVER_EVENTSERVER_SECTION,
                    VenueServerConfigParameters.EVENTSERVER_PORT,
                    VenueServerDefaults.eventPort));
            String keyStorePasswd = ConfigFile.getParameter(serverConfig,
                    VenueServerConfigParameters.VENUE_SERVER_SECTION,
                    VenueServerConfigParameters.SSL_KEYSTORE_PASSWORD, null);
            String keyStoreType = ConfigFile.getParameter(serverConfig,
                    VenueServerConfigParameters.VENUE_SERVER_SECTION,
                    VenueServerConfigParameters.SSL_KEYSTORE_TYPE,
                    VenueServerDefaults.keyStoreType);

            log.info("Starting VENUE-EVENT-SERVER");
            log.info("VenueEventServer Host: " + eventHost + " port: " + eventPort);
            eventLocation = InetAddress.getByName(eventHost).getCanonicalHostName();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            String keyStoreFileName = ConfigFile.getParameter(serverConfig,
                        VenueServerConfigParameters.VENUE_SERVER_SECTION,
                        VenueServerConfigParameters.VENUE_SERVER_CONFIG_LOCATION, "")
                            + ConfigFile.getParameter(serverConfig,
                        VenueServerConfigParameters.VENUE_SERVER_SECTION,
                        VenueServerConfigParameters.SSL_KEYSTORE_FILE, "");
            FileInputStream keyStoreFile = new FileInputStream(keyStoreFileName);
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(keyStoreFile, keyStorePasswd.toCharArray());
            keyManagerFactory.init(keyStore, keyStorePasswd.toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
            SSLServerSocketFactory sslsocketfactory = sslContext.getServerSocketFactory();
            sslsocket = (SSLServerSocket) sslsocketfactory.createServerSocket(eventPort);
            log.info("Event Location: " + eventLocation +  ":" + eventPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new DoShutdown());
    }

    public VenueEventServer(String eventhost, int eventPort,
            String keyStoreFileName, String keyStorePasswd, String keyStoreType) {
        try {
            log.info("VenueEventServer Host: " + eventhost + " port: " + eventPort);
            eventLocation = InetAddress.getByName(eventhost).getCanonicalHostName();
            this.eventPort = eventPort;
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            FileInputStream keyStoreFile = new FileInputStream(keyStoreFileName);
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(keyStoreFile, keyStorePasswd.toCharArray());
            keyManagerFactory.init(keyStore, keyStorePasswd.toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
            SSLServerSocketFactory sslsocketfactory = sslContext.getServerSocketFactory();
            sslsocket = (SSLServerSocket) sslsocketfactory.createServerSocket(eventPort);
            log.info("Event Location: " + eventLocation + ":" + eventPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new DoShutdown());
    }

    public void run() {
        done = false;
        while (!done) {
            try {
                Socket socket = sslsocket.accept();
                GroupClient groupClient = new GroupClient(socket);
                String groupId = groupClient.getGroupId();
                log.info("Accepting connection from " + socket.getInetAddress().getHostName()
                        + " for group " + groupId);
                synchronized (groups) {
                    Group group = groups.get(groupId);
                    if (group == null) {
                        group = new Group(groupId);
                        group.start();
                        groups.put(groupId, group);
                    }
                    groupClient.setGroup(group);
                    groupClient.start();
                    group.addClient(groupClient);
                }
            } catch (IOException e) {
                e.printStackTrace();
                done = true;
            }
        }
    }

    public void close(String groupId) {
        synchronized (groups) {
            Group group = groups.get(groupId);
            if (group != null) {
                close(group);
            }
        }
    }

    public void close(Group group) {
        synchronized (groups) {
            groups.remove(group.getId());
            group.closeAll();
        }
    }

    public void closeAll() {
        log.info("closeAll");
        done = true;
        synchronized (groups) {
            Iterator<Group> iter = groups.values().iterator();
            while  (iter.hasNext()) {
                close(iter.next());
            }
        }
        try {
            sslsocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addEvent(EventDescription event, String groupId) {
        log.info("AddEvent:" + event.getEventType());
        SoapSerializer soap = new SoapSerializer();
        Group group = groups.get(groupId);
        if (group != null) {
            String soapMessage;
            try {
                soapMessage = soap.serialize(event, "E" + event.hashCode());
                group.addMessage(soapMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addMessage(String message, String groupId) {
        Group group = groups.get(groupId);
        if (group != null) {
            group.addMessage(message);
        }
    }

    public String getLocation() {
        return eventLocation + ":" + eventPort;
    }

    private class DoShutdown extends Thread {
        public void run() {
            closeAll();
        }
    }

}
