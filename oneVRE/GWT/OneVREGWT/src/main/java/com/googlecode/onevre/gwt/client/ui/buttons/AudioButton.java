package com.googlecode.onevre.gwt.client.ui.buttons;

import com.googlecode.onevre.gwt.client.ui.MultiStateButton;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.MessageResponse;

public class AudioButton extends MultiStateButton {

	public AudioButton() {
		setImageUrls(new String[]{"images/audio.png","images/audioDisabled.png"});
		setNames(new String[]{"Disable Audio","Enable Audio"});
	}

	public void action() {
		MessagePopup mp = new MessagePopup("test", null, MessagePopup.INFO, MessageResponse.OK);
		mp.show();
	}

}
