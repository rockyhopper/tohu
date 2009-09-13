<%
	new org.drools.executionserver.ExecutionServerHelper(request.getSession()).removeKnowledgeSession();
	response.sendRedirect("questionnaire.jsp?" + request.getQueryString());
%>
