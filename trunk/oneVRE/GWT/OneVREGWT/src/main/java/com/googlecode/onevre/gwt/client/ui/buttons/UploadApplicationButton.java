package com.googlecode.onevre.gwt.client.ui.buttons;

import com.googlecode.onevre.gwt.client.ui.ActionButton;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.MessageResponse;

public class UploadApplicationButton extends ActionButton {

    public UploadApplicationButton() {
        setImageUrl("images/uploadapp.png");
        setName("Upload Application");
    }

    @Override
    public void action() {
        new MessagePopup("test", null, MessagePopup.INFO, MessageResponse.OK);
    }

}
