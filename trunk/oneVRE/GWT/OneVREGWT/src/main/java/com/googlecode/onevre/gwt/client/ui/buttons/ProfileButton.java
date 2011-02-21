package com.googlecode.onevre.gwt.client.ui.buttons;

import com.google.gwt.core.client.GWT;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.ClientProfile;
import com.googlecode.onevre.gwt.client.ui.ActionButton;
import com.googlecode.onevre.gwt.client.ui.panels.EditClientProfilePanel;
import com.googlecode.onevre.gwt.client.xmlrpc.SetClientProfile;
import com.googlecode.onevre.gwt.common.client.MessageResponse;
import com.googlecode.onevre.gwt.common.client.MessageResponseHandler;

public class ProfileButton extends ActionButton implements MessageResponseHandler {


    public ProfileButton() {
        setImageUrl("images/userProfile.png");
        setName("Change User Profie");
    }

    @Override
    public void action() {
        GWT.log("in ProfileButton action");
        EditClientProfilePanel ep = new EditClientProfilePanel(this);
        ep.setClientProfile(Application.getUserManager().getLocalUser());
        ep.show();
    }

    public void handleResponse(MessageResponse response) {
        if (response.getResponseCode() == MessageResponse.OK) {
            ClientProfile clientProfile = ((EditClientProfilePanel) response.getSource()).getClientProfile();
            Application.getUserManager().setLocalUser(clientProfile);
            SetClientProfile.setClientProfile();
        }
    }

}
