package com.googlecode.onevre.ag.types.server;

import java.util.HashMap;
import java.util.Vector;

import com.googlecode.onevre.ag.common.interfaces.AGNetworkService;
import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.service.AGNetworkServiceDescription;


/**
 * @author Tobias M Schiebeck
 *
 * A manager class for available network services in a venue.  This class
 * also includes a network service matcher that can select a chain of services
 * to use for streams that do not match capabilities of a node.
 */
public class AGNetworkServicesManager {

    private HashMap<String, AGNetworkServiceDescription> services = new HashMap<String, AGNetworkServiceDescription>();

    /**
     * Registers a network service with the manager.
     *
     * @param networkServiceDescription a description of the network service to add (AGNetworkServiceDescription).
     *
     */
    public void registerService (AGNetworkServiceDescription networkServiceDescription) {
        if (networkServiceDescription==null) {
            throw new RuntimeException("Missing network service parameter, failed to add service.");
        }
        if (services.containsKey(networkServiceDescription.getUri())){
            throw new RuntimeException("A service at url " + networkServiceDescription.getUri() + " is already present, failed to add service.");
        }
        services.put(networkServiceDescription.getUri(), networkServiceDescription);
    }

    /**
     * Removes a network service from the manager.
     *
     * @param networkServiceDescription a description of the network service to remove (AGNetworkServiceDescription).
     *
     */
    public void unRegisterService(AGNetworkServiceDescription networkServiceDescription) {
        if (networkServiceDescription==null) {
            throw new RuntimeException("Missing network service parameter, failed to remove service.");
        }
        if (services.remove(networkServiceDescription.getUri())==null){
            throw new RuntimeException("Service " + networkServiceDescription.getUri() +" is already unregistered");
        }
    }

    private Vector<AGNetworkServiceDescription> matchInCapabilities(StreamDescription stream, Vector<AGNetworkServiceDescription> services){
        Vector<AGNetworkServiceDescription> matchingServices = new Vector<AGNetworkServiceDescription>();
        Vector<Capability> streamProducerCaps = new Vector<Capability>();
        for (Capability cap : stream.getCapability()){
            if (cap.getRole().equals(Capability.PRODUCER)){
                streamProducerCaps.add(cap);
            }
        }
        for (AGNetworkServiceDescription service : services){
            boolean serviceMatch = true;
            for (Capability streamCap : streamProducerCaps ){
                boolean match = false;
                for (Capability cap: service.getCapabilities()){
                    if (cap.getRole().equals(Capability.CONSUMER) && streamCap.matches(cap)){
                        match = true;
                    }
                }
                if (!match){
                    serviceMatch = false;
                    break;
                }
            }
            if (!matchingServices.contains(service) && serviceMatch){
                matchingServices.add(service);
            }
        }
        return matchingServices;
    }

    private Vector<AGNetworkServiceDescription> matchOutCapabilities(Vector<AGNetworkServiceDescription> services, Vector<Capability> capabilties){
           Vector<AGNetworkServiceDescription> matchingServices = new Vector<AGNetworkServiceDescription>();
        Vector<Capability> nodeConsumerCaps = new Vector<Capability>();
        for (Capability cap : capabilties){
            if (cap.getRole().equals(Capability.CONSUMER)){
                nodeConsumerCaps.add(cap);
            }
        }
        for (AGNetworkServiceDescription service : services){
            boolean serviceMatch = true;
            for (Capability capability : nodeConsumerCaps){
                boolean match = false;
                for  (Capability cap: service.getCapabilities()){
                    if (cap.getRole().equals(Capability.PRODUCER) && capability.matches(cap)){
                        match=true;
                    }
                }
                if (!match){
                    serviceMatch = false;
                    break;
                }
            }
            if (!matchingServices.contains(service) && serviceMatch){
                matchingServices.add(service);
            }
        }
        return matchingServices;
    }

    private HashMap<StreamDescription, Vector<AGNetworkServiceDescription>> Match(Vector<StreamDescription> streamList, Vector<Capability> capabilties){
        HashMap<StreamDescription, Vector<AGNetworkServiceDescription>> streamServiceList = new HashMap<StreamDescription, Vector<AGNetworkServiceDescription>>();
        for (StreamDescription stream : streamList){
            Vector<AGNetworkServiceDescription> matchingServices = matchOutCapabilities(matchInCapabilities(stream, (Vector<AGNetworkServiceDescription>)services.values()), capabilties);
            if (matchingServices.size()>0){
                streamServiceList.put(stream, matchingServices);
            }
        }
        return streamServiceList;
    }


    /**
     * Matches streams to available network services. Uses network services to resolve
     * mismatch between stream capabilities and capabilities of a node.
     *
     * @param streamList a list of mismatched streams.
     * @param nodeCapabilities capabilities of a node.
     * @return a list of new streams that matches given node capabilities.
     *
     */
    public HashMap<StreamDescription,String> resolveMismatch(Vector<StreamDescription> streamList, Vector<Capability> nodeCapabilities) {

        if (nodeCapabilities==null){
            throw new RuntimeException("NetworkServicesManager.ResolveMismatch: Capability parameter is missing, can not complete matching");
        }
        if (streamList==null){
            throw new RuntimeException("NetworkServicesManager.ResolveMismatch: Stream list parameter is missing, can not complete matching");
        }
        HashMap<StreamDescription,String> matchedStreams = new HashMap<StreamDescription, String>();
        if (services.size() > 0){
            HashMap<StreamDescription,Vector<AGNetworkServiceDescription>> matchedServices = Match(streamList, nodeCapabilities);
            for (StreamDescription stream : matchedServices.keySet()){
                Vector<AGNetworkServiceDescription> netServices = matchedServices.get(stream);
                for (AGNetworkServiceDescription service: netServices){
                    try {
                        AGNetworkService netServiceProxy = new AGNetworkService(service.getUri());
                        StreamDescription outStream = netServiceProxy.transform(stream);
                        matchedStreams.put(outStream, service.getUri());
                    } catch (Exception e) {
                        //self.log.exception('ResolveMismatch: Transform for service %s failed'%service)
                    }
                }
            }
        }
        return matchedStreams;
    }
}
