package com.googlecode.onevre.ag.agserver;

import java.io.IOException;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;

public class DataStoreFtplet extends DefaultFtplet {

    private DataStore dataStore = null;

    public DataStoreFtplet(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public FtpletResult onUploadEnd(FtpSession session, FtpRequest request)
            throws FtpException, IOException {
        String venueId = session.getUser().getName();
        String dir = session.getFileSystemView().getWorkingDirectory().getAbsolutePath();
        FtpletResult result = super.onUploadEnd(session, request);
        if (request.hasArgument()) {
            dataStore.addFile(venueId, dir, request.getArgument());
        }
        return result;
    }
}
