/*
 * @(#)SoapServerClient.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.googlecode.onevre.protocols.soap.common.SoapDeserializer;
import com.googlecode.onevre.protocols.soap.common.SoapObjectParser;
import com.googlecode.onevre.protocols.soap.common.SoapSerializer;
import com.googlecode.onevre.types.soap.SoapObject;
import com.googlecode.onevre.types.soap.exceptions.SoapException;
import com.googlecode.onevre.types.soap.interfaces.SoapServable;

/**
 * A Client to the Soap Server
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class SoapServerClient extends HttpServlet {

	Log log = LogFactory.getLog("SoapServerClient");

    private static final long serialVersionUID = 1L;

    private static final String REQUEST_POSTFIX = "Request";

    private static final String RESPONSE_POSTFIX = "Response";

    private HashMap<String, SoapServable> objects =
        new HashMap<String, SoapServable>();

//    private SoapServer server = null;

    /**
     * Registers an object to be served
     * @param path The path to the object
     * @param object The object to serve
     */
    public void registerObject(String path, SoapServable object) {
        objects.put(path, object);
    }

    /**
     * Finds an object for a path
     * @param path The path
     * @return The object, or null if not mapped
     */
    public SoapServable findObjectForPath(String path) {
        return objects.get(path);
    }

    /**
     * Finds the URL of an object
     * @param object The object
     * @return The url or null if none
     */
    public String findPathForObject(SoapServable object) {
        Iterator<String> iter = objects.keySet().iterator();
        while (iter.hasNext()) {
            String path = (String) iter.next();
            SoapServable obj = objects.get(path);
            if (obj.equals(object)) {
                return path;
            }
        }
        return null;
    }

