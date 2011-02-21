package com.googlecode.onevre.ag.types.server;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.googlecode.onevre.ag.interfaces.BridgeInterface;
import com.googlecode.onevre.ag.interfaces.ServiceManagerInterface;
import com.googlecode.onevre.ag.types.BridgeDescription;
import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.ConnectionDescription;
import com.googlecode.onevre.ag.types.DataDescription;
import com.googlecode.onevre.ag.types.EventDescription;
import com.googlecode.onevre.ag.types.ProviderProfile;
import com.googlecode.onevre.ag.types.ServiceDescription;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.VenueState;
import com.googlecode.onevre.ag.types.application.ApplicationDescription;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.ag.types.network.UnicastNetworkLocation;
import com.googlecode.onevre.ag.types.service.AGServiceDescription;
import com.googlecode.onevre.ag.types.service.AGServiceManagerDescription;
import com.googlecode.onevre.ag.types.service.AGServicePackageDescription;
import com.googlecode.onevre.ag.types.service.parameter.AGParameter;
import com.googlecode.onevre.ag.types.service.parameter.OptionSetParameter;
import com.googlecode.onevre.ag.types.service.parameter.RangeParameter;
import com.googlecode.onevre.ag.types.service.parameter.TextParameter;
import com.googlecode.onevre.protocols.soap.common.SoapDeserializer;
import com.googlecode.onevre.types.soap.annotation.SoapParameter;
import com.googlecode.onevre.types.soap.annotation.SoapReturn;
import com.googlecode.onevre.types.soap.interfaces.SoapServable;
import com.googlecode.onevre.utils.Preferences;
import com.googlecode.onevre.utils.ServerClassLoader;
import com.googlecode.onevre.utils.Utils;
import com.googlecode.onevre.utils.ui.ProgressDialog;

