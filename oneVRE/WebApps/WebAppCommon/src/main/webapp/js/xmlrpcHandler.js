function pag_xmlrpcHandler(venueClientControllerId, jabberMessageDivId, profileFormId, venueNameId,
    venueUriId, treeDivId, imageLocation, setPrefsUrl, namespace, storePrefsUrl, dataDownloadUrl, dataUploadUrl,
    uploadDialogId, uploadDialogTextId, uploadProgressBarId, uploadTotalId, uploadCurrentId, uploadPercentId ) {

    this.venueClientControllerId=venueClientControllerId;
    this.venueClientController = document.getElementById(
        venueClientControllerId);

    this.jabberRoomColour = "#00CC00";
    this.jabberColours = new Array("#FF0000", "#00FF00",
        "#0000FF", "#FF00FF", "#00FFFF");
    this.jabberFromColours = new Array();
    this.jabberParticipantCount = 0;
    this.jabberMessageDiv = document.getElementById(jabberMessageDivId);

    this.profileFormId=profileFormId;
    this.setPrefsUrl=setPrefsUrl;
    this.dataDownloadUrl=dataDownloadUrl;
    this.dataUploadUrl=dataUploadUrl;
     this.namespace=namespace;
    this.storePrefsUrl=storePrefsUrl;
    this.venueNameId=venueNameId;
    this.venueUriId=venueUriId;
    this.treeDivId=treeDivId;
    this.imageLocation=imageLocation;
    this.localUser=null;

    this.uploadDialogId = uploadDialogId;
    this.uploadDialogTextId = uploadDialogTextId;
    this.uploadProgressBar = document.getElementById(uploadProgressBarId);
    this.uploadTotal = document.getElementById(uploadTotalId);
    this.uploadCurrent = document.getElementById(uploadCurrentId);
    this.uploadPercent = document.getElementById(uploadPercentId);

    this.jabberAddMessage = function(jabberMessageXml) {
 //		alert ("Jabber Messager entered:" +jabberMessageXml);

        var jabberMessage = this.venueClientController.getObjectDec(
            jabberMessageXml);
        var from = jabberMessage.getFrom();
        var colour = this.jabberFromColours[from];
        if ((colour == null) || (colour == "") && (from != "")) {
            colour = this.jabberColours[this.jabberParticipantCount
                % this.jabberColours.length];
            this.jabberParticipantCount += 1;
            this.jabberFromColours[jabberMessage.getFrom()] = colour;
        }
        if (from == "") {
            colour = this.jabberRoomColour;
            from = "*** ";
        } else {
            from = "&lt;" + from + "&gt; ";
        }
        this.jabberMessageDiv.innerHTML += "<span style='color: "
             + colour + "';>"
            + "[" + jabberMessage.getDate() + "] " + from + " "
            + jabberMessage.getMessageWithLinks() + "</span><br/>";
        this.jabberMessageDiv.scrollTop = this.jabberMessageDiv.scrollHeight;
    }

    this.jabberClearWindow = function() {
        this.jabberMessageDiv.innerHTML = "";
    }

    this.downloadDataItemResponse= function(dataXML){
        alert("data down: "+dataXML);
    }

    this.setClientProfileResponse = function(clientXml){
         var client = this.venueClientController.getObjectDec(clientXml);
        pag_setPreferences(this.setPrefsUrl, this.namespace, this.storePrefsUrl,
            "name", client.getName(),
            "email", client.getEmail(),
            "phoneNumber", client.getPhoneNumber(),
            "location", client.getLocation(),
            "homeVenue", client.getHomeVenue(),
            "profileType", client.getProfileType());
        document.getElementById(this.profileFormId).style.visibility = "hidden";
        document.pag_venueClientController.setClientProfile(
            client.getProfileType(),client.getName(),
            client.getEmail(),client.getPhoneNumber(),
            client.getLocation(), client.getHomeVenue());
         this.eventModifyUser(clientXml);
    }

    this.exitVenueResponse=function(venueStateXml) {
        this.enterVenueResponse(venueStateXml);
    }

    this.enterVenueResponse=function(venueStateXml, refresh){
        var venueState=this.venueClientController.getObjectDec(venueStateXml);
        var add=null;
        if (venueState != null) {
            var venuelist = venueState.getVenueList();
            var participants = venueState.getClients();
            var data  = venueState.getData();
            var services  = venueState.getServices();
            var applications = venueState.getApplications();
            if (venuelist != null){
                var roots=venuelist.getRoots();
                var name = venueState.getName();
                var uri = venueState.getUri();
                   document.getElementById(this.venueNameId).innerHTML = name;
                      document.getElementById(this.venueUriId).value = uri;
                   pag_venueTree =
                    new Tree("pag_venueTree", this.treeDivId, 22, "#ccccff", false,
                    true, "cursor: pointer;", this.imageLocation, false, true);
                pag_venueTree.addTreeNode(null, "pag_venueRoot",
                    imageLocation + "tree_blank.gif",
                    "", 1, null, null, null, null);
                pag_addVenuesToList(this.namespace, "pag_venueRoot", roots, this.venueClientControllerId);
                pag_venueTree.drawTree();
                pag_venueTree.refreshTree();
                if (refresh==null){
                       pag_setStatus('Starting services...');
                       this.venueClientController.getCapabilitiesXml();
                }
                add=true;
            }
            pag_createDataTree(participants,data,services,applications,this.dataDownloadUrl,this.dataUploadUrl,this.venueClientControllerId,add);
            if (refresh==null){
                //pag_xmlRpcClient.call("startApplicationQueue");
            } else {
                for (var i=0; i<applications.size();i++){
                    //pag_xmlRpcClient.call("getApplicationParticipants",applications.get(i).getUri(),applications.get(i).getId());
                }
            }
            return true;
        } else {
            alert("Error entering venue!");
            return false;
        }
    };

    this.enterPreviousVenueResponse=function(venueStateXml) {
        this.enterVenueResponse(venueStateXml);
    };

    this.startApplicationQueueResponse=function(applicationsXml){
        var applications=this.venueClientController.getObjectDec(applicationsXml);
         for (var i=0; i<applications.size();i++){
            pag_xmlRpcClient.call("getApplicationParticipants",applications.get(i).getUri(),applications.get(i).getId());
        }
    };

    this.getApplicationParticipantsResponse = function(applicationParticipantsXml){
        var appParticipants = this.venueClientController.getObjectDec(applicationParticipantsXml);
         var participant;
         var appId;
         var profile;
         for (var i=0; i<appParticipants.size();i++){
             participant=appParticipants.get(i);
             appId=participant.getAppId();
             profile=participant.getClientProfile();
            if (profile!=null){
                 if (pag_add_participant(profile, appId, " (" + participant.getStatus() + ")")){
                    pag_venueDataTree.addTreeNode("pag_application" + appId + "_participant" + profile.getPublicId(),
                        "pag_application" + appId + "_status_participant" + profile.getPublicId(),
                        null, "Status: " + participant.getStatus(),
                         0, null,null, null, null, null, null);
                }
            }
        }
        pag_venueDataTree.drawTree();
        pag_venueDataTree.refreshTree();
    };

    this.enterVenueError=function(faultCode, faultString) {
         alert("Error entering venue!");
    };

    this.negotiateCapabilitiesResponse=function(StreamDescriptionXml) {
         pag_setStatus('Setting streams...');
         if (!this.venueClientController.setStreams(StreamDescriptionXml)) {
             alert("Error entering venue: Could not set streams");
         }
         pag_setStatus('Done');
    };

    this.changeVenueSelectionResponse=function(venueListXml) {
        var venueList = this.venueClientController.getObjectDec(venueListXml);
        if (venueList != null) {
            var roots = venueList.getRoots();
            pag_venueTree =
                new Tree("pag_venueTree", this.treeDivId, 22, "#ccccff", false,
                true, "cursor: pointer;", this.imageLocation, false, true);
            pag_venueTree.addTreeNode(null, "pag_venueRoot",
                imageLocation + "tree_blank.gif", "", 1, null, null, null,
                null);
            pag_addVenuesToList(this.namespace, "pag_venueRoot", roots,
                this.venueClientControllerId);
            pag_venueTree.drawTree();
            pag_venueTree.refreshTree();
        }
    }

    this.expandVenueResponse=function(venueXml) {
         var venue=this.venueClientController.getObjectDec(venueXml);
     	if (venue != null) {
                var venueId = venue.getId();
                var connections=venue.getConnections();
                if ((connections != null) && (connections.size() > 0)) {
                    pag_addVenuesToList(this.namespace,"pag_venue"+venueId, connections, this.venueClientControllerId);
                }
        }
        pag_venueTree.drawTree();
        pag_venueTree.refreshTree();
    }

    this.collapseVenueResponse=function(venueIdXml){
        pag_venueTree.drawTree();
        pag_venueTree.refreshTree();
    }

    this.eventEnterVenue = function(clientXml){
        var client = this.venueClientController.getObjectDec(clientXml);
         pag_add_participant(client);
        pag_venueDataTree.drawTree();
        pag_venueDataTree.refreshTree();
    }

    this.eventExitVenue = function(clientXml){
        var client = this.venueClientController.getObjectDec(clientXml);
        pag_venueDataTree.deleteTreeNode("pag_participant" + client.getPublicId() );
        pag_venueDataTree.drawTree();
        pag_venueDataTree.refreshTree();
    }

    this.eventModifyUser = function(clientXml){
        var client = this.venueClientController.getObjectDec(clientXml);
        var expand=pag_venueDataTree.getTreeNodeExpand("pag_participant" + client.getPublicId());
        pag_venueDataTree.deleteTreeNode("pag_participant" + client.getPublicId());
         pag_add_participant(client,null,null,expand);
        pag_venueDataTree.drawTree();
        pag_venueDataTree.refreshTree();
    }

    this.eventAddData = function(dataXml){
        var data = this.venueClientController.getObjectDec(dataXml);
         pag_add_data(data,this.dataDownloadUrl);
        pag_venueDataTree.drawTree();
        pag_venueDataTree.refreshTree();
    }

    this.eventRemoveData = function(dataXml){
        var data = this.venueClientController.getObjectDec(dataXml);
        pag_venueDataTree.deleteTreeNode("pag_data_"+data.getId() );
        pag_venueDataTree.drawTree();
        pag_venueDataTree.refreshTree();
    }

    this.eventUpdateData = function(dataXml){
        var data = this.venueClientController.getObjectDec(dataXml);
           var expand=pag_venueDataTree.getTreeNodeExpand("pag_data_"+data.getId());
        pag_venueDataTree.deleteTreeNode("pag_data_"+data.getId());
          pag_add_data(data,this.dataDownloadUrl,expand);
        pag_venueDataTree.drawTree();
        pag_venueDataTree.refreshTree();
    }

    this.eventAddDirectory = function(dataXml){
    }

    this.eventRemoveDirectory = function(dataXml){
    }

    this.eventAddService = function(serviceXml){
        var service = this.venueClientController.getObjectDec(serviceXml);
           pag_add_service(service);
        pag_venueDataTree.drawTree();
        pag_venueDataTree.refreshTree();
    }

    this.eventRemoveService = function(serviceXml){
        var service = this.venueClientController.getObjectDec(serviceXml);
        pag_venueDataTree.deleteTreeNode("pag_service" + service.getId());
        pag_venueDataTree.drawTree();
        pag_venueDataTree.refreshTree();
    }

    this.eventUpdateService = function(serviceXml){
        var service = this.venueClientController.getObjectDec(serviceXml);
          var expand=pag_venueDataTree.getTreeNodeExpand("pag_service" + service.getId());
        pag_venueDataTree.deleteTreeNode("pag_service" + service.getId());
        pag_add_service(service,expand);
        pag_venueDataTree.drawTree();
        pag_venueDataTree.refreshTree();
    }

    this.eventAddApplication = function(applicationXml){
        var application = this.venueClientController.getObjectDec(applicationXml);
         pag_add_application(application);
        pag_venueDataTree.drawTree();
        pag_venueDataTree.refreshTree();
    }

    this.joinApplicationResponse=function(applicationUiXml){
        var applicationUi=this.venueClientController.getObjectDec(applicationUiXml);
    }

    this.eventRemoveApplication = function(applicationXml){
        var application = this.venueClientController.getObjectDec(applicationXml);
        pag_venueDataTree.deleteTreeNode("pag_application"+application.getId());
        pag_venueDataTree.drawTree();
        pag_venueDataTree.refreshTree();
    }

    this.eventUpdateApplication = function(applicationXml){
        var application = this.venueClientController.getObjectDec(applicationXml);
         var expand=pag_venueDataTree.getTreeNodeExpand("pag_application"+application.getId());
        pag_venueDataTree.deleteTreeNode("pag_application"+application.getId());
           pag_add_application(application,expand);
        pag_venueDataTree.drawTree();
        pag_venueDataTree.refreshTree();
    }

    this.eventAddConnection = function(connectionXml){
    }

    this.eventRemoveConnection = function(connectionXml){
    }

    this.eventAddStream = function(streamXml){
    }

    this.eventModifyStream = function(streamXml){
    }

    this.eventRemoveStream = function(streamXml){
    }

    this.eventRemoteStartApplication= function(applicationXml,profileXml){
        var application=this.venueClientController.getObjectDec(applicationXml);
        var profile=this.venueClientController.getObjectDec(profileXml);
        var warning="\n\n Your machine might be at risk if you join a shared Application you don't know! \n\n";
        var question="Do you want to start the application?";
        if (confirm("User: " + profile.getName() +" wants you to join" + application.getName() + "."+ warning + question )){
            pag_selectApplication(pag_namespace,application.getId());
        }
    }


    this.eventStartApplication=function(applicationXml,clientXml,appStateXml,venueDataLocationXml){
        var profile=this.venueClientController.getObjectDec(clientXml);
        var application=this.venueClientController.getObjectDec(applicationXml);
        var appId=application.getId();
        this.venueClientController.runApplication(applicationXml,appStateXml,pag_namespace,this.dataDownloadUrl);
            }

    this.ApplicationEvent=function(applicationXml,eventXml){
        var application=this.venueClientController.getObjectDec(applicationXml);
        var event=this.venueClientController.getObjectDec(eventXml);
        this.venueClientController.distributeApplicationEvent(applicationXml,eventXml);
    }

    this.eventJoinApplication=function(applicationXml,participantXml){
        var participant=this.venueClientController.getObjectDec(participantXml);
        var application=this.venueClientController.getObjectDec(applicationXml);
        var appId=application.getId();
        var profile=participant.getClientProfile();
        if (profile!=null){
             if (pag_add_participant(profile, appId, " (" + participant.getStatus() + ")")){
                pag_venueDataTree.addTreeNode("pag_application" + appId + "_participant" + profile.getPublicId(),
                    "pag_application" + appId + "_status_participant" + profile.getPublicId(),
                    null, "Status: " + participant.getStatus(),
                     0, null,null, null, null, null, null);
            }
            pag_venueDataTree.drawTree();
            pag_venueDataTree.refreshTree();
        }
    }

    this.eventLeaveApplication=function(applicationXml,participantXml){
        var participant=this.venueClientController.getObjectDec(participantXml);
        var application=this.venueClientController.getObjectDec(applicationXml);
        var profile=participant.getClientProfile();
        if (profile!=null){
            pag_venueDataTree.deleteTreeNodeRef("pag_application"+application.getId()+"_participant" + profile.getPublicId());
            pag_venueDataTree.drawTree();
            pag_venueDataTree.refreshTree();
        }
    }

    this.eventModifyApplicationUser=function(applicationXml,participantXml){
        var participant=this.venueClientController.getObjectDec(participantXml);
        var application=this.venueClientController.getObjectDec(applicationXml);
        var appId=application.getId();
        var profile=participant.getClientProfile();
        if (profile!=null){
             var expand=pag_venueDataTree.getTreeNodeExpand("pag_application" + appId + "_participant" + profile.getPublicId());
             pag_venueDataTree.deleteTreeNodeRef("pag_application" + appId + "_participant" + profile.getPublicId());
             pag_add_participant(profile,appId, " (" + participant.getStatus() + ")",expand);
             pag_venueDataTree.deleteTreeNode("pag_application" + appId + "_status_participant" + profile.getPublicId());
             pag_venueDataTree.addTreeNode("pag_application" + appId + "_participant" + profile.getPublicId(),
                 "pag_application" + appId + "_status_participant" + profile.getPublicId(),
                 null, "Status: " + participant.getStatus(),
                  0, null,null, null, null, null, null);

            pag_venueDataTree.drawTree();
            pag_venueDataTree.refreshTree();
        }
    }

    this.addMyVenueResponse = function(venueListXml) {
        this.changeVenueSelectionResponse(venueListXml);
    }

    this.changeMyVenueResponse = function(venueListXml) {
        this.changeVenueSelectionResponse(venueListXml);
    }

    this.removeMyVenueResponse = function(venueListXml) {
        this.changeVenueSelectionResponse(venueListXml);
    }

    this.showUploadStatus = function() {
        pag_showDialog(this.uploadDialogId, this.uploadDialogTextId,
                pag_centerX, pag_centerY, 262, 130);
    }

    this.setUploadStatus = function(filenameXml, totalXml, currentXml) {
        var filename = this.venueClientController.getObjectDec(filenameXml);
        var total = this.venueClientController.getObjectDec(totalXml);
        var current = this.venueClientController.getObjectDec(currentXml);
        var percent = (current / total) * 100;
        var modifiers = new Array("B", "kB", "MB", "GB");
        var totalMod = 0;
        var currentMod = 0;
        while ((total >= 1024) && (totalMod < modifiers.length)) {
            total /= 1024;
            totalMod += 1;
        }
        while ((current >= 1024) && (currentMod < modifiers.length)) {
            current /= 1024;
            currentMod += 1;
        }

        this.uploadTotal.innerHTML = Math.round(total * 100) / 100;
        this.uploadTotal.innerHTML += modifiers[totalMod];
        this.uploadCurrent.innerHTML = Math.round(current * 100) / 100;
        this.uploadCurrent.innerHTML += modifiers[currentMod];
        this.uploadPercent.innerHTML = Math.round(percent * 100) / 100;
        this.uploadProgressBar.style.width = Math.round(percent) + "%";
    }

    this.hideUploadStatus = function() {
        pag_hideDialog(this.uploadDialogId);
    }

    this.displayMessage = function(messageXml) {
        var message = this.venueClientController.getObjectDec(messageXml);
        alert(message);
    }

    this.addService = function(nameXml, descriptionXml) {
        this.venueClientController.addService(nameXml, descriptionXml);
    }

    this.getNewBridgesResponse = function(bridgesXml) {
        this.venueClientController.addBridges(bridgesXml);
    }

    this.invalidateBridges = function() {
        this.venueClientController.invalidateBridges();
    }
}
