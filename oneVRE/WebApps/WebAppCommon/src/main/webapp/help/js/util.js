/*
 *
 * Copyright 2003 Sun Microsytems, Inc. All Rights Reserved.
 *
 */

var staticCurrentURL = "";
var urlArray = new Array();
var currentURLPos = 0;

/*
 * check and see if any change in the content has occured
 * if it has fire an update with change
 */
function invoke(theURL, id) {
    url = top.contentsframe.document.URL;
    if (url.indexOf(theURL) == -1) {
        top.contentsframe.location.replace(theURL);
        urlArray[currentURLPos] = url;
        while (urlArray.length - 1 > currentURLPos) {            
            urlArray.pop();
        }
        urlArray.push(theURL);
        currentURLPos = urlArray.length - 1;
    }
}

function checkContentsFrame() {
    var url;
    url = top.contentsframe.document.URL;
    if (staticCurrentURL.indexOf(url) == -1) {
            staticCurrentURL = url;
            top.treeframe.tocTree.selectFromHelpURL(url);
    }
    top.setTimeout("top.checkContentsFrame( );", 1000);
}

function goBack() {
    if (currentURLPos > 0) {
        currentURLPos -= 1;
        top.contentsframe.location.replace(urlArray[currentURLPos]);
    }
}

function goForward() {
    if (currentURLPos < urlArray.length - 1) {
        currentURLPos += 1;
        top.contentsframe.location.replace(urlArray[currentURLPos]);
    }
}


var browser = new browserData();
function browserData()
{
    var useragnt = navigator.userAgent;
    this.canDoDOM = (document.getElementById) ? true : false;
    if ( useragnt.indexOf('Opera') >= 0) {
	this.name = 'Opera';
    } else if (  useragnt.indexOf('MSIE') >= 0 ) {
	this.name = 'InternetExplorer';
    } else {
	this.name = 'Another';
    }

    this.OS = ''
    var platform;
    if (typeof(window.navigator.platform) != 'undefined')
    {
	platform = window.navigator.platform.toLowerCase();
	if (platform.indexOf('win') != -1) {
	    this.OS = 'win';
	} else if (platform.indexOf('mac') != -1) {
	    this.OS = 'mac';
	} else if (platform.indexOf('unix') != -1 || platform.indexOf('linux') != -1 || platform.indexOf('sun') != -1) {
	    this.OS = 'nix';
	}
    }
}
