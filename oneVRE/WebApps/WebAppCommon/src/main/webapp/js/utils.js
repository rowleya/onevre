/*
 * @(#)utils.js
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
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Provides useful general functions
 */

 
/**
 * Creates a new AJAX request object
 */
function pag_newRequest() {
    if (window.XMLHttpRequest) {
        return new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
}

/**
 * Gets the width of an object
 * @param divid The id of the object
 */
function pag_getWidth(divid) {
    var elem = document.getElementById(divid);
    xPos = elem.offsetWidth;
    return xPos;
}

/**
 * Gets the height of an object
 * @param divid The id of the object
 */
function pag_getHeight(divid) {
    var elem = document.getElementById(divid);
    yPos = elem.offsetHeight;
    return yPos;
}

/**
 * Gets the top position of an object
 * @param divid The id of the object
 */
function pag_getTop(divid) {
    var obj = document.getElementById(divid);
    var curtop = 0;
    if (obj.offsetParent) {
        curtop = obj.offsetTop
        while (obj = obj.offsetParent) {
            curtop += obj.offsetTop
        }
    }
    return curtop;
}

/**
 * Gets the left position of an object
 * @param divid The id of the object
 */
function pag_getLeft(divid) {
    var obj = document.getElementById(divid);
    var curleft = 0;
    if (obj.offsetParent) {
        curleft = obj.offsetLeft
        while (obj = obj.offsetParent) {
            curleft += obj.offsetLeft;
        }
    }
    return curleft;
}

/**
 * Gets the bottom position of an object
 * @param divid The id of the object
 */
function pag_getBottom(divid) {
    return pag_getTop(divid) + pag_getHeight(divid);
}

/**
 * Gets the right position of an object
 * @param divid The id of the object 
 */
function pag_getRight(divid) {
    return pag_getLeft(divid) + pag_getWidth(divid);
}

/**
 * Gets the X position of the mouse
 * @param evt The event from which to get the position
 */
function pag_mouseX(evt) {
    if (evt.pageX) {
        return evt.pageX;
    } else if (evt.clientX) {
       return evt.clientX + (document.documentElement.scrollLeft ?
                             document.documentElement.scrollLeft :
                             document.body.scrollLeft);
    }
}

/**
 * Gets the Y position of the mouse
 * @param evt The event from which to get the position
 */
function pag_mouseY(evt) {
    if (evt.pageY) {
        return evt.pageY;
    } else if (evt.clientY) {
       return evt.clientY + (document.documentElement.scrollTop ?
                             document.documentElement.scrollTop :
                             document.body.scrollTop);
    }
}

/**
 * Determines if the browser can do AJAX
 * @return true if the browser can do AJAX, or false otherwise
 */
function pag_isXmlRequestCompatible() {
    request = newRequest();
    if (request != null) {
        return true;
    }
    return false;
}
