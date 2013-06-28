
<%@ page import="java.sql.*" %>  <-- Non-Compliant -->
<%@ foo import="java.sql.*" %>   <-- Compliant -->
<%@ page foo="java.sql.*" %>     <-- Compliant -->
