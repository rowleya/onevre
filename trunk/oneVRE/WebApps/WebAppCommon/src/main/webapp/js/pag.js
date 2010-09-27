/*
 * @(#)pag.js
 * Created: 07-Jun-2007
 * Version: 1.0
 * Copyright (c) 2007, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERSVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Provides functions talking to the PAG portlet
 */

/**
 * Sets and stores a set of preferenes in the PortletPreferences
 *
 * @param setPrefsUrl The AJAX url to set the preferences
 * @param namespace The namespace of the portlet
 * @param storePrefsUrl The portlet url to cause an action request to store
 *                          the preferences
 * @param ... Additional name, value pairs of the preferences to set
 */
function pag_setPreferences(setPrefsUrl, namespace, storePrefsUrl) {

    // Make the request from the arguments
    var args = pag_setPreferences.arguments;
    var postRequest = "namespace=" + encodeURIComponent(namespace);
    for (var i = 3; i < args.length; i += 2) {
        postRequest += "&name=" + encodeURIComponent(args[i]);
        postRequest += "&value=" + encodeURIComponent(args[i + 1]);
    }

    // Create and send an AJAX request to set the preferences
    var request = pag_newRequest();
    request.open("POST", setPrefsUrl, false);
    request.setRequestHeader('Content-Type',
                             'application/x-www-form-urlencoded');
    request.send(postRequest);

    // If there is an error, alert us to it
    if (request.status != 200) {
        alert("Error setting preferences: " + request.status);
    } else {

        // Make an AJAX request to store the preferences permanently
        request.open("POST", storePrefsUrl, false);
        request.send("");
        if (request.status != 200) {
            alert("Error storing preferences: " + request.status);
        }
    }
}

function pag_init(height) {
    var venueClientController = document.getElementById(
        "pag_venueClientController");

    if (!venueClientController.isAudioEnabled()) {
        var button = document.getElementById("pag-audiobutton");
        button.title = "Enable audio";
        document.getElementById(button.id + "_image").src =
            pag_context + "/images/audioDisabled.png";
    }

    if (!venueClientController.isVideoEnabled()) {
        var button = document.getElementById("pag-videobutton");
        button.title = "Enable video";
        document.getElementById(button.id + "_image").src =
            pag_context + "/images/videoDisabled.png";
    }

    if (!venueClientController.isDisplayEnabled()) {
        var button = document.getElementById("pag-displaybutton");
        button.title = "Enable display";
        document.getElementById(button.id + "_image").src =
            pag_context + "/images/displayDisabled.png";
    }

    var enc = venueClientController.getEncryption();
    if ((enc != null) && (enc != "")) {
        var button = document.getElementById("pag-encryptionbutton");
        document.getElementById(button.id + "_image").src =
            pag_context + "/images/lock.png";
    }

    var bridges = venueClientController.loadBridges();
    if (bridges.size() == 0) {
        var registries = venueClientController.getRegistries();
        pag_xmlRpcClient.call("getNewBridges",
            venueClientController.getXMLEnc(registries));
    }

    var error = venueClientController.getError();
    if (error == "") {

        // Show the portlet and hide the error message
        document.getElementById('pag-errormessage').style.height =
            '0px';
        document.getElementById('pag-content').style.height =
            height + 'px';
        document.getElementById('pag-errormessage').style.visibility =
            'hidden';
        document.getElementById('pag-content').style.visibility = 'visible';
    } else {
        document.getElementById('pag-errormessage').innerHTML =
            "<p>Error Starting VenueClient. "
            + "Please try restarting your browser."
            + "If this problem persists, "
            + "please contact your administrator.</p>"
            + "<p>" + error + "</p>";
    }
}

/**
 * Goes to the last visited venue
 */
function pag_goToPreviousVenue() {

    // If there is no previous venue, report an error
    if (pag_previousVenues.length == 0) {
        alert("There are no more venues to go back to!");
    } else {
        pag_previousVenues.pop();
        pag_setStatus('Entering venue...');
        pag_xmlRpcClient.call("enterPreviousVenue");
    }
}

/**
 * Adds venues to the list of venues
 *
 * @param connections The connections received from the response
 * @param venueClientControllerId The id of the venueClientController applet
 * @param expandVenueUrl The AJAX URL for expanding a venue in the list
 * @param collapseVenueUrl The AJAX URL for collapsing a venue in the list
 * @param enterVenueUrl The AJAX URL for entering a venue
 * @param setSelectedVenueUrl The AJAX URL for selecting a venue
 * @param namespace The portlet namespace
 * @param venueNameId The Id of the component containing the venue name
 * @param venueUriId The Id of the component containing the venue uri
 * @param treeDivId The Id of the div containing the venue tree
 * @param imageLocation The location of the images on the web server
 */