/**
 * An AGServiceManager
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class AGServiceManager extends SoapServable implements ServiceManagerInterface {

    private String name;

    private String uri;

    private HashMap<String, AGService> services =
        new HashMap<String, AGService>();

    private HashMap<String, AGServiceDescription> serviceDescriptions =
        new HashMap<String, AGServiceDescription>();

    private String nodeServiceUri;

    private Vector<StreamDescription> streamDescriptions =
        new Vector<StreamDescription>();

    private BridgeInterface clientBridge = null;

    private BridgeDescription bridge = null;

    private String pointOfReferenceUrl = null;

    /*  NOT implemented Methods
    static {
        RESULT_NAMES.put("addServiceByName", "serviceDescription");
        RESULT_TYPES.put("addServiceByName", null);
        RESULT_NAMES.put("getServicePackageDescriptions",
                "servicePackageDescription");
        RESULT_TYPES.put("getServicePackageDescriptions", null);
        RESULT_NAMES.put("getResource", "resources");
        RESULT_TYPES.put("getResource", null);
    }
*/

    static {
        SoapDeserializer.mapType(AGServicePackageDescription.class);
        SoapDeserializer.mapType(AGServiceDescription.class);
        SoapDeserializer.mapType(AGServiceManagerDescription.class);
        SoapDeserializer.mapType(Capability.class);
        SoapDeserializer.mapType(VenueState.class);
        SoapDeserializer.mapType(ClientProfile.class);
        SoapDeserializer.mapType(ProviderProfile.class);
        SoapDeserializer.mapType(ApplicationDescription.class);
        SoapDeserializer.mapType(ConnectionDescription.class);
        SoapDeserializer.mapType(DataDescription.class);
        SoapDeserializer.mapType(ServiceDescription.class);
        SoapDeserializer.mapType(StreamDescription.class);
        SoapDeserializer.mapType(EventDescription.class);
        SoapDeserializer.mapType(MulticastNetworkLocation.class);
        SoapDeserializer.mapType(UnicastNetworkLocation.class);
        SoapDeserializer.mapType(NetworkLocation.class);
        SoapDeserializer.mapType(TextParameter.class);
        SoapDeserializer.mapType(OptionSetParameter.class);
        SoapDeserializer.mapType(RangeParameter.class);
        SoapDeserializer.mapType(BridgeDescription.class);
    }

    /**
     * Creates a new AGServiceManager
     * @throws IOException
     *
     */
    public AGServiceManager() {
        name = Utils.getLocalHostAddress();
    }

    public void setClientBridge(BridgeInterface clientBridge) {
        this.clientBridge = clientBridge;
    }

    /**
     * Sets the uri of this AGServiceManager
     * @param uri The uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Invalidates the bridges causing them to be reloaded
     * <dl><dt><b>overrides:</b></dt>
     * <dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#setPointOfReference(java.lang.String)}</dd>
     * </dl>
     * @param url The url of the point of reference server
     */
    public void setPointOfReference(String url) throws IOException {
        if ((pointOfReferenceUrl == null) || !pointOfReferenceUrl.equals(url)) {
            this.pointOfReferenceUrl = url;
            clientBridge.setPointOfReferenceUrl(url);
        }
    }

    /**
     * add a Service
     * <dl><dt><b>overrides:</b></dt>
     * <dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#addService(ag3.interfaces.types.AGServicePackageDescription, java.util.Vector, ag3.interfaces.types.ClientProfile)}</dd></dl>
     * @param servicePackage the service package to register
     * @param configuration The configuration of the client
     * @param profile the client profile
     * @return The added service description
     */
    @SoapReturn (
        name = "serviceDescription"
    )
    public AGServiceDescription addService(
            @SoapParameter("servicePackage") AGServicePackageDescription servicePackage,
            AGParameter[] configuration,
            @SoapParameter("profile") ClientProfile profile) {
        try {
            System.err.println("Adding service "
                    + servicePackage.getName());
            System.err.println("ServiceDesc: " + servicePackage.getDescription());
            System.err.println("ServiceURL: " + servicePackage.getLaunchUrl());
            System.err.println("ServicePkg: " + servicePackage.getPackageName());
            System.err.println("ServiceClass: " + servicePackage.getServiceClass());
            AGService service = executeService(servicePackage);
            service.setName(servicePackage.getName());
            service.setConfiguration(configuration);
            service.setIdentity(profile);
            service.setPackageName(servicePackage.getPackageName());
            AGServiceDescription description = service.getDescription();
            services.put(service.getId(), service);
            serviceDescriptions.put(service.getId(), description);
            sendStreamsToService(service);
            return description;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(
                   "ServiceManager failed to add this service");
        }

    }

    /**
     * gets the service manager description
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#getDescription()}</dd></dl>
     * @return service manager description
     */
    @SoapReturn (
        name = "description"
    )
    public AGServiceManagerDescription getDescription() {
        return new AGServiceManagerDescription(this.name, this.uri);
    }

    /**
     * gets the url of the node service
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#getNodeServiceUrl()}</dd></dl>
     * @return url of node service
     */
    @SoapReturn (
        name = "nodeServiceUrl"
    )
    public String getNodeServiceUrl() {
        return this.nodeServiceUri;
    }

    /**
     * gets the available services
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#getServices()}</dd></dl>
     * @return a vector of available services
     */
    @SoapReturn (
        name = "serviceDescription"
    )
    public AGServiceDescription[] getServices() {
        return new Vector<AGServiceDescription>(
                serviceDescriptions.values()).toArray(
                new AGServiceDescription[0]);
    }

    /**
     * gets the version number of the service manager
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#getVersion()}</dd></dl>
     * @return string with version of service manager
     */
    @SoapReturn (
        name = "version"
    )
    public String getVersion() {
        return "1";
    }

    /**
     * test whether the service manager is valid
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#isValid()}</dd></dl>
     * @return the 'valid' state
     */
    @SoapReturn (
        name = "isValid",
        type = INTEGER_TYPE
    )
    public int isValid() {
        return 1;
    }

    /**
     * removes a service from the service manager
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#removeService(ag3.interfaces.types.AGServiceDescription)}</dd></dl>
     * @param serviceDescription the description of the service to be removed
     * @throws IOException
     */
    public void removeService(
            @SoapParameter("serviceDescription") AGServiceDescription serviceDescription)
            throws IOException {
        String id = serviceDescription.getId();
        AGService service = services.get(id);
        if (service != null) {
            System.err.println("remove service " + id);
            service.shutdown();
            services.remove(id);
            serviceDescriptions.remove(id);

        }
    }

    /**
     * removes all services from the service manager
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#removeServices()}</dd></dl>
     * @throws IOException
     */
    public void removeServices() throws IOException {
        for (AGServiceDescription serviceDescription : serviceDescriptions.values()) {
            removeService(serviceDescription);
        }
    }

    /**
     * sets the url for node service
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#setNodeServiceUrl(java.lang.String)}</dd></dl>
     * @param nodeServiceUri The URI of the node service
     */
    public void setNodeServiceUrl(
            @SoapParameter("nodeServiceUri") String nodeServiceUri) {
        this.nodeServiceUri = nodeServiceUri;
    }

    /**
     * shuts down the service manager
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#shutdown()}</dd></dl>
     * @throws IOException
     */
    public void shutdown() throws IOException {
        clientBridge.stop();
        this.removeServices();
    }

    /**
     * stops all services on service manager
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#stopServices()}</dd></dl>
     * @throws IOException
     */
    public void stopServices() throws IOException {
        for (AGService service : services.values()) {
            service.stop();
        }
    }

    /**
     * downloads, extracts and executes a service on the client machine through Java WebStart
     * @param servicePackage The description of the Package to send to the client machine
     * @return the URL of the running service
     * @throws IOException thrown if the service doesn't become reachable through SOAP
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private AGService executeService(AGServicePackageDescription servicePackage)
            throws Exception {

        ProgressDialog progress = new ProgressDialog("Loading Service",
                false, true);
        progress.setMessage("Loading Service "
                + servicePackage.getPackageName());
        progress.setVisible(true);
        try {
            String resourceDir = Preferences.getInstance().getLocalServicesDir()
                + servicePackage.getName();
            ServerClassLoader classLoader = new ServerClassLoader(
                    getClass().getClassLoader(), new File(resourceDir),
                    new URL(servicePackage.getLaunchUrl()));
            Class<?> serviceClass = Class.forName(servicePackage.getServiceClass(), true, classLoader);
            AGService service = (AGService) serviceClass.newInstance();
            progress.setMessage("Extracting resources for "
                    + servicePackage.getName());
            service.extractResources();
            progress.setVisible(false);
            return service;
        } catch (Exception e) {
            progress.setVisible(false);
            throw e;
        }
    }

    /**
     * Requests to join a bridge
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#joinBridge(ag3.interfaces.types.BridgeDescription)}</dd></dl>
     * @param bridgeDescription The description of the bridge
     * @throws IOException
     */
    public void joinBridge(BridgeDescription bridgeDescription)
            throws IOException {
        if ((bridge == null) || !bridge.equals(bridgeDescription)) {
            try {
                clientBridge.joinBridge(bridgeDescription);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException(e.getMessage());
            }
            this.bridge = bridgeDescription;
        }
    }

    /**
     * Sets the streams of this service manager
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#setStreams(java.util.Vector)}</dd></dl>
     * @param descriptions a vector of stream descriptions
     * @throws IOException
     */
    public void setStreams(StreamDescription[] descriptions)
            throws IOException {
        boolean streamsChanged = false;
        if (descriptions.length != streamDescriptions.size()) {
            streamsChanged = true;
        }
        for (int i = 0; (i < descriptions.length) && !streamsChanged; i++) {
            if (!streamDescriptions.contains(descriptions[i])) {
                streamsChanged = true;
            }
        }

        if (streamsChanged) {
            streamDescriptions = new Vector<StreamDescription>();
            if (descriptions != null) {
                for (int i = 0; i < descriptions.length; i++) {
                    streamDescriptions.add(descriptions[i]);
                }
            }
            //send the streams to the services
            AGServiceDescription[] serviceDescription = getServices();
            System.err.println("Sending streams to " + serviceDescription.length
                + " services: " + serviceDescription);
            try {
                clientBridge.setStreams(streamDescriptions);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException(e.getMessage());
            }
            for (int i = 0; i < serviceDescription.length; i++) {
                sendStreamsToService(services.get(
                        serviceDescription[i].getId()));
            }
        }
    }

    /**
     * adds a stream to the stream descriptions.
     * The stream descriptions are applied to the installed services according
     * to matching capabilities
     * @param description description of the stream
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    public void addStream(StreamDescription description)
            throws IOException {
        streamDescriptions.add(description);

        setStreams(streamDescriptions.toArray(new StreamDescription[0]));
    }


    /**
     * removes a stream from list --
     *     does not yet stop services using this stream
     * @param description stream description of stream to be removed
     * @throws IOException
     */
    public void removeStream(StreamDescription description)
            throws IOException {
        streamDescriptions.remove(description);

        setStreams(streamDescriptions.toArray(new StreamDescription[0]));
    }

    /**
     * Returns true if the streams have changed for a particular service
     * @param service The service to check for streams
     */
    private void sendStreamsToService(AGService service) {
        Vector<Capability> serviceCapabilities = service.getCapabilities();
        System.err.println("AGServiceManager sendStreamsToService "
                + serviceCapabilities.size());

        Iterator<StreamDescription> streams = streamDescriptions.iterator();
        boolean matched = false;
        while (streams.hasNext()) {
            Vector<Capability> matchedCapabilities = new Vector<Capability>();
            StreamDescription stream = streams.next();
//            System.err.println("AGNodeService sendStreamsToService "
//                    + stream.getLocation().getHost() + ":"
//                    + stream.getLocation().getPort() + " - "
//                    + stream.getLocation().getClass());
            for (int i = 0; i < serviceCapabilities.size(); i++) {
//                System.err.println("AGNodeService sendStreamsToService "
//                        + serviceCapabilities.get(i));
                Vector<Capability> streamCaps = stream.getCapability();
                for (int j = 0; j < streamCaps.size(); j++) {
                    if (streamCaps.get(j).matches(serviceCapabilities.get(i))) {
                        if (!matchedCapabilities.contains(
                                serviceCapabilities.get(i))) {
                            matchedCapabilities.add(serviceCapabilities.get(i));
                            System.err.println("Service " + service.getName()
                                    + " matches capability "
                                    + serviceCapabilities.get(i));
                        }
                    }
                }
            }

            if (matchedCapabilities.size() > 0) {
                matched = true;
                NetworkLocation location = stream.getLocation();

                // Send the client bridge location to the service
                NetworkLocation newLocation =
                    clientBridge.getLocalLocation(stream);
                System.err.println("Sending location " + newLocation + " = " + location + " to " + service.getName());

                StreamDescription newStream = new StreamDescription();
                newStream.setId(stream.getId());
                newStream.setName(stream.getName());
                newStream.setDescription(stream.getDescription());
                newStream.setCapability(stream.getCapability());
                newStream.setEncryptionFlag(0);
                newStream.setStatic(stream.getStatic());
                newStream.setLocation(newLocation);
                service.setStream(newStream);
                if (!service.isStarted() && service.isEnabled()) {
                    System.err.println("Starting service");
                    service.start();
                }
            }
        }
        if (!matched) {
            System.err.println("Service capability not found in streams");
            if (service.isStarted()) {
                System.err.println("Stopping service");
                service.stop();
            }
        }
    }

    /**
     * Sets the encryption
     * @param encryption The new encryption
     */
    public void setEncryption(String encryption) {
        clientBridge.setEncryption(encryption);
    }

    /**
     * Instructs the client to run automatic bridging
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#runAutomaticBridging()}</dd></dl>
     * @throws IOException
     */
    public void runAutomaticBridging() throws IOException {
        if (clientBridge != null) {
            try {
                clientBridge.runAutomaticBridging();
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException(e.getMessage());
            }
        }
        bridge = null;
    }

    /**
     * Invalidates the bridges causing them to be reloaded
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.java_interfaces.ServiceManagerInterface#invalidateBridges()}</dd></dl>
     */
