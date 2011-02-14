/*
 * @(#)PointOfReferenceServlet.java
 * Created: 10 Dec 2007
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
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.onevre.ag.agserver.VenueServerConfigParameters;
import com.googlecode.onevre.utils.ConfigFile;



/*
import ag3.MulticastAddressAllocator;
import ag3.interfaces.types.BridgeDescription;
import ag3.interfaces.types.MulticastNetworkLocation;
*/
/**
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class VenueServerStartServlet extends HttpServlet {

	Log log = LogFactory.getLog(this.getClass());

    private HashMap<String, HashMap<String, String>> serverConfig = new HashMap<String, HashMap<String,String>>();

    private String configFile="";

    private String configLocation = "";

    private VenueServer venueServer = null;

    /**
     *
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException {
        ServletConfig config = getServletConfig();

        log.info("servletname: "+config.getServletName());
        String configFile = config.getInitParameter("ServerConfig");
        if (configFile!=null){
            configLocation = (new File(new File(configFile).getAbsolutePath()).getParent())+"/";
            try {
                serverConfig = ConfigFile.read(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
             throw new ServletException("No VenueServer config file specified");
        }
        HashMap<String, String> serverCfg = serverConfig.get(VenueServerConfigParameters.VENUE_SERVER_SECTION);
        if (serverCfg==null){
            throw new ServletException("VenueServer config file needs [" +
                    VenueServerConfigParameters.VENUE_SERVER_SECTION + "] Section");
        }
        log.info("ConfigLocation: " + configLocation);
        serverCfg.put(VenueServerConfigParameters.VENUE_SERVER_CONFIG_LOCATION, configLocation);
        if ((serverCfg.get(VenueServerConfigParameters.SSL_KEYSTORE_FILE)==null)
                ||(serverCfg.get(VenueServerConfigParameters.SSL_KEYSTORE_PASSWORD)==null)
                ||(serverCfg.get(VenueServerConfigParameters.SSL_TRUSTSTORE_FILE)==null)
                ||(serverCfg.get(VenueServerConfigParameters.SSL_TRUSTSTORE_PASSWORD)==null)){
            throw new ServletException("VenueServer config file needs \"" +
                    VenueServerConfigParameters.SSL_KEYSTORE_FILE + "\", \"" +
                    VenueServerConfigParameters.SSL_KEYSTORE_PASSWORD + "\", \"" +
                    VenueServerConfigParameters.SSL_TRUSTSTORE_FILE	+ "\" and \"" +
                    VenueServerConfigParameters.SSL_TRUSTSTORE_PASSWORD + "\" parameters in [" +
                    VenueServerConfigParameters.VENUE_SERVER_SECTION + "] Section");
        }
        try {
            venueServer = new VenueServer(serverConfig);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        venueServer.start();
    }

    /**
     *
     * @throws IOException
     * @see javax.servlet.http.HttpServlet#doGet(
     *     javax.servlet.http.HttpServletRequest,
     *     javax.servlet.http.HttpServletResponse)
     */
    public  void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
 // receive SOAP request
 // decode SOAP
 // trigger
    	log.info("VenuesServlet - RequestProto: "+request.getProtocol());
                 response.setStatus(HttpServletResponse.SC_OK);
    }

    public void doPost(HttpServletRequest request,
            HttpServletResponse response)  throws IOException {
        doGet(request, response);
    }


/*
    protected void removeVenue(String id) {
        System.err.println("Removing venue " + id);
        venues.remove(id);
    }
*/
    /**
     *
     * @see javax.servlet.GenericServlet#destroy()
     */
    public void destroy() {
        try {

            venueServer.exit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