function pag_addVenuesToList(namespace, parent, connections, venueClientControllerId) {
     for (var i = 0; i < connections.size(); i++) {
        pag_venueTree.addTreeNode(
            parent,
            "pag_venue" + connections.get(i).getId(), null,
            connections.get(i).getName(),
            connections.get(i).isExpanded(),
            "pag_selectVenue(" + "'" + namespace + "'" + ","
                + "'" + connections.get(i).getId() + "'" + ");",
            "pag_isPreviousVenue = pag_goToVenue(" + "'"
                + connections.get(i).getUri() + "'" + ", "
                + "'" + venueClientControllerId + "'"
                + ");",
            "pag_expandVenueList("+ "'" + namespace + "'" + ","
                + "'" + connections.get(i).getId() + "'"
                + ");",
            "pag_collapseVenueList("+ "'" + namespace + "'" + ","
                + "'" + connections.get(i).getId() + "'"
                + ");",
            false);
        if (connections.get(i).isExpanded()){
            pag_addVenuesToList(namespace, "pag_venue" + connections.get(i).getId(), connections.get(i).getConnections(), venueClientControllerId);
        }
    }
}

function pag_imageButton(id, title, imgUrl, text, action, width, height, left, top, right, bottom ) {
    //set default values;
    width = width || 0;
    height = height || 0;
    left = left || 0;
    top = top || 0;
    rigth = right || -1;
    bottom = bottom || -1;

    // The number of borders that will be changed
    var NO_BORDERS = 2;

    // The width of the border in pixels
    var BORDER_WIDTH = 2;

    var button= '<div onclick="' + action +'"';
    if (id != null) {
        button += ' id="' + id + '"';
    }
    if (title != null) {
        button += ' title="' + title + '"';
    }
    button += ' onmousedown="this.style.borderColor =' + " '#828177 #f9f8f3 #f9f8f3 #828177';" + '"';
    button += ' onmouseup="this.style.borderColor =' + " '#f9f8f3 #828177 #828177 #f9f8f3';" + '"';
    button += ' onmouseout="this.style.borderColor ='+ " '#f9f8f3 #828177 #828177 #f9f8f3';" + '"';
    button += ' style="width: ' + (width - (BORDER_WIDTH * NO_BORDERS)) + 'px;';
    button += ' height: ' + (height - (BORDER_WIDTH * NO_BORDERS))+ 'px;';
//	button += ' position: absolute;'
    if (bottom != -1 ) {
        button += ' bottom: ' + bottom + 'px;';
    } else {
        button += ' top: ' + top + 'px;';
    }
    if (right != -1) {
        button += ' right: ' + right + 'px;';
    } else {
        button += ' left: ' + left + 'px;';
    }
    button += ' border-width: ' + BORDER_WIDTH + 'px;';
    button += ' border-style: solid;';
    button += ' border-color: #f9f8f3 #828177 #828177 #f9f8f3;';
    button += ' background-color: #ece9d8;';
    button += ' text-align: center; overflow: hidden;';
    button += ' ">';
    button += '<table style="border-width: 0px; width: 100%; height: 100%">';
    button += '<tr><td style="text-align: center; width: 100%; vertical-align: middle;">';
    if (imgUrl != null) {
        button += '<img src="'+imgUrl+'" id="' + id + '_image">';
    } else if (text != null) {
        button += '<span style="cursor: default">' + text + '</span>';
    }
    button += '</td></tr></table></div>';
    return button;
}

function pag_selectApplication(namespace, applicationId)
{
     pag_xmlRpcClient.call("joinApplication",applicationId);
//    alert ("join Application: " + applicationId );
};

function pag_selectService(namespace, serviceId)
{
    alert ("execute Service: " + serviceId );;
}
function pag_selectParticipant(namespace, participantId)
{
    alert ("Select Participant: " + participantId);
}

function pag_selectData(namespace, dataId, dataDownloadUrl)
{
    var postRequest = "namespace=" + encodeURIComponent(namespace);
    postRequest += "&file=" + encodeURIComponent(dataId) + "&selection=dataid";

    // Create and send an AJAX request to download a file
    window.open(dataDownloadUrl+'?'+postRequest);
}

