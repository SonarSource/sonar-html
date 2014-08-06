<foo />
<jsp:directive.taglib uri="http://java.sun.com/jstl/sql" prefix="prefixOfTag" /> <!-- Noncompliant -->
<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/sql" %>
<%@ taglib prefix="sql" uri="http://bla.com" %>
<%@ taglib uri="http://java.sun.com/jstl/sql" prefix="prefixOfTag" > <!-- Noncompliant -->
