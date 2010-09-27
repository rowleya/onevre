/*
 * @(#)XMLSerializer.java
 * Created: 30 Aug 2007
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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.googlecode.onevre.utils.Utils;

/**
 * Serializes classes to XML
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class XMLSerializer {

    private XMLSerializer() {
        // Does Nothing
    }

    /**
     * Serializes an object
     * @param object The object to serialize
     * @return The XML representing the object
     */
    public static String serialize(Object object) {
        return serialize(object.getClass().getSimpleName(),
                object);
    }

    /**
     * Serializes an object
     * @param name The name to start the tag with
     * @param object The object to serialize
     * @return The XML representing the object
     */
    public static String serialize(String name, Object object) {
        return serialize(name, object, object.getClass());
    }

    /**
     * Serializes an object
     * Uses every public non-static "get" or "is" method of the object that
     * has a corresponding "set" method.  Anything not in one of these
     * methods will not be serialized.
     *
     * @param name The name to start the tag with
     * @param object The object to serialize
     * @param cls The class of the object
     * @return XML representing the object
     */
    @SuppressWarnings("unchecked")
	public static String serialize(String name, Object object, Class<?> cls) {
        String xml = "";
        Method[] methods = cls.getMethods();

        if ((object instanceof Boolean)
                || (object instanceof Byte)
                || (object instanceof Short)
                || (object instanceof Integer)
                || (object instanceof Long)
                || (object instanceof Float)
                || (object instanceof Double)
                || (object instanceof String)) {
            xml += "<" + name;
            xml += " class=\"" + cls.getName() + "\"";
            xml += " value=\"";
            xml += Utils.escapeXmlRpcValue(object.toString());
            xml += "\">";
            xml += "</" + name + ">";
        } else if (cls.isArray()) {
            if (name.endsWith("[]")) {
                name = name.substring(0, name.length() - 2);
            }
            xml += "<" + name;
            xml += " class=\"" + cls.getName() + "\"";
            xml += " length=\"" + Array.getLength(object) + "\"";
            xml += ">";
            for (int j = 0; j < Array.getLength(object);
                    j++) {
                xml += serialize(name, Array.get(object, j),
                        cls.getComponentType());
            }
            xml += "</" + name + ">";
        } else if (object instanceof Collection) {
            xml += "<" + name;
            xml += " class=\"" + cls.getName() + "\"";
            xml += ">";
            Iterator iterator = ((Collection) object).iterator();
            while (iterator.hasNext()) {
                xml += serialize(name, iterator.next());
            }
            xml += "</" + name + ">";
        } else if (object instanceof Map) {
            xml += "<" + name;
            xml += " class=\"" + cls.getName() + "\"";
            xml += ">";
            Iterator iterator = ((Map) object).keySet().iterator();
            while (iterator.hasNext()) {
                Object key = iterator.next();
                Object value = ((Map) object).get(key);
                xml += "<element>";
                xml += serialize("key", key);
                xml += serialize("value", value);
                xml += "</element>";
            }
            xml += "</" + name + ">";
        } else {
            xml += "<" + name + " class=\"" + cls.getName() + "\">";
            for (int i = 0; i < methods.length; i++) {
                String method = methods[i].getName();
                String variable = null;
                Class<?> type = methods[i].getReturnType();
                int mod = methods[i].getModifiers();
                if (Modifier.isPublic(mod) && !Modifier.isStatic(mod) && methods[i].getParameterTypes().length==0) {
                    if (method.startsWith("get")) {
                        variable = method.substring("get".length());
                    } else if (method.startsWith("is")) {
                        variable = method.substring("is".length());
                    }
                    if (variable != null) {
                        String setMethod = "set" + variable;
                        Class<?> setType = type;
                        Method set = null;
                        while ((set == null) && (setType != null)) {
                            try {
                                set = cls.getMethod(setMethod,
                                        new Class[]{setType});
                            } catch (Exception e) {
                                set = null;
                                setType = setType.getSuperclass();
                            }
                        }
                        if (set != null) {
                            int setMod = set.getModifiers();
                            if (Modifier.isPublic(setMod)
                                    && !Modifier.isStatic(setMod)) {
                                try {
                                    Object result = methods[i].invoke(object,
                                            new Object[0]);
                                    if (result!=null){
                                        variable =
                                            variable.substring(0, 1).toLowerCase()
                                            + variable.substring(1);
                                        if (type.isPrimitive()) {
                                            xml += serialize(variable, result,
                                                    type);
                                        } else {
                                            xml += serialize(variable, result);
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("serialize - var: "+variable);
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            xml += "</" + name + ">";
        }
//        System.err.println("XML:" + xml);
        return xml;
    }
}
