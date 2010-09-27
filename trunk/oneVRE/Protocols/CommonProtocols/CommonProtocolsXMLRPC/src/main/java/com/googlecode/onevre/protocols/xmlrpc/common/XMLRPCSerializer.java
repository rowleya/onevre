/*
 * @(#)XMLRPCSerializer.java
 * Created: 13-Nov-2006
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

package com.googlecode.onevre.protocols.xmlrpc.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Vector;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * Converts an object to an XMLRPC Hashtable
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class XMLRPCSerializer {

    private XMLRPCSerializer() {
        // Does Nothing
    }

    /**
     * Converts an object to an XMLRPC Hashtable
     * @param object The object to serialize
     * @return The serialized hashtable
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    public static Hashtable<String, Object> serialize(SoapSerializable object)
            throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException {
        Hashtable<String, Object> serialized = new Hashtable<String, Object>();
        String[] fields = object.getFields();
        Class cls = object.getClass();
        for (int i = 0; i < fields.length; i++) {
            String getMethod = "get"
                + fields[i].substring(0, 1).toUpperCase()
                + fields[i].substring(1);
            Method getter = cls.getMethod(getMethod, new Class[0]);
            Object value = getter.invoke(object, (Object[]) new Class[0]);
            if (value instanceof SoapSerializable) {
                serialized.put(fields[i], serialize((SoapSerializable) value));
            } else if (value instanceof Vector) {
                Vector<Hashtable<?, ?>> values =
                    (Vector<Hashtable<?, ?>>) value;
                for (int j = 0; j < values.size(); j++) {
                    Object val = values.get(j);
                    if (val instanceof SoapSerializable) {
                        values.set(j, serialize((SoapSerializable) val));
                    }
                }
                serialized.put(fields[i], value);
            } else {
                serialized.put(fields[i], value);
            }
        }
        return serialized;
    }

}
