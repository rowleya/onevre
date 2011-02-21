package com.googlecode.onevre.gwt.client.ui.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.client.ui.ActionButton;
import com.googlecode.onevre.gwt.client.ui.panels.CreateVenuePanel;
import com.googlecode.onevre.gwt.client.xmlrpc.CreateVenue;
import com.googlecode.onevre.gwt.common.client.MessageResponse;
import com.googlecode.onevre.gwt.common.client.MessageResponseHandler;

public class CreateVenueButton extends ActionButton implements ClickHandler, MessageResponseHandler {

    private VenueServerType venueServerType = null;
    private CreateVenuePanel cvp = null;
    private String serverURI = null;

    public CreateVenueButton(String serverURI) {
        this.serverURI = serverURI;
        setImageUrl("images/addVenue.png");

        setImageHeight("22px");
        setName("Create new Venue");
        this.getButton().setHeight("22px");
        this.getButton().setWidth("22px");
        this.getButton().addClickHandler(this);
    }

    public VenueServerType getVenueServerType() {
        return venueServerType;
    }

    @Override
    public void action() {
        GWT.log("in CreateVenueButton action");
        cvp = new CreateVenuePanel(this);
        cvp.show();
    }

    public void handleResponse(MessageResponse response) {

        if (response.getResponseCode() == MessageResponse.OK) {
            CreateVenue createVenue = new CreateVenue(Application.getServerManager());
            createVenue.create(serverURI, cvp.getName(), cvp.getDescription(), cvp.getAttributes());
            // create Venue
        }
    }

    public void onClick(ClickEvent arg0) {
        this.action();
    }

}
