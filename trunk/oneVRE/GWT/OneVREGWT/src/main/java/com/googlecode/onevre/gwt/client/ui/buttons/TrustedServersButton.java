package com.googlecode.onevre.gwt.client.ui.buttons;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ui.ActionButton;
import com.googlecode.onevre.gwt.client.ui.panels.AddTrustedServerPanel;
import com.googlecode.onevre.gwt.client.xmlrpc.SetTrustedServers;
import com.googlecode.onevre.gwt.common.client.MessageResponse;
import com.googlecode.onevre.gwt.common.client.MessageResponseHandler;

public class TrustedServersButton extends ActionButton implements MessageResponseHandler {


    public TrustedServersButton() {
        setImageUrl("images/trustedServer.png");
        setName("Add Trusted Server");
    }

    @Override
    public void action() {
        GWT.log("in TrustedServerButton action");
        AddTrustedServerPanel atsp = new AddTrustedServerPanel(this);
        atsp.setTrustedServers(Application.getServerManager().getTrustedServers());
        atsp.show();
    }

    public void handleResponse(MessageResponse response) {
        if (response.getResponseCode() == MessageResponse.OK) {
            Vector<VenueServerType> trustedServers =
                ((AddTrustedServerPanel) response.getSource()).getTrustedServers();
            Application.getServerManager().setTrustedServers(trustedServers);
            SetTrustedServers.setTrustedServers();
        }
    }

}
