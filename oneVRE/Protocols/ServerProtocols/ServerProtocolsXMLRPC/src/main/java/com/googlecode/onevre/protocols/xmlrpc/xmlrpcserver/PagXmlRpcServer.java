/*
 * @(#)XmlRpcServer.java
 * Created: 16 Sep 2007
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

package com.googlecode.onevre.protocols.xmlrpc.xmlrpcserver;

import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;

import com.googlecode.onevre.protocols.xmlrpc.common.XMLSerializer;
import com.googlecode.onevre.utils.TimeoutListener;
import com.googlecode.onevre.utils.Utils;

/**
 * A server of XMLRPC objects
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class PagXmlRpcServer {

	Log log = LogFactory.getLog(this.getClass());

    private XmlRpcServer server = new XmlRpcServer();

    private XmlRpcResponseQueue queue = new XmlRpcResponseQueue();

    private XmlRpcMapping mapping = new XmlRpcMapping();

    /**
     * Creates a new PagXmlRpcServer
     */
    public PagXmlRpcServer() {
        log.info("starting XML_RPC server");
        Utils.setDefaultSslConnection();
        server.setHandlerMapping(mapping);
        XmlRpcServerConfigImpl severConfig =
            (XmlRpcServerConfigImpl) server.getConfig();
        severConfig.setEnabledForExtensions(true);
        severConfig.setContentLengthOptional(false);

    }

    /**
     * Adds a handler for an object
     * @param prefix The prefix for requests to the object
     * @param object The object to map to
     */
    public void addHandler(String prefix, Object object) {
        mapping.addHandler(prefix, object);
    }

    /**
     * Handles an XmlRpc request from a servlet, putting the result in the queue
     * @param request The request to process
     */
    public void handleRequest(String request) {
        XmlRpcThread thread = new XmlRpcThread(server, request, queue);
        thread.start();
    }

    public String handleSyncRequest(HttpServletRequest request){
    	log.info("in PagXmlRpcServer:handleSyncRequest");
        XmlRpcThread thread = new XmlRpcThread(server, request, queue);
        return thread.executeSync();
    }

    /**
     * Gets the next response on the queue
     * @return The next response
     */
    public String getNextResponse() {
        return queue.getNextResponse();
    }

    /**
     * Adds a method call request to the queue
     * @param methodName The name of the method to call
     * @param params The parameters of the method
     */
    public void addRequest(String methodName, Object[] params) {
        String xml = "";
        xml += "<?xml version=\"1.0\"?>";
        xml += "<methodCall>";
        xml += "<methodName>" + methodName + "</methodName>";
        xml += "<params>";
        for (int i = 0; i < params.length; i++) {
            xml += "<param><value>";
            xml += Utils.escapeXmlRpcValue(
                    XMLSerializer.serialize(params[i]));
            xml += "</value></param>";
        }
        xml += "</params>";
        xml += "</methodCall>";
        queue.addResponse(xml);
    }
/*
    public void addEvent(AgEvent event) {
    	try {
			JSONJAXBContext context = new JSONJAXBContext(JSONConfiguration.natural().build(),AgEvent.class);
			JSONMarshaller marshaller = context.createJSONMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.marshallToJSON(event, writer);
	        queue.addResponse(writer.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
    }
*/
    /**
     * Sets the time out listener
     * @param listener The listener to set
     */
    public void setListener(TimeoutListener listener) {
        queue.setListener(listener);
    }
}
