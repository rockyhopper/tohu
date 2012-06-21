<%
  String xml = (String)session.getAttribute("xml");
  if (xml == null) {
    xml = "<data>\n  <name>Fred</name>\n  <petName>Buck</petName>\n  <petType>dog</petType>\n</data>";
  }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" media="screen" href="css/style.css" />
<title>Domain Model</title>
</head>
<body>
	<p>This page is just a simple JSP, not part of the Tohu questionnaire.</p> 
  <form action="submit.jsp" method="get">
    <textarea type="text" name="xml" rows="10" cols="40"><%= xml %></textarea>
    <br/>
    <input type="submit" value="Submit"/>
  </form>
</body>
</html>
