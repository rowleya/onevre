/*
 * @(#)ServiceManager.java
 * Created: 06-Nov-2006
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

package com.googlecode.onevre.ag.interfaces;

import java.io.IOException;


import com.googlecode.onevre.ag.types.BridgeDescription;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.service.AGServiceDescription;
import com.googlecode.onevre.ag.types.service.AGServiceManagerDescription;
import com.googlecode.onevre.ag.types.service.AGServicePackageDescription;
import com.googlecode.onevre.ag.types.service.parameter.AGParameter;
import com.googlecode.onevre.types.soap.exceptions.SoapException;


/**
 * Interface to an AG3 ServiceManager
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public interface ServiceManagerInterface {

    /**
     * add a Service
     * @param servicePackage the service package to register
     * @param configuration The configuration of the client
     * @param profile the client profile
     * @return The added service description
     * @throws IOException
     * @throws SoapException
     */
    AGServiceDescription addService(
            AGServicePackageDescription servicePackage,
            AGParameter[] configuration,
            ClientProfile profile)
        throws IOException, SoapException;

    /**
     * gets the service manager description
     * @return service manager description
     * @throws IOException
     * @throws SoapException
     */
    AGServiceManagerDescription getDescription()
        throws IOException, SoapException;

    /**
     * gets the url of the node service
     * @return url of node service
     * @throws IOException
     * @throws SoapException
     */
    String getNodeServiceUrl()
        throws IOException, SoapException;

    /**
     * gets the available services
     * @return a vector of available services
     * @throws IOException
     * @throws SoapException
     */
    AGServiceDescription[] getServices()
        throws IOException, SoapException;

    /**
     * test whether the service manager is valid
     * @return the 'valid' state
     * @throws IOException
     * @throws SoapException
     */
    int isValid() throws IOException, SoapException;

    /**
     * removes a service from the service manager
     * @param serviceDescription the description of the service to be removed
     * @throws IOException
     * @throws SoapException
     */
    void removeService(AGServiceDescription serviceDescription)
            throws IOException, SoapException;

    /**
     * removes all services from the service manager
     * @throws IOException
     * @throws SoapException
     */
    void removeServices() throws IOException, SoapException;

    /**
     * sets the url for node service
     * @param nodeServiceUri
     * @throws IOException
     * @throws SoapException
     */
    void setNodeServiceUrl(String nodeServiceUri)
            throws IOException, SoapException;

    /**
     * shuts down the service manager
     * @throws IOException
     * @throws SoapException
     */
    void shutdown() throws IOException, SoapException;

    /**
     * stops all services on service manager
     * @throws IOException
     * @throws SoapException
     */
    void stopServices() throws IOException, SoapException;

    /**
     * gets the version number of the service manager
     * @return string with version of service manager
     * @throws IOException
     * @throws SoapException
     */
    String getVersion() throws IOException, SoapException;


    /**
     * Requests to join a bridge
     * @param bridgeDescription The description of the bridge
     * @throws IOException
     * @throws SoapException
     */
    void joinBridge(BridgeDescription bridgeDescription)
        throws IOException, SoapException;

    /**
     * Sets the streams of this service manager
     *
     * @param streamDescriptionList a vector of stream descriptions
     * @throws IOException
     * @throws SoapException
     */
    void setStreams(StreamDescription[] streamDescriptionList)
        throws IOException, SoapException;
    /**
     * adds a stream
     * @param argname stream description of new stream
     * @throws IOException
     * @throws SoapException
     */
    void addStream(StreamDescription argname)
        throws IOException, SoapException;
    /**
     * Removes a stream
     * @param argname The stream to remove
     * @throws IOException
     * @throws SoapException
     */
    void removeStream(StreamDescription argname)
        throws IOException, SoapException;
    /**
     * Sets the point of reference for network traffic
     * @param url The url of the point of reference server
     * @throws IOException
     * @throws SoapException
     */
    void setPointOfReference(String url)
        throws IOException, SoapException;

    /**
     * Sets the encryption
     * @param encryption The encryption
     * @throws IOException
     * @throws SoapException
     */
    void setEncryption(String encryption)
        throws IOException, SoapException;
    /**
     * Instructs the client to run automatic bridging
     * @throws IOException
     * @throws SoapException
     */
    void runAutomaticBridging()
        throws IOException, SoapException;

    /**
     * Determines if a service is enabled
     * @param service The service to get the status of
     * @return True if enabled, false otherwise
     * @throws IOException
     * @throws SoapException
     */
    boolean isServiceEnabled(AGServiceDescription service)
        throws IOException, SoapException;

    /**
     * Enables or disables a service
     * @param service The service to control
     * @param enabled True to enable, false to disable
     * @throws IOException
     * @throws SoapException
     */
    void enableService(AGServiceDescription service, boolean enabled)
        throws IOException, SoapException;

    /**
     * Gets the configuration parameters of a service
     * @param service The service to get the configuration of
     * @return The parameters
     * @throws IOException
     * @throws SoapException
     */
    AGParameter[] getServiceConfiguration(AGServiceDescription service)
        throws IOException, SoapException;
    /**
     * Sets the configuration parameters for a service
     * @param service The service to configure
     * @param config The new parameters
     * @throws IOException
     * @throws SoapException
     */
    void setServiceConfiguration(AGServiceDescription service,
            AGParameter[] config)
        throws IOException, SoapException;

    /**
     * Sets the client profile
     * @param profile The new profile
     * @throws IOException
     * @throws SoapException
     */
    void setClientProfile(ClientProfile profile)
        throws IOException, SoapException;
}
