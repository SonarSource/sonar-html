<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<html xmlns:t="http://sonarsource.com/test">
<body>
    <c:set var="myVal" value="${0}"/>
    <c:if test="${myVal > 1}">
        If-block
    </c:if>
    <c:forEach var="i" begin="1" end="2">
        Loop iteration
    </c:forEach>
    <when test="${myVal > 1}">
        When-block-without-namespace
    </when>
    <t:customTag id="id" attr="#{cond1 and cond2}"</t:customTag>
</body>
</html>
