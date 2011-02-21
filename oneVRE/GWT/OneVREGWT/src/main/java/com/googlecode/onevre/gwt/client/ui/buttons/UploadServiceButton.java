package com.googlecode.onevre.gwt.client.ui.buttons;

import com.googlecode.onevre.gwt.client.ui.ActionButton;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.MessageResponse;

public class UploadServiceButton extends ActionButton {

    public UploadServiceButton() {
        setImageUrl("images/uploadserv.png");
        setName("Upload Service");
    }

    @Override
    public void action() {
        new MessagePopup("test", null, MessagePopup.INFO, MessageResponse.OK);
    }

}
