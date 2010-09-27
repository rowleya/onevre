<%@ page isELIgnored="false" %>
<%@ taglib uri="/WEB-INF/tld/jhlib.tld" prefix="jh" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="helpBroker" class="javax.help.ServletHelpBroker"
    scope="session"/>
<c:set var="id" value="PAG"/>
<c:if test="${not empty param.id}">
    <c:set var="id" value="${param.id}"/>
</c:if>
<c:set var="helpset" value="${param.context}/help/help/jhelpset.hs"/>
<c:if test="${not empty param.helpset}">
    <c:set var="helpset" value="${param.helpset}"/>
</c:if>
<jh:validate helpBroker="${helpBroker}" currentID="${id}"
    helpSetName="${helpset}"/>
<c:set var="currentview" value="${helpBroker.currentNavigatorView}"/>
<c:choose>
    <c:when test="${not empty currentview}">
        <c:set var="currentview" value="${currentview.name}"/>
    </c:when>
    <c:otherwise>
        <c:set var="currentview" value=""/>
    </c:otherwise>
</c:choose>
<html>
    <head>
        <title>Help</title>
        <link rel="stylesheet" href="help.css" type="text/css"/>
        <script language="JavaScript" src="js/frames.js"></script>
        <script language="JavaScript1.3" src="js/tree.js"></script>
        <script language="JavaScript">
            currentview = '${currentview}';
        </script>
    </head>
    <body onresize="doresize('left', 'vertsep', 'right');">
        <div class="right" id="right">
            <div class="toolbar" id="toolbar">
                <div class="buttons">
                    <img src="images/back.gif" alt="Back"
                                onclick="goBack();"/>
                    <img src="images/forward.gif" alt="Forward"
                                onclick="goForward();"/>
                    <img src="images/print.gif" alt="Print"
                                onclick="doPrint();">
                </div>
                <div class="close">
                    <p onclick="window.close();">Close</p>
                </div>
            </div>
            <div class="content" id="content">
            </div>
        </div>
        <div class="left" id="left">
            <div class="menu" id="menu">
                <table class="menu" cellpadding="5px" cellspacing="0px">
                    <tr id="navigators">
                        <jh:navigators helpBroker="${helpBroker}">
                            <c:set var="tdclass" value="deselected"/>
                            <c:if test="${isCurrentNav}">
                                <c:set var="tdclass" value="selected"/>
                            </c:if>
                            <c:if test="${empty iconURL}">
                                <c:set var="iconURL"
                                    value="images/${className}.gif"/>
                            </c:if>
                            <td class="${tdclass}" id="${name}"
                                    onclick="selectNavigator('${name}');">
                                <img src="${iconURL}" alt="${tip}"/>
                            </td>
                        </jh:navigators>
                    </tr>
                </table>
            </div>
            <jh:navigators helpBroker="${helpBroker}">
                <c:set var="visiblity" value="hidden"/>
                <c:if test="${isCurrentNav}">
                    <c:set var="visibility" value="visible"/>
                </c:if>
                <div class="navigator" id="nav${name}"
                        style="visibility: ${visibility}">
                    <c:catch var="exception">
                        <c:set target="${helpBroker}" property="currentView"
                            value="${name}"/>
                        <jsp:include page="${className}.jsp"/>
                    </c:catch>
                    <c:if test="${not empty exception}">
                        <p>Error parsing the navigator:
                           <c:out value="${exception}"/>
                        </p>
                    </c:if>
                </div>
                <script language="JavaScript">
                    navigators.push("nav${name}");
                </script>
            </jh:navigators>
        </div>
        <div class="verticalseparator" id="vertsep"
            onmousedown="mouseOverVertical(event,
                new Array('left'), new Array('right'),
                'vertsep', new Array());"
            onmouseup="stopDrag();calculateWidthPercent('left');">
        </div>
        <script language="JavaScript">
            doresize('left', 'vertsep', 'right');
        </script>
    </body>
</html>