function  pag_add_participant (participant, applicationId, info, expand){
      //set default values;
    info = info || "";
    expand=expand || 0;
    var application="";
    var call=null;
    if (applicationId){
        application = "application"+applicationId+"_";
    }

    if ((participant.getPublicId() == pag_localUserId) && application == ""){
        call= "pag_configureUserProfile('pag_clientProfile');";
    }
     if ((participant!=null)&&(participant.getPublicId()!="")){
        if (pag_venueDataTree.addTreeNodeRef("pag_" + application + "participants",
            "pag_" + application + "participant" + participant.getPublicId(),
         pag_context + "/images/" +participant.getProfileIcon(),
            participant.getName() + info,
             expand, null, call, null, null, null, null)){
        pag_venueDataTree.addTreeNode("pag_" + application + "participant" + participant.getPublicId(),
            "pag_" + application + "email_participant" + participant.getPublicId(),
        null, "Email: "+participant.getEmail(),
        0, null, null, null, null, null, null);
        pag_venueDataTree.addTreeNode("pag_" + application + "participant" + participant.getPublicId(),
            "pag_" + application + "phone_participant"+participant.getPublicId(),
        null, "Phone Number: "+participant.getPhoneNumber(),
        0, null, null, null, null, null, null);
        pag_venueDataTree.addTreeNode("pag_" + application + "participant" + participant.getPublicId(),
            "pag_" + application + "location_participant"+participant.getPublicId(),
        null, "Location: "+participant.getLocation(),
        0, null, null, null, null, null, null);
        pag_venueDataTree.addTreeNode("pag_" + application + "participant" + participant.getPublicId(),
            "pag_" + application + "venue_participant"+participant.getPublicId(),
        null, "Home Venue: "+participant.getHomeVenue(),
        0, null,null, null, null, null, null);
        pag_venueDataTree.addTreeNode("pag_" + application + "participant" + participant.getPublicId(),
            "pag_" + application + "type_participant"+participant.getPublicId(),
        null, "Profile Type: "+participant.getProfileType(),
         0, null,null, null, null, null, null);
     } else {
         return false;
    }
    } else {
         return false;
    }
    return true;
}

function pag_delete_data(dataId,dataText) {
    if (confirm("Are you sure you want to delete "+dataText+" ?")){
        pag_xmlRpcClient.call("deleteData",dataId);
    }
}

function pag_shared_presentation(dataId,dataName,all){
    if (all){
        alert("open all");
    }
}

function pag_add_data(data,dataDownloadUrl,expand){
    expand=expand || 0;

    var action_buttons=pag_imageButton("delete_"+data.getId(), "delete "+data.getName() , null,
                "Delete","pag_delete_data('"+data.getId()+"','"+data.getName()+"')",60,22,0,-2)
    var CELL_SEP = "</td><td style=\"white-space: nowrap;\">&nbsp</td><td style=\"white-space: nowrap;\">";

//	var deldata="<a onclick=\"pag_delete_data('"+data.getId()+"','"+data.getName()+"')\">&lt;delete&gt;</a>";

    pag_venueDataTree.addTreeNode("pag_data",
        "pag_data_"+data.getId(), pag_context + "/images/defaultData.png", data.getName(),
        expand, null, "pag_selectData(pag_namespace, '"+data.getId()+"','"+dataDownloadUrl+"');",
        null, null, null, null);

    if  (data.getDescription()!=""){
        pag_venueDataTree.addTreeNode("pag_data_"+data.getId(),
            "pag_description_data"+data.getId(),
            null, data.getDescription(),
            0, null, null, null, null, null, null);
    }
    var data_owner=data.getOwner();
    if (data_owner=="")
        data_owner="None";
    var data_modified=data.getLastModified();
    if (data_modified==""){
        data_modified="Not available";
    }
    pag_venueDataTree.addTreeNode("pag_data_"+data.getId(),
        "pag_owner_data"+data.getId(),
        null, "Owner: "+data_owner,
        0, null, null, null, null, null, null);
    pag_venueDataTree.addTreeNode("pag_data_"+data.getId(),
        "pag_size_data"+data.getId(),
        null, "Size: "+data.getSize(),
        0, null, null, null, null, null, null);
     pag_venueDataTree.addTreeNode("pag_data_"+data.getId(),
        "pag_modified_data"+data.getId(),
        null, "Last Modified: "+data_modified,
        0, null, null, null, null, null, null);
    pag_venueDataTree.addTreeNode("pag_data_"+data.getId(),
        "pag_uri_data"+data.getId(),
        null, "Uri: "+data.getUri(),
        0, null, null, null, null, null, null);
    if (data.isPresentation()){
        action_buttons += CELL_SEP;
        action_buttons += pag_imageButton("openshared_"+data.getId(), "Create Shared Presentation using "+data.getName() , null,
            "Create Shared Presentation","pag_shared_presentation('"+data.getId()+"','"+data.getName()+"')",200,22,0,-2);
        action_buttons += CELL_SEP;
        action_buttons += pag_imageButton("openshared_"+data.getId(), "Create Shared Presentation using "+data.getName() , null,
            "Create Shared Presentation for all","pag_shared_presentation('"+data.getId()+"','"+data.getName()+"', true)",250,22,0,-2);
    }
    pag_venueDataTree.addTreeNode("pag_data_"+data.getId(),
        "pag_action_data_"+data.getId(),null, "",
        0, null, null,
        null, null, null, action_buttons);

}

