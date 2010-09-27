/*
 * @(#)XmlRpcMapping.java
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

import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;

/**
 * A mapping between objects and XML-RPC requests
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class XmlRpcMapping implements XmlRpcHandlerMapping {

    // The map of names to objects
    private HashMap<String, Object> map = new HashMap<String, Object>();

    /**
     * Adds a handler for an object
     *
     * Each public, non-static method is served that does not have
     * a void return type
     *
     * If key has already been used, it is overwritten
     *
     * @param key The key to put before the names of the methods (key.method),
     *            or null for none (dot is left off in this case)
     * @param object The object to register
     */
    public void addHandler(String key, Object object) {
        map.put(key, object);
    }

    /**
     * @see org.apache.xmlrpc.server.XmlRpcHandlerMapping#getHandler(
     *     java.lang.String)
     */
    public XmlRpcHandler getHandler(String pHandlerName)
            throws XmlRpcException {
        String key = "";
        String methodName = pHandlerName;
        Object object = null;
        int index = pHandlerName.indexOf(".");
        if (index != -1) {
            key = pHandlerName.substring(0, index);
            methodName = pHandlerName.substring(index + 1);
        }

        object = map.get(key);
        if (object == null) {
            throw new XmlRpcNoSuchHandlerException(
                    "No object registered for key \"" + key + "\"");
        }
        return new com.googlecode.onevre.protocols.xmlrpc.xmlrpcserver.XmlRpcHandler(object, methodName);
    }
}
