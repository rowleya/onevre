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

package com.googlecode.onevre.ag.common.interfaces;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;


import com.googlecode.onevre.ag.interfaces.ServiceManagerInterface;
import com.googlecode.onevre.ag.types.BridgeDescription;
import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.service.AGServiceDescription;
import com.googlecode.onevre.ag.types.service.AGServiceManagerDescription;
import com.googlecode.onevre.ag.types.service.AGServicePackageDescription;
import com.googlecode.onevre.ag.types.service.parameter.AGParameter;
import com.googlecode.onevre.ag.types.service.parameter.ValueParameter;
import com.googlecode.onevre.protocols.soap.common.SoapDeserializer;
import com.googlecode.onevre.protocols.soap.common.SoapResponseHash;
import com.googlecode.onevre.protocols.soap.soapclient.SoapRequest;
import com.googlecode.onevre.types.soap.exceptions.SoapException;
import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;


/**
 * Interface to an AG3 ServiceManager
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ServiceManager implements ServiceManagerInterface {

    // The service manager namespace
    private static final String SERVICE_MANAGER_NS =
        "http://www.accessgrid.org/v3.0/servicemanager";

    static {
        SoapDeserializer.mapType(AGServiceDescription.class);
        SoapDeserializer.mapType(AGServiceManagerDescription.class);
        SoapDeserializer.mapType(Capability.class);
        SoapDeserializer.mapType(StreamDescription.class);
        SoapDeserializer.mapType(ClientProfile.class);
        SoapDeserializer.mapType(StreamDescription.class);
        SoapDeserializer.mapType(ValueParameter.class);
        SoapDeserializer.mapType(AGServicePackageDescription.class);
    }

    // The current soap request
    private SoapRequest soapRequest = null;

    // The url of the server
    private String serverUrl = null;

    /**
     * Creates a new ServiceManager interface
     * @param serverUrl
     * @throws MalformedURLException
     */
    public ServiceManager(String serverUrl) throws MalformedURLException {
        this.soapRequest = new SoapRequest(serverUrl);
        this.serverUrl = serverUrl;
    }

    /**
     * Gets the url being used
     * @return The url
     */
    public String getUrl() {
        return serverUrl;
    }

    /**
     * add a Service
     * <dl><dt><b>overrides:</b></dt>
     * <dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#addService(ag3.interfaces.types.AGServicePackageDescription, java.util.Vector, ag3.interfaces.types.ClientProfile)}</dd></dl>
     * @param servicePackage the service package to register
     * @param configuration The configuration of the client
     * @param profile the client profile
     * @return The added service description
     * @throws IOException
     * @throws SoapException
     */
    public AGServiceDescription addService(
            AGServicePackageDescription servicePackage,
            AGParameter[] configuration,
            ClientProfile profile)
            throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(SERVICE_MANAGER_NS,
            "AddService", "AddServiceRequest",
            new String[]{"servicePackage",
                "configuration",
                "profile"},
            new Object[]{servicePackage, configuration, profile},
            new Object[]{null, null, null},
            new SoapResponseHash(
                 new String[]{SERVICE_MANAGER_NS + "/serviceDescription"},
                 new Class[]{AGServiceDescription.class}));
        Object sd = result.get("serviceDescription");
        if ((sd != null)
                && (sd instanceof AGServiceDescription)) {
            return (AGServiceDescription) sd;
        }
        throw new SoapException("Return type not correct");

    }

    /**
     * gets the service manager description
     * <dl><dt><b>overrides:</b></dt>
     * <dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#getDescription()}</dd></dl>
     * @return service manager description
     * @throws IOException
     * @throws SoapException
     */
    public AGServiceManagerDescription getDescription()
            throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(SERVICE_MANAGER_NS,
                "GetDescription", "GetDescriptionRequest",
                new String[]{},
                new Object[]{},
                new Object[]{},
                new SoapResponseHash(
                        new String[]{SERVICE_MANAGER_NS + "/description"},
                        new Class[]{AGServiceManagerDescription.class}));

        Object desc = result.get("description");
        if ((desc != null)
                && (desc instanceof AGServiceManagerDescription)) {
            return (AGServiceManagerDescription) desc;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * gets the url of the node service
     * <dl><dt><b>overrides:</b></dt>
     * <dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#getNodeServiceUrl()}</dd></dl>
     * @return url of node service
     * @throws IOException
     * @throws SoapException
     */
    public String getNodeServiceUrl()
            throws IOException, SoapException {
       HashMap<String, Object> result = soapRequest.call(SERVICE_MANAGER_NS,
            "GetNodeServiceUrl", "GetNodeServcieUrlRequest",
            new String[]{},
            new Object[]{},
            new Object[]{},
            new SoapResponseHash(
                    new String[]{SERVICE_MANAGER_NS + "/nodeServiceUri"},
                    new Class[]{String.class}));

       Object nsuri = result.get("nodeServiceUri");
       if ((nsuri != null) && (nsuri instanceof String)) {
            return (String) nsuri;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * gets the available services
     * <dl><dt><b>overrides:</b></dt>
     * <dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#getServices()}</dd></dl>
     * @return a vector of available services
     * @throws IOException
     * @throws SoapException
     */
    public AGServiceDescription[] getServices()
            throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(SERVICE_MANAGER_NS,
            "GetServices",
            "GetServicesRequest",
            new String[]{},
            new Object[]{},
            new Object[]{},
            new SoapResponseHash(
                new String[]{SERVICE_MANAGER_NS + "/serviceDescription"},
                new Class[]{AGServiceDescription.class}));
        Object sd = result.get("serviceDescription");
        if ((sd != null) && AGServiceDescription.class.equals(sd.getClass().getComponentType())) {
            System.err.println("ServiceManager getServices " + ((AGServiceDescription[]) sd).length);
            return (AGServiceDescription[]) sd;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * test whether the service manager is valid
     * <dl><dt><b>overrides:</b></dt>
     * <dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#isValid()}</dd></dl>
     * @return the 'valid' state
     * @throws IOException
     * @throws SoapException
     */
    public int isValid() throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(SERVICE_MANAGER_NS,
                "IsValid",
                "IsValidRequest",
                new String[]{},
                new Object[]{},
                new Object[]{},
                new SoapResponseHash(
                        new String[]{SERVICE_MANAGER_NS + "/isValid"},
                        new Class[]{Integer.class}));

        Object isValid = result.get("serviceDescription");
        if ((isValid != null) && (isValid instanceof Integer)) {
            return ((Integer) isValid).intValue();
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * removes a service from the service manager
     * <dl><dt><b>overrides:</b></dt>
     * <dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#removeService(ag3.interfaces.types.AGServiceDescription)}</dd></dl>
     * @param serviceDescription the description of the service to be removed
     * @throws IOException
     * @throws SoapException
     */
    public void removeService(AGServiceDescription serviceDescription)
            throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
                "RemoveService", "RemoveServiceRequest",
                new String[]{"serviceDescription"},
                new Object[]{serviceDescription},
                new Object[]{null},
                null);
    }

    /**
     * removes all services from the service manager
     * <dl><dt><b>overrides:</b></dt>
     * <dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#removeServices()}</dd></dl>
     * @throws IOException
     * @throws SoapException
     */
    public void removeServices()
            throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
            "RemoveServices", "RemoveServicesRequest",
            new String[]{},
            new Object[]{},
            new Object[]{},
            null);
    }

    /**
     * sets the url for node service
     * <dl><dt><b>overrides:</b></dt>
     * <dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#setNodeServiceUrl(java.lang.String)}</dd></dl>
     * @param nodeServiceUri the uri for the node service
     * @throws IOException
     * @throws SoapException
     */
    public void setNodeServiceUrl(String nodeServiceUri)
            throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
            "SetNodeServiceUrl", "SetNodeServiceUrlRequest",
            new String[]{"nodeServiceUri"},
            new Object[]{nodeServiceUri},
            new Object[]{SoapSerializable.STRING_TYPE},
            null);
    }

    /**
     * shuts down the service manager
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#shutdown()}</dd></dl>
     * @throws IOException
     * @throws SoapException
     */
    public void shutdown() throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
            "Shutdown", "ShutdownRequest",
            new String[]{},
            new Object[]{},
            new Object[]{},
            null);
    }

    /**
     * stops all services on service manager
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#stopServices()}</dd></dl>
     * @throws IOException
     * @throws SoapException
     */
    public void stopServices()
            throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
            "StopServices", "StopServicesRequest",
            new String[]{},
            new Object[]{},
            new Object[]{},
            null);
    }

    /**
     * gets the version number of the service manager
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#getVersion()}</dd></dl>
     * @return string with version of service manager
     * @throws IOException
     * @throws SoapException
     */
    public String getVersion()
            throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(SERVICE_MANAGER_NS,
            "GetVersion", "GetVersionRequest",
            new String[]{},
            new Object[]{},
            new Object[]{},
            new SoapResponseHash(
                    new String[]{SERVICE_MANAGER_NS + "/version"},
                    new Class[]{String.class}));
        Object version = result.get("version");
        if ((version != null) && (version instanceof String)) {
            return (String) version;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * adds a stream
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#addStream(ag3.interfaces.types.StreamDescription)}</dd></dl>
     * @param description stream description of new stream
     * @throws IOException
     * @throws SoapException
     */
    public void addStream(StreamDescription description)
            throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
                "AddStream", "AddStreamRequest",
                new String[]{"description"},
                new Object[]{description},
                new Object[]{null},
                null);
    }

    /**
     * Requests to join a bridge
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#joinBridge(ag3.interfaces.types.BridgeDescription)}</dd></dl>
     * @param bridgeDescription The description of the bridge
     * @throws IOException
     * @throws SoapException
     */
    public void joinBridge(BridgeDescription bridgeDescription)
            throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
                "JoinBridge", "JoinBridgeRequest",
                new String[]{"bridgeDescription"},
                new Object[]{bridgeDescription},
                new Object[]{null},
                null);
    }

    /**
     * Removes a stream
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#removeStream(ag3.interfaces.types.StreamDescription)}</dd></dl>
     * @param description The stream to remove
     * @throws IOException
     * @throws SoapException
     */
    public void removeStream(StreamDescription description)
            throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
                "RemoveStream", "RemoveStreamRequest",
                new String[]{"description"},
                new Object[]{description},
                new Object[]{null},
                null);
    }

    /**
     * Sets the streams of this service manager
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#setStreams(java.util.Vector)}</dd></dl>
     * @param streamDescriptionList a vector of stream descriptions
     * @throws IOException
     * @throws SoapException
     */
    public void setStreams(StreamDescription[] streamDescriptionList)
            throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
                "SetStreams", "SetStreamsRequest",
                new String[]{"streamDescription"},
                new Object[]{streamDescriptionList},
                new Object[]{null},
                null);
    }

    /**
     * Sets the point of reference for network traffic
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#setPointOfReference(java.lang.String)}</dd></dl>
     * @param url The url of the point of reference server
     * @throws IOException
     * @throws SoapException
     */
    public void setPointOfReference(String url)
            throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
                "SetPointOfReference", "SetPointOfReferenceRequest",
                new String[]{"url"},
                new Object[]{url},
                new Object[]{SoapSerializable.STRING_TYPE},
                null);
    }

    /**
     * Sets the encryption
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#setEncryption(java.lang.String)}</dd></dl>
     * @param encryption The encryption
     * @throws IOException
     * @throws SoapException
     */
    public void setEncryption(String encryption)
            throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
                "SetEncryption", "SetEncryptionRequest",
                new String[]{"encryption"},
                new Object[]{encryption},
                new Object[]{SoapSerializable.STRING_TYPE},
                null);
    }

    /**
     * Instructs the client to run automatic bridging
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#runAutomaticBridging()}</dd></dl>
     * @throws IOException
     * @throws SoapException
     */
    public void runAutomaticBridging()
            throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
                "RunAutomaticBridging", "RunAutomaticBridgingRequest",
                new String[]{},
                new Object[]{},
                new Object[]{},
                null);
    }

    /**
     * Invalidates the bridges causing them to be reloaded
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#invalidateBridges()}</dd></dl>
     * @throws IOException
     * @throws SoapException
     */
    public void invalidateBridges()
            throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
                "InvalidateBridges", "InvalidateBridgesRequest",
                new String[]{},
                new Object[]{},
                new Object[]{},
                null);
    }

    /**
     *
     * @see ag3.interfaces.java_interfaces.ServiceManagerInterface#
     *     enableService(ag3.interfaces.types.AGServiceDescription, boolean)
     * @throws IOException
     * @throws SoapException
     */
    public void enableService(AGServiceDescription service, boolean enabled)
            throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
                "EnableService", "EnableService",
                new String[]{"service", "enabled"},
                new Object[]{service, enabled},
                new Object[]{null, SoapSerializable.BOOLEAN_TYPE},
                null);
    }

    /**
     *
     * @see ag3.interfaces.java_interfaces.ServiceManagerInterface#
     *     isServiceEnabled(ag3.interfaces.types.AGServiceDescription)
     * @throws IOException
     * @throws SoapException
     */
    public boolean isServiceEnabled(AGServiceDescription service)
            throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(SERVICE_MANAGER_NS,
                "IsServiceEnabled", "IsServiceEnabledRequest",
                new String[]{"service"},
                new Object[]{service},
                new Object[]{null},
                new SoapResponseHash(
                        new String[]{SERVICE_MANAGER_NS + "/isServiceEnabled"},
                        new Class[]{Boolean.class}));
        Object isServiceEnabled = result.get("isServiceEnabled");
        if ((isServiceEnabled != null)
                && (isServiceEnabled instanceof Boolean)) {
            return (Boolean) isServiceEnabled;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     *
     * @see ag3.interfaces.java_interfaces.ServiceManagerInterface#
     *     getServiceConfiguration(ag3.interfaces.types.AGServiceDescription)
     * @throws IOException
     * @throws SoapException
     */
    public AGParameter[] getServiceConfiguration(AGServiceDescription service)
            throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(SERVICE_MANAGER_NS,
                "GetServiceConfiguration",
                "GetServiceConfigurationRequest",
                new String[]{"service"},
                new Object[]{service},
                new Object[]{null},
                new SoapResponseHash(
                    new String[]{SERVICE_MANAGER_NS + "/serviceConfiguration"},
                    new Class[]{AGParameter.class}));
        Object serviceConfiguration = result.get("serviceConfiguration");
        if ((serviceConfiguration != null)
                && AGParameter.class.equals(serviceConfiguration.getClass().getComponentType())) {
            return (AGParameter[]) serviceConfiguration;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * @see ag3.interfaces.java_interfaces.ServiceManagerInterface#
     *      setServiceConfiguration(ag3.interfaces.types.AGServiceDescription,
     *      ag3.interfaces.types.agservice.AGParameter[])
     * @throws IOException
     * @throws SoapException
     */
    public void setServiceConfiguration(AGServiceDescription service,
            AGParameter[] config) throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
                "SetServiceConfiguration", "SetServiceConfigurationRequest",
                new String[]{"service", "config"},
                new Object[]{service, config},
                new Object[]{null, null},
                null);
    }

    /**
     *
     * @see ag3.interfaces.java_interfaces.ServiceManagerInterface#
     *     setClientProfile(ag3.interfaces.types.ClientProfile)
     * @throws IOException
     * @throws SoapException
     */
    public void setClientProfile(ClientProfile profile)
            throws IOException, SoapException {
        soapRequest.call(SERVICE_MANAGER_NS,
                "SetClientProfile", "SetClientProfileRequest",
                new String[]{"profile"},
                new Object[]{profile},
                new Object[]{null},
                null);
    }
}
