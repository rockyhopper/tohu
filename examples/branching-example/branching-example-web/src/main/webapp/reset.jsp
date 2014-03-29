<%
	new org.tohu.server.ExecutionServerHelper(request.getSession()).removeKnowledgeSession();
	response.sendRedirect("questionnaire.jsp?" + request.getQueryString());
%>
