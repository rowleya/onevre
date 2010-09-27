<%--
  - Author: Andrew G D Rowley
  - Date: 07 June 2007
  -
  - Copyright Notice:
  - Copyright (c) 2005-2006, University of Manchester All rights reserved.
  - Redistribution and use in source and binary forms, with or without
  - modification, are permitted provided that the following conditions are met:
  -
  - Redistributions of source code must retain the above copyright notice, this
  - list of conditions and the following disclaimer. Redistributions in binary
  - form must reproduce the above copyright notice, this list of conditions and
  - the following disclaimer in the documentation and/or other materials
  - provided with the distribution. Neither the name of the University of
  - Manchester nor the names of its contributors may be used to endorse or
  - promote products derived from this software without specific prior written
  - permission.
  -
  - THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  - AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  - IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  - ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
  - LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  - CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
  - SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
  - INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  - CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
  - ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
  - POSSIBILITY OF SUCH DAMAGE.
  -
  - Description: The main porlet view
  --%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/pag-portlet.tld" prefix="pag" %>

<%-- Load some useful variables --%>
<pag:pag_namespace var="pag_namespace"/>
<pag:pag_sessionid var="pag_sessionid"/>
<pag:pag_storePreferencesUrl var="pag_storePrefsUrl"/>
<pag:pag_getVenueClientUI var="pag_venueClientUI"/>
<pag:pag_getClientProfile var="pag_clientProfile"/>
<pag:pag_getServices var="pag_services"/>
<pag:pag_getBridges var="pag_bridgeConnectors"/>
<pag:pag_getApplications var="pag_applications"/>
<pag:pag_getpreference var="pag_logFile" name="pagLogFile" def="logs/pag-usage.log" />
<pag:pag_getpreference var="pag_pointOfReference" name="pointOfReference" def="" />
<pag:pag_getpreference var="pag_defaultPointOfReference" name="pointOfReference" def="" loadDefault="true" />
<!-- ${pag_currentStreams} -->

<%-- Load the VenueClientController applet --%>
<applet width="0px" height="0px" code="com.googlecode.onevre.web.venueclientcontroller.VenueClientController"
        mayscript="true" id="pag_venueClientController"
        codebase="${pageContext.request.contextPath}/applets"
        archive="VenueClientController.jar, AccessGridTypes.jar, AccessGridClient.jar, civil-no-swt-1.0.jar,
				CommonProtocolsXMLRPC.jar, commons-lang-2.3.jar, commons-logging-1.1.jar,
				CommonTypesSOAP.jar, CommonUtils.jar, CommonWeb.jar,
				jmf-cross-platform-2.1.1f.jar, junit-3.8.1.jar, log4j-1.2.12.jar,
				PlatformUtils.jar, registry-1.0.jar, Security.jar,
				ServerProtocolsXMLRPC.jar, ws-commons-util-1.0.2.jar,
				xercesImpl-2.4.0.jar, xml-apis-1.0.b2.jar, xmlrpc-client-3.1.3.jar,
				xmlrpc-common-3.1.3.jar, xmlrpc-server-3.1.3.jar">
    <param name="namespace" value="${pag_namespace}"/>
    <param name="sessionid" value="${pag_sessionid}"/>
    <param name="pointOfReference" value="${pag_pointOfReference}"/>
    <param name="defaultPointOfReference" value="${pag_defaultPointOfReference}"/>
    <param name="clientProfile" value="<c:out value="${pag_clientProfile}" escapeXml="true"/>"/>
    <param name="services" value="<c:out value="${pag_services}" escapeXml="true"/>"/>
    <param name="bridgeConnectors" value="<c:out value="${pag_bridgeConnectors}" escapeXml="true"/>"/>
    <param name="applications" value="<c:out value="${pag_applications}" escapeXml="true"/>"/>
</applet>
<applet width="0px" height="0px" code="com.googlecode.onevre.web.version.JavaVersion"
    mayscript="true" id="pag_version"
    codebase="${pageContext.request.contextPath}/applets"
    archive="Version.jar">
</applet>

<%-- Load the preferences --%>
<pag:pag_getpreference name="heightPixels" var="pag_heightPixels"
    def="700"/>
<pag:pag_getpreference name="venueListWidthPercent"
    var="pag_venueListWidthPercent" def="30"/>
<pag:pag_getpreference name="textchatHeightPixels"
    var="pag_textchatHeightPixels" def="160"/>
<pag:pag_getpreference name="name" var="pag_name" def=""/>
<pag:pag_getpreference name="email" var="pag_email" def=""/>
<pag:pag_getpreference name="phoneNumber" var="pag_phoneNumber" def=""/>
<pag:pag_getpreference name="location" var="pag_location" def=""/>
<pag:pag_getpreference name="homeVenue" var="pag_homeVenue"
    def="https://sam.ag.manchester.ac.uk/Venues/default"/>
<pag:pag_getpreference name="profileType" var="pag_profileType" def="user"/>
<pag:pag_getVenueClientUI var="pag_venueClientUI"/>

<%-- Calculate the amount of pixels that can be moved by the user --%>
<c:set var="pag_movablePixels" value="${pag_heightPixels - 170}"/>