function  pag_add_application(application,expand){
     expand=expand || 0;
    pag_venueDataTree.addTreeNode("pag_applications",
        "pag_application"+application.getId(), pag_context + "/images/defaultApplication.png",
        '<a href="'+application.getUri()+'" type="'+application.getMimeType()+'">'+application.getName()+'</a>',
        expand, null, "pag_selectApplication(pag_namespace,'"+ application.getId() + "');",
        null, null, null, null);
    pag_venueDataTree.addTreeNode("pag_application"+application.getId(),
        "pag_description_application"+application.getId(),
        null, application.getDescription(),
        0, null, null, null, null, null, null);
    pag_venueDataTree.addTreeNode("pag_application"+application.getId(),
        "pag_location_application"+application.getId(),
        null, 'Location URL: <a href="'+application.getUri()+'" type="'+application.getMimeType()+'">'+application.getUri()+'</a>',
        0, null, null, null, null, null, null);
    pag_venueDataTree.addTreeNode("pag_application"+application.getId(),
        "pag_mimetype_application"+application.getId(),
        null, 'Mime Type: '+application.getMimeType(),
        0, null, null, null, null, null, null);
    pag_venueDataTree.addTreeNode("pag_application"+application.getId(),
        "pag_application"+application.getId()+"_participants",
        null, 'Participants',
        0, null, null, null, null, null, null);
}

function  pag_add_service(service,expand){
     expand=expand || 0;
    pag_venueDataTree.addTreeNode("pag_services",
        "pag_service"+service.getId(), pag_context + "/images/defaultService.png",
        service.getName(),
        expand, null, "pag_selectService(pag_namespace, '"+service.getId()+"');",
        null, null, null, null);
    pag_venueDataTree.addTreeNode("pag_service"+service.getId(),
        "pag_description_service"+service.getId(),
        null, service.getDescription(),
        0, null, null, null, null, null, null);
    pag_venueDataTree.addTreeNode("pag_service"+service.getId(),
        "pag_location_service"+service.getId(),
        null, 'Location URL: '+service.getUri(),
        0, null, null, null, null, null, null);
    pag_venueDataTree.addTreeNode("pag_service"+service.getId(),
        "pag_mimetype_service"+service.getId(),
        null, 'Mime Type: '+service.getMimeType(),
        0, null, null, null, null, null, null);
}


function pag_uploadData(venueClientControllerId, dataUploadUrl){
    var venueClientController = document.getElementById(
        venueClientControllerId);
    venueClientController.uploadFiles(pag_namespace, dataUploadUrl);
}

function pag_uploadService(venueClientControllerId, serviceUploadUrl,
        namespace) {
    var venueClientController = document.getElementById(
        venueClientControllerId);
    venueClientController.uploadFiles(namespace, serviceUploadUrl);
}

function pag_uploadBridge(venueClientControllerId, bridgeUploadUrl,
        namespace) {
    var venueClientController = document.getElementById(
        venueClientControllerId);
    venueClientController.uploadFiles(namespace, bridgeUploadUrl);
}

function pag_createDataTree(participants, data, services, applications,dataDownloadUrl,dataUploadUrl,venueClientControllerId,add)
{
    if (add) {
        var add_data=pag_imageButton("add_data", "Add Data to Venue" , null,
            "Add","pag_uploadData('"+venueClientControllerId+"','"+dataUploadUrl+"')",40,22,0,-1);
    }
    pag_venueDataTree = new Tree("pag_venueDataTree", "pag-venuedata", 22,
            "#ccccff",
            true, true,
            "cursor: pointer;",
            pag_context + "/images/", false, false);
    pag_venueDataTree.addTreeNode(null, "pag_venueDataRoot",
        pag_context + "/images/tree_blank.gif",
        "", 1, null, null, null, null);
    pag_venueDataTree.addTreeNode("pag_venueDataRoot",
        "pag_participants",
        pag_context + "/images/bullet.png",
        "Participants", 1, null, null, null, null);
    for (var i = 0; i < participants.size(); i++) {
        pag_add_participant(participants.get(i));
    }
    pag_venueDataTree.addTreeNode("pag_venueDataRoot",
        "pag_data",
        pag_context + "/images/bullet.png",
        "Data", 0, null, null, null, null, null,
        add_data);
    for (var i = 0; i < data.size(); i++) {
        pag_add_data(data.get(i),dataDownloadUrl);
    }
    pag_venueDataTree.addTreeNode("pag_venueDataRoot",
        "pag_services",
        pag_context + "/images/bullet.png",
        "Services", 0, null, null, null, null);
    for (var i = 0; i < services.size(); i++) {
         pag_add_service(services.get(i));
    }
    pag_venueDataTree.addTreeNode("pag_venueDataRoot",
        "pag_applications",
        pag_context + "/images/bullet.png",
        "Application Sessions", 0, null, null, null, null);
    for (var i = 0; i < applications.size(); i++) {
        pag_add_application(applications.get(i));
    }
    pag_venueDataTree.drawTree();
    pag_venueDataTree.refreshTree();
}

