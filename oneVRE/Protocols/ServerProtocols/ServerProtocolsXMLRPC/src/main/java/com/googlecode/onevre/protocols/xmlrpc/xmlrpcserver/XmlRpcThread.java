/*
 * @(#)XmlRpcThread.java
 * Created: 15 Sep 2007
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

import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClientRequestImpl;
import org.apache.xmlrpc.common.TypeFactoryImpl;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfigImpl;
import org.apache.xmlrpc.parser.XmlRpcRequestParser;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.googlecode.onevre.protocols.xmlrpc.common.XMLSerializer;
import com.googlecode.onevre.utils.Utils;


/**
 * Runs an XML-RPC request
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class XmlRpcThread extends Thread {

    private Log log = LogFactory.getLog(this.getClass());

    // The server on which to process the request
    private XmlRpcServer server = null;

    // The request to process
    private String request = null;

    // The queue to add the response to
    private XmlRpcResponseQueue queue = null;

    private HttpServletRequest syncRequest = null;

    /**
     * Creates a new XML-RPC Thread
     * @param server The server to process the request
     * @param request The request to process
     * @param queue The queue to add the response to
     */
    public XmlRpcThread(XmlRpcServer server, String request,
            XmlRpcResponseQueue queue) {
        this.server = server;
        this.request = request;
        this.queue = queue;
    }

    public XmlRpcThread(XmlRpcServer server, HttpServletRequest request,
            XmlRpcResponseQueue queue) {
        this.server = server;
        this.syncRequest = request;
        this.queue = queue;
    }

    /**
     * @see java.lang.Thread#run()
     */
    public void run() {
       queue.addResponse(execute());
    }

    public static String getFault(String methodName, int errorCode, String errorMessage) {
        Log log = LogFactory.getLog(XmlRpcThread.class);
        log.error("Returning error message " + errorMessage);
        String xml = "";
        xml += "<?xml version=\"1.0\"?>";
        xml += "<methodResponse>";
        xml += "<fault><value><struct>";
        if (methodName != null) {
            errorMessage += " ... in Method :" + methodName;
        }
        xml += "<member><name>faultCode</name><value><int>";
        xml += errorCode;
        xml += "</int></value></member>";
        xml += "<member><name>faultString</name><value><string>";
        xml += errorMessage;
        xml += "</string></value></member>";
        xml += "</struct></value></fault>";
        xml += "</methodResponse>";
        return xml;

    }

    public String execute() {
        String methodName = null;
        int errorCode = 0;
        String errorMessage = null;

        try {
            XmlRpcHttpRequestConfigImpl config =
                new XmlRpcHttpRequestConfigImpl();
            XmlRpcRequestParser requestParser = new XmlRpcRequestParser(
                    config, new TypeFactoryImpl(server));
            XMLReader parser = new org.apache.xerces.parsers.SAXParser();
            log.info("Request: " + request);
            InputSource input = new InputSource(new StringReader(request));
            parser.setContentHandler(requestParser);
            parser.parse(input);
            methodName = requestParser.getMethodName();
            log.info("Executing Sync:" + methodName + ":" + requestParser.getParams());

            XmlRpcRequest xmlRpcRequest = new XmlRpcClientRequestImpl(
                    config, methodName, requestParser.getParams());
            Object response = server.execute(xmlRpcRequest);

            String xml = "";
            xml += "<?xml version=\"1.0\"?>";
            xml += "<methodResponse>";
//            xml += "<methodName>" + methodName + "</methodName>";
            xml += "<params><param><value>";
            if (response != null) {
                xml += Utils.escapeXmlRpcValue(
                        XMLSerializer.serialize(response));
            } else {
                xml += "<boolean>1</boolean>";
            }
            xml += "</value></param></params>";
            xml += "</methodResponse>";
            return xml;
        } catch (XmlRpcException e) {
            log.error("XmlRpc Exception");
            e.printStackTrace();
            errorMessage = e.getMessage();
            if (errorMessage == null) {
                errorMessage = "Error when calling method";
            }
            errorCode = e.code;
        } catch (Exception e) {
            log.error("General Exception");
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (errorMessage == null) {
            errorMessage = "unkown fault";
        }
        return getFault(methodName, errorCode, errorMessage);
    }

    public String executeSync() {
        String methodName = null;
        int errorCode = 0;
        String errorMessage = null;

        try {
            XmlRpcHttpRequestConfigImpl config =
                new XmlRpcHttpRequestConfigImpl();
            XmlRpcRequestParser requestParser = new XmlRpcRequestParser(
                    config, new TypeFactoryImpl(server));
            XMLReader parser = new org.apache.xerces.parsers.SAXParser();
            log.info("Request: " + request);
            InputSource input = new InputSource(syncRequest.getReader());
            parser.setContentHandler(requestParser);
            parser.parse(input);
            methodName = requestParser.getMethodName();
            log.info("Executing Sync:" + methodName + ":" + requestParser.getParams());

            XmlRpcRequest xmlRpcRequest = new XmlRpcClientRequestImpl(
                    config, methodName, requestParser.getParams());
            Object response = server.execute(xmlRpcRequest);

            String xml = "";
            xml += "<?xml version=\"1.0\"?>";
            xml += "<methodResponse>";
//            xml += "<methodName>" + methodName + "</methodName>";
            xml += "<params><param><value>";
            if (response != null) {
                xml += Utils.escapeXmlRpcValue(
                        XMLSerializer.serialize(response));
            } else {
                xml += "<boolean>1</boolean>";
            }
            xml += "</value></param></params>";
            xml += "</methodResponse>";
            return xml;
        } catch (XmlRpcException e) {
            log.error("XmlRpc Exception");
            e.printStackTrace();
            errorMessage = e.getMessage();
            if (errorMessage == null) {
                errorMessage = "Error when calling method";
            }
            errorCode = e.code;
        } catch (Exception e) {
            log.error("General Exception");
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (errorMessage == null) {
            errorMessage = "unkown fault";
        }
        return getFault(methodName, errorCode, errorMessage);
    }

}