<%-- Load all the JavaScript--%>
<script language="JavaScript"
    src="${pageContext.request.contextPath}/js/utils.js"></script>
<script language="JavaScript"
    src="${pageContext.request.contextPath}/js/frames.js"></script>
<script language="JavaScript"
    src="${pageContext.request.contextPath}/js/pag.js"></script>
<script language="JavaScript"
    src="${pageContext.request.contextPath}/js/tree.js"></script>
<script language="JavaScript"
    src="${pageContext.request.contextPath}/js/axmlrpc.js"></script>
<script language="JavaScript"
    src="${pageContext.request.contextPath}/js/xmlrpcHandler.js"></script>

<%-- Write the appropriate JSP variables to javascript ones --%>
<script language="JavaScript">
    var pag_context = "${pageContext.request.contextPath}";
    var pag_venueListWidthPercent = ${pag_venueListWidthPercent};
    var pag_namespace = "${pag_namespace}";
    var pag_storePrefsUrl = "${pag_storePrefsUrl}";
    var pag_setPrefsUrl = pag_context + "/jsp/setPreference.jsp";
    var pag_dataDownloadUrl = pag_context + "/jsp/Download.jsp";
    var pag_dataUploadUrl = pag_context + "/jsp/Upload.jsp";
    var pag_serviceUploadUrl = pag_context + "/jsp/uploadService.jsp";
    var pag_bridgeUploadUrl = pag_context + "/jsp/uploadBridge.jsp";
    var pag_getAvailableBridgesUrl = pag_context + "/jsp/getAvailableBridges.jsp";
    var pag_networkMenuVisible = false;
    var pag_localUserId = "${pag_venueClientUI.clientProfile.publicId}";
    var pag_pointOfReference = "${pag_pointOfReference}";
    var pag_registries = new Array();
    var pag_previousVenues = new Array(${pag_venueClientUI.previousVenues});
    var pag_currentVenueUri = "${pag_venueClientUI.currentVenueUri}";
    var pag_isInVenue = ${pag_venueClientUI.inVenue};

    var date = new Date();
    date.setTime(date.getTime() + (365 * 24 * 60 * 60 * 1000));
    var expires = "; expires="+date.toGMTString();
    document.cookie = "JSESSIONID=${pag_sessionid}" + expires + "; path=/";
</script>

