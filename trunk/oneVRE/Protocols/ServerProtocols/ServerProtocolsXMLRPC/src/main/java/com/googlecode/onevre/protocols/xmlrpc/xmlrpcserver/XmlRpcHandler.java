/*
 * @(#)XmlRpcHandler.java
 * Created: 1 Sep 2007
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

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.TypeConverter;
import org.apache.xmlrpc.common.TypeConverterFactory;
import org.apache.xmlrpc.common.TypeConverterFactoryImpl;

/**
 * A handler of XML-RPC requests
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class XmlRpcHandler implements org.apache.xmlrpc.XmlRpcHandler {

	Log log = LogFactory.getLog(this.getClass());

    // The object to execute the method on
    private Object object = null;

    // The name of the method to execute
    private String methodName = null;

    // The type converter factory
    private TypeConverterFactory typeFactory = new TypeConverterFactoryImpl();

    /**
     * Creates a new XML-RPC Handler
     *
     * @param object The object to execute the method on
     * @param methodName The name of the method
     */
    public XmlRpcHandler(Object object, String methodName) {
        this.object = object;
        this.methodName = methodName;
    }

    /**
     * @see org.apache.xmlrpc.XmlRpcHandler#execute(
     *     org.apache.xmlrpc.XmlRpcRequest)
     */
    public Object execute(XmlRpcRequest pRequest) throws XmlRpcException {
        Object result = null;
        Method[] methods = object.getClass().getMethods();
        Method method = null;
        Object[] parameters = new Object[pRequest.getParameterCount()];
        for (int i = 0; (i < methods.length) && (method == null); i++) {
            if (methods[i].getName().equals(methodName)) {
                Class<?>[] parameterTypes = methods[i].getParameterTypes();
                if (pRequest.getParameterCount()
                        == methods[i].getParameterTypes().length) {
                    boolean parametersCorrect = true;
                    for (int j = 0; (j < parameterTypes.length)
                            && parametersCorrect; j++) {
                        TypeConverter typeConverter =
                            typeFactory.getTypeConverter(parameterTypes[j]);
                        Object parameter = pRequest.getParameter(j);
                        if (typeConverter.isConvertable(parameter)) {
                            parameters[j] = typeConverter.convert(parameter);
                        } else {
                            parametersCorrect = false;
                        }
                    }
                    if (parametersCorrect) {
                        method = methods[i];
                    }
                }
            }
        }
        if (method == null) {
            throw new XmlRpcException(
                    "Method " + methodName + " not found in "
                    + object.getClass());
        }
        log.info("invoke " + method.toString() + " on params "  + parameters.toString() );
        try {
            result = method.invoke(object, parameters);
        } catch (Exception e) {
            e.printStackTrace();
            throw new XmlRpcException(e.getMessage());
        }
        log.info("returns " + result.toString());
        return result;
    }

}
