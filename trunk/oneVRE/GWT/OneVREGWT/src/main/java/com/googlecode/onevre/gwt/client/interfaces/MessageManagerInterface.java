package com.googlecode.onevre.gwt.client.interfaces;

import com.googlecode.onevre.gwt.client.ag.MessageManager;
import com.googlecode.onevre.gwt.client.ag.types.MessageBox;
import com.googlecode.onevre.gwt.client.ag.types.UploadStatus;

public interface MessageManagerInterface {

	public void setMessageManager(MessageManager messageManager);

	public void showUpload(UploadStatus uploadStatus);

	public void setUploadStatus(UploadStatus uploadStatus);

	public void hideUpload();

	public void showMessage(MessageBox message);

}