<%-- The main div of the portlet - everything visual is within this --%>
<div style="position: relative;
             margin: 0px 0px 0px 0px;
             padding: 0px 0px 0px 0px;
             width: 100%;
             height: 0px;
             overflow: hidden;
             visibility: hidden;"
        id="pag-content">

    <%-- The button bar at the top --%>
    <div style="position: absolute;
                top: 0px;
                left: 0px;
                width: 100%;
                height: 30px;
                border-style: solid;
                border-color: black;
                border-bottom-width: 1px;
                border-top-width: 0px;
                border-left-width: 0px;
                border-right-width: 0px;
                overflow: hidden;"
            id="pag-buttons">
        <pag:pag_imagebutton width="26" height="26" top="2" left="2"
            id="pag-audiobutton"
            title="Disable audio"
            url="/images/audio.png"
            action="pag_toggleAudio(this);"/>
        <pag:pag_imagebutton width="26" height="26" top="2" left="28"
            id="pag-displaybutton"
            title="Disable display"
            url="/images/display.png"
            action="pag_toggleDisplay(this);"/>
        <pag:pag_imagebutton width="26" height="26"  top="2" left="54"
            id="pag-videobutton"
            title="Disable video"
            url="/images/camera.png"
            action="pag_toggleVideo(this);"/>
        <pag:pag_imagebutton width="26" height="26"  top="2" left="80"
            id="pag-configurenodeservicesbutton"
            title="Configure node services"
            url="/images/configure.png"
            action="pag_configureNodeServices();"/>
        <pag:pag_imagebutton width="26" height="26"  top="2" left="108"
            id="pag-configureprofilebutton"
            title="Configure user profile"
            url="/images/userProfile.png"
            action="pag_configureUserProfile('pag_clientProfile');"/>
        <pag:pag_imagebutton width="26" height="26"  top="2" left="140"
            id="pag-configurenetworkbutton"
            title="Multicast / Unicast Configuration"
            url="/images/network.png"
            action="pag_configureNetwork('pag_networkMenuDialog',
                'pag_networkMenu', 'pag_venueClientController', 'pag_bridges',
                'pag_registries', 'pag_automatic', 'pag_multicast',
                'pag_unicast', pag_centerX, pag_centerY);"/>
        <pag:pag_imagebutton width="26" height="26"  top="2" left="168"
            id="pag-encryptionbutton"
            title="Configure Encryption"
            url="/images/unlock.png"
            action="pag_displayEncryptionDialog('pag_venueClientController',
                'pag_encryptionDialog', 'pag_encryptionDialogText',
                'pag-encryption', pag_centerX, pag_centerY);"/>
        <pag:pag_imagebutton width="26" height="26"  top="2" left="198"
            id="pag-editmyvenuesbutton"
            title="Edit My Venues"
            url="/images/myvenues.png"
            action="pag_showDialog('pag_myVenuesEditDialog',
                'pag_myVenuesEditDialogText', pag_centerX, pag_centerY,
                320, 160);"/>
        <pag:pag_imagebutton width="26" height="26"  top="2" right="100"
            id="pag-uploadservice"
            title="Upload New Service"
            url="/images/uploadserv.png"
            action="pag_uploadService('pag_venueClientController',
                        pag_serviceUploadUrl, pag_namespace);"/>
        <pag:pag_imagebutton width="26" height="26"  top="2" right="72"
            id="pag-uploadservice"
            title="Upload New Application"
            url="/images/uploadapp.png"
            action="alert('This has not yet been implemented');"/>
        <pag:pag_imagebutton width="26" height="26"  top="2" right="44"
            id="pag-uploadservice"
            title="Upload New Bridge"
            url="/images/uploadbr.png"
            action="pag_uploadService('pag_venueClientController',
                        pag_bridgeUploadUrl, pag_namespace);"/>
        <pag:pag_imagebutton width="26" height="26"  top="2" right="0"
            id="pag-helpbutton"
            title="Help"
            url="/images/help.png"
            action="pag_help();"/>
    </div>

    <%-- The naviagation bar (e.g. venue URL, back button and go button) --%>
    <div style="position: absolute;
                top: 30px;
                left: 0px;
                width: 100%;
                height: 30px;
                border-style: solid;
                border-color: black;
                border-bottom-width: 1px;
                border-top-width: 0px;
                border-left-width: 0px;
                border-right-width: 0px;
                overflow: hidden;"
            id="pag-navigator">
        <table cellpadding="0px" cellspacing="2px"
            style="position: absolute;
                   top: 0px;
                   left: 0px;
                   width: 100%;
                   height: 30px;
                   border-width: 0px;">
            <tr>
            <td style="width: 55px">
                <pag:pag_imagebutton width="40" height="26" top="2"
                    left="2"
                  id="pag-previousvenue"
                  title="Go to previous venue"
                  url="/images/previous.png"
                  action="pag_goToPreviousVenue();"/>
          </td>
          <td>
              <input type="text" id="pag-currentvenue"
                  value="${pag_venueClientUI.currentVenueUri}"
                  style="margin-top: 1px;
                         width: 97%;"/>
          </td>
          <td style="width: 45px;">
              <pag:pag_imagebutton width="40" height="26" top="2"
                  right="44"
                  id="pag-go"
                  title="Enter Venue"
                  text="Go"
                  action="pag_goToVenue(document.getElementById(
                          'pag-currentvenue').value,
                          'pag_venueClientController');"/>
          </td>
          <td style="width: 45px;">
              <pag:pag_imagebutton width="40" height="26" top="2"
                  right="2"
                  id="pag-exit"
                  title="Exit Venue"
                  text="Exit"
                  action="pag_exitVenue();"/>
          </td>
        </tr>
    </table>
    </div>

    <%-- The current venue title bar --%>
    <div style="position: absolute;
                top: 60px;
                left: 0px;
                width: 100%;
                height: 30px;
                border-style: solid;
                border-color: black;
                border-bottom-width: 1px;
                border-top-width: 0px;
                border-left-width: 0px;
                border-right-width: 0px;
                overflow: hidden;">
        <p style="font-size: 24px;
                  font-weight: bold;
                  text-align: center;
                  margin: 2px 0px 0px 0px;">
            <span id="pag-venuetitle">
            <c:choose>
              <c:when test="${pag_venueClientUI.inVenue}">
                  ${pag_venueClientUI.currentVenueName}
              </c:when>
              <c:otherwise>
                  You are not in a venue
              </c:otherwise>
            </c:choose>
            </span>
            <span id="pag_status">
                ${pag_venueClientUI.status}
            </span>
        </p>
    </div>

    <%-- Venue List Mode selection --%>
    <select id="pag-venueselection"
            onchange="pag_changeVenueSelection(this.value);"
            style="position: absolute;
                  top: 92px;
                  left: 0px;
                  width: ${pag_venueListWidthPercent}%;
                  height: 25px;">
        <pag:pag_modeList indexVar="pag_index" nameVar="pag_mode"
                isSelectedVar="pag_isSelected">
            <option value="${pag_index}" ${pag_isSelected? "selected": "" }>
                ${pag_mode}
            </option>
        </pag:pag_modeList>
    </select>

    <%-- The Venue Listing Options --%>
    <div style="position: absolute;
                top: 117px;
                left: 0px;
                width: ${pag_venueListWidthPercent}%;
                height: 25px;"
            id="pag-venuesbuttons">
        <a href="javascript:pag_showAddMyVenueDialog('pag_addMyVenueDialog',
            'pag_addMyVenueDialogText', 'pag_venueNameBox', 'pag_venueUrlBox',
            'pag-venuetitle', 'pag-currentvenue', pag_centerX,
            pag_centerY);">Add to My Venues...</a>
    </div>

    <%-- The Venue Listing --%>
    <div style="position: absolute;
                top: 142px;
                left: 0px;
                margin: 0px 0px 0px 0px;
                padding: 0px 0px 0px 0px;
                width: ${pag_venueListWidthPercent}%;
                height: ${pag_movablePixels - 27 - 25
                    - pag_textchatHeightPixels}px;
                border-style: solid;
                border-color: black;
                border-bottom-width: 1px;
                border-top-width: 0px;
                border-left-width: 0px;
                border-right-width: 0px;
                overflow: auto;"
            id="pag-venues">
    </div>

    <%-- The venue data (e.g. participants etc.) --%>
    <div style="position: absolute;
                top: 90px;
                left: ${pag_venueListWidthPercent}%;
                margin: 0px 0px 0px 0px;
                padding: 0px 0px 0px 0px;
                width: ${100 - pag_venueListWidthPercent}%;
                height: ${pag_movablePixels
                    - pag_textchatHeightPixels}px;
                border-style: solid;
                border-color: black;
                border-bottom-width: 1px;
                border-top-width: 0px;
                border-left-width: 0px;
                border-right-width: 0px;
                overflow: auto;"
            id="pag-venuedatacontainer">
        <div style="position: absolute;
                    top: 2px;
                    left: 10px;"
                id="pag-venuedata">
      </div>
    </div>

    <%-- The venue Jabber text --%>
    <div style="position: absolute;
                top: ${105 + (pag_movablePixels
                     - pag_textchatHeightPixels)}px;
                left: 1%;
                margin: 0px 0px 0px 0px;
                padding: 0px 0px 0px 0px;
                width: 98%;
                height: ${pag_textchatHeightPixels}px;
                background-color: #ece9d8;
                border-style: solid;
                border-width: 2px;
                border-left-color: #828187;
                border-top-color: #828187;
                border-right-color: #f9f8f3;
                border-bottom-color: #f9f8f3;
                overflow: auto;"
            id="pag-textchat">
    </div>

    <%-- The venue jabber entry --%>
    <div style="position: absolute;
                top: ${pag_heightPixels - 60}px;
                left: 0px;
                width: 100%;
                height: 20px;"
            id="pag-textentry">
      <table style="border-width: 0px;
                    width: 100%;
                    height: 20px;"
              cellpadding="0px" cellspacing="2px">
          <tr>
              <td>
                <input type="text" id="pag-jabberinput"
                    style="width: 98%;"
                    onkeyup="if (event.keyCode == 13)
                          pag_sendText('pag-jabberinput');"/>
              </td>
              <td style="width: 60px">
                  <pag:pag_imagebutton width="60" height="26"  top="2"
                      right="2"
                      id="pag-displayText"
                      title="Send text message"
                      text="Display"
                      action="pag_sendText('pag-jabberinput');"/>
              </td>
          </tr>
      </table>
    </div>

    <%-- The status bar --%>
    <!-- <div style="position: absolute;
                top: ${pag_heightPixels - 25}px;
                left: 0px;
                width: 100%;
                height: 20px;
                background-color: #ece9d8;
                border-style: solid;
                border-width: 0px;
                overflow: hidden;">
        <p style="position: absolute;
                  margin: 0px 0px 0px 0px;"
                id="pag_status">
            ${pag_venueClientUI.status}
        </p>
    </div> -->

    <%-- The vertical slider bar - has to be here to be on top --%>
    <div style="position: absolute;
                top: 91px;
                left: ${pag_venueListWidthPercent}%;
                width: 5px;
                height: ${pag_movablePixels -
                     pag_textchatHeightPixels - 1}px;
                background-color: #ece9d8;
                border-style: solid;
                border-left-color: #f9f8f3;
                border-right-color: #828177;
                border-top-width: 0px;
                border-bottom-width: 0px;
                border-left-width: 2px;
                border-right-width: 2px;
                cursor: e-resize;
                overflow: hidden"
        id="pag-verticalsep"
        onmousedown="pag_mouseDownVertical(event,
                     new Array('pag-venues', 'pag-venueselection'),
                     new Array('pag-venuedatacontainer'), 'pag-verticalsep',
                     new Array(), pag_getLeft('pag-content') + 20,
                     pag_getRight('pag-content') - 20);"
        onmouseup="pag_stopDrag();
                   pag_venueListWidthPercent = pag_calculateWidthPercent(
                       'pag-venues', 'pag-content');
                   pag_setPreferences(pag_setPrefsUrl, pag_namespace,
                       pag_storePrefsUrl, 'venueListWidthPercent',
                       pag_venueListWidthPercent);">
    </div>

    <%-- The horizontal slider bar --%>
    <div style="position: absolute;
                top: ${pag_movablePixels
                     - pag_textchatHeightPixels + 90}px;
                left: 0px;
                width: 100%;
                height: 5px;
                background-color: #ece9d8;
                border-style: solid;
                border-top-color: #f9f8f3;
                border-bottom-color: #828177;
                border-top-width: 2px;
                border-bottom-width: 2px;
                border-left-width: 0px;
                border-right-width: 0px;
                cursor: n-resize;
                overflow: hidden;"
        id="pag-horizontalsep"
        onmousedown="pag_mouseOverHorizontal(event,
                     new Array('pag-venues', 'pag-venuedatacontainer',
                         'pag-verticalsep'),
                     new Array('pag-textchat'),
                     'pag-horizontalsep',
                     new Array(), pag_getTop('pag-content') + 40 + 90,
                     pag_getBottom('pag-content') - 40);"
        onmouseup="pag_stopDrag();
                   pag_setPreferences(pag_setPrefsUrl, pag_namespace,
                       pag_storePrefsUrl, 'textchatHeightPixels',
                       parseInt(document.getElementById(
                           'pag-textchat').style.height, 10));">
    </div>

    <%-- The networking dialog --%>
    <div style="position: absolute;
                top: 0px;
                left: 0px;
                width: 100%;
              height: ${pag_heightPixels}px;
              overflow: hidden;
              visibility: hidden;
              background-image: url('${
                 pageContext.request.contextPath}/images/overlay.png');"
            id="pag_networkMenuDialog">
        <div style="position: absolute;
                    top: 0px;
                    left: 0px;
                    width: 300px;
                    height: 200px;
                    background-color: #eee;
                    padding: 8px;
                    border: 2px outset #666;"
                id="pag_networkMenu">
            <p style="text-align: center;
                      font-weight: bold;
                      margin-bottom: 0px;">
                Network Configuration
            </p>
            <table style="border: 0px;
                          margin-left: auto;
                          margin-right: auto;
                          margin-top: 0px;">
                <tr>
                    <td>
                        <input type="radio" name="pag_network"
                            id="pag_automatic" value="automatic">
                    </td>
                    <td>Automatic</td>
                </td></tr>
                <tr>
                    <td>
                        <input type="radio" name="pag_network"
                            id="pag_multicast" value="multicast">
                    </td>
                    <td>Multicast</td>
                </td></tr>
                <tr>
                    <td>
                        <input type="radio" name="pag_network"
                            id="pag_unicast" value="unicast">
                    </td>
                    <td>Unicast</td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        Bridge: <select id="pag_bridges" style="width: 245px">
                                </select>
                    </td>
                </tr>
                <tr style="height: 5px"></tr>
                <tr>
                    <td colspan="2">
                        Point of Reference:<br/>
                        <input type="text" id="pag_pointOfRef"
                            style="width: 315px"
                            value="${pag_pointOfReference}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        Bridge Registries:<br/>
                        <select id="pag_registries" size="3"
                                style="width: 320px"
                                onchange="pag_selectRegistry('pag_registries',
                                              'pag_regedit');">
                        </select><br/>
                        <input type="text" id="pag_regedit"
                            style="width: 315px"/>
                    </td>
                </tr>
            </table>
            <pag:pag_imagebutton width="60" height="26"
                      top="270" left="10" id="pag-addreg"
                      title="Add Bridge Registry" text="Add"
                      action="pag_addRegistry('pag_venueClientController',
                                              'pag_registries',
                                              'pag_regedit');"/>
            <pag:pag_imagebutton width="60" height="26"
                      top="270" left="80" id="pag-editreg"
                      title="Edit Bridge Registry" text="Change"
                      action="pag_changeRegistry('pag_venueClientController',
                                              'pag_registries',
                                              'pag_regedit');"/>
            <pag:pag_imagebutton width="60" height="26"
                      top="270" left="150" id="pag-removereg"
                      title="Remove Bridge Registry" text="Remove"
                      action="pag_removeRegistry('pag_registries',
                                              'pag_regedit');"/>
            <pag:pag_imagebutton width="40" height="26"
                      top="310" left="290" id="pag-networkMenuClose"
                      title="OK" text="OK"
                      action="pag_currentBridgeId = pag_finishConfigureNetwork(
                          'pag_networkMenuDialog', 'pag_venueClientController',
                          'pag_bridges', 'pag_registries', 'pag_automatic',
                          'pag_multicast', 'pag_unicast', 'pag_pointOfRef',
                          pag_xmlRpcClient, pag_setPrefsUrl, pag_namespace,
                          pag_storePrefsUrl);"/>
            <pag:pag_imagebutton width="120" height="26"
                      top="310" left="160" id="pag-loadbridges"
                      title="Refresh Bridge List" text="Find More Bridges"
                      action="pag_getNewBridges('pag_venueClientController',
                          'pag_bridges', pag_getAvailableBridgesUrl, pag_namespace);"/>
            <pag:pag_imagebutton width="140" height="26"
                      top="310" left="10" id="pag-purgebridges"
                      title="Refresh Bridge List" text="Remove Dead Bridges"
                      action="pag_removeDeadBridges('pag_venueClientController',
                          'pag_bridges', pag_getAvailableBridgesUrl, pag_namespace);"/>
        </div>
    </div>

    <%-- Only show the user profile if they haven't filled it in --%>
    <c:set var="pag_profileVisibility" value="visible"/>
    <c:if test="${not empty pag_name}">
        <c:set var="pag_profileVisibility" value="hidden"/>
    </c:if>

    <%-- The user profile --%>
    <div style="position: absolute;
                top: 0px;
                left: 0px;
                width: 100%;
              height: ${pag_heightPixels}px;
              overflow: hidden;
              visibility: ${pag_profileVisibility};
              background-color: white;"
          id="pag_clientProfile">
      <h2>Please fill in the following details: </h2>
        <table style="border-width: 0px; width: 98%;">
            <tr>
                <th style="width: 100px; text-align: left;">Name: *</th>
                <td>
                    <input style="width: 98%; " id="pag_clientProfileName"
                        value="${pag_name}"/>
                </td>
            </tr>
            <tr>
                <th style="width: 100px; text-align: left;">E-mail: *</th>
                <td>
                    <input style="width: 98%;" id="pag_clientProfileEmail"
                        value="${pag_email}"/>
                </td>
            </tr>
            <tr>
                <th style="width: 100px; text-align: left;">Phone Number:</th>
                <td>
                    <input style="width: 98%;" id="pag_clientProfilePhone"
                        value="${pag_phoneNumber}"/>
                </td>
            </tr>
            <tr>
                <th style="width: 100px; text-align: left;">Location:</th>
                <td>
                    <input style="width: 98%;" id="pag_clientProfileLocation"
                        value="${pag_location}"/>
                </td>
            </tr>
            <tr>
                <th style="width: 100px; text-align: left;">Home Venue: *</th>
                <td>
                    <input style="width: 98%;" id="pag_clientProfileHomeVenue"
                        value="${pag_homeVenue}"/>
                </td>
            </tr>
            <tr>
                <th style="width: 100px; text-align: left;">Profile Type: *</th>
                <td>
                    <select style="width: 98%;" id="pag_clientProfileType">
                       <option value="user"
                               ${pag_profileType == "user"? "selected": ""}>
                           user
                       </option>
                       <option value="node"
                               ${pag_profileType == "node"? "selected": ""}>
                           node
                       </option>
                    </select>
                </td>
            </tr>
            <tr><td>(&nbsp;*&nbsp;required&nbsp;fields)</td></tr>
        </table>
        <br/>
        <input type="button" value="Set Profile Values"
            onclick="pag_setProfile('pag-currentvenue',
                                    ${pag_venueClientUI.inVenue},
                                    'pag_clientProfileName',
                                    'pag_clientProfileEmail',
                                    'pag_clientProfilePhone',
                                    'pag_clientProfileLocation',
                                    'pag_clientProfileHomeVenue',
                                    'pag_clientProfileType');"/>
    </div>

    <%-- Encryption box --%>
    <div style="position: absolute;
                top: 0px;
                left: 0px;
                width: 100%;
              height: ${pag_heightPixels}px;
              overflow: hidden;
              visibility: hidden;
              background-image: url('${
                 pageContext.request.contextPath}/images/overlay.png');"
            id="pag_encryptionDialog">
        <div style="position: absolute;
                    top: 0px;
                    left: 0px;
                    width: 300px;
                    height: 130px;
                    background-color: #eee;
                padding: 8px;
                border: 2px outset #666;"
                id="pag_encryptionDialogText">
            <p>Encryption Key: <input type="text" style="width: 190px"
                id="pag-encryption"></p>
            <pag:pag_imagebutton width="140" height="26"
                      top="40" left="160" id="pag-setencryptionbutton"
                      title="Set Encryption" text="Set Encryption"
                      action="pag_setEncryption('pag_venueClientController',
                          'pag_encryptionDialog', 'pag-encryption',
                          'pag-encryptionbutton_image', false);"/>
            <pag:pag_imagebutton width="140" height="26"
                      top="40" left="10" id="pag-cancelencryption"
                      title="Cancel" text="Cancel"
                      action="pag_setEncryption('pag_venueClientController',
                          'pag_encryptionDialog', 'pag-encryption',
                          'pag-encryptionbutton_image', true);"/>
        </div>
    </div>

    <%-- My Venues box --%>
    <div style="position: absolute;
                top: 0px;
                left: 0px;
                width: 100%;
              height: ${pag_heightPixels}px;
              overflow: hidden;
              visibility: hidden;
              background-image: url('${
                 pageContext.request.contextPath}/images/overlay.png');"
            id="pag_addMyVenueDialog">
        <div style="position: absolute;
                    top: 0px;
                    left: 0px;
                    width: 300px;
                    height: 130px;
                    background-color: #eee;
                padding: 8px;
                border: 2px outset #666;"
                id="pag_addMyVenueDialogText">
            <table style="border-width: 0px; width: 98%;">
                <tr>
                    <th style="width: 100px; text-align: left;">Name:</th>
                    <td><input style="width: 200px; " id="pag_venueNameBox"/></td>
                </tr>
                <tr>
                    <th style="width: 100px; text-align: left;">URL:</th>
                    <td><input style="width: 200px; " id="pag_venueUrlBox"/></td>
                </tr>
            </table>

            <pag:pag_imagebutton width="140" height="26"
                      top="80" left="160" id="pag-addmyvenue"
                      title="Add Venue" text="Add Venue"
                      action="pag_finishAddMyVenueDialog(
                          'pag_venueClientController',
                          'pag_addMyVenueDialog', 'pag_venueNameBox',
                          'pag_venueUrlBox', 'pag_myvenues', false);"/>
            <pag:pag_imagebutton width="140" height="26"
                      top="80" left="10" id="pag-canceladdmyvenue"
                      title="Cancel" text="Cancel"
                      action="pag_finishAddMyVenueDialog(
                          'pag_venueClientController',
                          'pag_addMyVenueDialog', 'pag_venueNameBox',
                          'pag_venueUrlBox', 'pag_myvenues', true);"/>
        </div>
    </div>

    <%-- The myVenues edit dialog --%>
    <div style="position: absolute;
                top: 0px;
                left: 0px;
                width: 100%;
              height: ${pag_heightPixels}px;
              overflow: hidden;
              visibility: hidden;
              background-image: url('${
                 pageContext.request.contextPath}/images/overlay.png');"
            id="pag_myVenuesEditDialog">
        <div style="position: absolute;
                    top: 0px;
                    left: 0px;
                    width: 300px;
                    height: 200px;
                    background-color: #eee;
                    padding: 8px;
                    border: 2px outset #666;"
                id="pag_myVenuesEditDialogText">
            <p style="text-align: center;
                      font-weight: bold;
                      margin-bottom: 0px;">
                Edit My Venues
            </p>
            <select id="pag_myvenues" size="3"
                    style="width: 320px"
                    onchange="pag_selectMyVenue('pag_myvenues',
                                  'pag_myvenuesname', 'pag_myvenuesurl');">
                <pag:pag_myVenueList urlVar="url" nameVar="name">
                    <option value="${url}">${name}</option>
                </pag:pag_myVenueList>
            </select>
            <table style="border-width: 0px; width: 98%;">
                <tr>
                    <th style="text-align: left;">Name:</th>
                    <td>
                        <input style="width: 265px; " id="pag_myvenuesname"/>
                    </td>
                </tr>
                <tr>
                    <th style="text-align: left;">URL:</th>
                    <td>
                        <input style="width: 265px; " id="pag_myvenuesurl"/>
                    </td>
                </tr>
            </table>
            <pag:pag_imagebutton width="60" height="26"
                      top="140" left="10" id="pag-editmyvenue"
                      title="Edit Venue" text="Change"
                      action="pag_changeMyVenue('pag_venueClientController',
                                              'pag_myvenues',
                                              'pag_myvenuesname',
                                              'pag_myvenuesurl');"/>
            <pag:pag_imagebutton width="60" height="26"
                      top="140" left="80" id="pag-removemyvenue"
                      title="Remove Venue" text="Remove"
                      action="pag_removeMyVenue('pag_myvenues',
                                              'pag_myvenuesname',
                                              'pag_myvenuesurl');"/>
            <pag:pag_imagebutton width="60" height="26"
                      top="140" left="150" id="pag-myvenueclose"
                      title="OK" text="OK"
                      action="pag_hideDialog('pag_myVenuesEditDialog');"/>
        </div>
    </div>

    <div style="position: absolute;
                top: 0px;
                left: 0px;
                width: 100%;
              height: ${pag_heightPixels}px;
              overflow: hidden;
              visibility: hidden;
              background-image: url('${
                 pageContext.request.contextPath}/images/overlay.png');"
            id="pag_uploadStatusDialog">
        <div style="position: absolute;
                    top: 0px;
                    left: 0px;
                    width: 210px;
                    height: 200px;
                    background-color: #eee;
                    padding: 8px;
                    border: 2px outset #666;"
                id="pag_uploadStatusDialogText">
            <p style="text-align: center; width: 100%;">
                Uploading...
            </p>
            <div style="height: 15px;
                        width: 254px;
                        background: #fff;
                        border: 1px solid silver;
                        margin: 0;
                        padding: 0;">
                <div style="height: 11px;
                            width: 0px;
                            margin: 2px;
                            padding: 0;
                            background: #C9DDEC;"
                    id="pag_uploadProgressBar">
                </div>
            </div>
            <p style="text-align: center; width: 100%;">
                Uploaded <span id="pag_uploadCurrent"></span>&nbsp;of&nbsp;
                <span id="pag_uploadTotal"></span>&nbsp;
                (<span id="pag_uploadPercent"></span>%)
            </p>
        </div>
    </div>
