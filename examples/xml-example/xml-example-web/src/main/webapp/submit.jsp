<%@page import="org.kie.internal.runtime.StatefulKnowledgeSession,org.tohu.server.ExecutionServerHelper,org.tohu.Answer,java.util.Map,com.thoughtworks.xstream.XStream,org.tohu.examples.xml.MapEntryConverter" %>
<%
	StatefulKnowledgeSession knowledgeSession = new ExecutionServerHelper(request.getSession()).newKnowledgeSession("xml");
	String xml = (String)request.getParameter("xml");	
	XStream xs = new XStream();
	xs.alias("data", java.util.TreeMap.class);
	xs.registerConverter(new MapEntryConverter());
	Map<String, String> map = (Map<String, String>)xs.fromXML(xml);
	for (Map.Entry<String, String> entry : map.entrySet()) {
		knowledgeSession.insert(new Answer(entry.getKey(), entry.getValue()));
	}
	response.sendRedirect("questionnaire.jsp");
%>
