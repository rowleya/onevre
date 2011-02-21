package com.googlecode.onevre.gwt.client.interfaces;

import com.googlecode.onevre.gwt.client.ag.MessageManager;
import com.googlecode.onevre.gwt.client.ag.types.MessageBox;
import com.googlecode.onevre.gwt.client.ag.types.UploadStatus;

public interface MessageManagerInterface {

    void setMessageManager(MessageManager messageManager);

    void showUpload(UploadStatus uploadStatus);

    void setUploadStatus(UploadStatus uploadStatus);

    void hideUpload();

    void showMessage(MessageBox message);

}