</div>

<%-- The div that is displayed initially --%>
<div style="position: relative;
             margin: 0px 0px 0px 0px;
             padding: 0px 0px 0px 0px;
             width: 100%;
             height: ${pag_heightPixels}px;
             overflow: hidden;
             visibility: visible;"
        id="pag-errormessage">
    <p>Javascript has not been detected.
       Please ensure your browser
       supports Javascript and that it is enabled.</p>
</div>

<%-- Script to run when the portlet is displayed --%>
<script language="JavaScript">

    document.getElementById("pag-errormessage").innerHTML =
        "<p>Please wait while the client is loaded...</p>";

    var pag_centerX = 0;
    var pag_centerY = 0;

    // Check that java is installed and enabled
    var versionApplet = document.getElementById("pag_version");
    if ((versionApplet == null) || !versionApplet.getVendor()
            || !versionApplet.getVersion()) {
        document.getElementById("pag-errormessage").innerHTML =
            "<p>Java has not been detected.  Please make sure that java is"
            + " installed, enabled and supported by your browser.  The"
            + " latest version of java can be downloaded from the"
            + " <a href='http://www.java.com/download' target='_blank'>"
            + "Sun download site</a>.</p>";
    } else {
        var vendor = versionApplet.getVendor();
        var version = versionApplet.getVersion();
        var versionregexp = /^(\d+).(\d+).(\d+)(_(\d+))?(-(.*))?/;
        var versionError = false;
        if ((vendor == null) || (version == null)) {
            versionError = true;
        } else if (!versionregexp.test(version)
                || (!versionApplet.vendorContains("sun")
                    && !versionApplet.vendorContains("apple"))) {
            versionError = true;
        } else {
            if (versionApplet.versionIsAtLeast("1.5.0")) {
                versionError = false;
            } else {
                versionError = true;
            }
        }
        if (versionError) {
            var error =
                "<p>The version of java detected is not compatible with this"
                + " portlet.  Please install the latest version of Java from"
                + " the <a href='http://www.java.com/download/'"
                + " target='_blank'>Sun download site</a>.</p>";
            if (version != null) {
                error += "<p>";
                error += "Java Version Detected: ";
                if (vendor != null) {
                    error += vendor + " version ";
                }
                error += version + "</p>";
            }
            document.getElementById("pag-errormessage").innerHTML = error;
        } else {

            // Create the venue tree
            var pag_venueTree = null;

            // Ensures that the client looks OK when the window is resized
            // Note we store and call the old resize function as this is a
            // portlet!
            var oldWindowResize = window.onresize;
            var oldWindowBeforeUnload = window.onbeforeunload;
            window.onresize = function() {
                if (oldWindowResize != null) {
                    oldWindowResize();
                }
                var width = pag_getWidth('pag-content');
                document.getElementById('pag-venues').style.width =
                    ((width * pag_venueListWidthPercent) / 100) + "px";
                document.getElementById('pag-venueselection').style.width =
                    ((width * pag_venueListWidthPercent) / 100) + "px";
                document.getElementById('pag-venuedatacontainer').style.width =
                    ((width * (100 - pag_venueListWidthPercent)) / 100) + "px";
                document.getElementById('pag-venuedatacontainer').style.left =
                    ((width * pag_venueListWidthPercent) / 100) + "px";
                document.getElementById('pag-verticalsep').style.left =
                    ((width * pag_venueListWidthPercent) / 100) + "px";
            }

            // Before the page is unloaded, store anything that you need when it
            // comes back!
            window.onbeforeunload = function() {
                if (oldWindowBeforeUnload != null) {
                    oldWindowBeforeUnload();
                }
                var div = document.getElementById('pag-venues');
                var verticalScroll = div.scrollTop;
                var horizontalScroll = div.scrollLeft;
                pag_xmlRpcClient.call("setVenueListScroll", verticalScroll,
                        horizontalScroll);
            }

            var pag_xmlRpcHandler = new pag_xmlrpcHandler(
                "pag_venueClientController", "pag-textchat",
                "pag_clientProfile", "pag-venuetitle",
                "pag-currentvenue", "pag-venues",
                pag_context + '/images/', pag_setPrefsUrl, pag_namespace,
                pag_storePrefsUrl, pag_dataDownloadUrl, pag_dataUploadUrl,
                'pag_uploadStatusDialog', 'pag_uploadStatusDialogText',
                'pag_uploadProgressBar', 'pag_uploadTotal', 'pag_uploadCurrent',
                'pag_uploadPercent');
            var pag_xmlRpcClient = new pag_axmlrpcClient("pag_xmlRpcClient",
                pag_namespace, pag_context + "/jsp/getNextXmlRpcResponse.jsp",
                pag_context + "/jsp/XmlRpcExecute.jsp",
                "pag_venueClientController", "pag_xmlRpcHandler");
            pag_xmlRpcHandler.enterVenueResponse(
                "${pag_venueClientUI.currentVenueState}", false);
            pag_xmlRpcClient.start();

            <pag:pag_jabberUpdate messageVar="message">
                pag_xmlRpcHandler.jabberAddMessage("${message}");
            </pag:pag_jabberUpdate>



            // Things to run after a short delay (for IE compatibility)
            function pag_delayedFunction() {
                pag_setPixelValues(new Array('pag-venues',
                    'pag-venuedatacontainer', 'pag-verticalsep',
                    'pag-venueselection'),
                     pag_getWidth('pag-content'));

                pag_centerX = pag_getWidth('pag-content') / 2;
                pag_centerY = ${pag_heightPixels} / 2;

                <c:if test="${pag_venueClientUI.inVenue}">
                    <pag:pag_getVenueListScroll
                        horizontalScrollVar="pag_horizVenueListScroll"
                        verticalScrollVar="pag_vertVenueListScroll"/>
                    document.getElementById('pag-venues').scrollTop =
                        ${pag_vertVenueListScroll};
                    document.getElementById('pag-venues').scrollLeft =
                        ${pag_horizVenueListScroll};
                    document.getElementById('pag-textchat').scrollTop =
                        document.getElementById('pag-textchat').scrollHeight;
                </c:if>

                var venueClientController = document.getElementById(
                    "pag_venueClientController");
                venueClientController.threadedEval(
                        "pag_init(${pag_heightPixels});");
                }
            window.setTimeout("pag_delayedFunction();", 500);
        }
    }
</script>
