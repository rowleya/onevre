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
<div style="visibility: hidden;">
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
</div>
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


<script type="text/javascript" language="javascript">
var Parameters = {

    /* The URL of the XML RPC server */
	xmlrpcRequestUrl: "/jsp/XmlRpcExecuteSync.jsp",
	xmlrpcResponseUrl: "${pageContext.request.contextPath}" + "/jsp/getNextXmlRpcResponse.jsp",
	pag_context: "${pageContext.request.contextPath}",
	pag_venueListWidthPercent: ${pag_venueListWidthPercent},
	pag_venueClientControllerId: "pag_venueClientController",
	pag_namespace: "${pag_namespace}",
	pag_storePrefsUrl: "${pag_storePrefsUrl}",
	pag_setPrefsUrl: "${pageContext.request.contextPath}" + "/jsp/setPreference.jsp",
	pag_dataDownloadUrl: "/jsp/Download.jsp",
	pag_dataUploadUrl: "${pageContext.request.contextPath}" + "/jsp/Upload.jsp",
	pag_serviceUploadUrl: "${pageContext.request.contextPath}" + "/jsp/uploadService.jsp",
	pag_bridgeUploadUrl: "${pageContext.request.contextPath}" + "/jsp/uploadBridge.jsp",
	pag_getAvailableBridgesUrl: "${pageContext.request.contextPath}" + "/jsp/getAvailableBridges.jsp",
	pag_networkMenuVisible: false,
	pag_localUserId: "${pag_venueClientUI.clientProfile.publicId}",
	pag_pointOfReference: "${pag_pointOfReference}",
	pag_registries: new Array(),
	pag_previousVenues: new Array(${pag_venueClientUI.previousVenues}),
	pag_currentVenueUri: "${pag_venueClientUI.currentVenueUri}",
	pag_isInVenue: ${pag_venueClientUI.inVenue},
};
</script>

<%-- The main div of the portlet - everything visual is within this --%>
<div style="position: relative;
             margin: 0px 0px 0px 0px;
             padding: 0px 0px 0px 0px;
             width: 100%;
             height: 100%;
             overflow: hidden;"
        id="pag-content">
</div>

<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/com.googlecode.onevre.gwt.Application.nocache.js"></script>