/*    protected void setServer(SoapServer server) {
        this.server = server;
    }
*/


    private String parseSoap(String request, SoapServable object)
            throws IOException, SAXException,
            IllegalAccessException, InvocationTargetException, SoapException {

        SoapObjectParser parser = new SoapObjectParser();
        SoapObject requestObject = parser.parse(request);

        String name = requestObject.getName();
        String methodName = name.substring(0,1).toLowerCase()+name.substring(1);
        String methodNameSpace = requestObject.getNameSpace();

        SoapDeserializer deserializer = new SoapDeserializer();

        Vector<Method> methodCandidates = new Vector<Method>();
        Method idealMethod = null;
        for (Method m:object.getClass().getMethods()){
        //	log.info ("Class Method: " + m.getName() + " methodName: "+ methodName );
            if (m.getName().equals(methodName) || methodName.equals(m.getName()+REQUEST_POSTFIX)){
                int i=0;
                Vector<String> paramNames =  requestObject.getSubObjectNames();
                Type[] methodParams = m.getGenericParameterTypes();
                for (int j = 0; j < methodParams.length; j++) {
                    String methodParam = object.getParameterName(m, j);
        //            log.info("ParamName[" + j+"]: " + paramNames.get(i) + " in Method: "+methodParam);
                    if (i >= paramNames.size()){
                        break;
                    }
                    if (paramNames.get(i).equalsIgnoreCase(methodParam)) {
                        String soapType = requestObject.getSubObject(paramNames.get(i)).firstElement().getSoapType();
                        if (soapType!=null) {
                            Class<?> cls = (Class<?>)methodParams[j];
        //                    log.info("Paramtype ["+ j + "]: " + soapType + " in Method: " + cls);
                            if (cls.equals(Vector.class)) {
                                break;
                            }
                            if (cls.isArray()){
                                cls=cls.getComponentType();
                            }
                            if (!SoapDeserializer.getJavaType(soapType).equals(cls)){
                                break;
                            };
                        }
                        i++;
                    }
                }
                if (i!=paramNames.size()){
                    continue;
                }
                if (paramNames.size()==methodParams.length){
                    idealMethod=m;
                    break;
                }
                methodCandidates.add(m);
            }
        }
        if (idealMethod==null){
            idealMethod = methodCandidates.firstElement();
        }
        Class<?>[] argTypes = idealMethod.getParameterTypes();
        Object[] arguments = new Object[argTypes.length];
        Type[] methodParams = idealMethod.getGenericParameterTypes();
        int i=0;
        for (int j = 0; j < methodParams.length; j++) {
            String methodParam = object.getParameterName(idealMethod, j);
            Vector<String> paramNames =  requestObject.getSubObjectNames();
            Object obj = null;
            if (paramNames.get(i).equalsIgnoreCase(methodParam)) {
                Vector<SoapObject> soapObjectVector = requestObject.getSubObject(paramNames.get(i));
                Class<?> cls = (Class<?>)methodParams[j];
                if (cls.isArray()){
                    cls=cls.getComponentType();
                    String soapType = SoapDeserializer.getSoapType(cls);
                    obj = Array.newInstance(cls, soapObjectVector.size());
                    for (int k=0; k<soapObjectVector.size();k++){
                        SoapObject so = soapObjectVector.get(k);
                        so.setSoapType(soapType);
                        Object o = deserializer.deserialize(so);
                        Array.set(obj, k, o);
                     }
                } else {
                    SoapObject so = soapObjectVector.firstElement();
                    so.setSoapType(SoapDeserializer.getSoapType(cls));
                    obj = deserializer.deserialize(so);
                }
            }
            arguments[i]=obj;
            i++;
        }
        // find resultType
        Object result = null;
        log.info("invoking "+ idealMethod.getDeclaringClass().getCanonicalName() + " " + idealMethod.toString());
        String args="";
        String	sep="on ( ";
        String close="";
        for (Object arg : arguments){
            args += sep + arg.toString();
            sep = ", ";
            close = " )";
        }
        log.info(args + close);

        result = idealMethod.invoke(object, arguments);
        String resultName = object.getResultParameterName(idealMethod);
        methodName = name.replace(REQUEST_POSTFIX, "") + RESPONSE_POSTFIX;


        SoapSerializer serializer = new SoapSerializer();
        if (result==null){
            return serializer.serializeMethod(methodNameSpace, methodName,
                    new String[]{}, new Object[]{},
                    new Object[]{});
        }
        String resultType = object.getResultParameterType(idealMethod);

        log.info("resultName: " +resultName +" "+ methodName);

        return serializer.serializeMethod(methodNameSpace, methodName,
                new String[]{resultName}, new Object[]{result},
                new Object[]{resultType});

    }

    /**
     *
     * @see javax.servlet.http.HttpServlet#doPost(
     *     javax.servlet.http.HttpServletRequest,
     *     javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();
        PrintWriter writer = response.getWriter();
        int contentLength = request.getContentLength();
        char[] data = new char[contentLength];
        int dataRead = 0;
        response.setContentType("text/xml");

        try {
            String path = request.getPathInfo();
            SoapServable object = findObjectForPath(path);
            String result = null;
            if (object == null) {
                throw new Exception("Object not found for path " + path);
            }

            while (dataRead < data.length) {
                int charsRead = reader.read(data, dataRead,
                        data.length - dataRead);
                if (charsRead < 0) {
                    throw new IOException("Data ended early: length: " + dataRead + "of "+ data.length +"\n "+ new String(data));
                }
                dataRead += charsRead;
            }
            log.info("SOAP-request: "+ new String(data));

            result = parseSoap(new String(data), object);
            log.info("SOAP-result: "+ result);
            writer.print(result);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String faultString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV="
                    + "\"http://schemas.xmlsoap.org/soap/envelope/\">";
            faultString += "<SOAP-ENV:Body>";
            faultString = "<SOAP-ENV:Fault>";
            faultString += "<faultcode>SOAP-ENV:Server</faultcode>";
            faultString += "<faultstring>" + e.getMessage()
                + "</faultstring>";
            faultString += "</SOAP-ENV:Fault>";
            faultString += "</SOAP-ENV:Body>";
            faultString += "</SOAP-ENV:Envelope>";
            writer.print(faultString);
            log.error("SOAP-error: "+ faultString);
            writer.flush();
        }
    }
}
