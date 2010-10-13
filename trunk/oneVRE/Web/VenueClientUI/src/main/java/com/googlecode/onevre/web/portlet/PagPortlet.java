/*
 * @(#)PagPortlet.java
 * Created: 16-Apr-2007
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

package com.googlecode.onevre.web.portlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.protocols.events.eventserver.AgEventServer;
import com.googlecode.onevre.protocols.xmlrpc.xmlrpcserver.PagXmlRpcServer;
import com.googlecode.onevre.web.common.Defaults;
import com.googlecode.onevre.web.ui.VenueClientUI;

/**
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class PagPortlet implements Portlet {

	Log log = LogFactory.getLog(this.getClass());

    /**
     * The parameter to pass in the action url for the namespace.
     */
    public static final String NAMESPACE_PARAM = "namespace";

    /**
     * The parameter to pass in the action url to store the preferences.
     */
    public static final String STORE_PREFERENCES_PARAM = "storePreferences";

    /**
     * The portlet session attribute of the UI state.
     */
    public static final String VENUECLIENT_UI_ATTRIBUTE = "venueClientUI";

    /**
     * The portlet session attribute of the XMLRPC server.
     */
    public static final String XMLRPC_SERVER_ATTRIBUTE = "xmlRpcServer";
    /**
     * The portlet session attribute of the XMLRPC server.
     */
    public static final String AGEVENT_SERVER_ATTRIBUTE = "agEventServer";

    /** The timeout of the session. */
    private static final int SESSION_TIMEOUT = 60 * 60 * 24 * 365;

    // The portlet configuration
    private PortletConfig config = null;

    // The url of the JSP file for the view rendering
    private String viewURL = null;

    // The url of the JSP file for the edit rendering
    private String editURL = null;

    // The url of the JSP file for the help rendering
    private String helpURL = null;

    // The url of the JSP file for the about rendering
    private String aboutURL = null;

    // The url of the JSP file for the minimized rendering
    private String minimizedURL = null;

    // The value to append to the unique id
    private long uniqueIdAppender = 0;

    static {
        System.setProperty("java.protocol.handler.pkgs",
                "javax.net.ssl");
        System.setProperty("java.protocol.handler.pkgs",
                "net.sf.ufsc");

    }

    // Gets a unique id
    private synchronized String getUniqueId() {
        uniqueIdAppender++;
        return String.valueOf(System.currentTimeMillis()) + uniqueIdAppender;
    }

    /**
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) {
        this.config = config;
        viewURL = config.getInitParameter("viewURL");
        editURL = config.getInitParameter("editURL");
        helpURL = config.getInitParameter("helpURL");
        aboutURL = config.getInitParameter("aboutURL");
        minimizedURL = config.getInitParameter("minimizedURL");
    }

    /**
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest,
     *     javax.portlet.ActionResponse)
     */
    @SuppressWarnings("unchecked")
	public void processAction(ActionRequest request, ActionResponse response) {
    	System.out.println("request: " + request.toString());

        // Handles the "storePreferences" request from AJAX
        if (request.getParameter(STORE_PREFERENCES_PARAM) != null) {
            String namespace = request.getParameter(NAMESPACE_PARAM);
            PortletSession portletSession = request.getPortletSession();
            VenueClientUI venueClientUI = (VenueClientUI)
                portletSession.getAttribute(
                        namespace + VENUECLIENT_UI_ATTRIBUTE,
                        PortletSession.APPLICATION_SCOPE);
            Enumeration<String> attributes = portletSession.getAttributeNames(
                    PortletSession.APPLICATION_SCOPE);
            PortletPreferences portletPreferences = request.getPreferences();

            if (request.getUserPrincipal() != null) {
            	System.out.println("User is logged in");
                namespace += "pref_";
                while (attributes.hasMoreElements()) {
                    String name = (String) attributes.nextElement();
                    if (name.startsWith(namespace)) {
                        String value = (String) portletSession.getAttribute(
                                name, PortletSession.APPLICATION_SCOPE);
                        name = name.substring(namespace.length());
                        try {
                            portletPreferences.setValue(name, value);
                        } catch (Exception e) {
                        	System.err.println(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                System.err.println("User is not logged in");
            }
            try {
                String myVenues = venueClientUI.getMyVenuesPreference();
                portletSession.setAttribute(namespace + "myVenues", myVenues,
                        PortletSession.APPLICATION_SCOPE);
                if (request.getUserPrincipal() != null) {
                    portletPreferences.setValue("myVenues", myVenues);
                }
            } catch (ReadOnlyException e) {
                e.printStackTrace();
            }
/*            try {
                String trustedServers = venueClientUI.getTrustedServersPreference();
                portletSession.setAttribute(namespace + "trustedServers", trustedServers,
                        PortletSession.APPLICATION_SCOPE);
                if (request.getUserPrincipal() != null) {
                    portletPreferences.setValue("trustedServers", trustedServers);
                }
            } catch (ReadOnlyException e) {
                e.printStackTrace();
            }
*/
            try {
                if (request.getUserPrincipal() != null) {
                    portletPreferences.store();
                }
            } catch (Exception e) {
            	System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the title of the portlet
     * @param request The request to get the locale from
     * @return The title
     */
    public String getTitle(RenderRequest request) {
        return config.getResourceBundle(
                request.getLocale()).getString("javax.portlet.title");
    }

    private ClientProfile loadClientProfile(PortletPreferences preferences,
            Map<String,String> userInfo, PortletSession session, String namespace) {
        String name = (String) session.getAttribute(
                namespace + "name",
                PortletSession.APPLICATION_SCOPE);
        String email = (String) session.getAttribute(
                namespace + "email",
                PortletSession.APPLICATION_SCOPE);
        String location = (String) session.getAttribute(
                namespace + "location",
                PortletSession.APPLICATION_SCOPE);
        String phoneNumber = (String) session.getAttribute(
                namespace + "phoneNumber",
                PortletSession.APPLICATION_SCOPE);
        String publicId = (String) session.getAttribute(
                namespace + "publicId",
                PortletSession.APPLICATION_SCOPE);
        String homeVenue = (String) session.getAttribute(
                namespace + "homeVenue",
                PortletSession.APPLICATION_SCOPE);
        String dname = (String) session.getAttribute(
                namespace + "distinguishedName",
                PortletSession.APPLICATION_SCOPE);
        String type = (String) session.getAttribute(
                namespace + "profileType",
                PortletSession.APPLICATION_SCOPE);

        if (((name == null) || name.equals("")) && (preferences != null)) {
            name = preferences.getValue("name", "");
        }
        if (((email == null) || email.equals("")) && (preferences != null)) {
            email = preferences.getValue("email", "");
        }
        if (((location == null) || location.equals(""))
                && (preferences != null)) {
            location = preferences.getValue("location", "");
        }
        if (((phoneNumber == null) || phoneNumber.equals(""))
                && (preferences != null)) {
            phoneNumber = preferences.getValue("phoneNumber", "");
        }
        if (((publicId == null) || publicId.equals(""))
                && (preferences != null)) {
            publicId = preferences.getValue("publicId", publicId);
        }
        if (((homeVenue == null) || homeVenue.equals(""))
                && (preferences != null)) {
            homeVenue = preferences.getValue("homeVenue", homeVenue);
        }
        if (((dname == null) || dname.equals(""))
                && (preferences != null)) {
            dname = preferences.getValue("distinguishedName", "");
        }
        if (((type == null) || type.equals(""))
                && (preferences != null)) {
            type = preferences.getValue("profileType", type);
        }

        if (((name == null) || name.equals("")) && (userInfo != null)) {
            String first = (String) userInfo.get("user.name.given");
            String last = (String) userInfo.get("user.name.family");
            if ((first == null) || (last == null) || first.equals("")
                    || last.equals("")) {
                String nameInfo = (String) userInfo.get("user.name.full");
                if ((nameInfo != null) && !nameInfo.equals("")) {
                    name = nameInfo;
                }
            } else {
                name = first + " " + last;
            }
        }

        if (((email == null) || email.equals("")) && (userInfo != null)) {
            String emailInfo = (String) userInfo.get(
                    "user.home-info.online.email");
            if (emailInfo == null) {
                emailInfo = (String) userInfo.get("user.email");
            }
            if (emailInfo != null) {
                email = emailInfo;
            }
        }

        if (((location == null) || location.equals("")) && (userInfo != null)) {
            String locationInfo = (String) userInfo.get(
                    "user.business-info.postal.organization");
            if ((locationInfo == null) || locationInfo.equals("")) {
                locationInfo = (String) userInfo.get("user.organization");
            }
            if (locationInfo != null) {
                location = locationInfo;
            }
        }

        if ((publicId == null) || publicId.equals("")) {
            publicId = getUniqueId();
        }
        if ((homeVenue == null) || homeVenue.equals("")) {
            homeVenue = Defaults.PAG_HOME_VENUE;
        }
        if ((type == null) || type.equals("")) {
            type = Defaults.PAG_NODE_TYPE;
        }
        if (name == null) {
            name = "";
        }
        if (email == null) {
            email = "";
        }
        if (location == null) {
            location = "";
        }
        if (phoneNumber == null) {
            phoneNumber = "";
        }
        if (dname == null) {
            dname = "";
        }

        session.setAttribute(namespace + "name", name,
                PortletSession.APPLICATION_SCOPE);
        session.setAttribute(namespace + "email", email,
                PortletSession.APPLICATION_SCOPE);
        session.setAttribute(namespace + "location", location,
                PortletSession.APPLICATION_SCOPE);
        session.setAttribute(namespace + "phoneNumber", phoneNumber,
                PortletSession.APPLICATION_SCOPE);
        session.setAttribute(namespace + "publicId", publicId,
                PortletSession.APPLICATION_SCOPE);
        session.setAttribute(namespace + "homeVenue", homeVenue,
                PortletSession.APPLICATION_SCOPE);
        session.setAttribute(namespace + "distinguishedName", dname,
                PortletSession.APPLICATION_SCOPE);
        session.setAttribute(namespace + "profileType", type,
                PortletSession.APPLICATION_SCOPE);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setName(name);
        clientProfile.setEmail(email);
        clientProfile.setPhoneNumber(phoneNumber);
        clientProfile.setPublicId(publicId);
        clientProfile.setLocation(location);
        clientProfile.setHomeVenue(homeVenue);
        clientProfile.setDistinguishedName(dname);
        clientProfile.setProfileType(type);
        return clientProfile;
    }

    /**
     * @throws IOException
     * @see javax.portlet.Portlet#render(javax.portlet.RenderRequest,
     *     javax.portlet.RenderResponse)
     */
    @SuppressWarnings("unchecked")
	public void render(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        log.info("Namespace = " + response.getNamespace());
        log.info("Portlet-Session = " + request.getPortletSession().getId() + " is new = " + request.getPortletSession().isNew());
        WindowState state = request.getWindowState();

/*        // Set the url of the bridge loader
        String portletBridgeUrl = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + request.getContextPath() + "/jsp/getBridgeJars.jsp";
        BridgeClientCreator.setServer(new URL(portletBridgeUrl));
*/
        // Check for the existence of the venue client UI
        PortletSession portletSession = request.getPortletSession();
        String namespace = response.getNamespace();
        AgEventServer agEventServer = (AgEventServer)
            portletSession.getAttribute(namespace + AGEVENT_SERVER_ATTRIBUTE,
                PortletSession.APPLICATION_SCOPE);
        PagXmlRpcServer xmlRpcServer = (PagXmlRpcServer)
            portletSession.getAttribute(namespace + XMLRPC_SERVER_ATTRIBUTE,
                PortletSession.APPLICATION_SCOPE);
        VenueClientUI venueClientUI = (VenueClientUI)
            portletSession.getAttribute(namespace + VENUECLIENT_UI_ATTRIBUTE,
                PortletSession.APPLICATION_SCOPE);
        portletSession.setMaxInactiveInterval(SESSION_TIMEOUT);

        // If the XmlRpc client doesn't exist, create it now
        if (agEventServer == null) {
            agEventServer = new AgEventServer();
            portletSession.setAttribute(namespace + AGEVENT_SERVER_ATTRIBUTE,
                    agEventServer, PortletSession.APPLICATION_SCOPE);

            log.info("setting AG Event Server: " + namespace + AGEVENT_SERVER_ATTRIBUTE);

        }

        if (xmlRpcServer == null) {
            xmlRpcServer = new PagXmlRpcServer();
            portletSession.setAttribute(namespace + XMLRPC_SERVER_ATTRIBUTE,
                    xmlRpcServer, PortletSession.APPLICATION_SCOPE);
            log.info("setting xmlRpcServer: " + namespace + XMLRPC_SERVER_ATTRIBUTE);
        }

        // If the client ui doesn't exist, create it now
        if (venueClientUI == null) {
            PortletPreferences preferences = request.getPreferences();
            Defaults.setLogFile(preferences.getValue("pagLogFile", ""));
            Defaults.setTrustedServerFile(preferences.getValue("trustedServerFile", "trustedServers.xml"));
            ClientProfile clientProfile = loadClientProfile(preferences,
                    (Map<String,String>) request.getAttribute(PortletRequest.USER_INFO),
                    portletSession, namespace + "pref_");
            String myVenuesPreference = (String) portletSession.getAttribute(
                    namespace + "myVenues", PortletSession.APPLICATION_SCOPE);
            if (myVenuesPreference == null) {
                myVenuesPreference = "";
                if (preferences != null) {
                    myVenuesPreference = preferences.getValue("myVenues", "");
                }
            }
            String trustedServersPreference = (String) portletSession.getAttribute(
                    namespace + "trustedServers", PortletSession.APPLICATION_SCOPE);
            if (trustedServersPreference == null) {
                trustedServersPreference = "";
                if (preferences != null) {
                    trustedServersPreference = preferences.getValue("trustedServers", "");
                }
            }
            log.info("TrustedServers:" + trustedServersPreference);
            venueClientUI = new VenueClientUI(clientProfile, agEventServer,
                    myVenuesPreference, trustedServersPreference);
            portletSession.setAttribute(namespace + VENUECLIENT_UI_ATTRIBUTE,
                venueClientUI, PortletSession.APPLICATION_SCOPE);
            xmlRpcServer.addHandler("", venueClientUI);
        }

        response.setTitle(getTitle(request));
        response.setContentType("text/html");
        if (!state.equals(WindowState.MINIMIZED)) {
            PortletMode mode = request.getPortletMode();
            if (mode.equals(PortletMode.VIEW)) {
                PortletRequestDispatcher dispatcher =
                    config.getPortletContext().getRequestDispatcher(viewURL);
                dispatcher.include(request, response);
            } else if (mode.equals(PortletMode.EDIT)) {
                PortletRequestDispatcher dispatcher =
                    config.getPortletContext().getRequestDispatcher(editURL);
                dispatcher.include(request, response);
            } else if (mode.equals(PortletMode.HELP)) {
                PortletRequestDispatcher dispatcher =
                    config.getPortletContext().getRequestDispatcher(helpURL);
                dispatcher.include(request, response);
            } else if (mode.equals(new PortletMode("about"))) {
                PortletRequestDispatcher dispatcher =
                    config.getPortletContext().getRequestDispatcher(aboutURL);
                dispatcher.include(request, response);
            } else {
                throw new PortletException("Unknown portlet mode: " + mode);
            }
        } else {
            PortletRequestDispatcher dispatcher =
                config.getPortletContext().getRequestDispatcher(minimizedURL);
            dispatcher.include(request, response);
        }
    }

    /**
     * @see javax.portlet.Portlet#destroy()
     */
    public void destroy() {
        // Does nothing just now
    }

}
