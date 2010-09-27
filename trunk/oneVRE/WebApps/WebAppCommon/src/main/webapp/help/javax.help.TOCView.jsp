<%@ page isELIgnored="false" %>
<%@ taglib uri="/WEB-INF/tld/jhlib.tld" prefix="jh" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="helpBroker" class="javax.help.ServletHelpBroker"
    scope="session"/>
<c:set var="curNav" value="${helpBroker.currentNavigatorView}"/>
<script>
    tocTree = new Tree("tocTree", 22, "ccccff", true, false, "tocitem",
        goToPage);
    <jh:tocItem helpBroker="${helpBroker}" tocView="${curNav}">
    <c:if test="${empty iconURL}">
        <c:set var="iconURL" value="null"/>
    </c:if>
    <c:if test="${empty contentURL}">
        <c:set var="contentURL" value="null"/>
    </c:if>
    tocTree.addTreeNode("${parentID}", "${nodeID}", "${iconURL}", "${name}",
        "${helpID}", "${contentURL}", "${expansionType}");
    </jh:tocItem>
    tocTree.drawTree();
    tocTree.refreshTree();
    <c:set var="id" value="${helpBroker.currentID}"/>
    <c:if test="${not empty id}">
        tocTree.selectFromHelpID("${id.IDString}");
    </c:if>
</script>


