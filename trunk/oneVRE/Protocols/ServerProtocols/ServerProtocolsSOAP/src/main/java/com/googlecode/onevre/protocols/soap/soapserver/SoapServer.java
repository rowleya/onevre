/*
 * @(#)SoapServer.java
 * Created: 28-Nov-2006
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

package com.googlecode.onevre.protocols.soap.soapserver;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.googlecode.onevre.types.soap.interfaces.SoapServable;
import com.googlecode.onevre.utils.Utils;


/**
 * A server for SOAP objects
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class SoapServer {

    private static final String PORT_SEP = ":";

    private Server server = null;

    private ServletHolder holder = null;

    private SoapServerClient soapServerClient=null;

 //   private HashMap<String, SoapServable> objects =
 //       new HashMap<String, SoapServable>();

    // True if the server is secure
    private boolean secure = false;

    // The port the server is using
    private int port = 0;

/*
    public SoapServer(int port, boolean secure) throws Exception  {
        HttpContext context = new HttpContext();
        ServletHandler handler = new ServletHandler();
        holder = handler.addServlet("/*", SoapServerClient.class.getName());
        context.addHandler(handler);
        context.setContextPath("/");
        context.setParentClassLoader(getClass().getClassLoader());
        context.setClassLoaderJava2Compliant(true);
        server = new Server();
        if (secure) {
            server.addListener(new SslListener(new InetAddrPort(port)));
        } else {
            server.addListener(PORT_SEP + port);
        }
        server.addContext(context);
        server.start();
        this.secure = secure;
        this.port = port;
    }
*/


    /**
     * Creates a new SoapServer
     * @param port The port to listen on
     * @param secure True if ssl is to be used
     * @throws Exception
     */
    public SoapServer(int port, boolean secure) throws Exception  {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        soapServerClient = new SoapServerClient();

        holder = new ServletHolder(soapServerClient);
        context.addServlet(holder,"/*");
        context.setContextPath("/");
        server = new Server();
        Connector connector = null;
        if (secure) {
            connector = new SslSocketConnector();
        } else {
            connector = new SocketConnector();
        }
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});
        server.setHandler(context);
        this.secure = secure;
        this.port = port;
    }

    /**
     * Registers an object to be served
     * @param path The path to the object
     * @param object The object to serve
     */
    public void registerObject(String path, SoapServable object) {
        soapServerClient.registerObject(path, object);
    }

    /**
     * Finds the URL of an object
     * @param object The object
     * @return The url or null if none
     */
    public String findURLForObject(SoapServable object) {
        String path = soapServerClient.findPathForObject(object);
        if (path == null) {
            return null;
        }
        String hostname = Utils.getLocalHostAddress();
        if (secure) {
            return "https://" + hostname + PORT_SEP + port + path;
        }
        return "http://" + hostname + PORT_SEP + port + path;
    }

    /**
     * Finds an object for a path
     * @param path The path
     * @return The object, or null if not mapped
     */
    public SoapServable findObjectForPath(String path) {
        return soapServerClient.findObjectForPath(path);
    }

    /**
     * Starts the server
     * @throws Exception
     */
    public void start() throws Exception {
        if (!server.isStarted()) {
            server.start();
        }
    }

    /**
     * Stops the server
     */
    public void end() {
    	try {
            server.stop();
        } catch (Exception e) {
            // Do Nothing
        }
    }
}