function pag_negotiateCapabilities(negotiateCapabilitiesUrl, capabilities,
        venueClientController) {
    var request = pag_newRequest();
    request.open("POST", negotiateCapabilitiesUrl, false);
    request.setRequestHeader('Content-Type',
                                          'application/x-www-form-urlencoded');
    request.send("namespace=" + encodeURIComponent(pag_namespace) + "&caps="
        + encodeURIComponent(capabilities));
    if (request.readyState == 4) {
        if (request.status == 200) {
            venueClientController.setStreams(request.responseText);
        }
    }
}



/**
 * Enters a venue
 *
 * @param venueUrl The venue url to enter
 * @param venueClientController The id of the venueClientController applet
 */
function pag_goToVenue(venueUrl, venueClientControllerId) {
    var venueClientController = document.getElementById(venueClientControllerId);
    if (venueClientController)
    if (!venueClientController.isValidUrl(venueUrl)) {
        alert("Please enter a valid venue URL!");
    } else {
        if (pag_isInVenue) {
            if (pag_currentVenueUri != "") {
                pag_previousVenues.push(pag_currentVenueUri);
            }
        }
        pag_isInVenue = true;
        pag_setStatus('Entering venue...');
        pag_xmlRpcClient.call("enterVenue", venueUrl);
    }
}

/**
 * Expands a venue on the list
 *
 * @param namespace The portlet namespace
 * @param venueId The Id of the venue to expand
 */
function pag_expandVenueList(namespace, venueId){
    pag_xmlRpcClient.call("expandVenue",venueId);
}

/**
 * Collapses a venue on the list
 *
 * @param namespace The portlet namespace
 * @param venueId The id of the venue to collapse
 */
function pag_collapseVenueList(namespace, venueId){
    pag_xmlRpcClient.call("collapseVenue",venueId);
}

/**
 * Toggles the audio
 */
function pag_toggleAudio(button) {
    if (button.title == "Disable audio") {
        button.title = "Enable audio";
        document.getElementById(button.id + "_image").src =
            pag_context + "/images/audioDisabled.png";
        document.pag_venueClientController.enableAudio(0);
    } else {
        button.title = "Disable audio";
        document.getElementById(button.id + "_image").src =
            pag_context + "/images/audio.png";
        document.pag_venueClientController.enableAudio(1);
    }
}

/**
 * Toggles the display
 */
function pag_toggleDisplay(button) {
    if (button.title == "Disable display") {
        button.title = "Enable display";
        document.getElementById(button.id + "_image").src =
            pag_context + "/images/displayDisabled.png";
        document.pag_venueClientController.enableDisplay(0);
    } else {
        button.title = "Disable display";
        document.getElementById(button.id + "_image").src =
            pag_context + "/images/display.png";
        document.pag_venueClientController.enableDisplay(1);
    }
}

/**
 * Toggles the sending of video
 */
function pag_toggleVideo(button) {
    if (button.title == "Disable video") {
        button.title = "Enable video";
        document.getElementById(button.id + "_image").src =
            pag_context + "/images/cameraDisabled.png";
        document.pag_venueClientController.enableVideo(0);
    } else {
        button.title = "Disable video";
        document.getElementById(button.id + "_image").src =
            pag_context + "/images/camera.png";
        document.pag_venueClientController.enableVideo(1);
    }
}

/**
 * Brings up the node services configuration dialog
 */
function pag_configureNodeServices() {
    document.pag_venueClientController.configureNodeServices();
}

/**
 * Changes the mode of the venue list
 *
 * @param mode The mode to change to
 */
function pag_changeVenueSelection(mode) {
    pag_xmlRpcClient.call("changeVenueSelection", mode);
}

/**
 * Sends jabber text
 */
function pag_sendText(textId) {
    pag_jabbertext=document.getElementById(textId).value;
    if (document.getElementById(textId).value != "") {
        pag_xmlRpcClient.call("sendJabberMessage",
            document.getElementById(textId).value);
        document.getElementById(textId).value = "";
    }
}

/**
 * Sets the current client profile
 *
 * @param nameId The id of the client name text box
 * @param emailId The id of the client email text box
 * @param phoneId The id of the client phone text box
 * @param locationId The id of the client location text box
 * @param homeId The id of the client home venue text box
 * @param typeId The id of the client profile type text box
 */
function pag_setProfile(currentVenueId, isInVenue, nameId, emailId, phoneId,
        locationId, homeId, typeId) {
    if (document.getElementById(nameId).value == "") {
        alert("You must enter a name!");
    } else if (document.getElementById(emailId).value == "") {
        alert("You must enter an e-mail address!");
    } else {
        pag_xmlRpcClient.call("setClientProfile",
                document.getElementById(nameId).value,
                document.getElementById(emailId).value,
                document.getElementById(phoneId).value,
                document.getElementById(locationId).value,
                document.getElementById(homeId).value,
                document.getElementById(typeId).value
            );
        if (!isInVenue) {
            var currentVenue = document.getElementById(currentVenueId);
            currentVenue.value = document.getElementById(homeId).value;
        }
    }
}

