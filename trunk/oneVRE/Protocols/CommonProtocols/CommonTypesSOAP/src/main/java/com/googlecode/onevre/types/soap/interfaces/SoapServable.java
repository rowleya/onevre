/*
 * @(#)SoapServable.java
 * Created: 30-Nov-2006
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

package com.googlecode.onevre.types.soap.interfaces;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Vector;


import com.googlecode.onevre.types.soap.annotation.SoapParameter;
import com.googlecode.onevre.types.soap.annotation.SoapReturn;
import com.googlecode.onevre.types.soap.exceptions.SoapException;


/**
 * An abstract class for an object that can be served using soap
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public abstract class SoapServable {

    // A map of the method names to the result names
    private static final HashMap<String, HashMap<String, Class<?>>> METHOD_PARAMETERS =
        new HashMap<String, HashMap<String, Class<?>>>();

    /**
     * adds a method to the METHOD_PARAMETERS map
     * @param method the name of the method
     * @param parameter the parameter name in the SOAP call
     * @param type the java type
     */
    private static void addMethod(String method, String parameter, Class<?> type) {
        HashMap<String, Class<?>> methodMap = METHOD_PARAMETERS.get(method);
        if (methodMap == null) {
            methodMap = new HashMap<String, Class<?>>();
            METHOD_PARAMETERS.put(method, methodMap);
        }
        methodMap.put(parameter, type);
    }

    /**
     * The soap type for a string
     */
    public static final String STRING_TYPE = "xsd:string";
    /**
     * The soap type for a boolean
     */
    public static final String BOOLEAN_TYPE = "xsd:boolean";
    /**
     * The soap type for a double
     */
    public static final String DOUBLE_TYPE = "xsd:double";
    /**
     * The soap type for a float
     */
    public static final String FLOAT_TYPE = "xsd:float";
    /**
     * The soap type for an int
     */
    public static final String INT_TYPE = "xsd:int";

    /**
     * The soap type for an integer
     */
    public static final String INTEGER_TYPE = "xsd:integer";

    /**
     * The soap type for a complex type (set to null as identifier
     */
    public static final String COMPLEX_TYPE = "soap:complex";

    private static final HashMap<Class<?>, String> SOAP_TYPES = new HashMap<Class<?>, String>();

    static {
        SOAP_TYPES.put(String.class, STRING_TYPE);
        SOAP_TYPES.put(int.class, INT_TYPE);
        SOAP_TYPES.put(boolean.class, BOOLEAN_TYPE);
        SOAP_TYPES.put(float.class, FLOAT_TYPE);
        SOAP_TYPES.put(double.class, DOUBLE_TYPE);
    }


    /**
     * Gets the name of the result of calling the method
     * @param method The name of the method
     * @return The name to give the parameter in the soap response
     */
    public String getResultParameterName(Method method) {
        SoapReturn returns = method.getAnnotation(SoapReturn.class);
        if (returns != null) {
            return returns.name();
        }
        return null;
    }

    /**
     * Gets the name of the Soap-Parameter to use for the method parameter
     * @param method The name of the method
     * @param i The index of the parameter
     * @return The soap parameter name of the parameter
     */
    public String getParameterName(Method method, int i) {
        Method meth = method;
        Annotation[][] annotations = null;
        Class<?> cls = method.getDeclaringClass();
        while (cls != null) {
            System.out.println("trying Method " + meth + " from Class " + cls);
            if (meth != null) {
                annotations = meth.getParameterAnnotations();
                for (Annotation annotation : annotations[i]) {
                    if (annotation.annotationType().equals(SoapParameter.class)) {
                        return ((SoapParameter) annotation).value();
                    }
                }
            }
            cls = cls.getSuperclass();
            if (cls != null) {
                try {
                    meth = cls.getDeclaredMethod(method.getName(), method.getParameterTypes());
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
        return null;
    }

    /**
     * Gets the SOAP type of the method
     * @param method The name of the method
     * @return The SOAP type, or null if object is SOAPSerializable
     * @throws SoapException if type is not specified and cannot be worked out
     */
    public String getResultParameterType(Method method) throws SoapException {

        SoapReturn returns = method.getAnnotation(SoapReturn.class);
        if (returns == null) {
            throw new SoapException("Soap Return Type of " + method.getName() + "not specified");
        }
        String returnType =  returns.type();
        if (returnType.equals("n/a")) {
            Class<?> methodReturnType = method.getReturnType();
            if (methodReturnType.isArray()) {
                methodReturnType = methodReturnType.getComponentType();
            }
            if (methodReturnType.equals(Vector.class)) {
                Type t = method.getGenericReturnType();
                methodReturnType = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
            }
            returnType = SOAP_TYPES.get(methodReturnType);
            if (returnType == null)  {
                try {
                    Object object = methodReturnType.newInstance();
                    if (object instanceof SoapSerializable) {
                        return COMPLEX_TYPE;
                    }
                } catch (Exception e) {
                    // do nothing ... worth a try ;-)
                }
                throw new SoapException("Soap Return Type of " + method.getName() + " not specified");
            }
        }
        return returnType;
    }

    private HashMap<String, Class<?>> registerMethod(String method) {
        Method[] methods = getClass().getMethods();
        for (Method m : methods) {
            if (m.getName().equals(method)) {
                Type [] types = m.getGenericParameterTypes();
                Class<?> [] classes = m.getParameterTypes();
                for (int i = 0; i < classes.length; i++) {
                    Class<?> cls = classes[i];
                    if (cls.isArray()) {
                        cls = cls.getComponentType();
                    }
                    if (!cls.equals(Vector.class)) {
                        addMethod(method, ((TypeVariable<?>) types[i]).getName(), cls);
                    }
                }
            }
        }
        return METHOD_PARAMETERS.get(method);
    }

    /**
     * Gets the SOAP type of the parameter of a method
     * @param method The name of the method
     * @param parameterName The name of the Parameter
     * @return The SOAP type, or null if object is SOAPSerializable
     * @throws SoapException
     */
    public Class<?> getParameterType(String method, String parameterName) throws SoapException {
        HashMap<String, Class<?>> params = METHOD_PARAMETERS.get(method);
        if (params == null) {
            params = registerMethod(method);
            if (params == null) {
                throw new SoapException("method " + method + " not registered");
            }
        }
        Class<?> type = params.get(parameterName);
        if (type == null) {
            throw new SoapException("parameter " + parameterName + "not registered for method " + method);
        }
        return type;
    }


}
