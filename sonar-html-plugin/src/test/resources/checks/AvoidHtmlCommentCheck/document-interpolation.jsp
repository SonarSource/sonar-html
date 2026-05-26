<!DOCTYPE html>
<%-- Server-side marker --%>
<!-- Plain static comment, no interpolation -->
<!-- Welcome ${user.name} -->
<!-- Debug: <% out.print(secret); %> -->
<!-- Render: <%= request.getAttribute("token") %> -->
<!-- Twig output: {{ user.email }} -->
<!-- Twig statement: {% if user.isAdmin %} -->
<!-- PHP echo: <?= $password ?> -->
<!--[if IE 7]>
<conditional />
<![endif]-->
<!-- Trailing static text after ${dynamic} stays flagged -->
<!-- Just words, no markers here -->