/*    public void invalidateBridges() {
        BridgeClientCreator.invalidate();
    }
*/
    /**
     *
     * @see ag3.interfaces.java_interfaces.ServiceManagerInterface#
     *     enableService(ag3.interfaces.types.AGServiceDescription, boolean)
     */
    public void enableService(AGServiceDescription serviceDescription, boolean enabled) {
        AGService service = services.get(serviceDescription.getId());
        if (service != null) {
            service.setEnabled(enabled);
        }
    }

    /**
     *
     * @see ag3.interfaces.java_interfaces.ServiceManagerInterface#
     *     getServiceConfiguration(ag3.interfaces.types.AGServiceDescription)
     */
    public AGParameter[] getServiceConfiguration(AGServiceDescription serviceDescription) {
        AGService service = services.get(serviceDescription.getId());
        return service.getConfiguration().toArray(new AGParameter[0]);
    }

    /**
     *
     * @see ag3.interfaces.java_interfaces.ServiceManagerInterface#
     *     isServiceEnabled(ag3.interfaces.types.AGServiceDescription)
     */
    public boolean isServiceEnabled(AGServiceDescription serviceDescription) {
        AGService service = services.get(serviceDescription.getId());
        return service.isEnabled();
    }

    /**
     *
     * @see ag3.interfaces.java_interfaces.ServiceManagerInterface#
     *     setServiceConfiguration(ag3.interfaces.types.AGServiceDescription,
     *     java.util.Vector)
     */
    public void setServiceConfiguration(AGServiceDescription serviceDescription,
            AGParameter[] config) {
        AGService service = services.get(serviceDescription.getId());
        service.setConfiguration(config);
    }

    /**
     *
     * @see ag3.interfaces.java_interfaces.ServiceManagerInterface#
     *     setClientProfile(ag3.interfaces.types.ClientProfile)
     */
    public void setClientProfile(ClientProfile profile) {
        for (AGServiceDescription serviceDescription : getServices()) {
            AGService service = services.get(serviceDescription.getId());
            service.setIdentity(profile);
        }
    }
}
