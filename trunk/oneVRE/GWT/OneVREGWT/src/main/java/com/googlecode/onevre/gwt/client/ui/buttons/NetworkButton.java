package com.googlecode.onevre.gwt.client.ui.buttons;

import com.googlecode.onevre.gwt.client.ui.ActionButton;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.MessageResponse;

public class NetworkButton extends ActionButton {

    public NetworkButton() {
        setImageUrl("images/network.png");
        setName("Network Configuration");
    }

    public void action() {
        MessagePopup mp = new MessagePopup("test", null, MessagePopup.INFO, MessageResponse.OK);
    }

}