/**
 * Allows the user to configure their profile
 * @param profileFormId The id of the user profile form div
 */
function pag_configureUserProfile(profileFormId) {
    document.getElementById(profileFormId).style.visibility = "visible";
}

/**
 * Selects a venue
 * @param setSelectedVenueUrl The AJAX URL for storing the selected venue
 * @param namespace The portlet namespace
 * @param venueId The id of the venue selected
 */
function pag_selectVenue(setSelectedVenueUrl, namespace, venueId) {
    var request = pag_newRequest();
    request.open("POST", setSelectedVenueUrl, false);
    request.setRequestHeader('Content-Type',
                             'application/x-www-form-urlencoded');
    request.send("namespace=" + encodeURIComponent(namespace)
        + "&venueId=" + venueId);
}

function pag_showDialog(dialogId, dialogTextId, centerX, centerY, width,
        height) {
    var dialog = document.getElementById(dialogId);
    var dialogText = document.getElementById(dialogTextId);
    dialog.style.visibility = 'visible';
    dialogText.style.left = centerX - (width / 2) + "px";
    dialogText.style.top = centerY - (height / 2) + "px";
    dialogText.style.width = width + "px";
    dialogText.style.height = height + "px";
}

function pag_hideDialog(dialogId) {
    document.getElementById(dialogId).style.visibility = 'hidden';
}

function pag_configureNetwork(dialogId, dialogTextId, venueClientControllerId,
        bridgeSelectBoxId, registrySelectBoxId, automaticId, multicastId,
        unicastId, centerX, centerY) {
    var venueClientController = document.getElementById(
        venueClientControllerId);
    pag_loadBridges(venueClientControllerId, bridgeSelectBoxId);
    pag_loadRegistries(venueClientControllerId, registrySelectBoxId);
    if (venueClientController.isAutomaticBridging()) {
        document.getElementById(automaticId).checked = true;
    } else if (venueClientController.getCurrentBridgeId() == "") {
        document.getElementById(multicastId).checked = true;
    } else {
        document.getElementById(unicastId).checked = true;
    }
    pag_showDialog(dialogId, dialogTextId, centerX, centerY, 325, 330);
}

function pag_finishConfigureNetwork(dialogId, venueClientControllerId,
        bridgeSelectBoxId, registrySelectBoxId, automaticId, multicastId,
        unicastId, pointOfReferenceId, xmlrpcClient, setPrefsUrl, namespace,
        storePrefsUrl) {
    pag_hideDialog(dialogId);
    var venueClientController = document.getElementById(
        venueClientControllerId);
    var currentBridgeId = venueClientController.getCurrentBridgeId();
    var currentlyAutomatic = venueClientController.isAutomaticBridging();
    var multicast = document.getElementById(multicastId).checked;
    var unicast = document.getElementById(unicastId).checked;
    var automatic = document.getElementById(automaticId).checked;
    var selectedBridge = document.getElementById(bridgeSelectBoxId).value;
    if (!automatic) {
        if (multicast && ((currentBridgeId != "") || currentlyAutomatic)) {
            if (!venueClientController.joinBridge("")) {
                alert("There was an error switching to multicast");
                venueClientController.runAutomaticBridging();
            }
        } else if (unicast && ((selectedBridge != currentBridgeId)
                || currentlyAutomatic)) {
            if (!venueClientController.joinBridge(selectedBridge)) {
                alert("There was an error joining the bridge");
                venueClientController.runAutomaticBridging();
            }
        }
    } else if (!currentlyAutomatic && automatic) {
        venueClientController.runAutomaticBridging();
    }
    var registryBox = document.getElementById(registrySelectBoxId);
    var registries = "";
    for (var i = 0; i < registryBox.length; i++) {
        registries += registryBox.options[i].value;
        if ((i + 1) < registryBox.length) {
            registries += ";";
        }
    }
    venueClientController.setRegistries(registries);
    var pointOfRef = document.getElementById(pointOfReferenceId).value;
    venueClientController.setPointOfReference(pointOfRef);
    pag_setPreferences(setPrefsUrl, namespace, storePrefsUrl,
        'pointOfReference', pointOfRef);
    return currentBridgeId;
}

function pag_loadBridges(venueClientControllerId, selectBoxId) {
    var venueClientController = document.getElementById(
        venueClientControllerId);
    var selectBox = document.getElementById(selectBoxId);
    var bridges = venueClientController.loadBridges();
    var currentBridgeId = venueClientController.getCurrentBridgeId();
    for (var i = 0; i < bridges.size(); i++) {
        var bridge = bridges.get(i);
        selectBox.options[i] = new Option(bridge.getName(),
            bridge.getGuid());
        if (currentBridgeId == bridge.getGuid()) {
            selectBox.selectedIndex = i;
            selectBox.options[i].defaultSelected = true;
        }
    }
    return bridges;
}

