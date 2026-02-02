<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<body>

<!-- Compliant: IDs in mutually exclusive c:if blocks with complementary conditions -->
<c:if test="${assessmentMediasCount > 0}">
    <div id="viewMediaLink">
        <a href="javascript:viewMedia()">View Media</a>
    </div>
</c:if>
<c:if test="${assessmentMediasCount == 0}">
    <div id="viewMediaLink" class="hide">
        <a href="javascript:viewMedia()">View Media</a>
    </div>
</c:if>

<!-- Compliant: IDs in c:choose/c:when/c:otherwise - mutually exclusive branches -->
<c:choose>
    <c:when test="${type == 'A'}">
        <div id="content">Type A content</div>
    </c:when>
    <c:when test="${type == 'B'}">
        <div id="content">Type B content</div>
    </c:when>
    <c:otherwise>
        <div id="content">Default content</div>
    </c:otherwise>
</c:choose>

<!-- Non-compliant: Duplicate IDs outside any conditional block -->
<div id="footer">Footer 1</div>
<div id="footer">Footer 2</div>

</body>
</html>
