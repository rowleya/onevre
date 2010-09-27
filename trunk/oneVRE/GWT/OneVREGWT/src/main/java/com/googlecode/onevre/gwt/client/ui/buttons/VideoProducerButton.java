package com.googlecode.onevre.gwt.client.ui.buttons;

import com.googlecode.onevre.gwt.client.ui.MultiStateButton;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.MessageResponse;

public class VideoProducerButton extends MultiStateButton {

	public VideoProducerButton() {
		setImageUrls(new String[]{"images/camera.png","images/cameraDisabled.png"});
		setNames(new String[]{"Disable VideoProducers","Enable VideoProducer"});
	}

	public void action() {
		MessagePopup mp = new MessagePopup("test", null, MessagePopup.INFO, MessageResponse.OK);
	}

}