function pag_loadRegistries(venueClientControllerId, selectBoxId) {
    var venueClientController = document.getElementById(
        venueClientControllerId);
    var selectBox = document.getElementById(selectBoxId);
    var registries = venueClientController.getRegistries();
    for (var i = 0; i < registries.size(); i++) {
        var registry = registries.get(i);
        selectBox.options[i] = new Option(registry, registry);
    }
    return registries;
}

function pag_selectRegistry(selectBoxId, editBoxId) {
    var selectBox = document.getElementById(selectBoxId);
    document.getElementById(editBoxId).value =
        selectBox.options[selectBox.selectedIndex].value;
}

function pag_addRegistry(venueClientControllerId, selectBoxId, editBoxId) {
    var venueClientController = document.getElementById(
        venueClientControllerId);
    var selectBox = document.getElementById(selectBoxId);
    var registry = document.getElementById(editBoxId).value;
    if (venueClientController.isValidUrl(registry)) {
        try {
            selectBox.options.add(new Option(registry, registry), null);
        } catch (ex) {
            selectBox.options.add(new Option(registry, registry));
        }
        document.getElementById(editBoxId).value = "";
    } else {
        alert("The URL you have typed is invalid");
    }
}

function pag_changeRegistry(venueClientControllerId, selectBoxId, editBoxId) {
    var venueClientController = document.getElementById(
        venueClientControllerId);
    var selectBox = document.getElementById(selectBoxId);
    if (selectBox.selectedIndex < 0) {
        alert("You must select a registry to change");
    } else {
        var registry = document.getElementById(editBoxId).value;
        if (venueClientController.isValidUrl(registry)) {
            selectBox.options[selectBox.selectedIndex] =
                new Option(registry, registry);
            document.getElementById(editBoxId).value = "";
        } else {
            alert("The URL you have typed is invalid");
        }
    }
}

function pag_removeRegistry(selectBoxId, editBoxId) {
    var selectBox = document.getElementById(selectBoxId);
    if (selectBox.selectedIndex < 0) {
        alert("You must select a registry to remove");
    } else {
        selectBox.remove(selectBox.selectedIndex);
        document.getElementById(editBoxId).value = "";
    }
}

function pag_getNewBridges(venueClientControllerId, selectBoxId,
        getBridgesUrl, namespace) {
    var venueClientController = document.getElementById(
        venueClientControllerId);
    var registries = venueClientController.getRegistries();
    var registryArgs = "";
    for (var i = 0; i < registries.size(); i++) {
        var registry = registries.get(i);
        registryArgs += "&registry=" + encodeURIComponent(registry);
    }
    var request = pag_newRequest();
    request.open("POST", getBridgesUrl, false);
    request.setRequestHeader('Content-Type',
                             'application/x-www-form-urlencoded');
    request.send("namespace=" + encodeURIComponent(namespace)
        + registryArgs);
    if (request.status != 200) {
        alert("Error getting new bridges: " + request.responseText);
    } else {
        venueClientController.addBridges(request.responseText);
        pag_loadBridges(venueClientControllerId, selectBoxId);
    }
}

function pag_removeDeadBridges(venueClientControllerId, selectBoxId,
        getBridgesUrl, namespace) {
    var venueClientController = document.getElementById(
        venueClientControllerId);
    venueClientController.clearBridges();
    pag_getNewBridges(venueClientControllerId, selectBoxId, getBridgesUrl,
        namespace);
}

function pag_displayEncryptionDialog(venueClientControllerId, dialogId,
        dialogTextId, encryptionId, centerX, centerY) {
     var venueClientController = document.getElementById(
        venueClientControllerId);
     var encryption = document.getElementById(encryptionId);
     encryption.value = venueClientController.getEncryption();
     pag_showDialog(dialogId, dialogTextId, centerX, centerY, 295, 60);
}

function pag_setEncryption(venueClientControllerId, dialogId, encryptionId,
        buttonImageId, cancel) {
    var venueClientController = document.getElementById(
        venueClientControllerId);
    var encryption = document.getElementById(encryptionId);
    if (!cancel) {
        if (venueClientController.setEncryption(encryption.value)
                != encryption.value) {
            alert("There was an error setting the encryption");
        } else {
            if (encryption.value != "") {
                document.getElementById(buttonImageId).src =
                        pag_context + "/images/lock.png";
            } else {
                document.getElementById(buttonImageId).src =
                        pag_context + "/images/unlock.png";
            }
        }
    }
    encryption.value = '';
    pag_hideDialog(dialogId);
}

