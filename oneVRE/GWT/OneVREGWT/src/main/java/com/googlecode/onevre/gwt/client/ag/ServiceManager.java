package com.googlecode.onevre.gwt.client.ag;

import java.util.HashMap;
import java.util.Vector;

import com.googlecode.onevre.gwt.client.ag.types.ServiceDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.interfaces.ServiceManagerInterface;

public class ServiceManager {

    private HashMap<VenueState, Vector<ServiceDescription>> services =
        new HashMap<VenueState, Vector<ServiceDescription>>();

    private ServiceManagerInterface ui = null;

    public Vector<ServiceDescription> addVenue(VenueState state) {
        Vector<ServiceDescription> serviceList = new Vector<ServiceDescription>();
        services.put(state, serviceList);
        return serviceList;
    }

    public ServiceManager(ServiceManagerInterface ui) {
        this.ui = ui;
        ui.setServiceManager(this);
    }

    public void addService(VenueState state, ServiceDescription service) {
        Vector<ServiceDescription> serviceList = services.get(state);
        if (serviceList == null) {
            serviceList = addVenue(state);
        }
        serviceList.add(service);
        ui.updateUI();
    }

    public Vector<ServiceDescription> getServices(VenueState state) {
        Vector<ServiceDescription> serviceList = services.get(state);
        return serviceList;
    }

    public void deleteService(VenueState state, ServiceDescription service) {
        Vector<ServiceDescription> serviceList = services.get(state);
        if (serviceList != null) {
            services.remove(service);
            ui.updateUI();
        }

    }

    public void updateService(VenueState state, ServiceDescription service) {
        Vector<ServiceDescription> serviceList = services.get(state);
        if (serviceList != null) {
            int idx = serviceList.indexOf(service);
            serviceList.remove(service);
            serviceList.insertElementAt(service, idx);
            ui.updateUI();
        }
    }

}
