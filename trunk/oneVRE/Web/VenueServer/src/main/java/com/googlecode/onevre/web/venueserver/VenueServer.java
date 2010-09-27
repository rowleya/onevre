/*
 * @(#)VenueServer.java
 * Created: 29 Sep 2009
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

package com.googlecode.onevre.web.venueserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.util.HashMap;

import javax.servlet.ServletException;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.jsslutils.keystores.KeyStoreLoader;
import org.jsslutils.sslcontext.PKIXSSLContextFactory;

import com.googlecode.onevre.ag.agserver.VenueServerConfigParameters;
import com.googlecode.onevre.ag.agserver.VenueServerDefaults;
import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.utils.ConfigFile;



/**
 * An Access Grid 3 Venue Server
 *
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class VenueServer extends Thread {

    /**
     * All files within a servlet
     */
    public static final String SERVLET_ALL_FILES = "/*";

    private Server server = null;

    private HashMap<String, HashMap<String, String>> serverConfig = new HashMap<String, HashMap<String,String>>();
    /**
     * Creates a new VenueServer
     * @param serverConfig The HashMap that stores the server configuration
     * @throws Exception
     */
    public VenueServer(HashMap<String, HashMap<String,String>> serverConfig) throws Exception {
        this.serverConfig = serverConfig;
        int serverPort =  Integer.valueOf(ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.VENUE_SERVER_PORT,
                VenueServerDefaults.venueServerPort));
        String configLocation =  ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.VENUE_SERVER_CONFIG_LOCATION, "");
        PrintWriter serverLog=null;
        try {
            String serverLogFile = ConfigFile.getParameter(serverConfig,
                    VenueServerConfigParameters.VENUE_SERVER_SECTION,
                    VenueServerConfigParameters.VENUE_SERVER_LOG_FILE,
                    VenueServerDefaults.venueServerLogFile);
            serverLog = new PrintWriter(configLocation + serverLogFile);
        } catch (FileNotFoundException e) {
            throw new ServletException(e);
        }
        server = new Server();
        String keyStore = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.SSL_KEYSTORE_FILE, "");
        String keyStorePasswd = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.SSL_KEYSTORE_PASSWORD, "");
        String keyStoreType = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.SSL_KEYSTORE_TYPE,
                VenueServerDefaults.keyStoreType);
        String trustStore = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.SSL_TRUSTSTORE_FILE, "");
        String trustStorePasswd = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.SSL_TRUSTSTORE_PASSWORD, "");
        String trustStoreType = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.SSL_TRUSTSTORE_TYPE,
                VenueServerDefaults.trustStoreType);
        String PkiCrls = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.PKI_CRL, "");

        if (!keyStore.startsWith("/")){
            keyStore=configLocation + keyStore;
        }
        if (!trustStore.startsWith("/")){
            trustStore=configLocation + trustStore;
        }
        SslSocketConnector connector = new SslSocketConnector();
        connector.setPort(Integer.valueOf(serverPort));
        connector.setMaxIdleTime(30000);
        connector.setHandshakeTimeout(10000);
        connector.setWantClientAuth(true);

        KeyStoreLoader keyStoreLoader = new KeyStoreLoader();
        keyStoreLoader.setKeyStorePath(keyStore);
        keyStoreLoader.setKeyStoreType(keyStoreType);
        keyStoreLoader.setKeyStorePassword(keyStorePasswd);
        KeyStore sslKeyStore = keyStoreLoader.loadKeyStore();

        KeyStoreLoader trustStoreLoader = new KeyStoreLoader();
        trustStoreLoader.setKeyStorePath(trustStore);
        trustStoreLoader.setKeyStoreType(trustStoreType);
        trustStoreLoader.setKeyStorePassword(trustStorePasswd);
        KeyStore sslTrustStore= trustStoreLoader.loadKeyStore();

        PKIXSSLContextFactory sslContextFactory = new PKIXSSLContextFactory(sslKeyStore,keyStorePasswd,sslTrustStore);
        if (PkiCrls!=null){
            for (String crl : PkiCrls.split(";")){
                sslContextFactory.addCrl(crl.trim());
            }
        }
        connector.setSslContext(sslContextFactory.buildSSLContext());

        server.setConnectors(new Connector[]{connector});

        String capString = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_CAPABILITIES,
                VenueServerConfigParameters.VENUE_SERVER_CAPABILITIY_TYPES, "").trim();
        for (String captype: capString.split("[:;]")){
            String ctype = ConfigFile.getParameter(serverConfig, captype.trim(), "vector", "").trim();
            for (String capSection : ctype.split("[:;]")){
                capSection=capSection.trim();
                Capability capability = new Capability(
                        ConfigFile.getParameter(serverConfig, capSection,
                                VenueServerConfigParameters.CAPABILITIY_ROLE, "").trim(),
                        ConfigFile.getParameter(serverConfig, capSection,
                                VenueServerConfigParameters.CAPABILITIY_TYPE, "").trim(),
                        ConfigFile.getParameter(serverConfig, capSection,
                                VenueServerConfigParameters.CAPABILITIY_CODEC, "").trim(),
                        Integer.valueOf(ConfigFile.getParameter(serverConfig, capSection,
                                VenueServerConfigParameters.CAPABILITIY_RATE, "").trim()),0);
                VenueServerConfigParameters.addCapabilty(captype, capability);
            }
        }
        addVenues(serverLog);
        server.start();
    }

    private ServletHolder addVenues(PrintWriter serverLog) throws ServletException{
        ServletContextHandler venuesContext= new ServletContextHandler(ServletContextHandler.SESSIONS);
        VenuesServlet venuesServlet=new VenuesServlet();
        venuesServlet.addVenues(serverConfig, serverLog);
        ServletHolder venues = new ServletHolder(venuesServlet);
        venuesContext.addServlet(venues, SERVLET_ALL_FILES);
        venuesContext.setContextPath("/Venues");
        server.setHandler(venuesContext);
        return venues;
    }

    public synchronized void exit() throws Exception{
        server.stop();
    }

    /**
     * Main Method
     * @param args The name of the VenueServer configuration file
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        HashMap<String, HashMap<String, String>> serverConfig = new HashMap<String, HashMap<String,String>>();
        String configLocation="";
        if (!(args.length>0)){
            System.out.println("Need VenueServer config file!");
            return;
        }
        String configFile = args[0];
        if (configFile!=null){
            configLocation = (new File((new File(configFile)).getParent())).getAbsolutePath()+"/";
            try {
                serverConfig = ConfigFile.read(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
             throw new Exception("No VenueServer config file specified");
        }
        HashMap<String, String> serverCfg = serverConfig.get(VenueServerConfigParameters.VENUE_SERVER_SECTION);
        if (serverCfg==null){
            throw new Exception("VenueServer config file needs [" +
                    VenueServerConfigParameters.VENUE_SERVER_SECTION + "] Section");
        }
        serverCfg.put(VenueServerConfigParameters.VENUE_SERVER_CONFIG_LOCATION, configLocation);
        if ((serverCfg.get(VenueServerConfigParameters.SSL_KEYSTORE_FILE)==null)
                ||(serverCfg.get(VenueServerConfigParameters.SSL_KEYSTORE_PASSWORD)==null)
                ||(serverCfg.get(VenueServerConfigParameters.SSL_TRUSTSTORE_FILE)==null)
                ||(serverCfg.get(VenueServerConfigParameters.SSL_TRUSTSTORE_PASSWORD)==null)){
            throw new Exception("VenueServer config file needs \"" +
                    VenueServerConfigParameters.SSL_KEYSTORE_FILE + "\", \"" +
                    VenueServerConfigParameters.SSL_KEYSTORE_PASSWORD + "\", \"" +
                    VenueServerConfigParameters.SSL_TRUSTSTORE_FILE	+ "\" and \"" +
                    VenueServerConfigParameters.SSL_TRUSTSTORE_PASSWORD + "\" parameters in [" +
                    VenueServerConfigParameters.VENUE_SERVER_SECTION + "] Section");
        }
        VenueServer venueServer = new VenueServer(serverConfig);
        venueServer.start();
    }
}
