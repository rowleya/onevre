package com.googlecode.onevre.gwt.client.ui.buttons;

import com.googlecode.onevre.gwt.client.ui.MultiStateButton;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.MessageResponse;

public class VideoConsumerButton extends MultiStateButton {

    public VideoConsumerButton() {
        setImageUrls(new String[]{"images/display.png", "images/displayDisabled.png"});
        setNames(new String[]{"Disable VideoConsumer", "Enable VideoConsumer"});
    }

    public void action() {
        new MessagePopup("test", null, MessagePopup.INFO, MessageResponse.OK);
    }

}
