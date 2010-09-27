var leftPercentWidth = 20;
var urlArray = new Array();
var currentURLPos = 0;
var navigators = new Array();
var helpUri = parseUri(window.location);
var thisPath = helpUri.protocol + "://" + helpUri.authority
     + helpUri.directoryPath;

function getWidth() {
    var myWidth = 0;
    if (typeof(window.innerWidth) == 'number') {
        myWidth = window.innerWidth;
    } else if (document.documentElement &&
             document.documentElement.clientWidth) {
        myWidth = document.documentElement.clientWidth;
    } else if (document.body && document.body.clientWidth) {
        myWidth = document.body.clientWidth;
    }
    return myWidth;
}

function getHeight() {
    var myHeight = 0;
    if (typeof(window.innerHeight) == 'number') {
        myHeight = window.innerHeight;
    } else if (document.documentElement &&
             document.documentElement.clientHeight) {
        myHeight = document.documentElement.clientHeight;
    } else if (document.body && document.body.clientHeight) {
        myHeight = document.body.clientHeight;
    }
    return myHeight;
}

function calculateWidthPercent(leftdivid) {
    var width = getWidth();
    var divWidth = parseInt(
        document.getElementById(leftdivid).style.width, 10);
    leftPercentWidth = Math.round((divWidth * 100) / width);
}

function setSize(itemid, widthpercent, heightpercent) {
    var width = getWidth();
    var height = getHeight();
    var item = document.getElementById(itemid);
    if (widthpercent == 0) {
        item.style.width = 0;
    } else if (typeof(widthpercent) == 'string') {
        item.style.width = widthpercent;
    } else {
        item.style.width = Math.round(((width * widthpercent) / 100)) + "px";
    }
    if (heightpercent == 0) {
        item.style.height = 0;
    } else if (typeof(heightpercent) == 'string') {
        item.style.height = heightpercent;
    } else {
        item.style.height = Math.round(((height * heightpercent) / 100)) + "px";
    }
}

function setPosition(itemid, widthpercent, heightpercent) {
    var width = getWidth();
    var height = getHeight();
    var item = document.getElementById(itemid);
    if (widthpercent == 0) {
        item.style.left = 0;
    } else {
        item.style.left = Math.round(((width * widthpercent) / 100)) + "px";
    }
    if (heightpercent == 0) {
        item.style.top = 0;
    } else {
        item.style.top = Math.round(((height * heightpercent) / 100)) + "px";
    }
}

function doresize(leftdivid, vertsepid, rightdivid) {
    setSize(leftdivid, leftPercentWidth, 100);
    setSize(rightdivid, 100 - leftPercentWidth, 100);
    setPosition(leftdivid, 0, 0);
    setPosition(rightdivid, leftPercentWidth, 0);
    setPosition(vertsepid, leftPercentWidth, 0);
    var rightdiv = document.getElementById(rightdivid);
    var navHeight = getHeight() - 40;
    for (var i = 0; i < navigators.length; i++) {
        document.getElementById(navigators[i]).style.height = navHeight + "px";
    }
}

function mouseOverVertical(event, leftitems, rightitems, dragitem,
        scrolls) {
    if (event == null) {
        event = window.event;
    }
    var draggingItemsLeft = new Array(leftitems.length);
    var draggingItemsLeftStartWidth = new Array(leftitems.length);
    var draggingItemsRight = new Array(rightitems.length);
    var draggingItemsRightStartWidth = new Array(rightitems.length);
    var draggingItemsRightStart = new Array(rightitems.length);
    var scrollItems = new Array(scrolls.length);
    var draggingItemVertical = document.getElementById(dragitem);
    var dragItemStart = draggingItemVertical.style.left;
    var cursorStart = event.clientX;
    for (var i=0; i<leftitems.length; i++)  {
        draggingItemsLeft[i] = document.getElementById(leftitems[i]);
        draggingItemsLeftStartWidth[i] = draggingItemsLeft[i].style.width;
    }
    for (var i=0; i<rightitems.length; i++)  {
        draggingItemsRight[i] = document.getElementById(rightitems[i]);
        draggingItemsRightStartWidth[i] = draggingItemsRight[i].style.width;
        draggingItemsRightStart[i] = draggingItemsRight[i].style.left;
    }
    for (var i=0; i<scrolls.length; i++)  {
        scrollItems[i] = document.getElementById(scrolls[i]);
    }

    document.onmousemove = function(event) {
        doDragVertical(event, draggingItemsLeft, draggingItemsRight,
            draggingItemVertical, cursorStart, dragItemStart,
            draggingItemsLeftStartWidth, draggingItemsRightStartWidth,
            draggingItemsRightStart, scrollItems);
        return false;
    };
}

function doDragVertical(event, draggingItemsLeft, draggingItemsRight,
        draggingItemVertical, cursorStart, dragItemStart,
        draggingItemsLeftStartWidth, draggingItemsRightStartWidth,
        draggingItemsRightStart, scrollItems) {
    if (event == null) {
        event = window.event;
    }
    var diff = event.clientX - cursorStart;
    draggingItemVertical.style.left = parseInt(dragItemStart, 10) + diff;
    for (var i=0; i<draggingItemsLeft.length; i++)  {
        draggingItemsLeft[i].style.width =
            parseInt(draggingItemsLeftStartWidth[i], 10) + diff;
    }
    for (var i=0; i<draggingItemsRight.length; i++)  {
        draggingItemsRight[i].style.width =
            parseInt(draggingItemsRightStartWidth[i], 10) - diff;
        draggingItemsRight[i].style.left =
            parseInt(draggingItemsRightStart[i], 10) + diff;
    }
    for (var i=0; i<scrollItems.length; i++)  {
        scrollItems[i].scrollTop = scrollItems[i].scrollHeight;
    }
}

