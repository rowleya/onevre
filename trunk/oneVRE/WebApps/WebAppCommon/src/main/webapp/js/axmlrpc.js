/**
 * This class is used to receive Asynchronous XMLRPC Calls and Responses
 */

/**
 * Creates a new xmlrpc client thread
 *
 * @param url The url to get the message from
 */
function pag_axmlrpcClient(name, namespace, url, callUrl,
        venueClientControllerId, handlerId) {
    this.url = url;
    this.request = null;
    this.namespace = namespace;
    this.closed = false;
    this.handlerId = handlerId;
    this.venueClientController = document.getElementById(
        venueClientControllerId);
    this.callUrl = callUrl;
    this.lastError = -1;

    this.getUpdate = function() {
        if (this.request == null) {
            this.request = pag_newRequest();
            this.request.onreadystatechange = function() {
                eval(name + ".handleUpdate();");
            };
            this.request.open("POST", this.url, true);
            this.request.setRequestHeader('Content-Type',
                                          'application/x-www-form-urlencoded');
            this.request.send("namespace="+encodeURIComponent(this.namespace));
        }
    };

    this.decodeParams = function(response) {
        var params = "";
        var param = response.getElementsByTagName("param");
        if (param != null) {
            for (var i = 0; i < param.length; i++) {
                var value = param[i].getElementsByTagName("value");
                if ((value != null) && (value.length > 0)) {
                    params += ", " + "'";
                    for (var j=0; j<value.length; j++) {
                        if (value[j].nodeType == 3) {
                            params += value[j].nodeValue;
                        } else if (value[j].nodeType == 1) {
                            for (var k=0 ; k<value[j].childNodes.length; k++) {
                                params += value[j].childNodes[k].nodeValue;
                            }
                        }
                    }
                    params += "'";
                }
            }
        }
        return params.substring(2);
    };

    this.handleUpdate = function() {
        if (this.request.readyState == 4 && !this.closed) {
            if (this.request.status == 200) {
                if (this.request.responseXML != null) {
                    var response = this.request.responseXML.documentElement;
                    if (response.nodeName == "methodCall") {
                        var methodName = response.getElementsByTagName(
                            "methodName");
                        if ((methodName != null) && (methodName.length > 0)) {
                            methodName = methodName[0].childNodes[0].nodeValue;
                            var method = this.handlerId + "." + methodName;
                            if (eval(method) == null) {
                                alert("XMLRPC Error in methodCall: " + method
                                    + " not found");
                            } else {
                                var params = this.decodeParams(response);
                                this.venueClientController.threadedEval(
                                    method + "(" + params + ")");
                            }
                        } else {
                            alert("XMLRPC Error: Method name missing from XML: "
                                 + this.request.responseText);
                        }
                    } else if (response.nodeName == "methodResponse") {
                        var methodName = response.getElementsByTagName(
                            "methodName");
                        var fault = response.getElementsByTagName("fault");
                        if ((fault != null) && (fault.length > 0)) {
                            var faultCode = -1;
                            var faultString = "Unknown Error";
                            var members = fault[0].getElementsByTagName(
                                "member");
                            if (members != null) {
                                for (var i = 0; i < members.length; i++) {
                                    var name = members[i].getElementsByTagName(
                                        "name");
                                    var value = members[i].getElementsByTagName(
                                        "value");
                                    if ((name != null) && (name.length > 0)
                                            && (value != null)
                                            && (value.length > 0)) {
                                        name = name[0].childNodes[0].nodeValue;
                                        if (name == "faultCode") {
                                            value =
                                                value[0].getElementsByTagName(
                                                "int");
                                            if ((value != null)
                                                    && (value.length > 0)) {
                                                faultCode = value[0]
                                                    .childNodes[0].nodeValue;
                                            }
                                        } else if (name == "faultString") {
                                            value =
                                                value[0].getElementsByTagName(
                                                "string");
                                            if ((value != null)
                                                    && (value.length > 0)) {
                                                faultString = value[0]
                                                    .childNodes[0].nodeValue;
                                            }
                                        }
                                    }
                                }
                            }
                            if ((methodName != null)
                                    && (methodName.length > 0)) {
                                methodName =
                                    methodName[0].childNodes[0].nodeValue;
                                var method = this.handlerId + "." + methodName
                                     + "Error";
                                if (eval(method) != null) {
                                    this.venueClientController.threadedEval(
                                        method + "(" + faultCode + ", \""
                                        + faultString + "\");");
                                } else {
                                    alert("Error calling method  " + methodName
                                        + ": " + faultCode + ": "
                                        + faultString);
                                }
                            } else {
                            	if (faultCode !=0){
	                                alert("XMLRPC Error " + faultCode + ": "
	                                    + faultString);
                            	}
                            }
                        } else {
                            var methodName = response.getElementsByTagName(
                                "methodName");
                            if ((methodName != null)
                                    && (methodName.length > 0)) {
                                methodName =
                                    methodName[0].childNodes[0].nodeValue;
                                var method = this.handlerId + "." + methodName
                                     + "Response";
                                if (eval(method) != null) {
                                    var params = this.decodeParams(response);
                                    this.venueClientController.threadedEval(
                                        method + "(" + params + ")");
                                }
                            } else {
                                alert("XMLRPC Error: Method name missing"
                                    + " from XML: "
                                    + this.request.responseText);
                            }
                        }
                    } else if (response.nodeName != "done") {
                        if (response.nodeName != "empty") {
                            alert("XMLRPC Error: unknown root node "
                                + response.nodeName);
                        }
                    }

                    if (response.nodeName != "done") {
                        this.request = null;
                        this.getUpdate();
                    }
                }
            } else if (!this.closed) {
                if (this.lastError != this.request.status) {
                    if (this.request.status !=0){
	                	alert("XMLRPC Error: "
	                        + this.request.status + ": "
	                        + this.request.responseText);
                	}
                    this.lastError = this.request.status;
                }
                this.request = null;
                this.getUpdate();
            }
        }
    };

    this.call = function(methodName, params) {
        var paramString = "";
        for (var i = 1; i < arguments.length; i++) {
            paramString += "<param><value>";
            paramString += arguments[i];
            paramString += "</value></param>";
        }
        var message = "<?xml version=\"1.0\"?>";
        message += "<methodCall>";
        message += "<methodName>" + methodName + "</methodName>";
        message += "<params>" + paramString + "</params>";
        message += "</methodCall>";
        var callRequest = pag_newRequest();
        callRequest.open("POST", this.callUrl, false);
        callRequest.setRequestHeader('Content-Type',
                                          'application/x-www-form-urlencoded');
        callRequest.send("namespace="
            + encodeURIComponent(this.namespace)
            + "&message=" + encodeURIComponent(message));
        if (callRequest.status != 200) {
            alert("XMLRPC Error calling method: " + callRequest.responseText);
        }
    };

    this.close = function() {
        this.closed = true;
        if (this.request) {
            this.request.abort();
        }
    };

    this.start = function() {
        this.getUpdate();
    };

    return this;
}


