<%@page import="org.kie.internal.runtime.StatefulKnowledgeSession,org.tohu.server.ExecutionServerHelper,org.tohu.examples.domain.Person" %>
<%
	StatefulKnowledgeSession knowledgeSession = new ExecutionServerHelper(request.getSession()).newKnowledgeSession("domain");
	Person person = new Person();
	person.setFullName((String)request.getParameter("name"));
	knowledgeSession.insert(person);
	response.sendRedirect("questionnaire.jsp");
%>