function stopDrag() {
    document.onmousemove = null;
}

function newRequest() {
    if (window.XMLHttpRequest) {
        return new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
}

var currentview = null;
function selectNavigator(newview) {
    if (currentview != newview) {
        if (currentview != null) {
            document.getElementById(currentview).className = 'deselected';
        }
        document.getElementById(newview).className = 'selected';
        currentview = newview;
    }
}

function displayHistory() {
    var history = "";
    for (var i = 0; i < urlArray.length; i++) {
        history += urlArray[i];
        if (i == currentURLPos) {
            history += " <---";
        }
        history += "\n";
    }
    alert(history);
}

function goForward() {
    if (currentURLPos < (urlArray.length - 1)) {
        var removedItems = Array();
        currentURLPos++;
        for (var i = currentURLPos + 1; i < urlArray.length; i++) {
            removedItems.push(urlArray[i]);
        }
        tocTree.selectFromHelpURL(urlArray[currentURLPos]);
        urlArray.pop();
        currentURLPos--;
        for (var i = 0; i < removedItems.length; i++) {
            urlArray.push(removedItems[i]);
        }
    }
}

function goBack() {
    if (currentURLPos > 0) {
        var removedItems = Array();
        currentURLPos--;
        for (var i = currentURLPos + 1; i < urlArray.length; i++) {
            removedItems.push(urlArray[i]);
        }
        tocTree.selectFromHelpURL(urlArray[currentURLPos]);
        urlArray.pop();
        currentURLPos--;
        for (var i = 0; i < removedItems.length; i++) {
            urlArray.push(removedItems[i]);
        }
    }
}

function goToPage(page, id) {
    var uri = parseUri(page);
    var content = document.getElementById("content");
    var request = newRequest();
    request.open("POST", page, false);
    request.send("");
    if (request.status == 200) {
        content.innerHTML = request.responseText;
        var anchors = content.getElementsByTagName("a");
        for (var i = 0; i < anchors.length; i++) {
            if (anchors[i].target == null || anchors[i].target == "" ||
                    anchors[i].target != "_blank") {
                var href = anchors[i].href;
                if (href.indexOf(thisPath) == 0) {
                    href = href.substring(thisPath.length);
                    href = uri.protocol + "://" + uri.authority +
                        uri.directoryPath + href;
                }
                anchors[i].href =
                    "javascript: tocTree.selectFromHelpURL('" + href + "');";
            }
        }
        while (urlArray.length - 1 > currentURLPos) {
            urlArray.pop();
        }
        urlArray.push(page);
        currentURLPos = urlArray.length - 1;
    }
}

function doPrint() {
    var content = document.getElementById('content').innerHTML;
    var printwin = window.open("", "",
        "menubar=no,resizable=yes,toolbar=no,status=yes,scrollbars=yes," +
        "height=480,width=640");
    printwin.document.open();
    printwin.document.write("<html>");
    printwin.document.write("<head>");
    printwin.document.write("<title>Print Window</title>");
    printwin.document.write("<link rel=\"stylesheet\" href=\"help.css\""
        + " type=\"text/css\"/>");
    printwin.document.write("</head>");
    printwin.document.write("<body>");
    printwin.document.write(content);
    printwin.document.write("</body)");
    printwin.document.write("</html>");
    printwin.document.close();
    printwin.focus();
    printwin.print();
    printwin.close();
}

function getDOM(text) {
    if (window.ActiveXObject) {
        var doc = new ActiveXObject("Microsoft.XMLDOM");
        doc.async = "false";
        doc.loadXML(text);
        return doc;
    } else {
        var parser = new DOMParser();
        var doc = parser.parseFromString(text,"text/xml");
        return doc;
    }
}

/* parseUri JS v0.1, by Steven Levithan (http://badassery.blogspot.com)
Splits any well-formed URI into the following parts (all are optional):
----------------------
- source (since the exec() method returns backreference 0
  [i.e., the entire match] as key 0, we might as well use it)
- protocol (scheme)
- authority (includes both the domain and port)
    - domain (part of the authority; can be an IP address)
    - port (part of the authority)
- path (includes both the directory path and filename)
    - directoryPath (part of the path; supports directories with periods,
      and without a trailing backslash)
    - fileName (part of the path)
- query (does not include the leading question mark)
- anchor (fragment)
*/
function parseUri(sourceUri){
    var uriPartNames = ["source", "protocol", "authority", "domain", "port",
        "path", "directoryPath", "fileName", "query", "anchor"];
    var uriParts = new RegExp("^(?:([^:/?#.]+):)?(?://)?"
         + "(([^:/?#]*)(?::(\\d*))?)?"
         + "((/(?:[^?#](?![^?#/]*\\.[^?#/.]+(?:[\\?#]|$)))*/?)?([^?#/]*))?"
         + "(?:\\?([^#]*))?(?:#(.*))?").exec(sourceUri);
    var uri = {};

    for(var i = 0; i < uriPartNames.length; i++){
        uri[uriPartNames[i]] = (uriParts[i] ? uriParts[i] : "");
    }

    // Always end directoryPath with a trailing backslash if a path was present
    // in the source URI
    // Note that a trailing backslash is NOT automatically inserted within or
    // appended to the "path" key
    if(uri.directoryPath.length > 0){
        uri.directoryPath = uri.directoryPath.replace(/\/?$/, "/");
    }

    return uri;
}