function pag_showAddMyVenueDialog(dialogId, dialogTextId, nameId, urlId,
        venueNameId, venueUrlId, centerX, centerY) {
    var venueName = document.getElementById(venueNameId).innerHTML;
    if (venueName != "You are not in a venue") {
        document.getElementById(nameId).value = venueName;
        document.getElementById(urlId).value =
            document.getElementById(venueUrlId).value;
    } else {
        document.getElementById(nameId).value = "";
        document.getElementById(urlId).value = "";
    }
    pag_showDialog(dialogId, dialogTextId, centerX, centerY, 295, 100);
}

function pag_finishAddMyVenueDialog(venueClientControllerId, dialogId, nameId,
        urlId, myvenuesId, cancel) {
    if (!cancel) {
        var venueClientController = document.getElementById(
            venueClientControllerId);
        var name = document.getElementById(nameId).value;
        var url = document.getElementById(urlId).value;
        var myvenues = document.getElementById(myvenuesId);
        if (name == "") {
            alert("The name cannot be blank");
        } else if (!venueClientController.isValidUrl(url)) {
            alert("The URL is not valid");
        } else if (pag_checkForMyVenueUrl(myvenues, url)) {
            alert("A venue with that URL has already been added!");
        } else {
            pag_xmlRpcClient.call("addMyVenue", url, name);
            try {
                myvenues.options.add(new Option(name, url), null);
            } catch (ex) {
                myvenues.options.add(new Option(name, url));
            }
            pag_hideDialog(dialogId);
        }
    } else {
        pag_hideDialog(dialogId);
    }
}

function pag_selectMyVenue(selectBoxId, nameId, urlId) {
    var selectBox = document.getElementById(selectBoxId);
    document.getElementById(urlId).value =
        selectBox.options[selectBox.selectedIndex].value;
    document.getElementById(nameId).value =
        selectBox.options[selectBox.selectedIndex].innerHTML;
}

function pag_checkForMyVenueUrl(selectBox, url) {
    for (var i = 0; i < selectBox.length; i++) {
        if (selectBox.options[i].value == url) {
            return true;
        }
    }
    return false;
}

function pag_changeMyVenue(venueClientControllerId, selectBoxId, nameId,
        urlId) {
    var venueClientController = document.getElementById(
        venueClientControllerId);
    var selectBox = document.getElementById(selectBoxId);
    if (selectBox.selectedIndex < 0) {
        alert("You must select a venue to change");
    } else {
        var url = document.getElementById(urlId).value;
        var name = document.getElementById(nameId).value;
        var oldurl = selectBox.options[selectBox.selectedIndex].value;
        if (!venueClientController.isValidUrl(url)) {
            alert("The URL you have typed is invalid");
        } else if (name == "") {
            alert("The name cannot be blank");
        } else if ((oldurl != url) && pag_checkForMyVenueUrl(selectBox, url)) {
            alert("A venue with that URL already exists");
        } else {
            pag_xmlRpcClient.call("changeMyVenue", oldurl, url, name);
            selectBox.options[selectBox.selectedIndex] =
                new Option(name, url);
            document.getElementById(urlId).value = "";
            document.getElementById(nameId).value = "";
        }
    }
}

function pag_removeMyVenue(selectBoxId, nameId, urlId) {
    var selectBox = document.getElementById(selectBoxId);
    if (selectBox.selectedIndex < 0) {
        alert("You must select a venue to remove");
    } else if (confirm("Are you sure that you want to remove that venue?")) {
        var url = selectBox.options[selectBox.selectedIndex].value;
        pag_xmlRpcClient.call("removeMyVenue", url);
        selectBox.remove(selectBox.selectedIndex);
        document.getElementById(urlId).value = "";
        document.getElementById(nameId).value = "";
    }
}

function pag_exitVenue() {
    if (pag_isInVenue) {
        if (pag_currentVenueUri != "") {
            pag_previousVenues.push(pag_currentVenueUri);
        }
    }
    pag_isInVenue = false;
    pag_xmlRpcClient.call("exitVenue");
}

function pag_setStatus(text) {
    if (text != "Done") {
        document.getElementById('pag_status').innerHTML =
            "&nbsp;-&nbsp;" + text;
        pag_xmlRpcClient.call("setStatus", "&amp;nbsp;-&amp;nbsp;" + text);
    } else {
        document.getElementById('pag_status').innerHTML = "";
        pag_xmlRpcClient.call("setStatus", "");
    }
}

function pag_help() {
    var win = window.open(pag_context + '/help/index.jsp?context='
            + encodeURIComponent(pag_context),
        'pag_helpWindow', 'WIDTH=800,HEIGHT=600,resizable=yes');
    win.focus();
}

function pag_negotiateCapabilities(capabilities) {
    pag_setStatus('Negotiating capabilities...');
    if (capabilities != null) {
        pag_xmlRpcClient.call("negotiateCapabilities", capabilities);
    } else {
        pag_setStatus("Done");
    }
}
